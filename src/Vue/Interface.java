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

import Controleur.Action;

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
	public static JMenuItem flux, temps;
	public static JTextArea in, out;
	private JScrollPane paneIn, paneOut;
	public static JButton valide;
	private JLabel textSearch;
	public static JTextField search;
	private Action action=new Action();

	/**
	 * Constructeur Interface
	 */
	public Interface(){
		creerInterface();
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
		flux=new JMenuItem("Flux RSS");
		temps=new JMenuItem("Temps rafraîchissement");
		flux.addActionListener(action);
		temps.addActionListener(action);
		menu.add(flux);
		menu.add(temps);
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
		in=new JTextArea();
		in.setEditable(false);
		paneIn=new JScrollPane(in);
		paneIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paneIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		bc.gridwidth=3;
		bc.gridx=0;
		bc.gridy=0;
		add(paneIn, bc);

		//Zone de recherche
		textSearch=new JLabel("Recherche: ");
		bc.gridwidth=1;
		bc.gridx=0;
		bc.gridy=1;
		add(textSearch, bc);

		search=new JTextField();
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
