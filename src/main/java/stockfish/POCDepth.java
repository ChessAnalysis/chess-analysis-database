package stockfish;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.io.Files;

import config.ConfigSQL;
import jline.internal.Log;

/**
 */
public class POCDepth {

	ListGames games;
	GamesCollector collector;
	private final int LIMIT = 10;
	private final int OFFSET = 0;
	private int DEPTH_MIN = 1;
	private int DEPTH_MAX = 20;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		new POCDepth();
	}

	public POCDepth() throws ClassNotFoundException, SQLException, IOException {

		collector = new GamesCollector("diverse", LIMIT, OFFSET);
		games = collector.getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();
		
		StringBuilder sb = new StringBuilder();
		sb.append("idGame;d1/d2;d2/d3;d3/d4;d4/d5;d5/d6;d6/d7;d7/d8;d8/d9;d9/d10;d10/d11;d11/d12;d12/d3;d13/d14;d14/d15;d15/d16;d16/d17;d17/d18;d18/d19;d19/d20\n");

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			sb.append(idGame + ";");
			for(int depth = DEPTH_MIN + 1; depth <= DEPTH_MAX; depth++) {
				Game game = games.get(idGame);
				Game game_d1 = game.getByDepth(depth-1);
				Game game_d2 = game.getByDepth(depth);
				int err = analyseDepth(idGame, game_d1, game_d2);
				sb.append(";" + err);
			}
			sb.append("\n");
		}
		
		Files.write(sb, new File("resources/comparativeDepth.csv"), Charset.defaultCharset());

		System.exit(0);
	}

	private int analyseDepth(int idGame, Game game_d1, Game game_d2) throws IOException {

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
		
		return erreurQuadratique;

	}

}


