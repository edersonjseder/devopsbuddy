package com.devopsbuddy.config;

import com.devopsbuddy.backend.service.UserSecurityService;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 05/06/17.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private Environment env;

    /** The encryption SALT. */
    private static final String SALT = "dfegrumam3lforiy8;4do";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }

    /** Public URLs */
    private static final String [] PUBLIC_MATCHES = {
            // Spring Security protects everything, so we need to be very precise on what
            // we wish to make public
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/",
            "/about/**",
            "/contact/**",
            "/errors/**/*",
            "/console/**",
            ForgotMyPasswordController.FORGOT_PASSWORD_URL_MAPPING,
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Gets the profiles from application.properties
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        /** Block that disable csrf and frame options for the H2 console to work correctly */
        if (activeProfiles.contains("dev")) {
            http.csrf().disable();
            http.headers().frameOptions().disable();
        }

        http
                .authorizeRequests()
                .antMatchers(PUBLIC_MATCHES).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/payload")
                .failureUrl("/login?error").permitAll()
                .and()
                .logout().permitAll();

    }

    @Autowired
    public void connfigureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        /** In Memory authentication -->>
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password")
                .roles("USER"); */

        auth
                .userDetailsService(userSecurityService)
                .passwordEncoder(passwordEncoder());

    }
}
