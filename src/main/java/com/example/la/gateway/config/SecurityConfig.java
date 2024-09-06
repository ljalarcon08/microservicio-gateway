package com.example.la.gateway.config;

import java.util.List;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import jakarta.servlet.Filter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.la.gateway.filter.JwtAuthenticationFilter;
import com.example.la.gateway.service.CustomUserDetailsService;
import com.example.la.gateway.util.JwtUtil;

@Configuration
@SecurityScheme(
		name = "Bearer Authentication",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer"
)
@EnableWebSecurity
public class SecurityConfig{

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorizeRequests->
		authorizeRequests
		.requestMatchers(HttpMethod.GET,"/api/usuario/email/**").hasAnyRole("USER","ADMIN")
		.requestMatchers(HttpMethod.GET,"/api/usuario/usuario/v1/**").permitAll()
		.requestMatchers("/api/usuario/**").hasRole("ADMIN")
		.requestMatchers(HttpMethod.POST,"/api/producto/**").hasAnyRole("ADMIN_PRODUCTO","ADMIN")
		.requestMatchers(HttpMethod.PUT,"/api/producto/**").hasAnyRole("ADMIN_PRODUCTO","ADMIN")
		.requestMatchers(HttpMethod.DELETE,"/api/producto/**").hasAnyRole("ADMIN_PRODUCTO","ADMIN")
		.requestMatchers(HttpMethod.GET,"/api/producto/**").permitAll()
		.requestMatchers(HttpMethod.GET,"/api/producto/**").permitAll()
		.requestMatchers("/auth/**","/auth/login").permitAll()
		.anyRequest().authenticated())
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		JwtAuthenticationFilter filter=new JwtAuthenticationFilter(jwtUtil,getApplicationContext());
		
		Class<? extends Filter> clazz=UsernamePasswordAuthenticationFilter.class;
		
		
		
		http.addFilterBefore(filter, clazz);
		
		return http.build();
	}
	
	
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
	
    private ApplicationContext getApplicationContext() {
		return this.appContext;
	}

	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
}
