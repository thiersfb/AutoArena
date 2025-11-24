package br.com.autoarena.enums;

public enum TipoDirecao {
    HIDRAULICA("Hidráulica"),
    ELETRICA("Elétrica"),
    MECANICA("Mecânica"),
    ELETRO_HIDRAULICA("Eletro-hidráulica"); // Adicionado para ser mais completo

    private final String descricao;

    TipoDirecao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}