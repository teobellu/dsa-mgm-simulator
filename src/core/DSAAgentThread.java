package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import core.basics.GainMessage;
import core.basics.Node;
import core.basics.ValueMessage;
import core.utils.MinorUtils;
import core.utils.Synchronizer;

public class DSAAgentThread extends AgentThread {
	
	public static final long SECURITY_WAIT_TIME = 35;
	
	public volatile BlockingQueue<ValueMessage> vqueue = new LinkedBlockingQueue<ValueMessage>();
	
	public static double THRESHOLD = Main.THRESHOLD;
	
	public DSAAgentThread() {
		super();
		this.start();
	}
	
	public void run() {
		super.run();
		runAlgorithmMaxIterations(false);
		endRun();
	}
	
	protected void algorithm() {
		sendValueMessage();
		waitNeighborsValueMessages(); // get all value messages in v-queue		
		synchronizeOnlyIfUserWantsIt();
		GainMessage g = bestUnilateralGain();
		if (canChangeMyColor(Math.random(), g.gain)) {
			changeMyColorTo(g.value);
		}
		removeOldestNeighborsMessages();
		Synchronizer.waitAllAgents(this);
	}
	
	private boolean canChangeMyColor(double myRandom, int gain) {
		return (myRandom < THRESHOLD && gain > 0);
	}
	
	private void changeMyColorTo(Color newColor) {
		Color newValue = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
		myNode.color = newValue;
		synchronized (Debugger.ACTIONS) {
			Debugger.ACTIONS.add("color" + agentId + "-" + MinorUtils.colorToString(newValue));
			CHANGES++;
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> void _debug_printer(T t) {
		if (t instanceof LinkedBlockingQueue) {
			LinkedBlockingQueue<?> c = (LinkedBlockingQueue<?>) t;
			if (c.peek() instanceof ValueMessage) {
				for(ValueMessage sms : (LinkedBlockingQueue<ValueMessage>) c) {
					System.out.println("I'm " + agentId +" |"+ sms);
				}
			} else if (c.peek() instanceof GainMessage) {
				for(GainMessage sms : (LinkedBlockingQueue<GainMessage>) c) {
					System.out.println(sms);
				}
			}
		}
		
	}
	
	private synchronized int currentUtility() {
		int currentLocalUtility = 0;
		for(ValueMessage mess : getOldestNeighborsValueMessages()) {
			currentLocalUtility += DCOP.getUtility(myNode, mess.value);
		}
		return currentLocalUtility;
	}
	
	private synchronized GainMessage bestUnilateralGain() {
		int maxGain = 0;
		Color oldColor = myNode.color;
		Color bestValue = myNode.color;
		for(Color color : DCOP.domains.get(myNode)) {
			int currentGain = -currentUtility();
			myNode.color = new Color(color.getRed(), color.getGreen(), color.getBlue());
			for (ValueMessage msm : getOldestNeighborsValueMessages()) {
				currentGain += DCOP.getUtility(myNode, msm.value);
			}
			if (currentGain > maxGain) {
				maxGain = currentGain;
				bestValue = new Color(color.getRed(), color.getGreen(), color.getBlue());
			}
			myNode.color = oldColor;
		}
		return new GainMessage(this, maxGain, bestValue);
	}
	
	private synchronized void sendValueMessage() {
		for (AgentThread agent : allNeighbors) {
			DSAAgentThread dsaagent = (DSAAgentThread) agent;
			synchronized(dsaagent.vqueue) {
				dsaagent.vqueue.add(new ValueMessage(this, new Node(myNode)));
				synchronized (Debugger.ACTIONS) {
					Debugger.ACTIONS.add("vsend" + agentId + "-" + dsaagent.agentId);
				}
			}
		}
	}
	
	private void waitNeighborsValueMessages() {
		while(true) {
			synchronized(vqueue) {
				if (vqueue.size() >= allNeighbors.size()) {
					return;
				}
			}
			MinorUtils.sleepFor(SECURITY_WAIT_TIME);
		}
	}
	
	private List<ValueMessage> getOldestNeighborsValueMessages() {
		List<ValueMessage> messages = new ArrayList<ValueMessage>();
		Iterator<ValueMessage> iterator = vqueue.iterator();
		for (int i = 0; i < allNeighbors.size(); i++) {
			messages.add(iterator.next());
		}
		return messages;
	}
	
	private void removeOldestNeighborsMessages() {
		for (int i = 0; i < allNeighbors.size(); i++) {
			vqueue.poll();
		}
	}
	
	
	
}