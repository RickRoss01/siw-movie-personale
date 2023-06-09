package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;

/**
 * The UserService handles logic for Users.
 */
@Service
public class ArtistService {

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
	private StorageService service;

    @Autowired
    private ArtistValidator artistValidator;

    @Transactional
    public void deleteArtist(Long artistId){
        
        artistRepository.deleteStarredAndDirectedMovies(artistId);
        artistRepository.deleteById(artistId);
    }

    @Transactional
    public void updateNameAndSurname(String newName, String newSurname, Long artistId) {
        Artist artist = this.artistRepository.findById(artistId).get();
        if(newName.length() !=0)
            artist.setName(newName);
        if(newSurname.length() != 0) 
            artist.setSurname(newSurname);
    }

    @Transactional
    public String updateDateOfBirth(String newDateOfBirthString, Long artistId) {
        Optional<Artist> artist = this.artistRepository.findById(artistId);
        if (artist != null){
            LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);
            LocalDate now = LocalDate.now();
            if(newDateOfBirth.isAfter(now))
                return "La data di nascita non puo' essere nel futuro";
        
            artist.get().setDateOfBirth(newDateOfBirth);
            return "Operazione completata";
        }
        return "Operazione non riuscita";
        
    }

    @Transactional
    public String updateDateOfDeath(String newDateOfDeathString, Long artistId) {
        Optional<Artist> artist = this.artistRepository.findById(artistId);
        LocalDate newDateOfDeath;
        if(artist != null){
            try{
                newDateOfDeath = LocalDate.parse(newDateOfDeathString);
            }catch (DateTimeParseException s){
                artist.get().setDateOfDeath(null);
                return "Operazione completata";
            }
            LocalDate now = LocalDate.now();
            if(newDateOfDeath.isAfter(now))
                return "La data di morte non puo' essere nel futuro";
            
            artist.get().setDateOfDeath(newDateOfDeath);
            return "Operazione completata";
        }
        return "Operazione non riuscita";
        
    }

    @Transactional
    public String updateBio(String newBio, Long artistId) {
        
        Optional<Artist> artist = this.artistRepository.findById(artistId);
		if(artist != null){
			artist.get().setBiography(newBio);
			return "Operazione Riuscita!";
		}
        return "Artista non trovato";
    }

    public Iterable<Artist> findAllArtists(){
        return this.artistRepository.findAll();
    }

    public List<Artist> findActorsNotInMovie(Long movieId) {
        return (List<Artist>) this.artistRepository.findActorsNotInMovie(movieId);
    }

    @Transactional
    public boolean newArtist(@Valid Artist artist, BindingResult bindingResult, MultipartFile file) throws IOException {
        this.artistValidator.validate(artist, bindingResult);
        if (!bindingResult.hasErrors()) {
			if(!file.isEmpty()){
				Image image = service.uploadImageToFileSystem(file);
				artist.setImage(image);
			}
			this.artistRepository.save(artist);
			return true;
		}
        return false;
    }

    public Artist findById(Long id) {
        Optional<Artist> artist = this.artistRepository.findById(id);
        if(artist != null)
            return artist.get();
        else
            return null;
    }

    public List<Movie> findTop3DirectorMoviesOrderByRatingDesc(Long id) {
        Pageable pageable = PageRequest.of(0, 3);
        return this.artistRepository.findTopDirectorMoviesOrderByRatingDesc(pageable, id);
    }

    public List<Movie> findTop3ActorMoviesOrderByRatingDesc(Long id) {
        Pageable pageable = PageRequest.of(0, 3);
        return this.artistRepository.findTopActorMoviesOrderByRatingDesc(pageable, id);
    }
    public Integer getNumberOfPages() {
        return (int) Math.ceil((double)this.artistRepository.countTotalArtists()/6);
    }

    public List<Artist> getArtistsByPage(int page){
        Pageable pageable = PageRequest.of(page-1, 6);
        return this.artistRepository.findAllArtists(pageable);

    }
    
}
    
