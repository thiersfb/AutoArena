package br.com.autoarena.service;

import br.com.autoarena.model.User;
import br.com.autoarena.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // Indica que esta classe é um serviço Spring
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    // Injeção de dependência do UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Encontra o usuário pelo username (ou email, dependendo da sua lógica)
        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username: " + username));

        // Converte as roles do seu modelo (strings como "ADMIN")
        // para GrantedAuthority do Spring Security (strings como "ROLE_ADMIN")
        Collection<? extends GrantedAuthority> authorities =
                usuario.getRoles().stream()
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName)) // Adiciona o prefixo "ROLE_"
                        .collect(Collectors.toList());

        // Retorna um objeto UserDetails que o Spring Security pode usar
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isEnabled(),
                true, true, true,
                authorities
        );
    }

    /*
    private Collection<? extends GrantedAuthority> getAuthorities(Collection<br.com.autoarena.model.Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
    */

    /*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
    */
}