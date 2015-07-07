import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.chess.AmbiguousChessMoveException;

import java.io.IOException;
import java.sql.SQLException;

import jline.internal.Log;

import com.beust.jcommander.JCommander;

import stockfish.StockfishAnalyze;

/**
 */
public class MainIgrida {

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
		if(args.length > 0) {
			try {
				StockfishAnalyze proc = new StockfishAnalyze();
				new JCommander(proc, args);
				proc.init();
			} catch (Exception e) {
				Log.info(e);
				Log.info("USAGE : java -jar {file-name.jar} -i {input} -pv {multipv} -d {depth} -t {threads}");
			}
		}
	}
}