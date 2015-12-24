package Vue;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import Controleur.Action;
import Modele.FeedParser;

/**
 * @description Classe Interface
 * @author Richet Guillaume - Cadio Mathieu
 * @date 21/11/2015
 *
 */
@SuppressWarnings("serial")
public class Interface extends JFrame{

	private JMenuBar menuBar;
	private JMenu menu;
	public static JMenuItem flux, temps, filtre;
	public static JTextArea in, out;
	private JScrollPane paneIn, paneOut;
	public static JButton valide;
	private JLabel textSearch;
	public static JTextField search;
	private Action action=new Action();
	public static FeedParser feedParser;

	/**
	 * Constructeur Interface
	 */
	public Interface(){
		creerInterface();
		feedParser=new FeedParser();
		menu.setEnabled(true);
		search.setEditable(true);
		valide.setEnabled(true);
	}

	/**
	 * Mise en place de l'interface
	 */
	public void creerInterface(){
		setLayout(new GridBagLayout());
		setExtendedState(MAXIMIZED_BOTH);
		setTitle("Architectures des systèmes de recherche et filtrage de l'information");
		setBackground(Color.WHITE);

		//Mise en place de la barre de menu
		menuBar=new JMenuBar();
		menu=new JMenu("Opérations");
		menu.setEnabled(false);
		flux=new JMenuItem("Flux RSS");
		filtre=new JMenuItem("Filtres");
		flux.addActionListener(action);
		filtre.addActionListener(action);
		menu.add(flux);
		menu.add(filtre);
		menuBar.add(menu);
		setJMenuBar(menuBar);

		//Mise en place du layout
		GridBagConstraints bc=new GridBagConstraints();
		bc.fill=GridBagConstraints.BOTH; 
		bc.insets=new Insets(15, 15, 15, 15); //marge
		bc.anchor = bc.ipady = GridBagConstraints.CENTER;
		bc.weightx=3;
		bc.weighty=3; 

		//Zone de texte d'ajout des items
		in=new JTextArea("Veuillez patienter quelques minutes que la base de donnée se mettent à jour avec les flux suivant:\n"
				+ "http://rmcsport.bfmtv.com/rss/basket/ Sport Fr \n"
				+ "http://www.thetimes.co.uk/tto/sport/rss Sport En \n"
				+ "http://www.lemonde.fr/sante/rss_full.xml Sante Fr \n"
				+ "http://www.thetimes.co.uk/tto/health/rss Sante En \n"
				+ "http://www.thetimes.co.uk/tto/science/rss Science En \n"
				+ "http://www.lemonde.fr/sciences/rss_full.xml Science Fr \n"
				+ "http://feeds.feedburner.com/cinemablendallthing Cinema En \n"
				+ "http://rss.allocine.fr/ac/actualites/cine Cinema Fr \n"
				+ "http://www.lesechos.fr/rss/rss_articles_journal.xml Economie Fr \n"
				+ "http://www.thetimes.co.uk/tto/business/rss Economie En");
		in.setEditable(false);
		in.setRows(15);
		paneIn=new JScrollPane(in);
		paneIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paneIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		bc.gridwidth=3;
		bc.gridx=0;
		bc.gridy=0;
		add(paneIn, bc);

		//Zone de recherche
		textSearch=new JLabel("Recherche: ");
		textSearch.setHorizontalAlignment(SwingConstants.RIGHT);
		bc.gridwidth=1;
		bc.gridx=0;
		bc.gridy=1;
		add(textSearch, bc);

		search=new JTextField();
		search.setEditable(false);
		bc.gridx=1;
		bc.gridy=1;
		add(search, bc);

		valide=new JButton("Valider");
		valide.setEnabled(false);
		valide.addActionListener(action);
		bc.gridx=2;
		bc.gridy=1;
		add(valide, bc);

		//Zone de texte de sortie de recherche des items
		out=new JTextArea();
		out.setEditable(false);
		out.setRows(15);
		paneOut=new JScrollPane(out);
		paneOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paneOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		bc.gridwidth=3;
		bc.gridx=0;
		bc.gridy=2;
		add(paneOut, bc);

		setVisible(true);
	}
	
	/**
	 * Lancement de l'application
	 * @param agrs
	 */
	public static void main(String[] agrs){
		@SuppressWarnings("unused")
		Interface inter=new Interface();
	}
}
