package field;

/** 
 * A class representing the State of the game, including:
 *      Human player deck count, hand count, and prize count
 *      Human player cards in hand+deck+prizes (visible)
 *      Human discard pile
 *      Human player side of field {6 pokemon slots}
 *      CPU deck count, hand, count, prize count
 *      CPU cards in hand, cards in deck + prizes (visible)
 *      CPU ordered / shuffled deck
 *      CPU discard pile
 *
 *
 * The field is fairly complicated:
 * There are 6 spaces on each side, which need to keep track of a pokemon, evolutions, energies, 
 * and status: hp, effect, turn played, anything else? It might be good to have a separate data
 * structure which keeps track of this information
 */
 
import java.util.ArrayList;
import java.util.Collections;
import cards.*;
import util.*;

public class State {



    public Field playerOneF; //The human player, for now
    public Field playerTwoF; //The cpu player, for now
    public int turnCount;
    public boolean playedEnergy; //Three toggles which should reset on each turn
    public boolean playedSupporter;
    public boolean performedSwitch;
    
    public State(String p1DeckFile, String p2DeckFile) {
        playerOneF = new Field(p1DeckFile);
        playerTwoF = new Field(p2DeckFile);
        turnCount = 0;
        
        playedEnergy = false;
        playedSupporter = false;
        performedSwitch = false;
    }
    
    public int initialDraw(boolean playerOne) {
        Field f = playerOne ? playerOneF : playerTwoF;
        int count = 0;
        boolean contin = true;
        while (contin) {
            count++;
            //shuffle deck
            Collections.shuffle(f.deck);
       
            //look at last 7 cards for a basic pkmn
            for (int i = f.deck.size() - 7; i < f.deck.size() && contin; i++) {
                Card d = f.deck.get(i);
                if (d instanceof Pokemon) {
                    Pokemon p = (Pokemon)d;
                    contin = !p.evolvesFrom.equalsIgnoreCase("Null");
                    //If we have a basic pokemon, we are allowed to stop looping
                    //else reloop and increment count
                }
            }
        }
        
        //if there is >= 1, then draw cards into the hand
        drawCardsToHand(playerOne, 7);
        //draw prizes
        drawCardsToPrizes(playerOne);

        return count;
    }
    
    //Move cards from deck to hand, return the actual number of cards moved
    //Should be used for initial draw as well as usual draws
    //Not for hypothetical draw!!
    public int drawCardsToHand(boolean playerOne, int numCards) {
        Field f = playerOne ? playerOneF : playerTwoF;
        boolean tmp;
        for(int i = 0; i < numCards; i++) {
            tmp = f.drawCardToHand();
            if (!tmp) return i;
        }
        return numCards;
    }
    
    //Set up the prize cards.  Nothing is returned because this should never fail
    private void drawCardsToPrizes(boolean playerOne) {
        Field f = playerOne ? playerOneF : playerTwoF;
        f.drawPrizeCards();
    }
    
    //Search the hand to see if there is a card with the given name
    public boolean hasCardInHand(boolean playerOne, String name) {
        Field f = playerOne ? playerOneF : playerTwoF;
        return f.hasCardInHand(name);
    }
    
    //Find out if the card in hand is a basic pokemon
    public boolean isCardInHandBasicPkmn(boolean playerOne, String name) {
        Field f = playerOne ? playerOneF : playerTwoF;
        return f.isCardInHandBasicPkmn(name);
    }
    
    //Try to play a basic pokemon (has checks for valid move built in)
    public boolean playBasicPkmn(boolean playerOne, String name) {
        Field f = playerOne ? playerOneF : playerTwoF;
        boolean test = f.isCardInHandBasicPkmn(name);
        if (!test) return false; //Not a basic pokemon or not in hand
        return f.playBasicPkmn(name, turnCount);
    }
    
    //Handle play of an evolution Pokemon 
    public boolean playEvolvPkmn(boolean playerOne, String name, int slotNum) {
        //TODO
        return false;
    }
    
