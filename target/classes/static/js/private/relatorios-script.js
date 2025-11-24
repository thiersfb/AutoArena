// src/main/resources/static/js/private/relatorios-script.js
$(document).ready(function() {
    var ctx = document.getElementById('veiculosPorMontadoraChart').getContext('2d');

    $.ajax({
        url: '/private/relatorios/veiculos-por-montadora',
        type: 'GET',
        success: function(data) {
            var labels = Object.keys(data);
            var contagens = Object.values(data);

            var myChart = new Chart(ctx, {
                type: 'bar', // Tipo de gráfico: barra
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Número de Veículos',
                        data: contagens,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.2)',
                            'rgba(54, 162, 235, 0.2)',
                            'rgba(255, 206, 86, 0.2)',
                            'rgba(75, 192, 192, 0.2)',
                            'rgba(153, 102, 255, 0.2)',
                            'rgba(255, 159, 64, 0.2)'
                        ],
                        borderColor: [
                            'rgba(255, 99, 132, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(153, 102, 255, 1)',
                            'rgba(255, 159, 64, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                precision: 0 // Mostra apenas números inteiros no eixo Y
                            }
                        }
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Erro ao buscar dados do relatório: " + error);
        }
    });
});