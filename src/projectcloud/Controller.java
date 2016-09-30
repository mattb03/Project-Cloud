package projectcloud;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Controller that controls the game (Game.java) that created it, update it and draw it on the screen.
 * 
 */

public class Controller extends View implements ActionListener{
    
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;

    /**
     * Time of one second in nanoseconds.
     */
    public static final long secInNanosec = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     */
    public static final long milisecInNanosec = 1000000L;
    /*
     * Frames per Second
     */
    private final int GAME_FPS = 60;
    /**
     * Pause between updates in nanosecs.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
    
    /**
     * Possible states of the game
     */
    public static enum GameState{STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, OPTIONS, PLAYING, GAMEOVER, DESTROYED}
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time.
     */
    private long gameTime;
    
    private long lastTime;
    
    private Model game;
    
    private Font font;
    
    // Images for menu.
    private BufferedImage mainBackground;
    private BufferedImage menuBorderImg;
    private BufferedImage skyColorImg;

    // menu variables
    private JPanel menu;
    private JButton[] buttons;
    
    
    public Controller ()
    {
        super();
        
        gameState = GameState.VISUALIZING;
        createMenu();
		this.add(menu);

        this.setLayout(new BorderLayout());
        menu.setBounds(513, 380, 300, 150);
		CloudSound.MENU.loop();
		
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                GameLoop();
            }
        };
        gameThread.start();
    }
    
    
   /**
     * Set variables and objects.
     * This method is intended to set the variables and objects for this class, variables and objects for the actual game can be set in Game.java.
     */
    private void Initialize()
    {
        font = new Font("monospaced", Font.BOLD, 28);
    }
    
    /**
     * Load files (images).
     * This method is intended to load files for this class, files for the actual game can be loaded in Game.java.
     */
    private void LoadContent()
    {
        try 
        {
          URL mainBackgroundImg = this.getClass().getResource("/projectcloud/resources/images/mainBackground.png");
          mainBackground = ImageIO.read(mainBackgroundImg);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic is updated and then the game is drawn on the screen.
     */
    private void GameLoop()
    {
      // This two variables are used in VISUALIZING state of the game. We used them to wait some time so that we get correct frame/window resolution.
      long visualizingTime = 0, lastVisualizingTime = System.nanoTime();
      
      // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
      long beginTime, timeTaken, timeLeft;
      
      while(true)
      {
        beginTime = System.nanoTime();
        
        switch (gameState)
        {
          case PLAYING:
            gameTime += System.nanoTime() - lastTime;
            
            game.UpdateGame(gameTime, mousePosition());
            
            lastTime = System.nanoTime();
            break;
          case GAMEOVER:
            //DO NOTHING
            break;
          case MAIN_MENU:
            //DO NOTHING
            break;
          case OPTIONS:
            //DO NOTHING
            break;
          case GAME_CONTENT_LOADING:
            //DO NOTHING
            break;
          case STARTING:
            // Sets variables and objects.
            Initialize();
            // Load files - images, sounds, ...
            LoadContent();
            
            // When all things that are called above finished, we change game status to main menu.
            gameState = GameState.MAIN_MENU;
            break;
          case VISUALIZING:
            if(this.getWidth() > 1 && visualizingTime > secInNanosec)
          {
            frameWidth = this.getWidth();
            frameHeight = this.getHeight();
            
            gameState = GameState.STARTING;
          }
            else
            {
              visualizingTime += System.nanoTime() - lastVisualizingTime;
              lastVisualizingTime = System.nanoTime();
            }
            break;
        }
        
        repaint();
        
        timeTaken = System.nanoTime() - beginTime;
        timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
        if (timeLeft < 10) 
          timeLeft = 10; //set a minimum
        try 
        {
          Thread.sleep(timeLeft);
        } catch (InterruptedException ex) { }
      }
    }
    
    /**
     * Draw the game to the screen. 
     */
    @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
                game.Draw(g2d, mousePosition(), gameTime);
            break;
            case GAMEOVER:
                drawMenuBackground(g2d);
                g2d.setColor(Color.black);
                g2d.drawString("Press ENTER to restart or ESC to exit.", frameWidth/2 - 113, frameHeight/4 + 30);
                game.DrawStatistic(g2d, gameTime);
                g2d.setFont(font);
                g2d.drawString("GAME OVER", frameWidth/2 - 90, frameHeight/4);
            break;
            case MAIN_MENU:
            	
                drawMenuBackground(g2d);
                g2d.setColor(Color.black);
                g2d.setFont(new Font("Arial", font.PLAIN, 50));
                g2d.drawString("Project: Cloud", 505, 350);

            break;
            case OPTIONS:
                //...
            break;
            case GAME_CONTENT_LOADING:
                g2d.setColor(Color.white);
                g2d.drawString("GAME is LOADING", frameWidth/2 - 50, frameHeight/2);
            break;
        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame()
    {
      gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Model();
    }
    
    /**
     *  Restart game - reset game time and call RestartGame() method of game object so that reset some variables.
     */
    private void restartGame()
    {
         gameTime = 0;
        lastTime = System.nanoTime();
        
        game.RestartGame();
        
          gameState = GameState.PLAYING;
    }
    
    
    /**
     * Returns the position of the mouse pointer
     * 
     * @return Point of mouse coordinates.
     */
    private Point mousePosition()
    {
        try
        {
            Point mp = this.getMousePosition();
            
            if(mp != null)
                return this.getMousePosition();
            else
                return new Point(0, 0);
        }
        catch (Exception e)
        {
            return new Point(0, 0);
        }
    }
    
    
    /**
     * keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedController(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        
        switch(gameState)
        {
            case GAMEOVER:
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    restartGame();
            break;
            case MAIN_MENU:
            	remove(menu);
    			CloudSound.MENU.stop();
    			CloudSound.GAMESTART.play();
    			if(true)
    	        {
    	            BufferedImage blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    	            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg, new Point(0, 0), null);
    	            this.setCursor(blankCursor);
    	        }
                newGame();
            break;
        }
    }
    
    /**
     * Mouse Click
     * 
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }
    /*
     * This Method draws the menu background
     * 
     * draws the background and menu
     */
    private void drawMenuBackground(Graphics2D g2d)
    {
    	g2d.drawImage(mainBackground,  0, 0, Controller.frameWidth, Controller.frameHeight, null);

        g2d.setColor(Color.white);

    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttons[0]){
			remove(menu);
			CloudSound.MENU.stop();
			CloudSound.GAMESTART.play();
			if(true)
	        {
	            BufferedImage blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg, new Point(0, 0), null);
	            this.setCursor(blankCursor);
	        }
			newGame();
		}
		else if(e.getSource() == buttons[1])
			System.exit(0);
	}
	
	public void createMenu(){
        menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        buttons = new JButton[2];
        buttons[0] = new JButton("Start"){
        	{
				setSize(400, 80);
				setMaximumSize(getSize());
        	}
        };
        buttons[1] = new JButton("Exit"){
        	{
				setSize(400, 80);
				setMaximumSize(getSize());
        	}
        };
		buttons[0].setFont(new Font("Arial", Font.PLAIN, 40));
		buttons[1].setFont(new Font("Arial", Font.PLAIN, 40));
		buttons[0].addActionListener(this);
		buttons[1].addActionListener(this);
		menu.add(buttons[0]);
		menu.add(buttons[1]);
	}
}