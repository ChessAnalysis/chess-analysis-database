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

	private Connection connexion;
	HashMap<Integer, Game> games;
	private final int LIMIT = 1000;
	private final int OFFSET = 0;
	private final int DEPTH_MIN = 1;
	private final int DEPTH_MAX = 20;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		ConfigSQL connexion= new ConfigSQL("localhost");
		new POCDepth(connexion);
	}

	public POCDepth(ConfigSQL config) throws ClassNotFoundException, SQLException, IOException {

		Class.forName(config.getDriver());
		this.connexion = DriverManager.getConnection(config.getUrl() + config.getDb() + "?user=" + config.getUser() + "&password=" + config.getPass() + "&rewriteBatchedStatements=true");
		this.connexion.setAutoCommit(true);

		getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();
		
		StringBuilder sb = new StringBuilder();
		sb.append("idGame;d1/d2;d2/d3;d3/d4;d4/d5;d5/d6;d6/d7;d7/d8;d8/d9;d9/d10;d10/d11;d11/d12;d12/d3;d13/d14;d14/d15;d15/d16;d16/d17;d17/d18;d18/d19;d19/d20\n");

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			sb.append(idGame);
			for(int depth = DEPTH_MIN + 1; depth <= DEPTH_MAX; depth++) {
				Game moves = games.get(idGame);
				List<MoveDepth> depth1 = moves.getMoves(depth-1);
				List<MoveDepth> depth2 = moves.getMoves(depth);
				int err = analyseDepth(idGame, depth1, depth2);
				sb.append(";" + err);
			}
			sb.append("\n");
		}
		Files.write(sb, new File("resources/comparativeDepth.csv"), Charset.defaultCharset());

		System.exit(0);
	}

	private int analyseDepth(int idGame, List<MoveDepth> gameD1, List<MoveDepth> gameD2) throws IOException {

		Iterator<MoveDepth> it1 = gameD1.iterator();
		Iterator<MoveDepth> it2 = gameD2.iterator();
		
		int erreurQuadratique = 0;

		while(it1.hasNext()) {
			MoveDepth tmpRow1 = it1.next();
			MoveDepth tmpRow2 = it2.next();
			
			if (!tmpRow1.getScoreType().equals("mate") && !tmpRow2.getScoreType().equals("mate")) {
				erreurQuadratique += Math.pow((tmpRow1.getScoreResult() - tmpRow2.getScoreResult()), 2);
			}
		}
		
		return erreurQuadratique;

	}

	private void getGames() throws SQLException {
		games = new HashMap<Integer, Game>();
		PreparedStatement selectGames = connexion.prepareStatement("SELECT id FROM Game LIMIT " + LIMIT + " OFFSET " + OFFSET);

		ResultSet rs = selectGames.executeQuery();
		Log.info("SELECT GAMES");
		while (rs.next()) {
			int id = rs.getInt(1);
			Log.info(id);
			games.put(id, getMoves(id));
		}
	}

	private Game getMoves(Integer idGame) throws SQLException {
		PreparedStatement selectMoves = connexion.prepareStatement("SELECT Move.move, Move.halfMove, FEN.log FROM FEN, Move WHERE Move.idGame = '" + idGame + "' AND Move.idFEN = FEN.id ORDER BY Move.halfMove ASC");
		ResultSet rs = selectMoves.executeQuery();

		Game game = new Game();

		while (rs.next()) {
			int idMove = rs.getInt(2);
			String log = rs.getString(3);

			Move move = new Move(rs.getString(1));

			String[] lines = log.split("\\.");
			for(int i = 0 ; i < lines.length ; i++) {
				if(!lines[i].trim().isEmpty() && !lines[i].trim().startsWith("bestmove") && !lines[i].trim().contains("mate 0")) {
					StringTokenizer st = new StringTokenizer(lines[i].trim(), " ");
					int k=0;
					MoveDepth depth = new MoveDepth(rs.getString(1));
					while(st.hasMoreTokens()) {
						String t = st.nextToken();
						switch(k) {
						case 2 : depth.setDepth(Integer.valueOf(t)); break;
						case 4 : depth.setSeldepth(Integer.valueOf(t)); break;
						case 6 : depth.setMultipv(Integer.valueOf(t)); break;
						case 8 : depth.setScoreType(t); break;
						case 9 : depth.setScoreResult(Integer.valueOf(t)); break;
						}
						k++;
					}
					move.add(depth);
				}
			}
			game.add(move);
		}
		return game;
	}

}


