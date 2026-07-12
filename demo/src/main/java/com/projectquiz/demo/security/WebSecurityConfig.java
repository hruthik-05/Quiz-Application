package com.projectquiz.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.projectquiz.demo.security.services.UserDetailsServiceImpl;
import com.projectquiz.demo.security.services.CustomOAuth2UserService;
import com.projectquiz.demo.security.oauth2.CustomOAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  CustomOAuth2UserService customOAuth2UserService;

  @Autowired
  CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

  @Bean
  public SecurityContextRepository securityContextRepository() {
      return new HttpSessionSecurityContextRepository();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
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
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(org.springframework.security.config.Customizer.withDefaults())
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
        .addFilterAfter(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)
        .securityContext(context -> context.securityContextRepository(securityContextRepository()))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(2)
        )
        .exceptionHandling(exception -> exception
            .defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")
            )
        )
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://apis.google.com; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; connect-src 'self' http://localhost:8200 http://localhost:5173 http://localhost:3000;"))
            .frameOptions(frame -> frame.deny())
            .contentTypeOptions(org.springframework.security.config.Customizer.withDefaults())
            .referrerPolicy(referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
        )
        .authorizeHttpRequests(auth -> 
          auth.requestMatchers("/api/auth/**").permitAll()
              .requestMatchers("/api/test/**").permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN")       
              .requestMatchers("/api/contest/create").hasRole("ADMIN") 
              .requestMatchers("/api/question/bulk").hasRole("ADMIN")      
              .requestMatchers("/api/question/addQuestion").hasRole("ADMIN")
              .requestMatchers("/api/question/deleteQuestion/**").hasRole("ADMIN")
              .requestMatchers("/api/question/updateQuestion").hasRole("ADMIN")
              .requestMatchers("/api/contest/submit").hasRole("USER")  
              .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(customOAuth2SuccessHandler)
        );

    http.authenticationProvider(authenticationProvider());

    return http.build();
  }

  @Bean
  public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
      org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
      configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:5173", "http://localhost:3000", "http://localhost:5174")); 
      configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
      configuration.setAllowCredentials(true);
      
      org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
}
