package edu.umich.yourcast;

import java.util.ArrayList;
import java.util.HashMap;

import android.text.Selection;
import android.util.Log;

public class RugbyEventTree extends SportEventTree {

	public RugbyEventTree(String home_team, String away_team) {
		super(home_team, away_team);

		current = new SportTreeNode();

		HashMap<String, SportTreeNode> tryTree = null, penaltyTree = null, scrumTree = null, dkTree = null, lineoutTree = null;

		// Tries.
		tryTree = new HashMap<String, SportTreeNode>();
		tryTree.put(away_team, new SportTreeNode(binTree, "Conversion Made?"));
		tryTree.put(home_team, new SportTreeNode(binTree, "Conversion Made?"));
		current.children.put("Try", new SportTreeNode(tryTree, "Who scored?"));

		// Lineouts.
		lineoutTree = new HashMap<String, SportTreeNode>();
		lineoutTree.put(away_team, new SportTreeNode(teamTree, "Who won?"));
		lineoutTree.put(home_team, new SportTreeNode(teamTree, "Who won?"));
		current.children.put("Lineout", new SportTreeNode(lineoutTree,
				"Whose Lineout?"));

		// Dropkicks.
		dkTree = new HashMap<String, SportTreeNode>();
		dkTree.put(away_team, new SportTreeNode(binTree, "Kick Made?"));
		dkTree.put(home_team, new SportTreeNode(binTree, "Kick Made?"));
		current.children.put("Dropkick", new SportTreeNode(dkTree,
				"Who kicked?"));

		// Scrums.
		scrumTree = new HashMap<String, SportTreeNode>();
		scrumTree.put(away_team, new SportTreeNode(teamTree, "Who won?"));
		scrumTree.put(home_team, new SportTreeNode(teamTree, "Who won?"));
		current.children.put("Scrum", new SportTreeNode(scrumTree,
				"Whose Scrum?"));

		// Penalties.
		HashMap<String, SportTreeNode> optionTree = new HashMap<String, SportTreeNode>();
		optionTree.put("Run", null);
		optionTree.put("Kick for Touch", null);
		optionTree.put("Kick for Points", new SportTreeNode(binTree,
				"Kick Made?"));

		penaltyTree = new HashMap<String, SportTreeNode>();
		penaltyTree.put(home_team, new SportTreeNode(optionTree,
				"What was done?"));
		penaltyTree.put(away_team, new SportTreeNode(optionTree,
				"What was done?"));
		current.children.put("Penalty", new SportTreeNode(penaltyTree,
				"Whose Penalty?"));
	}

	@Override
	public String createText(ArrayList<String> selections) {
		// TODO Auto-generated method stub
		String output = "";
		if (selections.get(0) == "Try") {
			output += selections.get(1) + " scores and ";
			if (selections.get(2) == "Yes") {
				output += "makes ";
			} else {
				output += "misses ";
			}
			output += "the conversion.";
		} else if (selections.get(0) == "Scrum") {
			if (selections.get(1) == selections.get(2)) {
				output += selections.get(1) + " wins its own scrum.";
			} else {
				output += selections.get(2) + " wins a " + selections.get(1)
						+ " scrum.";
			}
		} else if (selections.get(0) == "Lineout") {
			if (selections.get(1) == selections.get(2)) {
				output += selections.get(1) + " wins its own lineout.";
			} else {
				output += selections.get(2) + " wins a " + selections.get(1)
						+ " lineout.";
			}
		} else if (selections.get(0) == "Dropkick") {
			output += selections.get(1) + " attempts a drop kick and ";
			if (selections.get(2) == "Yes") {
				output += "the kick is good!";
			} else {
				output += "the kick is missed. Better luck next time.";
			}
		} else if (selections.get(0) == "Penalty") {
			output += "Penalty awarded to " + selections.get(1) + ". ";
			if (selections.get(2) == "Run") {
				output += "They choose to run the ball.";
			} else if (selections.get(2) == "Kick for Touch") {
				output += "They choose to kick for touch.";
			} else {
				output += "They kick for points and kick is ";
				if (selections.get(3) == "Yes") {
					output += "GOOD!";
				} else {
					output += "missed.";
				}
			}
		}
		
		return output;
	}

	@Override
	public int getHomePoints(ArrayList<String> selections) {
		return calculateScore(selections, home_team);
	}

	@Override
	public int getAwayPoints(ArrayList<String> selections) {
		return calculateScore(selections, away_team);
	}
	
	public int calculateScore(ArrayList<String> selections, String team) {
		int score = 0;
		if (selections.get(1) == team) {
			if (selections.get(0) == "Try") {
				score += 5; 
				if (selections.get(2) == "Yes") {
					score += 2; 
				} 
			} else if (selections.get(0) == "Dropkick" && selections.get(2) == "Yes") {
				score += 3; 
			} else if (selections.get(0) == "Penalty"
					&& selections.get(2) == "Kick for Points"
					&& selections.get(3) == "Yes") {
				score += 3; 
			}
		}
		return score;
	}
}
