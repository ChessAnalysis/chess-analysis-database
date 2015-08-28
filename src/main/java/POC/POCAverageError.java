package POC;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

import jline.internal.Log;

/**
 */
public class POCAverageError {

	private static ListGames games;
	private static GamesCollector collector;
	private static final int LIMIT = 500;
	private static final int OFFSET = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		collector = new GamesCollector("diverse", LIMIT, OFFSET);
		games = collector.getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			Game game = games.get(idGame);
			Game game_d = game.getByDepth(20);
			analyseAverageError(game_d);
		}
	}

	static void analyseAverageError(Game game) throws IOException {

		Iterator<Move> it = game.iterator();
		
		int totalPlyCount = game.getTotalPlyCount();
		
		int errorWhite = 0;
		int errorBlack = 0;

		int count = 0;
		Integer previous = 0;

		while(it.hasNext()) {
			Move currentMove = it.next();
			if(currentMove.size() > 0) {
				MoveDepth currentMoveDepth = currentMove.get(0);
				if (currentMoveDepth.isMate()) {
					previous = 0;
				} else {

					/* Compute the gain gk for each move k. Let sk−1 and sk be the position evaluation before and after move
					k, respectively. If the move was played by white, compute the gain as gk = sk − sk−1; if the move was
					played by black, compute the gain as gk = −(sk − sk−1). */

					Integer eval = getEval(count, currentMoveDepth.getScore());
					Integer gain = eval - previous;
					previous = eval;

					if((count%2)==0) {
						errorWhite += Math.abs(gain);
						} else {
						errorBlack += Math.abs(gain);
					}
				}
			}
			count++;
		}

		double aeWhite = (errorWhite/(totalPlyCount/2));
		double aeBlack = (errorWhite/(totalPlyCount/2));
		Log.info(game.getWhite() + "\t" + aeWhite/100);
		Log.info(game.getBlack() + "\t" + aeBlack/100);
		
	}

	private static Integer getEval(int count, int scoreResult) {
		if((count%2)==0) {
			return -Integer.valueOf(scoreResult);
		} else {
			return Integer.valueOf(scoreResult);
		}
	}

}


