package br.com.autoarena.enums;

public enum Cor {
    BRANCO("Branco"),
    PRETO("Preto"),
    PRATA("Prata"),
    CINZA("Cinza"),
    AZUL("Azul"),
    AMARELO("Amarelo"),
    VERMELHO("Vermelho"), // Combinação de combustíveis
    ROXO("Roxo"), // Combinação de combustíveis
    OUTRA("Outra"); // Para casos não listados

    private final String descricao;

    Cor(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
