package ai;

import field.*;

public interface AI {

    public String[] chooseStartingPokemon(State s);
    
    public String chooseAction(State s);
}
