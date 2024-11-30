/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.smarteventplanningplatform.config;

import com.kaan.smarteventplanningplatform.model.Role;
import com.kaan.smarteventplanningplatform.service.UserService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author kaan
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserService userService;
    private JwtAuthFilter jwtAuthFilter;
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    public SecurityConfig(UserService userService, JwtAuthFilter jwtAuthFilter, JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler) {
        this.userService = userService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf((csrfCustomizer) -> csrfCustomizer.disable());
        httpSecurity.sessionManagement((sessionManagementCustomizer) -> sessionManagementCustomizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                //.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/v1/user/register").anonymous()
                .requestMatchers("/v1/user/get-exec", "/v1/user/lock", "/v1/user/unlock", "/v1/user/update-exec", "/v1/user/delete-exec").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers("/v1/user/get", "/v1/user/update-pass", "/v1/user/update", "/v1/user/delete").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                .requestMatchers("/v1/participant/join", "/v1/participant/leave", "/v1/participant/get-all", "/v1/participant/get-all-event").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                .requestMatchers("/v1/participant/leave-exec", "/v1/participant/delete-all-user", "/v1/participant/get-all-exec").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers("/v1/message/send", "/v1/message/delete", "/v1/message/get", "/v1/message/get-all", "/v1/message/get-all-user", "/v1/message/update").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                .requestMatchers("/v1/message/get-all-exec", "/v1/message/delete-exec").hasAuthority(Role.ROLE_ADMIN.name())
                .requestMatchers("/v1/event/create", "/v1/event/delete", "/v1/event/update", "/v1/event/get", "/v1/event/get-all" , "/v1/event/main").hasAnyAuthority(Role.ROLE_ADMIN.name(), Role.ROLE_USER.name())
                .requestMatchers("/v1/event/delete-exec", "/v1/event/confirm", "/v1/event/decline", "/v1/event/update-exec").hasAuthority(Role.ROLE_ADMIN.name())
                .anyRequest().permitAll()).formLogin((form) -> form
                .successForwardUrl("/v1/event/main")
                .loginPage("/")
        );

        return httpSecurity.build();
    }

}
