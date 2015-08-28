package POC;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import com.google.common.io.Files;

import jline.internal.Log;
import POC.POCStockfishFirstLine;

/**
 */
public class POCAll {

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
		
		StringBuilder sb = new StringBuilder();
		sb.append("idGame;d1/d2;d2/d3;d3/d4;d4/d5;d5/d6;d6/d7;d7/d8;d8/d9;d9/d10;d10/d11;d11/d12;d12/d3;d13/d14;d14/d15;d15/d16;d16/d17;d17/d18;d18/d19;d19/d20\n");

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			Game game = games.get(idGame);
			Game game_d20 = game.getByDepth(20);
			Log.info();
			Log.info("Analyse of Game #" + idGame + " : " + game.getWhite() + " VS " + game.getBlack());
			Log.info();
			POCStockfishFirstLine.analyseStockfishFirstLine(game_d20);
			Log.info();
			POCBlunderMat.analyseBlunderMat(game_d20);
			POCEvolutionScore.analyseEvolutionScore(game_d20);
			sb.append(idGame + POCDepth.analyseDepth(game) + "\n");
		}
		
		Files.write(sb, new File("resources/depth/comparativeDepth.csv"), Charset.defaultCharset());

		System.exit(0);
	}

	public POCAll() throws ClassNotFoundException, SQLException, IOException {
		
	}
	
	
}