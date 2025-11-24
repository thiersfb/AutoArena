package br.com.autoarena.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TBTipos_Veiculo")
public class TipoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(unique = true, nullable = false) // Campo único e não nulo
    private String nome;

    @Column(nullable = false)
    private boolean enabled;


    // Construtor padrão
    public TipoVeiculo() {
        this.enabled = true; // Por padrão, um novo tipo de veículo pode ser ativo
    }

    // Getters e Setters
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

    public boolean isEnabled() { // Para boolean primitivo, o getter é isEnabled()
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Opcional: toString, equals, hashCode (boas práticas)
    @Override
    public String toString() {
        return "TipoVeiculo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", ativo=" + enabled +
                '}';
    }
}
