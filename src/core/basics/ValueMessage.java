package core.basics;

import core.AgentThread;
import core.utils.MinorUtils;

public class ValueMessage extends Message{
	
	public Node value;
	
	public ValueMessage(AgentThread sender, Node value) {
		this.sender = sender;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Value SMS from " + sender.agentId + " " + MinorUtils.colorToString(value.color);
	}
}