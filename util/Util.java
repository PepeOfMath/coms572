package util;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import cards.*;
import java.io.File;

public final class Util {

    private Util() {
        System.out.println("NOPE");
    }
    
    
    //----------------------------Miscellaneous constants
    public static final int NUM_ENERGY_TYPES = 4; //number of energy types currently supported (DPWC)
    
    
    
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
                String[] atkIsplit = atkI.split(",");
                int[] costI = determineCost(atkIsplit[0]);
                
                String atkII = scan.nextLine();
                String[] atkIIsplit = atkII.split(",");
                int[] costII = determineCost(atkIIsplit[0]);
                
                for (int i = 0; i < count; i++) {
                    //deck[index] =
                    deck.add(new Pokemon(name, evolvesFrom, hp, type, weakness, resistance, retreat, costI, atkIsplit[1], null, costII, atkIIsplit[1], null)); //TODO: define an actual effect
                    index++;
                }
            
            
            } else if (cardType.equals("Trainer")) {
                String name = scan.nextLine().trim();
                boolean supporter = (scan.nextLine().equals("True"));
                String effect = scan.nextLine();
                
                for (int i = 0; i < count; i++) {
                    //deck[index] = 
                    deck.add(new Trainer(name, supporter, null)); //TODO: define an actual effect
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
}