package Modele;

import java.util.ArrayList;

public class Categorie {

	String categorie;
	ArrayList<Item>liste;
	
	/**
	 * Constructeur de categorie
	 * @param categorie
	 */
	public Categorie(String categorie){
		this.categorie=categorie;
		liste=new ArrayList<Item>();
	}
	
	/**
	 * Ajout d'item dans cette categorie
	 * @param item
	 */
	public void addItemCategorie(Item item){
		liste.add(item);
	}
	
	public Item get(int i){
		return liste.get(i);
	}

	/**
	 * Getter du nom de la categorie
	 */
	public String getCategorie() {
		return categorie;
	}

	/**
	 * Setter du nom de la categorie
	 * @param categorie
	 */
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}
	
	public int size(){
		return liste.size();
	}
}
