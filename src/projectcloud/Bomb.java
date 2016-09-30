package projectcloud;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Bomb 
 */

public class Bomb{
    
    // Time that must pass before another bomb can be fired.
    public final static long timeBetweenNewBombs = Controller.secInNanosec / 4;
    public static long timeOfLastCreatedBomb = 0;
    
    // Damage that is made to an enemy turret when it is hit with a bomb.
    public static int damagePower = 100;
    
    // Bomb position
    public int xCoordinate;
    public int yCoordinate;
    
    // Moving speed and also direction. Bomb goes always straight, so we move it only on x coordinate.
    private double movingXspeed;
    private double movingYspeed;
    public static int bombSpeed = 10;
    

    // Image of bomb. Image is loaded and set in Game class in LoadContent() method.
    public static BufferedImage bombImg;
    

    /**
     * Set variables and objects for this class.
     * 
     * @param xCoordinate int
     * @param yCoordinate int
     * @param mousePosition Point
     */
    public void Initialize(int xCoordinate, int yCoordinate, Point mousePosition)
    {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        
        setDirectionAndSpeed(mousePosition);
    }
    
    
    /**
     * Checks if the bomb is left the screen.
     * 
     * @return true if the bomb is left the screen, false otherwise.
     */
    public boolean isItLeftScreen()
    {
     if(xCoordinate > 0 && xCoordinate < Controller.frameWidth &&
                yCoordinate > 0 && yCoordinate < Controller.frameHeight)
                 return false;
        else
            return true;
    }
    
    /**
     * sets the direction and speed to the bombs
     * 
     */
    
    private void setDirectionAndSpeed(Point mousePosition)
    {
        // Unit direction vector of the bullet.
        double directionVx = mousePosition.x - this.xCoordinate;
        double directionVy = mousePosition.y - this.yCoordinate;
        double lengthOfVector = Math.sqrt(directionVx * directionVx + directionVy * directionVy);
        directionVx = directionVx / lengthOfVector; // Unit vector
        directionVy = directionVy / lengthOfVector; // Unit vector
        
        this.movingXspeed = bombSpeed * directionVx;
        this.movingYspeed = bombSpeed * directionVy;
        
    }
    
    /**
     * Moves the bomb by updating position.
     */
    public void Update()
    {
        xCoordinate += movingXspeed;
        yCoordinate += movingYspeed;
    }
    
    
    /**
     * Draws the bomb to the screen.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(bombImg, xCoordinate, yCoordinate, null);
    }
}
