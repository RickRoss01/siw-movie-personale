package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {

	public List<Movie> findByYear(int year);

	public List<Movie> findByTitle(String title);

	public boolean existsByTitleAndYear(String title, int year);
	
	@Query("SELECT m FROM Movie m JOIN FETCH m.primaryImage ORDER BY m.rating DESC")
	List<Movie> findTop3MoviesOrderByRatingDesc(Pageable pageable);

	@Query("SELECT m FROM Movie m JOIN FETCH m.primaryImage ORDER BY m.rating DESC")
	List<Movie> findAllMovies(Pageable pageable);

	@Query("SELECT COUNT(m) FROM Movie m")
    Long countTotalMovies();

    

}