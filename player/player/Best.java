/* Best.java */

package player;

/* Best is a class for creating Bests used in minimax. A Best contains a best Move and an
 * associated score to be used by the minimax algorithm.
 */

public class Best {
	
	public int score;
	public Move move;
	
	
	public Best(int s, Move m) {
		score = s;
		move = m;
	}
	
	public Best(Move m) {
		move = m;
	}
	
	public Best(int s) {
		score = s;
	}
	
	public Best() {
	}
	 
}
