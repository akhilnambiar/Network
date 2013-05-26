/* Grid.java */

package player;

/* Grid is an object that will contain the game board and various other methods that will do the game tree analysis*/
import java.lang.Math;
import list.*;

public class Grid {
    public static final int SIZE = 8;
    public static final int EMPTY = 0;
    public static final int MY_TOKEN = 1;
    public static final int OPPONENT_TOKEN = 2;
    
    protected int[][] board;
    protected MachinePlayer me;
    
    protected Grid(MachinePlayer indv) {
        board = new int[SIZE][SIZE];
        me = indv;
        
    }
    
	/*
	 * Grid constructor that makes a copy of the input grid.
	 * This is used in the minimax algorithm to keep track of the original grid before
	 * recursive calls are made that change the board.
	 *
	 */
    protected Grid(Grid oldGrid) {
    	board = new int[SIZE][SIZE];
    	me = oldGrid.me;
    	for(int i = 0; i < SIZE; i++) {
    		for(int j = 0; j < SIZE; j++) {
    			board[i][j] = oldGrid.board[i][j];
    		}
    	}
    }
    /*
     * stepMove() performs a step move on this grid.
     *
     * @param m is the Move that is being performed.
     * @param isMe indicates which player is making the move.
     *
     */
    protected void stepMove(Move m, boolean isMe) {
    	if(m.moveKind == Move.STEP) {
    		board[m.x2][m.y2] = EMPTY;
            if(isMe) {
                board[m.x1][m.y1] = MY_TOKEN;
            }
            else {
                board[m.x1][m.y1] = OPPONENT_TOKEN;
            }
    	} else {
    		return;
    	}
    }
    
    /*
     * addMove() performs an add move on this grid.
     *
     * @param m is the Move that is being performed.
     * @param isMe indicates which player is making the move.
     *
     */
    protected void addMove(Move m, boolean isMe) {
    	if(m.moveKind == Move.ADD) {
    		if (isMe) {
        		board[m.x1][m.y1] = MY_TOKEN;
        	} else {
        		board[m.x1][m.y1] = OPPONENT_TOKEN;
        	}
    	} else {
    		return;
    	}
    	
    }
    
    /* 
	 * isValid() looks to see if a move is valid, it will take in a Move and an int representing the color and return a true or a false
     * 
	 * @param aMove, a Move that will be placed on this Grid to be checked for validity.
     * @param color, the color of the piece of the move maker
     * @return boolean, if the move is valid or not
     *
     */
    protected boolean isValid(Move aMove, int color) {
        boolean myself;
        if (color==me.myColor) {
            myself=true;
        }
        else{
            myself = false;
        }
        if (board[aMove.x1][aMove.y1]!=0){
            return false;
        }
        if (myself) {
            if (madeTenMoves(myself)) {
                if (aMove.moveKind==Move.ADD) {       //This case is checking if you haven't placed all 10 chips, then you can't step
                    return false;
                }
            }
            else {
                if (aMove.moveKind==Move.STEP) {     //This checks that if there are less than 10 moves
                    return false;
                }
            }
        }
        else {
            if (madeTenMoves(myself)) {
                if (aMove.moveKind==Move.ADD) {
                    return false;
                }
            }
            else {
                if (aMove.moveKind==Move.STEP) {
                    return false;
                }
            }
        }
        
        if((aMove.x1==0 && aMove.y1==0) || (aMove.x1==SIZE-1 && aMove.y1==0) || (aMove.x1==SIZE-1 && aMove.y1==0) || (aMove.x1==SIZE-1 && aMove.y1==SIZE-1)) {
                return false;               //Makes sure you can't place pieces in the corners
            }
        
        if ((color==0) && (aMove.x1==0 || aMove.x1==SIZE-1)) {
                return false;           //Can't put black pieces in a white goal
            }
        else if ((color==1) && (aMove.y1==0 || aMove.y1==SIZE-1)) {
                return false;           //Can't put white pieces in a black goal
        }
        else if (board[aMove.x1][aMove.y1]==0) {
            if (surround(aMove.x1,aMove.y1,color,false)) {
                    return true;          //makes sure that there are no surrouding pieces
            }
            else {
                return false;
            }
        } //The quit case hasn't been handled, but doesn't need to be
        return true;
    }
    
