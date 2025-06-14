package org.example.usermanagement.config;

import org.example.usermanagement.repository.UserRepository;
import org.example.usermanagement.security.JwtAuthenticationFilter;
import org.example.usermanagement.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import org.apache.catalina.connector.Connector;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/users/").hasAuthority("CAN_CREATE_USER")
                .requestMatchers(HttpMethod.GET, "/users/").hasAuthority("CAN_READ_USERS")
                .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAuthority("CAN_UPDATE_USERS")
                .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasAuthority("CAN_DELETE_USERS")

                .requestMatchers(HttpMethod.POST, "/orders/").hasAuthority("CAN_PLACE_ORDER")
                .requestMatchers(HttpMethod.GET, "/orders/").hasAuthority("CAN_SEARCH_ORDER")
                .requestMatchers(HttpMethod.GET, "/orders/search").hasAuthority("CAN_SEARCH_ORDER")
                .requestMatchers(HttpMethod.PUT, "/orders/cancel/").hasAuthority("CAN_CANCEL_ORDER")
                .requestMatchers(HttpMethod.GET, "/orders/track/{id}").hasAuthority("CAN_TRACK_ORDER")
                .requestMatchers(HttpMethod.POST, "/orders/schedule/").hasAuthority("CAN_SCHEDULE_ORDER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setScheme("http");
            connector.setPort(8081);
            connector.setSecure(false);
            connector.setRedirectPort(8443);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

