package com.example.employeemanagement.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component //Makes it a Spring bean so SecurityConfig can inject it
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    // constructor injection
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException{

        // 1. Get the Authorization header
        //    Client sends: "Authorization: Bearer eyJhbGci..."
        String header = request.getHeader("Authorization");

        // 2. Check if header exists and starts with "Bearer"
        if (header != null && header.startsWith("Bearer ")) {

            // 3. strip "Bearer " -> get just the token
            String token = header.substring(7);

            // 4. Validate the token
            if(tokenProvider.validateToken(token)){

                // 5. Extract username from the token
                String username = tokenProvider.getUsername(token);

                // 6. Create an authentication object
                //    (username, null password, empty authority list)
                UsernamePasswordAuthenticationToken auth =
                        // You're not doing role-based checks yet. Later: List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                // 7. Store it in the SecurityContext
                // → Spring Security now knows "this request is authenticated"
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 8. Pass the request to the next filter / controller
        //    (if no token or invalid token, this just continues unauthenticated
        //     → SecurityConfig will reject it with 401)
        // Always call this — without it, the request never reaches your controller
        filterChain.doFilter(request, response);

    }

}
