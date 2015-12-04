package field;

import cards.*;
import java.util.ArrayList;
import util.Util;

/**
 * Represents one side of the game field, including hand, deck, etc.
 */
public class Field {

    public int deckCount;
    public int handCount;
    public int prizeCount;
    public int pkmnCount;
    
    public Position[] pkmnSlots;
    
    
    public ArrayList<Card> deck;
    public ArrayList<Card> hand;
    public ArrayList<Card> prizes;
    public ArrayList<Card> discard;
    
    //May need another list of cards unseen by one or the other player
    //Will have to manage how to add and remove from these lists later
    public ArrayList<Card> unseenMe; //unseen by this player (deck + prizes)
    public ArrayList<Card> unseenOp; //unseen by the opponent(deck + prizes + hand)
    
    
    /**
     * Initialize as if starting a new game (so all cards go in the deck and unseen lists)
     *
     * Later, we may wish to have a copy constructor or something
     */
    public Field(String deckFile) {
        pkmnSlots = new Position[6];
        deck    = Util.readCardDatabase(deckFile); //eventually, shuffle
        hand    = new ArrayList<Card>();
        prizes  = new ArrayList<Card>();
        discard = new ArrayList<Card>();
        
        unseenMe= Util.readCardDatabase(deckFile);
        unseenOp= Util.readCardDatabase(deckFile);
        
        deckCount = deck.size();
        handCount = 0;
        prizeCount= 0;
        pkmnCount = 0;
    }
}
