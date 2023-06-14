package com.delix.deliveryou.spring.services;
import com.delix.deliveryou.exception.InsufficientArgumentException;
import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.exception.LogicViolationException;
import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.model.SearchFilterType;
import com.delix.deliveryou.spring.model.SimpleRating;
import com.delix.deliveryou.spring.pojo.*;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.repository.RatingRepository;
import com.delix.deliveryou.spring.repository.UserRepository;
import com.delix.deliveryou.spring.repository.extender.UserRepositoryExtender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRepositoryExtender userRepositoryExtender;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String phone) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.getUserByPhone(phone);

        if (user == null) {
            throw new UsernameNotFoundException(phone);
        }
        return new JWTUserDetails(user);
    }

    /**
     *
     * @param id: user id
     * @return JWTUserDetails castable UserDetails
     * @throws UsernameNotFoundException if the id is invalid
     */
    public UserDetails loadUserById(Long id) {
        //UserDetails userDetails = inMemoryUserDetailsManager.loadUserByUsername(phone);

        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new UsernameNotFoundException("id not found");
        }
        return new JWTUserDetails(user);
    }

    public boolean isRegularUser(long id) {
        return userRepository.isUser(id);
    }

    public boolean isShipper(long id) {
        return userRepository.isShipper(id);
    }

    public boolean isAdmin(long id) {
        return userRepository.isAdmin(id);
    }

    /**
     *
     * @param userId
     * @param role
     * @return a boolean value indicates if (userId) has its role as (role).
     * @throws NullPointerException if (role) is null
     */
    public boolean assertRole(long userId, UserRole role) {
        if (role == null)
            throw new NullPointerException();

        return userRepository.assertRole(userId, role);
    }

    /**
     *
     * @param phone
     * @return true if the number exists
     * @throws NullPointerException if [phone] is null
     * @throws LogicViolationException if [phone] length is less than 10
     */
    public boolean phoneExists(String phone) {
        if (phone == null)
            throw new NullPointerException();

        phone = phone.trim();
        if (phone.length() < 10)
            throw new LogicViolationException("violated [phone] length");

        return userRepository.phoneExists(phone);
    }

    public boolean idExists(long userId) {
        if (userId < 1)
            return false;

        return userRepository.idExists(userId);
    }

    /**
     * Verify if the user with id=[userId] has the password of [password]
     * @param userId
     * @param password origin password without being hashed
     * @return true if matches
     * @throws HttpBadRequestException if [userId] is invalid
     */
    public boolean verifyPassword(long userId, String password) {
        String hashed = userRepository.getPasswordHash(userId);

        if (hashed == null)
            throw new HttpBadRequestException();

        if (password == null || password.equals(""))
            return false;

        return bCryptPasswordEncoder.matches(password, hashed);
    }

    /**
     *
     * @param updatedUser
     * @return true if the user has updated
     * @throws HttpBadRequestException if [updatedUser] is null or [updatedUser.id] is invalid
     */
    public boolean updateUser(User updatedUser) {
        try {
            User originalUser = ((JWTUserDetails) loadUserById(updatedUser.getId())).getUser();

            boolean result = extractDifferences(originalUser, updatedUser);

            if (result) {
                // detected diffs
                userRepository.save(originalUser);
                return true;
            }
            return false;

        } catch (NullPointerException | UsernameNotFoundException | InsufficientArgumentException ex) {
            throw new HttpBadRequestException();
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Update differences to the [originalUser] object
     * @param originalUser
     * @param updatedUser
     * @return true if there are differences
     * @throws InsufficientArgumentException if either of the params is null
     * @throws LogicViolationException if the 2 user objects have different id
     */
    private boolean extractDifferences(User originalUser, User updatedUser) {
        if (originalUser == null || updatedUser == null)
            throw new InsufficientArgumentException();

        if (originalUser.getId() != updatedUser.getId())
            throw new LogicViolationException();

        boolean detected = false;

        // first name
        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().equals(originalUser.getFirstName())) {
            originalUser.setFirstName(updatedUser.getFirstName());
            detected = true;
        }
        // last name
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().equals(originalUser.getLastName())) {
            originalUser.setLastName(updatedUser.getLastName());
            detected = true;
        }
        // phone
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().equals(originalUser.getPhone())) {
            originalUser.setPhone(updatedUser.getPhone());
            detected = true;
        }
        // password
        if (updatedUser.getPassword() != null) {
            originalUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
            detected = true;
        }
        // citizen id
        if (updatedUser.getCitizenId() != null && !updatedUser.getCitizenId().equals(originalUser.getCitizenId())) {
            originalUser.setCitizenId(updatedUser.getCitizenId());
            detected = true;
        }
        // profile photo
        if (updatedUser.getProfilePictureUrl() != null && !updatedUser.getProfilePictureUrl().equals(originalUser.getProfilePictureUrl())) {
            originalUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
            detected = true;
        }
        // matching refs
        if (updatedUser.getMatchingReferences() != null && !updatedUser.getMatchingReferences().equals(originalUser.getMatchingReferences())) {
            originalUser.setMatchingReferences(updatedUser.getMatchingReferences());
            detected = true;
        }
        // birthdate
        if (updatedUser.getDateOfBirth() != null && !updatedUser.getDateOfBirth().equals(originalUser.getDateOfBirth())) {
            originalUser.setDateOfBirth(updatedUser.getDateOfBirth());
            detected = true;
        }

        return detected;

    }

    // --------------------------------

    public List<User> getUsersWithFilter(UserRole role, SearchFilter filter) {
        if (role == null || filter == null)
            return Collections.emptyList();

        // normalize filter indexes
        SearchFilterType.normalizeIndexes(filter);
//        return userRepository.getUsersWithFilter(role, filter);
          return userRepositoryExtender.getUsersWithFilter(role, filter);
    }

    public boolean markUserAsDeleted(long userId, boolean deleted) {
        var user = ((JWTUserDetails) loadUserById(userId)).getUser();

        if (user == null)
            return false;

        var result =  userRepository.markUserAsDeleted(userId, deleted);

        if (result > 0) {
            if (user.getEmail() != null) {

                new Thread(() -> {
                    String message = "Your account has been";
                    if (deleted) {
                        message += " banned!\nAll of your app operations will be rejected from your account.\nIf you believe this is a mistake, contact us at 08xxxxxxxx for detailed information.";
                    } else {
                        message += " unbanned!\nYou can continue to use our application service!";
                    }
                    mailService.sendSimpleMessage(
                            user.getEmail(),
                            "Deliveryou account notification service",
                            message
                    );
                }).start();
            }
            return true;
        }
        return false;
    }

    public int countSystemUsersWithRole(long roleId) {
        return userRepository.countSystemUsersWithRole(roleId);
    }

    public User addUser(User user, UserRole userRole) {
        if (user == null || userRole == null || phoneExists(user.getPhone()))
            throw new HttpBadRequestException();

        if (userRole.getId() == UserRole.SHIPPER.getId()) {
            var mr = new MatchingReferences();
            mr.setMatchingRadius(999);
            mr.setMinimumDeliveryPrice(10000);
            mr.setMaximumDeliveryDistance(999);
            user.setMatchingReferences(mr);
            user.setAverageRating(0f);
        }

        user.setRole(userRole);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     *
     * @param simpleRating
     * @param deliveryPackage
     * @return boolean values, throws error if failed to save new Rating
     */
    public boolean rateShipper(SimpleRating simpleRating, DeliveryPackage deliveryPackage) {
        if (simpleRating == null || deliveryPackage == null)
            return false;

        if (simpleRating.getRating() % 1 != 0)
            simpleRating.setRating((float) Math.floor(simpleRating.getRating()));

        if (simpleRating.getRating() < 1)
            simpleRating.setRating(1);
        else if (simpleRating.getRating() > 5)
            simpleRating.setRating(5);

        var rating = new Rating(
            deliveryPackage,
                simpleRating.getContent(),
                simpleRating.getRating(),
                false,
                LocalDateTime.now()
        );

        // save new rating
        ratingRepository.save(rating);

        var shipper = deliveryPackage.getShipper();

        var currentShipperRatingCount = ratingRepository.countRatingOfShipper(shipper.getId());

        if (shipper.getAverageRating() == null)
            shipper.setAverageRating(0f);

        var newAvg = (shipper.getAverageRating() * (currentShipperRatingCount - 1) + simpleRating.getRating()) / currentShipperRatingCount;

        shipper.setAverageRating(newAvg);

        // New average = ((Current average * Current count) + New rating) / (Current count + 1)
        // update average rating
        userRepository.save(shipper);

        return true;
    }

    public boolean canRateShipper(long packageId) {
        if (packageId < 1)
            return false;

        return ratingRepository.canRateShipper(packageId);
    }

    public List<Rating> getRatingList() {
        return ratingRepository.getAll();
    }

    public boolean markedRating(long ratingId, boolean value) {
        if (ratingId < 1)
            return false;

        var rating = ratingRepository.getById(ratingId);

        if (rating == null || rating.isMarked() == value)
            return false;

        rating.setMarked(value);

        ratingRepository.save(rating);
        return true;
    }

}
