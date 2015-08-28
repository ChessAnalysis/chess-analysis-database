package POC;

import java.io.IOException;
import java.util.Iterator;

import jline.internal.Log;

/**
 */
public class POCDepth {

	private static int DEPTH_MIN = 1;
	private static int DEPTH_MAX = 20;
	
	static String analyseDepth(Game game) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(int depth = DEPTH_MIN + 1; depth <= DEPTH_MAX; depth++) {
			Game game_d1 = game.getByDepth(depth-1);
			Game game_d2 = game.getByDepth(depth);
			int err = analyseDepth(game_d1, game_d2);
			sb.append(";" + err);
		}
		return sb.toString();
	}

	static int analyseDepth(Game game_d1, Game game_d2) throws IOException {

		Iterator<Move> it1 = game_d1.iterator();
		Iterator<Move> it2 = game_d2.iterator();
		
		int erreurQuadratique = 0;

		while(it1.hasNext()) {
			Move currentMove1 = it1.next();
			Move currentMove2 = it2.next();
			
			if(currentMove1.size() > 0 && currentMove2.size() > 0) {

				MoveDepth currentMoveDepth1 = currentMove1.get(0);
				MoveDepth currentMoveDepth2 = currentMove2.get(0);
				
				if (!currentMoveDepth1.isMate() && !currentMoveDepth2.isMate()) {
					erreurQuadratique += Math.pow((currentMoveDepth1.getScore() - currentMoveDepth2.getScore()), 2);
				}
			}
		}
		
		Log.info(erreurQuadratique);
		
		return erreurQuadratique;

	}

}


