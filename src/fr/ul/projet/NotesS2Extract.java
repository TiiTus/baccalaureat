package fr.ul.projet;

import java.util.Scanner;
import java.util.logging.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Classe pour gérer l'extraction des notes de la seconde session des candidats
 * selon leur profil
 * 	- la série
 * 	- la fillière
 * 	- la spécialité
 * 	- la section
 * @author hansjulien
 *
 */
public class NotesS2Extract {

	private static final Logger LOG = Logger.getLogger(NotesS2Extract.class.getName());


	//constructeur
	public NotesS2Extract() {
		super();
	}

	/**
	 * extraction des notes de la seconde session
	 * @param serie
	 * @param filliere
	 * @param specialite
	 * @param section
	 * @return res (boolean)
	 */
	private boolean export(String serie, String filliere, String specialite, String section) {
		boolean res = true;
		String sql = "";
		String filename = "../samples/export/notes_session2.csv";
		PreparedStatement ps = null;
		try {
			FileWriter fw = new FileWriter(filename);
			sql = "select libelle, Epreuve_idEpreuve, idCandidat, session, note"
					+ " from Resultat inner join Epreuve inner join Candidat"
					+ " where session = 2"
					+ " and Candidat.Profil_serie = '" + serie + "'"
					+ " and Candidat.Profil_filliere = '" + filliere + "'"
					+ " and Candidat.Profil_specialite = '" +specialite+ "'"
					+ " and Candidat.Profil_section = '" + section + "'"
					+ " and Epreuve.idEpreuve = Resultat.Epreuve_idEpreuve"
					+ " and Candidat.idCandidat = Resultat.Candidat_idCandidat;";
			 
			ResultSet rs;
			try {
				ps = DBManager.CONNECTION.prepareStatement(sql);
				rs = ps.executeQuery(sql);
			
				fw.write("#libelle;idEpreuve;idCandidat;session;note\n");
				
				while (rs.next()) {
				        fw.append(rs.getString(1));
				        fw.append(';');
				        fw.append(rs.getString(2));
				        fw.append(';');
				        fw.append(rs.getString(3));
				        fw.append(';');
				        fw.append(rs.getString(4));
				        fw.append(';');
				        fw.append(rs.getString(5));
				        fw.append('\n');
				       }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	            fw.flush();
	            fw.close();
	            System.out.println("Données enregistrées dans /samples/export/notes_session2.csv");

		} catch (IOException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
		return res;
	}
	/**
	 * executer l'extraction des données depuis la base de données
	 * @param idCand
	 */
	public void updateDB(String serie, String filliere, String specialite, String section) {
		DBManager.connect();
		export(serie, filliere, specialite, section);	
		DBManager.quit();

	}

	//===========================================
	public static void main(String[] args) {
		//les paramètres
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;

		//les options de la ligne de commande
		Options options = new Options();
		
		Option uri = new Option("r", "uri", true, "uri de la bd");
		uri.setRequired(true);
		options.addOption(uri);
		Option user = new Option("u", "user", true, "nom utilisateur");
		user.setRequired(true);
		options.addOption(user);
		Option pwd = new Option("p", "password", true, "mot de passe utilisateur");
		pwd.setRequired(true);
		options.addOption(pwd);

		//parser la ligne de commande
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse( options, args );
			if (line.hasOption("r")) {
				dburi = line.getOptionValue("r");
			}
			if (line.hasOption("u")) {
				dbuser = line.getOptionValue("u");
			}
			if (line.hasOption("p")) {
				dbpwd = line.getOptionValue("p");
			}
		}
		catch( ParseException exp ) {
			LOG.severe("Erreur dans la ligne de commande");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "epreuveloader", options );
			System.exit(1);
		}
		//traitement
		NotesS2Extract ns2e = new NotesS2Extract();
		DBManager.URI= dburi;
		DBManager.USER = dbuser;
		DBManager.PASSWORD = dbpwd;
		
		//Entrée des choix de l'utilisateur pour extraire les notes
		//en fonction d'une serie, d'une fillière, d'une spécialité et d'une section
		Scanner sc = new Scanner(System.in);
		System.out.println("Veuillez saisir une série :");
		String serie = sc.nextLine();
		System.out.println("Veuillez saisir une fillière :");
		String filliere = sc.nextLine();
		System.out.println("Veuillez saisir une spécialité :");
		String spe = sc.nextLine();
		System.out.println("Veuillez saisir une section :");
		String section = sc.nextLine();
		sc.close();
		
		ns2e.updateDB(serie, filliere, spe, section);
	}

}
