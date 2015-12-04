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
    
    public State(String p1DeckFile, String p2DeckFile) {
        playerOneF = new Field(p1DeckFile);
        playerTwoF = new Field(p2DeckFile);
        turnCount = 0;
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
        drawCardsToPrizes(playerOne, 7);

        return count;
    }
    
    public int drawCardsToHand(boolean playerOne, int numCards) {
        Field f = playerOne ? playerOneF : playerTwoF;
        System.out.println("TODO: draw hand cards");
        return 0;
        //move numCards to hand
        //remove those cards from the deck
        //remove those cards from the unseenMe set
        //return actual number of cards drawn
    
    }
    
    private int drawCardsToPrizes(boolean playerOne, int numCards) {
        Field f = playerOne ? playerOneF : playerTwoF;
        System.out.println("TODO: draw prize cards");
        return 0;
        //move numCards to prizes
        //remove those cards from the deck
        //return actual number of cards seen
    
    }


    /*
    //Human
    public int hdeck;
    public int hhand;
    public int hprizes;
    
    //datastructures
    public ArrayList<Card> hunseen;
    public ArrayList<Card> hdiscard;
    //in-play stuff
    public Position[] hInPlay;
    
    //CPU
    public int cdeck;
    public int chand;
    public int cprizes;
    
    //datastructures
    public ArrayList<Card> cunseen;
    public ArrayList<Card> chandCards;
    public ArrayList<Card> cdiscard;
    public ArrayList<Card> cprizesCards;
    public ArrayList<Card> shuffledDeck; //Should not be visible generally
    //in-play stuff
    public Position[] cpuInPlay;
    
    
    
    public State(String playerDeckFile, String cpuDeckFile) {
        //Human player side
        hprizes = 6;
        hhand = 7;
        hdeck = 60 - hhand - hprizes;
        
        hdiscard = new ArrayList<Card>();
        hunseen = Util.readCardDatabase(playerDeckFile);
        //initialize the unseen cards with the whole deck
        
        //CPU player side
        cprizes = 6;
        chand = 7;
        cdeck = 60 - chand - cprizes;
        
        //get the deck
        cdiscard = new ArrayList<Card>();
        chandCards = new ArrayList<Card>();
        cprizesCards = new ArrayList<Card>();
        cunseen = Util.readCardDatabase(cpuDeckFile);
        shuffledDeck = Util.readCardDatabase(cpuDeckFile);
        //intialize the unseen cards with the whole deck
        //copy to shuffledDeck
        
        //draw 7 cards into the hand from the deck/shuffledDeck
    }*/

}

