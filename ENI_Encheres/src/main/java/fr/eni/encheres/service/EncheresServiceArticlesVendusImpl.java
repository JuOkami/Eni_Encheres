package fr.eni.encheres.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.dao.EncheresDaoArticlesVendus;
import fr.eni.encheres.dao.EncheresDaoEncheres;

@Service
public class EncheresServiceArticlesVendusImpl implements EncheresServiceArticlesVendus {
	
	EncheresDaoArticlesVendus encheresDaoArticlesVendus;
	
	

	public EncheresServiceArticlesVendusImpl(EncheresDaoArticlesVendus encheresDaoArticlesVendus) {
		this.encheresDaoArticlesVendus = encheresDaoArticlesVendus;
		
	}



	@Override
	public List<ArticleVendu> findAllArticleVendu() {
		return encheresDaoArticlesVendus.getAllArticleVendu();
	}



	@Override
	public void createArticle(ArticleVendu article) {
		encheresDaoArticlesVendus.creerArticle(article);
		
	}



	@Override
	public ArticleVendu findArticleById(Integer id) {
		return encheresDaoArticlesVendus.getArticleById(id);
	}



	@Override
	public void majPrixArticle(Enchere enchere) {
		 encheresDaoArticlesVendus.majPrixArticle(enchere);
		
	}



	@Override
	public List<ArticleVendu> findArticleContainNom(String rechercheNom) {
		return encheresDaoArticlesVendus.getArticleContainNom(rechercheNom);
		
	}

}
