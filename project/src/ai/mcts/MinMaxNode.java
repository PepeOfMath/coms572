package ai.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import field.State;

public class MinMaxNode extends AbstractTreeNode {
	private static final double C = Math.sqrt(2);

	private Random r = new Random();
	private ArrayList<TreeNode> expandedChildren;
	private ArrayList<String> unexpandedActions;
	private double wins;
	private boolean player;
	
	/**
	 * 
	 * @param s State
	 * @param a Action
	 * @param p Player
	 */
	public MinMaxNode(State s, String a, boolean p) {
		super(s,a,p);
		expandedChildren = new ArrayList<TreeNode>();
		unexpandedActions = state.getAllMoves();
		wins = 0.0;
		player = p;
	}

	
	
	@Override
	public TreeNode select() {
		TreeNode best = this.childNodes().get(0);
		double v = Double.NEGATIVE_INFINITY;
		
		for (TreeNode child : this.childNodes()) {
			// w/n + C * Math.sqrt(ln(n(p)) / n)
        	// TODO : add a random hint to avoid ex-aequo
			
			// TODO : use heuristic?
            double value = child.value()
                    + C * Math.sqrt(Math.log(this.simulations()) / child.simulations());
                    
            if (value > v) {
                v = value;
                best = child;
            }
		}
		return best;
	}

	@Override
	public boolean isLeaf() {
		return state.gameOver;
	}

	@Override
	public void updateStats(double value) {
		wins += value;
		simulations++;
	}

	@Override
	public List<TreeNode> childNodes() {
		return expandedChildren;
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
	public boolean hasUntriedMoves() {
		return unexpandedActions.size() > 0;
	}

	@Override
	public TreeNode expand() {
		State s = gameState();
		String move = unexpandedActions.remove(0);
		ChanceNode child = new ChanceNode(s, move, player);
		return child;
	}


	@Override
	public double value() {
		return wins / simulations;
	}
}
