package com.delix.deliveryou.spring.configuration.security;

import com.delix.deliveryou.spring.configuration.JWT.filter.JWTAuthenticationFilter;
import com.delix.deliveryou.spring.configuration.JWT.provider.JWTProvider;
import com.delix.deliveryou.spring.services.UserService;
import graphql.com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails user1 = User.withUsername("user1")
                .password(bCryptPasswordEncoder().encode("User1Pass"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user1);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http builder configurations for authorize requests and form login (see below)
        http
                .cors()
                .and()
                .csrf()
                .disable()
//                .logout()
//                .logoutUrl("/logout")
//                //.deleteCookies("")
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.setStatus(HttpStatus.OK.value());
//                })
//                .and()
                .authorizeHttpRequests()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/user/**").hasAuthority("USER")
                .requestMatchers("/shipper/**").hasAuthority("SHIPPER")
//                .requestMatchers("/login").denyAll()
//                .requestMatchers("/test1").permitAll()
                .requestMatchers("/test2").authenticated()
                .requestMatchers("/test3").hasAuthority("USER")
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/websocket/**").permitAll()
                .requestMatchers("/send").permitAll()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

//        http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userService)
//                .passwordEncoder(bCryptPasswordEncoder);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService)
            throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public JWTProvider jwtProvider() {
        return new JWTProvider();
    }

//    @Bean
//    public WebMvcConfigurer webMvcConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowCredentials(false)
//                        //.maxAge(3600)
//                        .allowedHeaders("Accept", "Content-Type", "Origin", "Authorization", "X-Auth-Token")
//                        .exposedHeaders("X-Auth-Token", "Authorization")
//                        .allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS");
//
//            }
//        };
//    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500"));
////        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//
//        config.setAllowedOrigins(Arrays.asList("172.0.0.1:5500"));
//        //config.setAllowCredentials(true);
//        config.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
//        config.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

//@Bean
//public FilterRegistrationBean corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowCredentials(true);
//    config.addAllowedOrigin("http://127.0.0.1:5500");
//    config.setAllowedMethods(Arrays.asList("POST", "OPTIONS", "GET", "DELETE", "PUT"));
//    config.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
//    source.registerCorsConfiguration("/**", config);
//    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//    return bean;
//}
}
