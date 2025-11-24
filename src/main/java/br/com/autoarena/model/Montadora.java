package br.com.autoarena.model;

import jakarta.persistence.*;

@Entity
//@Table(name = "TBMntadoras") // Nome da tabela no banco de dados
@Table(name = "TBMontadoras") // Nome da tabela no banco de dados
public class Montadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Nome da montadora (ex: "Chevrolet", "Ford")
    private String nome;

    @Column(name = "enabled", nullable = false) // Indica se a montadora está ativa/habilitada
    private boolean enabled;

    // Construtor padrão
    public Montadora() {
        this.enabled = true; // Por padrão, uma nova montadora é criada como ativa/habilitada
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // --- Opcional: toString, equals, hashCode (boas práticas) ---
    @Override
    public String toString() {
        return "Montadora{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Montadora that = (Montadora) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}