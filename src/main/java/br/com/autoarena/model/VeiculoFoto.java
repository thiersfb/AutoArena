package br.com.autoarena.model;

import jakarta.persistence.*;

@Entity // Indica que esta classe é uma entidade JPA e mapeia para uma tabela no DB
@Table(name = "TBVeiculosFotos") // Nome da tabela no banco de dados
public class VeiculoFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    // Relacionamento com a entidade Veiculo
    // Uma foto pertence a um único veículo (ManyToOne)
    // Um veículo pode ter várias fotos (OneToMany, no lado do Veiculo)
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY é recomendado para evitar carregar todas as fotos do veículo em consultas de veículos
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    // A anotação @Lob indica que este campo armazenará um objeto grande.
    // O tipo de dados `byte[]` é ideal para dados binários como imagens.
    @Lob
    @Column(name = "foto_bytes", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] fotoBytes;


    // Um flag para indicar se esta é a foto principal (capa) do veículo
    @Column(name = "is_foto_principal")
    private boolean isFotoPrincipal;


    // Construtores
    public VeiculoFoto() {
    }

    public VeiculoFoto(Veiculo veiculo, byte[] fotoBytes, boolean isFotoPrincipal) {
        this.veiculo = veiculo;
        this.fotoBytes = fotoBytes;
        this.isFotoPrincipal = isFotoPrincipal;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public byte[] getFotoBytes() {
        return fotoBytes;
    }

    public void setFotoBytes(byte[] fotoBytes) {
        this.fotoBytes = fotoBytes;
    }

    public boolean isFotoPrincipal() {
        return isFotoPrincipal;
    }

    public void setFotoPrincipal(boolean fotoPrincipal) {
        isFotoPrincipal = fotoPrincipal;
    }
}
