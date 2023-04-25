package com.delix.deliveryou.spring.controller;

import com.delix.deliveryou.api.locationiq.LocationIQ;
import com.delix.deliveryou.api.sendbird.SendBird;
import com.delix.deliveryou.api.sendbird.SendBirdChannel;
import com.delix.deliveryou.api.sendbird.SendBirdUser;
import com.delix.deliveryou.carrier.LogInCarrier;
import com.delix.deliveryou.spring.configuration.JWT.JWTUserDetails;
import com.delix.deliveryou.spring.configuration.JWT.provider.JWTProvider;
import com.delix.deliveryou.spring.configuration.websocket.CommunicableUserContainer;
import com.delix.deliveryou.spring.pojo.*;
import com.delix.deliveryou.spring.services.ChatService;
import com.delix.deliveryou.spring.services.DeliveryService;
import com.delix.deliveryou.spring.services.UserService;
import com.delix.deliveryou.utility.JsonResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

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

    private ResponseEntity doLogin(LogInCarrier logInCarrier, boolean asAdmin) {
        // Xác thực từ username và password.
        // START: this segment is a temporary fix for the infinite loop occurring when the wrong credentials is provided
        try {
            JWTUserDetails userDetails = (JWTUserDetails) userService.loadUserByUsername(logInCarrier.getPhone());

            if (userDetails == null || !bCryptPasswordEncoder.matches(logInCarrier.getPassword(), userDetails.getPassword())) {
                return new ResponseEntity(JsonResponseBody.build(
                        "Error", "Authentication failed"
                ), HttpStatus.UNAUTHORIZED);
            }

            if (asAdmin) {
                if (userDetails.getUserObject().getRole().getId() != UserRole.ADMIN.getId())
                    return new ResponseEntity(JsonResponseBody.build(
                            "Error", "Only allow authentication as [Admin]"
                    ), HttpStatus.UNAUTHORIZED);
            } else {
                if (userDetails.getUserObject().getRole().getId() == UserRole.ADMIN.getId())
                    return new ResponseEntity(JsonResponseBody.build(
                            "Error", "Only allow authentication as [Shipper/RegularUser]"
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(JsonResponseBody.build(
                    "Error", "Authentication failed: internal error"
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LogInCarrier logInCarrier) {
        return doLogin(logInCarrier, false);
    }

    @CrossOrigin
    @PostMapping("/xn4JdnNALsjKRm/loginAsAdmin")
    public ResponseEntity loginAsAdmin(@RequestBody LogInCarrier logInCarrier) {
        return doLogin(logInCarrier, true);
    }

    void afterTokenVerifiedSuccessfully(long userId) {
        UserDetails userDetails = userService.loadUserById(userId);

        if (userDetails != null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @CrossOrigin
    @PostMapping("/verifyAccessToken")
    public ResponseEntity isAccessTokenValid(@RequestBody Map<String, String> body) {
        if (body.containsKey("accessToken")) {
            String token = body.get("accessToken");
            boolean res = tokenProvider.validateToken(token);

            if (res) {
                long id = tokenProvider.getUserIdFromJWT(token);
                afterTokenVerifiedSuccessfully(id);
            }

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

    @Autowired
    private CommunicableUserContainer container;

    @CrossOrigin
    @GetMapping("/add")
    public ResponseEntity add() {
        container.registerAsActive(1l);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Autowired
    private LocationIQ locationIQ;

    @CrossOrigin
    @GetMapping("/distance")
    public ResponseEntity getDistance(@RequestBody Map<String, String> map) {

        LocationIQ.Coordinate pointA = new LocationIQ.Coordinate(
                Double.valueOf(map.get("pointA_lat")),
                Double.valueOf(map.get("pointA_lon"))
        );
        LocationIQ.Coordinate pointB = new LocationIQ.Coordinate(
                Double.valueOf(map.get("pointB_lat")),
                Double.valueOf(map.get("pointB_lon"))
        );
        double distance = locationIQ.distance(pointA, pointB);
        return new ResponseEntity(distance, HttpStatus.OK);
    }

    // --------------------- TEST USER WS CHAT NOTIFICATION -----------------------

    @Autowired
    private ChatService chatService;
    @Autowired
    public SimpMessagingTemplate messagingTemplate;

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    class SimpleUser {
        long id;
        String firstName;
        String lastName;
        String profilePictureUrl;
    }

    public ResponseEntity chatTest() {
        try {
            User user = ((JWTUserDetails) userService.loadUserById(1l)).getUser();
            User shipper = ((JWTUserDetails) userService.loadUserById(2l)).getUser();

            var list = new ArrayList<Long>(){{
                add(user.getId());
                add(shipper.getId());
            }};

//            ChatSession chatSession = new ChatSession(user, shipper);
//
//            String sessionId = chatService.addSession(chatSession);
//
//            // ws
//            for (long id : list) {
//                messagingTemplate.convertAndSendToUser(String.valueOf(id), "/notification/chat", chatSession);
//            }

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // --------------------- END OF TEST USER WS CHAT NOTIFICATION -----------------------

    // --------------------- TEST DELIVERY PACKAGE MATCHED -----------------------
    @Autowired
    private DeliveryService deliveryService;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Setter
    @ToString
    //@Setter
    class PackageRequest{
        DeliveryPackage deliveryPackage;
        boolean accepted;
    }

    // matched -> send to all suitable shippers -> if accept
    @Autowired
    private SendBird sendBird;
    @Autowired
    private ObjectMapper objectMapper;

    @Data
    @ToString
    @AllArgsConstructor
    class Test {
        OffsetDateTime date;
    }
    @CrossOrigin
    @GetMapping("/package-matched")
    public ResponseEntity packageMatched() {

        try {
            // matched shipper
            User shipper = ((JWTUserDetails) userService.loadUserById(2l)).getUser();

            var deliveryPackage = deliveryService.getPackage(1);

            deliveryPackage.setShipper(shipper);
            deliveryPackage.setStatus(PackageDeliveryStatus.DELIVERING);

            if (deliveryService.updatePackage(deliveryPackage) == null)
                throw new Exception("cannot update package");

            for (long id : Arrays.asList(shipper.getId(), deliveryPackage.getUser().getId())) {
                System.out.println(">>>>>>>>>>> dev pack: " + deliveryPackage);
                String body = objectMapper.writeValueAsString(deliveryPackage);
                messagingTemplate.convertAndSendToUser(String.valueOf(id), "/notification/package", body);
            }

            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }




    @CrossOrigin
    @GetMapping("/redd/{id}")
    public ResponseEntity test12(@PathVariable String id) {
        User user = ((JWTUserDetails) userService.loadUserById(Long.valueOf(id))).getUser();
        var res = new Object(){
            ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        };

        sendBird.createUser(
                new SendBirdUser.RequestUser(){{
                    setUser_id(String.valueOf(user.getId()));
                    setNickname(user.getFirstName() + " " + user.getLastName());
                    setProfile_url(user.getProfilePictureUrl());
                }},
                responseUser -> {
                    System.out.println(">>>>>>>>>> user: " + user);
                },
                exception -> {
                    exception.printStackTrace();
                    res.responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
                }
        );
        return res.responseEntity;
    }

}
