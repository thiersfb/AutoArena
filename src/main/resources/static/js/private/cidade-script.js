// src/main/resources/static/js/private/cidade-script.js

$(document).ready(function() {
    var selectPais = $('#selectPais');
    var selectEstado = $('#selectEstado');
    var txtNome = $('#txtNome');
    var cidadeId = $('#cidadeId');
    var submitButton = $('#submitButton');
    var cancelButton = $('#cancelButton');

    //fitro de pais -> recebe o valor através do id do componente
    var filtroPaisId = $('#filtroPaisId');
    var filtroEstadoId = $('#filtroEstadoId');


    // Função para carregar estados via AJAX
    function loadEstados(paisId, targetSelect, callback) {
        targetSelect.empty().append('<option value="">-- Selecione um Estado --</option>');
        if (paisId) {
            $.ajax({
                url: '/api/estados-por-pais/' + paisId,
                type: 'GET',
                success: function(estados) {
                    $.each(estados, function(index, estado) {
                        targetSelect.append('<option value="' + estado.id + '">' + estado.nome + ' (' + estado.uf + ')' + '</option>');
                    });
                    if (callback) callback();
                }
            });
        }
    }

    // Função para carregar estados via AJAX
    function loadEstadosFiltro(paisId, targetSelect) {
        targetSelect.empty().append('<option value="">Todos</option>');

        // Verifica se um país foi selecionado
        if (paisId) {
            // Se sim, busca os estados relacionados ao país
            $.ajax({
                url: '/api/estados-por-pais/' + paisId,
                type: 'GET',
                success: function(estados) {
                    $.each(estados, function(index, estado) {
                        targetSelect.append('<option value="' + estado.id + '">' + estado.nome + ' (' + estado.uf + ')' + '</option>');
                    });
                }
            });
        } else {
            // Se 'Todos' for selecionado, busca todos os estados
            $.ajax({
                url: '/api/estados/todos', // NOVO ENDPOINT
                type: 'GET',
                success: function(estados) {
                    $.each(estados, function(index, estado) {
                        targetSelect.append('<option value="' + estado.id + '">' + estado.nome + ' (' + estado.uf + ')' + '</option>');
                    });
                }
            });
        }
    }

    // Evento para o formulário de cadastro
    selectPais.on('change', function() {
        var paisId = $(this).val();
        loadEstados(paisId, selectEstado);
    });

    // Evento para o formulário de filtro
    filtroPaisId.on('change', function() {
        var paisId = $(this).val();
        loadEstadosFiltro(paisId, filtroEstadoId);
    });


    // Lógica para o botão de edição
    $('.edit-cidade-btn').on('click', function() {
        var id = $(this).data('id');
        var nome = $(this).data('nome');
        var estadoId = $(this).data('estado-id');
        var paisId = $(this).data('pais-id');

        cidadeId.val(id);
        txtNome.val(nome);
        selectPais.val(paisId).trigger('change');

        // Aguarda os estados serem carregados antes de pré-selecionar o estado
        setTimeout(function() {
            selectEstado.val(estadoId);
        }, 300);

        submitButton.text('Atualizar');
        cancelButton.show();
    });

    cancelButton.on('click', function() {
        cidadeId.val('');
        txtNome.val('');
        selectPais.val('').trigger('change');
        submitButton.text('Salvar');
        cancelButton.hide();
    });
});