package br.com.autoarena.repository;

import br.com.autoarena.model.PasswordResetToken;
import br.com.autoarena.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // NOVO MÉTODO: Deleta todos os tokens associados a um usuário específico.
    // O Spring Data JPA irá inferir e criar a query correta.
    @Modifying // Indica que esta query modifica o estado do banco de dados
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user") // JPQL para deletar
    void deleteByUser(User user);
}