package core;

public class AgentThreadFactory{
	
	public static AgentThread generateNewAgentThread(String type){
		switch(type) {
			case "MGM" : return new MGMAgentThread();
			case "DSA" : return new DSAAgentThread();
		}
		return null;
	}
	
}