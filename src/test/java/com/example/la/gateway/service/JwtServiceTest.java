package com.example.la.gateway.service;

import com.example.la.common.usuario.entity.Rol;
import com.example.la.common.usuario.entity.Sesion;
import com.example.la.common.usuario.service.SesionService;
import com.example.la.common.usuario.service.UsuarioService;
import com.example.la.gateway.domain.AuthenticationRequest;
import com.example.la.gateway.domain.CheckTokenResponse;
import com.example.la.gateway.domain.RequestCambioPass;
import com.example.la.gateway.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SesionService sesionService;

    @Test
    public void createJwtTokenTest() throws Exception {
        Authentication auth=Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        List<Rol> roles=new ArrayList<>();
        Rol role=new Rol();
        role.setName("EJEMPLO");
        roles.add(role);

        List<GrantedAuthority> listAuth=roles.stream()
                .map(rol->new SimpleGrantedAuthority("ROLE"+rol.getName()))
                .collect(Collectors.toList());
        User userDetails = new User("email", "password", listAuth);

        Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        Mockito.doNothing().when(sesionService).logout(anyString());
        Mockito.when(sesionService.crearSesion(any(Sesion.class))).thenReturn(new Sesion());
        Mockito.when(jwtUtil.generateToken(any(User.class),anyLong())).thenReturn("token");

        AuthenticationRequest authenticationRequest=new AuthenticationRequest();
        authenticationRequest.setUsername("email");
        authenticationRequest.setPassword("password");
        String respuesta=jwtService.createJwtToken(authenticationRequest);
        Assert.notNull(respuesta,"OK");
    }

    @Test
    public void cambioPasswordTest(){
        Authentication auth=Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        Mockito.doNothing().when(usuarioService).actualizarPassUsuario(anyString(),anyString());

        RequestCambioPass request=new RequestCambioPass();
        request.setAnteriorPassword("pass");
        request.setEmail("email");
        request.setNuevoPassword("pass2");
        jwtService.cambioPassword(request);
    }

    @Test
    public void checkTokenTest(){
        Mockito.when(jwtUtil.extractClaim(anyString(),any(Function.class))).thenReturn("email");
        Mockito.when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()+24*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        CheckTokenResponse respuesta = jwtService.checkToken("tok");
        Assert.state(respuesta.isValid(),"OK");
    }

    @Test
    public void checkTokenTests(){
        Mockito.when(jwtUtil.extractClaim(anyString(),any(Function.class))).thenReturn("email");
        Mockito.when(jwtUtil.isTokenExpired(anyString())).thenReturn(true);
        Sesion sesion=new Sesion();
        sesion.setExpiration(new Date(new Date().getTime()-24*3600*1000));
        Optional<Sesion> sesionOp=Optional.of(sesion);
        Mockito.when(sesionService.getSesionActByEmail(anyString())).thenReturn(sesionOp);
        CheckTokenResponse respuesta = jwtService.checkToken("tok");
        Assert.state(!respuesta.isValid(),"OK");
    }

    @Test
    public void checkTokenTestCase(){
        Mockito.when(jwtUtil.extractClaim(anyString(),any(Function.class))).thenReturn("email");
        Mockito.when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
        CheckTokenResponse respuesta = jwtService.checkToken("tok");
        Assert.state(!respuesta.isValid(),"OK");
    }

    @Test
    public void desactivarSesionTEst(){
        Mockito.when(jwtUtil.extractClaim(anyString(),any(Function.class))).thenReturn("email");
        Mockito.doNothing().when(sesionService).logout(anyString());
        jwtService.desactivarSesion("tok");
    }
}
