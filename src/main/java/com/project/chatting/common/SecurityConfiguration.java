package com.project.chatting.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.chatting.auth.AuthEntryPointJwt;
import com.project.chatting.auth.JwtTokenFilter;


@Configuration
@EnableWebSecurity

public class SecurityConfiguration{
	
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;
	
	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	     return authenticationConfiguration.getAuthenticationManager();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity.ignoring().requestMatchers("/docs/**", "/error", "/v3/api-docs/**","/resources/**");
    }
	
	@Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(AbstractHttpConfigurer::disable).exceptionHandling(exceptionHandling -> exceptionHandling
                  .authenticationEntryPoint(unauthorizedHandler)
                ).
          sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            // 모두 허용
            .requestMatchers(
            		  "/user/auth/signin", // 로그인
            	      "/user/auth/signup",
            	      "/index.html",
            	      "/app.js",
            	      "/ws/**" ,
            	      "/webjars/**"
            ).permitAll()
            // 그 외는 인증 필요
            .anyRequest().authenticated())
          // jwt filter 추가
          .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
        
        
       
    }



}