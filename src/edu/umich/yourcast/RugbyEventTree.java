package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class RugbyEventTree extends SportEventTree {

	public RugbyEventTree(String home_team, String away_team) {
		super(home_team, away_team);
		
		current = new SportTreeNode(); 
		 
		HashMap<String, SportTreeNode> 
			tryTree = null, 
			penaltyTree = null, 
			scrumTree = null, 
			dkTree = null, 
			lineoutTree = null;
		
 
		// Tries. 
		tryTree = new HashMap<String, SportTreeNode>(); 
		tryTree.put(away_team, new SportTreeNode(binTree, "Conversion Made?")); 
		tryTree.put(home_team, new SportTreeNode(binTree, "Conversion Made?")); 
		current.children.put("Try", new SportTreeNode(tryTree, "Who scored?"));  
		
		// Lineouts. 
		lineoutTree = new HashMap<String, SportTreeNode>(); 
		lineoutTree.put(away_team, new SportTreeNode(teamTree, "Who won?")); 
		lineoutTree.put(home_team, new SportTreeNode(teamTree, "Who won?"));
		current.children.put("Lineout", new SportTreeNode(lineoutTree, "Whose Lineout?"));  
		
		// Dropkicks. 
		dkTree = new HashMap<String, SportTreeNode>(); 
		dkTree.put(away_team, new SportTreeNode(binTree, "Kick Made?")); 
		dkTree.put(home_team, new SportTreeNode(binTree, "Kick Made?"));  
		current.children.put("Dropkick", new SportTreeNode(dkTree, "Who kicked?")); 	
		
		// Scrums. 
		scrumTree = new HashMap<String, SportTreeNode>(); 
		scrumTree.put(away_team, new SportTreeNode(teamTree, "Who won?")); 
		scrumTree.put(home_team, new SportTreeNode(teamTree, "Who won?"));
		current.children.put("Scrum", new SportTreeNode(scrumTree, "Whose Scrum?")); 
		
		// Penalties. 
		HashMap<String, SportTreeNode> optionTree = new HashMap<String, SportTreeNode>(); 
		optionTree.put("Run", null); 
		optionTree.put("Kick for Touch", null); 
		optionTree.put("Kick for Points", new SportTreeNode(binTree, "Kick Made?"));  
		
		penaltyTree = new HashMap<String, SportTreeNode>(); 
		penaltyTree.put(home_team, new SportTreeNode(optionTree, "What was done?")); 
		penaltyTree.put(away_team, new SportTreeNode(optionTree, "What was done?")); 
		current.children.put("Penalty", new SportTreeNode(penaltyTree, "Whose Penalty?"));  
	}

	@Override
	public String createText(ArrayList<String> selections) {
		// TODO Auto-generated method stub
		return null;
	}

}
