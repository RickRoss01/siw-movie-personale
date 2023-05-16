package it.uniroma3.siw.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByUserAndMovie(int user, int movie);	

	public List<Movie> findByMovie(Long id);


	public boolean existsByMovieIdAndUserId(Long movieId, Long userId);

	public Review findByMovieIdAndUserId(Long movieId, Long userId);
}