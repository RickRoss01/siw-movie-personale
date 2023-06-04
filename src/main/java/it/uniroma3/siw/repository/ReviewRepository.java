package it.uniroma3.siw.repository;


import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByUserAndMovie(int user, int movie);	

	public List<Review> findByMovieId(Long movieId);


	public boolean existsByMovieIdAndUserId(Long movieId, Long userId);

	public Review findByMovieIdAndUserId(Long movieId, Long userId);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie = :movie")
	Float getAvgRating(@Param("movie") Movie movie);

	@Query("SELECT r FROM Review r WHERE r.movie = :movie ORDER BY r.createdOn DESC")
    public List<Review> findTop3ReviewsOrderByCreatedOnDesc(@Param("movie") Movie movie, Pageable pageable);

    public Long deleteByMovieId(Long movieId);
}