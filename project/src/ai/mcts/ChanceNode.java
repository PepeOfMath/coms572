package ai.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import field.State;

public class ChanceNode extends AbstractTreeNode {
	private static class NodeAndChance {
		public TreeNode node;
		public double chance;
		public NodeAndChance(TreeNode n, double c) {
			node = n;
			chance = c;
		}
	}
	
	private ArrayList<NodeAndChance> children;

	public ChanceNode(State s, String move, boolean p) {
		super(s, move, p);
		
		HashMap<State, Integer> stateCount = new HashMap<State, Integer>();
		int tries = 20;
		for(int i = 0; i < tries; i++) {
			State next = new State(state, true); // TODO - which one? 
			next.handleCommand(move, true);
			if (stateCount.containsKey(next)) {
				stateCount.put(next, stateCount.get(next) + 1);
			} else {
				stateCount.put(next, 1);
			}
		}
		children = new ArrayList<NodeAndChance>();
		for(State next : stateCount.keySet()) {
			children.add(new NodeAndChance(new MinMaxNode(next, move, p), 1.0*stateCount.get(next)/tries));
		}
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public void updateStats(double value) {
		simulations++;
	}

	@Override
	public List<TreeNode> childNodes() {
		return children.stream()
				.map(c -> c.node)
				.filter(n -> n.simulations() > 0)
				.collect(Collectors.toList());
	}

	@Override
	public TreeNode select() {
		double total = 0.0;
		double r = Math.random();
		for(NodeAndChance nc : children) {
			total += nc.chance;
			if (total >= r) return nc.node;
		}
		throw new RuntimeException("Chances don't sum to 1 probably");
	}

	@Override
	public boolean hasUntriedMoves() {
		return childNodes().size() < children.size();
	}

	@Override
	public TreeNode expand() {
		return children.stream().map(n -> n.node).filter(n -> n.simulations() == 0).findFirst().get();
	}

	@Override
	public double value() {
		double sum = 0;
		for (NodeAndChance n : children) {
			sum += n.chance * n.node.value();
		}
		return sum;
	}

}
