package com.example.la.gateway.controller;

import com.example.la.common.usuario.entity.Usuario;
import com.example.la.common.usuario.service.UsuarioService;
import com.example.la.gateway.domain.AuthenticationRequest;
import com.example.la.gateway.domain.AuthenticationResponse;
import com.example.la.gateway.domain.CheckTokenResponse;
import com.example.la.gateway.domain.RequestCambioPass;
import com.example.la.gateway.service.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private BCryptPasswordEncoder bcryp;

    private Usuario usuario;

    @BeforeAll
    public void init() {
        usuario=new Usuario();
        usuario.setPassword("pass");
    }

    @Test
    public void createAuthenticationTokenTest() throws Exception{
        AuthenticationRequest authenticationRequest=new AuthenticationRequest();
        authenticationRequest.setPassword("password");
        authenticationRequest.setUsername("username");
        Mockito.when(jwtService.createJwtToken(any(AuthenticationRequest.class))).thenReturn("");
        ResponseEntity<AuthenticationResponse> respuesta = (ResponseEntity<AuthenticationResponse>)authenticationController.createAuthenticationToken(authenticationRequest);
        Assert.notNull(respuesta.getBody(),"OK");
        Assert.notNull(respuesta.getBody().getJwt(),"OK");
        Assert.notNull(authenticationRequest.getUsername(),"OK");
    }

    @Test
    public void registerUserTest(){
        Mockito.when(bcryp.encode(eq("pass"))).thenReturn("");
        Mockito.when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);
        ResponseEntity<Usuario> respuesta = (ResponseEntity<Usuario>)authenticationController.registerUser(usuario);
        Assert.notNull(respuesta.getBody(),"OK");
    }

    @Test
    public void registerUserTestError(){
        Mockito.when(bcryp.encode(any(CharSequence.class))).thenReturn("");
        Mockito.when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(null);
        ResponseEntity<Usuario> respuesta = (ResponseEntity<Usuario>)authenticationController.registerUser(usuario);
        Assert.state(respuesta.getStatusCode()==HttpStatus.BAD_REQUEST,"OK");
    }

    @Test
    public void cambioPassTest(){
        Mockito.when(bcryp.encode(any(CharSequence.class))).thenReturn("");
        Mockito.doNothing().when(jwtService).cambioPassword(any(RequestCambioPass.class));
        RequestCambioPass request=new RequestCambioPass();
        request.setNuevoPassword("nuevopsw");
        request.setEmail("email");
        request.setAnteriorPassword("psw");
        ResponseEntity<?> respuesta = authenticationController.cambioPass(request);
        Assert.notNull(respuesta,"OK");
        Assert.notNull(request.getNuevoPassword(),"OK");
        Assert.notNull(request.getAnteriorPassword(),"OK");
        Assert.notNull(request.getEmail(),"OK");
    }

    @Test
    public void checkTokenTest(){
        CheckTokenResponse checkToken=new CheckTokenResponse();
        checkToken.setValid(true);
        Mockito.when(jwtService.checkToken(anyString())).thenReturn(checkToken);
        ResponseEntity<CheckTokenResponse> response = (ResponseEntity<CheckTokenResponse>)authenticationController.checkToken("bearertok");

        Assert.notNull(response.getBody(),"OK");
        CheckTokenResponse check=response.getBody();
        Assert.state(check.isValid(),"OK");

    }

    @Test
    public void logoutTest(){
        Mockito.doNothing().when(jwtService).desactivarSesion(anyString());
        ResponseEntity<?> respuesta = authenticationController.logout("bearer");
        Assert.state(respuesta.getStatusCode()==HttpStatus.OK,"OK");
    }
}
