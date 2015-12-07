package ai;

import java.util.Scanner;

import field.State;

/**
 * Requests user input to handle actions
 *
 */
public class Human implements AI {
	
	Scanner scan;
	
	public Human(Scanner ss) {
		scan = ss;
	}

	@Override
	public String chooseStartingPokemon(State s) {
		while(!scan.hasNextLine());
        return scan.nextLine().trim();
	}

	@Override
	public String chooseAction(State s) {
		while(!scan.hasNextLine());
        return scan.nextLine().toLowerCase();
	}

}
