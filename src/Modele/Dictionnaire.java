package Modele;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Dictionnaire {

	private HashMap<String, Frequence> dico;
	
	public Dictionnaire(){
		dico=new HashMap<String, Frequence>();
	}
	
	public Set<String> listCle(){
		return dico.keySet();
	}
	
	public void add(String key){
		dico.put(key, new Frequence());
	}
	
	public void doublon(String key){
		dico.get(key).IncrementTf();
	}
	
	public void idf(String key, int nbDoc){
		dico.get(key).CalcIdf(nbDoc);
	}
	
	public void write(String filename){
		try {
			FileOutputStream out=new FileOutputStream(new File(filename));
			for(String i:dico.keySet()){
				String content=i+" "+dico.get(i).getTf()+" "+dico.get(i).getIdf()+"\n";
				out.write(content.getBytes());
				out.flush();
			}
			out.close();
		}catch (IOException f){
			f.printStackTrace();
		}
	}
	
	public void read(String filename){
		try {
			FileInputStream in=new FileInputStream(new File(filename));
			int content;
			while((content=in.read())!=-1){
				String[] tab=String.valueOf(content).split(" ");
				Frequence freq=new Frequence(Integer.parseInt(tab[1]), Double.parseDouble(tab[2]));
				dico.put(tab[0], freq);
			}
			in.close();
		}catch (IOException f){
			f.printStackTrace();
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
