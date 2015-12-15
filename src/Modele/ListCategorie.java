package Modele;

import java.util.ArrayList;

public class ListCategorie {
	
	ArrayList<Categorie>liste;
	
	public ListCategorie(){
		liste=new ArrayList<Categorie>();
	}
	
	public int size(){
		return liste.size();
	}
	
	public void add(Categorie categorie){
		liste.add(categorie);
	}
}
