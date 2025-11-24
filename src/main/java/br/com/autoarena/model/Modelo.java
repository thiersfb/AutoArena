package br.com.autoarena.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Importar
import jakarta.validation.constraints.NotNull; // Importar

@Entity
@Table(name = "TBModelos") // Nome da tabela no banco de dados
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do modelo é obrigatório.") // Exemplo de validação
    @Column(nullable = false) // Nome do modelo (ex: "Civic", "Corolla")
    private String nome;

    @NotNull(message = "A montadora é obrigatória.") // <-- ADICIONADO: Garante que a montadora não seja nula
    @ManyToOne // Relacionamento Muitos Modelos para Uma Montadora
    @JoinColumn(name = "montadora_id", nullable = false) // Coluna que armazena o ID da montadora
    private Montadora montadora;

    @Column(name = "enabled", nullable = false) // Indica se o modelo está ativo/habilitado
    private boolean enabled;

    @NotNull(message = "O tipo de veículo é obrigatório.") // <-- ADICIONADO: Garante que o tipo de veículo não seja nulo
    @ManyToOne // Muitos modelos podem pertencer a um TipoVeiculo
    @JoinColumn(name = "tipo_veiculo_id", nullable = false) // Coluna no TBModelos que armazena o ID do TipoVeiculo
    private TipoVeiculo tipoVeiculo;

    // Construtor padrão
    public Modelo() {
        this.enabled = true; // Por padrão, um novo modelo é criado como ativo/habilitado
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

    public Montadora getMontadora() {
        return montadora;
    }

    public void setMontadora(Montadora montadora) {
        this.montadora = montadora;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    // --- Opcional: toString, equals, hashCode (boas práticas) ---
    @Override
    public String toString() {
        return "Modelo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", montadora=" + (montadora != null ? montadora.getNome() : "null") +
                ", enabled=" + enabled +
                ", tipoVeiculo=" + (tipoVeiculo != null ? tipoVeiculo.getNome() : "null") + // Evita NullPointerException
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modelo that = (Modelo) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}