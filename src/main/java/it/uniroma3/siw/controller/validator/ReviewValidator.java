package it.uniroma3.siw.controller.validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Component
public class ReviewValidator implements Validator {
	@Autowired
	private ReviewRepository reviewRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review)o;
		if (review.getTitle()!=null && review.getReviewDescription()!=null && review.getMovie()!=null && review.getUser()!=null 
				&& reviewRepository.existsByMovieIdAndUserId(review.getMovie().getId(), review.getUser().getId() )) {
			errors.reject("review.duplicate");
		}
	}
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
}