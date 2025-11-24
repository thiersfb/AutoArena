package br.com.autoarena.service;

import br.com.autoarena.model.Cidade;
import br.com.autoarena.model.Estado;
import br.com.autoarena.repository.CidadeRepository;
import br.com.autoarena.repository.EstadoRepository;
import br.com.autoarena.util.CidadeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CidadeService {

    private final CidadeRepository cidadeRepository;
    private final EstadoRepository estadoRepository; // Para buscar o Estado associado

    @Autowired
    public CidadeService(CidadeRepository cidadeRepository, EstadoRepository estadoRepository) {
        this.cidadeRepository = cidadeRepository;
        this.estadoRepository = estadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Cidade> findAll() {
        return cidadeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cidade> findById(Long id) {
        return cidadeRepository.findById(id);
    }

    @Transactional
    public Cidade saveOrUpdate(Cidade cidade) {
        // Validações
        if (cidade.getEstado() == null || cidade.getEstado().getId() == null) {
            throw new IllegalArgumentException("Uma cidade deve estar associada a um estado.");
        }

        // Verifica se o Estado associado realmente existe no banco de dados
        Estado estadoExistente = estadoRepository.findById(cidade.getEstado().getId())
                .orElseThrow(() -> new IllegalArgumentException("Estado com ID " + cidade.getEstado().getId() + " não encontrado."));
        cidade.setEstado(estadoExistente); // Garante que a entidade Estado gerenciada seja usada

        // Validação de nome único por estado
        if (cidade.getId() == null) { // Novo
            if (cidadeRepository.existsByNomeAndEstadoId(cidade.getNome(), cidade.getEstado().getId())) {
                throw new IllegalArgumentException("Já existe uma cidade com o nome '" + cidade.getNome() + "' neste estado.");
            }
        } else { // Atualização
            Optional<Cidade> existingCidade = cidadeRepository.findById(cidade.getId());
            if (existingCidade.isPresent()) {
                Cidade originalCidade = existingCidade.get();
                // Verifica se o nome foi alterado E se colide com outra cidade no MESMO estado
                if (!originalCidade.getNome().equalsIgnoreCase(cidade.getNome()) &&
                        cidadeRepository.existsByNomeAndEstadoId(cidade.getNome(), cidade.getEstado().getId())) {
                    throw new IllegalArgumentException("Já existe outra cidade com o nome '" + cidade.getNome() + "' neste estado.");
                }
            } else {
                throw new IllegalArgumentException("Cidade com ID " + cidade.getId() + " não encontrada para atualização.");
            }
        }

        return cidadeRepository.save(cidade);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!cidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Cidade com ID " + id + " não encontrada para exclusão.");
        }
        cidadeRepository.deleteById(id);
    }


    // NOVO MÉTODO: Para buscar cidades por ID do estado
    @Transactional(readOnly = true)
    public List<Cidade> findByEstadoId(Long estadoId) {
        // Opcional: Verificar se o paisId existe antes de buscar
        if (!estadoRepository.existsById(estadoId)) {
            throw new IllegalArgumentException("Estado com ID " + estadoId + " não encontrado.");
        }
        return cidadeRepository.findByEstadoId(estadoId);
    }

    public Page<Cidade> findAllFiltered(String nome, Long paisId, Long estadoId, Pageable pageable) {
        Specification<Cidade> specification = Specification.where(null);

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(CidadeSpecifications.hasNome(nome));
        }

        if (paisId != null) {
            specification = specification.and(CidadeSpecifications.hasPais(paisId));
        }

        if (estadoId != null) {
            specification = specification.and(CidadeSpecifications.hasEstado(estadoId));
        }

        return cidadeRepository.findAll(specification, pageable);
    }

}