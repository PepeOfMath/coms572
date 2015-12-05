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
        
        //unseenMe= Util.readCardDatabase(deckFile);
        //unseenOp= Util.readCardDatabase(deckFile);
        //Updated strategy means we only have one copy of the cards
        unseenMe = new ArrayList<Card>(deck.size());
        unseenOp = new ArrayList<Card>(deck.size());
        for (int i = 0; i < deck.size(); i++) {
            unseenMe.add(deck.get(i));
            unseenOp.add(deck.get(i));
        }
        
        
        
        deckCount = deck.size();
        handCount = 0;
        prizeCount= 0;
        pkmnCount = 0;
    }
    
    //This should never fail because it happens at the beginning of the game
    public boolean drawPrizeCards() {
        for (int i = 0; i < 6; i++) {
            Card c = (Card)deck.remove( deck.size()-1 );
            prizes.add(c);
            deckCount--;
            prizeCount++;
        }
        return true;
    }
    
    //Return true if a card is successfully drawn
    public boolean drawCardToHand() {
        //move card to hand
        //remove card from the deck
        //remove card from the unseenMe set
        if (deck.size() == 0) return false;
        Card c = (Card)deck.remove( deck.size()-1 );
        unseenMe.remove(c);
        hand.add(c);
        deckCount--;
        handCount++;
        return true;
    }
    
    //Return true if a card with the specified name is contained in the hand
    public boolean hasCardInHand(String name) {
        return findCardByName(name) != null;
    }
    
    //Return true if the specified card is in the hand and it is a basic pokemon
    public boolean isCardInHandBasicPkmn(String name) {
        Card c = findCardByName(name);
        if(c == null || !(c instanceof Pokemon)) return false;
        Pokemon p = (Pokemon)c;
        return p.evolvesFrom.equals("Null");
    }
    
    //Return true if the card is successfully placed in an empty Position
    //TODO: assumes this is a valid Basic Pokemon to play
    public boolean playBasicPkmn(String name, int turnNumber) {
        Card c = findCardByName(name);
        Pokemon p = (Pokemon)c;
        for(int i = 0; i < pkmnSlots.length; i++) {
            if(pkmnSlots[i] == null) {
                pkmnSlots[i] = new Position(p, turnNumber);
                hand.remove(p);
                handCount--;
                return true;
            }
        }   
        return false; //No space available to play another basic pokemon
    }
    
    //Returns the card with the given name, if it exists (null otherwise)
    private Card findCardByName(String name) {
        for(int i = 0; i < hand.size(); i++) {
            if( hand.get(i).name.equalsIgnoreCase(name) ) return hand.get(i);
        }
        return null;
    }
}
