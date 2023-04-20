package com.delix.deliveryou.spring.service_database;

import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository_database.UserRepository;
import com.delix.deliveryou.spring.repository_database.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService1 implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;

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

        if (userRepository.isUser(id)){
            System.out.println("true");
            return true;
        }else {
            System.out.println("false");
            return false;
        }
    }

    public boolean isShipper(long id) {
        if (userRepository.isShipper(id)){
            System.out.println("true");
            return true;
        }else {
            System.out.println("false");
            return false;
        }
    }

    public boolean isAdmin(long id) {
        if (userRepository.isAdmin(id)){
            System.out.println("true");
            return true;
        }else {
            System.out.println("false");
            return false;
        }
    }

    /**
     *
     * @param userId
     * @param type
     * @return a boolean value indicates if (userId) has its role as (role).
     * @throws NullPointerException if (role) is null
     */

    public boolean assertRole(long userId, UserRole type) {
        if (type == null)
            throw new NullPointerException();

        return userRepository.assertRole(userId, type);
    }

    public void add(User user) {
        UserRole userRole = UserRole.USER;
        if (userRole == null){
            userRole = userRoleRepository.getUserRoleById(1);
        }

        System.out.printf("Type: Id: %d; name: %s", userRole.getId(), userRole.getName());
        if (userRepository.existsUserByPhone(user.getPhone())){
            System.out.println("phone already exists");
            throw new UsernameNotFoundException(user.getPhone());
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    public void checkUserType(){
        List<UserRole> userRoles = userRoleRepository.findAll();
        for (UserRole userRole : userRoles){
            if (userRole.equals(UserRole.USER))
                System.out.println("user");
            if (userRole.equals(UserRole.SHIPPER))
                System.out.println("shipper");
            if (userRole.equals(UserRole.ADMIN))
                System.out.println("admin");
        }
    }
}
