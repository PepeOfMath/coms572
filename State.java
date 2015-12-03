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
import cards.*;

public class State {
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
    }

}

