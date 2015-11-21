package Modele;

//ROME example 
import java.io.File;
import java.io.FileOutputStream;
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
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

//api JSON
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;







import Controleur.Action;
import Vue.Interface;


//api langage detection
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @description Classe FeedParser détecte tous les items présent dans le flux RSS
 * @author Richet Guillaume - Cadio Mathieu
 * @date 21/11/2015
 *
 */
public class FeedParser{ 

	//TODO Création d'un flux JSON
	private	ObjectMapper objectMapper=new ObjectMapper();
	private	ListItems lists=new ListItems();
	private Item item;
	private boolean validite=true;
	protected ConcurrentNavigableMap<String, Item>Map;
	private int ratio=0;
	private Thread thread=new Thread();
	private int temps=30*60;
	private String URL="";

	/**
	 * Recupération des flux RSS d'une url
	 * @param urlEntree
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws LangDetectException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public void ReaderFeed(String urlEntree) throws IOException, IllegalArgumentException, FeedException, LangDetectException, SAXException, TikaException{
		URL url = new URL(urlEntree);

		HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();  
		// Reading the feed  
		SyndFeedInput input = new SyndFeedInput();  
		SyndFeed feed = input.build(new XmlReader(httpcon));  
		@SuppressWarnings("rawtypes")
		List entries = feed.getEntries();  
		@SuppressWarnings("rawtypes")
		Iterator itEntries = entries.iterator();  

		String st;
		String text="";

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
			st=entry.getTitle().replaceAll("\\n", "").replaceAll("\\t", "");
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
			if(!entry.getPublishedDate().toString().isEmpty()){
				System.out.println("Date publication: " + entry.getPublishedDate()); 
			}else{
				System.out.println("Date publication: Inconnu");
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
			detector.append(entry.getDescription().getValue().toString());
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
			parser.parse(instream, handler, metadata, new ParseContext());
			//Remplacement de tous les charactères spéciaux par un vide
			text=handler.toString().replaceAll("[^\\d\\p{L}!#$€%&'`(),;:/@...]","");
			System.out.println("content: " + text);
			System.out.println();

			//TODO stockage des items dans un fichier JSON
			item=new Item(st, url.toString(), entry.getLink(), entry.getAuthor(), entry.getPublishedDate().toString(), entry.getDescription().getValue().toString(), hashString.toString(), lang, text);
			lists.addItem(item);
		} 
		try {
			FileOutputStream out=new FileOutputStream(new File("items.json"));
			objectMapper.writeValue(out, lists);
		}catch (JsonGenerationException f){
			f.printStackTrace();
		}catch (IOException f){
			f.printStackTrace();
		}
		String entre="";
		for(String i:lists.KeySet()){
			if(!Map.containsKey(i)){
				ratio++;
				Map.put(i, lists.getItem(i));
				entre+="Item d'ID: "+i+" ajouté\n       Titre: "+lists.getItem(i).getTitre()+"\n";
				Action.indexerRSS.IndexRSS(lists.getItem(i));
			}
		}
		entre+="Taux de mise à jour: "+(ratio/Map.size())*100+"%\n -----------------------------------\n\n";
		Interface.in.setText(entre);
		Action.indexerRSS.close();
		db.commit();
		db.close();
		ratio=0;
	} 

	/**
	 * Revisite du flux RSS
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 * @throws LangDetectException
	 * @throws SAXException
	 * @throws TikaException
	 */
	@SuppressWarnings("static-access")
	public void revisite() throws IllegalArgumentException, IOException, FeedException, LangDetectException, SAXException, TikaException{
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
}  