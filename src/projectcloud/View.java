package projectcloud;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * listening for mouse and keyboard input
 */

public abstract class View extends JPanel implements KeyListener, MouseListener {
    
    // Keyboard states
    private static boolean[] keyboardState = new boolean[525];
    
    // Mouse states
    private static boolean[] mouseState = new boolean[3];
    private String[] menuStrings = {
        	"Start", "Exit"	
        };
        private JButton[] menuButtons = new JButton[2];
        private JPanel menuPanel;

    public View()
    {
        // We use double buffer to draw on the screen.
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        
        // Adds the keyboard listener to JPanel to receive key events from this component.
        this.addKeyListener(this);
        // Adds the mouse listener to JPanel to receive mouse events from this component.
        this.addMouseListener(this);
    }
    
    

    public abstract void Draw(Graphics2D g2d);
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        Draw(g2d);
    }
       
    
    /**
     * check for keyboard input
     * 
     * @param key Number of key for which you want to check the state.
     * @return true if the key is down, false if the key is not down.
     */
    public static boolean keyboardKeyState(int key)
    {
        return keyboardState[key];
    }
    
    // Methods of the keyboard listener.
    @Override
    public void keyPressed(KeyEvent e) 
    {
        keyboardState[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        keyboardState[e.getKeyCode()] = false;
        keyReleasedController(e);
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    public abstract void keyReleasedController(KeyEvent e);
    
    
    // Mouse
    /**
     * check if mouse has input
     * 
     * @param button Number of mouse button for which you want to check the state.
     * @return true if the button is down, false if the button is not down.
     */
    public static boolean mouseButtonState(int button)
    {
        return mouseState[button - 1];
    }
    
    // Sets mouse key status.
    private void mouseKeyStatus(MouseEvent e, boolean status)
    {
        if(e.getButton() == MouseEvent.BUTTON1)
            mouseState[0] = status;
        else if(e.getButton() == MouseEvent.BUTTON2)
            mouseState[1] = status;
        else if(e.getButton() == MouseEvent.BUTTON3)
            mouseState[2] = status;
    }
    
    // Methods of the mouse listener.
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseKeyStatus(e, true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouseKeyStatus(e, false);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }
    
    public void createMenu(){
    	menuPanel = new JPanel();
    	menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    	menuPanel.setBounds(420, 300, 400, 225);

		
		
		for(int i = 0; i < menuStrings.length; i++){
			menuButtons[i] = new JButton(menuStrings[i]) {
				{
				setSize(400, 80);
				setMaximumSize(getSize());
				
				}
			};
			
			System.out.println(menuStrings[i]);
			
			menuButtons[i].setFont(new Font("Arial", Font.PLAIN, 40));
			menuPanel.add(menuButtons[i]);
			
			menuButtons[i].addActionListener(new Controller());
		}
    }
    
}