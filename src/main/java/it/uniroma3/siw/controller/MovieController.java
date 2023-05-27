package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.StorageService;

@Controller
public class MovieController {
	@Autowired 
	private MovieRepository movieRepository;

	@Autowired 
	private ReviewRepository reviewRepository;
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired 
	private CredentialsService credentialsService;
	@Autowired
	private StorageService service;
	

	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Pageable pageable = PageRequest.of(0, 3);
		model.addAttribute("movies", this.movieRepository.findTop3MoviesOrderByRatingDesc(pageable));
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}
		model.addAttribute("isAdmin",isAdmin());

        return "index.html";
	}

	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		model.addAttribute("directors",artistRepository.findAll());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		Pageable pageable = PageRequest.of(0, 6);
		int pages = (int) Math.ceil((double)this.movieRepository.countTotalMovies()/6);//Stabilisci quante pagine devo far vedere


		model.addAttribute("page", 1);
		model.addAttribute("pages", pages);
		model.addAttribute("movies", this.movieRepository.findAllMovies(pageable));
		
		return "admin/manageMovies.html";
	}
	@GetMapping("/admin/manageMovies/{pageNumber}")

	public String getToModifyMoviesByPage(@PathVariable("pageNumber") Integer pageNumber,Model model) {
		Pageable pageable = PageRequest.of((pageNumber-1), 6);
		
		int pages = (int) Math.ceil((double)this.movieRepository.countTotalMovies()/6);//Stabilisci quante pagine devo far vedere
		
		model.addAttribute("pages", pages);
    	model.addAttribute("page", pageNumber);
		model.addAttribute("movies", this.movieRepository.findAllMovies(pageable));
		
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.movieRepository.findById(movieId).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistRepository.findAll());
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/newMovie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie,BindingResult bindingResult,@RequestParam("image")MultipartFile file,@RequestParam("movieimages")MultipartFile[] files,Model model) throws IOException, InterruptedException {
		
		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			Image image = service.uploadImageToFileSystem(file);
			movie.setPrimaryImage(image);
			
			Arrays.stream(files).forEach(multipartFile -> {
				try {
					movie.addImage(service.uploadImageToFileSystem(multipartFile));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			this.movieRepository.save(movie); 
			
			model.addAttribute("movie", movie);
			model.addAttribute("images", movie.getImages());
			model.addAttribute("isAdmin",isAdmin());
			Pageable pageable = PageRequest.of(0, 3);
			model.addAttribute("reviews", this.reviewRepository.findTop3ReviewsOrderByCreatedOnDesc(movie,pageable));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(!(authentication instanceof AnonymousAuthenticationToken)) {
				UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();
				Long userid = user.getId();
			
				model.addAttribute("review",reviewRepository.findByMovieIdAndUserId(movie.getId(),userid));
			
		}
			TimeUnit.SECONDS.sleep(1);
			return "movie.html";
		} else {
			model.addAttribute("directors",artistRepository.findAll());
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Movie movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		model.addAttribute("images", movie.getImages());
		Pageable pageable = PageRequest.of(0, 3);
		model.addAttribute("reviews", this.reviewRepository.findTop3ReviewsOrderByCreatedOnDesc(movie,pageable));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();
			Long userid = user.getId();
			
			model.addAttribute("review",reviewRepository.findByMovieIdAndUserId(id,userid));
			
		}

		return "movie.html";
	}

	@GetMapping("/movies/{pageNumber}")

	public String getMoviesByPage(@PathVariable("pageNumber") Integer pageNumber,Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Pageable pageable = PageRequest.of((pageNumber-1), 6);
		
		int pages = (int) Math.ceil((double)this.movieRepository.countTotalMovies()/6);//Stabilisci quante pagine devo far vedere
		
		model.addAttribute("pages", pages);
    	model.addAttribute("page", pageNumber);
		model.addAttribute("movies", this.movieRepository.findAllMovies(pageable));
		
		return "movies.html";
	}
	@GetMapping("/movies")
	public String getMovies(Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Pageable pageable = PageRequest.of(0, 6);
		if(SecurityContextHolder.getContext().getAuthentication().getName() != "anonymousUser") {
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			model.addAttribute("user", credentials.getUser());
		}
		
		int pages = (int) Math.ceil((double)this.movieRepository.countTotalMovies()/6);//Stabilisci quante pagine devo far vedere


		model.addAttribute("page", 1);
		model.addAttribute("pages", pages);
		model.addAttribute("movies", this.movieRepository.findAllMovies(pageable));
		
		return "movies.html";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam String year) {
		model.addAttribute("isAdmin",isAdmin());
		Pageable pageable = PageRequest.of(0, 6);
		List<Movie> foundMovies = this.movieRepository.findByTitleContainingIgnoreCase(year,pageable);
		int pages = (int) Math.ceil((double)foundMovies.size()/6);//Stabilisci quante pagine devo far vedere


		model.addAttribute("page", 1);
		model.addAttribute("pages", pages);
		model.addAttribute("movies", foundMovies);
		return "foundMovies.html";
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieRepository.findById(id).get());

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieRepository.save(movie);
		
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieRepository.save(movie);

		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}
}
