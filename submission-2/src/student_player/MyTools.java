package student_player;

import java.util.ArrayList;
//import java.util.List;
//import boardgame.Move;
//import java.lang.Math;
import java.util.HashSet;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutMove;
import tablut.TablutBoardState;
 

public class MyTools {
	
	
    public static int previous_x = 0;
    public static int previous_y = 0;

   //my evaluation function for a board
    public static int evaluationF(TablutBoardState board_state) {
    	int init_black = 16;
    	int init_white = 9;
    	int evaluation = 0;
        int weight_attack = 200;	//parameter for attacking a king
        int weight_corner = 130;		//parameter for proximity to the corner
        int weight_king = 300;		//parameter for proximity to the king
        int weight_kill_black = 15;
        int weight_diag_black = 30;
        int weight_diag_white = 5;
        int weight_kill_white = 20;
        //int weight_block = 15;
        int weight_sweet = 50;

        // evaluation function actually
        //get a king position
        Coord where_king = board_state.getKingPosition();

        	 if (board_state.getWinner() == 0) {
                 evaluation = Integer.MAX_VALUE;
                 return evaluation;
        	 } 
        	 else if((where_king.y == 0 && where_king.x == 0) || (where_king.y == 0 && where_king.x == 8) || (where_king.y == 8 && where_king.x == 0) || (where_king.y == 8 && where_king.x == 8)){
        		 evaluation = Integer.MIN_VALUE;
        		 return evaluation;
        	 }
        	 //black points:

        	    
		        //attacking a king
		        int number_attacks = 0;
		        HashSet<Coord> blacks = new HashSet<>();
		        if(board_state.getTurnPlayer() == 0) {
		        	blacks = board_state.getPlayerPieceCoordinates();
		        }
		        else if(board_state.getTurnPlayer() == 1){
		        	blacks = board_state.getOpponentPieceCoordinates();
		        }
	         	 for (Coord king_neighbour : Coordinates.getNeighbors(where_king)) {
	         		if (blacks.contains(king_neighbour)) number_attacks++;
	         	}
	         	evaluation += number_attacks*weight_attack;
	         	/////diags
	         	int black_diagonals = 0;
	     				     		
			     for (Coord black : blacks) {
			        try {
			        	Coord potential = Coordinates.get(black.x - 1, black.y - 1);
			        	if (blacks.contains(potential)) {
			        		black_diagonals += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(black.x - 1, black.y + 1);
			        	if (blacks.contains(potential)){
			        		black_diagonals += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(black.x + 1, black.y - 1);
			        	if (blacks.contains(potential)){
			        		black_diagonals += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(black.x + 1, black.y + 1);
			        	if (blacks.contains(potential)) {
			        		black_diagonals += 1;
			        	}
			        	} catch (Exception e) {}
			     }
			        evaluation += black_diagonals * weight_diag_black;
			    
  	
	         	
		        //if we killed some white
		        int new_whites = board_state.getNumberPlayerPieces(1);
		        if(new_whites < init_white){ // ugly
		        	int difference = init_white-new_whites;
		        	evaluation += difference*weight_kill_black;
		        }
		        
		        //time for white:
		        //king to the side!
		        if(where_king.x == 0 || where_king.x == 8 || where_king.y == 0 || where_king.y == 8){
	             	evaluation -= weight_king;
	             }
		        //king to the corner
		        int distance_to_corner = Coordinates.distanceToClosestCorner(where_king);
		        //I get distance to the corner, closer the better
		        evaluation += distance_to_corner*weight_corner;
		        
		        ///block sweet spots
		        int sweet_spots = 0;
		        HashSet<Coord> whites = new HashSet<>();
		        if(board_state.getTurnPlayer() == 1) {
		        	whites = board_state.getPlayerPieceCoordinates();
		        }
		        else if(board_state.getTurnPlayer() == 0){
		        	whites = board_state.getOpponentPieceCoordinates();
		        }
		        for(Coord white : whites){
		        	if((white.x == 0 && white.y == 6) || (white.x == 2 && white.y == 0) || (white.x == 6 && white.y == 0) || (white.x == 0 && white.y == 2)){
		        		//add brackets
		        		sweet_spots++;
		        	}
		        }
		        evaluation -= sweet_spots*weight_sweet;
		        //kills for white
		        int new_blacks = board_state.getNumberPlayerPieces(0);
		        if(new_blacks < init_black){
		        	int difference = init_black-new_blacks;
		        	evaluation -= weight_kill_white*difference;
		        }
		        
		    /////diags
	         	 int diagonals_white = 0;
	     		
			     for (Coord white : whites) {
			        try {
			        	Coord potential = Coordinates.get(white.x - 1, white.y - 1);
			        	if (whites.contains(potential)) {
			        		diagonals_white += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(white.x - 1, white.y + 1);
			        	if (whites.contains(potential)){
			        		diagonals_white += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(white.x + 1, white.y - 1);
			        	if (whites.contains(potential)){
			        		diagonals_white += 1;
			        	}
			        	} catch (Exception e) {}
			        try {
			        	Coord potential = Coordinates.get(white.x + 1, white.y + 1);
			        	if (whites.contains(potential)) {
			        		diagonals_white += 1;
			        	}
			        	} catch (Exception e) {}
			     }
			        evaluation += diagonals_white * weight_diag_white;
        
		        return evaluation;
        		
		    }
		    
    
    public static int alphaBeta(TablutBoardState bs, int turn, int depth, int a, int b, int depthLimit){
    	//if depth = 0 or node is a terminal node
    	if(depth == 0){
    		return evaluationF(bs);
    	}
    	//maximization is for black
    	//if maximizingPlayer
    	if(turn == 0){
    		int evaluation;
    		int v = Integer.MIN_VALUE; //v := -∞
    		int i;
    		//get all moves
            ArrayList<TablutMove> all_moves = bs.getAllLegalMoves();
            //get rid of opponent moves in your all moves
            for (i = 0; i < all_moves.size(); i++) {
            	int current_turn = all_moves.get(i).getPlayerID();
            	if (turn != current_turn) {
            		all_moves.remove(i);
            		i--; //weed out opponents moves
            	} 
            }
            int width = all_moves.size();
            //an array of arrays where each move and its evaluation come in pairs
            int[][] trackingMoves = new int[width][2];
            //call each move and evaluate resulting board
            //for each child of node:
            for(i = 0; i<width; i++){
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(i));
            	evaluation = evaluationF(cloned_bs);
            	//keep it so the first move is the best
            	sortDecreasing(i, evaluation, trackingMoves);
            }
            
            int returnedMove = trackingMoves[0][0];
            for(i = 0; i<width; i++){
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(trackingMoves[i][0]));
            	//check first to make sure we didnt get a win
            	if (cloned_bs.getWinner() == 0){
            		v = Integer.MAX_VALUE;
            	}
            	else if (cloned_bs.getWinner() == 1){
            		v = Integer.MIN_VALUE;
            	}
            	else{
            		v = alphaBeta(cloned_bs,1, depth - 1, a, b,depthLimit);
            	}
            	//just from pseudocode:
            	//v := max(v, alphabeta(child, depth – 1, α, β, FALSE))
            	// α := max(α, v)
            	if(maxOfTwo(v, a) == v){
            		returnedMove = trackingMoves[i][0]; 
            		a = v;
            	}
            	// pruning!!
            	//if β ≤ α
            	if (b <= a) {
            		break;
            	}
            	
            }
            
            if (depth != depthLimit) {
            	return v;
            }
            else { 
            	/*	if (all_moves.get(returnedMove).getStartPosition().x == previous_x
    			&& all_moves.get(returnedMove).getStartPosition().y == previous_y) {
    		if (returnedMove == best_moves[0][0]) return trackingMoves[1][0];
    		else return trackingMoves[0][0];
    		
    		}*/
            	return returnedMove; 
            }            
            
    	} 
    	else{//minimizing
    		int evaluation;
    		int v = Integer.MAX_VALUE;
    		int i;
    		//get all moves
            ArrayList<TablutMove> all_moves = bs.getAllLegalMoves();
           
            for (i = 0; i < all_moves.size(); i++) {
            	int current_turn = all_moves.get(i).getPlayerID();
            	if (turn != current_turn) {
            		all_moves.remove(i);
            		i--; //weed out opponents moves
            	} 
            }
            int width = all_moves.size();
            if (width == 0) {
            	return v;
            }
            //an array of arrays where each move and its evaluation come in pairs
            int[][] trackingMoves = new int[width][2];
            
            for(i = 0; i<width; i++){
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(i));
            	evaluation = evaluationF(cloned_bs);
            	sortIncreasing(i, evaluation, trackingMoves);
            }
            
            int returnedMove = trackingMoves[0][0];
            for(i = 0; i<width; i++){
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(trackingMoves[i][0]));
            	//checking for winners:
            	if (cloned_bs.getWinner() == 0){
            		v = Integer.MAX_VALUE;
            	}
            	else if (cloned_bs.getWinner() == 1){
            		v = Integer.MIN_VALUE;
            	}
            	else {
            		v = alphaBeta(cloned_bs,0, depth - 1, a, b,depthLimit);
            	}
            	//continuing the algo:
            	if(minOfTwo(v, b) == v){
            		returnedMove = trackingMoves[i][0]; 
            		b = v;
            	}
            	// pruning!!
            	if (b <= a) {
            		break;
            	}
            	
            }
            
            if (depth != depthLimit) {
              	return v;
            }
            else { 
            	/*	if (all_moves.get(returnedMove).getStartPosition().x == previous_x
    			&& all_moves.get(returnedMove).getStartPosition().y == previous_y) {
    		
    		
    		if (returnedMove == best_moves[0][0]) return trackingMoves[1][0];
    		else return returnedMove[0][0];
    	  }*/
            	return returnedMove; 
            }              
    	} 
            
    }
   

    
    public static int maxOfTwo(int a, int b){
    	if(a>b) return a;
    	else if(b>a) return b;
    	else return a;
    }
    
    public static int minOfTwo(int a, int b){
    	if(a<b) return a;
    	else if(b<a) return b;
    	else return a;
    }
    
    //I make sure my array is sorted for the minimizing agent to have the best move first
    private static void sortIncreasing (int place, int evaluation, int[][] my_moves) { 
    	int i;
    	String length1 = "";
    	int length = my_moves.length;
    	for (i = 0; i < length - 1; i++) if ((my_moves[i][0] == 0 && my_moves[i][1] == 0) || evaluation < my_moves[i][1]) break;
    	for (int j = length - 2; j >= i; j--){
    		my_moves[j+1][0] = my_moves[j][0];
    		my_moves[j+1][1] = my_moves[j][1];
    	}
    	my_moves[i][0] = place;
    	my_moves[i][1] = evaluation;
    }
    
    public static int newAlphaBeta(TablutBoardState bs, int turn, int depth, int a, int b){
    	if(depth == 0) return evaluationF(bs);
    	
    	if(turn == 0){
    		int v = Integer.MIN_VALUE;
    		ArrayList<TablutMove> all_moves = bs.getAllLegalMoves();
    		for (int i = 0; i < all_moves.size(); i++) {
            	
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(i)); //otherwise process the move
            	int alphaBeta = newAlphaBeta(cloned_bs,1, depth-1, a, b);
            	v = maxOfTwo(v, alphaBeta);
            	a = maxOfTwo(a, v);
            	//pruning!
            	if(b <= a) break;
    		}
    		return v;
    	}
    	else{
    		int v = Integer.MAX_VALUE;
    		ArrayList<TablutMove> all_moves = bs.getAllLegalMoves();
    		for (int i = 0; i < all_moves.size(); i++) {
            	
            	TablutBoardState cloned_bs = (TablutBoardState)bs.clone();
            	cloned_bs.processMove(all_moves.get(i)); //otherwise process the move
            	int alphaBeta = newAlphaBeta(cloned_bs,0, depth-1, a, b);
            	v = minOfTwo(v, alphaBeta);
            	b = minOfTwo(b, v);
            	//pruning!
            	if(b <= a) break;
    		}
    		return v;
    	}
    }

    //I make sure my array is sorted for the minimizing agent to have the best move first
    private static void sortDecreasing (int place, int evaluation, int[][] my_moves) {
    	int i;
       	String length1 = "";
    	int length = my_moves.length;
    	for (i = 0; i < length - 1; i++) if ((my_moves[i][0] == 0 && my_moves[i][1] == 0)  || evaluation > my_moves[i][1]) break;
    	for (int j = length - 2; j >= i; j--){
    		my_moves[j+1][0] = my_moves[j][0];
    		my_moves[j+1][1] = my_moves[j][1];
    	}
    	my_moves[i][0] = place;
    	my_moves[i][1] = evaluation;	
    }
    

    

}
