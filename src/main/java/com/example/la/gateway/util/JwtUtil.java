package com.example.la.gateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

	Logger logger=LoggerFactory.getLogger(getClass()); 
	
	@Value("${jwt.secret}")
	private String secret;
	
	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	
	public <T> T extractClaim(String token,Function<Claims,T> claimsResolver) {
		final Claims claims=extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	
	public Claims extractAllClaims(String token) {
		//return Jwts.parser().setSigningKey(secret).build().parseSignedClaims(token).getPayload();
		
		return Jwts.parser().verifyWith(getSecret()).build().parseSignedClaims(token).getPayload();
	}
	
	
    private SecretKey getSecret() {
		byte[] bytes=Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(bytes);
	}


	public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
	
	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	public String generateToken(UserDetails userDetails,long currentTime) {
		Map<String,Object> claims=new HashMap<>();
		List<String> claimList=userDetails.getAuthorities().stream()
				.map(auth->auth.getAuthority()).peek(auth->logger.info(auth)).toList();
		claims.put("roles",claimList);
		return createToken(claims,userDetails.getUsername(),currentTime);
		
	}

	private String createToken(Map<String, Object> claims, String username,long currentTime) {
		
		return Jwts.builder().subject(username).claims(claims)
				.header().type("JWT").and()
				.issuedAt(new Date(currentTime))
				.expiration(new Date(currentTime+1000*60*60*10))
				.signWith(getSecret(),Jwts.SIG.HS256).compact();
	}
}
