package Modele;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
}
