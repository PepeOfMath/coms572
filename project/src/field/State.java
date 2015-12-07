package field;

import java.util.ArrayList;

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
 
import java.util.Collections;
import cards.*;
import util.*;
import java.util.Random;

public class State {



    public Field playerOneF; //The human player, for now
    public Field playerTwoF; //The cpu player, for now
    public int turnCount;
    public boolean playedEnergy; //Three toggles which should reset on each turn
    public boolean playedSupporter;
    public boolean performedSwitch;
    
    public boolean playerOneTurn;
    public boolean gameOver;
    
    public State(String p1DeckFile, String p2DeckFile) {
        playerOneF = new Field(p1DeckFile);
        playerTwoF = new Field(p2DeckFile);
        turnCount = 0;
        
        playedEnergy = false;
        playedSupporter = false;
        performedSwitch = false;
        
        playerOneTurn = true;
        gameOver = false;
    }
    
    /**
     * Duplicate the game State, with some potential modifications
     * @param s	State to be duplicated
     * @param playerOne Which player is currently active
     * @param exactCopy Whether to create an exact copy, or a modified version for prediction purposes
     */
    public State(State s, boolean playerOne, boolean exactCopy) {
    	//If exactCopy, duplicate everything from the two Fields exactly
    	//If not,
    	//For the current player, we copy the hand,discard exactly, and reshuffle/get the deck,prizes
        //For the opponent, we copy discard exactly, and reshuffle/get the deck,hand,prizes
    	playerOneF = new Field(s.playerOneF, exactCopy, playerOne);
    	playerTwoF = new Field(s.playerTwoF, exactCopy, !playerOne);
    		
    	//Copy other variables as well
    	turnCount = s.turnCount;
    	playedEnergy = s.playedEnergy;
    	playedSupporter = s.playedSupporter;
    	performedSwitch = s.performedSwitch;
    	
    	playerOneTurn = s.playerOneTurn;
    	gameOver = s.gameOver;
    }
    
