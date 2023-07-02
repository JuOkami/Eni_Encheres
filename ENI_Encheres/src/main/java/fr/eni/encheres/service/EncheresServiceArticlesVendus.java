package fr.eni.encheres.service;

import java.util.List;

import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Enchere;

public interface EncheresServiceArticlesVendus {
	
	List<ArticleVendu> findAllArticleVendu();
	void createArticle(ArticleVendu article);
	ArticleVendu findArticleById(Integer id);
	void majPrixArticle(Enchere enchere);
	List<ArticleVendu> findArticleByCategorieContainNom(String rechercheNom, Categorie categorie);

}
