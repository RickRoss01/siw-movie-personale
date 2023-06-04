package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class MovieController {
	
	@Autowired 
	private CredentialsService credentialsService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ArtistService artistService;
	

	@GetMapping(value = "/") 
	public String index(Model model) {
		model.addAttribute("movies", this.movieService.getTopMovies());
		if (this.movieService.getAuthentication() instanceof AnonymousAuthenticationToken) {
	        return "index.html";
		}
		model.addAttribute("isAdmin",isAdmin());

        return "index.html";
	}

	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		model.addAttribute("directors",artistService.findAllArtists());
		model.addAttribute("isAdmin", isAdmin());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("movie", this.movieService.findMovieById(id));
		
		model.addAttribute("reviews", this.reviewService.findTop3ReviewsByMovieId(id));

		model.addAttribute("review",getLoggedUserMovieReview(id, model));

		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {

		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("page", 1);
		model.addAttribute("pages", this.movieService.getNumberOfPages());
		model.addAttribute("movies", this.movieService.getMoviesByPage(1));
		
		return "admin/manageMovies.html";
	}
	@GetMapping("/admin/manageMovies/{pageNumber}")
	public String getToModifyMoviesByPage(@PathVariable("pageNumber") Integer pageNumber,Model model) {
		model.addAttribute("pages", this.movieService.getNumberOfPages());
    	model.addAttribute("page", pageNumber);
		model.addAttribute("movies", this.movieService.getMoviesByPage(pageNumber));
		model.addAttribute("isAdmin", this.isAdmin());
		
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		this.movieService.setDirectorToMovie(directorId,movieId);
		
		
		model.addAttribute("reviews", this.reviewService.findTop3ReviewsByMovieId(movieId));

		model.addAttribute("review",getLoggedUserMovieReview(movieId, model));
		model.addAttribute("movie", this.movieService.findMovieById(movieId));
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/deleteMovie/{movieId}")
	public String deleteMovie(@PathVariable("movieId") Long movieId, Model model){
		model.addAttribute("operation", this.movieService.deleteMovie(movieId));
		return manageMovies(model);
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistService.findAllArtists());
		model.addAttribute("movie", this.movieService.findMovieById(id));
		return "admin/directorsToAdd.html";
	}



	@PostMapping("/updateTitle")
	public String updateMovieTitle(@RequestParam("movieId") Long movieId, @RequestParam("newTitle") String newTitle, Model model) {
		model.addAttribute("operation", this.movieService.updateMovieTitle(movieId,newTitle));

    	return formUpdateMovie(movieId, model);
}

@PostMapping("/updateYear")
	public String updateMovieYear(@RequestParam("movieId") Long movieId, @RequestParam("newYear") Integer newYear, Model model) {
    model.addAttribute("operation", this.movieService.updateMovieYear(movieId, newYear));
   	
    
    return formUpdateMovie(movieId, model);
}



	@PostMapping("/admin/newMovie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie,BindingResult bindingResult,@RequestParam("image")MultipartFile file,@RequestParam("movieimages")MultipartFile[] files,Model model) throws IOException, InterruptedException {
		;
		if(this.movieService.newMovie(movie, bindingResult, file,files)){
			model.addAttribute("movie", movie);
			model.addAttribute("images", movie.getImages());
			model.addAttribute("isAdmin",isAdmin());
			model.addAttribute("reviews", this.reviewService.findTop3ReviewsByMovieId(movie.getId()));
			TimeUnit.SECONDS.sleep(1);
			return "admin/formUpdateMovie.html";
		}
			
		else {
			model.addAttribute("directors",artistService.findAllArtists());
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Movie movie = this.movieService.findMovieById(id);
		model.addAttribute("movie", movie);
		model.addAttribute("images", movie.getImages());
		model.addAttribute("reviews", this.reviewService.findTop3ReviewsByMovieId(id));

		model.addAttribute("review",getLoggedUserMovieReview(id, model));

		return "movie.html";
	}

	private Review getLoggedUserMovieReview(Long id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!(authentication instanceof AnonymousAuthenticationToken)) {
			if(authentication.getPrincipal() instanceof DefaultOAuth2User){
				Map<String, Object> attributes = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes();
				String username = (String) attributes.get("email");
				Credentials credentials = credentialsService.getCredentials(username);
				return reviewService.findByMovieIdAndUserId(id,credentials.getUser().getId());
			}else{
				UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();
				Long userid = user.getId();
			
				return reviewService.findByMovieIdAndUserId(id,userid);
			}
			
			
		}
		return null;
	}

	@GetMapping("/movies/{pageNumber}")

	public String getMoviesByPage(@PathVariable("pageNumber") Integer pageNumber,Model model) {
		model.addAttribute("isAdmin",isAdmin());
		
		model.addAttribute("pages", this.movieService.getNumberOfPages());
    	model.addAttribute("page", pageNumber);
		model.addAttribute("movies", this.movieService.getMoviesByPage(pageNumber));
		
		return "movies.html";
	}
	@GetMapping("/movies")
	public String getMovies(Model model) {
		model.addAttribute("isAdmin",isAdmin());


		model.addAttribute("page", 1);
		model.addAttribute("pages", this.movieService.getNumberOfPages());
		model.addAttribute("movies", this.movieService.getMoviesByPage(1));
		
		return "movies.html";
	}
	
	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam String year) {
		model.addAttribute("isAdmin",isAdmin());
		
		List<Movie> foundMovies = this.movieService.findMovieByTitle(year);
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
		model.addAttribute("movie", this.movieService.findMovieById(id));

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		this.movieService.addActorToMovie(actorId,movieId);
		
		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", this.movieService.findMovieById(movieId));
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		this.movieService.removeActorFromMovie(actorId,movieId);

		List<Artist> actorsToAdd = actorsToAdd(movieId);
		
		model.addAttribute("movie", this.movieService.findMovieById(movieId));
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
	private List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistService.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}
}
