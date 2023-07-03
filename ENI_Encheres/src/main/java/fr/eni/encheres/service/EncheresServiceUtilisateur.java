package fr.eni.encheres.service;

import java.util.List;

import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;

public interface EncheresServiceUtilisateur {

	List<Utilisateur> findAllUtilisateurs();
	void createUtilisateur(Utilisateur utilisateur);
	Utilisateur findUtilisateurById(Integer id);
	public Utilisateur findUserByPseudo(String pseudo);
	void majCreditUtilisateur(Enchere enchere);
	void remboursementDernierEncherisseur(Enchere ancienneMeilleureEnchere);
}
