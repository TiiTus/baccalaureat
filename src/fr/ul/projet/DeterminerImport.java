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
 * Classe qui gère l'import des données dans la table Determiner
 * @author hansjulien
 *
 */
public class DeterminerImport {

	private static final Logger LOG = Logger.getLogger(EpreuveImport.class.getName());

	//attribut
	private String filename;


	//constructeur
	public DeterminerImport(String filename) {
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
	 * insérer les données dans la table Determiner
	 * @param Profil_serie
	 * @param Profil_filliere
	 * @param Profil_specialite
	 * @param Profil_section
	 * @param Profil_annee
	 * @param Epreuve_idEpreuve
	 * @param coefficient
	 * @param composition
	 * @param bonus
	 * @param facultatif
	 * @return res (boolean)
	 */
	private boolean add(String pSerie, String pFilliere, String pSpecialite, String pSection, String pAnnee, String idEp, String coeff,
			String compo, String bonus, String facultatif) {
		boolean res = true;
		String sql = "";
		PreparedStatement ps = null;
		sql = "INSERT into determiner(Profil_serie, Profil_filliere, Profil_specialite, Profil_section, Profil_annee,"
				+ "Epreuve_idEpreuve, coefficient, composition, bonus, facultatif)"
				+" VALUES(?,?,?,?,?,?,?,?,?,?)";
		try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, pSerie);
			ps.setString(2, pFilliere);
			ps.setString(3, pSpecialite);
			ps.setString(4, pSection);
			ps.setString(5, pAnnee);
			ps.setString(6, idEp);
			ps.setString(7, coeff);
			ps.setString(8, compo);
			ps.setString(9, bonus);
			ps.setString(10, facultatif);

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
	 * @return res (boolean)
	 */
	public int updateDB(CSVParser parser) {
		int res = 0;
		DBManager.connect();
		for (CSVRecord item : parser) {
			String serie = item.get(0).trim();
			String filliere = item.get(1).trim();
			String spe = item.get(2).trim();
			String section = item.get(3).trim();
			String annee = item.get(10).trim();
			String idEp = item.get(4).trim();
			String coeff = item.get(6).trim();
			String compo = item.get(7).trim();
			String bonus = item.get(8).trim();
			String fac = item.get(9).trim();
		
			//enregistrer
			if (add(serie, filliere, spe, section, annee, idEp, coeff, compo, bonus, fac)){
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
		Option input = new Option("i", "input", true, "nom du fichier .csv contenant les données à importer");
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
		DeterminerImport cl = new DeterminerImport(filename);
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
