package com.example.la.gateway.util;

import com.example.la.common.usuario.entity.Rol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtUtilTest {

    @Spy
    private final JwtUtil jwtUtil=new JwtUtil();

    private UserDetails userDetails;
    private long currentTime;

    private String token;

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
    }

    @Test
    public void generateTokenTest(){
        Assert.notNull(token,"OK");
    }

    @Test
    public void extractUserNameTest(){
        String user=jwtUtil.extractUserName(token);
        Assert.state("email".equals(user),"OK");
    }

    @Test
    public void isTokenExpiredTest(){
        Boolean expired=jwtUtil.isTokenExpired(token);
        Assert.state(!expired,"OK");
    }

    @Test
    public void validateTokenTest(){
        Boolean valido = jwtUtil.validateToken(token, userDetails);
        Assert.state(valido,"OK");
    }

}
