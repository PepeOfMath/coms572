package ai.mcts;

import java.util.LinkedList;
import java.util.List;

import ai.AI;
import field.State;

public class MonteCarloTreeSearchBot extends AI {
	private static final long COMPUTE_TIME = 5000; 
	private TreeNode curNode;
	
	@Override
	public String[] chooseStartingPokemon(State s, boolean player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String chooseAction(State s, boolean player) {
		// Breadth first search for the node
		curNode = findNode(curNode, s);
		if (curNode == null) {
			// Oops, I guess we're starting over from scratch
			curNode = new MinMaxNode(s, null, null, 1.00);
		}
		
		long start = System.currentTimeMillis();
		 
		while (System.currentTimeMillis() - start < COMPUTE_TIME) {
			List<TreeNode> visited = new LinkedList<TreeNode>();
	        TreeNode cur = curNode;
	        visited.add(curNode);
	        while (!cur.isLeaf()) {
	            cur = select(cur);
	            visited.add(cur);
	        }
	        expand(cur);
	        TreeNode newNode = select(cur);
	        visited.add(newNode);
	        double value = rollOut(newNode);
	        for (TreeNode node : visited) {
	            node.updateStats(value);
	        }
		}
		
		// TODO - handle chance nodes
		TreeNode maxChild = curNode.childNodes().get(0);
        for (TreeNode node : curNode.childNodes()) {
            if (node.score() > maxChild.score()) {
            	maxChild = node;
            }
        }
        return maxChild.action();
	}

	private double rollOut(TreeNode newNode) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void expand(TreeNode cur) {
		State s = cur.gameState();
		List<String> moves = s.getAllMoves(true); // TODO : remove arg
		for(String move: moves) {
			State next = new State(s, true); // TODO what should this be? 
			next.handleCommand(move);
		}
	}

	private static final double C = Math.sqrt(2);
	private TreeNode select(TreeNode cur) {
		TreeNode best = cur.childNodes().get(0);
		double v = Double.NEGATIVE_INFINITY;
		
		for (TreeNode child : cur.childNodes()) {
			// w/n + C * Math.sqrt(ln(n(p)) / n)
        	// TODO : add a random hint to avoid ex-aequo
            double value = ((child.simulations() == 0) ? 0 : (child.wins() / child.simulations()))
                    + C * Math.sqrt(Math.log(cur.simulations()) / child.simulations());
                    
            if (value > v) {
                v = value;
                best = child;
            }
		}
		return best;
	}

	private TreeNode findNode(TreeNode root, State s) {
		LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			TreeNode cur = queue.poll();
			if (cur.gameState().equals(s)) return cur;
			queue.addAll(cur.childNodes());
		}
		return null;
	}

}
