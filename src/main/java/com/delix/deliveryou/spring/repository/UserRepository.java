package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

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
        }});
        userMockData.add(new User() {{
            setId(2l);
            setPhone("0783948591");
            setPassword(bCryptPasswordEncoder.encode("123456Bb"));
            setFirstName("Terrie");
            setLastName("Koe");
            setRole(UserRole.SHIPPER);
        }});
        userMockData.add(new User() {{
            setId(3l);
            setPhone("0934958344");
            setPassword(bCryptPasswordEncoder.encode("123456Cc"));
            setFirstName("JJ");
            setLastName("Peeter");
            setRole(UserRole.ADMIN);
        }});
    }

    public User getUserById(Long id) {
        if (id > userMockData.size() || id < 1)
            return null;
        return userMockData.get(Math.toIntExact(id - 1));
    }

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

}
