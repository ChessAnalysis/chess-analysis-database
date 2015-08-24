package stockfish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import jline.internal.Log;

public class Move extends ArrayList<MoveDepth> implements Serializable {
	
	String move;
	String FEN;
	int halfMove;
	
	public String getFEN() {
		return FEN;
	}

	public void setFEN(String fEN) {
		FEN = fEN;
	}

	public int getHalfMove() {
		return halfMove;
	}

	public void setHalfMove(int halfMove) {
		this.halfMove = halfMove;
	}

	public Move() {
		super();
	}
	
	public Move(String move, int halfMove, String FEN) {
		super();
		this.move = move;
		this.halfMove = halfMove;
		this.FEN = FEN;
	}
	
	public MoveDepth getBestScore() {
		return getMove(20);
	}
	
	public MoveDepth getMove(int i) {
		if(getByDepth(i).size() > 0)
			return getByDepth(i).get(0);
		else
			return new MoveDepth();
	}

	public Move getByPV(int pv) {
		Iterator<MoveDepth> it = this.iterator();
		Move rl = new Move();
		
		while(it.hasNext()) {
			MoveDepth crtRow =  it.next();
			if(crtRow.getMultipv() == pv) {
				rl.add(crtRow);
			}
		}
		return rl;
	}
	
	public Move getByDepth(int depth) {
		Iterator<MoveDepth> it = this.iterator();
		Move rl = new Move(move, halfMove, FEN);
		
		while(it.hasNext()) {
			MoveDepth crtRow =  it.next();
			if(crtRow.getDepth() == depth) {
				rl.add(crtRow);
				return rl;
			}
		}
		return rl;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<MoveDepth> it = this.iterator();
		
		while(it.hasNext()) {
			sb.append(it.next() + "\n");
		}
		
		return sb.toString();
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	
}
