package fr.eni.encheres.dao;

import java.util.List;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.ArticleVendu;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;

@Repository
public class EncheresDaoEncheresImpl implements EncheresDaoEncheres {

	
	public final static String INSERT_ENCHERES ="INSERT INTO ENCHERES (date_enchere, montant_enchere, no_article, no_utilisateur) VALUES (:date_enchere, :montant_enchere, :no_article, :no_utilisateur)";
	public final static String SELECT_ENCHERES_BY_ARTICLE = "select encheres.no_utilisateur, encheres.no_article, date_enchere, montant_enchere from ENCHERES inner join ARTICLES_VENDUS on ENCHERES.no_article = ARTICLES_VENDUS.no_article where ARTICLES_VENDUS.no_article =:no_article";
	public final static String UPDATE_ENCHERE = "update ENCHERES set date_enchere=:date_enchere, montant_enchere=:montant_enchere where no_utilisateur=:no_utilisateur";
	private static final String SELECT_ENCHERES_BY_USER = "SELECT * FROM ENCHERES INNER JOIN ARTICLES_VENDUS ON ENCHERES.no_utilisateur = ARTICLES_VENDUS.no_utilisateur WHERE ARTICLES_VENDUS.no_utilisateur =:no_utilisateur";
	
	private EncheresDaoUtilisateurs encheresDaoUtilisateurs;
	private EncheresDaoArticlesVendus encheresDaoArticlesVendus;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public EncheresDaoEncheresImpl(EncheresDaoUtilisateurs encheresDaoUtilisateurs,
			EncheresDaoArticlesVendus encheresDaoArticlesVendus,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		super();
		this.encheresDaoUtilisateurs = encheresDaoUtilisateurs;
		this.encheresDaoArticlesVendus = encheresDaoArticlesVendus;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}


	@Override
	public void saveEnchere(Enchere enchere) {
		//new EncheresRowMapper(encheresDaoArticlesVendus, encheresDaoUtilisateurs);
		MapSqlParameterSource enchereMap = new MapSqlParameterSource();	
		enchereMap.addValue("date_enchere", (enchere.getDate_enchere()));
		enchereMap.addValue("montant_enchere", enchere.getMontant_enchere());
		enchereMap.addValue("no_article", enchere.getArticle().getNo_article());
		enchereMap.addValue("no_utilisateur", enchere.getUtilisateur().getNo_utilisateur());	
		
		Boolean dejaEncherit = false;
		for (Enchere enchereDejaExistante : findEncheresByArticle(enchere.getArticle())) {
			if (enchere.getUtilisateur().getNo_utilisateur() == enchereDejaExistante.getUtilisateur().getNo_utilisateur()) {
				dejaEncherit = true;
				break;
			} 
			dejaEncherit = false;
			}
		if (dejaEncherit) {
		namedParameterJdbcTemplate.update(UPDATE_ENCHERE, enchereMap);
		} else {
		namedParameterJdbcTemplate.update(INSERT_ENCHERES, enchereMap);
		}
	}
	
	public List<Enchere> findEncheresByArticle(ArticleVendu article){
		List<Enchere> liste;
		liste = namedParameterJdbcTemplate.query(SELECT_ENCHERES_BY_ARTICLE, new BeanPropertySqlParameterSource(article) ,new EncheresRowMapper(encheresDaoArticlesVendus, encheresDaoUtilisateurs));
		return liste;
	}


	@Override
	public List<Enchere> getEncheresByUser(Utilisateur utilisateur) {
		List<Enchere> liste;
		liste = namedParameterJdbcTemplate.query(SELECT_ENCHERES_BY_USER, new BeanPropertySqlParameterSource(utilisateur) ,new EncheresRowMapper(encheresDaoArticlesVendus, encheresDaoUtilisateurs));
		return liste;
	}

}
