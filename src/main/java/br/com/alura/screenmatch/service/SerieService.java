package br.com.alura.screenmatch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

@Service
public class SerieService {

	@Autowired
	private SerieRepository repositorio;

	public List<SerieDTO> obterTodasSeries() {

		return converteDados(repositorio.findAll());

	}

	public List<SerieDTO> obterTop5Series() {

		return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
	}

	public List<SerieDTO> obterLancamentos() {

		return converteDados(repositorio.encontrarEpisodiosMaisRecentes());
	}

	public SerieDTO obterSeriePorId(Long id) {
		
		var optSerie = repositorio.findById(id);
		
		if(optSerie.isPresent()) {
			
			return converteDado(optSerie.get());
			
		}
		
		return null;
	}
	
	public List<EpisodioDTO> obterTodasTemporadasPorSerie(Long id) {
		
		return converteDadosEpisodio(repositorio.buscaEpisodiosPorIdSerie(id));
		
	}
	
	public List<EpisodioDTO> obterTemporadaPorNumero(Long id, Integer numero) {
		
		return converteDadosEpisodio(repositorio.buscaEpisodiosPorSerieETemporada(id, numero));
		
	}
	
	private List<SerieDTO> converteDados(List<Serie> lista) {

		return lista.stream().map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
				s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse())).collect(Collectors.toList());

	}
	
	private SerieDTO converteDado(Serie s) {
		
		return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
				s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
	
	}
	
	private EpisodioDTO converteDadoEpisodio(Episodio e) {
		
		return new EpisodioDTO(e.getId(),e.getTemporada(),e.getTitulo(),e.getNumeroEpisodio(),e.getAvaliacao(),e.getDataLancamento());
	
	}
	
	private List<EpisodioDTO> converteDadosEpisodio(List<Episodio> lista) {
		
		return lista.stream().map(e -> converteDadoEpisodio(e)).collect(Collectors.toList());	
	
	}

}
