import cards.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import util.*;

public class Test {

    public static void main(String[] args) {
        //Read in the database file, transform it into cards, insert into an array
        if (args.length != 1) {
            System.out.println("Need relative file path of card database");
        } else {
            //Card[] deck = readCardDatabase(args[0]);
            //for( int i = 0; i < deck.length; i++ ) {
            //    System.out.println(deck[i]);
            //}
        }
    }
    
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
    
    private static int getCount(String s) {
        //Assumes we have something of the form <#>
        String ss = s.substring(1,s.length()-1);
        return Integer.parseInt(ss);
    }

}


