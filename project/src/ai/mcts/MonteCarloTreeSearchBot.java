package ai.mcts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ai.AI;
import cards.Card;
import cards.Pokemon;
import field.Field;
import field.State;

public class MonteCarloTreeSearchBot implements AI {
	private static final long COMPUTE_TIME = 100; 
	private TreeNode curNode;
	private Random r = new Random();
	
	@Override
	public String chooseStartingPokemon(State s, boolean playerOne) {
    	ArrayList<String> cmds = new ArrayList<String>();
        Field f = playerOne ? s.playerOneF : s.playerTwoF;
        for (int i = 0; i < f.handCount; i++) {
        	Card c = f.hand.get(i);
        	if ( (c instanceof Pokemon) && ((Pokemon)c).isBasic() ) cmds.add(c.name);
        }
        
        if (cmds.size() == 0) return "done";
        return cmds.get( r.nextInt(cmds.size()) );
	}

	@Override
	public String chooseAction(State s) {
		boolean player = s.playerOneTurn;
		curNode = findNode(curNode, s);
		
		if (curNode == null) {
			// Oops, I guess we're starting over from scratch
			curNode = new MinMaxNode(s, null, player);
			System.out.println("\n\nSTARTING OVER\n\n");
		}
		
		long start = System.currentTimeMillis();
		int iterations = 0;
		
		while (System.currentTimeMillis() - start < COMPUTE_TIME) {
//		while (iterations < 1000) {
			iterations++;
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
//		System.out.println("iterations: " + iterations);
		
		TreeNode maxChild = curNode.childNodes().get(0);
        for (TreeNode node : curNode.childNodes()) {
//        	System.out.println(node.value());
            if (node.value() > maxChild.value()) {
            	maxChild = node;
            }
        }
//        System.out.println(maxChild.action());
        return maxChild.action();
	}
	


	private TreeNode findNode(TreeNode root, State s) {
		if (root == null) return null;
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
