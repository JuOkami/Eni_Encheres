package fr.eni.encheres.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Retrait;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.service.EncheresServiceArticlesVendus;
import fr.eni.encheres.service.EncheresServiceCategorie;
import fr.eni.encheres.service.EncheresServiceEncheres;
import fr.eni.encheres.service.EncheresServiceRetrait;
import fr.eni.encheres.service.EncheresServiceUtilisateur;
import jakarta.validation.Valid;

@Controller
public class EncheresController {

	EncheresServiceArticlesVendus encheresServiceArticlesVendus;
	EncheresServiceCategorie encheresServiceCategorie;
	EncheresServiceUtilisateur encheresServiceUtilisateur;
	EncheresServiceRetrait encheresServiceRetrait;
	EncheresServiceEncheres encheresServiceEncheres;
	public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/Images";

	// CONSTRUCTEUR
	public EncheresController(EncheresServiceArticlesVendus encheresServiceArticlesVendus,
			EncheresServiceCategorie encheresServiceCategorie, EncheresServiceUtilisateur encheresServiceUtilisateur,
			EncheresServiceRetrait encheresServiceRetrait, EncheresServiceEncheres encheresServiceEncheres) {
		this.encheresServiceArticlesVendus = encheresServiceArticlesVendus;
		this.encheresServiceCategorie = encheresServiceCategorie;
		this.encheresServiceUtilisateur = encheresServiceUtilisateur;
		this.encheresServiceRetrait = encheresServiceRetrait;
		this.encheresServiceEncheres = encheresServiceEncheres;
	}

	/************ AFFICHAGE ACCUEIL LISTE ARTICLE ********************/
	@GetMapping("/encheres")
	public String afficherAccueil(Model model, Principal principal) {
		model.addAttribute("logoutMessage", "Déconnecté avec succès");
		if (principal != null) {
			model.addAttribute("loginMessage", "Bonjour " + principal.getName());
		}
		model.addAttribute("listeCategorie", encheresServiceCategorie.findAllCategorie());
		model.addAttribute("listeArticles", encheresServiceArticlesVendus.findAllArticleVendu());
		return "index";
	}

	@GetMapping("/")
	public String afficherRien() {
		return "redirect:/encheres";
	}

	/************* AFFICHAGE CREATION ARTICLE ******************/
	@GetMapping("/creerarticle")
	public String creerArticle(@ModelAttribute ArticleVendu articleVendu, Principal principal, Model model) {
		
		model.addAttribute("categories", encheresServiceCategorie.findAllCategorie());
		Retrait retrait = new Retrait();
		encheresServiceRetrait.setRetraitParDefaut(retrait,
				encheresServiceUtilisateur.findUserByPseudo(principal.getName()));
		model.addAttribute("retrait", retrait);
		return "creerarticle";
	}

	/********** BOUTON VALIDATION CREATION ENCHERE ***********/
	@PostMapping("/encherir")
	public String encherir(@Valid @ModelAttribute Enchere enchere, BindingResult validationResult, Model model) {
		if(validationResult.hasFieldErrors()) {
			return("redirect:/article?id="+ enchere.getArticle().getNo_article() +"&mess=true" );
		}
		if (enchere.getMontant_enchere() < enchere.getArticle().getPrix_vente()) {
			return("redirect:/article?id="+ enchere.getArticle().getNo_article() +"&mess=true" );
		}
		encheresServiceEncheres.surencherir(enchere);
		return "redirect:/article?id=" + enchere.getArticle().getNo_article();
	}

	/*********** BOUTON VALIDATION CREATION ARTICLE ***********/
	@PostMapping("/creationarticle")
	public String creationArticle(@Valid @ModelAttribute ArticleVendu articleVendu, BindingResult result, @RequestParam("image") MultipartFile file,
			@ModelAttribute Retrait retrait, Principal principal, Model model) throws IOException {
	if(result.hasErrors()) {
		model.addAttribute("categories", encheresServiceCategorie.findAllCategorie());
		return("creerarticle");
	}
		
		articleVendu.setUtilisateur(encheresServiceUtilisateur.findUserByPseudo(principal.getName()));
		encheresServiceArticlesVendus.createArticle(articleVendu);
		encheresServiceRetrait.createRetrait(retrait, articleVendu);
		// images
		StringBuilder fileNames = new StringBuilder();
		Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, (articleVendu.getNo_article().toString() + ".jpg"));
		fileNames.append(articleVendu.getNo_article() + ".jpg");
		Files.write(fileNameAndPath, file.getBytes());
		model.addAttribute("msg", fileNames.toString());
		return "redirect:/encheres";
	}

	/****************** AFFICHAGE PAGE DETAIL ARTICLE ***************/
	@GetMapping("/article")
	public String afficherDetailsArticles(Integer id, Model model, Principal principal) {
		ArticleVendu article = encheresServiceArticlesVendus.findArticleById(id);
		Retrait retrait = encheresServiceRetrait.findRetraitByArticle(article);
		Enchere meilleureEnchere = encheresServiceEncheres.getMeilleureEnchereByArticle(article);
		Enchere enchere = new Enchere();
		enchere.setMontant_enchere(article.getPrix_vente() + 10);

		if (principal != null) {
			model.addAttribute("user", encheresServiceUtilisateur.findUserByPseudo(principal.getName()));
		}
		model.addAttribute("article", article);
		model.addAttribute("retrait", retrait);
		model.addAttribute("enchere", enchere);
		model.addAttribute("meilleureEnchere", meilleureEnchere);
		Boolean tokenAffichage = encheresServiceEncheres.affichageDuBouton(article, principal);
		model.addAttribute("tokenAffichage", tokenAffichage);
		model.addAttribute("messageEnchereIndisponible",
				encheresServiceEncheres.messageBoutonEncherirIndisponible(article, principal));
		Boolean tokenBoutonCloture = encheresServiceEncheres.affichageBoutonCloture(article, principal);
		model.addAttribute("tokenCloture", tokenBoutonCloture);
		return "article";
	}

	/************** BOUTON RECHERCHE GET *****************/
	@GetMapping("/recherche")
	public String recherche(String rechercheNom, Model model, Categorie categorie, Utilisateur utilisateur,
			Principal principal, String optionArticle, String ventesEnCours, String ventesTerminees,
			String ventesNonDebutees, String encheresEnCours, String encheresOuvertes, String encheresGagnees) {
		model.addAttribute("listeCategorie", encheresServiceCategorie.findAllCategorie());
		if (principal != null) {
			utilisateur = encheresServiceUtilisateur.findUserByPseudo(principal.getName());
			List<ArticleVendu> listeArticlesVendeur = encheresServiceArticlesVendus.rechercheConnecte(rechercheNom,
					categorie, utilisateur, optionArticle, ventesEnCours, ventesTerminees, ventesNonDebutees,
					encheresEnCours, encheresOuvertes, encheresGagnees);
			model.addAttribute("listeArticles", listeArticlesVendeur);
			return "index";
		} else {
			// Recherche Non connectée OK
			List<ArticleVendu> listeArticlesCate = encheresServiceArticlesVendus.rechercheNonConnecte(rechercheNom,
					categorie);
			model.addAttribute("listeArticles", listeArticlesCate);
			return "index";
		}
	}

	@GetMapping("/cloturer")
	public String cloturerVente(Integer id, Principal principal) {
		System.out.println("On entre dans la methode cloturer");
		ArticleVendu article = encheresServiceArticlesVendus.findArticleById(id);
		System.out.println(article);
		encheresServiceEncheres.conclureVente(article);
		return "redirect:/encheres";
	}

}
