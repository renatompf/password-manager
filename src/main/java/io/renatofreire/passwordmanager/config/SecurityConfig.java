package io.renatofreire.passwordmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers("/auth/register").permitAll();
                    auth.requestMatchers("/auth/login").permitAll();
                    auth.requestMatchers("/password-generator").permitAll();
                    auth.requestMatchers("/password-strength").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> {
                    logout.logoutUrl("/auth/logout");
                    logout.addLogoutHandler(logoutHandler);
                    logout.logoutSuccessHandler(
                            ((request, response, authentication) -> SecurityContextHolder.clearContext())
                    );
                });

        return httpSecurity.build();
    }
}
