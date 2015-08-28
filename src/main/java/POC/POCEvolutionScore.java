package POC;

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

import org.rosuda.JRI.Rengine;

import com.google.common.io.Files;

import config.ConfigSQL;
import jline.internal.Log;

/**
 */
public class POCEvolutionScore {

	private static ListGames games;
	private static GamesCollector collector;
	private static final int LIMIT = 10;
	private static final int OFFSET = 0;

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		collector = new GamesCollector("diverse", LIMIT, OFFSET);
		games = collector.getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			for(int depth = 10; depth <= 20; depth++) {
				Game game = games.get(idGame);
				Game game_d = game.getByDepth(depth);
				analyseEvolutionScore(game_d);
			}
		}
	}

	static void analyseEvolutionScore(Game game) throws IOException {

		String SEPARATOR = "\t";
		SEPARATOR = ",";

		StringBuilder sb = new StringBuilder("Ply" + SEPARATOR + "Move" + SEPARATOR + "White" + SEPARATOR + "Black" + SEPARATOR + "Score" + SEPARATOR + "Eval" + SEPARATOR + "Comment\n");
		Iterator<Move> it = game.iterator();

		int count = 0;
		Integer previous = 0;

		while(it.hasNext()) {
			Move currentMove = it.next();
			if(currentMove.size() > 0) {
				MoveDepth currentMoveDepth = currentMove.get(0);
				if (currentMoveDepth.isMate()) {
					if((count%2)==0)
						sb.append(count + SEPARATOR + "[" + ((count/2)+1) + "]" + SEPARATOR + currentMove.getMove() + SEPARATOR + SEPARATOR + SEPARATOR + SEPARATOR + "#" + currentMoveDepth.getScore() + "#\n");
					else
						sb.append(count + SEPARATOR + "[" + ((count/2)+1) + "]" + SEPARATOR + SEPARATOR + currentMove.getMove() + SEPARATOR + SEPARATOR + SEPARATOR + "#" + currentMoveDepth.getScore() + "#\n");
					previous = 0;
				} else {

					/* Compute the gain gk for each move k. Let sk−1 and sk be the position evaluation before and after move
					k, respectively. If the move was played by white, compute the gain as gk = sk − sk−1; if the move was
					played by black, compute the gain as gk = −(sk − sk−1). */

					Integer eval = getEval(count, currentMoveDepth.getScore());
					Integer gain = eval - previous;
					previous = eval;
					String comment = getComment(gain);

					if((count%2)==0) {
						sb.append(count + SEPARATOR + "[" + ((count/2)+1) + "]" + SEPARATOR + currentMove.getMove() + SEPARATOR + SEPARATOR + Double.valueOf(eval)/100 + SEPARATOR + Double.valueOf(gain)/100 + SEPARATOR + comment + "\n");
					} else {
						sb.append(count + SEPARATOR + "[" + ((count/2)+1) + "]" + SEPARATOR + SEPARATOR + currentMove.getMove() + SEPARATOR + Double.valueOf(eval)/100 + SEPARATOR + Double.valueOf(-gain)/100 + SEPARATOR + comment + "\n");
					}
				}
			}
			count++;
		}

		Log.info(sb.toString());
		
		String filename = "analyse_" + game.getIdGame();
		Files.write(sb, new File("resources/evolutionScore/"+filename+".csv"), Charset.defaultCharset());

		Rengine re = Rengine.getMainEngine();
		if(re == null)
			re = new Rengine(new String[] {"--vanilla"}, false, null);

		if (!re.waitForR()) {
			Log.error("Cannot load R");
			return;
		}

		re.eval("library(ggplot2)");
		re.eval("library(plyr)");

		re.eval("evaluations = read.csv(\"/Users/fesnault/git/chess-analysis-database/resources/evolutionScore/"+filename+".csv\", head=TRUE, sep=\",\")");
		re.eval("df1 <- data.frame(evaluations$Ply, evaluations$Score)");
		re.eval("p1 = ggplot(df1, aes(evaluations$Ply, evaluations$Score)) + geom_line() + ggtitle(\"Game " + game.getIdGame() + "\")");
		re.eval("ggsave(p1, file=\"/Users/fesnault/git/chess-analysis-database/resources/evolutionScore/"+filename+".png\", width=10, height=10)");

	}

	private static Integer getEval(int count, int scoreResult) {
		if((count%2)==0) {
			return -Integer.valueOf(scoreResult);
		} else {
			return Integer.valueOf(scoreResult);
		}
	}

	private static String getComment(Integer gain) {
		if(gain<-300) {
			return "Gaffe";
		} else if(gain<-100) {
			return "Erreur";
		}
		return "";
	}

}


