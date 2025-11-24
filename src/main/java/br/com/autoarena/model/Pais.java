package br.com.autoarena.model;

import jakarta.persistence.*; // Use jakarta.persistence para Spring Boot 3+

import java.util.Objects;


@Entity // Indica que esta classe é uma entidade JPA e mapeia para uma tabela no DB
@Table(name = "TBPaises") // Nome da tabela no banco de dados
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(unique = true, nullable = false) // Campo único e não nulo
    private String nome;

    // Construtor padrão (necessário para JPA)
    public Pais() {
    }

    // Construtor com nome (útil para criar novos países)
    public Pais(String nome) {
        this.nome = nome;
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


    // Métodos equals e hashCode (boa prática para entidades JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pais pais = (Pais) o;
        return Objects.equals(id, pais.id) && Objects.equals(nome, pais.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Pais{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
