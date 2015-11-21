package Modele;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import Vue.Interface;

/**
 * @description Classe IndexerRSS qui permet de faire la recherche d'information
 * @author Richet Guillaume - Cadio Mathieu
 * @date 21/11/2015
 *
 */
public class IndexerRSS {

	private IndexWriter writer;
	private StandardAnalyzer analyzer=new StandardAnalyzer();

	/**
	 * Constructeur IndexerRSS
	 */
	public IndexerRSS(){
		Path path = Paths.get(new File("").getAbsolutePath()+"\\index");
		Directory directory=null;
		try {
			directory = FSDirectory.open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		IndexWriterConfig config=new IndexWriterConfig(analyzer);
		try {
			writer = new IndexWriter(directory, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stockage de tous les informations d'un item
	 * @param item
	 */
	public void IndexRSS(Item item){
		try{
			Document document=new Document();
			document.add(new TextField("Titre", item.getTitre(), Field.Store.YES));
			document.add(new TextField("URL Flux", item.getURLFlux(), Field.Store.YES));
			document.add(new TextField("URL Source", item.getURLSource(), Field.Store.YES));
			document.add(new TextField("Auteur", item.getAuteur(), Field.Store.YES));
			document.add(new TextField("Date", item.getDate(), Field.Store.YES));
			document.add(new TextField("Description", item.getDescription(), Field.Store.YES));
			document.add(new TextField("ID", item.getID(), Field.Store.YES));
			document.add(new TextField("Langue", item.getLangue(), Field.Store.YES));
			document.add(new TextField("Contenu", item.getContenu(), Field.Store.YES));
			writer.addDocument(document);
			System.out.println("Indexation de l'item d'id: "+item.getID());
		}catch(Exception e){
			System.err.println("Erreur d'indexation");
		}

	}

	/**
	 * Recherche des items comprenant les mots clés de la recherche
	 * @param search
	 * @throws IOException
	 * @throws ParseException
	 */
	public void SearchIndexRSS(String search) throws IOException, ParseException{
		Path path=Paths.get(new File("").getAbsolutePath()+"\\index");
		Directory directory=FSDirectory.open(path);
		IndexReader indexReader=DirectoryReader.open(directory);
		IndexSearcher indexSearcher=new IndexSearcher(indexReader);
		TopScoreDocCollector collector=TopScoreDocCollector.create(10);
		Query query=new MultiFieldQueryParser(new String[]{"Titre", "URL Flux", "URL Source", "Auteur", "ID", "Description", "Contenu", "Langue", "Date"}, analyzer).parse(search);
		indexSearcher.search(query, collector);
		ScoreDoc[] hits=collector.topDocs().scoreDocs;
		String sortie=hits.length+" résultat(s) trouvé(s):\n";
		for(int i=0; i<hits.length; i++){
			int docId=hits[i].doc;
			Document d=indexSearcher.doc(docId);
			sortie+="Item "+(i+1)+". Titre: "+d.get("Titre")+"\n        ID: "+d.get("ID")+"\n        Description: "+d.get("Description")+"\n        Auteur: "+d.get("Auteur")+"\n        Date: "+d.get("Date")+"\n        URL Source: "+d.get("URL Source")+"\n        URL Flux: "+d.get("URL Flux")+"\n        Langue: "+d.get("Langue")+"\n ------------------------------------------ \n";
		}
		Interface.out.setText(sortie);
	}

	/**
	 * Fermeture de l'écriture d'indexation
	 */
	public void close(){
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
