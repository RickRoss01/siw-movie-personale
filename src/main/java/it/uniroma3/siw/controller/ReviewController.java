package it.uniroma3.siw.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class ReviewController {
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private MovieController movieController;




	@Autowired
	private CredentialsService credentialsService;

	@GetMapping("/movie/{id}/reviews")
	public String getMovieReviews(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Movie movie= this.movieService.findMovieById(id);
		model.addAttribute("reviews", reviewService.findByMovieId(id));
		model.addAttribute("movie", movie);

		return "Reviews.html";
	}

	@GetMapping("/admin/deleteReview/{reviewId}/{movieId}")
	public String manageArtists(@PathVariable("reviewId") Long reviewId , @PathVariable("movieId") Long movieId ,Model model) {
		if(reviewService.existsById(reviewId)){
			this.reviewService.deleteById(reviewId);
		}
		model.addAttribute("operation", "Recensione Cancellata");
		this.movieService.updateMovieRaing(this.movieService.findMovieById(movieId));
		return getMovieReviews(movieId, model);
	}

	@GetMapping("/authenticated/writeReview/{id}")
public String writeReview(@PathVariable("id") Long id, Model model) {
    Movie movie = this.movieService.findMovieById(id);
    Review newreview = new Review();
	
	
    
	model.addAttribute("user", getCurrentUser());
	model.addAttribute("movie", movie);
    model.addAttribute("newreview", newreview);
    return "authenticated/writeReview.html";
}

	@PostMapping("/newReview")
	public String newReview(@Valid @ModelAttribute("newreview") Review newreview,  BindingResult bindingResult, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		
		if (this.reviewService.newReview(newreview,bindingResult)) {
			Movie movie = newreview.getMovie();
			model.addAttribute("movie",movie);
			return this.movieController.getMovie(movie.getId(), model);
		} else {
			model.addAttribute("movie", newreview.getMovie());
			model.addAttribute("user", getCurrentUser());
			return "authenticated/writeReview.html"; 
		}
	}

	
	

	private User getCurrentUser() {
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof DefaultOAuth2User){
			DefaultOAuth2User oauth2User = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Map<String, Object> attributes = oauth2User.getAttributes();
    
            String username = (String) attributes.get("email");
			Credentials credentials = credentialsService.getCredentials(username);
			return credentials.getUser();
		}else{
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return(credentialsService.getCredentials(userDetails.getUsername()).getUser());
		}
		
	}

	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
}
