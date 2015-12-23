package Modele;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @description Classe Frequence
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

public class Frequence {

	private int tf;
	private double idf;
	private HashSet<String>listKey;

	/**
	 * Constructeur Frequence par défaut
	 */
	public Frequence(){
		tf=1;
		idf=-1.0;
		listKey=new HashSet<String>();
	}
	
	/**
	 * Constructeur Frequence 
	 * @param tf
	 * @param idf
	 */
	public Frequence(int tf, double idf){
		this.tf=tf;
		this.idf=idf;
		listKey=new HashSet<String>();
	}
	
	/**
	 * Incrementation de tf
	 */
	public void IncrementTf(){
		tf++;
	}
	
	/**
	 * Calcul de la idf
	 * @param nbDoc
	 */
	public void CalcIdf(){
		idf=-(Math.log(tf/listKey.size())/Math.log(2));
	}
	
	/**
	 * Getter de tf
	 * @return tf
	 */
	public int getTf(){
		return tf;
	}
	
	/**
	 * Getter de idf
	 * @return idf
	 */
	public double getIdf(){
		return idf;
	}
	
	/**
	 * Setter de tf
	 * @param tf
	 */
	public void setTf(int tf){
		this.tf=tf;
	}
	
	/**
	 * Setter de idf
	 * @param idf
	 */
	public void setIdf(double idf){
		this.idf=idf;
	}
	
	/**
	 * Ajout d'une clé
	 * @param key
	 */
	public void addKey(String key){
		listKey.add(key);
	}
	
	/**
	 * Accesseur de tous les clés
	 * @return
	 */
	public ArrayList<String> getListKey(){
		ArrayList<String>tmp=new ArrayList<String>();
		Iterator<String>it=listKey.iterator();
		while(it.hasNext()){
			tmp.add(it.next());
		}
		return tmp;
	}
}
