package com.example.expense_tracker.filter;

import com.example.expense_tracker.service.JwtService;
import com.example.expense_tracker.service.MyUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, MyUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        //The authorization header value starts with "Bearer ..."
        //so we ignore the first 7 characters
        final String jwt = authHeader.substring(7);

        try{
            String email = jwtService.extractUsername(jwt);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){

                //finds the user in the db
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                //calls the service to match the token with the userDetails
                if(jwtService.isTokenValid(jwt, userDetails)){

                    //internal springboot token containing user info
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    //attaches additional details from the request
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //tells spring that the request has been authenticated
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (JwtException | IllegalArgumentException | UsernameNotFoundException e){
            //tells spring to not authenticate
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
