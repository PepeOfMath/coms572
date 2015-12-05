//A class to control the main program flow

import java.util.Scanner;
import field.*;

public class Main {
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String deckFilePath = "data/database.txt"; //TODO: for now, this path is hardcoded
        boolean cpuPlayer = false;
        boolean cpuControl= false;
        int ctrlFlag = 2;
        String cmd;
        
        printBlock("Press Enter to Begin");
        while(!s.hasNextLine());
        s.nextLine();
        State game;
        
        while(true) {
            //Start up
            printBlock("Would you like to control the CPU Player? (Y/N)");
            prompt(cpuPlayer);
            while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("y")) {
                cpuControl = true;
                printBlock("Player is Controlling the Computer");
            } else {
                printBlock("TODO: Instantiate AI");
            }
            
            
            //Set up the initial game State
            printBlock("Initializing Game");
            game = new State(deckFilePath, deckFilePath);
            
            //Now we need to do preliminary game setup
            // This includes these actions:
            // * Shuffle Deck
            //   Draw a 7 card hand
            //   If there are basic pokemon, 
            //     set up prizes
            //     (play some basic pokemon ... probably needs some AI)
            //   If there are no basic pokemon,
            //     restart at step *
            printBlock("Drawing Hands");
            printBlock("CPU draw");
            int cpuTries = game.initialDraw(false);
            printBlock("Hu draw");
            int humTries = game.initialDraw(true); //Human is player 1
            if (cpuTries > humTries) {
                //TODO: CPU may take bonus cards (low priority)
                printBlock("TODO: CPU may take bonus cards");
            } else if (cpuTries < humTries) {
                //TODO: Human may take bonus cards
                printBlock("TODO: Human may take bonus cards");
            }
            
            //TODO (high priority)
            //Idea: probably use heuristics here to choose 1-3 starting pokemon based on the hand
            printBlock("Player 1: Choose Basic Pokemon To Begin");
            printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
            game.printHand(true);
            String toPlay = "";
            boolean canEnd = false;
            while(!toPlay.toLowerCase().equals("done")) {
                prompt(false);
                while(!s.hasNextLine());
                toPlay = s.nextLine().trim();
                //interpret and attempt to play cards
                boolean success = game.playBasicPkmn(true, toPlay);
                if(success) {
                    canEnd = true;
                } else if (!canEnd || !toPlay.toLowerCase().equals("done")) {
                    toPlay = "";
                    printBlock("Invalid Choice - No Card Played");
                }
            }
            

            if(cpuControl) {
                printBlock("Player 2: Choose Basic Pokemon To Begin");
                printBlock("Enter one Pokemon Name at a Time; Use \"done\" to finish");
                game.printHand(false);
                toPlay = "";
                canEnd = false;
                while(!toPlay.toLowerCase().equals("done")) {
                    prompt(true);
                    while(!s.hasNextLine());
                    toPlay = s.nextLine().trim();
                    //interpret and attempt to play cards
                    boolean success = game.playBasicPkmn(false, toPlay);
                    if(success) {
                        canEnd = true;
                    } else if (!canEnd || !toPlay.toLowerCase().equals("done")) {
                        toPlay = "";
                        printBlock("Invalid Choice - No Card Played");
                    }
                }
            } else {
                //TODO have user (or AI) choose pokemon for player 2
                printBlock("TODO: AI Selects Basic Pokemon");
            }
            
            
            
            
            //Loop for commands
            printBlock("Press Enter to Begin Game");
            while(!s.hasNextLine());
            s.nextLine();
            
            boolean contin = true;
            while(contin) {
                /*
                 * Current Actions:
                 *   "stop"         ends the game immediately
                 *   "end turn"     player ends turn, control switches, between turn effects happen
                 *   "attack ..."   use an attack (exact syntax needs to be defined TODO)
                 *   "switch ..."   switch the active pokemon (TODO exact syntax)
                 *   "play ..."     play a card (TODO exact syntax)
                 */
                //TODO: maybe print game state before each action
                printBlock("CURRENT GAME STATE");
                game.printState(!cpuPlayer, !cpuPlayer || cpuControl);
                
                
                prompt(cpuPlayer);
                if (cpuPlayer && !cpuControl) {
                    //TODO (high priority) have AI choose an action
                    printBlock("TODO: ask AI for action");
                    cmd = "end turn";
                } else {
                    while(!s.hasNextLine());
                    cmd = s.nextLine().toLowerCase();
                }
                
                
                
                //Read and execute the command.
                if(cmd.startsWith("stop")) {
                    printBlock("Terminating Game");
                    contin = false;
                } else if(cmd.startsWith("end turn")) {
                    printBlock("Ending Turn");
                    //Toggle player control
                    cpuPlayer = !cpuPlayer;
                    //TODO Handle between turn effects
                } else if(cmd.startsWith("attack")) {
                    printBlock("Attacking!");
                    //TODO Handle attack
                    //Toggle Player control
                    cpuPlayer = !cpuPlayer;
                    //TODO Handle between turn effects
                } else if(cmd.startsWith("switch")) {
                    printBlock("Switching");
                    //TODO pay retreat cost
                    //switch order of pokemon, remove status effects
                } else if(cmd.startsWith("play")) {
                    printBlock("Playing a Card");
                    //TODO handle card stuff
                } else {
                    printBlock("Unrecognized Command");
                }
                
                
                //If the command is End Turn or Attack, toggle the cpuPlayer flag
            
                //prompt(cpuPlayer);
            }
            
            
            //Decide if we want to restart the game
            printBlock("Would you like to play a new game? (Y/N)");
            prompt(cpuPlayer);
            while(!s.hasNextLine());
            cmd = s.nextLine();
            if (cmd.toLowerCase().startsWith("n")) {
                printBlock("Shutting Down");
                return;
            }
        }
    
    }
    
    public static void prompt(boolean cpuPlayer) {
        if (cpuPlayer) {
            System.out.print("CPU >> ");
        } else {
            System.out.print("Hu  >> ");
        }
    
    }
    
    //Print with a small leading block
    public static void printBlock(String s) {
        System.out.println("|----| " + s);
    }
    



}
