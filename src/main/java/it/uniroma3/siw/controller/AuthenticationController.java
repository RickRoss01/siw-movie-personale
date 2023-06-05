package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;

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
            
            String username = (String) attributes.get("email");
            String name = (String) attributes.get("given_name");
            String surname = (String) attributes.get("family_name");
            // Controlla se l'utente è già registrato
            Credentials credentials = credentialsService.getCredentials(username);
            if (credentials == null) {
                // Utente non registrato, esegui registrazione
                credentials = new Credentials();
                User user = new User();
                user.setName(name);
                user.setSurname(surname);
                user.setEmail(username);
                credentials.setUser(user);
                credentials.setUsername(username);
                credentials.setPassword(""); 
                credentials.setRole(Credentials.DEFAULT_ROLE);
    
                // Salva credenziali
                userRepository.save(user);
                credentialsService.saveCredentials(credentials);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(DEFAULT_ROLE);
                List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
                updatedAuthorities.add(authority);
                
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(principal, credentials, updatedAuthorities);
                
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            }else{
                List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
                if(credentialsService.getCredentials(username).getRole().equals(DEFAULT_ROLE)){
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(DEFAULT_ROLE);
                    updatedAuthorities.add(authority);
                }else if (credentialsService.getCredentials(username).getRole().equals(ADMIN_ROLE)){
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ADMIN_ROLE);
                    updatedAuthorities.add(authority);
                }
                
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(principal, credentials, updatedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) 
                    return "admin/indexAdmin.html";
            }
        
            
            
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
            model.addAttribute("operation", "Registrazione Completata");
            return "formLogin";
        }
        return "formRegisterUser";
    }
}
