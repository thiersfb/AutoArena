package br.com.autoarena.model;

import jakarta.persistence.*; // Use jakarta.persistence para Spring Boot 3+

import java.time.LocalDate;
import java.util.Collection; // Para GrantedAuthority
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity // Indica que esta classe é uma entidade JPA e mapeia para uma tabela no DB
@Table(name = "TBUsers") // Nome da tabela no banco de dados
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(unique = true, nullable = false) // Campo único e não nulo
    private String username;

    @Column(nullable = false) // Campo não nulo
    private String password;

    @Column(unique = true, nullable = false) // Adiciona a restrição de unicidade e não nulo no banco de dados
    private String email;

    // NOVOS CAMPOS
    @Column(nullable = true, length = 100) // Nome pode ser nulo se não for obrigatório
    private String nome;

    @Column(nullable = true, length = 100) // Sobrenome pode ser nulo se não for obrigatório
    private String sobrenome;

    @Column(name = "data_nascimento", nullable = true) // Nome da coluna no DB, pode ser nulo
    private LocalDate dataNascimento; // Usamos LocalDate para armazenar apenas a data

    @Column(nullable = false)
    private boolean enabled; // Se o usuário está ativo

    @Lob // Anotação para grandes objetos binários (BLOB)
    @Column(name = "foto_data", columnDefinition = "LONGBLOB") // ALTERADO: Use LONGBLOB para MySQL
    private byte[] fotoData; // Armazenará os bytes da imagem


    // Mapeia a coleção de strings (roles) para a tabela user_roles
    // Construtor padrão (necessário para JPA)
    //@ElementCollection(fetch = FetchType.EAGER) // Carrega os roles junto com o usuário
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER) // EAGER para carregar as roles junto com o usuário
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    //private List<String> roles; // Armazenar as roles como Strings (ex: "ROLE_USER", "ROLE_ADMIN")

    public User() {
        this.enabled = true; // Por padrão, usuários novos são ativos
    }

    // Construtor para criar um novo usuário
    public User(String nome, String sobrenome, String username, String password, String email, LocalDate dataNascimento, byte[] fotoData, boolean enabled) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.fotoData = fotoData;
        this.enabled = enabled;
    }


    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isEnabled() {
        //return true;
        return this.enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    //public List<String> getRoles() {
    public Set<String> getRoles() {
        return roles;
    }

    //public void setRoles(List<String> roles) {
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Método auxiliar para adicionar uma role
    public void addRole(String role) {
        this.roles.add(role);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    // Getter e Setter para fotoData (anteriormente fotoUrl)
    public byte[] getFotoData() {
        return fotoData;
    }

    public void setFotoData(byte[] fotoData) {
        this.fotoData = fotoData;
    }

    // Implementação dos métodos de UserDetails do Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implemente a lógica de expiração da conta se necessário
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implemente a lógica de bloqueio da conta se necessário
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implemente a lógica de expiração das credenciais se necessário
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", enabled=" + enabled +
                //", roles=" + roles +
                //", fotoUrl='" + fotoUrl + '\'' + // Adicionado fotoUrl
                '}';
    }
}