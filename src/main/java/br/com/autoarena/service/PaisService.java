package br.com.autoarena.service;

import br.com.autoarena.model.Estado;
import br.com.autoarena.model.Pais;
import br.com.autoarena.repository.EstadoRepository;
import br.com.autoarena.repository.PaisRepository;
import br.com.autoarena.util.EstadoSpecifications;
import br.com.autoarena.util.PaisSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaisService {

    private final PaisRepository paisRepository;
    private final EstadoRepository estadoRepository;

    @Autowired // Injeção de dependência via construtor (recomendado)
    public PaisService(PaisRepository paisRepository, EstadoRepository estadoRepository) {
        this.paisRepository = paisRepository;
        this.estadoRepository = estadoRepository;
    }

    @Transactional(readOnly = true) // Adicione findById para a lógica de edição/atualização
    public Optional<Pais> findById(Long id) {
        return paisRepository.findById(id);
    }

    // Implementação do método findAll()
    @Transactional(readOnly = true) // Otimiza a transação para apenas leitura
    public List<Pais> findAll() {
        return paisRepository.findAll();
    }

    public Pais salvarPais(Pais pais) throws Exception {

        if (pais.getId() == null) { // Se for um novo registro, verifica nome
            if (paisRepository.findByNome(pais.getNome()).isPresent()) {
                //throw new Exception("País " + pais.getNome() + " já cadastrado.");
                throw new IllegalArgumentException("País " + pais.getNome() + " já cadastrado.");
            }
        } else { // Se for uma atualização, verifica se o nome atualizado não colide com outro existente (exceto ele mesmo)
            Optional<Pais> existingPaisWithSameName = paisRepository.findByNome(pais.getNome());
            if (existingPaisWithSameName.isPresent() && !existingPaisWithSameName.get().getId().equals(pais.getId())) {
                throw new IllegalArgumentException("Já existe outro país cadastrado com o nome '" + pais.getNome() + "'.");
            }
        }

        //Salvar o novo pais
        return  paisRepository.save(pais);
    }


    // NOVO MÉTODO PARA EXCLUIR
    @Transactional // Garante que a operação de exclusão seja transacional
    public void deleteById(Long id) {
        if (!paisRepository.existsById(id)) {
            throw new IllegalArgumentException("País com ID " + id + " não encontrado para exclusão.");
        }

        //Validação: Verificar se existem estados associados a este país
        if (estadoRepository.existsByPaisId(id)) {
            //throw new PaisComEstadosAssociadosException("Não é possível excluir o país pois existem estados associados a ele.");
            throw new IllegalArgumentException("Não é possível excluir o país pois existem estados associados a ele.");
        }
        paisRepository.deleteById(id);
    }

    public Page<Pais> findAllFiltered(String nome, Pageable pageable) {
        Specification<Pais> specification = Specification.where(null);

        if (nome != null && !nome.trim().isEmpty()) {
            specification = specification.and(PaisSpecifications.hasNome(nome));
        }

        return paisRepository.findAll(specification, pageable);
    }

}
