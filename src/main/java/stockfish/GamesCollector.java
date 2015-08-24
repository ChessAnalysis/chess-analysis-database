package stockfish;

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
		PreparedStatement selectGames = connexion.prepareStatement("SELECT id FROM Game LIMIT " + limit + " OFFSET " + offset);

		ResultSet rs = selectGames.executeQuery();
		Log.info("SELECT GAMES");
		while (rs.next()) {
			int id = rs.getInt(1);
			Log.info(id);
			games.put(id, getMoves(id));
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

	private Game getMoves(Integer idGame) throws SQLException {
		java.sql.Statement nbPly = connexion.createStatement();
		ResultSet rs1 = nbPly.executeQuery("SELECT totalPlyCount FROM Game WHERE id = '" + idGame + "'");
		rs1.next();
		int totalPlyCount = rs1.getInt(1);

		PreparedStatement selectMoves = connexion.prepareStatement("SELECT Move.move, Move.halfMove, FEN.id, Move.halfMove, FEN.log FROM FEN, Move WHERE Move.idGame = '" + idGame + "' AND Move.idFEN = FEN.id ORDER BY Move.halfMove ASC LIMIT " + totalPlyCount);
		ResultSet rs = selectMoves.executeQuery();

		Game game = new Game(idGame, totalPlyCount);

		while (rs.next()) {
			String log = rs.getString(5);
			Move move = new Move(rs.getString(1), Integer.valueOf(rs.getString(2)), rs.getString(3));

			String[] lines = log.split("\\.");
			for(int i = 0 ; i < lines.length ; i++) {
				if(lines[i].trim().startsWith("bestmove") || lines[i].trim().isEmpty() || lines[i].contains("/")) {
					// Do nothing
				} else if (lines[i].equalsIgnoreCase("info depth 0 score mate 0")) {
					move.add(new MoveDepth(true));
				} else {
					String[] split = lines[i].trim().split(" ");
					MoveDepth currentMove = new MoveDepth(Integer.valueOf(split[2]), Integer.valueOf(split[6]), Integer.valueOf(split[9]));
					if(split[8].equals("mate")) {
						currentMove.setMate(true);
					}
					split = lines[i].split(" pv ");
					currentMove.setMoves(split[1]);
					move.add(currentMove);
				}
			}
			game.add(move);
		}
		return game;
	}

}
