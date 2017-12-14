package fr.ul.projet;

import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Classe pour gérer la suppression d'un candidat par rapport à son identifiant
 * identifiant à mettre dans le fichier suppCandidat.sh (ou .bat) dans le dossier /bin
 * dans la section paramètres de la commande, option -i
 * @author hansjulien
 *
 */
public class CandidatDelete {

	private static final Logger LOG = Logger.getLogger(EpreuveImport.class.getName());


	//constructeur
	public CandidatDelete() {
		super();
	}

	/**
	 * supprimer un candidat selon son identifiant
	 * @param id_cand
	 * @return res (boolean)
	 */
	private boolean delete(String idCandidat) {
		boolean res = true;
		String sql = "";
		String sql2 = "";
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		sql = "DELETE from resultat WHERE Candidat_idCandidat=?";
		sql2 = "DELETE from candidat WHERE idCandidat=?";
		try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps2 = DBManager.CONNECTION.prepareStatement(sql2);
			ps.setString(1, idCandidat);
			ps2.setString(1, idCandidat);


			LOG.info(ps.toString());
			LOG.info(ps2.toString());
			ps.executeUpdate();
			ps2.executeUpdate();
			
			System.out.println("Le candidat n° : " + idCandidat + " à bien été supprimé de la base de données");
			
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
		return res;
	}
	/**
	 * mettre à jour la base de données
	 * @param idCand
	 */
	public void updateDB(String idCand) {
		DBManager.connect();
		delete(idCand);	
		DBManager.quit();

	}

	//===========================================
	public static void main(String[] args) {
		//les paramètres
		String idc = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;

		//les options de la ligne de commande
		Options options = new Options();
		Option input = new Option("i", "identifiantCandidat", true, "identifiant du candidat à supprimer");
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
				idc = line.getOptionValue("i");
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
		CandidatDelete cl = new CandidatDelete();
		DBManager.URI= dburi;
		DBManager.USER = dbuser;
		DBManager.PASSWORD = dbpwd;
		cl.updateDB(idc);
	}

}
