package stockfish;

import java.io.Serializable;

public class MoveDepth implements Serializable {

	private int depth, multipv, score;
	private boolean checkMate = false;
	private boolean isMate = false;
	private String moves;

	public String getMoves() {
		return moves;
	}

	public void setMoves(String moves) {
		this.moves = moves;
	}

	public boolean isCheckMate() {
		return checkMate;
	}

	public void setCheckMate(boolean checkMate) {
		this.checkMate = checkMate;
	}

	public boolean isMate() {
		return isMate;
	}

	public void setMate(boolean isMate) {
		this.isMate = isMate;
	}

	public MoveDepth() {
		this.multipv = 1;
	}
	
	public MoveDepth(boolean checkMate) {
		this.checkMate = checkMate;
		this.depth = 20;
		this.multipv = 1;
	}
	
	public MoveDepth(Integer depth, Integer multipv, Integer score) {
		this.depth = depth;
		this.multipv = multipv;
		this.score = score;
	}

	/**
	 * Method getDepth.
	 * @return String
	 */
	public int getDepth() {
		return depth;
	}
	/**
	 * Method setDepth.
	 * @param depth String
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}
	/**
	 * Method getMultipv.
	 * @return String
	 */
	public int getMultipv() {
		return multipv;
	}
	/**
	 * Method setMultipv.
	 * @param multipv String
	 */
	public void setMultipv(int multipv) {
		this.multipv = multipv;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public String toString() {
		return isMate + " pv " + this.multipv + " score " + score + "";
	}

}
