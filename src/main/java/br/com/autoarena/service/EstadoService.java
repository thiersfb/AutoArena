package br.com.autoarena.service;

import br.com.autoarena.model.Cidade;
import br.com.autoarena.model.Estado;
import br.com.autoarena.model.Pais; // Precisará da entidade Pais
import br.com.autoarena.repository.CidadeRepository;
import br.com.autoarena.repository.EstadoRepository;
import br.com.autoarena.repository.PaisRepository; // Precisará do PaisRepository
import br.com.autoarena.util.CidadeSpecifications;
import br.com.autoarena.util.EstadoSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoService {

    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository;
    private final PaisRepository paisRepository; // Para buscar o País associado

    @Autowired
    public EstadoService(CidadeRepository cidadeRepository, EstadoRepository estadoRepository, PaisRepository paisRepository) {
        this.cidadeRepository = cidadeRepository;
        this.estadoRepository = estadoRepository;
        this.paisRepository = paisRepository;
    }


    @Transactional(readOnly = true)
    public List<Estado> findAll() {
        return estadoRepository.findAll();
    }


    //public List<Estado> findAll() { return estadoRepository.findAll(); }


    @Transactional(readOnly = true)
    public Optional<Estado> findById(Long id) {
        return estadoRepository.findById(id);
    }

    @Transactional
    public Estado saveOrUpdate(Estado estado) {
        // Validações antes de salvar/atualizar
        if (estado.getPais() == null || estado.getPais().getId() == null) {
            throw new IllegalArgumentException("Um estado deve estar associado a um país.");
        }

        // Verifica se o País associado realmente existe no banco de dados
        Pais paisExistente = paisRepository.findById(estado.getPais().getId())
                .orElseThrow(() -> new IllegalArgumentException("País com ID " + estado.getPais().getId() + " não encontrado."));
        estado.setPais(paisExistente); // Garante que a entidade Pais gerenciada seja usada

        // Validações para nomes e UFs únicos por país (exemplo)
        if (estado.getId() == null) { // Novo estado
            if (estadoRepository.existsByNomeAndPaisId(estado.getNome(), estado.getPais().getId())) {
                throw new IllegalArgumentException("Já existe um estado com o nome '" + estado.getNome() + "' neste país.");
            }
            if (estadoRepository.existsByUfAndPaisId(estado.getUf(), estado.getPais().getId())) {
                throw new IllegalArgumentException("Já existe um estado com a UF '" + estado.getUf() + "' neste país.");
            }
        } else { // Atualização de estado existente
            Optional<Estado> existingEstado = estadoRepository.findById(estado.getId());
            if (existingEstado.isPresent()) {
                Estado originalEstado = existingEstado.get();
                // Verifica se o nome ou UF foi alterado e se colide com outro estado no MESMO país
                if (!originalEstado.getNome().equalsIgnoreCase(estado.getNome()) &&
                        estadoRepository.existsByNomeAndPaisId(estado.getNome(), estado.getPais().getId())) {
                    throw new IllegalArgumentException("Já existe outro estado com o nome '" + estado.getNome() + "' neste país.");
                }
                if (!originalEstado.getUf().equalsIgnoreCase(estado.getUf()) &&
                        estadoRepository.existsByUfAndPaisId(estado.getUf(), estado.getPais().getId())) {
                    throw new IllegalArgumentException("Já existe outro estado com a UF '" + estado.getUf() + "' neste país.");
                }
            } else {
                throw new IllegalArgumentException("Estado com ID " + estado.getId() + " não encontrado para atualização.");
            }
        }

        return estadoRepository.save(estado);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!estadoRepository.existsById(id)) {
            throw new IllegalArgumentException("Estado com ID " + id + " não encontrado para exclusão.");
        }
        //Validação: Verificar se existem cidades associados a este estado
        if (cidadeRepository.existsByEstadoId(id)) {
            throw new IllegalArgumentException("Não é possível excluir o estado pois existem cidades associadas a ele.");
        }
        estadoRepository.deleteById(id);
    }

    // NOVO MÉTODO: Para buscar estados por ID do país
    @Transactional(readOnly = true)
    public List<Estado> findByPaisId(Long paisId) {
        // Opcional: Verificar se o paisId existe antes de buscar
        if (!paisRepository.existsById(paisId)) {
            throw new IllegalArgumentException("País com ID " + paisId + " não encontrado.");
        }
        return estadoRepository.findByPaisId(paisId);
    }


    public Page<Estado> findAllFiltered(String nome, Long paisId, Pageable pageable) {
        Specification<Estado> specification = Specification.where(null);

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(EstadoSpecifications.hasNome(nome));
        }

        if (paisId != null) {
            specification = specification.and(EstadoSpecifications.hasPais(paisId));
        }

        return estadoRepository.findAll(specification, pageable);
    }


}