package core.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import core.Main;

public class JTextFieldHidden extends JTextField implements ActionListener{

	private static final long serialVersionUID = 5388158845415725152L;

	public JTextFieldHidden(String string) {
		super(string);
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

