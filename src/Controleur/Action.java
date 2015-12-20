package Controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import com.cybozu.labs.langdetect.LangDetectException;
import com.sun.syndication.io.FeedException;

import Modele.FeedParser;
import Modele.IndexerRSS;
import Vue.Interface;

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
	
	/**
	 * Mise en place des actions de l'interface
	 * @param event
	 */
	public void actionPerformed(ActionEvent event) {
		if(event.getSource()==Interface.valide){
			if(Interface.search.getText()!=null){
				try {
					Action.indexerRSS.close();
					indexerRSS.SearchIndexRSS(Interface.search.getText());
					indexerRSS.close();
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				}
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

}
