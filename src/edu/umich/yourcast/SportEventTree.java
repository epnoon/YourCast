package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SportEventTree {
	String home_team, away_team; 
	SportTreeNode current; 
	HashMap<String, SportTreeNode> teamTree, binTree; 
	public SportEventTree(String home_team, String away_team) {
		this.home_team = home_team; 
		this.away_team = away_team; 
		
		// Team Tree. 
		teamTree = new HashMap<String, SportTreeNode>(); 
		teamTree.put(away_team, null);
		teamTree.put(home_team, null); 
		
		// Yes or No Tree.  
		binTree = new HashMap<String, SportTreeNode>(); 
		binTree.put("No", null);
		binTree.put("Yes", null); 
	}
	
	public class SportTreeNode {
		HashMap<String, SportTreeNode> children; 
		String title; 
		public SportTreeNode(HashMap<String, SportTreeNode> children, String title) { 
			this.children = children; 
			this.title = title; 
		}
		
		public SportTreeNode() {
			this.children = new HashMap<String, SportTreeNode>(); 
			this.title = "Event"; 
		}
	}; 
	
	/**
	 * Produces the update based on the options selected. 
	 * @param selections
	 * @return
	 */
	public abstract String createText(ArrayList<String> selections);

	/*
	 * Iterates to the next option. 
	 */
	public void next(String s) {
		current = current.children.get(s); 
	}

	public ArrayList<String> options() {
		return new ArrayList<String>(current.children.keySet());
	}

	public String title() {
		// TODO Auto-generated method stub
		return current.title; 
	} 
	
	public boolean isDone() {
		return current == null; 
	}
}