    /* 
	 * surround() is a helper that will check the surroundings to check and see if there are a cluster of chips around it 
     * @param x, The x location of the piece
     * @param y, The y location of the piece
     * @param color, The color of the piece
     * @param twice is used to make sure surround doesn't use infinite recursive calls, only to be set true within surround
     * @return checks to see if  the surrounding pieces are cleared to have another one.
     */
    private boolean surround(int x, int y, int color, boolean twice) {
        int pieceCount=0;
        int pX=0;
        int pY=0; 
        for (int i=-1; i<2; i++) {
            for (int j=-1; j<2; j++) {
                if ((x+i>=0) && (x+i<=7) && (y+j>=0) && (y+j<=7)) {  //This checks through the loop and looks for surrounding pieces
                    if (color==me.myColor){
                        if (board[x+i][y+j]==1) {
                            if (x+i!=0 || y+j!=0) {
                                if (x+i!=x || y+j!=y) {
                                    pX = x+i;
                                    pY = y+j;
                                    if (pieceCount==0) {
                                        pieceCount++;
                                        pX = x+i;
                                        pY = y+j;
                                    }
                                    else {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (board[x+i][y+j]==2) {
                            if (x+i!=0 || y+j!=0) {
                                if (x+i!=x || y+j!=y) {
                                    pX = x+i;
                                    pY = y+j;
                                    if (pieceCount==0) {
                                        pieceCount++;
                                        pX = x+i;
                                        pY = y+j;
                                    }
                                    else {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (pieceCount==0) {    //If there are no pieces surrounding
            return true;
        }
        if (pieceCount>1) {     //If there is more than one piece surrounding
            return false;
        }
        if (pieceCount==1 && twice==true) {
            return false;
        }
        if (pieceCount==1 && surround(pX, pY, color,true)) {
            return true;
        }
        else {
            return false;
        }
    }
    /* hasNetwork() checks to see if there is a network or not. The rules for a network can be found in the readme.
     * 
	 * @param color checks to see which color we want to know has a network
	 * @param me checks to see if we are checking a network for me or my opponent
     * 
     * @return boolean on if there is a Network
     */
    protected boolean hasNetwork (int color, boolean me) {
        DListArr top, bottom;
            top = topGoal(color, me);   //calling a function to create DListArr
            if (top.length()==0) {      //if the top has no nodes
                return false;
            }
            bottom = bottomGoal(color, me);
            if (bottom.length()==0) {
                return false;
            }
            try {
                DListNodeArr curr = (DListNodeArr) top.front();
                for (int i=0; i<top.length(); i++) {
                    DListArr network = new DListArr();
                    network.insertFront(curr.item());
                    curr = (DListNodeArr) curr.next();
                    if (finishNetwork(network,top, bottom)) {
                        return true;
                    }
                }
            }
            catch (InvalidNodeException e1) {
                System.out.println(e1);
                System.out.println("error in hasNetwork");
                System.exit(0);
            }
            return false;
    }
	
    /* 
	 * finishNetwork() is a helper that is determining whether or not a network exists 
     *
	 * @param netSoFar is a DListArr of all of the nodes that we have traversed so far
     * @param top is a DListArr of all of the nodes in the top/left goal
     * @param bottom is a DListArr of all of the nodes in the bottom/right goal
     * @return boolean on if there is a network or not
     */ 
    private boolean finishNetwork (DListArr netSoFar, DListArr top, DListArr bottom) {
        try {
            DListArr validConnections = chipConnections(((Integer)netSoFar.front().item()[0]).intValue(), ((Integer)netSoFar.front().item()[1]).intValue());// we are creating a list of valid connections using x and y
            DListNodeArr currConnections =(DListNodeArr) validConnections.front();  // we examine the first item in these valid connections
            if (netSoFar.length()>=5) {          //We know that the node is always coming from the top
                for (int i=0;i<validConnections.length();i++) {
                    if (i!=0) {
                        currConnections = (DListNodeArr) currConnections.next();
                    }
                    DListNodeArr currBottom  = (DListNodeArr) bottom.front();
                    for (int j=0;j<bottom.length();j++) {
                        if (j!=0) {
                            currBottom = (DListNodeArr) currBottom.next();
                        }
                        if (currBottom.equals(currConnections)) {
                            if (turnsCorner((DListNodeArr)netSoFar.front().next(),(DListNodeArr)netSoFar.front(),currConnections)) {
                                return true;
                            }
                        }
                    }
                }
            }
            DListNodeArr currSoFar=(DListNodeArr)netSoFar.front();
            if (netSoFar.length()==1) {                 // check the special case that there is only 1 piece in the network so far
                for (int i=0; i< validConnections.length(); i++) {
                    if (i==0) {
                        currConnections = (DListNodeArr) validConnections.front();
                    }
                    else{
                        currConnections = (DListNodeArr) currConnections.next();
                    }
                    DListArr net2 = new DListArr(netSoFar);
                    net2.insertFront(currConnections.item());
                    if (finishNetwork(net2, top, bottom)) {
                        return true;
                    }
                }
                return false;
            }
            nextConnection:
            for (int i=0; i<validConnections.length();i++) {
                if (i==0) {
                    currConnections = (DListNodeArr) validConnections.front();
                }
                else{
                    currConnections = (DListNodeArr)currConnections.next();
                }
                if (turnsCorner((DListNodeArr)netSoFar.front().next(),(DListNodeArr)netSoFar.front(),currConnections)) {
                    if ((!inList(netSoFar,currConnections)) && (!inList(bottom, currConnections)) && (!inList (top, currConnections))) {
                        DListArr insList = new DListArr(netSoFar);
                        insList.insertFront(currConnections.item()); //if it ins't in the list so far we will insert it
                        if (finishNetwork(insList,top,bottom)){
                            return true;
                        }
                    }
                }
            }
        }
                //If we don't get a true value, we should just keep moving on with the rest of the nodes
                // if we have exhausted through every single option in valid connections, then we know that we haven't found a connection
        catch (InvalidNodeException e1) {
            System.out.println("error in finishNetwork");
            System.out.println(e1);
            System.exit(0);
        }
        return false;
    }
    
    /* inList() is a helper that will take in the netList DListArr and make sure that the current node is not included in this list
     * @param DListArr netList, A List containing all the nodes in the bottom
	 * @param DListNodeArr curr, the node that you want to check
	 * @return boolean on whether or not a node is in a current list
     */
    
    private boolean inList(DListArr netList, DListNodeArr curr) {
        try {
        DListNodeArr currList=(DListNodeArr)netList.front();
        for (int j=0; j<netList.length(); j++) {  //can't be in the bottom, only the last node can
            if (j!=0) {
                currList = (DListNodeArr) currList.next();
            }
            if (curr.equals(currList)) { //if the current node is equal to anything in the top, it is invalid
                return true;
            }
        }
        return false;
        }
        catch (InvalidNodeException e1) {
            System.out.println("error in inList");
            System.out.println(e1);
            System.exit(0);
        }
        return false;
    }
    
    /* This helper is used in order to determine whether or not three pieces "have turned" a corner
     @param DList netSoFar is a DList of the nodes we have traversed so far
     @param DList top is all of the nodes on the top/left goal
     @param DList bottom is all of the nodes on the bottom/right goal
     @result boolean on whether or not the 3 pieces create a turn
     */
    private boolean turnsCorner (DListNodeArr first, DListNodeArr second, DListNodeArr third) {
        /* There are 8 directions total
         x-This is the x-direction
         y-This is the y-direction
         xyq1-this is a velctor with slope 1
         xyq2-this is a vector with slope -1
         pos-this is saying if the vector is in the positive direction
         ASSUMPTIONS
         If it went in the x-direction, the next piece cannot also be in the x-direction
         if it went in the y-direction, it cannot go in the y direction again
         The same applies to all other direction
         */
        try {
            int xDiff = ((Integer)first.item()[0]).intValue()-((Integer)second.item()[0]).intValue();  //the difference between the current piece and piece before it
            int yDiff = ((Integer)first.item()[1]).intValue()-((Integer)second.item()[1]).intValue();
            boolean x=false, y=false, xq1=false, xq2=false;
            if (Math.abs(xDiff)>0 && Math.abs(yDiff)>0) {
                if ((yDiff>0 && xDiff>0) || (yDiff<0 && xDiff<0)) {
                    xq1 = true;
                }
                else {
                    xq2 = true;
                }
            }
            else if (Math.abs(xDiff)>0) {
                x=true;
            }
            else if (Math.abs(yDiff)>0) {
                y=true;
            }
            else {
                System.out.println("Error in turnsCorner");
                System.out.println("Error not by exception");
                System.exit(0);
            }
            //Second part of the function
            xDiff = ((Integer)second.item()[0]).intValue()-((Integer)third.item()[0]).intValue();
            yDiff = ((Integer)second.item()[1]).intValue()-((Integer)third.item()[1]).intValue();       //checking to see the difference between 3rd and 2nd
            if (Math.abs(xDiff)>0 && Math.abs(yDiff)>0) {
                if ((yDiff>0 && xDiff>0) || (yDiff<0 && xDiff<0)) {
                    if(xq1) {
                        return false;
                    }
                }
                else {
                    if (xq2) {
                        return false;
                    }
                }
            }
            else if (Math.abs(xDiff)>0) {
                if(x) {
                    return false;
                }
            }
            else if (Math.abs(yDiff)>0) {
                if (y) {
                    return false;
                }
            }
        }
        catch (InvalidNodeException e1) {
            System.out.println(e1);
            System.out.println("error in turnsCorner");
            System.exit(0);
        }
        return true;
    }
    
    /* topGoal() is a helper that returns a DListArr with all of the items in the top goal
     * Top is the same as left, bottom is the same as right
     * @param int color takes in the color
	 * @param boolean me is whether or not the person is me
     * @return a DListArr that contains the pieces in the top goal
     */
    protected DListArr topGoal(int color, boolean me){
        DListArr result = new DListArr();
        Integer[] xy = new Integer[2];
        if (color==MachinePlayer.BLACK) {
            for (int i=1; i<7; i++) {   //if it is black, then we are looking at the top goals
                if (me) {   
                    if (board[i][0]==1) {    //checking to see if each of the spots have a piece in the top
                        xy[0] = new Integer(i);              //x position
                        xy[1] = new Integer(0);     //y position will always be a 0
                        result.insertFront(xy);
                    }
                }
                else {
                    if (board[i][0]==2) {     //if we are looking for opponents connections, we will check for opponents spots, which is defined by 2
                        xy[0] = new Integer(i);
                        xy[1] = new Integer(0);
                        result.insertFront(xy);
                    }
                }
            }
        }
        else {
            for (int i=1; i<7; i++) {
                if (me) {
                    if (board[0][i]==1) {        //if it is white, you will be looking at the left goal
                        xy[0] = new Integer(0);
                        xy[1] = new Integer(i);
                        result.insertFront(xy);
                    }
                }
                else {
                    if (board[0][i]==2) {
                        xy[0] = new Integer(0);
                        xy[1] = new Integer(i);
                        result.insertFront(xy);
                    }
                }
            }
        }
        return result;
    }
    
    
    /* bottomGoal() is a helper that returns a DListArr with all of the items in the bottom goal
     * @param color takes in the color
     * @param boolean me takes in whether or not the person is me
     * @return a DListArr that contains the pieces in the bottom goal.
     */
    private DListArr bottomGoal(int color, boolean me) {
        DListArr result = new DListArr();
        Integer[] xy = new Integer[2];
        if (color==MachinePlayer.BLACK) {
            for (int i=1; i<7; i++) {
                if (me) {
                    if (board[i][7]==1) {        // we are now checking for the very bottom goal
                        xy[0] = new Integer(i);
                        xy[1] = new Integer(7);
                        result.insertFront(xy);
                    }
                }
                else {
                    if (board[i][7]==2) {
                        xy[0] = new Integer(i);
                        xy[1] = new Integer(7);
                        result.insertFront(xy);
                    }
                }
            }
        }
        else {
            for (int i=1; i<7; i++) {
                if (me) {
                    if (board[7][i]==1) {
                        xy[0] = new Integer(7);      // we are looking to check the right goals
                        xy[1] = new Integer(i);
                        result.insertFront(xy);
                    }
                }
                else {
                    if (board[7][i]==2) {
                        xy[0] = new Integer(7);
                        xy[1] = new Integer(i);
                        result.insertFront(xy);
                    }
                }
            }
        }
        return result;
    }
    
    
    
    /*  chipConnections() returns a DListArr whose nodes hold arrays that represent all of chip's
     *  direct connections on this MachinePlayer's Grid for this MachinePlayer. A chip's direct connection is a chip of the same
     *  color that is in a location that would be able to be used as the next chip in a
     *  network. The guidelines for this can be found in the readme file.
     *
     * @param x the first coordinate of the chip whose connections are sought.
     * @param y the second coordinate of the chip whose connections are sought.
     * @return a DListArr whose items are arrays of Integers that represent the coordinates of the chip's connections.
     */
    protected DListArr chipConnections(int x, int y) {
    	DListArr connections = new DListArr();
    	int color = this.board[x][y];
    	for(int j = y - 1; j > -1; j--) {  // look N
    		if(this.board[x][j] == EMPTY || (isInGoal(x, y) && isInGoal(x, j))) {
    			continue;
    		}else if(this.board[x][j] == color) {
    			connections.insertBack(new Integer[]{x, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x + 1, j = y - 1; i < SIZE && j > -1; i++, j--) {  //look NE
    		if(this.board[i][j] == EMPTY || (isInGoal(x, y) && isInGoal(i, j))) {
    			continue;
    		}else if(this.board[i][j] == color) {
    			connections.insertBack(new Integer[]{i, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x + 1; i < SIZE; i++) {  //look E
    		if(this.board[i][y] == EMPTY || (isInGoal(x, y) && isInGoal(i, y))) {
    			continue;
    		}else if(this.board[i][y] == color) {
    			connections.insertBack(new Integer[]{i, y});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x + 1, j = y + 1; i < SIZE && j < SIZE; i++, j++) {  //look SE
    		if(this.board[i][j] == EMPTY || (isInGoal(x, y) && isInGoal(i, j))) {
    			continue;
    		}else if(this.board[i][j] == color) {
    			connections.insertBack(new Integer[]{i, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int j = y + 1; j < SIZE; j++) {  //look S
    		if(this.board[x][j] == EMPTY || (isInGoal(x, y) && isInGoal(x, j))) {
    			continue;
    		}else if(this.board[x][j] == color) {
    			connections.insertBack(new Integer[]{x, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x -1, j = y + 1; i > -1 && j < SIZE ; i--, j++) {  //look SW
    		if(this.board[i][j] == EMPTY || (isInGoal(x, y) && isInGoal(i, j))) {
    			continue;
    		}else if(this.board[i][j] == color) {
    			connections.insertBack(new Integer[]{i, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x - 1; i > -1; i--) {  //look W
    		if(this.board[i][y] == EMPTY || (isInGoal(x, y) && isInGoal(i, y))) {
    			continue;
    		}else if(this.board[i][y] == color) {
    			connections.insertBack(new Integer[]{i, y});
    			break;
    		}else {
    			break;
    		}
    	}
    	for(int i = x - 1, j = y - 1; i > -1 && j > -1; i--, j--) {  //look NW
    		if(this.board[i][j] == EMPTY || (isInGoal(x, y) && isInGoal(i, j))) {
    			continue;
    		}else if(this.board[i][j] == color) {
    			connections.insertBack(new Integer[]{i, j});
    			break;
    		}else {
    			break;
    		}
    	}
    	return connections;
    }
    
    /*
     * isInGoal() is a helper method that determines if a location is in one of
     * four goals.
     *
     * @param x is the x coordinate of the location in question.
     * @param y is the y coordinate of the location in question.
     *
     * @return a boolean that says whether the location is in one of the goals or not.
     */
    private boolean isInGoal(int x, int y) {
    	return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
    }
    

    /* validMoves() generates a list of valid moves that this Grid's me or
     * the opponent can make on this Grid in its current state.
     *
     * @param isMe indicates if we are determining the validMoves for me or the opponent.
     *
     * @return a DList whose items are every valid move that this Grid's me or the opponent can make.
     */
    protected DList validMoves(boolean isMe) {
    	Move aMove;
    	DList moves = new DList();
    	DListArr mySpaces = this.myOccupiedSpaces(isMe);
    	DListNodeArr currentMy = (DListNodeArr) (mySpaces.front());
    	DListArr emptySpaces = this.emptySpaces();
    	DListNodeArr currentEmpty = (DListNodeArr) (emptySpaces.front());
    	int color;
    	if(isMe) {
    		color = me.myColor;
    	} else {
    		color = (me.myColor + 1) % 2;
    	}
    	try{
    		if(madeTenMoves(isMe)) { //checks to see if the player has made 10 moves and therefore has to make a step move.
        		while(currentMy.isValidNode()) {
        			while(currentEmpty.isValidNode()) {
        				Integer[] currEmptyArray = (Integer[]) currentEmpty.item();
        				Integer[] currMyArray = (Integer[]) currentMy.item();
        				aMove = new Move(currEmptyArray[0], currEmptyArray[1], currMyArray[0], currMyArray[1]);
        				if(isValid(aMove, color)) {
        					moves.insertBack(aMove);
        				}
        				currentEmpty = (DListNodeArr) (currentEmpty.next());
        			}
        			currentEmpty = (DListNodeArr) (emptySpaces.front());
        			currentMy = (DListNodeArr) (currentMy.next());
        		}
        	} else { //otherwise the player has not made 10 moves yet and the player must make an add move.
        		while(currentEmpty.isValidNode()) {
        			Integer[] currEmptyArray = (Integer[]) currentEmpty.item();
        			aMove = new Move(currEmptyArray[0], currEmptyArray[1]);
        			if(isValid(aMove, color)) {
        				moves.insertBack(aMove);
        			}
        			currentEmpty = (DListNodeArr) (currentEmpty.next());
        		}
        	}
    	}
    	catch(InvalidNodeException e1) {
    		System.out.println(e1);
    	}
    	return moves;
    }
    
    /* emptySpaces() is a helper method that generates a DListArr that represents
     * the locations of all the empty spaces on the board.
     */
    private DListArr emptySpaces() {
    	DListArr emptys = new DListArr();
    	for(int i = 0; i < SIZE; i++) {
    		for(int j = 0; j < SIZE; j++) {
    			if(this.board[i][j] == EMPTY) {
    				Integer[] arr = new Integer[2];
    				arr[0] = new Integer(i);
    				arr[1] = new Integer(j);
    				emptys.insertBack(arr);
    			}
    		}
    	}
    	return emptys;
    }
    
    /* myOccupiedSpaces() is a helper method that generates a list of arrays of Integers that represents
     * the locations of all of this Grid's me's or the opponent's occupied spaces.
     *
     * @param isMe indicates if we are looking at me's occupied spaces or the opponent's.
     *
     * @return a DListArr of all of this Grid's me's or the opponent's occupied spaces.
     */
    private DListArr myOccupiedSpaces(boolean isMe) {
    	DListArr myOccupied = new DListArr();
    	for(int i = 0; i < SIZE; i++) {
    		for(int j = 0; j < SIZE; j++) {
    			if((this.board[i][j] == MY_TOKEN && isMe) || (this.board[i][j] == OPPONENT_TOKEN && !isMe)) {
    				Integer[] arr = new Integer[2];
    				arr[0] = new Integer(i);
    				arr[1] = new Integer(j);
    				myOccupied.insertBack(arr);
    			}
    		}
    	}
    	return myOccupied;
    }
    
    /*
     * madeTenMoves() is a helper method that determines if the player that
     * is in question has madeTenMoves. This is used for the validMoves() method.
     *
     * @param isMe indicates which player we want to know has made ten moves.
     *
     * @return a boolean that indicates whether the player in question has made ten moves or not.
     */
    private boolean madeTenMoves(boolean isMe) {
    	if(isMe) {
    		return me.moves >= 10;
    	} else {
    		if(me.myColor == MachinePlayer.WHITE) {
    			return me.moves > 10;
    		} else {
    			return me.moves >= 10;
    		}
    	}
    }
	
	/* Helper method used to check status at a particular point*/
    private void netChecker (DListArr soFar, DListArr top, DListArr bottom, DListNodeArr comp, int iters) {
        try {
            String result = "The Network so far is [ ";
            DListNodeArr curr = (DListNodeArr) soFar.front();
            for (int i=0; i<soFar.length(); i++) {
                if (i!=0) {
                    curr = (DListNodeArr) curr.next();
                }
                result += "("+curr.item()[0]+","+curr.item()[1]+")";
            }
            result += " ]";
            System.out.println(result);
            result = "The node we want to compare with is [ ";
            curr = (DListNodeArr) comp;
            result += "("+curr.item()[0]+","+curr.item()[1]+")";
            result += " ]";
            System.out.println(result);
            
            
            result = "The number or iterations is in this loop is "+iters;
            System.out.println(result);
        }
        catch (InvalidNodeException e1) {
            System.out.println(e1);
            System.out.println("error in netChecker");
            System.exit(0);
        }
    }
    
	/* Nothing to see here. Just test code.	*/

	public static void main(String[] args) {
        MachinePlayer person = new MachinePlayer(0);
        System.out.println("Starting tests...");
        System.out.println("Move 0,0 should be false"+person.forceMove(new Move(0,0)));
        System.out.println("Move 0,7 should be false"+person.forceMove(new Move(0,7)));
        System.out.println("Move 7,0 should be false"+person.forceMove(new Move(7,0)));
        System.out.println("Move 7,7 should be false"+person.forceMove(new Move(7,7)));
        System.out.println("Move 3,3 should be true"+person.forceMove(new Move(3,3)));
        System.out.println("Move 5,5 should be true"+person.forceMove(new Move(5,5)));
        System.out.println("Move 3,4 should be true"+person.forceMove(new Move(3,4)));
        System.out.println("Move 4,4 should be false"+person.forceMove(new Move(4,4)));
        System.out.println("Move 3,3 should be false"+person.forceMove(new Move(3,3)));
        System.out.println("Move 0,4 should be false"+person.forceMove(new Move(0,4)));
        System.out.println("Move 4,0 should be true"+person.forceMove(new Move(4,0)));
        
        
        
        System.out.println("Starting tests for network connection");
        System.out.println("Naive case with no opponent");
        person = new MachinePlayer(0);
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        System.out.println("Forcing a move at 2,0"+person.forceMove(new Move(2,0)));
        System.out.println("Forcing a move at 2,4"+person.forceMove(new Move(2,4)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        //need to fix network code for inserting second item (check for a special case)
        System.out.println("Forcing a move at 3,3"+person.forceMove(new Move(3,3)));
        System.out.println("Forcing a move at 5,3"+person.forceMove(new Move(5,3)));
        System.out.println("Forcing a move at 6,4"+person.forceMove(new Move(6,4)));
        System.out.println("Forcing a move at 3,7"+person.forceMove(new Move(3,7)));
        System.out.println("checking for network, should be true"+person.myGrid.hasNetwork(person.myColor,true));
        
        
        
        System.out.println("Networks with not enough turns");
        person = new MachinePlayer(0);
        System.out.println("Forcing a move at 2,0"+person.forceMove(new Move(2,0)));
        System.out.println("Forcing a move at 2,4"+person.forceMove(new Move(2,4)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        //need to fix network code for inserting second item (check for a special case)
        System.out.println("Forcing a move at 3,3"+person.forceMove(new Move(3,3)));
        System.out.println("Forcing a move at 4,3"+person.forceMove(new Move(4,3)));
        System.out.println("Forcing a move at 4,5"+person.forceMove(new Move(4,5)));
        System.out.println("Forcing a move at 4,7"+person.forceMove(new Move(4,7)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        
        
        System.out.println("Valid network with 9 pieces");
        person = new MachinePlayer(0);
        System.out.println("Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Forcing a move at 2,1"+person.forceMove(new Move(2,1)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        System.out.println("Forcing a move at 2,3"+person.forceMove(new Move(2,3)));
        System.out.println("Forcing a move at 3,4"+person.forceMove(new Move(3,4)));
        System.out.println("Forcing a move at 5,4"+person.forceMove(new Move(5,4)));
        System.out.println("Forcing a move at 6,3"+person.forceMove(new Move(6,3)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        System.out.println("Forcing a move at 6,1"+person.forceMove(new Move(6,1)));
        System.out.println("Forcing a move at 5,1"+person.forceMove(new Move(5,1)));
        System.out.println("Forcing a move at 1,5"+person.forceMove(new Move(1,5)));
        System.out.println("Forcing a move at 1,7"+person.forceMove(new Move(1,7)));
        System.out.println("checking for network, should be true"+person.myGrid.hasNetwork(person.myColor,true));
        
        System.out.println("Valid case with opponent blocking opponent");
        person = new MachinePlayer(0);
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        System.out.println("Forcing a move at 2,0"+person.forceMove(new Move(2,0)));
        System.out.println("Forcing a move at 2,4"+person.forceMove(new Move(2,4)));
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        //need to fix network code for inserting second item (check for a special case)
        System.out.println("Forcing a move at 3,3"+person.forceMove(new Move(3,3)));
        System.out.println("Forcing a move at 5,3"+person.forceMove(new Move(5,3)));
        System.out.println("Forcing a move at 6,5"+person.forceMove(new Move(6,5)));
        System.out.println("Forcing a move at 4,7"+person.forceMove(new Move(4,7)));
        System.out.println("checking for network, should be true"+person.myGrid.hasNetwork(person.myColor,true));
        System.out.println("Placing enemy piece at 4,3");
        person.myGrid.board[4][3]=2;
        System.out.println("checking for network, should be false"+person.myGrid.hasNetwork(person.myColor,true));
        
        System.out.println("Checking if can create a valid network for opponent");
        person = new MachinePlayer(0);
        person.myGrid.board[2][0]=2;
        person.myGrid.board[2][4]=2;
        person.myGrid.board[3][3]=2;
        person.myGrid.board[3][7]=2;
        person.myGrid.board[5][3]=2;
        person.myGrid.board[6][4]=2;
        System.out.println("checking for network, should be true"+person.myGrid.hasNetwork(((person.myColor+1)%2),false));
        
        System.out.println("taking care of cheating");
        person = new MachinePlayer(0);
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 3,1"+person.opponentMove(new Move(3,1)));
        System.out.println("Player: Forcing a move at 1,1"+person.forceMove(new Move(1,1)));
        System.out.println("Computer: Forcing a move at 2,1"+person.opponentMove(new Move(2,1)));
        System.out.println("Player: Forcing a move at 1,2"+person.forceMove(new Move(1,2)));
        
        System.out.println("will this board be found");
        System.out.println("Computer: Forcing a move at 0,4"+person.opponentMove(new Move(0,4)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        System.out.println("Computer: Forcing a move at 7,5"+person.opponentMove(new Move(7,5)));
        System.out.println("Player: Forcing a move at 1,0"+person.forceMove(new Move(1,0)));
        
        MachinePlayer player3 = new MachinePlayer(0, 1);
        System.out.println("Testing to see if opponent makes a connection");
        player3.opponentMove(new Move(6, 1));
        player3.opponentMove(new Move(5, 2));
        player3.opponentMove(new Move(2, 3));
        player3.opponentMove(new Move(3, 3));
        player3.opponentMove(new Move(0, 4));
        player3.opponentMove(new Move(1, 5));
        player3.opponentMove(new Move(4, 5));
        player3.opponentMove(new Move(7, 5));
        player3.opponentMove(new Move(3, 6));
        player3.opponentMove(new Move(6, 6));
        player3.forceMove(new Move(1, 1));
        player3.forceMove(new Move(4, 1));
        player3.forceMove(new Move(5, 1));
        player3.forceMove(new Move(1, 3));
        player3.forceMove(new Move(5, 3));
        player3.forceMove(new Move(6, 3));
        player3.forceMove(new Move(1, 6));
        player3.forceMove(new Move(4, 6));
        player3.forceMove(new Move(6, 5));
        player3.forceMove(new Move(6, 7));
        System.out.println("is there a network "+player3.myGrid.hasNetwork(1, false));
        for (int j = 0; j < 8; j++) {
            
            for(int i = 0; i < 8; i++) {
                
                System.out.print(player3.myGrid.board[i][j]);
                
            }
            
            System.out.println("");
        }
        
        MachinePlayer player4 = new MachinePlayer(1, 3);
        player4.forceMove(new Move(0, 1));
        player4.forceMove(new Move(5, 1));
        player4.forceMove(new Move(4, 2));
        player4.forceMove(new Move(0, 4));
        player4.forceMove(new Move(1, 5));
        player4.forceMove(new Move(0, 1));
        player4.opponentMove(new Move(2, 0));
        player4.opponentMove(new Move(3, 1));
        player4.opponentMove(new Move(2, 3));
        player4.opponentMove(new Move(5, 3));
        player4.opponentMove(new Move(3, 5));
        player4.opponentMove(new Move(2, 7));
        System.out.println("what move is returned "+player4.chooseMove());
        System.out.println("Does the opponent have a network "+player4.myGrid.hasNetwork(0,false));
        System.out.println("HELLO MATEY");
    }
}

