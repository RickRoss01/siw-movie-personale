package it.uniroma3.siw.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class ReviewController {
	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private ReviewValidator reviewValidator;

	@Autowired
	private MovieController movieController;




	@Autowired
	private CredentialsService credentialsService;

	@GetMapping("/movie/{id}/reviews")
	public String getMovieReviews(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Movie movie= this.movieRepository.findById(id).get();
		model.addAttribute("reviews", reviewRepository.findByMovieId(id));
		model.addAttribute("movie", movie);

		return "Reviews.html";
	}

	@GetMapping("/admin/deleteReview/{reviewId}/{movieId}")
	public String manageArtists(@PathVariable("reviewId") Long reviewId , @PathVariable("movieId") Long movieId ,Model model) {
		if(reviewRepository.existsById(reviewId)){
			this.reviewRepository.deleteById(reviewId);
		}
		model.addAttribute("operation", "Recensione Cancellata");
		updateMovieRaing(this.movieRepository.findById(movieId).get());
		return getMovieReviews(movieId, model);
	}

	@GetMapping("/authenticated/writeReview/{id}")
public String writeReview(@PathVariable("id") Long id, Model model) {
    Movie movie = this.movieRepository.findById(id).get();
    Review newreview = new Review();
	
	
    
	model.addAttribute("user", getCurrentUser());
	model.addAttribute("movie", movie);
    model.addAttribute("newreview", newreview);
    return "authenticated/writeReview.html";
}

	@PostMapping("/newReview")
	public String newReview(@Valid @ModelAttribute("newreview") Review newreview,  BindingResult bindingResult, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		
		this.reviewValidator.validate(newreview, bindingResult);
		if (!bindingResult.hasErrors()) {
			Movie movie =  newreview.getMovie();
			model.addAttribute("movie",movie);
			this.reviewRepository.save(newreview); 
			updateMovieRaing(newreview.getMovie());
			return this.movieController.getMovie(movie.getId(), model);
		} else {
			model.addAttribute("movie", newreview.getMovie());
			model.addAttribute("user", getCurrentUser());
			return "authenticated/writeReview.html"; 
		}
	}

	
	private void updateMovieRaing(Movie movie) {
		Float movieRating = reviewRepository.getAvgRating(movie);
		if(movieRating != null){
			movie.setRating((float) ((Math.round(movieRating *10.0)/10.0)));
		}else
			movie.setRating(0.0f);
			
		this.movieRepository.save(movie);
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
