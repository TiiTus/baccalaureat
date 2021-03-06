/*
 *  License and Copyright:
 *  This file is part of "exercice" project.
 *
 *   It is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   It is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Copyright 2017 by LORIA, Université de Lorraine
 *  All right reserved
 */
/**
 * Projet : exercice
 * Classe de chargement d'une liste d'épreuve au format CSV
 * et de stockage dans la base de donnée
  * @author Azim Roussanaly
  * sept 2017 
*/
package fr.ul.projet;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

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
 * Classe qui gère l'importation des données sur les epreuves dans la table Epreuve
 * @author hansjulien
 *
 */
public class EpreuveImport {
    private static final Logger LOG = Logger.getLogger(EpreuveImport.class.getName());
	
    //attribut
	private String filename;
	

	//constructeur
	public EpreuveImport(String filename) {
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
	 * insérer ou mettre à jour une épreuve
	 * @param idEpreuve
	 * @param libelle
	 * @param session
	 * @return res (boolean)
	 */
	private boolean add(String idEpreuve, String libelle, String session) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "INSERT into epreuve(idEpreuve,libelle,session)"
        		+" VALUES(?,?,?)"
        		+" ON DUPLICATE KEY UPDATE"
        		+" libelle=?, session=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, idEpreuve);
	        ps.setString(2, libelle);
	        ps.setString(3, session);
	        ps.setString(4, libelle);
	        ps.setString(5, session);
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
			String idEpreuve = item.get(0).trim();
			String libelle = item.get(1).trim();
			String session = item.get(2).trim();

			//enregistrer
			if (add(idEpreuve, libelle, session)){
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
		Option input = new Option("i", "input", true, "nom du fichier .csv contenant les epreuves");
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
	    EpreuveImport el = new EpreuveImport(filename);
	    
	    DBManager.URI= dburi;
	    DBManager.USER = dbuser;
	    DBManager.PASSWORD = dbpwd;
	    CSVParser p = null;
	    try {
	    	p = el.buildCVSParser();
	    	el.updateDB(p);
			
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}

	}

}
