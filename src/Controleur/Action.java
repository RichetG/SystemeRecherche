package Controleur;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	private JFrame frame;
	private Discriminant discriminant=new Discriminant();
	private Dictionnaire dicoFr, dicoEng;
	private Stemmer stemmer=new Stemmer();
	private HashMap<String, Integer>croissant=new HashMap<String, Integer>();
	@SuppressWarnings("unused")
	private Comparateur comparateur;
	private Categorie sportFr, sportEn, santeFr, santeEn, econoEn, econoFr, scienceFr, scienceEn, cinemaFr, cinemaEn;
	private ListCategorie listCategorie;
	private int cpt=1;
	private JDialog dialog;
	private boolean filtreOn=true;
	private JCheckBox sante=new JCheckBox("Sante"), sport=new JCheckBox("Sport"), science=new JCheckBox("Science"), cine=new JCheckBox("Cinéma"), eco=new JCheckBox("Economie");

	/**
	 * Mise en place des actions de l'interface
	 * @param event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void actionPerformed(ActionEvent event) {
		//action sur le bouton valide
		if(event.getSource()==Interface.valide){
			if(Interface.search.getText()!=null){
				Interface.out.setText("");
				//action sur l'utilisation du filtre
				if(filtreOn){
					filtresFirst();
				}
				cpt=1;
				try {
					//voir methode privé plus bas
					read();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				String[] mots=Interface.search.getText().toLowerCase().split(" ");
				croissant.clear();
				//parcous de tous les mots de la recherche
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
								//affichage dans la sortie de l'interface
								Interface.out.setText(Interface.out.getText()+affichage(listCategorie.get(k).get(l)));
							}
						}
					}
				}
			}
			filtreOn=false;
			//action sur le flux
		}else if(event.getSource()==Interface.flux){
			frame=new JFrame("");
			frame.setVisible(false);
			Interface.valide.setEnabled(true);
			String url=JOptionPane.showInputDialog(frame, "Saissisez l'URL RSS:", "http://liberation.fr.feedsportal.com/c/32268/fe.ed/rss.liberation.fr/rss/10/");
			Interface.feedParser.setURL(url);
			try {
				Interface.feedParser.ReaderFeed(Interface.feedParser.getURL());
			} catch (IllegalArgumentException | IOException | FeedException
					| LangDetectException | SAXException | TikaException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//action sur le filtre
		}else if(event.getSource()==Interface.filtre){
			if(filtreOn){
				filtresFirst();
			}
			dialog=new JDialog();
			dialog.setSize(320, 170);
			dialog.setLayout(new BorderLayout());
			JLabel categorie;
			JPanel panel1, panel2, panel3, panel4, panel5;
			panel3=new JPanel(new FlowLayout());
			categorie=new JLabel("Veuillez sélectionné les catégories que désirées:");
			panel3.add(categorie);
			panel1=new JPanel(new FlowLayout());
			panel1.add(sante);
			panel1.add(sport);
			panel1.add(science);
			panel2=new JPanel(new FlowLayout());
			panel2.add(cine);
			panel2.add(eco);
			panel4=new JPanel(new GridLayout(3, 1));
			panel4.add(panel3);
			panel4.add(panel1);
			panel4.add(panel2);
			dialog.add(panel4, "Center");
			JButton valider, annuler;
			panel5=new JPanel(new FlowLayout());
			valider=new JButton("Valider");
			valider.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
				}
			});
			annuler=new JButton("Annuler");
			annuler.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					dialog.setVisible(false);
				}
			});
			panel5.add(valider);
			panel5.add(annuler);
			dialog.add(panel5, "South");
			filtreOn=false;
			dialog.setVisible(true);
		}
	}

	/**
	 * Stockage du dictionnaire ainsi que la connaissance des checkbox coché/non coché
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void read() throws ClassNotFoundException, IOException{
		//TODO dictionnaire
		dicoFr=new Dictionnaire();
		dicoEng=new Dictionnaire();
		dicoFr.read(new File("donnee/dicoFr.txt"));
		dicoEng.read(new File("donnee/dicoEng.txt"));
		//categorit
		listCategorie=new ListCategorie();
		if(sport.isSelected()){
			sportFr=new Categorie("SportFr");
			sportFr.read(new File("donnee/sportFr.ser"));
			listCategorie.add(sportFr);
		}
		if(science.isSelected()){
			scienceFr=new Categorie("ScienceFr");
			scienceFr.read(new File("donnee/scienceFr.ser"));
			listCategorie.add(scienceFr);
		}
		if(cine.isSelected()){
			cinemaFr=new Categorie("CinemaFr");
			cinemaFr.read(new File("donnee/cinemaFr.ser"));
			listCategorie.add(cinemaFr);
		}
		if(eco.isSelected()){
			econoFr=new Categorie("EconoFr");
			econoFr.read(new File("donnee/econoFr.ser"));
			listCategorie.add(econoFr);
		}
		if(sante.isSelected()){
			santeFr=new Categorie("SanteFr");
			santeFr.read(new File("donnee/santeFr.ser"));
			listCategorie.add(santeFr);
		}
		if(sport.isSelected()){
			sportEn=new Categorie("SportEn");
			sportEn.read(new File("donnee/sportEn.ser"));
			listCategorie.add(sportEn);
		}
		if(sante.isSelected()){
			santeEn=new Categorie("SanteEn");
			santeEn.read(new File("donnee/santeEn.ser"));
			listCategorie.add(santeEn);
		}
		if(cine.isSelected()){
			cinemaEn=new Categorie("CinemaEn");
			cinemaEn.read(new File("donnee/cinemaEn.ser"));
			listCategorie.add(cinemaEn);
		}
		if(science.isSelected()){
			scienceEn=new Categorie("ScienceEn");
			scienceEn.read(new File("donnee/scienceEn.ser"));
			listCategorie.add(scienceEn);
		}
		if(eco.isSelected()){
			econoEn=new Categorie("EconoEn");
			econoEn.read(new File("donnee/econoEn.ser"));
			listCategorie.add(econoEn);
		}
	}

	/**
	 * Affichage d'un item après recherche
	 * @param item
	 * @return string
	 */
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

	/**
	 * Activation par defaut de tous les checkbox
	 */
	private void filtresFirst(){
		sante.setSelected(true);
		science.setSelected(true);
		sport.setSelected(true);
		cine.setSelected(true);
		eco.setSelected(true);
	}
}
