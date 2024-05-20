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

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
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

	private SerieRepository repositorio;

	private List<Serie> series = new ArrayList<>();

	public Principal(SerieRepository repositorio) {
		this.repositorio = repositorio;
	}

	public void exibeMenu() {

		var opcao = -1;
		while (opcao != 0) {

			System.out.print("""
					1 - Buscar série
					2 - Buscar episódios
					3 - Listar séries buscadas
					4 - Buscar série por título
					5 - Buscar séries por ator
					6 - Buscar as 5 melhores séries
					7 - Buscar séries por gênero
					8 - Buscar séries para maratonar
					9 - Buscar episódios por trecho de título
					0 - Sair

					Digite a opção:""");

			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {

			case 1:
				this.consultarSerie(obterNomeSerie());
				break;
			case 2:
				exibeListaSeries();
				this.consultaEpisodiosPorSerie(obterNomeSerie());
				break;
			case 3:
				exibeListaSeries();
				break;
			case 4:
				buscaSeriePorTitulo(obterNomeSerie());
				break;
			case 5:
				buscaSeriesPorAtor();
				break;
			case 6:
				top5Series();
				break;
			case 7:
				exibeSeriesPorCategoria();
				break;
			case 8:
				exibeSeriesParaMaratonar();
				break;
			case 9:
				buscarSeriesPorTrechoTitulo();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida!");
				break;
			}
		}
	}

	private String obterNomeSerie() {
		
		System.out.print("Digite o nome da śerie para pesquisa: ");
		
		return leitura.nextLine();
	
	}
	
	private void consultarSerie(String nomeSerie) {

		nomeSerie = nomeSerie.toLowerCase().replace(" ", "+");
		
		var json = consumoAPI.obterDados(API_URL + nomeSerie + API_KEY);
		var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
		var serie = new Serie(dadosSerie);
		this.repositorio.save(serie);
		System.out.println(serie);

	}

	private void consultaEpisodiosPorSerie(String nomeSerie) {

		var serie = repositorio.findByTituloContainsIgnoreCase(nomeSerie);

		if (serie.isPresent()) {

			temporadas.clear();

			var serieEncontrada = serie.get();
			var nomeSerieEncontrada = serieEncontrada.getTitulo().toLowerCase().replace(" ", "+");

			for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
				var json = consumoAPI.obterDados(API_URL + nomeSerieEncontrada + "&season=" + i + API_KEY);
				var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}

			this.imprimeTemporadas();

			List<Episodio> episodios = temporadas.stream()
					.flatMap(dt -> dt.episodios().stream().map(de -> new Episodio(dt.numero(), de)))
					.collect(Collectors.toList());
			
			serieEncontrada.setEpisodios(episodios);
			
			repositorio.save(serieEncontrada);
			

		} else {
			System.out.println("Série não encontrada");
		}

	}
	
	private void buscaSeriePorTitulo(String obterNomeSerie) {
		
		var serieBuscada = repositorio.findByTituloContainsIgnoreCase(obterNomeSerie);
		
		if(serieBuscada.isPresent()) {
			
			System.out.println("Dados da série: " + serieBuscada.get());
			
		} else {
			
			System.out.println("Série não encontrada!");
			
		}
		
	}
	
	private void buscaSeriesPorAtor() {
		
		System.out.print("Digite um nome para a busca: ");
		
		var nomeAtor = leitura.nextLine();
		
		System.out.print("Digite uma avaliação mínima: ");
		
		var avaliacao = leitura.nextDouble();
 		leitura.nextLine();
		
		List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		
		System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
		seriesEncontradas.forEach(s -> 
				System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
		
	}

	private void top5Series() {
		
		List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
		
		seriesTop.forEach(s -> 
		System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
		
	}
	
	private void exibeSeriesPorCategoria() {
		
		System.out.print("Digite um gênero para a busca: ");
		var genero = leitura.nextLine();
		var categoria = Categoria.fromPortugues(genero);
		List<Serie> series = repositorio.findByGeneroOrderByAvaliacaoDesc(categoria);
		
		System.out.println("Lista de séries do gênero " + genero + ":");
		
		series.forEach(s -> 
		System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
		
	}
	
	private void exibeSeriesParaMaratonar() {
	
		System.out.print("Digite um número máximo de temporadas: ");
		var totalTemporadas = leitura.nextInt();
		
		System.out.print("Digite uma nota de avalição mínima: ");
		var avaliacao = leitura.nextDouble();
		leitura.nextLine();
		
		List<Serie> series = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas,avaliacao);
		
		series.forEach(s -> 
			System.out.println(s.getTitulo() 
					+ " - Total Temporadas: " + s.getTotalTemporadas() 
			+ " - Avaliação: " + s.getAvaliacao()));
		
	}
	
	private void buscarSeriesPorTrechoTitulo() {
		
		System.out.print("Digite um trecho do título do episódio: ");

		var trechoTitulo = leitura.nextLine();
		
		List<Episodio> episodios = repositorio.buscaSeriesPorTrechoTitulo(trechoTitulo);
		
		episodios.forEach(e -> 
			System.out.printf("Série: %s Temporada: %s - Episodio %s - %s\n",
					e.getSerie().getTitulo(), e.getTemporada(), 
					e.getNumeroEpisodio(), e.getTitulo()));
		
	}
	
	private void exibeListaSeries() {

		series = repositorio.findAll();

		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

	}
	
	private void buscarEpisodioTrechoTitulo() {

		System.out.print("Digite um trecho do título do episódio: ");

		var trechoTitulo = leitura.nextLine();

		Optional<Episodio> episodioBuscado = episodios.stream().filter(e -> e.getTitulo().contains(trechoTitulo))
				.findFirst();

		if (episodioBuscado.isPresent()) {
			var ep = episodioBuscado.get();
			System.out.println("Episódio encontrado: " + ep.getTitulo());
			System.out.println("Temporada: " + ep.getTemporada());
		} else {
			System.out.println("Episódio não encontrado");
		}

	}
	
	private void filtraEpisodioPorData() {

		System.out.print("A partir de que ano você deseja ver os episódios?: ");

		var ano = leitura.nextInt();
		leitura.nextLine();

		LocalDate dataBusca = LocalDate.of(ano, 1, 1);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		episodios.stream().filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
				.forEach(e -> System.out.println("Temporada: " + e.getTemporada() + " Episódio: " + e.getTitulo()
						+ " Data de Lançamento: " + e.getDataLancamento().format(dtf)));

	}

	private void imprimeTemporadas() {

		temporadas.forEach(System.out::println);

	}

	private void imprimeTituloEpisodios() {

		temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

	}

	private void juntaEpisodiosSerie() {

		episodiosSerie = temporadas.stream().flatMap(t -> t.episodios().stream()).collect(Collectors.toList());

	}

	
	
	private void top5Episodios() {

		juntaEpisodiosSerie();

		episodiosSerie.stream().filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
				.sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()).limit(5)
				.forEach(System.out::println);

	}

	private void colecionaEpisodios() {

		episodios = temporadas.stream().flatMap(t -> t.episodios().stream().map(de -> new Episodio(t.numero(), de)))
				.collect(Collectors.toList());

		episodios.forEach(System.out::println);

	}

	

	

	private void avaliacoesPorTemporada() {

		Map<Integer, Double> avaliacoesTemporada = episodios.stream().filter(e -> e.getAvalicao() > 0.0).collect(
				Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvalicao)));

		System.out.println(avaliacoesTemporada);

	}

	private void estatisticas() {

		DoubleSummaryStatistics est = episodios.stream().filter(e -> e.getAvalicao() > 0.0)
				.collect(Collectors.summarizingDouble(Episodio::getAvalicao));

		System.out.println(est);
		System.out.println("Média: " + est.getAverage());
		System.out.println("Melhor episódio: " + est.getMax());
		System.out.println("Pior episódio: " + est.getMin());
		System.out.println("Quantidade: " + est.getCount());

	}

}
