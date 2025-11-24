package br.com.autoarena.service;

import br.com.autoarena.model.Modelo;
import br.com.autoarena.model.Montadora;
import br.com.autoarena.model.User;
import br.com.autoarena.model.Veiculo;
import br.com.autoarena.repository.ModeloRepository;
import br.com.autoarena.repository.MontadoraRepository;
import br.com.autoarena.repository.VeiculoRepository;
import br.com.autoarena.util.VeiculoSpecifications;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private MontadoraRepository montadoraRepository;

    @Autowired
    private ModeloRepository modeloRepository;
    @Transactional(readOnly = true)
    public List<Veiculo> findAll() {
        return veiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Veiculo> findAllForSale() {
        return veiculoRepository.findByForSaleTrue();
    }


    // Novo método que utiliza paginação
    public Page<Veiculo> findByForSaleTrue(Pageable pageable) {
        return veiculoRepository.findByForSaleTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<Veiculo> findAllSold() {
        return veiculoRepository.findByForSaleFalse();
    }


    @Transactional(readOnly = true)
    public List<Veiculo> findAllForSaleById(Long idForSale) {
        return veiculoRepository.findByForSaleTrue();
    }


    @Transactional(readOnly = true)
    public Optional<Veiculo> findById(Long id) {
        return veiculoRepository.findById(id);
    }

    /**
    // Método para buscar veículos por usuário de cadastro (TODOS, não apenas os "à venda")
    @Transactional(readOnly = true)
    public List<Veiculo> findByUserRegisteredVehicles(User user) {
        if (user == null) {
            return List.of();
        }
        return veiculoRepository.findByUsuarioCadastro(user);
    }
     * Retorna todos os veículos cadastrados por um usuário específico,
     * independentemente do seu status de venda (à venda ou vendido).
     * Requer o método findByUsuarioCadastro(User user) no VeiculoRepository.
     * @param user O objeto User para o qual os veículos serão buscados.
     * @return Uma lista de veículos associados ao usuário, ou uma lista vazia se o usuário for nulo.
     */
    @Transactional(readOnly = true)
    public List<Veiculo> findByUser(User user) {
        if (user == null) {
            return List.of();
        }
        // Alterado para buscar TODOS os veículos do usuário, não apenas os 'à venda'
        return veiculoRepository.findByUsuarioCadastro(user);
    }


    @Transactional(readOnly = true)
    public List<Veiculo> listarVeiculosAVendaDoUsuario(User user) {
        if (user == null) {
            return List.of();
        }
        // Alterado para buscar TODOS os veículos do usuário à venda
        return veiculoRepository.findByForSaleTrueAndUsuarioCadastro(user);
        //return veiculoRepository.findByForSaleTrueAndUsuarioCadastro(user); // <<< AQUI!

    }



    // NOVO MÉTODO DE FILTRO E ORDENAÇÃO
    //public List<Veiculo> findAllFilteredAndSorted(Long id, Long tipoVeiculoId, Long montadoraId, Long modeloId, /* String placa, Boolean forSale, */ Pageable pageable, Sort sort) {
    public Page<Veiculo> findAllFilteredAndSorted(Long id, Long tipoVeiculoId, Long montadoraId, Long modeloId, Boolean forSale, Pageable pageable) {

        //Specification<Veiculo> specification = Specification.where(null);
        Specification<Veiculo> specification = buildSpecification(id, tipoVeiculoId, montadoraId, modeloId /*, placa */,  forSale);
                //.and(VeiculoSpecifications.isForSale(true));        // Adiciona a condição forSale=true para usuários


        //specification = specification.and(VeiculoSpecifications.isForSale(true));
        specification = specification.and(VeiculoSpecifications.isForSale(forSale));

        if (tipoVeiculoId != null) {
            specification = specification.and(VeiculoSpecifications.hasTipoVeiculo(tipoVeiculoId));
        }
        if (montadoraId != null) {
            specification = specification.and(VeiculoSpecifications.hasMontadora(montadoraId));
        }
        if (modeloId != null) {
            specification = specification.and(VeiculoSpecifications.hasModelo(modeloId));
        }
        /*
        if (placa != null && !placa.trim().isEmpty()) {
            specification = specification.and(VeiculoSpecifications.hasPlaca(placa));
        }

        if (forSale != null) {
            specification = specification.and(VeiculoSpecifications.isForSale(forSale)); // CORRIGIDO: usa o novo Specification
        }
        */

        //return veiculoRepository.findAll(specification, sort);
        return veiculoRepository.findAll(specification, pageable);


    }


    // NOVO MÉTODO: para usuários (filtra apenas os veículos do usuário)
    //public List<Veiculo> findAllFilteredAndSortedByUser(User user, Long tipoVeiculoId, Long montadoraId, Long modeloId, /* String placa, Boolean forSale, */ Sort sort) {
    public Page<Veiculo> findAllFilteredAndSortedByUser(User user, Long id, Long tipoVeiculoId, Long montadoraId, Long modeloId, Boolean forSale, Pageable pageable) {

        Specification<Veiculo> specification = buildSpecification(id, tipoVeiculoId, montadoraId, modeloId /*, placa */, forSale)
                .and(VeiculoSpecifications.isCadastradoPor(user));  // Adiciona o filtro de usuário
                //.and(VeiculoSpecifications.isForSale(true));        // Adiciona a condição forSale=true para usuários

        //return veiculoRepository.findAll(specification, sort);
        return veiculoRepository.findAll(specification, pageable);

    }

    // Método privado para construir a Specification base (evita duplicação de código)
    //private Specification<Veiculo> buildSpecification(Long tipoVeiculoId, Long montadoraId, Long modeloId /*, String placa, Boolean forSale*/) {
    private Specification<Veiculo> buildSpecification(Long id, Long tipoVeiculoId, Long montadoraId, Long modeloId, Boolean forSale) {
        Specification<Veiculo> specification = Specification.where(null);

        // Prioridade 1: Filtro por ID (se presente, ignora os outros)
        if (id != null) {
            return VeiculoSpecifications.hasId(id);
        }


        if (tipoVeiculoId != null) {
            specification = specification.and(VeiculoSpecifications.hasTipoVeiculo(tipoVeiculoId));
        }
        if (montadoraId != null) {
            specification = specification.and(VeiculoSpecifications.hasMontadora(montadoraId));
        }
        if (modeloId != null) {
            specification = specification.and(VeiculoSpecifications.hasModelo(modeloId));
        }
        /*
        if (placa != null && !placa.trim().isEmpty()) {
            specification = specification.and(VeiculoSpecifications.hasPlaca(placa));
        }
        */

        if (forSale != null) {
            specification = specification.and(VeiculoSpecifications.isForSale(forSale));
        }


        return specification;
    }

    @Transactional
    public Veiculo save(Veiculo veiculo) {
        if (veiculo.getTipoVeiculo() == null || veiculo.getTipoVeiculo().getId() == null) {
            throw new IllegalArgumentException("O veículo deve ter um tipo de veículo associado.");
        }
        if (veiculo.getMontadora() == null || veiculo.getMontadora().getId() == null) {
            throw new IllegalArgumentException("O veículo deve ter uma montadora associada.");
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().getId() == null) {
            throw new IllegalArgumentException("O veículo deve ter um modelo associado.");
        }
        if (veiculo.getUsuarioCadastro() == null || veiculo.getUsuarioCadastro().getId() == null) {
            throw new IllegalArgumentException("O veículo deve ter um usuário de cadastro associado.");
        }

        // Validação de regras de negócio antes de salvar
        if (veiculo.getPrecoAnunciado() == null || veiculo.getPrecoAnunciado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço anunciado deve ser maior que zero.");
        }

        if (!veiculo.isForSale() && veiculo.getDataVenda() == null) {
            throw new IllegalArgumentException("A data de venda é obrigatória se o veículo não estiver mais 'À Venda'.");
        }

        if (veiculo.isForSale() && veiculo.getDataVenda() != null) {
            // Se o veículo está à venda, não deveria ter data de venda preenchida.
            // Isso pode ser um aviso ou erro, dependendo da sua regra de negócio.
            // Por simplicidade, vamos limpar a data de venda se for marcado como "À Venda".
            veiculo.setDataVenda(null);
            veiculo.setPrecoVendido(null); // Limpa preço vendido também
        }


        // Se o preço vendido for preenchido, o carro não pode estar forSale
        if (veiculo.getPrecoVendido() != null && veiculo.isForSale()) {
            throw new IllegalArgumentException("O veículo não pode estar 'À Venda' se um 'Preço Vendido' for informado.");
        }

        // Se o veículo não estiver para venda, mas o preço vendido não foi preenchido, gere um erro
        if (!veiculo.isForSale() && veiculo.getPrecoVendido() == null) {
            throw new IllegalArgumentException("O 'Preço Vendido' é obrigatório se o veículo não estiver mais 'À Venda'.");
        }

        // Se a data de venda estiver no futuro
        if (veiculo.getDataVenda() != null && veiculo.getDataVenda().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A 'Data da Venda' não pode ser uma data futura.");
        }

        return veiculoRepository.save(veiculo);
    }

    @Transactional
    public void deleteById(Long id) {
        veiculoRepository.deleteById(id);
    }

    @Transactional
    public Veiculo updateForSaleStatus(Long id, boolean forSale, LocalDate dataVenda, BigDecimal precoVendido) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado com ID: " + id));
        veiculo.setForSale(forSale);

        // Lógica adicional para dataVenda e precoVendido ao alterar status via botão rápido
        if (forSale) { // Se está voltando para "À Venda"
            veiculo.setDataVenda(null); // Limpa a data de venda
            veiculo.setPrecoVendido(null); // Limpa o preço vendido
        } else { // Se está marcando como "Vendido" via botão rápido
            // Se a data de venda e o preço vendido não forem preenchidos por outros meios,
            // você precisaria de um formulário adicional ou uma abordagem diferente para coletar essas informações.
            // Para simplicidade, aqui assumimos que se o status for mudado para "vendido"
            // via botão, o usuário precisará editar o veículo para informar data/preço.
            // Ou você pode adicionar um valor padrão (ex: data atual, preco vendido = preco anunciado)
            // se a regra de negócio permitir. No momento, o save() já valida isso.
            veiculo.setDataVenda(dataVenda);
            veiculo.setPrecoVendido(precoVendido);
        }

        return veiculoRepository.save(veiculo);
    }


    //Pagina Publica - Filtrar Busca de veiculos
    public Page<Veiculo> findByFiltros(Pageable pageable, Long brandId, Long modelId, String color) {
        System.out.println("Filtros recebidos: brandId=" + brandId + ", modelId=" + modelId + ", color=" + color);

        Specification<Veiculo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Sempre filtra por forSale = true
            predicates.add(criteriaBuilder.isTrue(root.get("forSale")));

            // Filtro por marca
            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("montadora").get("id"), brandId));
            }

            // Filtro por modelo
            if (modelId != null) {
                predicates.add(criteriaBuilder.equal(root.get("modelo").get("id"), modelId));
            }

            // Filtro por cor
            if (color != null && !color.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("cor"), color));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return veiculoRepository.findAll(spec, pageable);
    }

    public List<Montadora> findAllMontadoras() {
        return montadoraRepository.findAll();
    }

    public List<Modelo> findAllModelos() {
        return modeloRepository.findAll();
    }

    public List<String> findAllCores() {
        return veiculoRepository.findDistinctCoresByForSaleTrue();
    }



    /*********************************** RELATORIOS ***********************************/
    public List<Object[]> countVeiculosByMontadora() {
        return veiculoRepository.countVeiculosByMontadora();
    }

}