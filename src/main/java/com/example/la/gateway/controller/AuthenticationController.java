package com.example.la.gateway.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.la.common.usuario.entity.Usuario;
import com.example.la.common.usuario.service.UsuarioService;
import com.example.la.gateway.domain.AuthenticationRequest;
import com.example.la.gateway.domain.AuthenticationResponse;
import com.example.la.gateway.domain.RequestCambioPass;
import com.example.la.gateway.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private BCryptPasswordEncoder bcryp; 
	
	Logger logger=LoggerFactory.getLogger(AuthenticationController.class);
	
	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
		String password=authenticationRequest.getPassword();
		authenticationRequest.setPassword(password);
		final String jwt=jwtService.createJwtToken(authenticationRequest);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody Usuario usuario){
		usuario.setPassword(bcryp.encode(usuario.getPassword()));
		Usuario usuarioCreado=usuarioService.crearUsuario(usuario);
		
		if(usuarioCreado!=null) {
			return ResponseEntity.ok(usuarioCreado);
		}
		return ResponseEntity.badRequest().body("Usuario ya existe");
	}
	
	@PostMapping("/cambiarPass")
	public ResponseEntity<?> cambioPass(@RequestBody RequestCambioPass request){
		request.setNuevoPassword(bcryp.encode(request.getNuevoPassword()));
		logger.info(request.getNuevoPassword());
		jwtService.cambioPassword(request);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/checkToken")
	public ResponseEntity<?> getFecha(@RequestHeader("Authorization") String bearerToken){
		return ResponseEntity.ok(jwtService.checkToken(bearerToken));
	}
	
	
	@GetMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken){
		jwtService.desactivarSesion(bearerToken);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
