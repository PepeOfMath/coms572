//A class to control the main program flow

import java.util.Scanner;
import field.*;
import util.*;
import ai.*;

public class Main {
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String deckFilePath = "src/data/database.txt"; //TODO: for now, this path is hardcoded
        boolean cpuPlayer = false;
        boolean cpuControl= false;
        String cmd;
        AI agent = new RandomPlAI(); //Instantiate AI here
        
        Util.printBlock("Press Enter to Begin");
        while(!s.hasNextLine());
        s.nextLine();
        State game;
        
        while(true) {
            //Start up
        	Util.printBlock("Would you like to control the CPU Player? (Y/N)");
        	Util.prompt(cpuPlayer);
            while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("y")) {
                cpuControl = true;
                Util.printBlock("Player is Controlling the Computer");
            } else {
            }
            
            
            //Set up the initial game State
            Util.printBlock("Initializing Game");
            game = new State(deckFilePath, deckFilePath);
            
            //Now we need to do preliminary game setup
            // This includes these actions:
            // * Shuffle Deck
            //   Draw a 7 card hand
            //   If there are basic Pokemon, 
            //     set up prizes
            //     (play some basic Pokemon ... probably needs some AI)
            //   If there are no basic Pokemon,
            //     restart at step *
            Util.printBlock("Drawing Hands");
            Util.printBlock("CPU draw");
            int cpuTries = game.initialDraw(false);
            Util.printBlock("Hu draw");
            int humTries = game.initialDraw(true); //Human is player 1
            if (cpuTries > humTries) {
                //TODO: CPU may take bonus cards (low priority)
            	Util.printBlock("TODO: CPU may take up to " + 2*(cpuTries-humTries) + " bonus cards");
            } else if (cpuTries < humTries) {
                //TODO: Human may take bonus cards
            	Util.printBlock("TODO: Human may take up to " + 2*(humTries-cpuTries) + " bonus cards");
            }
            
            Util.printBlock("Player 1: Choose Basic Pokemon To Begin");
            Util.printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
            game.printHand(true);
            String toPlay = "";
            boolean canEnd = false;
            while(!toPlay.toLowerCase().equals("done")) {
            	Util.prompt(false);
                while(!s.hasNextLine());
                toPlay = s.nextLine().trim();
                //interpret and attempt to play cards
                boolean success = game.playBasicPkmn(true, toPlay);
                if(success) {
                    canEnd = true;
                } else if (!canEnd || !toPlay.toLowerCase().equals("done")) {
                    toPlay = "";
                    Util.printBlock("Invalid Choice - No Card Played");
                }
            }
            
            //TODO (high priority)
            //Idea: probably use heuristics here to choose 1-3 starting pokemon based on the hand
            if(true /*cpuControl*/) {//TODO switch condition when ready
            	Util.printBlock("Player 2: Choose Basic Pokemon To Begin");
                Util.printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
                game.printHand(false);
                toPlay = "";
                canEnd = false;
                while(!toPlay.toLowerCase().equals("done")) {
                	Util.prompt(true);
                    while(!s.hasNextLine());
                    toPlay = s.nextLine().trim();
                    //interpret and attempt to play cards
                    boolean success = game.playBasicPkmn(false, toPlay);
                    if(success) {
                        canEnd = true;
                    } else if (!canEnd || !toPlay.toLowerCase().equals("done")) {
                        toPlay = "";
                        Util.printBlock("Invalid Choice - No Card Played");
                    }
                }
            } else {
                //TODO have AI choose pokemon
            	Util.printBlock("TODO: AI Selects Basic Pokemon");
            }
            
            
            //Now Player One begins by drawing a Card, which should not fail
            game.drawCardsToHand(!cpuPlayer, 1);
            
            //Loop for commands
            Util.printBlock("Press Enter to Begin Game");
            while(!s.hasNextLine());
            s.nextLine();
            
