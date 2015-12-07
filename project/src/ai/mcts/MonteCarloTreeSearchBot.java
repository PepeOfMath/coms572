package ai.mcts;

import java.util.LinkedList;
import java.util.List;

import ai.AI;
import field.State;

public class MonteCarloTreeSearchBot implements AI {
	private static final long COMPUTE_TIME = 5000; 
	private TreeNode curNode;
	
	@Override
	public String chooseStartingPokemon(State s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String chooseAction(State s) {
		boolean player = s.playerOneTurn;
		curNode = findNode(curNode, s);

		if (curNode == null) {
			// Oops, I guess we're starting over from scratch
			curNode = new MinMaxNode(s, null, player);
		}
		
		long start = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - start < COMPUTE_TIME) {
			List<TreeNode> visited = new LinkedList<TreeNode>();
	        TreeNode cur = curNode;
	        visited.add(curNode);
	        
	        while (!cur.isLeaf() && !cur.hasUntriedMoves()) {
	            cur = cur.select();
	            visited.add(cur);
	        }
	        
	        if (cur.hasUntriedMoves()) {
	        	cur = cur.expand();
	        	visited.add(cur);
	        }
	        
	        double value = cur.rollOut();
	        
	        for (int i = visited.size()-1; i>=0; i--) {
	        	TreeNode node = visited.get(i);
	            node.updateStats(value);
	        }
		}
		
		TreeNode maxChild = curNode.childNodes().get(0);
        for (TreeNode node : curNode.childNodes()) {
            if (node.value() > maxChild.value()) {
            	maxChild = node;
            }
        }
        return maxChild.action();
	}
	


	private TreeNode findNode(TreeNode root, State s) {
		LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			TreeNode cur = queue.poll();
			queue.addAll(cur.childNodes());
			if (cur instanceof ChanceNode) continue;
			if (cur.gameState().equals(s)) return cur;
		}
		return null;
	}

}
