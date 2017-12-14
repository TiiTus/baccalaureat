package fr.ul.projet;

import java.util.logging.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Classe qui gère les importations de remplacements de notes suivant les sessions dans 
 * la table Remplacer
 * @author hansjulien
 *
 */
public class RemplacerImport {

	private static final Logger LOG = Logger.getLogger(EpreuveImport.class.getName());

	//attribut
	private String filename;


	//constructeur
	public RemplacerImport(String filename) {
		super();
		this.filename = filename;
	}

	/**
	 * chargement d'un fichier au format CSV
	 * dont la première ligne est le nom des champs
	 * @return CSVParser
	 * @throws IOException
	 */
	public CSVParser buildCVSParser() throws IOException {
		CSVParser res = null;
		Reader in;
		in = new FileReader(filename);
		CSVFormat csvf = CSVFormat.DEFAULT.withCommentMarker('#').withDelimiter(';');
		res = new CSVParser(in, csvf);
		return res;
	}	
	/**
	 * insérer ou mettre à jour un remplacement au niveau des idEpreuves entre première et seconde session
	 * @param idEp1
	 * @param idEp2
	 * @return res
	 */
	private boolean add(String idEp1, String idEp2) {
		boolean res = true;
		String sql = "";
		PreparedStatement ps = null;
		sql = "INSERT into remplacer(Epreuve_idEpreuve1,Epreuve_idEpreuve2)"
				+" VALUES(?,?)";
		try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, idEp1);
			ps.setString(2, idEp2);

			LOG.info(ps.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
		return res;
	}
	/**
	 * sauvegarde dans la base de données
	 * @param parser
	 * @return res
	 */
	public int updateDB(CSVParser parser) {
		int res = 0;
		DBManager.connect();
		for (CSVRecord item : parser) {
			String idEpreuve1 = item.get(0).trim();
			String idEpreuve2 = item.get(1).trim();

			//enregistrer
			if (add(idEpreuve1, idEpreuve2)){
				res++;
			}
		}
		DBManager.quit();
		return res;
	}

	//setters & getters
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

	//===========================================
	public static void main(String[] args) {
		//les paramètres
		String filename = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;

		//les options de la ligne de commande
		Options options = new Options();
		Option input = new Option("i", "input", true, "nom du fichier .csv contenant les remplacements");
		input.setRequired(true);
		options.addOption(input);
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
			if (line.hasOption("i")) {
				filename = line.getOptionValue("i");
			}
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
		RemplacerImport cl = new RemplacerImport(filename);
		DBManager.URI= dburi;
		DBManager.USER = dbuser;
		DBManager.PASSWORD = dbpwd;
		CSVParser p = null;
		try {
			p = cl.buildCVSParser();
			cl.updateDB(p);
			
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}

	}

}
