package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "episodios")
public class Episodio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer temporada;
	private String titulo;
	private Integer numeroEpisodio;
	private Double avalicao;
	private LocalDate dataLancamento;
	
	@ManyToOne
	private Serie serie;

	public Episodio() {

	}

	public Episodio(Integer temporada, DadosEpisodio episodio) {

		this.temporada = temporada;
		this.titulo = episodio.titulo();
		this.numeroEpisodio = episodio.numero();

		try {
			this.avalicao = Double.valueOf(episodio.avaliacao());
		} catch (NumberFormatException ex) {
			this.avalicao = 0.0;
		}

		try {
			this.dataLancamento = LocalDate.parse(episodio.dataLancamento());
		} catch (DateTimeParseException ex) {
			this.dataLancamento = null;
		}

	}

	@Override
	public String toString() {
		return "[temporada=" + temporada + ", titulo=" + titulo + ", numeroEpisodio=" + numeroEpisodio + ", avalicao="
				+ avalicao + ", dataLancamento=" + dataLancamento + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public Integer getTemporada() {
		return temporada;
	}

	public void setTemporada(Integer temporada) {
		this.temporada = temporada;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getNumeroEpisodio() {
		return numeroEpisodio;
	}

	public void setNumeroEpisodio(Integer numeroEpisodio) {
		this.numeroEpisodio = numeroEpisodio;
	}

	public Double getAvalicao() {
		return avalicao;
	}

	public void setAvalicao(Double avalicao) {
		this.avalicao = avalicao;
	}

	public LocalDate getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(LocalDate dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

}
