//VeiculoController bkp
package br.com.autoarena.controller;

import br.com.autoarena.enums.Cor;
import br.com.autoarena.enums.TipoCarroceria;
import br.com.autoarena.enums.TipoCombustivel;
import br.com.autoarena.enums.TipoDirecao;
import br.com.autoarena.model.*;
import br.com.autoarena.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
//@RequestMapping("/private/cadastro/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private MontadoraService montadoraService;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private TipoVeiculoService tipoVeiculoService;

    @Autowired
    private UserService userService; // Para buscar o usuário logado

    @Autowired
    private PaisService paisService;

    @Autowired
    private EstadoService estadoService;

    @Autowired
    private CidadeService cidadeService;


    /*
    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_VEICULO_VENDA')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping
    public String showVeiculosPage(
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "id", required = false) Long id) {

        model.addAttribute("pageTitle", "Cadastro de Veículos à Venda");
        model.addAttribute("blockTitle", "Lista de Veículos");
        model.addAttribute("blockSubtitle", "Todos os registros");

        // Adiciona um novo Veiculo ao modelo, ou busca um existente para edição
        Veiculo veiculo = new Veiculo();
        if (id != null) {
            Optional<Veiculo> veiculoExistente = veiculoService.findById(id);
            if (veiculoExistente.isPresent()) {
                veiculo = veiculoExistente.get();
                // Passa os IDs das entidades relacionadas para repopular os selects no frontend em caso de edição
                model.addAttribute("initialMontadoraId", veiculo.getMontadora() != null ? veiculo.getMontadora().getId() : null);
                model.addAttribute("initialModeloId", veiculo.getModelo() != null ? veiculo.getModelo().getId() : null);
                model.addAttribute("initialTipoVeiculoId", veiculo.getTipoVeiculo() != null ? veiculo.getTipoVeiculo().getId() : null);
                model.addAttribute("initialForSale", veiculo.isForSale());

                model.addAttribute("initialPaisId", veiculo.getPais());
                model.addAttribute("initialEstadoId", veiculo.getEstado());
                model.addAttribute("initialCidadeId", veiculo.getCidade());

                // Passa o ID do veículo para o formulário no frontend (usado para edição)
                model.addAttribute("veiculoIdForEdit", veiculo.getId());
            } else {
                // Se o ID não for encontrado, pode adicionar uma mensagem de erro ou redirecionar
                // Por simplicidade, continuará com um novo objeto Veiculo
                model.addAttribute("messageType", "error");
                model.addAttribute("message", "Veículo com ID " + id + " não encontrado.");
            }
        }
        model.addAttribute("veiculo", veiculo);


        // Adiciona listas para os selects
        List<Montadora> montadoras = montadoraService.findAllEnabled();
        model.addAttribute("montadoras", montadoras);

        List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        model.addAttribute("tiposVeiculo", tiposVeiculo);

        List<Pais> paises = paisService.findAll();
        model.addAttribute("paises", paises);


        List<Estado> estados = estadoService.findAll();
        model.addAttribute("estados", estados);

        List<Cidade> cidades = cidadeService.findAll();
        model.addAttribute("cidades", cidades);


        // Adiciona as enums para os selects (Direção, Carroceria, Combustível)
        model.addAttribute("tiposDirecao", Arrays.asList(TipoDirecao.values()));
        model.addAttribute("tiposCarroceria", Arrays.asList(TipoCarroceria.values()));
        model.addAttribute("tiposCombustivel", Arrays.asList(TipoCombustivel.values()));
        model.addAttribute("cores", Arrays.asList(Cor.values()));

        // Adiciona a lista de veículos para exibição na tabela
        // Se for admin, mostra todos. Se for vendedor, mostra só os dele.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            List<Veiculo> veiculos;
            if (isAdmin) {
                //veiculos = veiculoService.findAll(); // Administrador vê todos os veículos
                veiculos = veiculoService.findAllForSale(); // Administrador vê todos os veículos
            } else {
                String username = authentication.getName();
                User currentUser = userService.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado no sistema: " + username));
                //veiculos = veiculoService.findByUser(currentUser); // Vendedor vê APENAS seus veículos cadastrados (todos, forSale=true/false)
                veiculos = veiculoService.listarVeiculosAVendaDoUsuario(currentUser); // Vendedor vê APENAS seus veículos cadastrados (todos, forSale=true/false)
            }
            model.addAttribute("veiculos", veiculos);
        } else {
            model.addAttribute("veiculos", List.of()); // Caso não esteja logado ou seja um principal String
        }


        model.addAttribute("currentUri", request.getRequestURI());
        //return "veiculos"; // Nome do template Thymeleaf
        return "private/cadastro/veiculos";
    }
    */


    @PreAuthorize("hasAnyRole('ADMIN', 'CADASTRAR_VEICULO_VENDA')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    //@GetMapping
    @GetMapping("/private/cadastro/veiculos")
    public String showPage(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "tipoVeiculoId", required = false) Long tipoVeiculoId,
            @RequestParam(value = "montadoraId", required = false) Long montadoraId,
            @RequestParam(value = "modeloId", required = false) Long modeloId,
            //@RequestParam(value = "placa", required = false) String placa,
            //@RequestParam(value = "forSale", required = false) Boolean forSale, // CORRIGIDO: StatusVeiculo -> Boolean
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {


        model.addAttribute("pageTitle", "Cadastro de Veículos à Venda");
        model.addAttribute("blockTitle", "Lista de Veículos");
        model.addAttribute("blockSubtitle", "Todos os registros");


        /*
        Sort sortObj = Sort.by("id").descending();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sortObj = Sort.by(direction, property);
            }
        }

        // Adiciona a lista de veículos para exibição na tabela
        // Se for admin, mostra todos. Se for vendedor, mostra só os dele.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<br.com.autoarena.model.Veiculo> veiculos = List.of(); // Usamos o nome completo da classe para evitar conflitos de importação
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (isAdmin) {
                //veiculos = veiculoService.findAllFilteredAndSorted(tipoVeiculoId, montadoraId, modeloId, placa, forSale, sortObj);
                veiculos = veiculoService.findAllFilteredAndSorted(tipoVeiculoId, montadoraId, modeloId, sortObj);
            } else {
                String username = authentication.getName();
                User currentUser = userService.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado no sistema: " + username));

                //veiculos = veiculoService.findAllFilteredAndSortedByUser(currentUser, tipoVeiculoId, montadoraId, modeloId, placa , forSale, sortObj);
                veiculos = veiculoService.findAllFilteredAndSortedByUser(currentUser, tipoVeiculoId, montadoraId, modeloId, sortObj);
            }
        }

        model.addAttribute("veiculos", veiculos);
        */

        /*
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Sort sortObj = Sort.by(direction, property);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        */


        Sort sortObj = Sort.by("id").descending();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sortObj = Sort.by(direction, property);
            }
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Veiculo> veiculosPage;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (isAdmin) {
                //veiculosPage = veiculoService.findAllFilteredAndSorted(id, tipoVeiculoId, montadoraId, modeloId, placa, pageable);
                veiculosPage = veiculoService.findAllFilteredAndSorted(id, tipoVeiculoId, montadoraId, modeloId, true, pageable);

            } else {
                String username = authentication.getName();
                User currentUser = userService.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado no sistema: " + username));

                //veiculosPage = veiculoService.findAllFilteredAndSortedByUser(currentUser, id, tipoVeiculoId, montadoraId, modeloId, placa, pageable);
                veiculosPage = veiculoService.findAllFilteredAndSortedByUser(currentUser, id, tipoVeiculoId, montadoraId, modeloId, true, pageable);
            }
        } else {
            veiculosPage = Page.empty(pageable);
        }
        model.addAttribute("veiculos", veiculosPage.getContent());

        model.addAttribute("page", veiculosPage);
        model.addAttribute("totalPages", veiculosPage.getTotalPages());


        //// Busca os veículos filtrados e ordenados
        //model.addAttribute("veiculos", veiculoService.findAllFilteredAndSorted(tipoVeiculoId, montadoraId, modeloId, /* placa,*/ forSale, sortObj));
        //model.addAttribute("veiculos", veiculoService.findAllFilteredAndSorted(tipoVeiculoId, montadoraId, modeloId, sortObj));

        // Busca as listas de tipo de veículo e montadora
        //List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        //model.addAttribute("tiposVeiculo", tiposVeiculo);
        //List<Montadora> montadoras = montadoraService.findAllEnabled(); // CORRIGIDO: Agora a lista é buscada
        //model.addAttribute("montadoras", montadoras);


        // Adiciona listas para os selects
        List<Montadora> montadoras = montadoraService.findAllEnabled();
        model.addAttribute("montadoras", montadoras);

        List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        model.addAttribute("tiposVeiculo", tiposVeiculo);

        List<Pais> paises = paisService.findAll();
        model.addAttribute("paises", paises);

        List<Estado> estados = estadoService.findAll();
        model.addAttribute("estados", estados);

        List<Cidade> cidades = cidadeService.findAll();
        model.addAttribute("cidades", cidades);


        // Adiciona as enums para os selects (Direção, Carroceria, Combustível)
        model.addAttribute("tiposDirecao", Arrays.asList(TipoDirecao.values()));
        model.addAttribute("tiposCarroceria", Arrays.asList(TipoCarroceria.values()));
        model.addAttribute("tiposCombustivel", Arrays.asList(TipoCombustivel.values()));
        model.addAttribute("cores", Arrays.asList(Cor.values()));



        // Adiciona a lista de modelos para o filtro
        if (montadoraId != null) {
            Optional<Montadora> montadoraOptional = montadoras.stream()
                    .filter(m -> m.getId().equals(montadoraId))
                    .findFirst();
            if (montadoraOptional.isPresent()) {
                model.addAttribute("modelosFiltrar", modeloService.findByMontadoraAndEnabledTrue(montadoraOptional.get()));
            } else {
                model.addAttribute("modelosFiltrar", List.of());
            }
        } else {
            // Se nenhuma montadora estiver selecionada, busca todos os modelos ativos para o dropdown inicial
            //model.addAttribute("modelosFiltrar", modeloService.findAll().stream().filter(Modelo::getEnabled).collect(Collectors.toList()));
            model.addAttribute("modelosFiltrar", modeloService.findAll().stream().filter(Modelo::isEnabled).collect(Collectors.toList()));

        }

        //gambiarra: validar se precisa mesmo
        if (!model.containsAttribute("veiculo")) {
            model.addAttribute("veiculo", new Veiculo());
        }

        // Adiciona os valores do filtro ao modelo para manter os campos do formulário preenchidos
        model.addAttribute("filtroId", id);
        model.addAttribute("filtroTipoVeiculoId", tipoVeiculoId);
        model.addAttribute("filtroMontadoraId", montadoraId);
        model.addAttribute("filtroModeloId", modeloId);
        //model.addAttribute("filtroPlaca", placa);
        //model.addAttribute("filtroForSale", forSale); // CORRIGIDO: Status -> forSale
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        model.addAttribute("currentUri", request.getRequestURI());
        //return "private/cadastro/veiculos";
        return "private/cadastro/veiculos";
    }

    @PostMapping("/private/cadastro/veiculos/save")
    public String saveVeiculo(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("tipoVeiculoId") Long tipoVeiculoId,
            @RequestParam("montadoraId") Long montadoraId,
            @RequestParam("modeloId") Long modeloId,
            @RequestParam("anoModelo") Integer anoModelo,
            @RequestParam("anoFabricacao") Integer anoFabricacao,
            //@RequestParam("cor") String cor,
            @RequestParam("cor") Cor cor,
            @RequestParam("motor") String motor,
            @RequestParam(value = "temArCondicionado", defaultValue = "false") boolean temArCondicionado,
            @RequestParam(value = "temVidroEletrico", defaultValue = "false") boolean temVidroEletrico,
            @RequestParam(value = "temTravasEletricas", defaultValue = "false") boolean temTravasEletricas,

            @RequestParam(value = "temFreioAbs", defaultValue = "false") boolean temFreioAbs,
            @RequestParam(value = "temAirBag", defaultValue = "false") boolean temAirbag,
            @RequestParam(value = "temCentralMultimidia", defaultValue = "false") boolean temCentralMultimidia,
            @RequestParam(value = "temComandosVolante", defaultValue = "false") boolean temComandosVolante,
            @RequestParam(value = "temAlarme", defaultValue = "false") boolean temAlarme,
            @RequestParam(value = "temLimpadorTraseiro", defaultValue = "false") boolean temLimpadorTraseiro,
            @RequestParam(value = "temDesembacadorTraseiro", defaultValue = "false") boolean temDesembacadorTraseiro,
            @RequestParam(value = "temCameraRe", defaultValue = "false") boolean temCameraRe,
            @RequestParam(value = "temSensorEstacionamento", defaultValue = "false") boolean temSensorEstacionamento,
            @RequestParam(value = "temCambioAutomatico", defaultValue = "false") boolean temCambioAutomatico,

            @RequestParam("tipoDirecao") TipoDirecao tipoDirecao,
            @RequestParam("finalPlaca") Integer finalPlaca,
            @RequestParam("carroceria") TipoCarroceria carroceria,
            @RequestParam("quilometragem") Integer quilometragem,
            @RequestParam("tipoCombustivel") TipoCombustivel tipoCombustivel,

            @RequestParam("paisId") Long paisId,
            @RequestParam("estadoId") Long estadoId,
            @RequestParam("cidadeId") Long cidadeId,


            @RequestParam(value = "forSale", defaultValue = "true") boolean forSale,
            @RequestParam(value = "dataVenda", required = false) LocalDate dataVenda,
            @RequestParam("precoAnunciado") BigDecimal precoAnunciado,
            @RequestParam("informacoesAdicionais") String informacoesAdicionais,
            @RequestParam(value = "precoVendido", required = false) BigDecimal precoVendido,
            RedirectAttributes redirectAttributes) {

        try {
            Veiculo veiculo = new Veiculo();
            if (id != null) {
                veiculo = veiculoService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado para edição."));
            }

            // Atribui as entidades relacionadas (Montadora, Modelo, TipoVeiculo)
            veiculo.setTipoVeiculo(tipoVeiculoService.findById(tipoVeiculoId)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de Veículo não encontrado.")));
            veiculo.setMontadora(montadoraService.findById(montadoraId)
                    .orElseThrow(() -> new IllegalArgumentException("Montadora não encontrada.")));
            veiculo.setModelo(modeloService.findById(modeloId)
                    .orElseThrow(() -> new IllegalArgumentException("Modelo não encontrado.")));


            // Atribui as entidades relacionadas (pais, estado, cidade)
            veiculo.setPais(paisService.findById(paisId)
                    .orElseThrow(() -> new IllegalArgumentException("País não encontrado.")));
            veiculo.setEstado(estadoService.findById(estadoId)
                    .orElseThrow(() -> new IllegalArgumentException("Estado não encontrado.")));
            veiculo.setCidade(cidadeService.findById(cidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Cidade não encontrada.")));


            // Atribui o usuário logado ao veículo
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado."));
            veiculo.setUsuarioCadastro(currentUser);

            veiculo.setAnoModelo(anoModelo);
            veiculo.setAnoFabricacao(anoFabricacao);

            //veiculo.setCor(cor);
            veiculo.setMotor(motor);
            veiculo.setTemArCondicionado(temArCondicionado);
            veiculo.setTemVidroEletrico(temVidroEletrico);
            veiculo.setTemTravasEletricas(temTravasEletricas);

            veiculo.setTemFreioAbs(temFreioAbs);
            veiculo.setTemAirBag(temAirbag);
            veiculo.setTemCentralMultimidia(temCentralMultimidia);
            veiculo.setTemComandosVolante(temComandosVolante);
            veiculo.setTemAlarme(temAlarme);
            veiculo.setTemLimpadorTraseiro(temLimpadorTraseiro);
            veiculo.setTemDesembacadorTraseiro(temDesembacadorTraseiro);
            veiculo.setTemCameraRe(temCameraRe);
            veiculo.setTemSensorEstacionamento(temSensorEstacionamento);
            veiculo.setTemCambioAutomatico(temCambioAutomatico);

            veiculo.setTipoDirecao(tipoDirecao);
            veiculo.setFinalPlaca(finalPlaca);
            veiculo.setCarroceria(carroceria);
            veiculo.setQuilometragem(quilometragem);
            veiculo.setTipoCombustivel(tipoCombustivel);
            veiculo.setCor(cor);
            veiculo.setForSale(forSale);
            veiculo.setDataVenda(dataVenda);
            veiculo.setPrecoAnunciado(precoAnunciado);
            veiculo.setPrecoVendido(precoVendido);
            veiculo.setInformacoesAdicionais(informacoesAdicionais);

            veiculoService.save(veiculo);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Veículo salvo com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro de validação: " + e.getMessage());
            // Repopula os dados para o formulário em caso de erro
            redirectAttributes.addFlashAttribute("veiculo", new Veiculo()); // Objeto limpo ou com dados do erro?
            // Para repopular o form em erro, é melhor passar os parâmetros recebidos
            redirectAttributes.addFlashAttribute("oldId", id);
            redirectAttributes.addFlashAttribute("oldTipoVeiculoId", tipoVeiculoId);
            redirectAttributes.addFlashAttribute("oldMontadoraId", montadoraId);
            redirectAttributes.addFlashAttribute("oldModeloId", modeloId);
            redirectAttributes.addFlashAttribute("oldAnoModelo", anoModelo);
            redirectAttributes.addFlashAttribute("oldAnoFabricacao", anoFabricacao);
            redirectAttributes.addFlashAttribute("oldCor", cor);
            redirectAttributes.addFlashAttribute("oldMotor", motor);
            redirectAttributes.addFlashAttribute("oldTemArCondicionado", temArCondicionado);
            redirectAttributes.addFlashAttribute("oldTemVidroEletrico", temVidroEletrico);
            redirectAttributes.addFlashAttribute("oldTemTravasEletricas", temTravasEletricas);

            redirectAttributes.addFlashAttribute("oldTemFreiosAbs", temFreioAbs);
            redirectAttributes.addFlashAttribute("oldTemAirBag", temAirbag);
            redirectAttributes.addFlashAttribute("oldTemCentralMultimidia", temCentralMultimidia);
            redirectAttributes.addFlashAttribute("oldTemComandosVolante", temComandosVolante);
            redirectAttributes.addFlashAttribute("oldTemAlarme", temAlarme);
            redirectAttributes.addFlashAttribute("oldTemLimpadorTraseiro", temLimpadorTraseiro);
            redirectAttributes.addFlashAttribute("oldTemDesembacadorTraseiro", temDesembacadorTraseiro);
            redirectAttributes.addFlashAttribute("oldTemCameraRe", temCameraRe);
            redirectAttributes.addFlashAttribute("oldTemSensorEstacionamento", temSensorEstacionamento);
            redirectAttributes.addFlashAttribute("oldTemCambioAutomatico", temCambioAutomatico);

            redirectAttributes.addFlashAttribute("oldTipoDirecao", tipoDirecao);
            redirectAttributes.addFlashAttribute("oldFinalPlaca", finalPlaca);
            redirectAttributes.addFlashAttribute("oldCarroceria", carroceria);
            redirectAttributes.addFlashAttribute("oldQuilometragem", quilometragem);
            redirectAttributes.addFlashAttribute("oldTipoCombustivel", tipoCombustivel);

            redirectAttributes.addFlashAttribute("oldPaisId", paisId);
            redirectAttributes.addFlashAttribute("oldEstadoId", estadoId);
            redirectAttributes.addFlashAttribute("oldCidadeId", cidadeId);

            redirectAttributes.addFlashAttribute("oldForSale", forSale);
            redirectAttributes.addFlashAttribute("oldDataVenda", dataVenda);
            redirectAttributes.addFlashAttribute("oldPrecoAnunciado", precoAnunciado);
            redirectAttributes.addFlashAttribute("oldInformacoesAdicionais", informacoesAdicionais);
            redirectAttributes.addFlashAttribute("oldPrecoVendido", precoVendido);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao salvar veículo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/veiculos";
    }


    // Método para alterar o status de venda (forSale)
    //@PostMapping("/alterar-status")
    @PostMapping("/private/cadastro/veiculos/alterar-status")
    public String changeVeiculoForSaleStatus(
            @RequestParam("id") Long id,
            @RequestParam("forSale") boolean forSale,

            @RequestParam(value = "dataVenda", required = false) LocalDate dataVenda,
            //@RequestParam("precoAnunciado") BigDecimal precoAnunciado,
            @RequestParam(value = "precoVendido", required = false) BigDecimal precoVendido,
            RedirectAttributes redirectAttributes) {

        try {
            Veiculo updatedVeiculo = veiculoService.updateForSaleStatus(id, forSale, dataVenda, precoVendido);
            String statusMsg = forSale ? "colocado à venda" : "marcado como vendido";
            redirectAttributes.addFlashAttribute("messageType", "success");
            //redirectAttributes.addFlashAttribute("message", "Veículo 'Cor: " + updatedVeiculo.getCor() + ", Modelo: " + updatedVeiculo.getModelo().getNome() + "' foi " + statusMsg + " com sucesso!");
            redirectAttributes.addFlashAttribute("message", "Veículo 'ID: " + updatedVeiculo.getId() + ", Montadora:" + updatedVeiculo.getMontadora().getNome() + ", Modelo: " + updatedVeiculo.getModelo().getNome() + "' foi " + statusMsg + " com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao alterar status de venda do veículo: " + e.getMessage());
        }

        //return "redirect:/private/cadastro/veiculos";

        // Lógica de redirecionamento condicional
        if (forSale) {
            //return "redirect:/private/relatorios";
            return "redirect:/private/vendidos";
        } else {
            return "redirect:/private/cadastro/veiculos";
        }


    }

    //@PostMapping("/excluir")
    @PostMapping("/private/cadastro/veiculos/excluir")
    public String deleteVeiculo(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            veiculoService.deleteById(id);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Veículo excluído com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("message", "Erro ao excluir veículo: " + e.getMessage());
        }

        return "redirect:/private/cadastro/veiculos";
    }


    // Endpoint para buscar modelos por montadora (já existente no seu código)
    @GetMapping("/modelos-por-montadora/{idMontadora}")
    @ResponseBody
    public List<Modelo> getModelosPorMontadora(@PathVariable Long idMontadora) {
        //return modeloService.buscarPorMontadora(idMontadora);
        return modeloService.findByMontadora(idMontadora);
    }

    // **VERIFIQUE ESTE MÉTODO E O PATH**
    //@GetMapping("/estados-por-pais/{idPais}") // **IMPORTANTE: Path precisa ser "/estados-por-pais/{idPais}"**
    @GetMapping("/private/cadastro/veiculos/estados-por-pais/{idPais}") // **IMPORTANTE: Path precisa ser "/estados-por-pais/{idPais}"**
    @ResponseBody // **IMPORTANTE: Para retornar JSON/lista diretamente**
    public List<Estado> getEstadosPorPais(@PathVariable Long idPais) {
        //return estadoService.buscarPorPais(idPais);
        return estadoService.findByPaisId(idPais);
    }

    // **VERIFIQUE ESTE MÉTODO E O PATH**
    //@GetMapping("/cidades-por-estado/{idEstado}") // **IMPORTANTE: Path precisa ser "/cidades-por-estado/{idEstado}"**
    @GetMapping("/private/cadastro/veiculos/cidades-por-estado/{idEstado}") // **IMPORTANTE: Path precisa ser "/cidades-por-estado/{idEstado}"**
    @ResponseBody // **IMPORTANTE: Para retornar JSON/lista diretamente**
    public List<Cidade> getCidadesPorEstado(@PathVariable Long idEstado) {
        //return cidadeService.buscarPorEstado(idEstado);
        return cidadeService.findByEstadoId(idEstado);
    }


    /********* ABAIXO SE REFERE À PÁGINA DE VEÍCULOS VENDIDOS *********/

    //@GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAnyRole('ADMIN', 'VEICULOS_VENDIDOS')") // APENAS ESTAS ROLES PODEM ACESSAR ESTA PÁGINA
    @GetMapping("/private/vendidos")
    public String showVeiculosVendidosPage(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "tipoVeiculoId", required = false) Long tipoVeiculoId,
            @RequestParam(value = "montadoraId", required = false) Long montadoraId,
            @RequestParam(value = "modeloId", required = false) Long modeloId,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            Model model) {

        model.addAttribute("pageTitle", "Venda de Veículos");
        model.addAttribute("blockTitle", "Lista de Veículos Vendidos");
        //model.addAttribute("blockSubtitle", "Todos os registros");

        //List<Veiculo> veiculos = veiculoService.findAllSold();
        //model.addAttribute("veiculos", veiculos);

        model.addAttribute("currentUri", request.getRequestURI());



        Sort sortObj = Sort.by("id").descending();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String property = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sortObj = Sort.by(direction, property);
            }
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Veiculo> veiculosPage;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (isAdmin) {
                veiculosPage = veiculoService.findAllFilteredAndSorted(id, tipoVeiculoId, montadoraId, modeloId, false, pageable);

            } else {
                String username = authentication.getName();
                User currentUser = userService.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado no sistema: " + username));

                veiculosPage = veiculoService.findAllFilteredAndSortedByUser(currentUser, id, tipoVeiculoId, montadoraId, modeloId, false, pageable);
            }
        } else {
            veiculosPage = Page.empty(pageable);
        }
        model.addAttribute("veiculos", veiculosPage.getContent());

        model.addAttribute("page", veiculosPage);
        model.addAttribute("totalPages", veiculosPage.getTotalPages());



        // Adiciona listas para os selects
        List<Montadora> montadoras = montadoraService.findAllEnabled();
        model.addAttribute("montadoras", montadoras);

        List<TipoVeiculo> tiposVeiculo = tipoVeiculoService.findAllEnabled();
        model.addAttribute("tiposVeiculo", tiposVeiculo);

        // Adiciona a lista de modelos para o filtro
        if (montadoraId != null) {
            Optional<Montadora> montadoraOptional = montadoras.stream()
                    .filter(m -> m.getId().equals(montadoraId))
                    .findFirst();
            if (montadoraOptional.isPresent()) {
                model.addAttribute("modelosFiltrar", modeloService.findByMontadoraAndEnabledTrue(montadoraOptional.get()));
            } else {
                model.addAttribute("modelosFiltrar", List.of());
            }
        } else {
            // Se nenhuma montadora estiver selecionada, busca todos os modelos ativos para o dropdown inicial
            //model.addAttribute("modelosFiltrar", modeloService.findAll().stream().filter(Modelo::getEnabled).collect(Collectors.toList()));
            model.addAttribute("modelosFiltrar", modeloService.findAll().stream().filter(Modelo::isEnabled).collect(Collectors.toList()));

        }

        //gambiarra: validar se precisa mesmo
        if (!model.containsAttribute("veiculo")) {
            model.addAttribute("veiculo", new Veiculo());
        }

        // Adiciona os valores do filtro ao modelo para manter os campos do formulário preenchidos
        model.addAttribute("filtroId", id);
        model.addAttribute("filtroTipoVeiculoId", tipoVeiculoId);
        model.addAttribute("filtroMontadoraId", montadoraId);
        model.addAttribute("filtroModeloId", modeloId);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);

        model.addAttribute("currentUri", request.getRequestURI());


        //return "private/cadastro/veiculos";
        return "private/vendidos";

    }

}