package com.example.la.gateway.filter;

import com.example.la.common.usuario.entity.Rol;
import com.example.la.common.usuario.entity.Sesion;
import com.example.la.common.usuario.service.SesionService;
import com.example.la.gateway.service.CustomUserDetailsService;
import com.example.la.gateway.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtAuthenticationFilterTest {

    @Spy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CustomUserDetailsService userDetailsService;

    private SesionService sesionService;

    private ApplicationContext applicationContext;

    @Spy
    private final JwtUtil jwtUtil=new JwtUtil();

    private UserDetails userDetails;

    private String token;

    private long currentTime;

    @BeforeAll
    public void init(){
        ReflectionTestUtils.setField(jwtUtil, "secret", "1234567890ABCDEfghijklmNOPQRSTUVWXYZ1234567");
        currentTime=System.currentTimeMillis();
        List<Rol> roles=new ArrayList<>();
        Rol role=new Rol();
        role.setName("EJEMPLO");
        roles.add(role);

        List<GrantedAuthority> listAuth=roles.stream()
                .map(rol->new SimpleGrantedAuthority("ROLE"+rol.getName()))
                .collect(Collectors.toList());
        userDetails=new org.springframework.security.core.userdetails
                .User("email", "password", listAuth);
        token = jwtUtil.generateToken(userDetails, currentTime);
        applicationContext=Mockito.mock(ApplicationContext.class);
        userDetailsService=Mockito.mock(CustomUserDetailsService.class);
        sesionService=Mockito.mock(SesionService.class);
        Mockito.when(applicationContext.getBean(eq(CustomUserDetailsService.class))).thenReturn(userDetailsService);
        Mockito.when(applicationContext.getBean(eq(SesionService.class))).thenReturn(sesionService);
        jwtAuthenticationFilter=new JwtAuthenticationFilter(jwtUtil,applicationContext);
        Assert.notNull(jwtAuthenticationFilter,"OK");
    }

    @Test
    public void doFilterInternalTest() throws Exception{
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+token);
        MockHttpServletResponse response=new MockHttpServletResponse();
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()+24*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        FilterChain filter=new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };
        jwtAuthenticationFilter.doFilterInternal(request,response,filter);
    }

    @Test
    public void doFilterInternalTestCase() throws Exception{
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+token);
        MockHttpServletResponse response=new MockHttpServletResponse();
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        FilterChain filter=new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };
        jwtAuthenticationFilter.doFilterInternal(request,response,filter);
    }

    @Test
    public void doFilterInternalError3Test() throws Exception{
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+token);
        MockHttpServletResponse response=new MockHttpServletResponse();
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()-48*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        FilterChain filter=new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };
        jwtAuthenticationFilter.doFilterInternal(request,response,filter);
    }

    @Test
    public void doFilterInternalErrorTest() throws Exception{
        MockHttpServletRequest request=new MockHttpServletRequest();

        MockHttpServletResponse response=new MockHttpServletResponse();
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()+24*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        FilterChain filter=new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };
        jwtAuthenticationFilter.doFilterInternal(request,response,filter);
    }


    @Test
    public void doFilterInternalError2Test() throws Exception{
        MockHttpServletRequest request=new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION,token);
        MockHttpServletResponse response=new MockHttpServletResponse();
        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);

        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()+24*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        FilterChain filter=new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

            }
        };
        jwtAuthenticationFilter.doFilterInternal(request,response,filter);
    }
}
