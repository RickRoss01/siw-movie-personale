package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;

@Controller
public class ArtistController {
	
	@Autowired
	private ArtistService artistService;


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
		
		if(this.artistService.newArtist(artist,bindingResult,file)){
			model.addAttribute("artist", artist);
			TimeUnit.SECONDS.sleep(1);
			return formUpdateArtist(artist.getId(), model);
		}else {
			
			return "admin/formNewArtist.html"; 
		}
	}

	@PostMapping("/updateArtistNameAndSurname")
	public String updateArtistNameAndSurname(@RequestParam("newName") String newName, @RequestParam("newSurname") String newSurname,@RequestParam("artistId") Long artistId, Model model){
		this.artistService.updateNameAndSurname(newName, newSurname, artistId);
		model.addAttribute("operation", "Operazione effettuata");
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistDateOfBirth")
	public String updateArtistDateOfBirth(@RequestParam("newDateOfBirth") String newDateOfBirth, @RequestParam("artistId") Long artistId, Model model){
		
		model.addAttribute("operation", this.artistService.updateDateOfBirth(newDateOfBirth,artistId));
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistDateOfDeath")
	public String updateArtistDateOfDeath(@RequestParam("newDateOfDeath") String newDateOfDeath, @RequestParam("artistId") Long artistId, Model model){
		model.addAttribute("operation", this.artistService.updateDateOfDeath(newDateOfDeath,artistId));
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistBio")
	public String updateArtistBio(@RequestParam("newBio") String newBio, @RequestParam("artistId") Long artistId, Model model){
		model.addAttribute("operation", this.artistService.updateBio(newBio,artistId));
		return formUpdateArtist(artistId, model);
	}

	@PostMapping("/updateArtistImage")
	public String updateArtistImage(@RequestParam("newImage") MultipartFile file, @RequestParam("artistId") Long artistId, Model model) throws IOException, InterruptedException{
		model.addAttribute("operation", this.artistService.updateImage(file,artistId));
		TimeUnit.SECONDS.sleep(1);
		return formUpdateArtist(artistId, model);
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		
		model.addAttribute("artist", this.artistService.findById(id));
		model.addAttribute("topDirectorMovies", this.artistService.findTop3DirectorMoviesOrderByRatingDesc(id));
		model.addAttribute("topActorMovies", this.artistService.findTop3ActorMoviesOrderByRatingDesc(id));
		return "artist.html";
	}

	@GetMapping("/artists")
	public String getArtists(Model model) {
		
		model.addAttribute("page", 1);
		model.addAttribute("pages", this.artistService.getNumberOfPages());

		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistService.getArtistsByPage(1));
		return "artists.html";
	}

	@GetMapping("/artists/{pageNumber}")
	public String getArtistsByPage(@PathVariable("pageNumber") Integer pageNumber, Model model) {
		
		
		model.addAttribute("pages", this.artistService.getNumberOfPages());
    	model.addAttribute("page", pageNumber);
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistService.getArtistsByPage(pageNumber));
		return "artists.html";
	}
	

	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) {


		model.addAttribute("page", 1);
		model.addAttribute("pages", this.artistService.getNumberOfPages());

		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistService.getArtistsByPage(1));
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
		
		model.addAttribute("pages", this.artistService.getNumberOfPages());
    	model.addAttribute("page", pageNumber);
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artists", this.artistService.getArtistsByPage(pageNumber));
		return "admin/manageArtists.html";
	}

	@GetMapping(value="/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("isAdmin",isAdmin());
		model.addAttribute("artist", artistService.findById(id));
		model.addAttribute("topDirectorMovies", this.artistService.findTop3DirectorMoviesOrderByRatingDesc(id));
		model.addAttribute("topActorMovies", this.artistService.findTop3ActorMoviesOrderByRatingDesc(id));

		return "admin/formUpdateArtist.html";
	}
	

	public boolean isAdmin() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
	}
}
