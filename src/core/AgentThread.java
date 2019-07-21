package core;

import java.util.ArrayList;
import java.util.List;

import core.basics.Node;
import core.utils.Synchronizer;

public abstract class AgentThread extends Thread{

	//This list is used <<<! ONLY !>>> for neighbors searching when algorithm starts!
	//Note the private keyword...
	private volatile static List<AgentThread> agents = new ArrayList<AgentThread>();
	
	//Note the public keyword...
	public List<AgentThread> allNeighbors;
	
	public static int CHANGES = 0;
	
	public int agentId;

	public Node myNode;
	
	/**
	 * Internals
	 */
	public volatile static int syncVar = 0;
	public int latchPointer = 1;
	
	public AgentThread() {
		this.agentId = syncVar;
		AgentThread.agents.add(this);
		myNode = DCOP.nodes.get(agentId);
		syncVar++;
	}
	
	@Override
	public void run() {
		Synchronizer.waitAllAgents(this);
		allNeighbors = getNeighbors();
	}
	
	public List<AgentThread> getNeighbors() {
		List<AgentThread> allNeighbors = new ArrayList<AgentThread>();
		Node myNode = DCOP.nodes.get(agentId);
		for (AgentThread agent : agents) {
			Node agentNode = DCOP.nodes.get(agent.agentId);
			for (Node node : DCOP.neighbors.get(myNode)) {
				if (node.equals(agentNode)) {
					allNeighbors.add(agent);
				}
			}
		}
		return allNeighbors;
	}
	
	public void runAlgorithmMaxIterations(boolean haltIfNoChange) {
		int iter = 0;
		while(iter < Main.MAX_ITERATIONS) {
			algorithm();
			if (haltIfNoChange && CHANGES == 0) {
				Synchronizer.waitAllAgents(this);
				break;
			}
			else if (haltIfNoChange){
				Synchronizer.waitAllAgents(this);
				CHANGES = 0;
				Synchronizer.waitAllAgents(this);
			}
			iter++;
		}
	}
	
	/**
	 * Main algorithm
	 */
	protected abstract void algorithm();
	
	public void endRun() {
		Synchronizer.waitAllAgents(this);
		if (agentId == 0) {
			synchronized (Debugger.ACTIONS) {
				Debugger.ACTIONS.add("end");
			}
			new Debugger();
		}
	}
	
	public void synchronizeOnlyIfUserWantsIt() {
		if (Main.SYNCRONIZE_EVERY_ACTION) 
			Synchronizer.waitAllAgents(this);
	}
}
