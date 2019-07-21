package core.basics;

import java.awt.Color;

import core.AgentThread;
import core.utils.MinorUtils;

public class GainMessage extends Message{
	
	public int gain;
	
	public Color value;
	
	public GainMessage(AgentThread sender, int gain, Color value) {
		this.sender = sender;
		this.gain = gain;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Gain SMS from " + sender.agentId + " " + gain + " "+ MinorUtils.colorToString(value);
	}
}