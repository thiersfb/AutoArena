package br.com.autoarena.repository;

import br.com.autoarena.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Indica que esta interface é um componente de repositório Spring
public interface UserRepository extends JpaRepository<User, Long> {
    // Método customizado para encontrar um usuário pelo username
    Optional<User> findByUsername(String username);

    // Método para buscar usuário por email
    Optional<User> findByEmail(String email);

    // Opcional: para checar unicidade de username durante a edição
    Optional<User> findByUsernameAndIdNot(String username, Long id);

}