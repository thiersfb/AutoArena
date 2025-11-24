// src/main/java/br/com/autoarena/model/Estado.java
package br.com.autoarena.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "TBEstado")
public class Estado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponde ao 'id int' na sua descrição

    @Column(name = "nome", nullable = false, length = 100)
    private String nome; // Corresponde ao 'nome string'

    @Column(name = "uf", nullable = false, length = 2)
    private String uf; // Corresponde ao 'uf string'

    // Relacionamento Many-to-One com a entidade Pais
    // Muitos Estados podem pertencer a um único País
    @ManyToOne
    @JoinColumn(name = "pais_id", nullable = false) // 'pais_id' é a coluna da chave estrangeira na tabela 'estado'
    private Pais pais; // JPA fará o mapeamento para 'pais_id int'

    // Construtores
    public Estado() {
    }

    public Estado(String nome, String uf, Pais pais) {
        this.nome = nome;
        this.uf = uf;
        this.pais = pais;
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

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    // Métodos equals e hashCode (essenciais para entidades JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estado estado = (Estado) o;
        return Objects.equals(id, estado.id); // Comparar por ID é geralmente suficiente para entidades
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Estado{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", uf='" + uf + '\'' +
                ", pais=" + (pais != null ? pais.getNome() : "null") + // Evita NullPointerException
                '}';
    }
}