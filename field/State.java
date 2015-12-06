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
import java.util.Random;

public class State {



    public Field playerOneF; //The human player, for now
    public Field playerTwoF; //The cpu player, for now
    public int turnCount;
    private boolean toIncrement; //Used to increment turnCount only every other turn
    public boolean playedEnergy; //Three toggles which should reset on each turn
    public boolean playedSupporter;
    public boolean performedSwitch;
    
    public State(String p1DeckFile, String p2DeckFile) {
        playerOneF = new Field(p1DeckFile);
        playerTwoF = new Field(p2DeckFile);
        turnCount = 0;
        toIncrement = false;
        
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
        Field f = playerOne ? playerOneF : playerTwoF;
        return f.playEvolvPkmn(name, slotNum);
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
        boolean result = f.doSwitch(slotNum);
        performedSwitch = result;
        return result;
    }
    
    //Handle playing a new card from the hand
    public boolean playCard(boolean playerOne, String name, int slotNum) {
        Field f = playerOne ? playerOneF : playerTwoF;
        //get the card
        Card c = f.findCardByName(name);
        if (c instanceof Energy) {
            return playEnergy(playerOne, name, slotNum);
        } else if (c instanceof Trainer) {
            System.out.println("Handling Trainer by Discarding - No Effect");
            Trainer t = (Trainer)f.findCardByName(name);
            if (t.isSupporter && playedSupporter) return false; //Can't play two supporters
            String e = t.cardEffect;
            //If the effect is valid, then we discard the card, else don't
            if (t.isSupporter) playedSupporter = true;
            return f.playTrainer(t);
            //TODO handle trainer cards
            //Place the card correctly, and grab an Effect to deal with.  This could be complicated
        } else {//Pokemon Card
            if( ((Pokemon)c).evolvesFrom.equals("Null") ) { //Basic Pokemon
                System.out.println("!!  Attempting to play " + name);
                return playBasicPkmn(playerOne, name);
            } else { //Attempt to do an Evolution at the given slot
                System.out.println("!!  Attempting to evolve to " + name);
                return playEvolvPkmn(playerOne, name, slotNum);
            }
        }
    }
    
    //Handle using an Attack
    public boolean doAttack(boolean playerOne, int choice) {
        Field f = playerOne ? playerOneF : playerTwoF;
        Field f2= playerOne ? playerTwoF : playerOneF;
        
        Pokemon p = f.pkmnSlots[0].getPokemon();
        //check for valid energy quantity.  If not enough, return false
        boolean good = Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(choice) );
        if (!good) {
            System.out.println("Insufficient Energy for Attack");
            return false;
        }
        if (f2.pkmnSlots[0] == null) {
            System.out.println("Opponent has No Active Pokemon");
            return false;
        }
        if (f.pkmnSlots[0].stat == Status.ASLEEP || f.pkmnSlots[0].stat == Status.PARALYZED) {
            System.out.println("Status Prevents Attack - Turn Ending");
            return true;
        }
        
        //get the attack effect and name
        System.out.println("Attacking with " + p.getAttackName(choice));
        String[] effects = p.getAttackEffect(choice).split(",");
        
