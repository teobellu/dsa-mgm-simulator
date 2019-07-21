package core;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.utils.JLabelHidden;
import core.utils.JTextFieldHidden;
import core.utils.MinorUtils;

public class Main {
	
	private final static String FONT = "<html><span style='font-size:16px'>";
	private final static Font JFONT = new Font("Arial Black", Font.PLAIN, 20);
	public static boolean RANDOM_INITIAL_CONFIG = false;
	public static boolean SYNCRONIZE_EVERY_ACTION = false;
	public static JComboBox<String> COMBO;
	public static String ALGORITHM = "";
	public static double THRESHOLD = 0.7;
	public static int MESSAGE_SPEED = 2;
	public static int NUMBER_OF_AGENTS = 11;
	public static float PROB_EDGE_EXISTS_GIVEN_2_NODES = 1f; 
	public static int MAX_ITERATIONS = -1;
	
	public static void main(String[] args) {
		inputForm();
		DCOP.generateCaseFullyRGBY(NUMBER_OF_AGENTS, PROB_EDGE_EXISTS_GIVEN_2_NODES);
		MinorUtils.sleepFor(50);
		for(int i = 0; i < NUMBER_OF_AGENTS; i++)
			AgentThreadFactory.generateNewAgentThread(ALGORITHM);
			//Agents are independent threads
	}
	
	public static void inputForm() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.WEST;
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("./src/res/logoMGM.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] algorithms = {"DSA", "MGM"};
		COMBO = new JComboBox<String>(algorithms);
		JLabelHidden labelT = new JLabelHidden(FONT+"Threshold:");
		JTextFieldHidden fieldT = new JTextFieldHidden("0.2");
		COMBO.addActionListener(fieldT);
		COMBO.addActionListener(labelT);
		JTextField field0 = new JTextField("6");
	    JTextField field1 = new JTextField("4");
	    JTextField field2 = new JTextField("0.8");
	    JTextField fieldMaxIter = new JTextField("10");
	    COMBO.setFont(JFONT);
	    field0.setFont(JFONT);
	    field1.setFont(JFONT);
	    field2.setFont(JFONT);
	    fieldT.setFont(JFONT);
	    fieldMaxIter.setFont(JFONT);
	    JPanel panel = new JPanel(new GridBagLayout());
	    Image scaledImage = image.getScaledInstance(380,180,Image.SCALE_SMOOTH);
	    JLabel picLabel = new JLabel(new ImageIcon(scaledImage));
	    panel.add(new JLabel(FONT+"Welcome!"),gbc);
	    panel.add(picLabel,gbc);
	    panel.add(new JLabel(FONT+"Algorithm:"),gbc);
	    panel.add(COMBO,gbc);
	    panel.add(labelT,gbc);
	    panel.add(fieldT,gbc);
	    panel.add(new JLabel(FONT+"Number of agents:"),gbc);
	    panel.add(field0,gbc);
	    panel.add(new JLabel(FONT+"Edge existence probability:"),gbc);
	    panel.add(field2,gbc);
	    JCheckBox randomDomains = new JCheckBox(FONT+"Random initial configuration");
	    JCheckBox clearExecution = new JCheckBox(FONT+"Synchronize every action", true);
	    panel.add(randomDomains,gbc);
	    panel.add(clearExecution,gbc);
	    panel.add(new JLabel(FONT+"Number of max iterations (-1 = +&#8734;):"),gbc);
	    panel.add(fieldMaxIter,gbc);
	    panel.add(new JLabel(FONT+"Debugger speed (1 slow, 15 fast):"),gbc);
	    panel.add(field1,gbc);
	    int result = JOptionPane.showConfirmDialog(null, panel, "The best simulator for 1-coordinated algorithms in the world",
	        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if (result == JOptionPane.OK_OPTION) {
	    	try {
	    		ALGORITHM = COMBO.getSelectedItem().toString();
	    		THRESHOLD = Float.parseFloat(fieldT.getText());
	    		MESSAGE_SPEED = Integer.parseInt(field1.getText());
	    		NUMBER_OF_AGENTS = Integer.parseInt(field0.getText());
	    		RANDOM_INITIAL_CONFIG = randomDomains.isSelected();
	    		SYNCRONIZE_EVERY_ACTION = clearExecution.isSelected();
	    		PROB_EDGE_EXISTS_GIVEN_2_NODES = Float.parseFloat(field2.getText());
	    		MAX_ITERATIONS = Integer.parseInt(fieldMaxIter.getText());
	    		if (MAX_ITERATIONS < -1 || NUMBER_OF_AGENTS < 0 || PROB_EDGE_EXISTS_GIVEN_2_NODES < 0 ||
	    				PROB_EDGE_EXISTS_GIVEN_2_NODES > 1 || THRESHOLD < 0 || THRESHOLD > 1 || 
	    				MESSAGE_SPEED < 0 || MAX_ITERATIONS == 0 || (MAX_ITERATIONS == -1 && ALGORITHM.equals("DSA"))) 
	    			throw new Exception();
	    		// For security reasons we assume that +infinity = 10000
	    		if (Main.MAX_ITERATIONS == -1)
	    			Main.MAX_ITERATIONS = 10000;
	    	} catch (Exception e) {
	    		inputForm();
	    	}
	    } else {
	    	System.exit(0);
	    }
	}
	
	
}
