package br.com.autoarena.service;

import br.com.autoarena.model.Veiculo;
import br.com.autoarena.model.VeiculoFoto;
import br.com.autoarena.repository.VeiculoFotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoFotoService {

    private final VeiculoFotoRepository veiculoFotoRepository;

    @Autowired
    public VeiculoFotoService(VeiculoFotoRepository veiculoFotoRepository) {
        this.veiculoFotoRepository = veiculoFotoRepository;
    }


    public void salvarFotoDoVeiculo(Veiculo veiculo, MultipartFile foto) throws IOException {
        if (foto.isEmpty()) {
            return;
        }

        byte[] fotoBytes = foto.getBytes();
        VeiculoFoto veiculoFoto = new VeiculoFoto();
        veiculoFoto.setVeiculo(veiculo);
        veiculoFoto.setFotoBytes(fotoBytes);

        // Lógica para definir a foto principal (exemplo: a primeira foto salva)
        // Isso pode ser aprimorado para permitir que o usuário defina qual é a principal
        List<VeiculoFoto> fotosDoVeiculo = veiculoFotoRepository.findByVeiculoId(veiculo.getId());
        veiculoFoto.setFotoPrincipal(fotosDoVeiculo.isEmpty());

        veiculoFotoRepository.save(veiculoFoto);
    }

    public List<VeiculoFoto> findAll(Long veiculoId) {
        List<VeiculoFoto> fotosDoVeiculo = veiculoFotoRepository.findByVeiculoId(veiculoId);
        return fotosDoVeiculo;

        //return veiculoFotoRepository.findByVeiculoId(veiculoId);
    }

    public Optional<VeiculoFoto> findById(Long fotoId) {
        return veiculoFotoRepository.findById(fotoId);
    }

    public void excluirFoto(Long fotoId) {
        //veiculoFotoRepository.deleteById(fotoId);
        Optional<VeiculoFoto> fotoOptional = veiculoFotoRepository.findById(fotoId);

        if (fotoOptional.isPresent()) {
            VeiculoFoto fotoParaExcluir = fotoOptional.get();

            // Verificação 1: A foto a ser excluída é a principal?
            if (fotoParaExcluir.isFotoPrincipal()) {
                Long veiculoId = fotoParaExcluir.getVeiculo().getId();

                // Verificação 2: Existem outras fotos para o veículo?
                // Usa o novo método do repositório para buscar as outras fotos.
                List<VeiculoFoto> outrasFotos = veiculoFotoRepository.findByVeiculoIdAndIdNot(veiculoId, fotoId);

                // Se sim, define a primeira delas como a nova foto principal
                if (!outrasFotos.isEmpty()) {
                    VeiculoFoto novaFotoPrincipal = outrasFotos.get(0);
                    novaFotoPrincipal.setFotoPrincipal(true);
                    veiculoFotoRepository.save(novaFotoPrincipal);
                }
            }

            // Finalmente, exclui a foto
            veiculoFotoRepository.delete(fotoParaExcluir);
        } else {
            throw new IllegalArgumentException("Foto não encontrada.");
        }
    }


    @Transactional // Garante que todas as operações sejam atômicas
    public void definirFotoPrincipal(Long veiculoId, Long fotoId) {
        // Passo 1: Encontrar todas as fotos do veículo
        List<VeiculoFoto> fotosDoVeiculo = veiculoFotoRepository.findByVeiculoId(veiculoId);

        // Passo 2: Iterar e definir todas como não-principal (false)
        for (VeiculoFoto foto : fotosDoVeiculo) {
            foto.setFotoPrincipal(false);
            veiculoFotoRepository.save(foto); // Salva a alteração
        }

        // Passo 3: Encontrar a foto que foi clicada e defini-la como principal (true)
        Optional<VeiculoFoto> fotoPrincipalOptional = veiculoFotoRepository.findById(fotoId);

        if (fotoPrincipalOptional.isPresent()) {
            VeiculoFoto fotoPrincipal = fotoPrincipalOptional.get();
            fotoPrincipal.setFotoPrincipal(true);
            veiculoFotoRepository.save(fotoPrincipal); // Salva a nova foto principal
        } else {
            throw new IllegalArgumentException("Foto principal não encontrada.");
        }
    }
}