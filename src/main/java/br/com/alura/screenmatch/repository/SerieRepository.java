package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;

public interface SerieRepository extends JpaRepository<Serie,Long> {

	Optional<Serie> findByTituloContainsIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

	List<Serie> findTop5ByOrderByAvaliacaoDesc();

	List<Serie> findByGeneroOrderByAvaliacaoDesc(Categoria categoria);

	List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);
	
	@Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
	List<Serie> seriesPorTemporadaEAvaliacao(int totalTemporadas, double avaliacao);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoTitulo%")
	List<Episodio> buscaSeriesPorTrechoTitulo(String trechoTitulo);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
	List<Episodio> topEpisodiosPorSerie(Serie serie);

	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :ano")
	List<Episodio> buscaEpisodiosPorSerieEAno(Serie serie, int ano);
	
	List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();
	
	@Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();
	
	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id")
	List<Episodio> buscaEpisodiosPorIdSerie(Long id);
	
	@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numero")
	List<Episodio> buscaEpisodiosPorSerieETemporada(Long id, Integer numero);

	List<Serie> findByGenero(Categoria categoria);
	
}
