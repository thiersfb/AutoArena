package br.com.autoarena.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*; // Use jakarta.persistence para Spring Boot 3+

import java.time.LocalDateTime;

@Entity
@Table(name = "TBPasswordResetToken")
public class PasswordResetToken {

    private static final int EXPIRATION_MINUTES = 60 * 24; // Token válido por 24 horas

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken() {
        // Inicializa a data de expiração para 24 horas a partir de agora
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}