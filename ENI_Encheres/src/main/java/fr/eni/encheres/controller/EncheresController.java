package fr.eni.encheres.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Retrait;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.service.EncheresServiceArticlesVendus;
import fr.eni.encheres.service.EncheresServiceCategorie;
import fr.eni.encheres.service.EncheresServiceUtilisateur;

@Controller
@SessionAttributes ({"utilisateurConnecte"})
public class EncheresController {
	
	EncheresServiceArticlesVendus encheresServiceArticlesVendus;
	EncheresServiceCategorie encheresServiceCategorie;
	EncheresServiceUtilisateur encheresServiceUtilisateur;

	public EncheresController(EncheresServiceArticlesVendus encheresServiceArticlesVendus, EncheresServiceCategorie encheresServiceCategorie,EncheresServiceUtilisateur encheresServiceUtilisateur) {
		this.encheresServiceArticlesVendus = encheresServiceArticlesVendus;
		this.encheresServiceCategorie = encheresServiceCategorie;
		this.encheresServiceUtilisateur=encheresServiceUtilisateur;
	}

	@GetMapping("/encheres")
	public String afficherAccueil(Model model) {
		model.addAttribute("listeArticles", encheresServiceArticlesVendus.findAllArticleVendu());
		return "index";
	}
	
	@GetMapping("/")
	public String afficherRien() {
		return "redirect:/encheres";
	}
	
	@GetMapping("/login")
	public String afficherLogin() {
		return "login";
	}
	
	@GetMapping("/inscription")
	public String afficherProfil(@ModelAttribute Utilisateur utilisateur) {
		return "profil";
	}
	
	@GetMapping("/profil")
	public String afficherProfil(@ModelAttribute("utilisateurConnecte") Utilisateur utilisateurConnecte, Principal principal , Model model) {
		System.out.println("PRINCIPAL"+principal.getName());
		model.addAttribute("utilisateur", utilisateurConnecte);
		return "profil";
	}
	
	@PostMapping("/register")
	 public String register( @ModelAttribute Utilisateur utilisateur ) {
		utilisateur.setMotDePasse( "{noop}" + utilisateur.getMotDePasse() );
		encheresServiceUtilisateur.createUtilisateur( utilisateur );
        return "redirect:/login";
    }
	
	@GetMapping("/creerarticle")
	public String creerArticle(@ModelAttribute ArticleVendu articleVendu,@ModelAttribute Retrait retrait ,Model model) {
		model.addAttribute("categories", encheresServiceCategorie.findAllCategorie());
		return "creerarticle";
	}
	
	
	
	
}
