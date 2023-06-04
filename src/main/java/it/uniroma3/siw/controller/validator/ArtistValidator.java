package it.uniroma3.siw.controller.validator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Component
public class ArtistValidator implements Validator {
	@Autowired
	private ArtistRepository artistRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Artist artist = (Artist)o;
		if (artist.getName()!=null && artist.getSurname()!=null && artist.getDateOfBirth() !=null
				&& artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname())) {
			errors.reject("artist.duplicate");
		}
		LocalDate now = LocalDate.now();
		if(artist.getDateOfDeath() != null && artist.getDateOfDeath().isAfter(now)){
			errors.reject("artist.futuredate");
		}
		
		if(artist.getDateOfBirth() !=null && artist.getDateOfBirth().isAfter(now)){
			errors.reject("artist.futuredate");
		}
	}
	@Override
	public boolean supports(Class<?> aClass) {
		return Artist.class.equals(aClass);
	}
}