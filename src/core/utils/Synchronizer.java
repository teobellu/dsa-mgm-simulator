package core.utils;

import java.util.concurrent.CountDownLatch;

import core.AgentThread;
import core.Main;

public class Synchronizer {
	
	private volatile static int numOfThreads = Main.NUMBER_OF_AGENTS;
	private volatile static CountDownLatch[] agentsLatchs
		= {new CountDownLatch(numOfThreads), 
		   new CountDownLatch(numOfThreads),
		   new CountDownLatch(numOfThreads)};
	
	public static void setNumberOfThreads(int numOfThreads) {
		Synchronizer.numOfThreads = numOfThreads;
		Synchronizer.agentsLatchs[0] = new CountDownLatch(numOfThreads);
		Synchronizer.agentsLatchs[1] = new CountDownLatch(numOfThreads);
		Synchronizer.agentsLatchs[2] = new CountDownLatch(numOfThreads);
	}

	public static void waitAllAgents(AgentThread agent) {
		try { useLatch(agent); } catch (Exception e) { e.printStackTrace(); }
	}
	
	private static void useLatch(AgentThread agent) throws Exception {
		int latchIndex = agent.latchPointer;
		if (latchIndex == 1) {
			Synchronizer.agentsLatchs[0].countDown();
			Synchronizer.agentsLatchs[0].await();
			Synchronizer.agentsLatchs[2] = new CountDownLatch(numOfThreads);
		} else if (latchIndex == 2) {
			Synchronizer.agentsLatchs[1].countDown();
			Synchronizer.agentsLatchs[1].await();
			Synchronizer.agentsLatchs[0] = new CountDownLatch(numOfThreads);
		} else {
			Synchronizer.agentsLatchs[2].countDown();
			Synchronizer.agentsLatchs[2].await();
			Synchronizer.agentsLatchs[1] = new CountDownLatch(numOfThreads);
		}
		agent.latchPointer = (latchIndex == 3) ? 1 : latchIndex + 1;
	}
}
