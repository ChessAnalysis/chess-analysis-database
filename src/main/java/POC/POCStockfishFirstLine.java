package POC;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import jline.internal.Log;

/**
 */
public class POCStockfishFirstLine {

	ListGames games;
	GamesCollector collector;
	private final int LIMIT = 500;
	private final int OFFSET = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		new POCStockfishFirstLine();
	}

	public POCStockfishFirstLine() throws ClassNotFoundException, SQLException, IOException {
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
		String movesUCIALL = "{ ";
		String[] movesUCI = game.getMovesUCI().trim().split(" ");	
		int count = 0;
		CharSequence previous = "";
		int bestMoveWhite = 0;
		int bestMoveBlack = 0;
		int totalPlyOpeningCount = game.getTotalPlyOpeningCount();
		int totalPlyCount = game.getTotalPlyCount();
		int totalNonBookMove = (totalPlyCount-totalPlyOpeningCount)/2;
		
		Log.info("-----");
		
		
		while(count < totalPlyOpeningCount) {
			it.next();
			movesUCIALL += movesUCI[count] + " ";
			count++;
		}
		
		movesUCIALL += "} ";

		while(it.hasNext()) {
			Move currentMove = it.next();
			if(currentMove.size() > 0) {
				MoveDepth currentMoveDepth = currentMove.get(0);
				
				if(movesUCI[count].equals(previous)) {
					movesUCIALL += "*" + movesUCI[count] + "* ";
					if((count%2)==0) {
						bestMoveWhite++;
					} else {
						bestMoveBlack++;
					}
				} else {
					movesUCIALL += movesUCI[count] + " ";
				}
				if(currentMoveDepth.getMoves() != null) {
					previous = currentMoveDepth.getMoves().subSequence(0, 4);
				} else {
					previous = "";
				}
			} else {
				movesUCIALL += "----" + " ";
				previous = "";
			}
			count++;
		}
		
		Log.info(movesUCIALL);
		Log.info(game.getWhite() + " " + bestMoveWhite + "/" + totalNonBookMove + " --> " + ((bestMoveWhite*100)/totalNonBookMove) + "%");
		Log.info(game.getBlack() + " " + bestMoveBlack + "/" + totalNonBookMove + " --> " + ((bestMoveBlack*100)/totalNonBookMove) + "%");
	}
}


