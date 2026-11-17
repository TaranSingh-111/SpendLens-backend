package com.example.expense_tracker.config;

import com.example.expense_tracker.filter.JwtAuthenticationFilter;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig{

   @Bean
    public SecurityFilterChain securityFilterChain(
           HttpSecurity http,
           DaoAuthenticationProvider authenticationProvider,
           JwtAuthenticationFilter jwtAuthenticationFilter)
           throws Exception{
       http
               .csrf(csrf -> csrf.disable())
               .authenticationProvider(authenticationProvider)

               .sessionManagement(session ->
                       session.sessionCreationPolicy(
                               SessionCreationPolicy.STATELESS
                       ))

               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/auth/**").permitAll()
                       .anyRequest().authenticated()
               )
               //jwt filter runs before the standard auth filter
               .addFilterBefore(
                       jwtAuthenticationFilter,
                       UsernamePasswordAuthenticationFilter.class
               );
       return http.build();
   }

   @Bean
    public PasswordEncoder passwordEncoder(){
       return new BCryptPasswordEncoder();
   }

   @Bean
    public DaoAuthenticationProvider authenticationProvider(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder){

       DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
       provider.setPasswordEncoder(passwordEncoder);

       return provider;
   }

   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
       throws Exception{
       return configuration.getAuthenticationManager();
   }
}
