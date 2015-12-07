package ai.mcts;

import java.util.List;

import field.State;

public interface TreeNode {
	
	public boolean isLeaf();

	public void updateStats(double value);

	public List<TreeNode> childNodes();

	public int score();

	public String action();

	public int simulations();

	public int wins();

	public State gameState();

}
