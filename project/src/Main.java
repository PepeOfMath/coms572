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
        boolean playerOne = true;
        boolean cpuControl1 = true;
        boolean cpuControl2 = true;
        String cmd;
        AI agentOne = new RandomPlAI(); //Instantiate AI here
        AI agentTwo = new RandomPlAI();
        
        Util.printBlock("Press Enter to Begin");
        while(!s.hasNextLine());
        s.nextLine();
        State game;
        
        while(true) {
        	//Start up
        	Util.printBlock("Is Player 1 a CPU Player? (Y/N)");
        	Util.prompt(playerOne);
        	while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("y")) {
                cpuControl1 = false;
                Util.printBlock("Player 1 is CPU");
            } else {
            	cpuControl1 = true;
            	agentOne = new Human(s);
            	Util.printBlock("Player 1 is Human");
            }
            
            //TODO: currently unused
            Util.printBlock("Is Player 2 a CPU Player? (Y/N)");
            Util.prompt(!playerOne);
            while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("y")) {
                cpuControl2 = false;
                Util.printBlock("Player 2 is CPU");
            } else {
            	cpuControl2 = true;
            	agentTwo = new Human(s);
            	Util.printBlock("Player 2 is Human");
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
            
            String toPlay = "";
            boolean canEnd = false;
            if(true /*cpuControl1*/) {
	            Util.printBlock("Player 1: Choose Basic Pokemon To Begin");
	            Util.printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
	            game.printHand(true);
	            while(!toPlay.toLowerCase().equals("done")) {
	            	Util.prompt(true);
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
            } else {
            	//TODO have AI choose Pokemon
            	Util.printBlock("TODO: AI Selects Basic Pokemon");
            }
            
            //TODO (high priority)
            //Idea: probably use heuristics here to choose 1-3 starting pokemon based on the hand
            if(true /*cpuControl2*/) {//TODO switch condition when ready
            	Util.printBlock("Player 2: Choose Basic Pokemon To Begin");
                Util.printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
                game.printHand(false);
                toPlay = "";
                canEnd = false;
                while(!toPlay.toLowerCase().equals("done")) {
                	Util.prompt(false);
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
            game.drawCardsToHand(playerOne, 1);
            
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
                //Maybe print game state before each action
            	Util.printBlock("CURRENT GAME STATE");
                game.printState(playerOne, (playerOne && cpuControl1) || (!playerOne && cpuControl2));
                
                Util.prompt(playerOne);
                if (playerOne) {
                	cmd = agentOne.chooseAction(new State(game, false));
                	
                	if (!cpuControl1) {
                		Util.printBlock("TODO: ask AI for action");
                		//Display the command and insert a pause
                        System.out.print(cmd);
                        while(!s.hasNextLine());
                        s.nextLine();
                	}
                } else {
                	cmd = agentTwo.chooseAction(new State(game, false));
                	
                	if (!cpuControl2) {
                		Util.printBlock("TODO: ask AI for action");
                		//Display the command and insert a pause
                        System.out.print(cmd);
                        while(!s.hasNextLine());
                        s.nextLine();
                	}
                }
                
                //Do a quick validation of inputs first
                if (!Util.validateCommand(cmd)) cmd = ""; //Makes it clearly invalid so no errors occur
                
                game.handleCommand(cmd, false); //Silent is false so the user receives all extra info
                contin = !game.gameOver;
                playerOne = game.playerOneTurn;
            }
            
            //Decide if we want to restart the game
            Util.printBlock("FINAL GAME STATE");
            game.printState(!cpuPlayer, (playerOne && !cpuControl1) || (!playerOne && !cpuControl2));
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