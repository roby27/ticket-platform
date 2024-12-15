package it.r27.ticket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @SuppressWarnings("removal")
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()
            .requestMatchers("/api/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/ticket/new", "/user/new").hasAuthority("ADMIN")
            .requestMatchers("/user/list").hasAuthority("ADMIN")
            .requestMatchers("/ticket", "/ticket/**").hasAnyAuthority("OPERATOR", "ADMIN")
            .requestMatchers("/user", "/user/**").hasAnyAuthority("OPERATOR", "ADMIN")
            .requestMatchers("/**").hasAnyAuthority("OPERATOR", "ADMIN")
            .and().formLogin()
            .and().logout()
            .and().exceptionHandling()
            .and().csrf().disable();

        return http.build();
    }

    @Bean
    DatabaseUserDetailsService userDetailsService() {
        return new DatabaseUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

}
