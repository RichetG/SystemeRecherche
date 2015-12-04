package Modele;

import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;

public class Stemmer {

	private FrenchStemmer fr_stemmer;
	private EnglishStemmer en_stemmer;
	
	public Stemmer(){
		fr_stemmer = new FrenchStemmer();
		en_stemmer = new EnglishStemmer();
	}
	
	public String rootWord(String mots){
		String tmp="";
		en_stemmer.setCurrent(mots);
		if (en_stemmer.stem()){
		    tmp=en_stemmer.getCurrent();
		}
		return tmp;
	}
	
	public String motsRacine(String mots){
		String tmp="";
		fr_stemmer.setCurrent(mots);
		if (fr_stemmer.stem()){
		    tmp=fr_stemmer.getCurrent();
		    
		}
		return tmp;
	}
}
