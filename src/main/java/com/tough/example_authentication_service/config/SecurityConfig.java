package com.tough.example_authentication_service.config;

import com.tough.example_authentication_service.oauth2.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;

    @Autowired
    public SecurityConfig(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
             .requestMatchers("/", "/css/**").permitAll()
             .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 ->
            oauth2.userInfoEndpoint(
                userInfo ->
                    userInfo.oidcUserService(customOidcUserService)
            )
        )
        .logout(logout ->
            logout.logoutSuccessUrl("/").permitAll()
        );

        return httpSecurity.build();
    }
}