package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	
	private ConsumoAPI consumoAPI = new ConsumoAPI();
	
	private ConverteDados converteDados = new ConverteDados();
	
	private final String API_URL = "http://www.omdbapi.com/?t=";
	
	private final String API_KEY = "&apikey=616bb6bc";
	
	private List<DadosTemporada> temporadas = new ArrayList<>();
	
	private List<DadosEpisodio> episodiosSerie = new ArrayList<>();
	
	private List<Episodio> episodios = new ArrayList<>();
	
	public void exibeMenu() {
		
		System.out.print("Digite o nome da śerie para pesquisa: ");
		
		var nome = leitura.nextLine();
		
		nome = nome.toLowerCase().replace(" ", "+");
		
		this.consultarSerie(nome);
		
		this.colecionaEpisodios();
		
		this.estatisticas();
		
	}
	
	private void consultarSerie(String nomeSerie) {
		
		var json = consumoAPI.obterDados(API_URL + nomeSerie + API_KEY);
		var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
		System.out.println(dadosSerie);
		
		for(int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
			json = consumoAPI.obterDados(API_URL + nomeSerie + "&season="+ i + API_KEY);
			var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		
	}
	
	private void imprimeTemporadas() {
		
		temporadas.forEach(System.out::println);
		
	}
	
	private void imprimeTituloEpisodios() {
		
		temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
		
	}
	
	private void juntaEpisodiosSerie() {
		
		episodiosSerie = temporadas.stream()
			.flatMap(t -> t.episodios().stream())
			.collect(Collectors.toList());
		
	}
	
	private void top5Episodios() {
		
		this.juntaEpisodiosSerie();
		
		episodiosSerie.stream()
			.filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
			.sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
			.limit(5)
			.forEach(System.out::println);
		
	}
	
	private void colecionaEpisodios() {
		
		episodios = temporadas.stream()
				.flatMap(t -> t.episodios().stream()
						.map(de -> new Episodio(t.numero(),de))
						).collect(Collectors.toList());
				
		episodios.forEach(System.out::println);
		
	}
	
	private void filtraEpisodioPorData() {
		
		System.out.print("A partir de que ano você deseja ver os episódios?: ");
		
		var ano = leitura.nextInt();
		leitura.nextLine();
		
		LocalDate dataBusca = LocalDate.of(ano, 1, 1);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		episodios.stream()
			.filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
			.forEach(e -> System.out.println(
					"Temporada: " + e.getTemporada() +
					" Episódio: " + e.getTitulo() +
					" Data de Lançamento: " + e.getDataLancamento().format(dtf)
					));
		
	}
	
	private void buscarEpisodioTrechoTitulo() {
		
		System.out.print("Digite um trecho do título do episódio: ");
		
		var trechoTitulo = leitura.nextLine();
		
		Optional<Episodio> episodioBuscado = episodios.stream()
				.filter(e -> e.getTitulo().contains(trechoTitulo))
				.findFirst();
		
		if(episodioBuscado.isPresent()) {
			var ep = episodioBuscado.get();
			System.out.println("Episódio encontrado: " + ep.getTitulo());
			System.out.println("Temporada: " + ep.getTemporada());
		} else {
			System.out.println("Episódio não encontrado");
		}
		
	}
	
	private void avaliacoesPorTemporada() {
		
		Map<Integer, Double> avaliacoesTemporada = episodios.stream()
				.filter( e -> e.getAvalicao() > 0.0)
				.collect(Collectors.groupingBy(Episodio::getTemporada, 
						Collectors.averagingDouble(Episodio::getAvalicao)));
		
		System.out.println(avaliacoesTemporada);
		
	}
	
	private void estatisticas() {
		
		DoubleSummaryStatistics est = episodios.stream()
				.filter( e -> e.getAvalicao() > 0.0)
				.collect(Collectors.summarizingDouble(Episodio::getAvalicao));
		
		System.out.println(est);
		System.out.println("Média: " + est.getAverage());
		System.out.println("Melhor episódio: " + est.getMax());
		System.out.println("Pior episódio: " + est.getMin());
		System.out.println("Quantidade: " + est.getCount());
		
	}
	
}
