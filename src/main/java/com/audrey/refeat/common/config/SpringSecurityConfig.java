package com.audrey.refeat.common.config;

import com.audrey.refeat.common.authenticate.JwtAccessDeniedHandler;
import com.audrey.refeat.common.authenticate.JwtAuthenticationEntryPoint;
import com.audrey.refeat.common.authenticate.JwtFilter;
import com.audrey.refeat.common.filter.AccessLogFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final JwtFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AccessLogFilter accessLogFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    String[] authenticationFreeUrls = new String[]{
            "/user/email/**",
            "/user/login",
            "/user/register",
            "/user/refresh",
            "/user/auth/**",
            "/swagger-ui/**",
            "/v3/**",
            "/821hejij1n3br7ef739hwee/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(request -> request
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers(authenticationFreeUrls).permitAll()
                        .anyRequest().authenticated()).csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)    // 401
                .accessDeniedHandler(jwtAccessDeniedHandler);   // 403
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(accessLogFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
