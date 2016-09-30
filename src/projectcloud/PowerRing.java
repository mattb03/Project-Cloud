package projectcloud;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Power ring.
 * 
 */

public class PowerRing {
    
    // For creating new rings.
    private static final long timeBetweenNewEnemiesInit = Controller.secInNanosec * 3;
    public static long timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
    public static long timeOfLastCreatedEnemy = 0;
    
    // Health of the ring.
    public int health;
    
    // Position of the ring on the screen.
    public int xCoordinate;
    public int yCoordinate;
    
    //is ring on left of screen
    
    public boolean spawnleft;
    public boolean spawnright;
   
    //
    //PROGRAM ring TO FIRE AT PLAYER
    
    // Moving speed and direction.
    private static final double movingXspeedInit = -4;
    private static final double movingYspeedInit = 4; //ADDED BY ME
    private static double movingXspeed = movingXspeedInit;
    private static double movingYspeed = movingYspeedInit; //ADDED BY ME
    
    // Images of ring.
    public static BufferedImage powerRingImg;
    public static BufferedImage originalRing;

    /**
     * Initialize ring.
     * 
     * @param xCoordinate Starting x coordinate of ring.
     * @param yCoordinate Starting y coordinate of ring.
     * @param powerRingImg Image of ring.
     */
    public void Initialize(int xCoordinate, int yCoordinate)
    {
        health = 100;
        
        // Sets ring position.
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        
        //
        if(xCoordinate < (Controller.frameWidth/2)){
         this.spawnleft = true;
        } else {
         this.spawnright = true;
        }
        
        // Moving speed and direction of enemy.
        //PowerRing.movingXspeed = -4;
        
        if (spawnleft){
        PowerRing.movingXspeed = -4;
        } else if (spawnright){
         PowerRing.movingXspeed = 4;
        }
        
        PowerRing.movingYspeed = 2;// ADDED BY ME
    }
    
    /**
     * It sets speed and time between enemies to the initial properties.
     */
    public static void restartEnemy(){
        PowerRing.timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
        PowerRing.timeOfLastCreatedEnemy = 0;
        PowerRing.movingXspeed = movingXspeedInit;
    }
    
    
    /**
     * It increase enemy speed and decrease time between new enemies.
     */
    public static void speedUp(){
        if(PowerRing.timeBetweenNewEnemies > Controller.secInNanosec)
            PowerRing.timeBetweenNewEnemies -= Controller.secInNanosec / 100;
        
        PowerRing.movingXspeed -= 0.25;
    }
    
    
    /**
     * Checks if the enemy is left the screen.
     * 
     * @return true if the enemy is left the screen, false otherwise.
     */
    public boolean isLeftScreen()
    {
        if(xCoordinate < 0 - powerRingImg.getWidth()) // When the entire ring is out of the screen.
            return true;
        else
            return false;
    }
    // ADDED BY ME
    public boolean isRightScreen()
    {
        if(xCoordinate > (Controller.frameWidth)) // When the entire ring is out of the screen.
            return true;
        else
            return false;
    }
    public boolean isBottomScreen(){
     if(yCoordinate > (Controller.frameHeight + powerRingImg.getHeight())) // When the entire ring is out of the screen.
            return true;
        else
            return false;
    }
    
    
    
    
    /*
     * Takes in an image and creates a new image that is the scaled version of the original image
     * 
     */
    
    public static BufferedImage resizeImg(BufferedImage img, double newW, double newH)
    {
      double w = img.getWidth();
      double h = img.getHeight();
      BufferedImage dimg = new BufferedImage((int)newW, (int)newH, img.getType());
      Graphics2D g = dimg.createGraphics();
      g.drawImage(img, 0, 0, (int)newW, (int)newH, 0, 0, (int)w, (int)h, null);
      g.dispose();
      return dimg;      
    }
    
    /*
     * changes the scale speed depending on the size of the image 
     * 
     */
    public void sizeUp()
    {
      double w = (double) powerRingImg.getWidth() * 1.15;
      double h = (double) powerRingImg.getHeight() * 1.15;
      if (powerRingImg.getHeight() <= (int)(originalRing.getWidth() * 0.25) && powerRingImg.getHeight() <= (int)(originalRing.getWidth() * 0.50))
      {
        w = (double) powerRingImg.getWidth() * 1.10;
        h = (double) powerRingImg.getHeight() * 1.10;
        this.powerRingImg = resizeImg(this.powerRingImg, (int)w, (int)h);
      }
      else if (powerRingImg.getHeight() >= (int)(originalRing.getWidth() * 0.50) && powerRingImg.getHeight() >= (int)(originalRing.getWidth() * 0.50))
      {
        
        w = (double) powerRingImg.getWidth() * 1.02;
        h = (double) powerRingImg.getHeight() * 1.02;
        this.powerRingImg = resizeImg(this.powerRingImg, (int)w, (int)h);
      }
      
      this.powerRingImg = resizeImg(this.powerRingImg, (int)w, (int)h);
      
    }
    
    /*
     * Restarts image's dimensions
     * 
     */
    
    public void restartSize(int w, int h)
    {
      this.powerRingImg = resizeImg(this.originalRing, (int)originalRing.getWidth(), (int)originalRing.getHeight());
    }
    
    //ADDED BY ME    
    /**
     * Updates position of ring
     */
    public void Update()
    {
        // Move enemy on x coordinate.
        xCoordinate += movingXspeed;
        yCoordinate += movingYspeed;
        }
       
    /**
     * Draws ring to the screen.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    { 
        g2d.drawImage(powerRingImg, xCoordinate, yCoordinate, null);
    }
    
}
