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

import java.util.Map;

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

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LogInCarrier logInCarrier) {
        // Xác thực từ username và password.
        // START: this segment is a temporary fix for the infinite loop occurring when the wrong credentials is provided
        JWTUserDetails userDetails = (JWTUserDetails) userService.loadUserByUsername(logInCarrier.getPhone());

        if (userDetails == null || !bCryptPasswordEncoder.matches(logInCarrier.getPassword(), userDetails.getPassword())) {

            return new ResponseEntity(JsonResponseBody.build(
                    "Error", "Authentication failed"
            ), HttpStatus.UNAUTHORIZED);
        }

        // END
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        logInCarrier.getPhone(),
                        logInCarrier.getPassword()
                    )
            );
        // Nếu không xảy ra exception tức là thông tin hợp lệ
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((JWTUserDetails) authentication.getPrincipal());

        return new ResponseEntity(JsonResponseBody.build(
                "accessToken", jwt,
                "tokenType", "Bearer",
                "userType", userDetails.getRole(),
                "id", userDetails.getId()
        ), HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/verifyAccessToken")
    public ResponseEntity isAccessTokenValid(@RequestBody Map<String, String> body) {
        if (body.containsKey("accessToken")) {
            String token = body.get("accessToken");
            boolean res = tokenProvider.validateToken(token);
            return new ResponseEntity(JsonResponseBody.build(
                    "isValid", res
            ), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @PostMapping("auth-test")
    public ResponseEntity test() {
        System.out.println("called");
        return new ResponseEntity(HttpStatus.OK);
    }
}