            boolean contin = true;
            while(contin) {
                /*
                 * Current Actions:
                 *   "stop"         ends the game immediately
                 *   "end turn"     player ends turn, control switches, between turn effects happen
                 *   "attack ..."   use an attack attack # (1 or 2)
                 *   "switch ..."   switch the active Pokemon switch position#
                 *   "play ..."     play a card: play position# cardName
                 */
                //TODO: maybe print game state before each action
            	Util.printBlock("CURRENT GAME STATE");
                game.printState(!cpuPlayer, !cpuPlayer || cpuControl);
                
                
                Util.prompt(cpuPlayer);
                if (cpuPlayer && !cpuControl) {
                    //TODO (high priority) have AI choose an action
                	Util.printBlock("TODO: ask AI for action");
                    cmd = agent.chooseAction(game, !cpuPlayer);
                    System.out.println("Theoretical Action: " + cmd);
                    cmd = "end turn";
                } else {
                    while(!s.hasNextLine());
                    cmd = s.nextLine().toLowerCase();
                }
                
                //Do a quick validation of inputs first
                if (!Util.validateCommand(cmd)) cmd = ""; //Makes it clearly invalid so no errors occur
                
                //Read and execute the command.
                if(cmd.startsWith("stop")) {
                	Util.printBlock("Terminating Game");
                    contin = false;
                    
                } else if(cmd.startsWith("end turn")) {
                	Util.printBlock("Ending Turn");
                    //Toggle player control
                    cpuPlayer = !cpuPlayer;
                    
                    //Handle between turn effects
                    game.processStatus(!cpuPlayer);
                    contin = Util.evaluateCheckPokemon( game.checkPokemon() );
                    
                    //Begin next player's turn
                    game.resetSwitches(cpuPlayer);
                    int ncard = game.drawCardsToHand(!cpuPlayer, 1);
                    if (ncard == 0) { //The current player loses, and the game ends
                        contin = false;
                        Util.printBlock("Player " + (cpuPlayer ? 1 : 2) + " Wins!");
                    }
                    
                } else if(cmd.startsWith("attack")) {
                    //Single parameter {1 or 2} to decide which attack is used
                    int choice = Integer.parseInt( cmd.substring("attack".length()).trim() );
                    if( game.doAttack(!cpuPlayer, choice) ) {
                        //Toggle Player control
                        cpuPlayer = !cpuPlayer;
                        
                        //Handle between turn effects
                        game.processStatus(!cpuPlayer);
                        contin = Util.evaluateCheckPokemon( game.checkPokemon() );
                        
                        //Begin next player's turn
                        game.resetSwitches(cpuPlayer);
                        int ncard = game.drawCardsToHand(!cpuPlayer, 1);
                        if (ncard == 0) { //The current player loses, and the game ends
                            contin = false;
                            Util.printBlock("Player " + (cpuPlayer ? 1 : 2) + " Wins!");
                        }
                    } else {
                    	Util.printBlock("Invalid Attack");
                    }
                    
                } else if(cmd.startsWith("switch")) {
                    //Extract a numerical parameter between 1-5 indicating the position to switch to
                    int slotNum = Integer.parseInt( cmd.substring("switch".length()).trim() );
                    if (game.doSwitch(!cpuPlayer, slotNum)) {
                    	Util.printBlock("Switched Active Pokemon");
                    } else {
                    	Util.printBlock("Invalid Switch");
                    }
                    
                } else if(cmd.startsWith("play")) {
                    //Extract the number parameter
                    //Extract the card name
                    String trimmed = cmd.substring("play".length()).trim();
                    int val = trimmed.indexOf(" "); //May not be relevant for some cards
                    int slotNum = Integer.parseInt( trimmed.substring(0,val) );
                    String cardName = trimmed.substring(val).trim();
                    if (game.playCard(!cpuPlayer, cardName, slotNum)) {
                    	Util.printBlock("Played Card " + cardName);
                    } else {
                    	Util.printBlock("Invalid Play");
                    }
                    
                } else {
                	Util.printBlock("Invalid Action");
                }
            }
            
            //Decide if we want to restart the game
            Util.printBlock("Would you like to play a new game? (Y/N)");
            Util.prompt(cpuPlayer);
            while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("n")) {
            	Util.printBlock("Shutting Down");
                s.close();
                return;
            }
        }
    }
}