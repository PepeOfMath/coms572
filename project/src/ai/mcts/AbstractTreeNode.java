package ai.mcts;

import java.util.List;
import java.util.Random;

import field.State;
import util.Util;

public abstract class AbstractTreeNode implements TreeNode {
	protected String action; // How we got here (action from parent)
	protected State state;
	protected int simulations;
	protected boolean player; 
	protected Random rand = new Random();
	
	public AbstractTreeNode(State s, String a, boolean p) {
		state = s;
		action = a;
		player = p;
		simulations = 0;
	}

	@Override
	public String action() {
		return action;
	}

	@Override
	public int simulations() {
		return simulations;
	}

	@Override
	public State gameState() {
		return state;
	}


	@Override
	public double rollOut() {
		State cur = new State(gameState(), false);
		
		while (!cur.isGameOver()) {
			List<String> moves = cur.getAllMoves();
			String move = moves.get(rand.nextInt(moves.size()));
			cur.handleCommand(move, true);
		}
		
		// TODO - heuristic maybe
		if (cur.isDraw()) return .5; 
		if (Util.PLAYER_ONE_WIN == cur.getGameResult() && player || Util.PLAYER_TWO_WIN == cur.getGameResult() && !player) return 1;
		return 0;
	}
}
