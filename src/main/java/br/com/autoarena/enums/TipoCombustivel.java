package br.com.autoarena.enums;

public enum TipoCombustivel {
    GASOLINA("Gasolina"),
    ETANOL("Etanol"),
    FLEX("Flex"),
    DIESEL("Diesel"),
    ELETRICO("Elétrico"),
    GNV("GNV"),
    //GNV("Gás Natural Veicular"),
    HIBRIDO("Híbrido"), // Combinação de combustíveis
    OUTRO("Outro"); // Para casos não listados

    private final String descricao;

    TipoCombustivel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}