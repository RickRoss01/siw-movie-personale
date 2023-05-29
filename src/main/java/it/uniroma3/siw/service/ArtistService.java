package it.uniroma3.siw.service;

import java.util.Optional;

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
    public void deleteArtist(Long artistid){
        this.artistRepository.deleteById(artistid);
    }
}
