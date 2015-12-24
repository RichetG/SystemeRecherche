package Modele;

//ROME example 
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//api md5
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;





//api tika
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.xml.sax.ContentHandler;

import Controleur.Action;
import Vue.Interface;





//api langage detection
import com.cybozu.labs.langdetect.DetectorFactory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
//api JSON

/**
 * @description Classe FeedParser détecte tous les items présent dans le flux RSS
 * @author Richet Guillaume - Cadio Mathieu
 * @date 21/11/2015
 *
 */
@SuppressWarnings("unused")
public class FeedParser{ 

	//TODO Création d'un flux JSON
	private	ObjectMapper objectMapper=new ObjectMapper();
	private	ListItems lists=new ListItems();
	private Item item;
	private boolean validite=true;
	protected ConcurrentNavigableMap<String, Item>Map;
	private double ratio=0;
	private Thread thread=new Thread();
	private int temps=30*60;
	private String URL="";
	private String entre="";
	private Dictionnaire dicoEng, dicoFr;
	private Stemmer stem;
	private ListCategorie listCategorie;
	private Discriminant discriminant=new Discriminant();
	private Classication classFr, classEn;
	private Categorie sportFr, sportEn, santeFr, santeEn, econoEn, econoFr, scienceFr, scienceEn, cinemaFr, cinemaEn;

