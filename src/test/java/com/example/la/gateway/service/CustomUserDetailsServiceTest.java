package com.example.la.gateway.service;

import com.example.la.common.usuario.entity.Rol;
import com.example.la.common.usuario.entity.Usuario;
import com.example.la.common.usuario.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UsuarioService usuarioService;

    @Test
    public void loadUserByUsernameTest(){
        List<Usuario> usuarios=new ArrayList<>();
        Usuario usuario=new Usuario();
        usuario.setEmail("user");
        usuario.setPassword("pass");
        List<Rol> roles=new ArrayList<>();
        Rol rol=new Rol();
        rol.setName("rolname");
        roles.add(rol);
        usuario.setRoles(roles);
        usuarios.add(usuario);
        Mockito.when(usuarioService.findUsuarioByEmail(anyString())).thenReturn(usuarios);
        UserDetails userDetails=customUserDetailsService.loadUserByUsername("user");
        Assert.notNull(userDetails,"OK");
    }

    @Test
    public void loadUserByUsernameErrorTest(){
        List<Usuario> usuarios=new ArrayList<>();
        Mockito.when(usuarioService.findUsuarioByEmail(anyString())).thenReturn(usuarios);
        UsernameNotFoundException exception=
                Assertions.assertThrows(UsernameNotFoundException.class,()-> customUserDetailsService.loadUserByUsername("user"));
        Assert.state("user not found".equalsIgnoreCase(exception.getMessage()),"OK");

    }
}
