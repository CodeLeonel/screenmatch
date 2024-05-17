package br.com.alura.screenmatch.model;

import java.util.OptionalDouble;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Serie {

	private String titulo;
	
	private Integer totalTemporadas;
	
	private Double avaliacao;
	
	private Categoria genero;
	
	private String atores;
	
	private String poster;
	
	private String sinopse;
	
	public Serie(DadosSerie ds) {
		
		this.titulo = ds.titulo();
		this.totalTemporadas = ds.totalTemporadas();
		
		this.avaliacao = OptionalDouble.of(Double.valueOf(ds.avaliacao())).orElse(0.0);
		this.genero = Categoria.fromString(ds.genero().split(",")[0].trim());
		
		this.atores = ds.atores();
		this.sinopse = ds.sinopse();
		this.poster = ds.poster();
		
	}
	
	
	
}
