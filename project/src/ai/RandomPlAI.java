package ai;

import java.util.ArrayList;
import java.util.Random;

import javax.rmi.CORBA.Util;

import field.*;


//This will be a very simple agent, choosing moves randomly after the initial setup
public class RandomPlAI extends AI{

    private Random r;
    
    public RandomPlAI() {
        super();
        r = new Random();
    }
    
    //TODO
    public String[] chooseStartingPokemon(State s, boolean player) {
        Field f = player ? s.playerOneF : s.playerTwoF;
        return null;
    }
    
    //Choose a random action
    public String chooseAction(State s, boolean player) {
    	ArrayList<String> cmds;
    	
    	double value = r.nextDouble();
    	if (value < 0.05) {
    		return "end turn";
    	} else if (value < 0.2) {
    		//Get a switch command
    		cmds = s.getAllSwitchMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s, player) : cmds.get( r.nextInt(cmds.size()) );
    	} else if (value < 0.4) {
    		cmds = s.getAllAttackMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s, player) : cmds.get( r.nextInt(cmds.size()) );
    	} else {
    		cmds = s.getAllPlayMoves(player);
    		return (cmds.size() == 0) ? chooseAction(s, player) : cmds.get( r.nextInt(cmds.size()) );
    	}
    	
    	/*
        //Possible actions: end turn, attack, switch, or play a card
        if (value < 0.05) {
            return "end turn";
        } else if (value < 0.2) {
            return "switch " + (r.nextInt(5)+1);
        } else if (value < 0.4) {
            return "attack " + (r.nextInt(2)+1);
        } else {
            //Attempt to play a random card on a random spot
            Field f = player ? s.playerOneF : s.playerTwoF;
            int pos = r.nextInt(6);
            int card = r.nextInt(f.handCount);
            return "play " + pos + " " + f.hand.get(card).toString();
        }*/
    }
}
