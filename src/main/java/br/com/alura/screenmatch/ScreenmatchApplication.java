package br.com.alura.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		var consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("http://www.omdbapi.com/?t=gilmore+girls&apikey=616bb6bc");
		var converteDados = new ConverteDados();
		var dadosSerie = converteDados.obterDados(json, DadosSerie.class);
		System.out.println(dadosSerie);
		json = consumoAPI.obterDados("http://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=616bb6bc");
		var dadosEpisodio = converteDados.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);
	}
	
	

}
