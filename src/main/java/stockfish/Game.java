package stockfish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jline.internal.Log;

public class Game extends ArrayList<Move> implements Serializable {
	
	private int idGame;
	private int totalPlyCount;
	
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

	public Game() {
		super();
	}
	
	public Game(int idGame) {
		super();
		this.idGame = idGame;
	}
	
	public Game(int idGame, int totalPlyCount) {
		super();
		this.idGame = idGame;
		this.totalPlyCount = totalPlyCount;
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
		Game rl = new Game(this.idGame, totalPlyCount);
		
		while(it.hasNext()) {
			Move move =  it.next();
			rl.add(move.getByPV(pv));
		}
		return rl;
	}
	
	public Game getByDepth(int pv) {
		Iterator<Move> it = this.iterator();
		Game rl = new Game(this.idGame, totalPlyCount);
		
		while(it.hasNext()) {
			Move move =  it.next();
			rl.add(move.getByDepth(pv));
		}
		return rl;
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
