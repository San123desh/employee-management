package com.example.employeemanagement.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http

                // ← disable CSRF (JWT is stateless, CSRF doesn't apply)
                .csrf(csrf -> csrf
                        .disable()
                )
                // ← stateless: no session cookies, every request carries its own token. token carries all info
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no sessions, stateless
                // TODO : change to authenticated() and remove disable csrf() add login

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() //login is public
                        .requestMatchers("/h2-console/**").permitAll() //h2 console (dev)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() // everything else needs JWT
                )


                // ← insert your JWT filter BEFORE Spring's default auth filter
                //Your JWT filter intercepts every request
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
