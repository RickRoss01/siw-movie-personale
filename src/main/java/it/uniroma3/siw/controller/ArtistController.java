package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Controller
public class ArtistController {
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private MovieController movieController;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}
	
	@PostMapping("/admin/artist")
	public String newArtist(@ModelAttribute("artist") Artist artist, Model model) {
		if (!artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname())) {
			this.artistRepository.save(artist); 
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			model.addAttribute("messaggioErrore", "Questo artista esiste già");
			return "admin/formNewArtist.html"; 
		}
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Pageable pageable = PageRequest.of(0, 3);
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		model.addAttribute("topDirectorMovies", this.artistRepository.findTopDirectorMoviesOrderByRatingDesc(pageable,id));
		model.addAttribute("topActorMovies", this.artistRepository.findTopActorMoviesOrderByRatingDesc(pageable,id));
		return "artist.html";
	}

	@GetMapping("/artists")
	public String getArtists(Model model) {
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistRepository.findAll());
		return "artists.html";
	}
	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
}
