package br.com.alura.screenmatch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.service.SerieService;

@RestController
@RequestMapping("/series")
public class SerieController {
	
	@Autowired
	private SerieService servico;
	
	@GetMapping
	public List<SerieDTO> obterSeries() {
		return servico.obterTodasSeries();
	}
	
	@GetMapping("/top5")
	public List<SerieDTO> obterTop5Series() {
		return servico.obterTop5Series();
	}
	
	@GetMapping("/lancamentos")
	public List<SerieDTO> obterLancamentos() {
		return servico.obterLancamentos();
	}
	
	@GetMapping("/{id}")
	public SerieDTO obterSeriePorId(@PathVariable Long id) {
		return servico.obterSeriePorId(id);
	}
	
	@GetMapping("/{id}/temporadas/todas")
	public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id) {
		return servico.obterTodasTemporadasPorSerie(id);
	}
	
	@GetMapping("{id}/temporadas/{numero}")
	public List<EpisodioDTO> obterTemporadaPorNumero(@PathVariable Long id, @PathVariable Integer numero) {
		return servico.obterTemporadaPorNumero(id, numero);
	}
	
	@GetMapping("categoria/{genero}")
	public List<SerieDTO> obterSeriePorCategoria(@PathVariable String genero){
		
		return servico.obterSeriesPorCategoria(genero);
		
	}

}
