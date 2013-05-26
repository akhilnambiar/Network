/* MachinePlayer.java */

package player;

import list.*;
import java.lang.Math;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
    
	public final static int BLACK = 0;
    public final static int WHITE = 1;
	int moves;
    int myColor;
    int treeDepth;
    Grid myGrid;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
      myColor=color;
      myGrid = new Grid(this);
      moves = 0;
      treeDepth = 2;
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
	  myColor = color;
	  treeDepth = searchDepth;
	  myGrid = new Grid(this);
	  moves = 0;
  }
  
  /**
   *  assignScore() returns a floating point value between -10 and 10 that represents the
   *  likelihood of this MachinePlayer winning with its grid. A positive number means
   *  that this MachinePlayer is likely to win and a negative number means that the
   *  opponent is likely to win.
   *
   *  @param Grid that you want
   *  @return int value correlated with the validity of aMove
   */
  /* there are a few parts that will help assign a value 
   1) Look at the amount of connections that exist
   2) Look and make sure there is at least a top and bottom goal
   3) Linear functions
   */
    protected int evaluation(Grid evalGrid, boolean who) {
		int connectNum=0;
        int opponentConnNum=0;
        int oppTopGoal=0;
        int myTopGoal =0;
        int myBottomGoal = 0;
        int oppBottomGoal = 0;
        if (myColor==BLACK) {
            for (int i=0; i<Grid.SIZE; i++) {
                for (int j=0; j<Grid.SIZE; j++) {
                    if(evalGrid.board[i][j] == 1) {
                        connectNum += myGrid.chipConnections(i, j).length();
                        if(j == 0) {
                            myTopGoal++;
                        }
                        if(j == 7) {
                            myBottomGoal++;
                        }
                    }
                    if (evalGrid.board[i][j]==2) {
                        opponentConnNum += myGrid.chipConnections(i, j).length();
                        if(i == 0) {
                            oppTopGoal++;
                        }
                        if(i == 7) {
                            oppBottomGoal++;
                        }
                    }
                }
            }
        }
        else {
            for (int i=0; i<Grid.SIZE; i++) {
                for (int j=0; j<Grid.SIZE; j++) {
                    if(evalGrid.board[i][j] == 1) {
                        connectNum += myGrid.chipConnections(i, j).length();
                        if(i == 0) {
                            myTopGoal++;
                        }
                        if(i == 7) {
                            myBottomGoal++;
                        }
                    }
                    if (evalGrid.board[i][j] == 2) {
                        opponentConnNum += myGrid.chipConnections(i, j).length();
                        if(j == 0) {
                            oppTopGoal++;
                        }
                        if(j == 7) {
                            oppBottomGoal++;
                        }
                    }
                }
            }
        }
        int myGoalTrack;
        if(myTopGoal > 1 || myBottomGoal > 1) {
            myGoalTrack = - (myTopGoal*myBottomGoal);
        } else if(myTopGoal == 0 || myBottomGoal == 0) {
            myGoalTrack = -30;
        } else {
            myGoalTrack = 30;
        }
        
        int oppGoalTrack;
        if(oppTopGoal > 1 || oppBottomGoal > 1) {
            oppGoalTrack = - (oppTopGoal*oppBottomGoal);
        } else if(oppTopGoal == 0 || oppBottomGoal == 0) {
            oppGoalTrack = -30;
        } else {
            oppGoalTrack = 30;
        }
      	
        return connectNum + myGoalTrack - opponentConnNum - oppGoalTrack;
    }



  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
      int oppColor = (myColor + 1) % 2;
	  Move m = minimax(this.myColor, Integer.MIN_VALUE, Integer.MAX_VALUE, 0).move;
      System.out.println(m);
	  if(m.moveKind == Move.ADD) {
		  myGrid.addMove(m, true);
	  }else if(m.moveKind == Move.STEP) {
		  myGrid.stepMove(m, true);
	  }
	  moves++;
	  return m;
  }
  
  
    
	/*
	 * minimax() is the recursive game tree search algorithm that returns a Best containing the
	 * best move and an associated score for alpha-beta pruning.
	 *
	 * @param color is the color of the player that is currently being checked.
	 * @param alpha is the alpha value for alpha-beta pruning.
	 * @param beta is the beta value for alpha-beta pruning.
	 * @param depth is an int that represents how many levels of moves the search has gone.
	 *
	 * @return a Best that contains the best move and an associated score for alpha-beta pruning.
	 *
	 */
	protected Best minimax(int color, int alpha, int beta, int depth) {
        Best myBest = new Best();
        Best reply;
        int oppColor = (color + 1) % 2;
        if(myGrid.hasNetwork(((myColor+1)%2), false)) { 
            return new Best(-100*(treeDepth - depth + 1), new Move());
        }
        else if(myGrid.hasNetwork(myColor, true)) {
            return new Best(100*(treeDepth - depth + 1), new Move());
        }
        else if(depth >= treeDepth) {
            return new Best(evaluation(myGrid, oppColor == myColor), new Move());
        }
        
        if (oppColor == myColor) {
            myBest.score = Integer.MAX_VALUE;
        } else {
            myBest.score = Integer.MIN_VALUE;
        }
        
        DListNode currNode = (DListNode) (myGrid.validMoves(color == myColor).front());
        while(currNode.isValidNode()) {
            Grid oldGrid = new Grid(myGrid);
            int oldMoves = moves;
            try {
                Move m = (Move) (currNode.item());
                if(m.moveKind == Move.ADD) {
                    myGrid.addMove(m, color == myColor);
                } else if(m.moveKind == Move.STEP) {
                    myGrid.stepMove(m, color == myColor);
                }
                if(color == myColor) {
                    moves++;
                }
                reply = minimax(oppColor, alpha, beta, depth + 1);
                moves = oldMoves;
                myGrid = oldGrid;
                if(color == myColor && reply.score >= myBest.score) {
                    myBest.move = (Move) (currNode.item());
                    myBest.score = reply.score;
                    alpha = Math.max(alpha, reply.score);
                } else if (oppColor == myColor && reply.score <= myBest.score) {
                    myBest.move = (Move) (currNode.item());
                    myBest.score = reply.score;
                    beta = Math.max(beta, reply.score);
                }
                if(alpha >= beta) {
                    return myBest;
                }
                currNode = (DListNode) (currNode.next());
            }
            catch (InvalidNodeException e1) {
                System.out.println(e1);
            }
        }
        return myBest;
    }

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
      int oppColor = (myColor+1)%2;
      if (myGrid.isValid(m, oppColor)) {
    	  if(m.moveKind == Move.ADD) {
        	  myGrid.addMove(m, false);
          } else if(m.moveKind == Move.STEP) {
        	  myGrid.stepMove(m, false);
          }
          return true;
      }
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
    public boolean forceMove(Move m) {
        if (myGrid.isValid(m, myColor)) {
            if(m.moveKind == Move.ADD) {
                myGrid.addMove(m, true);
                moves++;
            } else if(m.moveKind == Move.STEP) {
                myGrid.stepMove(m, true);
                moves++;
            }
            return true;
        }
        return false;
    }


  public static void main(String[] args) {
	  MachinePlayer myPlayer = new MachinePlayer(0, 3);
	  myPlayer.opponentMove(new Move(3, 2));
	  myPlayer.forceMove(new Move(1, 3));
	  myPlayer.opponentMove(new Move(1, 1));
	  myPlayer.forceMove(new Move(4, 0));
	  myPlayer.opponentMove(new Move(1, 6));
	  myPlayer.forceMove(new Move(7, 2));
	  myPlayer.opponentMove(new Move(0, 3));
	  myPlayer.forceMove(new Move(3, 1));
	  myPlayer.opponentMove(new Move(5, 2));
	  myPlayer.forceMove(new Move(6, 2));
	  myPlayer.opponentMove(new Move(3, 6));
	  myPlayer.forceMove(new Move(0, 4));
	  myPlayer.opponentMove(new Move(7, 4));
	  myPlayer.forceMove(new Move(5, 1));
	  myPlayer.opponentMove(new Move(2, 5));
	  myPlayer.forceMove(new Move(2, 3));
	  myPlayer.opponentMove(new Move(1, 7));
	  myPlayer.forceMove(new Move(2, 5));
	  myPlayer.opponentMove(new Move(2, 4));
	  myPlayer.forceMove(new Move(1, 6));
	  myPlayer.opponentMove(new Move(3, 7));
	  System.out.println(myPlayer.myGrid.chipConnections(1, 3));
	  System.out.println(myPlayer.myGrid.chipConnections(3, 2));
	  System.out.println(myPlayer.myGrid.chipConnections(1, 1));
	  System.out.println(myPlayer.myGrid.chipConnections(4, 0));
	  System.out.println(myPlayer.myGrid.chipConnections(1, 6));
	  System.out.println(myPlayer.myGrid.chipConnections(7, 2));
	  System.out.println(myPlayer.myGrid.chipConnections(0, 3));
	  System.out.println(myPlayer.myGrid.chipConnections(3, 1));
	  System.out.println(myPlayer.myGrid.chipConnections(5, 2));
	  System.out.println(myPlayer.myGrid.chipConnections(6, 2));
	  System.out.println(myPlayer.myGrid.chipConnections(3, 6));
	  System.out.println(myPlayer.myGrid.chipConnections(0, 4));
	 // System.out.println("attempting to choose a move");
	 // myPlayer.chooseMove();
	  MachinePlayer player2 = new MachinePlayer(0, 1);
	  player2.opponentMove(new Move(2, 3));
	  player2.chooseMove();
  }
  
}
