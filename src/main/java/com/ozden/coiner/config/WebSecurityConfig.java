package com.ozden.coiner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

// in real world application, real security mechanism should be implemented
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final User.UserBuilder userBuilder = User.builder().passwordEncoder(encoder::encode);
        List<UserDetails> userDetailsList = new ArrayList<>();
        UserDetails user = userBuilder
                .username("user1")
                .password("user1")
                .roles("USER")
                .build();
        userDetailsList.add(user);

        user = userBuilder
                .username("user2")
                .password("user2")
                .roles("USER")
                .build();
        userDetailsList.add(user);

        UserDetails admin = userBuilder
                .username("admin")
                .password("admin")
                .roles("USER", "ADMIN")
                .build();
        userDetailsList.add(admin);

        return new InMemoryUserDetailsManager(userDetailsList);
    }
}