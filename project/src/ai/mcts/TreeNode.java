package ai.mcts;

import java.util.List;

import field.State;

public interface TreeNode {
	
	public boolean isLeaf();

	public void updateStats(double value);

	public List<TreeNode> childNodes();

	public String action();

	public int simulations();

	public State gameState();

	public TreeNode select();

	public boolean hasUntriedMoves();

	public TreeNode expand();

	public double rollOut();

	public double value();

}
