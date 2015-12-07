package ai;

import java.util.ArrayList;
import java.util.Random;

import field.*;
import cards.*;


//This will be a very simple agent, choosing moves randomly after the initial setup
public class RandomPlAI extends AI{

    private Random r;
    
    public RandomPlAI() {
        super();
        r = new Random();
    }
    
    
    public String chooseStartingPokemon(State s) {
    	ArrayList<String> cmds = new ArrayList<String>();
        Field f = s.playerOneTurn ? s.playerOneF : s.playerTwoF;
        for (int i = 0; i < f.handCount; i++) {
        	Card c = f.hand.get(i);
        	if ( (c instanceof Pokemon) && ((Pokemon)c).isBasic() ) cmds.add(c.name);
        }
        return cmds.get( r.nextInt(cmds.size()) );
    }
    
    //Choose a random action
    public String chooseAction(State s) {
    	ArrayList<String> cmds;
    	
    	double value = r.nextDouble();
    	if (value < 0.05) {
    		return "end turn";
    	} else if (value < 0.2) {
    		cmds = s.getAllSwitchMoves();
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	} else if (value < 0.4) {
    		cmds = s.getAllAttackMoves();
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	} else {
    		cmds = s.getAllPlayMoves();
    		return (cmds.size() == 0) ? chooseAction(s) : cmds.get( r.nextInt(cmds.size()) );
    	}
    }
}
