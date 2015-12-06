package ai;

import field.*;

public abstract class AI {

    public abstract String[] chooseStartingPokemon(State s, boolean player);
    
    public abstract String chooseAction(State s, boolean player);
}
