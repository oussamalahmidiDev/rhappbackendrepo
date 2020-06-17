package com.gi.rhapp.config;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtFilter;

    @Autowired
    private JwtAuthEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
//      Disable CSRF
        httpSecurity.cors().disable().csrf().disable()
//      Allow certain routes
            .authorizeRequests().antMatchers("/ws/**", "/", "/api/auth", "/api/forgot_password", "/confirm", "/set_password").permitAll().
            and().authorizeRequests().antMatchers("/api/auth").permitAll().
            and().authorizeRequests().antMatchers("/rh/**").hasAnyRole(Role.ADMIN.name(), Role.RH.name()).
//            and().authorizeRequests().antMatchers("/rh/users").hasRole(Role.ADMIN.name()).
    and().authorizeRequests().antMatchers("/salarie/**").hasRole(Role.SALARIE.name()).

// all other requests need to be authenticated
    anyRequest().authenticated().and().

// make sure we use stateless session; session won't be used to
// store user's state.
    exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

// Add a filter to validate the tokens with every request

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
