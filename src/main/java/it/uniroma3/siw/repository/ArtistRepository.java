package it.uniroma3.siw.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

	public boolean existsByNameAndSurname(String name, String surname);	

	@Query(value="select  * "
			+ "from artist a "
			+ "where a.id not in "
			+ "(select actors_id "
			+ "from movie_actors "
			+ "where movie_actors.starred_movies_id = :movieId)", nativeQuery=true)
	public Iterable<Artist> findActorsNotInMovie(@Param("movieId") Long id);

	@Query("SELECT m FROM Movie m JOIN FETCH m.director d WHERE d.id = :directorId ORDER BY m.rating DESC")
	public List<Movie> findTopDirectorMoviesOrderByRatingDesc(Pageable pageable, @Param("directorId") Long id);

	@Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId ORDER BY m.rating DESC")
	public List<Movie> findTopActorMoviesOrderByRatingDesc(Pageable pageable, @Param("actorId") Long id);

	@Query("SELECT COUNT(a) FROM Artist a")
	public Long countTotalArtists();

	@Query("SELECT a FROM Artist a")
	public List<Artist> findAllArtists(Pageable pageable);

	@Modifying
	@Query(value = "update movie set director_id = null where director_id = :artistId ; delete from movie_actors where actors_id = :artistId", nativeQuery=true)
	public void deleteStarredAndDirectedMovies(@Param("artistId") Long id);
}