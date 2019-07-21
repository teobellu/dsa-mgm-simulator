package core.basics;

import java.awt.Color;

public class Node {

	public int x, y;
	public String name;
	public volatile Color color;

	public Node(String myName, int myX, int myY, Color color) {
		x = myX;
		y = myY;
		name = myName;
		this.color = color;
	}
	
	public Node(String myName, Color color) {
		name = myName;
		this.color = color;
	}
	
	//TODO attenzione alle hashtables
	public Node(Node node) {
		x = node.x;
		y = node.y;
		name = new String(node.name);
		color = new Color(node.color.getRed(), node.color.getGreen(), node.color.getBlue());
	}

	public void setCoordinates(int i, int j) {
		x = i;
		y = j;
	}
}
