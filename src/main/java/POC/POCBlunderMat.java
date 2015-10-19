package POC;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import jline.internal.Log;

/**
 */
public class POCBlunderMat {

	private static ListGames games;
	private static GamesCollector collector;
	private static final int LIMIT = 500;
	private static final int OFFSET = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		collector = new GamesCollector("local3", LIMIT, OFFSET);
		games = collector.getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			Game game = games.get(idGame);
			Game game_d20 = game.getByDepth(20);
			analyseBlunderMat(game_d20);
		}
	}

	static void analyseBlunderMat(Game game) throws IOException {

		Iterator<Move> it = game.iterator();
		int totalPlyCount = game.getTotalPlyCount();

		while(it.hasNext()) {
			Move currentMove = it.next();
			if(currentMove.size() > 0) {
				MoveDepth currentMoveDepth = currentMove.get(0);
				int currentPly = currentMove.getHalfMove();
				if (currentMoveDepth.isMate()) {
					if(totalPlyCount > (currentPly + (Math.abs(currentMoveDepth.getScore()))*2)) {
						Log.info("Move [" + (currentPly/2) + "/" + (totalPlyCount/2) +"] " + currentMove.getMove() + "\tMat en " + currentMoveDepth.getScore() + "\t" + currentMoveDepth.getMoves());
					}
				}
			}
		}
	}
}


