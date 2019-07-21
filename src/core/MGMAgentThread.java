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

public class MGMAgentThread extends AgentThread {
	
	public static final long SECURITY_WAIT_TIME = 35;
	
	public volatile BlockingQueue<ValueMessage> vqueue = new LinkedBlockingQueue<ValueMessage>();
	public volatile BlockingQueue<GainMessage> gqueue = new LinkedBlockingQueue<GainMessage>();
	
	public MGMAgentThread() {
		super();
		this.start();
	}
	
	public void run() {
		super.run();
		runAlgorithmMaxIterations(true);
		endRun();
	}
	
	protected void algorithm() {
		sendValueMessage();
		waitNeighborsValueMessages(); // get all value messages in v-queue		
		synchronizeOnlyIfUserWantsIt();
		GainMessage g = bestUnilateralGain();
		sendGainMessage(g);
		waitNeighborsGainMessages(); // get all gain messages in g-queue
		synchronizeOnlyIfUserWantsIt();
		if (canChangeMyColor(g)) {
			changeMyColorTo(g.value);
		}
		removeOldestNeighborsMessages();
		Synchronizer.waitAllAgents(this);
	}
	
	private boolean canChangeMyColor(GainMessage g) {
		boolean update = true;
		for(GainMessage gsms : getOldestNeighborsGainMessages()) {
			if (gsms.gain > g.gain ||
			   (gsms.gain == g.gain && gsms.sender.agentId > agentId)) {
				update = false;
			}
		}
		if (g.gain != 0 && update)
			return true;
		return false;
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
	
	@SuppressWarnings("unused")
	@Deprecated //but can be useful in the future
	private synchronized int getMaxNeighborGains() {
		int max = Integer.MIN_VALUE;
		synchronized(gqueue) {
			for (GainMessage msm : gqueue) {
				max = Math.max(max, msm.gain);
			}
		}
		return max;
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
			MGMAgentThread mgmagent = (MGMAgentThread) agent;
			synchronized(mgmagent.vqueue) {
				mgmagent.vqueue.add(new ValueMessage(this, new Node(myNode)));
				synchronized (Debugger.ACTIONS) {
					Debugger.ACTIONS.add("vsend" + agentId + "-" + mgmagent.agentId);
				}
			}
		}
	}
	
	private synchronized void sendGainMessage(GainMessage g) {
		for (AgentThread agent : allNeighbors) {
			MGMAgentThread mgmagent = (MGMAgentThread) agent;
			synchronized(mgmagent.gqueue) {
				mgmagent.gqueue.add(g);
				synchronized (Debugger.ACTIONS) {
					Debugger.ACTIONS.add("gsend" + agentId + "-" +  mgmagent.agentId);
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
	
	private void waitNeighborsGainMessages() {
		while(true) {
			synchronized(gqueue) {
				if (gqueue.size() >= allNeighbors.size()) {
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
	
	private List<GainMessage> getOldestNeighborsGainMessages() {
		List<GainMessage> messages = new ArrayList<GainMessage>();
		Iterator<GainMessage> iterator = gqueue.iterator();
		for (int i = 0; i < allNeighbors.size(); i++) {
			messages.add(iterator.next());
		}
		return messages;
	}
	
	private void removeOldestNeighborsMessages() {
		for (int i = 0; i < allNeighbors.size(); i++) {
			vqueue.poll();
			gqueue.poll();
		}
	}
	
}