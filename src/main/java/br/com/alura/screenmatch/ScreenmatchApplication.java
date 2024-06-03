package br.com.alura.screenmatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screenmatch.principal.Principal;
import br.com.alura.screenmatch.repository.SerieRepository;

@SpringBootApplication
public class ScreenmatchApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

}
