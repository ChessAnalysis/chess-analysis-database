import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.chess.AmbiguousChessMoveException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import POC.POCEvolutionScore;
import jline.console.ConsoleReader;
import jline.internal.Log;
import config.ConfigSQL;
import database.GenerateECOFromDatabase;
import database.GenerateFENFromDatabase;
import database.InsertECOToDatabase;
import database.InsertPGNToDatabase;
import database.UpdateFENFromFile;

/**
 */
public class MainDatabase {

	/**
	 * Method main.
	 * @param args String[]
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AmbiguousChessMoveException
	 * @throws IllegalMoveException
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, AmbiguousChessMoveException, IllegalMoveException {

		ConfigSQL connexion= new ConfigSQL("localhost");
		
		/*Log.info("[1] Insert Opening into database");
		new InsertECOToDatabase(connexion);*/
		
		/*Log.info("[2] Generate MoveECO from Opening table");
		new GenerateECOFromDatabase(connexion);*/
		
		/*Log.info("[3] Insert Game into database from pgn/games.pgn");
		String[] filesName;
		File[] files;

		filesName = new File("pgn").list();
		files = new File[filesName.length];
		for(int i = 0; i < filesName.length; i++) {
			files[i] = new File("pgn/" + filesName[i]);
		}
		for(File file: files) {
			if (!file.exists() || file.isHidden() || file.getName().equals(".DS_Store")) {
				Log.warn("Le fichier " + file.getName() + " n'existe pas.");
			} else {
				Log.info("> " + file.getAbsolutePath());
				new InsertPGNToDatabase(file.getAbsolutePath(), connexion);
			}
		} */
		
		/*Log.info("[4] Generate Move and FEN into database from Game table");
		new GenerateFENFromDatabase(connexion, 0);*/
		
		/* Log.info("[5] Analyse fen/output with Igrida and Stockfish chess engine"); */
		
		/* Log.info("[6] Add log analyse to FEN with mysql script"); */
		
	}
}