    //Try to play an Energy card
    public boolean playEnergy(boolean playerOne, String name, int slotNum) {
        if (playedEnergy) return false; //Cannot play another energy
        Field f = playerOne ? playerOneF : playerTwoF;
        boolean result = f.playEnergy(name, slotNum);
        playedEnergy = result;
        return result;
    }
    
    //Try to switch 2 Pokemon on the field
    public boolean doSwitch(boolean playerOne, int slotNum) {
        if (performedSwitch) return false; //Cannot switch twice
        Field f = playerOne ? playerOneF : playerTwoF;
        return f.doSwitch(slotNum);
    }
    
    public boolean playCard(boolean playerOne, String name, int slotNum) {
        Field f = playerOne ? playerOneF : playerTwoF;
        //get the card
        Card c = f.findCardByName(name);
        if (c instanceof Energy) {
            return playEnergy(playerOne, name, slotNum);
        } else if (c instanceof Trainer) {
            //TODO handle trainer cards
            return false;
        } else {//Pokemon Card
            if( ((Pokemon)c).evolvesFrom.equals("Null") ) { //Basic Pokemon
                return playBasicPkmn(playerOne, name);
            } else { //Attempt to do an Evolution at the given slot
                return playEvolvPkmn(playerOne, name, slotNum);
            }
        }
        //Pass control to the appropriate Field in order to place the card correctly
        //May have some effects to handle with the whole state, however so we need to return 
        //a card effect TODO.  This means an invalid play would have to throw an exception
    }
    
    //Print out the hand for the specified user
    public void printHand(boolean playerOne) {
        Field f = playerOne ? playerOneF : playerTwoF;
        System.out.println(playerOne ? "Player 1 Hand" : "Player 2 Hand");
        for (int i = 0; i < f.hand.size(); i++) {
            System.out.println(f.hand.get(i));
        }
    }
    
    //Print to command line: a representation of the current state
    //2nd parameter indicates whether to print the player's hand
    public void printState(boolean playerOne, boolean showHand) {
        //Player One Information
        System.out.println("");
        System.out.println("Player 1");
        System.out.println("-----------------------------------------");
        System.out.println("Deck: " + playerOneF.deckCount + "  Hand: " + playerOneF.handCount + "  Prize: " + playerOneF.prizeCount + "  Discard: " + playerOneF.discard.size());
        System.out.println("Field:");
        for (int i = 0; i < playerOneF.pkmnSlots.length; i++) {
            if (i == 0) {
                System.out.println("Active: " + playerOneF.pkmnSlots[i]);
            } else {
                System.out.println("Bench" + i + ": " + playerOneF.pkmnSlots[i]);
            }
        }
        //TODO: print the six Positions with information
        if(playerOne && showHand) {
            System.out.println("");
            System.out.println("Hand:");
            for (int i = 0; i < playerOneF.hand.size(); i++) {
                System.out.println(playerOneF.hand.get(i));
            }
        }
        
        //Player Two Information
        System.out.println("");
        System.out.println("Player 2");
        System.out.println("-----------------------------------------");
        System.out.println("Deck: " + playerTwoF.deckCount + "  Hand: " + playerTwoF.handCount + "  Prize: " + playerTwoF.prizeCount + "  Discard: " + playerTwoF.discard.size());
        System.out.println("Field:");
        for (int i = 0; i < playerTwoF.pkmnSlots.length; i++) {
            if (i == 0) {
                System.out.println("Active: " + playerTwoF.pkmnSlots[i]);
            } else {
                System.out.println("Bench" + i + ": " + playerTwoF.pkmnSlots[i]);
            }
        }
        //TODO: print the six Positions with information
        if((!playerOne) && showHand) {
            System.out.println("");
            System.out.println("Hand:");
            for (int i = 0; i < playerTwoF.hand.size(); i++) {
                System.out.println(playerTwoF.hand.get(i));
            }
        }
        
        System.out.println("");
    }

    //Reset the set of switch variables
    public void resetSwitches() {
        playedEnergy = false;
        playedSupporter = false;
        performedSwitch = false;
    }
  
}

