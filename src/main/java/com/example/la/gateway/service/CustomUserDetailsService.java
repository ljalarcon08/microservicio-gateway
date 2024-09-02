package com.example.la.gateway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.la.common.usuario.entity.Rol;
import com.example.la.common.usuario.entity.Usuario;
import com.example.la.common.usuario.service.UsuarioService;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioService usuarioService;
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private String ROLE="ROLE_";
	
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Usuario> usuarios=(List<Usuario>)usuarioService.findUsuarioByEmail(username);
		
		if(usuarios.isEmpty()) {
			throw new UsernameNotFoundException(username+" not found");
		}
		
		Usuario usuario=usuarios.get(0);
		logger.info(usuario.toString());
		List<Rol> roles=usuario.getRoles();
		
		List<GrantedAuthority> listAuth=roles.stream()
				.map(rol->new SimpleGrantedAuthority(ROLE+rol.getName()))
				.collect(Collectors.toList());
		logger.info("antes return userdetails");
		return new org.springframework.security.core.userdetails
				.User(usuario.getEmail(), usuario.getPassword(), listAuth);
	}
	
	
	
}
