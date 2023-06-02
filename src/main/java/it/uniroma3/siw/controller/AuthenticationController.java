package it.uniroma3.siw.controller;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class AuthenticationController {
	
	@Autowired
	private CredentialsService credentialsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
	private MovieController movieController;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}
	
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	
		
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
    
        if (principal instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
            //String username = oauth2User.getAuthorities().stream();
            
            Map<String, Object> attributes = oauth2User.getAttributes();
    
            // Ottenere il valore della stringa "name" dalla mappa di attributi
            String username = (String) attributes.get("email");
            String name = (String) attributes.get("given_name");
            String surname = (String) attributes.get("family_name");
            // Controlla se l'utente è già registrato nel tuo database
            Credentials credentials = credentialsService.getCredentials(username);
            if (credentials == null) {
                // Utente non registrato, esegui la registrazione
                // Esempio di registrazione di un nuovo utente con i dati forniti da OAuth2
                credentials = new Credentials();
                User user = new User();
                user.setName(name);
                user.setSurname(surname);
                user.setEmail(username);
                credentials.setUser(user);
                credentials.setUsername(username);
                credentials.setPassword(""); // Setta una password vuota o generata casualmente
                credentials.setRole(Credentials.DEFAULT_ROLE);
    
                // Salva le credenziali nel tuo database
                userRepository.save(user);
                credentialsService.saveCredentials(credentials);
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(principal, credentials, oauth2User.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                SecurityContextHolder.getContext().getAuthentication();
            }else{
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(principal, credentials, oauth2User.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }
        
            
            // Puoi fare ulteriori operazioni con le autorizzazioni dell'utente OAuth2
            Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();
            // ...
    
            // Restituisci la pagina desiderata in base al ruolo dell'utente
            
        }else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	    Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	    if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "admin/indexAdmin.html";
            
        }
       
        
    }
    return movieController.index(model);
}

    @PostMapping(value = { "/register" })
    public String registerUser(@Valid @ModelAttribute("user") User user,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {

        // se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
        if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "registrationSuccessful";
        }
        return "registerUser";
    }
}
