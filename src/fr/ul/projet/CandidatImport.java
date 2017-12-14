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
 * Classe pour gérer l'import des candidats dans la base de données
 * @author hansjulien
 *
 */
public class CandidatImport {

	private static final Logger LOG = Logger.getLogger(EpreuveImport.class.getName());

	//attribut
	private String filename;


	//constructeur
	public CandidatImport(String filename) {
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
	 * insérer un candidat dans la base de données
	 * @param idCand
	 * @param pSerie
	 * @param pFilliere
	 * @param pSpecialite
	 * @param pSection
	 * @param pAnnee
	 * @return res (boolean)
	 */
	private boolean add(String idCand, String pSerie, String pFilliere, String pSpecialite, String pSection, String pAnnee) {
		boolean res = true;
		String sql = "";
		PreparedStatement ps = null;
		sql = "INSERT into candidat(idCandidat, Profil_serie, Profil_filliere, Profil_specialite, Profil_section, Profil_annee)"
				+" VALUES(?,?,?,?,?,?)";
		try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, idCand);
			ps.setString(2, pSerie);
			ps.setString(3, pFilliere);
			ps.setString(4, pSpecialite);
			ps.setString(5, pSection);
			ps.setString(6, pAnnee);


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
			String idCand = item.get(7).trim();
			String serie = item.get(3).trim();
			String filliere = item.get(4).trim();
			String spe = item.get(5).trim();
			String section = item.get(6).trim();
			String annee = item.get(1).trim();

		
			//enregistrer
			if (add(idCand, serie, filliere, spe, section, annee)){
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
		Option input = new Option("i", "input", true, "nom du fichier .csv contenant les données sur les candidats");
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
		CandidatImport ci = new CandidatImport(filename);
		DBManager.URI= dburi;
		DBManager.USER = dbuser;
		DBManager.PASSWORD = dbpwd;
		CSVParser p = null;
		try {
			p = ci.buildCVSParser();
			ci.updateDB(p);
			
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}

	}

}
