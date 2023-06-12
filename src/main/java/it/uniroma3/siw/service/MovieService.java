package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;

/**
 * The UserService handles logic for Users.
 */
@Service
public class MovieService {

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected MovieRepository movieRepository;

    @Autowired
    private ImageRepository fileDataRepository;

    @Autowired 
	private MovieValidator movieValidator;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
	private StorageService service;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public List<Movie> getTopMovies() {
        Pageable pageable = PageRequest.of(0, 3);
        return this.movieRepository.findTop3MoviesOrderByRatingDesc(pageable);
    }

    public Integer getNumberOfPages() {
        return (int) Math.ceil((double)this.movieRepository.countTotalMovies()/6);
    }

    public List<Movie> getMoviesByPage(int page){
        Pageable pageable = PageRequest.of(page-1, 6);
        return this.movieRepository.findAllMovies(pageable);

    }

    public Movie findMovieById(Long id){
        return this.movieRepository.findById(id).get();
    }

    @Transactional
    public void setDirectorToMovie(Long directorId, Long movieId) {
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.movieRepository.findById(movieId).get();
        movie.setDirector(director);
    }

    @Transactional
    public String deleteMovie(Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
		if(movie != null){
            reviewRepository.deleteByMovieId(movieId);
			movieRepository.deleteById(movieId);
            
            return "film eliminato con successo";
        }
        return null;
        
    }

    @Transactional
    public String updateMovieTitle(Long movieId, String newTitle) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie == null) {
            return "Errore, film non trovato";
        }
	    if(newTitle.length() != 0){
		    movie.get().setTitle(newTitle);
	    }
        return "Titolo Aggiornato";
    }


    public String updateMovieYear(Long movieId, Integer newYear){
        Optional<Movie> movie = movieRepository.findById(movieId);
    if (movie == null) {
        return "Errore, film non trovato";
    }
	if(newYear != null){
		movie.get().setYear(newYear);
    	movieRepository.save(movie.get());
	}
    return "Anno Aggiornato";
    }

    @Transactional
    public String updateMovieImage(MultipartFile file, Long movieId)throws IOException {
        Movie movie = movieRepository.findById(movieId).get();
		if(!file.isEmpty()){
			Image image = service.uploadImageToFileSystem(file);
			movie.setPrimaryImage(image);
            movieRepository.save(movie);
            return "Operazione effettuata";
		}
			
			return "Nessun Operazione effettuata";
			
	}
        

    public List<Movie> findMovieByTitle(String year) {
        
        return this.movieRepository.findByTitleContainingIgnoreCase(year);
    }

    @Transactional
    public void removeActorFromMovie(Long actorId, Long movieId) {
        Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
    }

    @Transactional
    public void addActorToMovie(Long actorId, Long movieId) {
        Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
    }

    @Transactional
    public boolean newMovie(@Valid Movie movie, BindingResult bindingResult, MultipartFile file, MultipartFile[] files) throws IOException {
        this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			if(!file.isEmpty()){
				Image image = service.uploadImageToFileSystem(file);
				movie.setPrimaryImage(image);
			}
			
			
			Arrays.stream(files).forEach(multipartFile -> {
				try {
                    Image image = service.uploadImageToFileSystem(multipartFile);
                    image.setMovie(movie);
					movie.addImage(image);
                    fileDataRepository.save(image);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			movieRepository.save(movie);
			return true;
		}
        return false;
    }

    @Transactional
    public void updateMovieRaing(Movie movie) {
		Float movieRating = reviewRepository.getAvgRating(movie);
		if(movieRating != null){
			movie.setRating((float) ((Math.round(movieRating *10.0)/10.0)));
		}else
			movie.setRating(0.0f);
			
	}



   
}
