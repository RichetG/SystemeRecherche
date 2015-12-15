package Modele;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Dictionnaire {

	private HashMap<String, Frequence> dico;

	/**
	 * Constructeur Dictionnaire
	 */
	public Dictionnaire(){
		dico=new HashMap<String, Frequence>();
	}
	
	public int size(){
		return dico.size();
	}

	/**
	 * Getter du contenu des clé du dico
	 * @return liste clé
	 */
	public Set<String> listStem(){
		return dico.keySet();
	}

	/**
	 * Getter de la frequence
	 * @param key
	 * @return
	 */
	public Frequence getFrequence(String key){
		return dico.get(key);
	}

	/**
	 * Ajout d'une clé
	 * @param key
	 */
	public void add(String stem, String doc){
		Frequence frequence=new Frequence();
		frequence.addKey(doc);
		dico.put(stem, frequence);
	}

	/**
	 * Incrementation du tf
	 * @param key
	 */
	public void doublon(String stem, String doc){
		dico.get(stem).IncrementTf();
		dico.get(stem).addKey(doc);
	}

	/**
	 * Sauvegarde du dico
	 * @param filename
	 * @throws IOException
	 */
	public void write(File filename) throws IOException{
		try {
			FileWriter fos=new FileWriter(filename);
			for(String i:dico.keySet()){
				String tmp=i+" "+dico.get(i).getTf()+" "+dico.get(i).getIdf()+" {";
				ArrayList<String>ids=dico.get(i).getListKey();
				for(int j=0; j<ids.size(); j++){
					tmp+=ids.get(j)+",";
				}
				tmp+="}\n";
				fos.write(tmp);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restauration du dico
	 * @param filename
	 * @throws IOException
	 */
	public void read(File filename) throws IOException{
		try {
			FileReader fis=new FileReader(filename);
			BufferedReader reader=new BufferedReader(fis);
			String line="";
			while((line=reader.readLine())!=null){
				String[] tmp=line.split(" ");
				if(tmp.length==4){
					Frequence frequence=new Frequence(Integer.valueOf(tmp[1]), Double.valueOf(tmp[2]));
					String[] ids=tmp[3].replace("{", "").replace(",}", "").split(",");
					for(int i=0; i<ids.length; i++){
						frequence.addKey(ids[i]);
					}
					dico.put(tmp[0], frequence);
				}
			}
			reader.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test de l'existance du stem
	 * @param mots
	 * @return
	 */
	public boolean parcours(String mots){
		boolean test=false;
		for(String i:dico.keySet()){
			if(i.equals(mots)){
				test=true;
				break;
			}
		}
		return test;
	}
}
