package Controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import Modele.Categorie;
import Modele.Comparateur;
import Modele.Dictionnaire;
import Modele.Discriminant;
import Modele.FeedParser;
import Modele.IndexerRSS;
import Modele.Item;
import Modele.ListCategorie;
import Modele.Stemmer;
import Vue.Interface;

import com.cybozu.labs.langdetect.LangDetectException;
import com.sun.syndication.io.FeedException;

/**
 * @description Classe Action 
 * @author Richet Guillaume - Cadio Mathieu
 * @date 21/11/2015
 *
 */
public class Action implements ActionListener{

	public static IndexerRSS indexerRSS=new IndexerRSS();
	private FeedParser feedParser=new FeedParser();
	private JFrame frame;
	private Discriminant discriminant=new Discriminant();
	private Dictionnaire dicoFr, dicoEng;
	private Stemmer stemmer=new Stemmer();
	private HashMap<String, Integer>croissant=new HashMap<String, Integer>();
	private Comparateur comparateur;
	private Categorie sportFr, sportEn, santeFr, santeEn, econoEn, econoFr, scienceFr, scienceEn, cinemaFr, cinemaEn;
	private ListCategorie listCategorie;
	private int cpt=1;

	/**
	 * Mise en place des actions de l'interface
	 * @param event
	 */
	public void actionPerformed(ActionEvent event) {
		if(event.getSource()==Interface.valide){
			if(Interface.search.getText()!=null){
				cpt=1;
				try {
					read();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				String[] mots=Interface.search.getText().toLowerCase().split(" ");
				croissant.clear();
				for(int i=0; i<mots.length; i++){
					//determination du mot clé dans le dico francais donc passage par stem

					if(!discriminant.existFr(mots[i])){
						String res=stemmer.motsRacine(mots[i]);
						if(dicoFr.contains(res)){
							ArrayList<String> cle=dicoFr.getFrequence(res).getListKey();
							//ajout de toute les clés items dans une hashmap
							for(int j=0; j<cle.size();j++){
								//permet de savoir si une clé a été appelé plusieurs fois (à partir de tous les mots clés de la recherche)
								if(croissant.containsKey(cle.get(j))){
									croissant.replace(cle.get(j), croissant.get(cle.get(j))+1);
								}else{
									croissant.put(cle.get(j), 1);
								}
							}
						}						
					}
					//idem en verion anglais
					if(!discriminant.existEn(mots[i])){
						String res=stemmer.motsRacine(mots[i]);
						if(dicoEng.contains(res)){
							ArrayList<String> cle=dicoEng.getFrequence(res).getListKey();
							for(int j=0; j<cle.size();j++){
								if(croissant.containsKey(cle.get(j))){
									croissant.replace(cle.get(i), croissant.get(cle.get(j))+1);
								}else{
									croissant.put(cle.get(j), 1);
								}
							}
						}						
					}
				}
				//hashmap complet: affichage des items dont la clé est souvent appelé dans l'ordre decroissant
				Comparateur comp =  new Comparateur(croissant);
				TreeMap tri = new TreeMap(comp);
				tri.putAll(croissant);
				Set<String>setKey=tri.keySet();
				for(String j:setKey){
					for(int k=0; k<listCategorie.size(); k++){
						for(int l=0; l<listCategorie.get(k).size(); l++){
							if(listCategorie.get(k).get(l).getID().equals(j)){
								Interface.out.setText(Interface.out.getText()+affichage(listCategorie.get(k).get(l)));
							}
						}
					}
				}

				//ne sert plus a rien
				/*try {
					Action.indexerRSS.close();
					indexerRSS.SearchIndexRSS(Interface.search.getText());
					indexerRSS.close();
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}*/
			}
		}else if(event.getSource()==Interface.flux){
			frame=new JFrame("");
			frame.setVisible(false);
			Interface.valide.setEnabled(true);
			String url=JOptionPane.showInputDialog(frame, "Saissisez l'URL RSS:", "http://liberation.fr.feedsportal.com/c/32268/fe.ed/rss.liberation.fr/rss/10/");
			feedParser.setURL(url);
			try {
				feedParser.ReaderFeed(feedParser.getURL());
			} catch (IllegalArgumentException | IOException | FeedException
					| LangDetectException | SAXException | TikaException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(event.getSource()==Interface.temps){
			frame=new JFrame("");
			frame.setVisible(false);
			String duree=JOptionPane.showInputDialog(frame, "Saissisez le temps de rafraîchiessement (en min):", "30*60");
			feedParser.setTemps(Integer.parseInt(duree));
		}
	}

	private void read() throws ClassNotFoundException, IOException{
		//TODO dictionnaire
		dicoFr=new Dictionnaire();
		dicoEng=new Dictionnaire();
		dicoFr.read(new File("donnee/dicoFr.txt"));
		dicoEng.read(new File("donnee/dicoEng.txt"));
		//categorit
		listCategorie=new ListCategorie();
		sportFr=new Categorie("SportFr");
		scienceFr=new Categorie("ScienceFr");
		cinemaFr=new Categorie("CinemaFr");
		econoFr=new Categorie("EconoFr");
		santeFr=new Categorie("SanteFr");
		sportFr.read(new File("donnee/sportFr.ser"));
		econoFr.read(new File("donnee/econoFr.ser"));
		santeFr.read(new File("donnee/santeFr.ser"));
		scienceFr.read(new File("donnee/scienceFr.ser"));
		cinemaFr.read(new File("donnee/cinemaFr.ser"));
		listCategorie.add(sportFr);
		listCategorie.add(santeFr);
		listCategorie.add(scienceFr);
		listCategorie.add(econoFr);
		listCategorie.add(cinemaFr);
		sportEn=new Categorie("SportEn");
		santeEn=new Categorie("SanteEn");
		cinemaEn=new Categorie("CinemaEn");
		scienceEn=new Categorie("ScienceEn");
		econoEn=new Categorie("EconoEn");
		cinemaEn.read(new File("donnee/cinemaEn.ser"));
		sportEn.read(new File("donnee/sportEn.ser"));
		santeEn.read(new File("donnee/santeEn.ser"));
		scienceEn.read(new File("donnee/scienceEn.ser"));
		econoEn.read(new File("donnee/econoEn.ser"));
		listCategorie.add(sportEn);
		listCategorie.add(santeEn);
		listCategorie.add(scienceEn);
		listCategorie.add(econoEn);
		listCategorie.add(cinemaEn);
	}

	private String affichage(Item item){
		String titre="", id="", description="", auteur="", date="", flux="", source="", langue="";
		if(item.getTitre().equals("")){
			titre="Inconnu";
		}else{
			titre=item.getTitre();
		}
		if(item.getID().equals("")){
			id="Inconnu"; 
		}else{
			id=item.getID();
		}
		if(item.getDescription().equals("")){
			description="Inconnu";
		}else{
			description=item.getDescription();
		}
		if(item.getAuteur().equals("")){
			auteur="Inconnu";
		}else{
			auteur=item.getAuteur();
		}
		if(item.getDate().equals("")){
			date="Inconnu";
		}else{
			date=item.getDate();
		}
		if(item.getURLFlux().equals("")){
			flux="Inconnu";
		}else{
			flux=item.getURLFlux();
		}
		if(item.getURLSource().equals("")){
			source="Inconnu";
		}else{
			source=item.getURLSource();
		}
		if(item.getLangue().equals("")){
			langue="Inconnu";
		}else{
			langue=item.getLangue();
		}
		String sortie="Item "+(cpt++)+". Titre: "+titre+"\n        ID: "+id+"\n        Description: "+description+"\n        Auteur: "+auteur+"\n        Date: "+date+"\n        URL Source: "+source+"\n        URL Flux: "+flux+"\n        Langue: "+langue+"\n ------------------------------------------ \n";
		return sortie;
	}
}
