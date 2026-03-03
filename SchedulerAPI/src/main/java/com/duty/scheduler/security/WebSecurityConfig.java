package com.duty.scheduler.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.duty.scheduler.security.jwt.AuthEntryPointJwt;
import com.duty.scheduler.security.jwt.AuthTokenFilter;
import com.duty.scheduler.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

      @Bean
      public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
      }

      @Bean
      public DaoAuthenticationProvider authenticationProvider() {
          DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

          authProvider.setPasswordEncoder(passwordEncoder());

          return authProvider;
      }

      @Bean
      public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
      }

      @Bean
      public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
      }

      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http,
                                             DaoAuthenticationProvider authenticationProvider) throws Exception {
          http
              .csrf(csrf -> csrf.disable())
              .cors(cors -> {})
              .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(authenticationProvider)
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/api/auth/**").permitAll()
                      .requestMatchers("/api/test/**").permitAll()
                      .requestMatchers("/api/public/**").permitAll()
                      .anyRequest().authenticated()
              );

          http.addFilterBefore(authenticationJwtTokenFilter(),
                  UsernamePasswordAuthenticationFilter.class);

        return http.build();
      }
}
