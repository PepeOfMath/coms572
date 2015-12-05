package cards;

import field.*;
//A class to represent the effect of an attack or trainer card
//  This may end up being very complicated, and will have to work with an effect processing routine
//  However, the alternative is to write a separate class for each individual card, and
//  have each work on the (thus-far undefined) game state.  This would potentially result
//  in needing to update each card when a design change is made.
//Maybe we could even define an effect for switching or something, and then the effects will
//actually be Actions
public class Effect {

    public boolean doEffect(State s) {
        return true;
    }
}
