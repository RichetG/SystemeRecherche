package Modele;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @description Classe Categorie
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

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

	/**
	 * Getter de l'item à partir de son numéro d'index
	 * @param i
	 * @return item
	 */
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

	/**
	 * Taille d ela liste d'item
	 * @return taille 
	 */
	public int size(){
		return liste.size();
	}

	/**
	 * Sauvegarde de la liste des items de la categorie
	 * @param filename
	 * @throws IOException
	 */
	public void write(File filename) throws IOException{
		try {
			FileOutputStream fos=new FileOutputStream(filename);
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(fos);
			objectOutputStream.writeInt(liste.size());
			for(int i=0; i<liste.size(); i++){
				objectOutputStream.writeObject(liste.get(i));
				objectOutputStream.flush();
			}
			objectOutputStream.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restoration de la liste des items de la categorie
	 * @param filename
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void read(File filename) throws IOException, ClassNotFoundException{
		try {
			FileInputStream fos=new FileInputStream(filename);
			ObjectInputStream objectOutputStream=new ObjectInputStream(fos);
			int cpt=(Integer)objectOutputStream.readInt();
			for(int i=0; i<cpt; i++){
				Item item=(Item)objectOutputStream.readObject();
				liste.add(item);
			}
			objectOutputStream.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Mise a zero de la liste
	 */
	public void clear(){
		liste.clear();
	}
}
