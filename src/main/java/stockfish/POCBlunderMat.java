package stockfish;

import java.io.IOException;
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

import config.ConfigSQL;
import jline.internal.Log;

/**
 */
public class POCBlunderMat {

	private Connection connexion;
	HashMap<Integer, Moves> games;
	private final int LIMIT = 1000;
	private final int OFFSET = 0;
	private final int DEPTH_MIN = 20;
	private final int DEPTH_MAX = 20;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		ConfigSQL connexion= new ConfigSQL("localhost");
		new POCBlunderMat(connexion);
	}

	public POCBlunderMat(ConfigSQL config) throws ClassNotFoundException, SQLException, IOException {

		Class.forName(config.getDriver());
		this.connexion = DriverManager.getConnection(config.getUrl() + config.getDb() + "?user=" + config.getUser() + "&password=" + config.getPass() + "&rewriteBatchedStatements=true");
		this.connexion.setAutoCommit(true);

		getGames();

		Set<Integer> keys = games.keySet();
		Log.info(keys);
		Iterator<Integer> itKeys = keys.iterator();

		while(itKeys.hasNext()) {
			Integer idGame = itKeys.next();
			for(int depth = DEPTH_MIN; depth <= DEPTH_MAX; depth++) {
				Moves moves = games.get(idGame);
				List<RowLog> scores = moves.getBestScores(depth);
				analyseBlunderMat(idGame, scores, depth);
			}
		}

		System.exit(0);
	}

	private void analyseBlunderMat(int idGame, List<RowLog> game, int depth) throws IOException {

		Iterator<RowLog> it = game.iterator();

		int ply = 0;

		while(it.hasNext()) {
			RowLog tmpRow = it.next();
			if (tmpRow.getScoreType().equals("mate")) {
				int totalMove = game.size()/2;
				int move = ply/2;
				if(totalMove > (move + Math.abs(tmpRow.getScoreResult()))) {
					Log.info("Mat oublié --> Move " + move  + "/" + totalMove + " - Mat en " + tmpRow.getScoreResult());
					Log.info();
				}
				
			}
			ply++;
		}

		//Log.info(sb.toString());

	}

	private void getGames() throws SQLException {
		games = new HashMap<Integer, Moves>();
		PreparedStatement selectGames = connexion.prepareStatement("SELECT id FROM Game LIMIT " + LIMIT + " OFFSET " + OFFSET);

		ResultSet rs = selectGames.executeQuery();
		Log.info("SELECT GAMES");
		while (rs.next()) {
			int id = rs.getInt(1);
			Log.info(id);
			games.put(id, getMoves(id));
		}
	}

	private Moves getMoves(Integer idGame) throws SQLException {
		PreparedStatement selectMoves = connexion.prepareStatement("SELECT Move.move, Move.halfMove, FEN.log FROM FEN, Move WHERE Move.idGame = '" + idGame + "' AND Move.idFEN = FEN.id ORDER BY Move.halfMove ASC");
		ResultSet rs = selectMoves.executeQuery();

		Moves game = new Moves();

		while (rs.next()) {
			int idMove = rs.getInt(2);
			String log = rs.getString(3);

			Move move = new Move(rs.getString(1));

			String[] lines = log.split("\\.");
			for(int i = 0 ; i < lines.length ; i++) {
				if(!lines[i].trim().isEmpty() && !lines[i].trim().startsWith("bestmove") && !lines[i].trim().contains("mate 0")) {
					StringTokenizer st = new StringTokenizer(lines[i].trim(), " ");
					int k=0;
					RowLog depth = new RowLog(rs.getString(1));
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


