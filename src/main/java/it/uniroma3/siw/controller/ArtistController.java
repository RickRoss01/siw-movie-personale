package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.StorageService;

@Controller
public class ArtistController {
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private ArtistValidator artistValidator;

	@Autowired 
	private MovieController movieController;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private StorageService service;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		model.addAttribute("isAdmin",isAdmin());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}
	
	@PostMapping("/admin/newArtist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist,BindingResult bindingResult,@RequestParam("artistimage")MultipartFile file,Model model) throws IOException, InterruptedException {
		this.artistValidator.validate(artist, bindingResult);
		if (!bindingResult.hasErrors()) {
			if(!file.isEmpty()){
				Image image = service.uploadImageToFileSystem(file);
				artist.setImage(image);
			}
			this.artistRepository.save(artist);
			model.addAttribute("artist", artist);
			 TimeUnit.SECONDS.sleep(1);
			return getArtist(artist.getId(), model);
		}else {
			
			return "admin/formNewArtist.html"; 
		}
	}

	@PostMapping("/updateArtistNameAndSurname")
	public String updateArtistNameAndSurname(@RequestParam("newName") String newName, @RequestParam("newSurname") String newSurname,@RequestParam("artistId") Long artistId, Model model){
		Optional<Artist> artist = this.artistRepository.findById(artistId);
		if(artist == null){
			model.addAttribute("operation", "Artista non trovato");
			return manageArtists(model);
		}
		this.artistService.updateNameAndSurname(newName,newSurname,artist.get());
		model.addAttribute("operation", "Operazione effettuata");
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistDateOfBirth")
	public String updateArtistDateOfBirth(@RequestParam("newDateOfBirth") String newDateOfBirth, @RequestParam("artistId") Long artistId, Model model){
		Optional<Artist> artist = this.artistRepository.findById(artistId);
		if(artist == null){
			model.addAttribute("operation", "Artista non trovato");
			return manageArtists(model);
		}
		model.addAttribute("operation", this.artistService.updateDateOfBirth(newDateOfBirth,artist.get()));
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistDateOfDeath")
	public String updateArtistDateOfDeath(@RequestParam("newDateOfDeath") String newDateOfDeath, @RequestParam("artistId") Long artistId, Model model){
		Optional<Artist> artist = this.artistRepository.findById(artistId);
		if(artist == null){
			model.addAttribute("operation", "Artista non trovato");
			return manageArtists(model);
		}
		model.addAttribute("operation", this.artistService.updateDateOfDeath(newDateOfDeath,artist.get()));
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistBio")
	public String updateArtistBio(@RequestParam("newBio") String newBio, @RequestParam("artistId") Long artistId, Model model){
		Optional<Artist> artist = this.artistRepository.findById(artistId);
		if(artist == null){
			model.addAttribute("operation", "Artista non trovato");
			return manageArtists(model);
		}
		model.addAttribute("operation", this.artistService.updateBio(newBio,artist.get()));
		return formUpdateArtist(artistId, model);
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
		Pageable pageable = PageRequest.of(0, 6);
		int pages = (int) Math.ceil((double)this.artistRepository.countTotalArtists()/6);//Stabilisci quante pagine devo far vedere


		model.addAttribute("page", 1);
		model.addAttribute("pages", pages);

		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistRepository.findAllArtists(pageable));
		return "artists.html";
	}

	@GetMapping("/artists/{pageNumber}")
	public String getArtistsByPage(@PathVariable("pageNumber") Integer pageNumber, Model model) {
		Pageable pageable = PageRequest.of((pageNumber-1), 6);
		
		int pages = (int) Math.ceil((double)this.artistRepository.countTotalArtists()/6);//Stabilisci quante pagine devo far vedere
		
		model.addAttribute("pages", pages);
    	model.addAttribute("page", pageNumber);
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistRepository.findAllArtists(pageable));
		return "artists.html";
	}
	

	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) {
		Pageable pageable = PageRequest.of(0, 6);
		int pages = (int) Math.ceil((double)this.artistRepository.countTotalArtists()/6);//Stabilisci quante pagine devo far vedere


		model.addAttribute("page", 1);
		model.addAttribute("pages", pages);

		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistRepository.findAllArtists(pageable));
		return "admin/manageArtists.html";
	}

	@GetMapping("/admin/deleteArtist/{artistid}")
	public String manageArtists(@PathVariable("artistid") Long artistId , Model model) {
		this.artistService.deleteArtist(artistId);
		model.addAttribute("operation", "Artista Cancellato");
		return manageArtists(model);
	}

	@GetMapping("/admin/manageArtists/{pageNumber}")
	public String getManageArtistsByPage(@PathVariable("pageNumber") Integer pageNumber, Model model) {
		Pageable pageable = PageRequest.of((pageNumber-1), 6);
		
		int pages = (int) Math.ceil((double)this.artistRepository.countTotalArtists()/6);//Stabilisci quante pagine devo far vedere
		
		model.addAttribute("pages", pages);
    	model.addAttribute("page", pageNumber);
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistRepository.findAllArtists(pageable));
		return "admin/manageArtists.html";
	}

	@GetMapping(value="/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		Pageable pageable = PageRequest.of(0, 3);
		model.addAttribute("artist", artistRepository.findById(id).get());
		model.addAttribute("topDirectorMovies", this.artistRepository.findTopDirectorMoviesOrderByRatingDesc(pageable,id));
		model.addAttribute("topActorMovies", this.artistRepository.findTopActorMoviesOrderByRatingDesc(pageable,id));

		return "admin/formUpdateArtist.html";
	}
	

	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
}
