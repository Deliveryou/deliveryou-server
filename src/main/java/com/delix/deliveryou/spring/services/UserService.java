package com.delix.deliveryou.spring.services;
import com.delix.deliveryou.exception.InsufficientArgumentException;
import com.delix.deliveryou.exception.HttpBadRequestException;
import com.delix.deliveryou.exception.LogicViolationException;
import com.delix.deliveryou.spring.model.SearchFilter;
import com.delix.deliveryou.spring.model.SearchFilterType;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

//    @Autowired
//    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String phone) {
        // Kiểm tra xem user có tồn tại trong database không?
        //User user = userRepository.findByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException(username);
//        }
//        return new CustomUserDetails(user);
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
                return userRepository.updateUser(originalUser);
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
        return userRepository.getUsersWithFilter(role, filter);
    }

    public boolean markUserAsDeleted(long userId, boolean deleted) {
        if (!idExists(userId))
            return false;
        return userRepository.markUserAsDeleted(userId, deleted);
    }

}
