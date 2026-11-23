package com.MrSurenK.SpendCentsBackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class EndPointConfig {
    JwtAuthenticationFilter jwtAuthenticationFilter;
    AuthenticationProvider authenticationProvider;

    public EndPointConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                                AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Spring Security 6 onwards has deprecated fluent API style, use lambda as shown below
        http
                .csrf(AbstractHttpConfigurer::disable) //JWT is stateless and does not use any cookie for authentication
                .cors(Customizer.withDefaults()) //Enable cors configuration
                .authorizeHttpRequests((authorizeHttpRequests)->
                      authorizeHttpRequests
                              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                              .requestMatchers("/auth/**","/checkEmail","/checkUsername").permitAll() //These endpoints do not need authentication
                              .anyRequest().authenticated()) //All other endpoint need to be authenticated
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .formLogin(AbstractHttpConfigurer::disable) //Disable default Spring form login
                .logout(AbstractHttpConfigurer::disable) // Disable default Spring form logout
                .sessionManagement((sessionManagement)->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //JWT is stateless, do not require sessions
                .authenticationProvider(authenticationProvider) //Previously configured bean to instantiate DaoAuthenicationProvider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //Add custom JWTFilter before UsernamePassword filter

        return http.build(); //Build and return the SecurityFilterChain object after setting all the configurations above
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5173",
                "http://localhost:5173",
                "https://spendcents.surenk.dev"));
        corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);

        return source;
    }
}
