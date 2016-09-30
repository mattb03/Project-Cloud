package projectcloud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Enemy turret.
 * 
 */

public class EnemyTurret {
    
    // For creating new enemies.
    private static final long timeBetweenNewEnemiesInit = Controller.secInNanosec * 3;
    public static long timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
    public static long timeOfLastCreatedEnemy = 0;
    
    // Health of the turret.
    public int health;
    
    // Position of the turret on the screen.
    public int xCoordinate;
    public int yCoordinate;
    
    //is tower on left of screen
    
    public boolean spawnleft;
    public boolean spawnright;
    
    
    // ADD AMMO TO ENEMY TOWER
    private final int numberOfGBulletInit = 50;
    public int numberOfGBullet;
    public static int fireLimit = 4;
    public static int currentLimit = 0;
    
    
    //PROGRAM TOWER TO FIRE AT PLAYER
    
    
    // Offset of the ship bomb holder.
    private int offsetXMachineGun;
    private int offsetYMachineGun;
    // Position on the frame/window of the ship bomb holder.
    public int machineGunXcoordinate;
    public int machineGunYcoordinate;
    
    // Moving speed and direction.
    private static final double movingXspeedInit = -4;
    private static final double movingYspeedInit = 4; //ADDED BY ME
    private static double movingXspeed = movingXspeedInit;
    private static double movingYspeed = movingYspeedInit; //ADDED BY ME
    
    // Images of enemy turret.
    public static BufferedImage turretBodyImg;
    public static BufferedImage originalTurret;
    

    /**
     * Initialize enemy turret.
     * 
     * @param xCoordinate Starting x coordinate of turret.
     * @param yCoordinate Starting y coordinate of turret.
     */
 
    public void Initialize(int xCoordinate, int yCoordinate)
    {
        health = 50;
        this.numberOfGBullet = numberOfGBulletInit;
        // Sets enemy position.
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;

        this.offsetXMachineGun = 100;
        this.offsetYMachineGun = 200;
        machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
        
        this.offsetXMachineGun = (turretBodyImg.getWidth()/2);
        this.offsetYMachineGun = turretBodyImg.getHeight();
        this.machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        this.machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
    
        
        //
        if(xCoordinate < (Controller.frameWidth/2)){
         this.spawnleft = true;
        }
        else 
        {
         this.spawnright = true;
        }
        
        // Moving speed and direction of enemy.
        
        if (spawnleft)
        {
          EnemyTurret.movingXspeed = -4;
        } 
        else if (spawnright)
        {
          EnemyTurret.movingXspeed = 4;
        }
        
        EnemyTurret.movingYspeed = 2;
    }
    
    /**
     * It sets speed and time between enemies to the initial properties.
     */
    public static void restartEnemy()
    {
        EnemyTurret.timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
        EnemyTurret.timeOfLastCreatedEnemy = 0;
        EnemyTurret.movingXspeed = movingXspeedInit;
        EnemyTurret.currentLimit = 0;
    }
    
    public boolean isFiring(long gameTime)
    {
       // int limit = 0;
    	// Checks if right mouse button is down && if it is the time for new bomb && if he has any bomb left.
        if(currentLimit < fireLimit && ((gameTime - GBullet.timeOfLastCreatedBullet) >= GBullet.timeBetweenNewBullets)&& this.numberOfGBullet > 0)
        {
        	CloudSound.TURRETFIRE.play();
        	currentLimit++;
        	
            return true;
        } else
            return false;
    }
    
    /**
     * It increase enemy speed and decrease time between new enemies.
     */
    public static void speedUp(){
        if(EnemyTurret.timeBetweenNewEnemies > Controller.secInNanosec)
            EnemyTurret.timeBetweenNewEnemies -= Controller.secInNanosec / 100;
        
        EnemyTurret.movingXspeed -= 0.25;
    }
    
    
    /**
     * Checks if the enemy is left the screen.
     * 
     * @return true if the enemy is left the screen, false otherwise.
     */
    public boolean isLeftScreen()
    {
        if(xCoordinate < 0 - turretBodyImg.getWidth()) // When the entire turret is out of the screen.
            return true;
        else
            return false;
    }

    public boolean isRightScreen()
    {
        if(xCoordinate > (Controller.frameWidth)) // When the entire turret is out of the screen.
            return true;
        else
            return false;
    }
    public boolean isBottomScreen(){
     if(yCoordinate > (Controller.frameHeight + turretBodyImg.getHeight())) // When the entire turret is out of the screen.
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
      double w = (double) turretBodyImg.getWidth() * 1.15;
      double h = (double) turretBodyImg.getHeight() * 1.15;
      if (turretBodyImg.getHeight() <= (int)(originalTurret.getWidth() * 0.25) && turretBodyImg.getHeight() <= (int)(originalTurret.getWidth() * 0.50))
      {
        w = (double) turretBodyImg.getWidth() * 1.10;
        h = (double) turretBodyImg.getHeight() * 1.10;
        this.turretBodyImg = resizeImg(this.turretBodyImg, (int)w, (int)h);
      }
      else if (turretBodyImg.getHeight() >= (int)(originalTurret.getWidth() * 0.50) && turretBodyImg.getHeight() >= (int)(originalTurret.getWidth() * 0.50))
      {
        
        w = (double) turretBodyImg.getWidth() * 1.02;
        h = (double) turretBodyImg.getHeight() * 1.02;
        this.turretBodyImg = resizeImg(this.turretBodyImg, (int)w, (int)h);
      }
      
      this.turretBodyImg = resizeImg(this.turretBodyImg, (int)w, (int)h);
      
    }
    
    /*
     * Restarts image's dimensions
     * 
     */
    
    public void restartSize(int w, int h)
    {
      this.turretBodyImg = resizeImg(this.originalTurret, (int)originalTurret.getWidth(), (int)originalTurret.getHeight());
    }
    
    public void resetLimit(){
    	this.currentLimit = 0;
    }
    
    
    //ADDED BY ME    
    /**
     * Updates position of turret, animations.
     */
    public void Update()
    {
        // Move enemy on x coordinate.
        xCoordinate += movingXspeed;
        yCoordinate += movingYspeed;
        
        // Change position of the GBullet holder.
        this.machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        this.machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
  }
       
    /**
     * Draws turret to the screen.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    { 
        g2d.drawImage(turretBodyImg, xCoordinate, yCoordinate, null);
    }
    
}