        //apply the attack effect.  This section might be pulled into another method
        //to handle trainer card effects also
        int damageDone = 0;
        for (int i = 0; i < effects.length; i++) {
        
            if (effects[i].startsWith("doDamage")) {
                int amount = Integer.parseInt( effects[i].substring("doDamage".length()+1) );
                damageDone += f2.pkmnSlots[0].applyDamage(amount, p.type);
                
            } else if (effects[i].startsWith("healDamage")) {
                String s = effects[i].substring("healDamage".length()+1);
                int amount = (s.equals("EQ")) ? damageDone : Integer.parseInt(s);
                f.pkmnSlots[0].healDamage(amount);
                
            } else if (effects[i].startsWith("deckHand")) {
                char[] c = effects[i].substring("deckHand".length()+1).trim().toCharArray();
                for (int j = 0; j < c.length; j++) {
                    f.getCardFromDeck(c[j]);
                }
            
            } else if (effects[i].startsWith("doStatus")) {
                f2.pkmnSlots[0].stat = Status.valueOf( effects[i].substring("doStatus".length()+1).trim() );
                
            } else if (effects[i].startsWith("coin")) {
                String[] parts = effects[i].trim().split(":");
                int ncoin = Integer.parseInt(parts[1]);
                Random r = new Random();
                if ( (ncoin == 1 && r.nextDouble() > 0.5) || (ncoin == 2 && r.nextDouble() > 0.75) ) {
                    //Actually perform the effect
                    //Options: do damage, apply effect, discard energy
                    if (parts[2].equals("E")) {
                        f2.discard.add( f2.pkmnSlots[0].removeRandomEnergy() );
                    } else {
                        try {
                            int amount = Integer.parseInt( parts[2] );
                            damageDone += f2.pkmnSlots[0].applyDamage(amount, p.type);
                        } catch (NumberFormatException e) {
                            f2.pkmnSlots[0].stat = Status.valueOf( parts[2] );
                        }
                    }
                }

            }
        }
        //Fainting Results are handled separately (see the method below)
        return true;
    }
    
    public boolean doTrainerEffect(boolean playerOne, int choice, String effect) {
        Field f = playerOne ? playerOneF : playerTwoF;
        String[] effects = effect.split(",");
        for (int i = 0; i < effects.length; i++) {
        
            if (effects[i].startsWith("healDamage")) {
                String s = effects[i].substring("healDamage".length()+1);
                int amount = Integer.parseInt(s); //(s.equals("EQ")) ? damageDone : 
                if (choice >= 0 && choice <= f.pkmnSlots.length && f.pkmnSlots[choice] != null) {
                    f.pkmnSlots[choice].healDamage(amount);
                }
                
            } else if (effects[i].startsWith("deckHand")) {
                char[] c = effects[i].substring("deckHand".length()+1).trim().toCharArray();
                for (int j = 0; j < c.length; j++) {
                    f.getCardFromDeck(c[j]);
                }
                
            } else if (effects[i].startsWith("discHand")) {
                char[] c = effects[i].substring("discHand".length()+1).trim().toCharArray();
                for (int j = 0; j < c.length; j++) {
                    f.getCardFromDisc(c[j]);
                }
                
            }
        }
        
        return true;
    }
    
    //Each player checks for fainted pokemon, discards those, replaces the active pokemon randomly,
    //and reports back the number of faints.  Each player then draws prize cards.  This method returns
    //a number indicating if either player has won: 0 (no winner yet), 1 (player 1), 2 (player two),
    // 3 (draw)
    public int checkPokemon() {
        Field f = playerOneF;
        Field f2 = playerTwoF;
        
        //Check Field for fainted Pokemon, Discard, Return Count
        int faint1 = f.checkPokemon();
        int faint2 = f2.checkPokemon();
        
        //Draw Prize Cards
        int prize1 = f.drawPrizes(faint2);
        int prize2 = f2.drawPrizes(faint1);
        
        //If No Active Pokemon, Attempt to Replace
        boolean hasActive1 = true;
        boolean hasActive2 = true;
        if (!f.hasActivePokemon()) {
            System.out.println("Replacing Active Pokemon Randomly");
            hasActive1 = f.chooseRandomActivePkmn();
        }
        if (!f2.hasActivePokemon()) {
            System.out.println("Replacing Active Pokemon Randomly");
            hasActive2 = f2.chooseRandomActivePkmn();
        }
        
        int pOneWin = (f.prizeCount == 0 || !hasActive2) ? 1 : 0;
        int pTwoWin = (f2.prizeCount == 0 || !hasActive1) ? 2 : 0;
        //If Eiher Player has No Active Pokemon or No Prizes, we can declare End of Game (interpret)
        return pOneWin + pTwoWin;
    
    }
    
    public void processStatus(boolean playerOne) {
        playerOneF.pkmnSlots[0].processStatus(playerOne);
        playerTwoF.pkmnSlots[0].processStatus(!playerOne);
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
    public void resetSwitches(boolean playerOne) {
        Field f = playerOne ? playerOneF : playerTwoF;
        f.turnCount++;
        playedEnergy = false;
        playedSupporter = false;
        performedSwitch = false;
    }
}
