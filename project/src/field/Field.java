package field;

import cards.*;
import java.util.ArrayList;
import util.Util;
import java.util.Random;
import java.util.Collections;

/**
 * Represents one side of the game field, including hand, deck, etc.
 */
public class Field {

    public int deckCount;
    public int handCount;
    public int prizeCount;
    public int pkmnCount;
    
    public Position[] pkmnSlots;
    public int turnCount;
    
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
        
        turnCount = 0;
        
        deckCount = deck.size();
        handCount = 0;
        prizeCount= 0;
        pkmnCount = 0;
    }
    
    /**
     * Duplicate the Field
     * @param f
     * @param exactCopy
     * @param keepHand
     */
	@SuppressWarnings("unchecked")
	public Field(Field f, boolean exactCopy, boolean keepHand) {
    	//duplicate pkmnSlots, discard, unseenOp, deckCount, handCount, prizeCount, pkmnCount
    	discard = (ArrayList<Card>)f.discard.clone();
    	unseenOp = (ArrayList<Card>)f.unseenOp.clone();
    	deckCount = f.deckCount;
    	handCount = f.handCount;
    	prizeCount = f.prizeCount;
    	pkmnCount = f.pkmnCount;
    	
    	//copy Pokemon slots
    	pkmnSlots = new Position[6];
    	for (int i = 0; i < pkmnSlots.length; i++) {
    		if(f.pkmnSlots[i] != null) {
    			//Duplicate the Position
    			pkmnSlots[i] = new Position(f.pkmnSlots[i]);
    		}
    	}
		
    	//copy the deck, hand, prizes as needed, configure unseenMe
		if(exactCopy) {
			//Duplicate Everything
			deck = (ArrayList<Card>)f.deck.clone();
			hand = (ArrayList<Card>)f.hand.clone();
			prizes = (ArrayList<Card>)f.prizes.clone();
			unseenMe = (ArrayList<Card>)f.unseenMe.clone();
		} else {
			if (keepHand) {
				hand = (ArrayList<Card>)f.hand.clone();
				unseenMe = (ArrayList<Card>)f.unseenMe.clone();
				Collections.shuffle(unseenMe);
				
				//Just redraw the deck and prizes
				deck = new ArrayList<Card>();
				prizes = new ArrayList<Card>();
				for (int i = 0; i < prizeCount; i++) {
					prizes.add( unseenMe.get(i) );
				}
				for (int i = prizeCount; i < unseenMe.size(); i++) {
					deck.add( unseenMe.get(i) );
				}
			} else {
				//Redraw the deck, hand, and prizes
				deck = new ArrayList<Card>();
				prizes = new ArrayList<Card>();
				hand = new ArrayList<Card>();
				unseenMe = new ArrayList<Card>();
				Collections.shuffle(unseenOp);
				
				//Now draw
				for (int i = 0; i < prizeCount; i++) {
					prizes.add( unseenOp.get(i) );
					unseenMe.add( unseenOp.get(i) );
				}
				for (int i = prizeCount; i < prizeCount+handCount; i++) {
					hand.add( unseenOp.get(i) );
					unseenMe.add( unseenOp.get(i) );
				}
				for (int i = prizeCount+handCount; i < unseenOp.size(); i++) {
					deck.add( unseenOp.get(i) );
					deck.add( unseenOp.get(i) );
				}
			}
		}
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
    
    public boolean doSwitch(int slotNum) {
        if(slotNum < 1 || slotNum >= pkmnSlots.length) return false; //No such position
        if(pkmnSlots[slotNum] == null) return false; //No pokemon to switch with
        Pokemon p = pkmnSlots[0].getPokemon();
        if(p.retreatCost > pkmnSlots[0].getEnergyCount()) return false;
        
        //The retreat is valid
        //Remove the necessary energies (remove from Slot, move to Discard)
        for( int i = 0; i < p.retreatCost; i++ ) {
            discard.add( pkmnSlots[0].removeRandomEnergy() ); //Should be safe from null
        }
        //Swap, remove any status conditions
        Position tmp = pkmnSlots[0];
        tmp.stat = Status.NORMAL;
        pkmnSlots[0] = pkmnSlots[slotNum];
        pkmnSlots[slotNum] = tmp;
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
                unseenOp.remove(p);
                handCount--;
                pkmnCount++;
                return true;
            }
        }   
        return false; //No space available to play another basic pokemon
    }
    
    //Return true if the card is successfully used to evolve a Pokemon
    //TODO: assumes this is a valid Evolved Pokemon to play
    public boolean playEvolvPkmn(String name, int slotNum) {
        if(pkmnSlots[slotNum] == null) return false; //No Pokemon to Evolve
        Pokemon p = (Pokemon)findCardByName(name);
        System.out.println(p);
        boolean result = pkmnSlots[slotNum].evolveWith(p, turnCount);
        if (result) {
            hand.remove(p);
            unseenOp.remove(p);
            handCount--;
            return true;
        }
        return false;
    }
    
    //Return true if the card is successfully played.  Note, this is essentially just 
    //going to discard the Trainer card and return 
    //TODO assumes this card is known to be in the hand
    public boolean playTrainer(Trainer t) {
        hand.remove(t);
        unseenOp.remove(t);
        discard.add(t);
        handCount--;
        return true;
    }
    
    //Play the desired energy if possible
    public boolean playEnergy(String name, int slotNum) {
        Energy e = (Energy)findCardByName(name);
        if(e == null || pkmnSlots[slotNum] == null) return false; //Invalid play
        pkmnSlots[slotNum].addEnergy(e);
        hand.remove(e);
        unseenOp.remove(e);
        handCount--;        
        return true;
    }
    
    //Returns the card with the given name, if it exists (null otherwise)
    public Card findCardByName(String name) {
        for(int i = 0; i < hand.size(); i++) {
            if( hand.get(i).name.equalsIgnoreCase(name) ) return hand.get(i);
        }
        return null;
    }
    
    //Check for fainted pokemon, discard cards as needed, return number fainted
    public int checkPokemon() {
        int count = 0;
        for (int i = 0; i < pkmnSlots.length; i++) {
            if(pkmnSlots[i] != null && pkmnSlots[i].isFaintedPokemon() ) {
                count++;
                //Remove the Pokemon
                Pokemon p = pkmnSlots[i].removeTopPokemon();
                while(p != null) {
                    discard.add(p);
                    p = pkmnSlots[i].removeTopPokemon();
                }
                Energy e = pkmnSlots[i].removeLastEnergy();
                while(e != null) {
                    discard.add(e);
                    e = pkmnSlots[i].removeLastEnergy();
                }
                pkmnSlots[i] = null;
                pkmnCount--;
            }
        }
        return count;
    }
    
    //Draw a number of prize cards into the hand
    public int drawPrizes(int rewards) {
        for (int i = 0; i < rewards; i++) {
            if (prizes.size() == 0) return i;
            Card c = prizes.remove( prizes.size()-1 );
            hand.add( c );
            unseenMe.remove( c );
            //hand.add( prizes.remove( prizes.size()-1 ) );
            handCount++;
            prizeCount--;
        }
        return rewards;
    }
    
    //Return true if there is currently a Pokemon in Position 0
    public boolean hasActivePokemon() {
        return !(pkmnSlots[0] == null);
    }
    
    //If there is no currently active Pokemon, a new one is chosen at random
    public boolean chooseRandomActivePkmn() {
        if(hasActivePokemon()) return true;
        Random r = new Random();
        int j = (pkmnCount==0) ? -1 : r.nextInt(pkmnCount);
        for (int i = 1; i < pkmnSlots.length; i++) {
            if(pkmnSlots[i] != null) {
                if(j==0) {
                    pkmnSlots[0] = pkmnSlots[i];
                    pkmnSlots[i] = null;
                    return true;
                }
                j--;
                
            }
        }
        return false;
    }
    
    //Get a random card from the deck and put it into the hand (by type)
    //Supported types:
    // E: Energy card
    // 1: Draw a card
    // V: Evolution card
    public boolean getCardFromDeck(char c) {
        if (c == 'E') {
            for (int i = 0; i < deck.size(); i++) {
                if(deck.get(i) instanceof Energy) {
                	Card cc = deck.remove(i);
                	hand.add( cc );
                	unseenMe.remove( cc );
                    //hand.add( deck.remove(i) );
                    deckCount--;
                    handCount++;
                    Collections.shuffle(deck);
                    return true;
                }
            }
        } else if (c == '1') {
            return drawCardToHand();
        } else if (c == 'V') {
            for (int i = 0; i < deck.size(); i++) {
                if(deck.get(i) instanceof Pokemon && !((Pokemon)deck.get(i)).evolvesFrom.equals("Null")) {
                    Card cc = deck.remove(i);
                    hand.add( cc );
                    unseenMe.remove( cc );
                	//hand.add( deck.remove(i) );
                    deckCount--;
                    handCount++;
                    Collections.shuffle(deck);
                    return true;
                }
            
            }
        }
        return false;
    }
    
    //Similar to above, but we get a card from the discard pile and bring it back to the hand
    //Supported types:
    // E: Energy card
    public boolean getCardFromDisc(char c) {
        if (c == 'E') {
            for (int i =0; i < discard.size(); i++) {
                if(discard.get(i) instanceof Energy) {
                	Card cc = discard.remove(i);
                	hand.add( cc );
                	unseenOp.add( cc );
                    //hand.add( discard.remove(i) );
                    handCount++;
                    return true;
                }
            }
        }
        return false;
    }
}
