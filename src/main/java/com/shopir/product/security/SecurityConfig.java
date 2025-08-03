package com.shopir.product.security;

import com.shopir.product.exceptions.JwtAuthEntryPoint;
import com.shopir.product.security.filter.JwtFilter;
import com.shopir.product.service.UserService;
import com.shopir.product.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    public JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    public UserService userService;

    @Bean
    public JwtFilter tokenFilter(JwtUtils jwtUtils, UserDetailsService customUserDetailsService){
        return new JwtFilter(jwtUtils, customUserDetailsService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, UserDetailsService customUserDetailsService, JwtUtils jwtUtils) throws Exception {

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->  auth .requestMatchers("/product").authenticated()
                        .requestMatchers("/search").authenticated()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().permitAll());;

        httpSecurity.authenticationProvider(authenticationProvider())
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(new HttpSessionSecurityContextRepository())
                )

                .addFilterBefore(tokenFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint));

        httpSecurity.authenticationProvider(authenticationProvider());

        return httpSecurity.build();

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
