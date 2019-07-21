package core.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import core.Main;

public class JLabelHidden extends JLabel implements ActionListener{
	
	String original;

	private static final long serialVersionUID = 5388158845415725152L;

	public JLabelHidden(String string) {
		super(string);
		original = string;
		this.setVisible(true);
		revalidate();
        repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(Main.COMBO.getSelectedItem().toString().equalsIgnoreCase("MGM"))
		    this.setVisible(false);
	    else
	    	this.setVisible(true);
		revalidate();
        repaint();
	}
}

