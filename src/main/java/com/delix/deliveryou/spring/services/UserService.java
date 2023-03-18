package com.delix.deliveryou.spring.services;
import com.delix.deliveryou.spring.pojo.User;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.pojo.UserRole;
import com.delix.deliveryou.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

//    @Autowired
//    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @Autowired
    private UserRepository userRepository;

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

}
