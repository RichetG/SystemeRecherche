package Modele;

import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;

/**
 * @description Classe Stemmer
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

public class Stemmer {

	private FrenchStemmer fr_stemmer;
	private EnglishStemmer en_stemmer;
	
	/**
	 * Constructeur Stemmer
	 */
	public Stemmer(){
		fr_stemmer = new FrenchStemmer();
		en_stemmer = new EnglishStemmer();
	}
	
	/**
	 * Conversion d'un mot en son stem anglais
	 * @param mots
	 * @return stem
	 */
	public String rootWord(String mots){
		String tmp="";
		en_stemmer.setCurrent(mots);
		if (en_stemmer.stem()){
		    tmp=en_stemmer.getCurrent();
		}
		return tmp;
	}
	
	/**
	 * Conversion d'un mot en son stem francais
	 * @param mots
	 * @return stem
	 */
	public String motsRacine(String mots){
		String tmp="";
		fr_stemmer.setCurrent(mots);
		if (fr_stemmer.stem()){
		    tmp=fr_stemmer.getCurrent();
		    
		}
		return tmp;
	}
}
