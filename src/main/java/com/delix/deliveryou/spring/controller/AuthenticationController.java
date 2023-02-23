package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.carrier.LogInCarrier;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.configuration.JWT.provider.JWTProvider;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTProvider tokenProvider;

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LogInCarrier logInCarrier) {
        // Xác thực từ username và password.
        System.out.println("----------- controller: " + logInCarrier.getPhone() + " - " + logInCarrier.getPassword());

        UserDetails userDetails = userService.loadUserByUsername(logInCarrier.getPhone());

        if (userDetails == null || !bCryptPasswordEncoder.matches(logInCarrier.getPassword(), userDetails.getPassword())) {

            return new ResponseEntity(JsonResponseBody.build(
                    "Error", "Authentication failed"
            ), HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        logInCarrier.getPhone(),
                        logInCarrier.getPassword()
                    )
            );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((JWTUserDetails) authentication.getPrincipal());

        return new ResponseEntity(JsonResponseBody.build(
                "accessToken", jwt,
                "tokenType", "Bearer"
        ), HttpStatus.OK);
    }

    @PostMapping("auth-test")
    public ResponseEntity test() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
