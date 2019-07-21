package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.basics.Edge;
import core.basics.Node;
import core.utils.MinorUtils;
import core.utils.ScrolledPane;

public class Debugger {
	ScrolledPane textpanel = new ScrolledPane();
	
	JFrame frame = new JFrame("Debugger");
	public static final List<String> ACTIONS = new ArrayList<String>();

    public Debugger() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JPanel panel = new JPanel();
                
                panel.setLayout(new GridLayout(1,0));
                frame.setBackground(Color.BLACK);
                
                panel.add(new TestPane());
                panel.add(textpanel);
                
                frame.add(panel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setResizable(false);
                //frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {
    	
    	public final Color SHADOW_CYAN = new Color(0, 220, 170);
    	
    	public final Color DARK_RED = new Color(130, 0, 0);
    	
    	int count = 0;
    	
		private static final long serialVersionUID = 1L;

		int MESSAGE_SPEED = Main.MESSAGE_SPEED;
    	
    	private final List<Double> XS = new ArrayList<Double>();
    	private final List<Double> YS = new ArrayList<Double>();
    	
    	private List<Color> CS = new ArrayList<Color>();
    	
    	private int pointer = 0;

        private double x = 1000;
        private double y = 1000;
        
        private Color messageColor = Color.RED;

        public TestPane() {
        	this.setBackground(Color.BLACK);
        	this.setOpaque(true);
        	
        	int k = Main.NUMBER_OF_AGENTS;
        	
        	double pi2 = 2*Math.PI;
        	
        	List<Double> thetas = new ArrayList<>();
        	for(int i = 0; i < k; i++) {
        		thetas.add(i*pi2/k);
        		CS.add(DCOP.initialConfig.get(i));
        	}
        	
        	for(Double theta : thetas) {
        		double x = Math.cos(theta);
        		double y = Math.sin(theta);
        		x *= 300;
        		y *= 300;
        		x += 300;
        		y += 300;
        		XS.add(x);
        		YS.add(y);
        	
        	}
        	
        	
            Timer timer = new Timer(3, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	String action = ACTIONS.get(pointer);
                	if (count == pointer) {
                		if (action.substring(0,3).equals("end"))
                			textpanel.console.append(
        							"[Debug] End of the simulation \n");
                		else if (action.substring(0,5).equals("vsend"))
                			textpanel.console.append(
                							"[Debug] Value message sent by " + 
                							action.substring(5).split("-")[0] + 
                							" and directed to " + 
                							action.substring(5).split("-")[1] + 
                							" \n");
                		else if (action.substring(0,5).equals("gsend"))
                			textpanel.console.append(
        							"[Debug] Gain message sent by " + 
        							action.substring(5).split("-")[0] + 
        							" and directed to " + 
        							action.substring(5).split("-")[1] + 
        							" \n");                    	
                		else if (action.substring(0,5).equals("color"))
                			textpanel.console.append(
        							"[Debug] The node " + 
        							action.substring(5).split("-")[0] + 
        							" changed his color to " + 
        							action.substring(5).split("-")[1] + 
        							" \n"); 
                		count++;
                	}
                	if (action.equals("end"))
                		((Timer)e.getSource()).stop();
                	else if (action.substring(1,5).equals("send"))
                		sendMessage(action);
                	else if (action.substring(0,5).equals("color"))
                		colorNode(action);
                    repaint();
                }
            });
            timer.start();
        }
        
        protected void colorNode(String encode) {
        	if (x == 1000 && y == 1000) {
        		x = 2000;
        	}
        	else if (x == 2000) {
        		x = 1000;
        		MinorUtils.sleepFor(3);
        		pointer++;
        	}
        	String[] parts = encode.substring(5).split("-");
        	String a = parts[0]; 
        	String col = parts[1].substring(0,3);
        	int i = Integer.parseInt(a);
        	if (col.equals("red"))
        		CS.set(i, Color.RED);
        	else if (col.equals("gre"))
    			CS.set(i, Color.GREEN);
        	else if (col.equals("blu"))
    			CS.set(i, Color.BLUE);
        	else if (col.equals("yel"))
    			CS.set(i, Color.YELLOW);
        	
        }
        
        protected void sendMessage(String encode) {
        	String[] parts = encode.substring(5).split("-");
        	String a1 = parts[0]; 
        	String a2 = parts[1];
        	sendMessage(Integer.parseInt(a1), Integer.parseInt(a2));
        	if (encode.substring(0,1).equals("v"))
        		messageColor = Color.ORANGE;
        	else if (encode.substring(0,1).equals("g"))
        		messageColor = Color.MAGENTA;
        	else
        		messageColor = Color.RED;
        }
        
        //from i to j
        protected void sendMessage(int i, int j) {
        	Double xGrowth = ((XS.get(j) - XS.get(i))/200 * MESSAGE_SPEED);
        	Double yGrowth = ((YS.get(j) - YS.get(i))/200 * MESSAGE_SPEED); 
        	if (x == 1000 && y == 1000) {
    			x = XS.get(i);
    			y = YS.get(i);
    		}
    		else if (Math.abs(x - XS.get(j)) <= 2*MESSAGE_SPEED && Math.abs(y - YS.get(j)) <= 2*MESSAGE_SPEED) {
    			x = 1000;
    			y = 1000;
    			pointer++;
    		} else {
    			x += xGrowth;
    			y += yGrowth;
    		}
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(630, 630);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            
            for (Edge e : DCOP.edges) {
            	int i = DCOP.nodes.indexOf(e.n1);
            	int j = DCOP.nodes.indexOf(e.n2);
            	g2d.setStroke(new BasicStroke(2));
            	
            	Node eqn1 = new Node("copy", CS.get(i));
            	Node eqn2 = new Node("copy", CS.get(j));
            	if (DCOP.getUtility(eqn1, eqn2) > 0) {
            		g2d.setColor(SHADOW_CYAN);
            	} else {
            		g2d.setColor(DARK_RED);
            	}
                g2d.drawLine(XS.get(i).intValue()+15, YS.get(i).intValue()+15, XS.get(j).intValue()+15, YS.get(j).intValue()+15);
                g2d.setStroke(new BasicStroke(1));
            }
            
            for (int i = 0; i < Main.NUMBER_OF_AGENTS; ++i) {
            	g2d.setColor(CS.get(i));
                g2d.fillOval(XS.get(i).intValue(), YS.get(i).intValue(), 30, 30);
            }
            g2d.setColor(messageColor);
            g2d.fillRect((int)x+2, (int)y+8, 25, 15);
            g2d.dispose();
        }

    }

}