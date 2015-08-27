package POC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConfigSQL;
import jline.internal.Log;

public class GamesCollector {

	private Connection connexion;
	private String mysql_db;
	private int limit;
	private int offset;
	private boolean fileExists = false;

	public GamesCollector(String connexion, int limit, int offset) {
		this.mysql_db = connexion;
		this.limit = limit;
		this.offset = offset;

		File f = new File("lib/persist/games" + limit + "_" + offset + ".dat");
		if(f.exists() && !f.isDirectory()) {
			fileExists = true;
		}
	}

	ListGames getGames() throws SQLException, ClassNotFoundException {

		if(fileExists) {
			ListGames result = null;
			try {
				FileInputStream saveFile = new FileInputStream("lib/persist/games" + limit + "_" + offset + ".dat");
				ObjectInputStream in = new ObjectInputStream(saveFile);
				result = (ListGames) in.readObject();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		ConfigSQL config = new ConfigSQL(mysql_db);

		Class.forName(config.getDriver());
		connexion = DriverManager.getConnection(config.getUrl() + config.getDb() + "?user=" + config.getUser() + "&password=" + config.getPass() + "&rewriteBatchedStatements=true");
		connexion.setAutoCommit(true);

		ListGames games = new ListGames();
		PreparedStatement selectGames = connexion.prepareStatement("SELECT g.id, g.totalPlyCount, p1.name, g.whiteElo, p2.name, g.blackElo, g.movesUCI, o.nbMoves FROM Game g, Player p1, Player p2, Opening o WHERE o.id = g.ecoId AND g.id < 50000 AND g.whiteElo > 2500 AND g.blackElo > 2500 AND g.whiteId = p1.id AND g.blackId = p2.id LIMIT " + limit + " OFFSET " + offset);

		ResultSet rs = selectGames.executeQuery();
		Log.info("SELECT GAMES");
		while (rs.next()) {
			int id = rs.getInt(1);
			int totalPlyCount = rs.getInt(2);
			int totalPlyOpeningCount = rs.getInt(8);
			String white = rs.getString(3) + " (" + rs.getInt(4) + ")";
			String black = rs.getString(5) + " (" + rs.getInt(6) + ")";
			String movesUCI = rs.getString(7);
			Game game = new Game(id, totalPlyCount, totalPlyOpeningCount, white, black, movesUCI);
			game.addAll(addMoves(id, totalPlyCount));
			Log.info(id);
			games.put(id, game);
		}
		
		if(!fileExists) {
			try {
			      FileOutputStream saveFile = new FileOutputStream("lib/persist/games" + limit + "_" + offset + ".dat");
			      ObjectOutputStream out = new ObjectOutputStream(saveFile);
			      out.writeObject(games);
			      out.close();
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
		}

		return games;
	}

	private List<Move> addMoves(Integer idGame, int totalPlyCount) throws SQLException {

		PreparedStatement selectMoves = connexion.prepareStatement("SELECT Move.move, Move.halfMove, FEN.id, Move.halfMove, FEN.log FROM FEN, Move WHERE Move.idGame = '" + idGame + "' AND Move.idFEN = FEN.id ORDER BY Move.halfMove ASC LIMIT " + totalPlyCount);
		ResultSet rs = selectMoves.executeQuery();

		List<Move> moves = new ArrayList<Move>();

		while (rs.next()) {
			String log = rs.getString(5);
			if(log != null && !log.trim().isEmpty()) {
				Move move = new Move(rs.getString(1), Integer.valueOf(rs.getString(2)), rs.getString(3));
	
				String[] lines = log.split("\\.");
				for(int i = 0 ; i < lines.length ; i++) {
					if(lines[i].trim().startsWith("bestmove") || lines[i].trim().isEmpty() || lines[i].contains("/")) {
						// Do nothing
					} else if (lines[i].equalsIgnoreCase("info depth 0 score mate 0")) {
						move.add(new MoveDepth(true));
					} else if (lines[i].equalsIgnoreCase("info depth 0 score cp 0")) {
						move.add(new MoveDepth(false));
					} else {
						String[] split = lines[i].trim().split(" ");
						//Log.info(lines[i]);
						MoveDepth currentMove = new MoveDepth(Integer.valueOf(split[2]), Integer.valueOf(split[6]), Integer.valueOf(split[9]));
						if(split[8].equals("mate")) {
							currentMove.setMate(true);
						}
						split = lines[i].split(" pv ");
						currentMove.setMoves(split[1]);
						move.add(currentMove);
					}
				}
				moves.add(move);
			}
		}
		return moves;
	}

}
