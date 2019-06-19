package student_player;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutPlayer;
import tablut.TablutMove;
import java.util.ArrayList;
/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260606583");
    }
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState board_state) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        
        int turn = board_state.getTurnPlayer();
        //get the moves
        ArrayList<TablutMove> all_moves = board_state.getAllLegalMoves();

        //get rid of opponent moves
        for (int i = 0; i < all_moves.size(); i++) {
        	int current_turn = all_moves.get(i).getPlayerID();
        	if (turn != current_turn) {
        		all_moves.remove(i);
        		i--; //weed out opponents moves
        	} 
        }
        
              
      int alphabeta = MyTools.alphaBeta((TablutBoardState)board_state, turn, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, 2);
       // Return your move to be processed by the server.
      
      MyTools.previous_x = all_moves.get(alphabeta).getEndPosition().x;
      MyTools.previous_y = all_moves.get(alphabeta).getEndPosition().y;
    	
      	//return ma move
        return all_moves.get(alphabeta);
    }

}
        