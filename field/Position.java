package field;

import cards.*;
import util.*;
import java.util.ArrayList;
import java.util.Random;

public class Position {
    
    //The pokemon in this position
    private ArrayList<Pokemon> pkmn;
    //The energies in this position
    private ArrayList<Energy> en;
    public Status stat;
    public int turnPlayed;
    int damage;
    Random r;


    public Position(Pokemon p, int turnNumber) {
        if (p == null || !p.evolvesFrom.equals("Null")) {
            //Not a basic pokemon
            throw new IllegalArgumentException("Not a basic pokemon");
        }
        pkmn = new ArrayList<Pokemon>(3);
        en = new ArrayList<Energy>(5);
        pkmn.add(p);
        stat = Status.NORMAL;
        turnPlayed = turnNumber;
        damage = 0;
        r = new Random();
    }
    
    //Get the active (highest evolution) Pokemon in this position
    public Pokemon getPokemon() {
        return pkmn.get(pkmn.size()-1);
    }
    
    public int getEnergyCount() {
        return en.size();
    }

    //Attempt to evolve the Pokemon in this position
    public boolean EvolveWith(Pokemon p) {
        if (this.getPokemon().name.equals(p.evolvesFrom)) {
            pkmn.add(p);
            return true;
        }
        return false;
    }
    
    public boolean addEnergy(Energy e) {
        en.add(e);
        return true;
    }
    
    public Energy removeRandomEnergy() {
        return en.remove(r.nextInt(en.size()));
    }
    
    //Return an array of energy counts in the same format as Util determines costs
    public int[] determineEnergy() {
        int[] energy = new int[Util.NUM_ENERGY_TYPES];
        for (int i = 0; i < en.size(); i++) {
            switch(en.get(i).eType) {
                case DARK:
                    energy[0]++;
                    break;
                case PSYCHIC:
                    energy[1]++;
                    break;
                case WATER:
                    energy[2]++;
                    break;
            }
        }
        
        return energy;
    }
    
    public String energyString() {
        int[] list = determineEnergy();
        String text = "";
        if (list[0] != 0) text += list[0] + "D";
        if (list[1] != 0) text += list[1] + "P";
        if (list[2] != 0) text += list[2] + "W";
        if (text.equals("")) return "-";
        return text;
    }
    
    // Return a string describing the pokemon in this slot
    public String toString() {
        String text = "";
        Pokemon p = getPokemon();
        text += p.name;
        text = String.format("%1$-" + 12 + "s", text);
        text += energyString();
        text = String.format("%1$-" + 20 + "s", text);
        text += (p.totalHP - damage) + "/" + p.totalHP;
        text = String.format("%1$-" + 30 + "s", text);
        text += stat;
        return text;
    }
}
