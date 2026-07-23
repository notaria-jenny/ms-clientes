package notaria.jenny.ms_clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * DEUDA TÉCNICA: hoy toda la API está abierta (permitAll) para desarrollo.
     * Antes de producción: autenticación JWT centralizada en el API Gateway.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/swagger-iu/**",
                        "/swagger-iu.html",
                        "/v3/api-docs/**",
                        "/actuator/health/**",
                        "/actuator/info"
                )
                .permitAll()
                .anyRequest()
                .permitAll()
                );
        return http.build();
    }
}
