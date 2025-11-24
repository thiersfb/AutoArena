package br.com.autoarena.security; // Pacote corrigido

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // <--- Esta importação não será mais necessária
import org.springframework.boot.autoconfigure.security.servlet.PathRequest; // <--- NOVA IMPORTAÇÃO para recursos estáticos

import br.com.autoarena.service.CustomUserDetailsService; // Importe seu CustomUserDetailsService
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // NOVO: para configurar o provedor de autenticação
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Remova importações de User, UserDetails, InMemoryUserDetailsManager se não for mais usar usuários em memória
import org.springframework.security.core.userdetails.UserDetailsService; // Mantenha esta importação
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a segurança web do Spring Security
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService; // Injetar seu serviço

    // Construtor para injetar CustomUserDetailsService
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso público a recursos estáticos comuns (css, js, imagens, webjars, favicon)
                        // Esta é a forma recomendada e mais concisa para Spring Boot 3+ / Spring Security 6+
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // Permite acesso público a outras URLs específicas
                        // Não precisa mais do 'new AntPathRequestMatcher()'
                        .requestMatchers("/",
                                "/home",
                                //"/hello",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/empresa",
                                "/estoque",
                                "/contato",
                                "/detalhes/**",
                                "/api/modelos/**",
                                "/registro",
                                "/login",
                                "/forgot",
                                "/error/**",
                                "/reset",
                                "reset_password_submit"
                        ).permitAll()
                        // Permite o endpoint AJAX de estados por país publicamente (se não for para ser protegido)
                        .requestMatchers("/api/estados-por-pais/**").permitAll()
                        // **ADICIONE ESTA LINHA:** Todas as URLs que começam com /private/ exigem autenticação
                        .requestMatchers("/private/**").authenticated() // Somente usuários autenticados podem acessar /private/**

                        // Todas as outras requisições requerem autenticação
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Define a URL da sua página de login customizada
                        .defaultSuccessUrl("/private/dashboard", true) // Redireciona para /private após login bem-sucedido (forçado)
                        .failureUrl("/login?error") // Redireciona para /login?error em caso de falha no login
                        .permitAll() // Permite que todos acessem o formulário de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL que aciona o logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // Invalida a sessão HTTP
                        .deleteCookies("JSESSIONID") // Deleta o cookie de sessão
                        .permitAll() // Permite que a rota de logout seja acessada publicamente
                )
                //.csrf(csrf -> csrf.disable()); // Desabilita CSRF para simplificar em desenvolvimento (NÃO FAÇA EM PRODUÇÃO sem entender os riscos)
                // --- Configuração CSRF ---
                // Opção 1 (Recomendado): Usar o padrão (não precisa configurar explicitamente .csrf() se quiser o padrão)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")); // Exemplo para ignorar CSRF em APIs específicas, se aplicável

                // Opção 2 (NÃO RECOMENDADO para web apps com formulários): Desativar CSRF
                // .csrf(csrf -> csrf.disable());

                // Opção 3 (Avançado): Configurar um handler de token CSRF para APIs (se você estiver misturando formulários e APIs)
                // .csrf(csrf -> csrf
                //    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Permite que o JS leia o token do cookie
                //    .csrfTokenRequestAttributeHandler(new CsrfTokenRequestAttributeHandler())
                // );


        // Se você estiver usando H2 Console e tiver problemas de frames (SameOrigin), pode precisar disso:
        // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }


    // NOVO: Bean para o provedor de autenticação
    // Este bean configura como o Spring Security vai carregar os usuários e verificar senhas
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); // Usa o seu CustomUserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Usa o seu PasswordEncoder
        return authProvider;
    }

    // Bean para o codificador de senhas (necessário para Spring Security 5+)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Bean para definir usuários em memória (para testes - você pode remover isso quando usar um banco de dados)
    /*
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password")) // Codifica a senha
                .roles("USER") // Define a role (função) do usuário
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin")) // Codifica a senha
                .roles("ADMIN", "USER") // Define múltiplas roles
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
    */

}