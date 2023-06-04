package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import javax.management.Query;
import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.apache.catalina.authenticator.SpnegoAuthenticator.AuthenticateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;


@Service
public class ReviewService {

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected MovieRepository movieRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected CredentialsService credentialsService;

    @Autowired
    protected ReviewValidator reviewValidator;

    @Autowired
    protected MovieService movieService;

    public List<Review> findTop3ReviewsByMovieId(Long movieId){
        Pageable pageable = PageRequest.of(0, 3);
        Movie movie = this.movieRepository.findById(movieId).get();
        return this.reviewRepository.findTop3ReviewsOrderByCreatedOnDesc(movie,pageable);
    }


    public Review getUserReviewByMovie(Long movieId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(!(authentication instanceof AnonymousAuthenticationToken) && authentication instanceof User) {
				UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User user = credentialsService.getCredentials(userDetails.getUsername()).getUser();
				Long userid = user.getId();
			
				return reviewRepository.findByMovieIdAndUserId(movieId,userid);
			
		}
            return null;
    }

    public Review findByMovieIdAndUserId (Long movieId, Long userId){
        return this.reviewRepository.findByMovieIdAndUserId(movieId, userId);
    }


    public List<Review> findByMovieId(Long id) {
        return this.reviewRepository.findByMovieId(id);
    }


    public boolean existsById(Long reviewId) {
        return this.reviewRepository.existsById(reviewId);
    }


    public void deleteById(Long reviewId) {
        this.reviewRepository.deleteById(reviewId);
    }


    public boolean newReview(@Valid Review newreview, BindingResult bindingResult) {
        this.reviewValidator.validate(newreview, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.reviewRepository.save(newreview); 
			this.movieService.updateMovieRaing(newreview.getMovie());
			return true;
		} else {
			return false;
		}
    }
   
}
