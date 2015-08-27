package stockfish;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import jline.internal.Log;

/**
 */
public class POCPercentageBestMove {

	ListGames games;
	GamesCollector collector;
	private final int LIMIT = 50;
	private final int OFFSET = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		new POCPercentageBestMove();
	}

	public POCPercentageBestMove() throws ClassNotFoundException, SQLException, IOException {
		collector = new GamesCollector("diverse", LIMIT, OFFSET);
		games = collector.getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			Game game = games.get(idGame);
			Game game_d20 = game.getByDepth(20);
			analyseBlunderMat(idGame, game_d20, 20);
		}

		System.exit(0);
	}

	private void analyseBlunderMat(int idGame, Game game, int depth) throws IOException {

		Iterator<Move> it = game.iterator();
		int totalPlyCount = game.getTotalPlyCount();

		while(it.hasNext()) {
			Move currentMove = it.next();
			if(currentMove.size() > 0) {
				MoveDepth currentMoveDepth = currentMove.get(0);
				int currentPly = currentMove.getHalfMove();
				if (currentMoveDepth.isMate()) {
					if(totalPlyCount > (currentPly + (Math.abs(currentMoveDepth.getScore()))*2)) {
						Log.info("Game " + game.getIdGame() + " en " + game.getTotalPlyCount() + " ply\n"
								+ "Mat en #" + currentMoveDepth.getScore()*2 + " ply au coup  " + currentPly + "\n"
										+ "[" + currentMove.getFEN()  + "] " + currentMoveDepth.getMoves());
					}
				}
			}
		}
	}
}


