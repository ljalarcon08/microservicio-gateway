package com.example.la.gateway.service;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.la.common.usuario.entity.Sesion;
import com.example.la.common.usuario.service.SesionService;
import com.example.la.common.usuario.service.UsuarioService;
import com.example.la.gateway.domain.AuthenticationRequest;
import com.example.la.gateway.domain.CheckTokenResponse;
import com.example.la.gateway.domain.RequestCambioPass;
import com.example.la.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Service
public class JwtService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private SesionService sesionService;
    
    Logger logger=LoggerFactory.getLogger(JwtService.class);
    
    
    public String createJwtToken(AuthenticationRequest authenticationRequest) throws Exception{
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        
        
        long currentTime=System.currentTimeMillis();
        
        sesionService.logout(authenticationRequest.getUsername());
        
        Sesion sesion=new Sesion();
        sesion.setEmail(authenticationRequest.getUsername());
        sesion.setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10));
        sesion.setEstado("ACTIVO");
        
        sesionService.crearSesion(sesion);
        
        return jwtUtil.generateToken(userDetails,currentTime);
    }
    
    public void cambioPassword(RequestCambioPass requestCambioPass) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        		requestCambioPass.getEmail(), requestCambioPass.getAnteriorPassword()));
        
        usuarioService.actualizarPassUsuario(requestCambioPass.getEmail(),requestCambioPass.getNuevoPassword());
    }
    
    
    public CheckTokenResponse checkToken(String bearerToken) {
    	CheckTokenResponse response=new CheckTokenResponse();
    	String token=bearerToken.replace("Bearer ", "");
    	String username=jwtUtil.extractClaim(token, Claims::getSubject);
    	boolean tokenValido=!jwtUtil.isTokenExpired(token);
    	Optional<Sesion> sesionAct=sesionService.getSesionActByEmail(username);
    	Date fechaActual=new Date();
    	boolean sesionValida=false;
    	if(!sesionAct.isEmpty()) {
    		Date fechaExp=sesionAct.get().getExpiration();
    		if(fechaExp.compareTo(fechaActual)>0) {
    			sesionValida=true;
    		}
    	}
    	response.setValid(tokenValido && sesionValida);
    	return response;
    }
    
    
    public void desactivarSesion(String bearerToken) {
    	String token=bearerToken.replace("Bearer ", "");
    	String username=jwtUtil.extractClaim(token, Claims::getSubject);
    	sesionService.logout(username);    	
    }
}
