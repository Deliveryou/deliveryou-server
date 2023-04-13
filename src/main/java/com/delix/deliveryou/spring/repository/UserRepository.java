package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private List<User> userMockData = new ArrayList<>();

    @PostConstruct
    public void setMockData() {
        userMockData.add(new User() {{
            setId(1l);
            setPhone("0858594852");
            setPassword(bCryptPasswordEncoder.encode("123456Aa"));
            setFirstName("Andie");
            setLastName("W");
            setRole(UserRole.USER);
            setDateOfBirth(LocalDate.of(2001, 2, 2));
            setProfilePictureUrl("https://randomuser.me/api/portraits/men/32.jpg");
        }});
        userMockData.add(new User() {{
            setId(2l);
            setPhone("0783948591");
            setPassword(bCryptPasswordEncoder.encode("123456Bb"));
            setFirstName("Terrie");
            setLastName("Koe");
            setRole(UserRole.SHIPPER);
            setCitizenId("123456789");
            setDateOfBirth(LocalDate.of(1998, 7, 5));
            setProfilePictureUrl("https://randomuser.me/api/portraits/men/36.jpg");
        }});
        userMockData.add(new User() {{
            setId(3l);
            setPhone("0934958344");
            setPassword(bCryptPasswordEncoder.encode("123456Cc"));
            setFirstName("JJ");
            setLastName("Peeter");
            setRole(UserRole.ADMIN);
            setProfilePictureUrl("https://randomuser.me/api/portraits/men/30.jpg");
        }});
    }

    /**
     * Get user by id
     * @param id an integer starts from 1
     * @return a user object if the id is valid, null if the id is invalid
     */
    public User getUserById(Long id) {
        if (id > userMockData.size() || id < 1)
            return null;
        return userMockData.get(Math.toIntExact(id - 1));
    }


    /**
     * Get user by phone number
     * @param phone
     * @return a user object if the phone number is valid, null if invalid
     */
    public User getUserByPhone(String phone) {
        if (phone.equals("0858594852"))
            return userMockData.get(0);
        else if (phone.equals("0783948591")) {
            return userMockData.get(1);
        } else if (phone.equals("0934958344")) {
            return userMockData.get(2);
        } else
            return null;
    }

    @Deprecated
    public boolean isUser(long userId) {
        try {
            return getUserById(userId).getRole() == UserRole.USER;
        } catch (Exception e) {
            return false;
        }
    }

    @Deprecated
    public boolean isShipper(long userId) {
        try {
            return getUserById(userId).getRole() == UserRole.SHIPPER;
        } catch (Exception e) {
            return false;
        }
    }

    @Deprecated
    public boolean isAdmin(long userId) {
        try {
            return getUserById(userId).getRole() == UserRole.ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean assertRole(long userId, UserRole role) {
        try {
            return getUserById(userId).getRole() == role;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check whether a phone number exists
     * @param phone
     * @return true if the number exists
     */
    public boolean phoneExists(String phone) {
        for (User user : userMockData)
            if (user.getPhone().equals(phone))
                return true;
        return false;
    }

    /**
     * Get hashed password by userId
     * @param userId
     * @return a hashed password if [userId] exists, null if not exists
     */
    public String getPasswordHash(long userId) {
        User user = getUserById(userId);
        if (user == null)
            return null;
        return user.getPassword();
    }

    public boolean updateUser(User updatedUser) {
        User user = userMockData.get((int) (updatedUser.getId() - 1));

        if (user == null)
            return false;

        String newProfileUrl = updatedUser.getProfilePictureUrl();
        if (newProfileUrl != null && !newProfileUrl.equals(user.getProfilePictureUrl()))
            user.setProfilePictureUrl(newProfileUrl);

        return true;
    }

    public boolean idExists(long userId) {
        for (User user : userMockData) {
            if (user.getId() == userId)
                return true;
        }
        return false;
    }
}
