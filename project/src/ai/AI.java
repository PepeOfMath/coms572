package ai;

import field.*;

public abstract class AI {

    public abstract String[] chooseStartingPokemon(State s);
    
    public abstract String chooseAction(State s);
}
