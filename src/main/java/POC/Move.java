package POC;

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
		this.halfMove = halfMove+1; // Because registred ply in database begins from 0
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
		Move clone = (Move) this.clone();
		clone.clear();
		Iterator<MoveDepth> it = this.iterator();
		
		while(it.hasNext()) {
			MoveDepth crtRow =  it.next();
			if(crtRow.getMultipv() == pv) {
				clone.add(crtRow);
				return clone;
			}
		}
		return clone;
	}
	
	public Move getByDepth(int depth) {
		Move clone = (Move) this.clone();
		clone.clear();
		Iterator<MoveDepth> it = this.iterator();
		
		while(it.hasNext()) {
			MoveDepth crtRow = it.next();
			if(crtRow.getDepth() == depth) {
				clone.add(crtRow);
				return clone;
			}
		}

		return clone;
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
