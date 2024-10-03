package com.example.la.gateway.config;

import com.example.la.gateway.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApplicationContext appContext;

    @Test
    public void securityFilterChainTest() throws Exception {
        HttpSecurity http= Mockito.mock(HttpSecurity.class);
        Mockito.when(http.cors(any(Customizer.class))).thenReturn(http);
        Mockito.when(http.csrf(any(Customizer.class))).thenReturn(http);
        Mockito.when(http.authorizeHttpRequests(any(Customizer.class))).thenReturn(http);
        Mockito.when(http.build()).thenReturn(new DefaultSecurityFilterChain(new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return false;
            }
        }));
        SecurityFilterChain response = securityConfig.securityFilterChain(http);


        Assert.notNull(response,"error");
    }

    @Test
    public void securityFilterChain2Test() throws Exception {

    }

    @Test
    public void corsConfigurationSourceTest(){
        CorsConfigurationSource response = securityConfig.corsConfigurationSource();
        Assert.notNull(response,"error");
    }

    @Test
    public void authenticationManagerTest() throws Exception {
        AuthenticationConfiguration authenticationConfiguration=Mockito.mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager=Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        AuthenticationManager response = securityConfig.authenticationManager(authenticationConfiguration);
        Assert.notNull(response,"error");
    }

    @Test
    public void passwordEncoderTest(){
        PasswordEncoder response = securityConfig.passwordEncoder();
        Assert.notNull(response,"error");
    }
}
