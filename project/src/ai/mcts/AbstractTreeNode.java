package ai.mcts;

import java.util.List;

import field.State;

public abstract class AbstractTreeNode implements TreeNode {

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateStats(double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TreeNode> childNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int score() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String action() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int simulations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int wins() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public State gameState() {
		// TODO Auto-generated method stub
		return null;
	}

}
