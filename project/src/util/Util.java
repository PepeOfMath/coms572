package util;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import cards.*;
import java.io.File;
import field.*;

public final class Util {

    private Util() {
        System.out.println("NOPE");
    }
    
    
    //----------------------------Miscellaneous constants
    public static final int NUM_ENERGY_TYPES = 4; //number of energy types currently supported (DPWC)
    public static final int PLAYER_ONE_WIN = 1;
    public static final int PLAYER_TWO_WIN = 2;
    public static final int GAME_DRAW = 3;
    
    
    /**
     * Creates an ArrayList<Card> of cards from the provided database
     */
    public static ArrayList<Card> readCardDatabase(String s) {
        System.out.println("Reading Cards.  TODO: handle attack and trainer effects");
        
        String prefix = System.getProperty("user.dir") + File.separator;
        File f = new File(prefix + s);
        Scanner scan;
        try {
            scan = new Scanner(f);
        } catch(FileNotFoundException e) {
            throw new IllegalArgumentException("File Path is Invalid");
        }
        
        ArrayList<Card> deck = new ArrayList<Card>(60);
        //Card[] deck = new Card[60];
        int index = 0;
        while(scan.hasNextLine()) {
            //Begin reading card information
            //We will generally assume the database file is written correctly
            
            //We need the first two lines
            int count = getCount(scan.nextLine());
            String cardType = scan.nextLine();
            
            if (cardType.equals("Pokemon")) {
                String name = scan.nextLine().trim();
                Type type = Type.valueOf(scan.nextLine().trim().toUpperCase());
                int hp = Integer.parseInt(scan.nextLine().trim());
                String evolvesFrom = scan.nextLine().trim();
                Type weakness = Type.valueOf(scan.nextLine().trim().toUpperCase());
                Type resistance = Type.valueOf(scan.nextLine().trim().toUpperCase());
                int retreat = Integer.parseInt(scan.nextLine().trim());
                
                String atkI = scan.nextLine();
                int[] costI = determineCost(atkI.substring(0,atkI.indexOf(",")));
                atkI = atkI.substring(atkI.indexOf(",")+1);
                String atkIName = atkI.substring(0,atkI.indexOf(","));
                atkI = atkI.substring(atkI.indexOf(",")+1);
                
                //String[] atkIsplit = atkI.split(",");
                //int[] costI = determineCost(atkIsplit[0]);
                
                String atkII = scan.nextLine();
                int[] costII = determineCost(atkII.substring(0,atkII.indexOf(",")));
                atkII = atkII.substring(atkII.indexOf(",")+1);
                String atkIIName = atkII.substring(0,atkII.indexOf(","));
                atkII = atkII.substring(atkII.indexOf(",")+1);
                
                //String[] atkIIsplit = atkII.split(",");
                //int[] costII = determineCost(atkIIsplit[0]);
                
                for (int i = 0; i < count; i++) {
                    //deck[index] =
                    deck.add(new Pokemon(name, evolvesFrom, hp, type, weakness, resistance, retreat, costI, atkIName, atkI, costII, atkIIName, atkII)); //TODO: define an actual effect
                    index++;
                }
            
            
            } else if (cardType.equals("Trainer")) {
                String name = scan.nextLine().trim();
                boolean supporter = (scan.nextLine().equals("True"));
                boolean targetsPokemon = (scan.nextLine().equals("True"));
                String effect = scan.nextLine();
                
                for (int i = 0; i < count; i++) {
                    //deck[index] = 
                    deck.add(new Trainer(name, supporter, targetsPokemon, effect));
                    index++;
                }
            } else if (cardType.equals("Energy")) {
                String type = scan.nextLine().trim();
                for (int i = 0; i < count; i++) {
                    //deck[index] = 
                    deck.add(new Energy(type));
                    index++;
                }
            } else { //Throw an exception
                throw new IllegalArgumentException("Unsupported Card Type.  Index = " + index);
            }
            scan.nextLine();
        }
        
        scan.close();
        return deck;
    }
    
