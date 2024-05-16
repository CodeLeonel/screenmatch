package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	
	private ConsumoAPI consumoAPI = new ConsumoAPI();
	
	private ConverteDados converteDados = new ConverteDados();
	
	private final String API_URL = "http://www.omdbapi.com/?t=";
	
	private final String API_KEY = "&apikey=616bb6bc";
	
	public void exibeMenu() {
		
		System.out.print("Digite o nome da śerie para pesquisa: ");
		
		var nome = leitura.nextLine();
		
		nome = nome.toLowerCase().replace(" ", "+");
		
		this.consultarSerie(nome);
		
		
	}
	
	private void consultarSerie(String nomeSerie) {
		
		var json = consumoAPI.obterDados(API_URL + nomeSerie + API_KEY);
		var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
		System.out.println(dadosSerie);
		
		List<DadosTemporada> temporadas = new ArrayList<DadosTemporada>();
		
		for(int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
			json = consumoAPI.obterDados(API_URL + nomeSerie + "&season="+ i + API_KEY);
			var dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		
		temporadas.forEach(System.out::println);
		
	}
	
}
