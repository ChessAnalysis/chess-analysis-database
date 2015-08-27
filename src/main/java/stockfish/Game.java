package stockfish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jline.internal.Log;

public class Game extends ArrayList<Move> implements Serializable {
	
	private int idGame;
	private int totalPlyCount;
	private String white, black;
	
	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	public int getIdGame() {
		return idGame;
	}

	public void setIdGame(int idGame) {
		this.idGame = idGame;
	}

	public int getTotalPlyCount() {
		return totalPlyCount;
	}

	public void setTotalPlyCount(int totalPlyCount) {
		this.totalPlyCount = totalPlyCount;
	}
	
	public Game(int idGame, int totalPlyCount, String white, String black) {
		super();
		this.idGame = idGame;
		this.totalPlyCount = totalPlyCount;
		this.white = white;
		this.black = black;
	}

	public List<MoveDepth> getMoves(int i) {
		Iterator<Move> it = this.iterator();
		List<MoveDepth> moves = new ArrayList<MoveDepth>();
		
		while(it.hasNext()) {
			moves.add(it.next().getMove(i));
		}
		return moves;
	}
	
	public Game getByPV(int pv) {
		Iterator<Move> it = this.iterator();
		List<Move> moves = new ArrayList<Move>();
		
		while(it.hasNext()) {
			Move move =  it.next();
			moves.add(move.getByPV(pv));
		}
		
		this.clear();
		this.addAll(moves);
		return this;
	}
	
	public Game getByDepth(int pv) {
		Iterator<Move> it = this.iterator();
		List<Move> moves = new ArrayList<Move>();
		
		while(it.hasNext()) {
			Move move =  it.next();
			moves.add(move.getByDepth(pv));
		}
		
		this.clear();
		this.addAll(moves);
		return this;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Move> it = this.iterator();
		
		while(it.hasNext()) {
			Move move = it.next();
			Log.info(move);
			sb.append(move + "\n");
		}
		return sb.toString();
	}

}
