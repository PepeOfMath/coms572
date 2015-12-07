package ai;

import java.util.ArrayList;
import java.util.Random;

import field.*;


//This will be a very simple agent, choosing moves randomly after the initial setup
public class RandomPlAI extends AI{

    private Random r;
    
    public RandomPlAI() {
        super();
        r = new Random();
    }
    
    //TODO
    public String[] chooseStartingPokemon(State s) {
        Field f = s.playerOneTurn ? s.playerOneF : s.playerTwoF;
        return null;
    }
    
    //Choose a random action
    public String chooseAction(State s) {
    	ArrayList<String> cmds;
    	boolean player = s.playerOneTurn;
    	
    	double value = r.nextDouble();
    	if (value < 0.05) {
    		return "end turn";
    	} else if (value < 0.2) {
    		cmds = s.getAllSwitchMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	} else if (value < 0.4) {
    		cmds = s.getAllAttackMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	} else {
    		cmds = s.getAllPlayMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	}
    }
}
