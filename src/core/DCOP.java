package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import core.basics.Edge;
import core.basics.Node;

public class DCOP {
	
	public static List<Node> nodes;
	
	public static List<Edge> edges;
	
	public static List<Color> initialConfig; //ordered by agent
	
	public static Map<Node, List<Color>> domains;
	
	public static BiFunction<Node, Node, Integer> utilityFunction;
	
	public static Map<Node, List<Node>> neighbors;
		
	public static void generateCaseFullyRGBY(int numAgents, float edgeProb) {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		initialConfig = new ArrayList<Color>();
		domains = new HashMap<Node, List<Color>>();
		utilityFunction = (a,b) -> (!a.color.equals(b.color)) ? 1 : -100;
		neighbors = new HashMap<Node, List<Node>>();
		
		List<Color> domain = Arrays.asList(Color.red, Color.green, Color.blue, Color.yellow);
		for (int a = 0; a < numAgents; a++)
			if (Main.RANDOM_INITIAL_CONFIG)
				initialConfig.add(domain.get((int)(Math.random()*domain.size())));
			else
				initialConfig.add(domain.get(0));
		
		for (int a = 0; a < numAgents; a++) {
			Color original = initialConfig.get(a);
			Color copy = new Color(original.getRed(), original.getGreen(), original.getBlue());
			nodes.add(new Node("Agent " + a, copy));
		}
		
		nodes.forEach(n -> domains.put(n, domain));
		nodes.forEach(n -> neighbors.put(n, new ArrayList<Node>()));
		
		for(int i = 0; i < numAgents - 1; i++) {
			for(int j = i + 1; j < numAgents; j++) {
				if (Math.random() >= 1 - edgeProb) {
					addEdge(nodes.get(i), nodes.get(j));
				}
			}
		}
	}
	
	public static void generateCase5() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		domains = new HashMap<Node, List<Color>>();
		utilityFunction = (a,b) -> (!a.color.equals(b.color)) ? 1 : -100;
		neighbors = new HashMap<Node, List<Node>>();
		Node n0 = new Node("Agent 0", Color.yellow);
    	Node n1 = new Node("Agent 1", Color.yellow);
    	Node n2 = new Node("Agent 2", Color.yellow);
    	Node n3 = new Node("Agent 3", Color.yellow);
    	Node n4 = new Node("Agent 4", Color.yellow);
    	nodes.addAll(Arrays.asList(n0,n1,n2,n3,n4));
    	List<Color> domainA = Arrays.asList(Color.yellow, Color.green);
    	List<Color> domainB = Arrays.asList(Color.yellow, Color.red);
    	domains.put(n0, domainA);
    	domains.put(n1, domainB);
    	domains.put(n2, domainA);
    	domains.put(n3, domainB);
    	domains.put(n4, domainA);
    	neighbors.put(n0, new ArrayList<Node>());
    	neighbors.put(n1, new ArrayList<Node>());
    	neighbors.put(n2, new ArrayList<Node>());
    	neighbors.put(n3, new ArrayList<Node>());
    	neighbors.put(n4, new ArrayList<Node>());
    	addEdge(n0, n1);
    	addEdge(n0, n3);
    	addEdge(n0, n4);
    	addEdge(n1, n2);
    	addEdge(n2, n3);
    	addEdge(n1, n4);
    	addEdge(n1, n3);
    	addEdge(n2, n4);
	}
	
	public static synchronized int getUtility(Node n1, Node n2) {
		return utilityFunction.apply(n1, n2);
	}
	
	private static synchronized void addEdge(Node n1, Node n2) {
		edges.add(new Edge(n1, n2));
		neighbors.get(n1).add(n2);
		neighbors.get(n2).add(n1);
	}

}
