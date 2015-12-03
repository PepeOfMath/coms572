//A class to control the main program flow

import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        boolean cpuPlayer = false;
        boolean cpuControl= false;
        int ctrlFlag = 2;
        String cmd;
        
        printBlock("Press Enter to Begin");
        while(!s.hasNextLine());
        s.nextLine();
        
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
            
            
            //Set up the computer's side
            //Create game state
            //Draw cards
            //Report back number of attempts before a valid hand was obtained
            //Ask how many attempts the user had
            //Draw extra cards if desired
            
            
            
            
            //Loop for commands
            printBlock("Press Enter to Begin Game");
            while(!s.hasNextLine());
            s.nextLine();
            
            boolean contin = true;
            while(contin) {
                prompt(cpuPlayer);
                while(!s.hasNextLine());
                cmd = s.nextLine();
                
                
                
                //Read and execute the command.
                if(cmd.toLowerCase().startsWith("stop")) {
                    printBlock("Terminating Game");
                    contin = false;
                }
                
                
                //If the command is End Turn or Attack, toggle the cpuPlayer flag
            
                //prompt(cpuPlayer);
            }
        
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
