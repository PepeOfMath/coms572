package ai;

import java.util.ArrayList;
import java.util.Random;

import cards.Card;
import cards.Pokemon;
import field.Field;
import field.State;

public class RandomSearchAI implements AI {

	
	private int pkmnCount = 0;
    private Random r;
    private final int NUM_CHECKS = 20;
    
    public RandomSearchAI() {
        super();
        r = new Random();
    }
	
	@Override
	public String chooseStartingPokemon(State s, boolean playerOne) {
		ArrayList<String> cmds = new ArrayList<String>();
		int best = -1;
		int bestScore = 0;
        Field f = playerOne ? s.playerOneF : s.playerTwoF;
        for (int i = 0; i < f.handCount; i++) {
        	Card c = f.hand.get(i);
        	if ( (c instanceof Pokemon) && ((Pokemon)c).isBasic() ) {
        		Pokemon p = (Pokemon)c;
        		
        		//Apply a basic heuristic
        		if ( bestScore < Math.max(10, p.totalHP + (40 - 10*p.retreatCost)) ) best = i;
        	}
        }
        
        if (best == -1 || pkmnCount >= 3) {
        	pkmnCount = 0;
        	return "done";
        }
        pkmnCount++;
        return f.hand.get(best).name;
	}

	@Override
	public String chooseAction(State s) {
		long t1 = System.currentTimeMillis();
		boolean maximize = s.playerOneTurn;
		int turnCount = s.turnCount;
		//int depth = 4;
		ArrayList<String> cmds = s.getAllMoves();
		//System.out.println("Choosing from " + cmds.size() + " possible moves");
		//for (int i = 0; i < cmds.size(); i++) {
		//	System.out.println(cmds.get(i));
		//}
		int bestMove = 0;
		int bestScore = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for (int i = 0; i < cmds.size(); i++ ) {
			State tmp = new State(s, true);
			tmp.handleCommand(cmds.get(i), tmp.playerOneTurn);
			int score;
			if (!tmp.isGameOver()) {
				//int score = scoreAction(tmp, depth);
				score = scoreAction2(tmp, turnCount+2);
			} else {
				score = s.scoreGame();
			}
			if ( (maximize && score > bestScore) || (!maximize && score < bestScore) ) {
				bestScore = score;
				bestMove = i;
			}
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("");
		System.out.println("Best Score: " + bestScore);
		return cmds.get(bestMove);
	}
	
	//New version.  Non-stochastic.  Might be too much searching
	public int scoreAction2(State s, int stopTurn) {
		if (s.turnCount == stopTurn) return s.scoreGame();
		boolean maximize = s.playerOneTurn;
		int bestScore = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		ArrayList<String> cmds = s.getAllMoves();
		for (int i = 0; i < cmds.size(); i++) {
			State tmp = new State(s, true);
	    	tmp.handleCommand(cmds.get(i), true);
	    	int score;
	    	if (!tmp.isGameOver()) {
		    	//Descend
		    	score = scoreAction2(tmp, stopTurn);
	    	} else {
	    		score = s.scoreGame();
	    	}
	    	
	    	if ( (maximize && score > bestScore) || (!maximize && score < bestScore) ) {
				bestScore = score;
	    	}
		}
		
		return bestScore;
	}
	
	public int scoreAction(State s, int d) {
		if (d == 0) return s.scoreGame();
		//Thoughts: it seems that we want to play to a fixed point in the future
		//Otherwise, moves are biased by the fact that the AI will want to avoid giving the opponent moves
		
		
		boolean maximize = s.playerOneTurn;
		int bestScore = maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		ArrayList<String> cmds;
		String choice;
		for (int i = 0; i < NUM_CHECKS; i++) {
			//Choose a move randomly
	    	double value = r.nextDouble();
	    	if (value < 0.03) { //End Turn
	    		choice = "end turn";
	    	} else if (value < 0.12) { //Switch
	    		cmds = s.getAllSwitchMoves();
	    		choice = (cmds.size() == 0) ? "" : cmds.get( r.nextInt(cmds.size()) );
	    	} else if (value < 0.4) { //Attack
	    		cmds = s.getAllAttackMoves();
	    		choice = (cmds.size() == 0) ? "" : cmds.get( r.nextInt(cmds.size()) );
	    	} else { //Play a card
	    		cmds = s.getAllPlayMoves();
	    		choice = (cmds.size() == 0) ? "" : cmds.get( r.nextInt(cmds.size()) );
	    	}
	    	
	    	//Apply to a copied state
	    	State tmp = new State(s, true);
	    	tmp.handleCommand(choice, true);
	    	int score;
	    	if (!tmp.isGameOver()) {
		    	//Descend
		    	score = scoreAction(tmp, d-1);
	    	} else {
	    		score = s.scoreGame();
	    	}
	    	
	    	if ( (maximize && score > bestScore) || (!maximize && score < bestScore) ) {
				bestScore = score;
	    	}
	    	
		}
		
		return bestScore;
	}

}
