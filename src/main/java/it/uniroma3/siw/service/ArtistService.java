package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.management.Query;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

/**
 * The UserService handles logic for Users.
 */
@Service
public class ArtistService {

    @Autowired
    protected ArtistRepository artistRepository;

    @Transactional
    public void deleteArtist(Long artistId){
        
        artistRepository.deleteStarredAndDirectedMovies(artistId);
        artistRepository.deleteById(artistId);
    }

    @Transactional
    public void updateNameAndSurname(String newName, String newSurname, Artist artist) {
        if(!newName.isBlank())
            artist.setName(newName);
        if(!newSurname.isBlank()) 
            artist.setSurname(newSurname);
    }

    @Transactional
    public String updateDateOfBirth(String newDateOfBirthString, Artist artist) {
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);
        LocalDate now = LocalDate.now();
        if(newDateOfBirth.isAfter(now))
            return "La data di nascita non puo' essere nel futuro";
        
        artist.setDateOfBirth(newDateOfBirth);
        return "Operazione completata";
    }

    @Transactional
    public String updateDateOfDeath(String newDateOfDeathString, Artist artist) {
        LocalDate newDateOfDeath;
        try{
            newDateOfDeath = LocalDate.parse(newDateOfDeathString);
        }catch (DateTimeParseException s){
            artist.setDateOfDeath(null);
            return "Operazione completata";
        }
        
        LocalDate now = LocalDate.now();
        if(newDateOfDeath.isAfter(now))
            return "La data di morte non puo' essere nel futuro";
        
        artist.setDateOfDeath(newDateOfDeath);
        return "Operazione completata";
    }

    @Transactional
    public String updateBio(String newBio, Artist artist) {
        
        artist.setBiography(newBio);
        return "Operazione completata";
    }
}