    /**
     * Perform the initial draw for one player.  Return the number of attempts needed before a basic Pokemon was obtained.
     * @param playerOne If the current player is playerOne or not
     * @return Number of hand draw attempts needed
     */
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
        if (c == null) return false; //no such card
        if (slotNum < 0 || slotNum > f.pkmnSlots.length) return false; //invalid slot number
        if (c instanceof Energy) {
            return playEnergy(playerOne, name, slotNum);
        } else if (c instanceof Trainer) {
            Trainer t = (Trainer)f.findCardByName(name);
            if (t.isSupporter && playedSupporter) return false; //Can't play two supporters
            
            //We require the slot number to have a Pokemon in case the card has effects
            if (f.pkmnSlots[slotNum] == null) return false; //Attempted to play on an invalid slot
            String e = t.cardEffect;
            //If the effect is valid, then we discard the card, else don't
            if (t.isSupporter) playedSupporter = true;
            boolean result = f.playTrainer(t);
            if (result) doTrainerEffect(playerOne, slotNum, e);
            return result;
        } else {//Pokemon Card
            if( ((Pokemon)c).evolvesFrom.equals("Null") ) { //Basic Pokemon
                return playBasicPkmn(playerOne, name);
            } else { //Attempt to do an Evolution at the given slot
                return playEvolvPkmn(playerOne, name, slotNum);
            }
        }
    }
    
    //Handle using an Attack
    public boolean doAttack(boolean playerOne, int choice, boolean silent) {
        Field f = playerOne ? playerOneF : playerTwoF;
        Field f2= playerOne ? playerTwoF : playerOneF;
        
        Pokemon p = f.pkmnSlots[0].getPokemon();
        //check for valid energy quantity.  If not enough, return false
        boolean good = Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(choice) );
        if (!good) {
            if (!silent) System.out.println("Insufficient Energy for Attack");
            return false;
        }
        if (f2.pkmnSlots[0] == null) {
        	if (!silent) System.out.println("Opponent has No Active Pokemon");
            return false;
        }
        if (f.pkmnSlots[0].stat == Status.ASLEEP || f.pkmnSlots[0].stat == Status.PARALYZED) {
        	if (!silent) System.out.println("Status Prevents Attack - Turn Ending");
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
                        Energy e = f2.pkmnSlots[0].removeRandomEnergy();
                        if (!(e == null) ) f2.discard.add( e );
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
    public int checkPokemon(boolean silent) {
        Field f = playerOneF;
        Field f2 = playerTwoF;
        
        //Check Field for fainted Pokemon, Discard, Return Count
        int faint1 = f.checkPokemon();
        int faint2 = f2.checkPokemon();
        
        //Draw Prize Cards
        f.drawPrizes(faint2);
        f2.drawPrizes(faint1);
        
        //If No Active Pokemon, Attempt to Replace
        boolean hasActive1 = true;
        boolean hasActive2 = true;
        if (!f.hasActivePokemon()) {
        	if (!silent) System.out.println("Replacing Active Pokemon Randomly");
            hasActive1 = f.chooseRandomActivePkmn();
        }
        if (!f2.hasActivePokemon()) {
        	if (!silent) System.out.println("Replacing Active Pokemon Randomly");
            hasActive2 = f2.chooseRandomActivePkmn();
        }
        
        int pOneWin = (f.prizeCount == 0 || !hasActive2) ? 1 : 0;
        int pTwoWin = (f2.prizeCount == 0 || !hasActive1) ? 2 : 0;
        //If Either Player has No Active Pokemon or No Prizes, we can declare End of Game (interpret)
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
        System.out.println("----------------------------------------------");
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
        System.out.println("----------------------------------------------");
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
    
    /**
     * Process a command
     * @param cmd The command to process
     * @param silent True indicates not to print any info statements
     */
    public void handleCommand(String cmd, boolean silent) {
    	//Temporary variables to support the copy/paste
    	boolean contin = true;
    	boolean cpuPlayer = !playerOneTurn;
    	
    	//Read and execute the command.
        if(cmd.startsWith("stop")) {
        	if (!silent) Util.printBlock("Terminating Game");
            contin = false;
            
        } else if(cmd.startsWith("end turn")) {
        	contin = Util.endTurnAction(this, cpuPlayer, silent);
        	cpuPlayer = !cpuPlayer;
            
        } else if(cmd.startsWith("attack")) {
            //Single parameter {1 or 2} to decide which attack is used
            int choice = Integer.parseInt( cmd.substring("attack".length()).trim() );
            if( this.doAttack(!cpuPlayer, choice, silent) ) {
            	contin = Util.evaluateCheckPokemon( this.checkPokemon(silent) , silent );
            	if(contin) {
                	//Handle ending the turn
                	contin = Util.endTurnAction(this, cpuPlayer, silent);
                	cpuPlayer = !cpuPlayer;
            	}
            } else {
            	if (!silent) Util.printBlock("Invalid Attack");
            }
            
        } else if(cmd.startsWith("switch")) {
            //Extract a numerical parameter between 1-5 indicating the position to switch to
            int slotNum = Integer.parseInt( cmd.substring("switch".length()).trim() );
            if (this.doSwitch(!cpuPlayer, slotNum)) {
            	if (!silent) Util.printBlock("Switched Active Pokemon");
            } else {
            	if (!silent) Util.printBlock("Invalid Switch");
            }
            
        } else if(cmd.startsWith("play")) {
            //Extract the number parameter
            //Extract the card name
            String trimmed = cmd.substring("play".length()).trim();
            int val = trimmed.indexOf(" "); //May not be relevant for some cards
            int slotNum = Integer.parseInt( trimmed.substring(0,val) );
            String cardName = trimmed.substring(val).trim();
            if (this.playCard(!cpuPlayer, cardName, slotNum)) {
            	if (!silent) Util.printBlock("Played Card " + cardName);
            } else {
            	if (!silent) Util.printBlock("Invalid Play");
            }
            
        } else {
        	if (!silent) Util.printBlock("Invalid Action");
        }
        
        //Re-update the State variables
        gameOver = !contin;
        playerOneTurn = !cpuPlayer;
    }
    
    //Get only valid switch commands
    public ArrayList<String> getAllSwitchMoves(boolean playerOne) {
    	ArrayList<String> commands = new ArrayList<String>();
    	Field f = playerOne ? playerOneF : playerTwoF;
    	Pokemon p = f.pkmnSlots[0].getPokemon();
    	
    	//Switches (check energy requirements, add only for other benched pokemon)
    	if (!performedSwitch && p.retreatCost <= f.pkmnSlots[0].getEnergyCount()) {
    		for (int i = 1; i < f.pkmnSlots.length; i++) {
    			if (f.pkmnSlots[i] != null) commands.add("switch " + i);
    		}
    	}
    	
    	return commands;
    }
    
    //Get only valid attack commands
    public ArrayList<String> getAllAttackMoves(boolean playerOne) {
    	ArrayList<String> commands = new ArrayList<String>();
    	Field f = playerOne ? playerOneF : playerTwoF;
    	Pokemon p = f.pkmnSlots[0].getPokemon();
    	
    	//Attacks: check energy requirements before adding the command
        if ( Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(1) ) ) commands.add("attack 1");
        if ( Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(2) ) ) commands.add("attack 2");
        
        return commands;
    }
    
    //Get only valid play commands
    public ArrayList<String> getAllPlayMoves(boolean playerOne) {
    	ArrayList<String> commands = new ArrayList<String>();
    	Field f = playerOne ? playerOneF : playerTwoF;
    	Pokemon p = f.pkmnSlots[0].getPokemon();
    	
    	//Play card
    	for (int i = 0; i < f.handCount; i++) {
    		Card c = f.hand.get(i);
    		if (c instanceof Energy && !playedEnergy) {
        		for (int j = 0; j < f.pkmnSlots.length; j++) {
        			if (f.pkmnSlots[j] != null) commands.add("play " + j + " " + c.name);
        		}
    		} else if (c instanceof Trainer) {
    			Trainer t = (Trainer)c;
    			if (!(t.isSupporter && playedSupporter)) {
    				if (!t.targetsPokemon) {
    					commands.add("play 0 " + c.name);
    				} else {
    					for (int j = 0; j < f.pkmnSlots.length; j++) {
    	        			if (f.pkmnSlots[j] != null) commands.add("play " + j + " " + c.name);
    	        		}
    				}
    			}
    		} else if (c instanceof Pokemon) {
    			Pokemon p2 = (Pokemon)c;
    			if ( p2.isBasic() && (f.pkmnCount < f.pkmnSlots.length) ) {
    				commands.add("play 0 " + p2.name);
    			} else if ( !p2.isBasic() ) {
    				for (int j = 0; j < f.pkmnSlots.length; j++) {
    					//Check if the Pokemon can be evolved
    					if (f.pkmnSlots[j] != null && f.pkmnSlots[j].canEvolveWith(p2, turnCount)) commands.add("play " + j + " " + c.name);
    				}
    			}
    		}
    	}
    	
    	return commands;
    }
    
    /**
     * Generate a list of possible moves for the given player
     * @param playerOne The current player
     * @return List of moves
     */
    public ArrayList<String> getAllMoves(boolean playerOne) {
    	ArrayList<String> commands = new ArrayList<String>();
    	Field f = playerOne ? playerOneF : playerTwoF;
    	Pokemon p = f.pkmnSlots[0].getPokemon();
    	
    	//Play card
    	for (int i = 0; i < f.handCount; i++) {
    		Card c = f.hand.get(i);
    		if (c instanceof Energy && !playedEnergy) {
        		for (int j = 0; j < f.pkmnSlots.length; j++) {
        			if (f.pkmnSlots[j] != null) commands.add("play " + j + " " + c.name);
        		}
    		} else if (c instanceof Trainer) {
    			Trainer t = (Trainer)c;
    			if (!(t.isSupporter && playedSupporter)) {
    				if (!t.targetsPokemon) {
    					commands.add("play 0 " + c.name);
    				} else {
    					for (int j = 0; j < f.pkmnSlots.length; j++) {
    	        			if (f.pkmnSlots[j] != null) commands.add("play " + j + " " + c.name);
    	        		}
    				}
    			}
    		} else if (c instanceof Pokemon) {
    			Pokemon p2 = (Pokemon)c;
    			if ( p2.isBasic() && (f.pkmnCount < f.pkmnSlots.length) ) {
    				commands.add("play 0 " + p2.name);
    			} else if ( !p2.isBasic() ) {
    				for (int j = 0; j < f.pkmnSlots.length; j++) {
    					//Check if the Pokemon can be evolved
    					if (f.pkmnSlots[j] != null && f.pkmnSlots[j].canEvolveWith(p2, turnCount)) commands.add("play " + j + " " + c.name);
    				}
    			}
    		}
    	}
    	
    	//Switches (check energy requirements, add only for other benched pokemon)
    	if (!performedSwitch && p.retreatCost <= f.pkmnSlots[0].getEnergyCount()) {
    		for (int i = 1; i < f.pkmnSlots.length; i++) {
    			if (f.pkmnSlots[i] != null) commands.add("switch " + i);
    		}
    	}
    	
    	//Attacks: check energy requirements before adding the command
        if ( Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(1) ) ) commands.add("attack 1");
        if ( Util.hasSufficientEnergy( f.pkmnSlots[0].determineEnergy(), p.getAttackCost(2) ) ) commands.add("attack 2");
    	
    	//Can always end turn
    	commands.add("end turn");
    	
    	return commands;
    }
    
    //Score game from the perspective of the person whose turn it is
    public int scoreGame() {
    	Field f = playerOneTurn ? playerOneF : playerTwoF;
    	Field f2 = playerOneTurn ? playerTwoF : playerOneF;
    	return f.evaluateField() - f2.evaluateField();
    }
}