	/**
	 * Constructeur FeedParser
	 * @throws Exception 
	 */
	public FeedParser(){
		dicoFr=new Dictionnaire();
		dicoEng=new Dictionnaire();
		listCategorie=new ListCategorie();
		sportFr=new Categorie("SportFr");
		sportEn=new Categorie("SportEn");
		santeFr=new Categorie("SanteFr");
		santeEn=new Categorie("SanteEn");
		scienceFr=new Categorie("ScienceFr");
		scienceEn=new Categorie("ScienceEn");
		cinemaFr=new Categorie("CinemaFr");
		cinemaEn=new Categorie("CinemaEn");
		econoFr=new Categorie("EconoFr");
		econoEn=new Categorie("EconoEn");
		listCategorie.add(sportFr);
		listCategorie.add(sportEn);
		listCategorie.add(santeFr);
		listCategorie.add(santeEn);
		listCategorie.add(scienceFr);
		listCategorie.add(scienceEn);
		listCategorie.add(econoFr);
		listCategorie.add(econoEn);
		listCategorie.add(cinemaFr);
		listCategorie.add(cinemaEn);
		new File("donnee").mkdir();
		try {
			entre+=Interface.in.getText()+"\n\n Mise à jour: 0%\n-------------------------\n";
			curseur(entre);
			ReaderFeed("http://rmcsport.bfmtv.com/rss/basket/");
			entre+="Mise à jour: 20%\n-------------------------\n";
			curseur(entre);
			/*ReaderFeed("http://www.thetimes.co.uk/tto/sport/rss");
			entre+="Mise à jour: 20%\n-------------------------\n";
			curseur(entre);*/
			ReaderFeed("http://www.lemonde.fr/sante/rss_full.xml");
			entre+="Mise à jour: 40%\n-------------------------\n";
			curseur(entre);	
			/*ReaderFeed("http://www.thetimes.co.uk/tto/health/rss");
			entre+="Mise à jour: 40%\n-------------------------\n";
			curseur(entre);	*/	
			ReaderFeed("http://www.lemonde.fr/sciences/rss_full.xml");
			entre+="Mise à jour: 60%\n-------------------------\n";
			curseur(entre);		
			/*ReaderFeed("http://www.thetimes.co.uk/tto/science/rss");
			entre+="Mise à jour: 60%\n-------------------------\n";
			curseur(entre);*/	
			ReaderFeed("http://rss.allocine.fr/ac/actualites/cine");
			entre+="Mise à jour: 80%\n-------------------------\n";
			curseur(entre);	
			/*ReaderFeed("http://feeds.feedburner.com/cinemablendallthing");
			entre+="Mise à jour: 80%\n-------------------------\n";
			curseur(entre);*/
			ReaderFeed("http://www.lesechos.fr/rss/rss_articles_journal.xml");
			entre+="Mise à jour: 100%\n-------------------------\n";
			curseur(entre);
			/*ReaderFeed("http://www.thetimes.co.uk/tto/business/rss");
			entre+="Mise à jour: 100%\n-------------------------\n";
			curseur(entre);*/
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Recupération des flux RSS d'une url
	 * @param urlEntree
	 * @throws Exception 
	 */
	public void ReaderFeed(String urlEntree) throws Exception{
		URL url = new URL(urlEntree);

		HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();  
		// Reading the feed  
		SyndFeedInput input = new SyndFeedInput();  
		SyndFeed feed = input.build(new XmlReader(httpcon));  
		@SuppressWarnings("rawtypes")
		List entries = feed.getEntries();  
		@SuppressWarnings("rawtypes")
		Iterator itEntries = entries.iterator();  

		String st, text="", date="";

		//TODO utilisation de mapDp
		DB db=DBMaker.newFileDB(new File("BDD")).closeOnJvmShutdown().encryptionEnable("password").make();
		Map=db.getTreeMap("Items");

		//TODO Chargement du dossier avec les profils de langues
		if(validite){
			String path=new File("").getAbsolutePath()+"/profiles.sm";
			DetectorFactory.loadProfile(path);
		}
		//TODO Création du detecteur
		com.cybozu.labs.langdetect.Detector detector = DetectorFactory.create();

		while (itEntries.hasNext()) {  
			SyndEntry entry = (SyndEntry) itEntries.next();  
			st=entry.getTitle().replaceAll("\\n", "").replaceAll("\\t", "").replaceAll("[^\\d\\p{L}!#$€%&'`(),;:/@...]"," ");
			//TODO si le titre et l'url source est inconnu, on rejette l'item car on ne pourra connaitre la clé Id
			if(st.isEmpty() || entry.getLink().isEmpty()){
				entry=(SyndEntry) itEntries.next(); //TODO donc on passe au suivant
			}
			System.out.println("Titre: " + st);
			System.out.println("URL flux: "+ url);
			System.out.println("URL source: " + entry.getLink());
			if(!entry.getAuthor().isEmpty()){
				System.out.println("Auteur: " + entry.getAuthor());  
			}else{
				System.out.println("Auteur: Inconnu");
			}
			if(entry.getPublishedDate()!=null){
				System.out.println("Date publication: " + entry.getPublishedDate()); 
				date=entry.getPublishedDate().toString();
			}else{
				System.out.println("Date publication: Inconnu");
				date="Inconnu";
			}
			if(!entry.getDescription().getValue().isEmpty()){
				System.out.println("Description: " + entry.getDescription().getValue());
			}else{
				System.out.println("Description: Inconnu");
			}

			//TODO mise en place de la clé MD5
			String keyMD5=st + entry.getLink(); //creation de la clé a partir du titre et url source
			byte[] key=keyMD5.getBytes();
			byte[] hash=null;

			try{
				hash=MessageDigest.getInstance("MD5").digest(key);
			}catch(NoSuchAlgorithmException e){
				System.err.println("erreur MD5");
			}
			StringBuilder hashString=new StringBuilder();
			for(int i=0; i<hash.length;i++){
				String hex=Integer.toHexString(hash[i]);//conversion hexadecimal
				if(hex.length()==1){
					hashString.append("0");
					hashString.append(hex.charAt(hex.length()-1));
				}else{
					hashString.append(hex.substring(hex.length()-2));
				}
			}
			System.out.println("ID: "+hashString.toString());
			//fin MD5

			//TODO A chaque entrée fait le test de la détection de langue
			detector.append(st);
			//Stockage de la lanue dans un String
			String lang = detector.detect();
			System.out.println("Langue: " + lang);

			//TODO mise ne place de TIKA
			HttpGet httpget = new HttpGet(entry.getLink()); 
			HttpEntity entity = null;
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpget);
			entity = response.getEntity();
			InputStream instream = entity.getContent();
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			Parser parser = new AutoDetectParser();
			char [] content = handler.toString().toCharArray();
			//Ignore les espaces dans mon contenu parsé
			handler.ignorableWhitespace(content, 0, content.length);
			try {
				parser.parse(instream, handler, metadata, new ParseContext());
				//Remplacement de tous les charactères spéciaux par un vide
				text=handler.toString().replaceAll("[^\\d\\p{L}!#$€%&'`(),;:/@...]","");
			}catch (IOException e){
				text="";
			}

			System.out.println("content: " + text);
			System.out.println();

			//TODO stockage des items dans un fichier JSON
			item=new Item(st, url.toString(), entry.getLink(), entry.getAuthor(), date, entry.getDescription().getValue().toString().replaceAll("[^\\d\\p{L}!#$€%&'`(),;:/@...]"," "), hashString.toString(), lang, text);
			lists.addItem(item);
		} 

		//TODO categorie
		sportFr.clear();
		sportEn.clear();
		santeFr.clear();
		santeEn.clear();
		scienceFr.clear();
		scienceEn.clear();
		cinemaFr.clear();
		cinemaEn.clear();
		econoFr.clear();
		econoEn.clear();
		sportFr.read(new File("donnee/sportFr.ser"));
		sportEn.read(new File("donnee/sportEn.ser"));
		santeFr.read(new File("donnee/santeFr.ser"));
		santeEn.read(new File("donnee/santeEn.ser"));
		scienceFr.read(new File("donnee/scienceFr.ser"));
		scienceEn.read(new File("donnee/scienceEn.ser"));
		cinemaFr.read(new File("donnee/cinemaFr.ser"));
		cinemaEn.read(new File("donnee/cinemaEn.ser"));
		econoFr.read(new File("donnee/econoFr.ser"));
		econoEn.read(new File("donnee/econoEn.ser"));

		//TODO dictionnaire
		System.out.println("Restauration des dicos\n");
		dicoFr.read(new File("donnee/dicoFr.txt"));
		dicoEng.read(new File("donnee/dicoEng.txt"));
		stem=new Stemmer();

		//TODO ajout des items inexistants dans mapDb
		for(String i:lists.KeySet()){
			if(!Map.containsKey(i)){
				ratio++;
				Map.put(i, lists.getItem(i));
				entre+="Item d'ID: "+i+" ajouté\n       Titre: "+lists.getItem(i).getTitre()+"\n";

				//TODO categorie defaut
				//flux des sport FR
				if(urlEntree.equals("http://rmcsport.bfmtv.com/rss/basket/")){
					//voir methode privé plus bas
					stemFr(lists.getItem(i));
					sportFr.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.thetimes.co.uk/tto/sport/rss")){
					stemEn(lists.getItem(i));
					sportEn.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.lemonde.fr/sante/rss_full.xml")){
					stemFr(lists.getItem(i));
					santeFr.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.thetimes.co.uk/tto/health/rss")){
					stemEn(lists.getItem(i));
					santeEn.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.lemonde.fr/sciences/rss_full.xml")){
					stemFr(lists.getItem(i));
					scienceFr.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.thetimes.co.uk/tto/science/rss")){
					stemEn(lists.getItem(i));
					scienceEn.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://rss.allocine.fr/ac/actualites/cine")){
					stemFr(lists.getItem(i));
					cinemaFr.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://feeds.feedburner.com/cinemablendallthing")){
					stemEn(lists.getItem(i));
					cinemaEn.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.lesechos.fr/rss/rss_articles_journal.xml")){
					stemFr(lists.getItem(i));
					econoFr.addItemCategorie(lists.getItem(i));
				}else if(urlEntree.equals("http://www.thetimes.co.uk/tto/business/rss")){
					stemEn(lists.getItem(i));
					econoEn.addItemCategorie(lists.getItem(i));
					//flux inconnu
				}else{
					if(lists.getItem(i).getLangue().equals("fr")){
						stemFr(lists.getItem(i));
						//Mise a jour du classifier
						classFr=new Classication(dicoFr);
						String test="sport, sante, science, eco, cine, autre: ";
						for(int j=0; j<listCategorie.size(); j+=2){
							classFr.instance(listCategorie.get(j));
							test+=listCategorie.get(j).size()+" ";
						}
						System.out.println(test);
						String cat=classFr.instanceInconnu(lists.getItem(i));
						for(int j=0; j<listCategorie.size(); j++){
							if(listCategorie.get(j).getCategorie().equals(cat)){
								listCategorie.get(j).addItemCategorie(lists.getItem(i));
							}
						}
					}else{
						stemEn(lists.getItem(i));
						classEn=new Classication(dicoEng);
						//TODO pb version anglais
						for(int j=1; j<listCategorie.size(); j+=2){
							classEn.instance(listCategorie.get(j));
						}
						String cat=classEn.instanceInconnu(lists.getItem(i));
						for(int j=0; j<listCategorie.size(); j++){
							if(listCategorie.get(j).getCategorie().equals(cat)){
								listCategorie.get(j).addItemCategorie(lists.getItem(i));
							}
						}
					}
				}
			}
		}
		//mise a jour du calcul de idf
		for(String i: dicoFr.listStem()){
			dicoFr.getFrequence(i).CalcIdf();
		}
		for(String i: dicoEng.listStem()){
			dicoEng.getFrequence(i).CalcIdf();
		}		
		entre+="Taux de mise à jour de la base de donnée: "+(ratio/(double)Map.size())*100+"%\n-------------------------\n";
		curseur(entre);
		db.commit();
		db.close();
		ratio=0;
		validite=false;

		System.out.println("Sauvegarde des dicos et des categories\n");
		dicoFr.write(new File("donnee/dicoFr.txt"));
		dicoEng.write(new File("donnee/dicoEng.txt"));
		sportFr.write(new File("donnee/sportFr.ser"));
		sportEn.write(new File("donnee/sportEn.ser"));
		santeFr.write(new File("donnee/santeFr.ser"));
		santeEn.write(new File("donnee/santeEn.ser"));
		scienceFr.write(new File("donnee/scienceFr.ser"));
		scienceEn.write(new File("donnee/scienceEn.ser"));
		cinemaFr.write(new File("donnee/cinemaFr.ser"));
		cinemaEn.write(new File("donnee/cinemaEn.ser"));
		econoFr.write(new File("donnee/econoFr.ser"));
		econoEn.write(new File("donnee/econoEn.ser"));

		//TODO classifieur misa  jour final
		System.out.println("Classification");
		classFr=new Classication(dicoFr);
		classEn=new Classication(dicoEng);
		for(int i=0; i<listCategorie.size(); i+=2){
			classFr.instance(listCategorie.get(i));
		}
		//TODO pb version anglais
		/*for(int i=1; i<listCategorie.size(); i+=2){
			classEn.instance(listCategorie.get(i));
		}*/
		classFr.save();
		classEn.save();
	} 

	/**
	 * Revisite du flux RSS
	 * @throws Exception 
	 */
	@SuppressWarnings("static-access")
	public void revisite() throws Exception{
		//TODO mise en place de la revisite tous les x secondes(boucle infini)
		//validite=false;
		try {
			thread.sleep(getTemps()*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReaderFeed(getURL());
	}

	/**
	 * getter du temps
	 * @return temps
	 */
	public int getTemps(){
		return temps;
	}

	/**
	 * Setter du temps
	 * @param temps
	 */
	public void setTemps(int temps){
		this.temps=temps;
	}

	/**
	 * Getter d'url
	 * @return url
	 */
	public String getURL(){
		return URL;
	}

	/**
	 * Setter d'url
	 */
	public void setURL(String URL){
		this.URL=URL;
	}

	/**
	 * Mise a jour du dictionnaire francais
	 * @param i
	 */
	private void stemFr(Item i){
		//nettoyage textuelle de l'item pour l'analyse
		String traitementTit=i.getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String traitementDes=i.getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String[] tit=traitementTit.split(" ");
		String[] des=traitementDes.split(" ");
		//parcours de tous les mots de titre d l'item
		for(int j=0; j<tit.length; j++){
			//si le stem exixste dans le dico, on incremente tf, sinon on le cree
			if(!discriminant.existFr(tit[j])){
				if(dicoFr.parcours(stem.motsRacine(tit[j]))){
					dicoFr.doublon(stem.motsRacine(tit[j]), i.getID());
				}else{
					dicoFr.add(stem.motsRacine(tit[j]), i.getID());
				}
			}
		}
		//idem pour tous les mots de description de l'item
		for(int j=0; j<des.length; j++){
			if(!discriminant.existFr(des[j])){
				if(dicoFr.parcours(stem.motsRacine(des[j]))){
					dicoFr.doublon(stem.motsRacine(des[j]), i.getID());
				}else{
					dicoFr.add(stem.motsRacine(des[j]), i.getID());
				}
			}
		}
	}

	/**
	 * Mise à jour du dictionnaire anglais
	 * @param i
	 */
	private void stemEn(Item i){
		String traitementTit=i.getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String traitementDes=i.getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String[] tit=traitementTit.split(" ");
		String[] des=traitementDes.split(" ");
		for(int j=0; j<tit.length; j++){
			//si le stem exixste dans le dico, on incremente tf, sinon on le cree
			if(!discriminant.existEn(tit[j])){
				if(dicoEng.parcours(stem.motsRacine(tit[j]))){
					dicoEng.doublon(stem.motsRacine(tit[j]), i.getID());
				}else{
					dicoEng.add(stem.motsRacine(tit[j]), i.getID());
				}
			}
		}
		for(int j=0; j<des.length; j++){
			if(!discriminant.existEn(des[j])){
				if(dicoEng.parcours(stem.motsRacine(des[j]))){
					dicoEng.doublon(stem.motsRacine(des[j]), i.getID());
				}else{
					dicoEng.add(stem.motsRacine(des[j]), i.getID());
				}
			}
		}
	}

	private void curseur(String entree){
		Interface.in.setText(entre);
		int length = Interface.in.getText().length();
		Interface.in.setCaretPosition(length);
	}
}  