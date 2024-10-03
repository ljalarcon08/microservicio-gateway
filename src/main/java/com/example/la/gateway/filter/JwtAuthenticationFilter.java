package com.example.la.gateway.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.la.common.usuario.entity.Sesion;
import com.example.la.common.usuario.service.SesionService;
import com.example.la.gateway.service.CustomUserDetailsService;
import com.example.la.gateway.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private JwtUtil jwtUtil;
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private CustomUserDetailsService userDetailsService;
	
	private SesionService sesionService;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil,ApplicationContext ctx) {
		this.jwtUtil=jwtUtil;
		this.userDetailsService=ctx.getBean(CustomUserDetailsService.class);
		this.sesionService=ctx.getBean(SesionService.class);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
		
		String username=null;
		String jwt=null;
		
		if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
			jwt=authorizationHeader.substring(7);
			username=jwtUtil.extractUserName(jwt);
		}
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userDetails=userDetailsService.loadUserByUsername(username);
			boolean tokenValido=jwtUtil.validateToken(jwt,userDetails);
			boolean estadoSesion=sesionValida(username);
			if(tokenValido && estadoSesion) {
				UsernamePasswordAuthenticationToken authentication=
						new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
		
	}
	
	private boolean sesionValida(String username) {
    	Optional<Sesion> sesionAct=sesionService.getSesionActByEmail(username);
    	Date fechaActual=new Date();
    	if(!sesionAct.isEmpty()) {
    		Date fechaExp=sesionAct.get().getExpiration();
    		if(fechaExp.compareTo(fechaActual)>0) {
    			return true;
    		}
    	}
    	return false;
	}
	
}
