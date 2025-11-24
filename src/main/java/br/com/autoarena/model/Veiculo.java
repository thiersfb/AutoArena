package br.com.autoarena.model;

import br.com.autoarena.enums.Cor;
import br.com.autoarena.enums.TipoCarroceria;
import br.com.autoarena.enums.TipoCombustivel;
import br.com.autoarena.enums.TipoDirecao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TBVeiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento Many-to-One com TipoVeiculo
    @ManyToOne
    @JoinColumn(name = "tipo_veiculo_id", nullable = false)
    private TipoVeiculo tipoVeiculo;

    // Relacionamento Many-to-One com Montadora
    @ManyToOne
    @JoinColumn(name = "montadora_id", nullable = false)
    private Montadora montadora;

    // Relacionamento Many-to-One com Modelo
    @ManyToOne
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(name = "ano_modelo", nullable = false)
    private Integer anoModelo;

    @Column(name = "ano_fabricacao", nullable = false)
    private Integer anoFabricacao;

    //@Column(nullable = false, length = 50)
    //private String cor;


    @Enumerated(EnumType.STRING)
    //@Column(name = "tipo_combustivel", nullable = false)
    private Cor cor;

    @Column(nullable = false, length = 100)
    private String motor;

    @Column(name = "tem_ar_condicionado", nullable = false)
    private boolean temArCondicionado;

    @Column(name = "tem_vidro_eletrico", nullable = false)
    private boolean temVidroEletrico;

    @Column(name = "tem_travas_eletricas", nullable = false)
    private boolean temTravasEletricas;

    // NOVOS CHECKBOX
    @Column(name = "tem_freio_abs", nullable = false)
    private boolean temFreioAbs;

    @Column(name = "tem_air_bag", nullable = false)
    private boolean temAirBag;

    @Column(name = "tem_central_multimidia", nullable = false)
    private boolean temCentralMultimidia;

    @Column(name = "tem_comando_volante", nullable = false)
    private boolean temComandosVolante;

    @Column(name = "tem_alarme", nullable = false)
    private boolean temAlarme;

    @Column(name = "tem_limpador_traseiro", nullable = false)
    private boolean temLimpadorTraseiro;

    @Column(name = "tem_desembacador_traseiro", nullable = false)
    private boolean temDesembacadorTraseiro;

    @Column(name = "tem_camera_re", nullable = false)
    private boolean temCameraRe;

    @Column(name = "tem_sensor_estacionamento", nullable = false)
    private boolean temSensorEstacionamento;

    @Column(name = "tem_cambio_automatico", nullable = false)
    private boolean temCambioAutomatico;


    // Armazena o nome da enum no banco de dados
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_direcao", nullable = false)
    private TipoDirecao tipoDirecao;

    @Column(name = "final_placa", nullable = false)
    private Integer finalPlaca; // 1 caractere numérico

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCarroceria carroceria;

    @Column(nullable = true)
    private Integer quilometragem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustivel", nullable = false)
    private TipoCombustivel tipoCombustivel;

    // Booleano para indicar se o veículo está à venda
    @Column(name = "for_sale", nullable = false)
    private boolean forSale;

    @Column(name = "data_venda", nullable = true) // Opcional
    private LocalDate dataVenda;

    @Column(name = "preco_anunciado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAnunciado;

    @Column(name = "preco_vendido", nullable = true, precision = 10, scale = 2) // Opcional
    private BigDecimal precoVendido;

    @Column(name = "informacoes_adicionais")
    private String informacoesAdicionais;

    // --- NOVOS CAMPOS PARA LOCALIZAÇÃO ---
    @ManyToOne
    @JoinColumn(name = "pais_id", nullable = false) // Assumindo que o país é obrigatório
    private Pais pais;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false) // Assumindo que o estado é obrigatório
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "cidade_id", nullable = false) // Assumindo que a cidade é obrigatória
    private Cidade cidade;
    // ----------------------------------


    // Relacionamento Many-to-One com User para o usuário que cadastrou
    @ManyToOne
    @JoinColumn(name = "usuario_cadastro_id", nullable = false)
    private User usuarioCadastro;


    // Relacionamento com a entidade VeiculoFoto
    // O 'mappedBy' indica o nome da propriedade na classe VeiculoFoto que mapeia este relacionamento.
    // 'cascade = CascadeType.ALL' significa que se um veículo for deletado, suas fotos também serão.
    // 'orphanRemoval = true' garante que fotos sem veículo sejam excluídas automaticamente.
    // 'fetch = FetchType.LAZY' é uma boa prática para evitar carregar todas as fotos por padrão.
    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<VeiculoFoto> fotos = new HashSet<>(); // Inicialize a coleção para evitar NullPointerException


    public Veiculo() {
        this.temArCondicionado = false;
        this.temVidroEletrico = false;
        this.temTravasEletricas = false;
        this.temFreioAbs = false;

        this.forSale = true; // Por padrão, um novo veículo é listado como 'à venda'
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoVeiculo getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public Montadora getMontadora() {
        return montadora;
    }

    public void setMontadora(Montadora montadora) {
        this.montadora = montadora;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Integer getAnoModelo() {
        return anoModelo;
    }

    public void setAnoModelo(Integer anoModelo) {
        this.anoModelo = anoModelo;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    /*
    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }
    */


    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }


    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public boolean isTemArCondicionado() {
        return temArCondicionado;
    }

    public void setTemArCondicionado(boolean temArCondicionado) {
        this.temArCondicionado = temArCondicionado;
    }

    public boolean isTemVidroEletrico() {
        return temVidroEletrico;
    }

    public void setTemVidroEletrico(boolean temVidroEletrico) {
        this.temVidroEletrico = temVidroEletrico;
    }

    public boolean isTemTravasEletricas() {
        return temTravasEletricas;
    }

    public void setTemTravasEletricas(boolean temTravasEletricas) {
        this.temTravasEletricas = temTravasEletricas;
    }

    public boolean isTemCameraRe() {
        return temCameraRe;
    }

    public void setTemCameraRe(boolean temCameraRe) {
        this.temCameraRe = temCameraRe;
    }

    public boolean isTemFreioAbs() {
        return temFreioAbs;
    }

    public void setTemFreioAbs(boolean temFreioAbs) {
        this.temFreioAbs = temFreioAbs;
    }

    public boolean isTemAirBag() {
        return temAirBag;
    }

    public void setTemAirBag(boolean temAirBag) {
        this.temAirBag = temAirBag;
    }

    public boolean isTemCentralMultimidia() {
        return temCentralMultimidia;
    }

    public void setTemCentralMultimidia(boolean temCentralMultimidia) {
        this.temCentralMultimidia = temCentralMultimidia;
    }

    public boolean isTemComandosVolante() {
        return temComandosVolante;
    }

    public void setTemComandosVolante(boolean temComandosVolante) {
        this.temComandosVolante = temComandosVolante;
    }

    public boolean isTemAlarme() {
        return temAlarme;
    }

    public void setTemAlarme(boolean temAlarme) {
        this.temAlarme = temAlarme;
    }

    public boolean isTemLimpadorTraseiro() {
        return temLimpadorTraseiro;
    }

    public void setTemLimpadorTraseiro(boolean temLimpadorTraseiro) {
        this.temLimpadorTraseiro = temLimpadorTraseiro;
    }

    public boolean isTemDesembacadorTraseiro() {
        return temDesembacadorTraseiro;
    }

    public void setTemDesembacadorTraseiro(boolean temDesembacadorTraseiro) {
        this.temDesembacadorTraseiro = temDesembacadorTraseiro;
    }

    public boolean isTemSensorEstacionamento() {
        return temSensorEstacionamento;
    }

    public void setTemSensorEstacionamento(boolean temSensorEstacionamento) {
        this.temSensorEstacionamento = temSensorEstacionamento;
    }

    public boolean isTemCambioAutomatico() {
        return temCambioAutomatico;
    }

    public void setTemCambioAutomatico(boolean temCambioAutomatico) {
        this.temCambioAutomatico = temCambioAutomatico;
    }

    public TipoDirecao getTipoDirecao() {
        return tipoDirecao;
    }

    public void setTipoDirecao(TipoDirecao tipoDirecao) {
        this.tipoDirecao = tipoDirecao;
    }

    public Integer getFinalPlaca() {
        return finalPlaca;
    }

    public void setFinalPlaca(Integer finalPlaca) {
        this.finalPlaca = finalPlaca;
    }

    public TipoCarroceria getCarroceria() {
        return carroceria;
    }

    public void setCarroceria(TipoCarroceria carroceria) {
        this.carroceria = carroceria;
    }

    public Integer getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Integer quilometragem) {
        this.quilometragem = quilometragem;
    }

    public TipoCombustivel getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(TipoCombustivel tipoCombustivel) {
        this.tipoCombustivel = tipoCombustivel;
    }

    public boolean isForSale() {
        return forSale;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getPrecoAnunciado() {
        return precoAnunciado;
    }

    public void setPrecoAnunciado(BigDecimal precoAnunciado) {
        this.precoAnunciado = precoAnunciado;
    }

    public BigDecimal getPrecoVendido() {
        return precoVendido;
    }

    public String getInformacoesAdicionais() {
        return informacoesAdicionais;
    }

    public void setInformacoesAdicionais(String informacoesAdicionais) {
        this.informacoesAdicionais = informacoesAdicionais;
    }

    public void setPrecoVendido(BigDecimal precoVendido) {
        this.precoVendido = precoVendido;
    }

    public User getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(User usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }


    // NOVOS GETTERS E SETTERS
    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
    // FIM NOVOS GETTERS E SETTERS

    public Set<VeiculoFoto> getFotos() {
        return fotos;
    }

    public void setFotos(Set<VeiculoFoto> fotos) {
        this.fotos = fotos;
    }

    // --- Opcional: toString, equals, hashCode (boas práticas) ---
    @Override
    public String toString() {
        return "Veiculo{" +
                "id=" + id +
                ", tipoVeiculo=" + (tipoVeiculo != null ? tipoVeiculo.getNome() : "null") +
                ", montadora=" + (montadora != null ? montadora.getNome() : "null") +
                ", modelo=" + (modelo != null ? modelo.getNome() : "null") +
                ", anoModelo=" + anoModelo +
                ", anoFabricacao=" + anoFabricacao +
                ", cor='" + cor + '\'' +
                ", motor='" + motor + '\'' +
                ", temArCondicionado=" + temArCondicionado +
                ", temVidroEletrico=" + temVidroEletrico +
                ", temTravasEletricas=" + temTravasEletricas +

                ", temFreioAbs=" + temFreioAbs +
                ", temAirBag=" + temAirBag +
                ", temCentralMultimidia=" + temCentralMultimidia +
                ", temComandosVolante=" + temComandosVolante +
                ", temAlarme=" + temAlarme +
                ", temLimpadorTraseiro=" + temLimpadorTraseiro +
                ", temDesembacadorTraseiro=" + temDesembacadorTraseiro +
                ", temCameraRe=" + temCameraRe +
                ", temSensorEstacionamento=" + temSensorEstacionamento +
                ", temCambioAutomatico=" + temCambioAutomatico +

                ", tipoDirecao=" + tipoDirecao +
                ", finalPlaca=" + finalPlaca +
                ", carroceria=" + carroceria +
                ", quilometragem=" + quilometragem +
                ", tipoCombustivel=" + tipoCombustivel +
                ", pais=" + pais +
                ", estado=" + estado +
                ", cidade=" + cidade +
                ", forSale=" + forSale +
                ", dataVenda=" + dataVenda +
                ", precoAnunciado=" + precoAnunciado +
                ", precoVendido=" + precoVendido +
                ", informacoesAdicionais=" + informacoesAdicionais +
                ", usuarioCadastro=" + (usuarioCadastro != null ? usuarioCadastro.getUsername() : "null") +
                '}';
    }
}