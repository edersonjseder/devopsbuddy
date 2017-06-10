package com.devopsbuddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 05/06/17.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

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

        auth
                .inMemoryAuthentication()
                .withUser("user").password("password")
                .roles("USER");
    }
}
