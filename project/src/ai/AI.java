package ai;

import field.*;

public interface AI {


	//Need the playerOne boolean since the turn will not switch
    public String chooseStartingPokemon(State s, boolean playerOne);
    
    public String chooseAction(State s);
}
