package com.delix.deliveryou.spring.configuration.JWT.filter;

import com.delix.deliveryou.spring.configuration.JWT.provider.JWTProvider;
import com.delix.deliveryou.spring.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JWTProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            System.out.println("-------------- header (" + LocalTime.now().toString() + "): " + request.getHeader("Host"));
            System.out.println("-------------- auth (" + LocalTime.now().toString() + "): " + request.getHeader("Authorization"));

            // Lấy jwt từ request
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Lấy id user từ chuỗi jwt
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                // Lấy thông tin người dùng từ id
                UserDetails userDetails = userService.loadUserById(userId);

                if(userDetails != null) {
                    // Nếu người dùng hợp lệ, set thông tin cho Seturity Context
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("[TRACK]: JWT FIlte");
        filterChain.doFilter(request, response);
        System.out.println("[TRACK]: after JWT FIlte");
    }

    // Through HTTPS, headers are also encrypted
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
