package core.utils;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScrolledPane extends JPanel{

	private static final long serialVersionUID = 1L;
	public JScrollPane vertical;
    public JTextArea console;

    public ScrolledPane(){
        setPreferredSize(new Dimension(630, 630));
        console = new JTextArea(30, 47); //y and x
        console.setBackground(Color.BLACK);
        console.setForeground(Color.GREEN);
        console.setFont(console.getFont().deriveFont(16f));
        vertical = new JScrollPane(console);
        vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(vertical);
    }
}