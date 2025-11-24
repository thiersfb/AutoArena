package br.com.autoarena.enums;

public enum TipoCarroceria {
    HATCH("Hatch"),
    SEDAN("Sedan"),
    SUV("SUV"),
    PICKUP("Pickup"),
    PERUA("Perua"),
    COUPE("Coupê"),
    CONVERSIVEL("Conversível"),
    MINIVAN("Minivan"),
    UTILITARIO("Utilitário"),
    OUTRO("Outro"); // Para casos não listados

    private final String descricao;

    TipoCarroceria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}