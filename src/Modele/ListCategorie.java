package Modele;

import java.util.ArrayList;

/**
 * @description Classe ListCategorie
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

public class ListCategorie {
	
	ArrayList<Categorie>liste;
	
	/**
	 * Constructeur ListCategorie
	 */
	public ListCategorie(){
		liste=new ArrayList<Categorie>();
	}
	
	/**
	 * Taille de la liste de categorie
	 * @return taille
	 */
	public int size(){
		return liste.size();
	}
	
	/**
	 * Ajout d'une categorie
	 * @param categorie
	 */
	public void add(Categorie categorie){
		liste.add(categorie);
	}
	
	/**
	 * Getter d'une categorie à partir du numero d'index
	 * @param i
	 * @return Categorie
	 */
	public Categorie get(int i){
		return liste.get(i);
	}
}