    private static int[] determineCost(String s) {
        //11 types of energy
        //dark, psychic, water, colorless are the first four
        int[] energy = new int[Util.NUM_ENERGY_TYPES];
        char[] cost = s.toCharArray();
        for (int i = 0; i < cost.length/2; i++) {
            switch(cost[2*i+1]) {
                case 'D':
                    energy[0] += cost[2*i]-'0';
                    break;
                case 'P':
                    energy[1] += cost[2*i]-'0';
                    break;
                case 'W':
                    energy[2] += cost[2*i]-'0';
                    break;
                case 'C':
                    energy[3] += cost[2*i]-'0';
                    break;
                default:
                    System.out.println("Invalid Energy Type Found");
                    break;
            }
        }
        
        return energy;
    }
    
    //Returns true if the available energy array has enough for the given attack cost
    //Note that colorless energy acts like a wildcard (can be supplied by any other energy type)
    public static boolean hasSufficientEnergy(int[] available, int[] cost) {
        int excess = 0;
        for (int i = 0; i < Util.NUM_ENERGY_TYPES-1; i++) {
            if (available[i] < cost[i]) return false;
            excess += (available[i] - cost[i]);
        }
        return ( available[Util.NUM_ENERGY_TYPES-1] + excess >= cost[Util.NUM_ENERGY_TYPES-1] );
    }
    
    //Extract the number of copies of a particular card from a String of form <#>
    private static int getCount(String s) {
        String ss = s.substring(1,s.length()-1);
        return Integer.parseInt(ss);
    }
    
    public static boolean validateCommand(String s) {
        if (s.startsWith("stop") || s.startsWith("end turn")) return true;
        if (s.startsWith("attack")) {
            //check that we have a numerical parameter next
            if (s.length() == "attack".length()) return false; // no parameters
            s = s.substring("attack".length()).trim();
            try {
                int k = Integer.parseInt(s);
                if (k == 1 || k == 2) return true;
                return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (s.startsWith("switch") || s.startsWith("play")) {
            String[] split = s.trim().split(" ");
            if (split.length == 1) return false;
            try {
                Integer.parseInt(split[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return false;
    }
    
    //Print with a small leading block
    public static void printBlock(String s) {
        System.out.println("|----| " + s);
    }
    
    //Return true if the result indicates that the game should continue
    //Also print any relevant messages
    public static boolean evaluateCheckPokemon(int result, boolean silent) {
        if (result == Util.PLAYER_ONE_WIN) {
            if (!silent) Util.printBlock("Player 1 Wins!");
            return false;
        } else if (result == Util.PLAYER_TWO_WIN) {
        	if (!silent) Util.printBlock("Player 2 Wins!");
            return false;
        } else if (result == Util.GAME_DRAW) {
        	if (!silent) Util.printBlock("Game Ends in a Draw");
            return false;
        }
        return true;
    }
    
    public static void prompt(boolean cpuPlayer) {
        if (cpuPlayer) {
            System.out.print("CPU >> ");
        } else {
            System.out.print("Hu  >> ");
        }
    }
    
    /**
     * Applies the "end turn" action and returns whether play should continue
     * @param game The State object
     * @param cpuPlayer If the current player is the CPU player
     * @return True if play should continue
     */
    public static boolean endTurnAction(State game, boolean cpuPlayer, boolean silent) {
    	boolean contin;
    	if (!silent) Util.printBlock("Ending Turn");
        //Toggle player control
        cpuPlayer = !cpuPlayer;
        
        //Handle between turn effects
        game.processStatus(!cpuPlayer);
        contin = Util.evaluateCheckPokemon( game.checkPokemon() , silent);
        
        //Begin next player's turn
        game.resetSwitches(cpuPlayer);
        int ncard = game.drawCardsToHand(!cpuPlayer, 1);
        if (ncard == 0) { //The current player loses, and the game ends
            contin = false;
            if (!silent) Util.printBlock("Player " + (cpuPlayer ? 1 : 2) + " Wins!");
        }
        
        return contin;
    }
}
