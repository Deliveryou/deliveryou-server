package com.delix.deliveryou.spring.repository;

import com.delix.deliveryou.spring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User getUserById(Long id) {
        if (id != 1)
            return null;
        //12345
        System.out.println("by id");
        return new User(1, "Andie", "W", "0858594852", bCryptPasswordEncoder.encode("123456Az"), "USER");
    }

    public User getUserByPhone(String phone) {
        if (!phone.equals("0858594852"))
            return null;
        System.out.println("by phone");
        return new User(1, "Andie", "W", "0858594852", bCryptPasswordEncoder.encode("123456Az"), "USER");
    }
}
