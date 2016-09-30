package projectcloud;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Ship which is managed by player.
 * 
 */

public class PlayerShip {
    
    // Health of the ship.
    private final int healthInit = 100;
    public int health;
    
    // Position of the ship on the screen.
    public int xCoordinate;
    public int yCoordinate;
    
    // Moving speed and also direction.
    private double movingXspeed;
    public double movingYspeed;
    private double acceleratingXspeed;
    private double acceleratingYspeed;
    private double stoppingXspeed;
    private double stoppingYspeed;
    
    // ship bombs.
    private final int numberOfBombsInit = 80;
    public int numberOfBombs;
    
    // ship machinegun ammo.
    private final int numberOfAmmoInit = 1400;
    public int numberOfAmmo;
    
    // Images of ship.
    public BufferedImage shipBodyImg;
    

    
    // Offset of the ship bomb holder.
    private int offsetXBombHolder;
    private int offsetYBombHolder;
    // Position on the frame/window of the ship bomb holder.
    public int bombHolderXcoordinate;
    public int bombHolderYcoordinate;
    
    // Offset of the ship machine gun. We add offset to the position of the position of ship.
    private int offsetXMachineGun;
    private int offsetYMachineGun;
    // Position on the frame/window of the ship machine gun.
    public int machineGunXcoordinate;
    public int machineGunYcoordinate;
    
    
    /**
     * Creates object of player.
     * 
     * @param xCoordinate Starting x coordinate of ship.
     * @param yCoordinate Starting y coordinate of ship.
     */
    public PlayerShip(int xCoordinate, int yCoordinate)
    {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        
        LoadContent();
        Initialize();
    }
    
    
    /**
     * Set variables and objects for this class.
     */
    private void Initialize()
    {
        this.health = healthInit;
        
        this.numberOfBombs = numberOfBombsInit;
        this.numberOfAmmo = numberOfAmmoInit;
        
        this.movingXspeed = 0;
        this.movingYspeed = 0;
        this.acceleratingXspeed = 0.6;
        this.acceleratingYspeed = 0.6;
        this.stoppingXspeed = 0.4;
        this.stoppingYspeed = 0.4;

        
        this.offsetXBombHolder = 138;
        this.offsetYBombHolder = 40;
        this.bombHolderXcoordinate = this.xCoordinate + this.offsetXBombHolder;
        this.bombHolderYcoordinate = this.yCoordinate + this.offsetYBombHolder;
       
        this.offsetXMachineGun = (shipBodyImg.getWidth()/2);
        this.offsetYMachineGun = shipBodyImg.getHeight();
        this.machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        this.machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
    }
    
    /**
     * Load files for this class.
     */
    private void LoadContent()
    {
      try 
      {
        URL shipBodyImgUrl = this.getClass().getResource("/projectcloud/resources/images/Jet.png");
        shipBodyImg = ImageIO.read(shipBodyImgUrl);
        
      } 
      catch (IOException ex) {
        Logger.getLogger(PlayerShip.class.getName()).log(Level.SEVERE, null, ex);
      }
      
    }
    
    
    /**
     * Resets the player.
     * 
     * @param xCoordinate Starting x coordinate of ship.
     * @param yCoordinate Starting y coordinate of ship.
     */
    public void Reset(int xCoordinate, int yCoordinate)
    {
        this.health = healthInit;
        
        this.numberOfBombs = numberOfBombsInit;
        this.numberOfAmmo = numberOfAmmoInit;
        
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        
        this.machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        this.machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
        
        this.movingXspeed = 0;
        this.movingYspeed = 0;
    }
    
    
    /**
     * Checks if player is shooting. It also checks if player can 
     * shoot (time between bullets, does a player have any bullet left).
     * 
     * @param gameTime The current elapsed game time in nanoseconds.
     * @return true if player is shooting.
     */
    public boolean isShooting(long gameTime)
    {
        // Checks if left mouse button is down && if it is the time for a new bullet.
        if( View.mouseButtonState(MouseEvent.BUTTON1) && 
            ((gameTime - Bullet.timeOfLastCreatedBullet) >= Bullet.timeBetweenNewBullets) && 
            this.numberOfAmmo > 0) 
        {
            return true;
        } else
            return false;
    }
    
    
    /**
     * Checks if player is fired a bomb. It also checks if player can 
     * fire a bomb (time between bombs, does a player have any bomb left).
     * 
     * @param gameTime The current elapsed game time in nanoseconds.
     * @return true if player is fired a bomb.
     */
    public boolean isFiredBomb(long gameTime)
    {
        // Checks if right mouse button is down && if it is the time for new bomb && if he has any bomb left.
        if( View.mouseButtonState(MouseEvent.BUTTON3) && 
            ((gameTime - Bomb.timeOfLastCreatedBomb) >= Bomb.timeBetweenNewBombs) && 
            this.numberOfBombs > 0 ) 
        {
            return true;
        } else
            return false;
    }
    
    
    /**
     * Checks if player moving ship and sets its moving speed if player is moving.
     */
    public void isMoving()
    {
        // Moving on the x coordinate.
        if(View.keyboardKeyState(KeyEvent.VK_D) || View.keyboardKeyState(KeyEvent.VK_RIGHT)){
         if(xCoordinate <= (Controller.frameWidth - 150)){
          movingXspeed += acceleratingXspeed;
         }
        }
        else if(View.keyboardKeyState(KeyEvent.VK_A) || View.keyboardKeyState(KeyEvent.VK_LEFT))
         if(xCoordinate >= -60)
          movingXspeed -= acceleratingXspeed;
        else    // Stoping
            if(movingXspeed < 0)
                movingXspeed += stoppingXspeed;
            else if(movingXspeed > 0)
                movingXspeed -= stoppingXspeed;
        
        // Moving on the y coordinate.
        if((View.keyboardKeyState(KeyEvent.VK_W) || View.keyboardKeyState(KeyEvent.VK_UP)) && xCoordinate != -50)
         //if(yCoordinate <= Controller.frameHeight - 100)
          movingYspeed -= acceleratingYspeed;
        else if(View.keyboardKeyState(KeyEvent.VK_S) || View.keyboardKeyState(KeyEvent.VK_DOWN))
         //if(yCoordinate >= -60)
          movingYspeed += acceleratingYspeed;
        else    // Stoping
            if(movingYspeed < 0)
                movingYspeed += stoppingYspeed;
            else if(movingYspeed > 0)
                movingYspeed -= stoppingYspeed;
    }
    
    
    /**
     * Updates position of ship, animations.
     */
    public void Update()
    {
        // Move ship
    //set window boundaries
     if(xCoordinate <= -60)
      xCoordinate = -56;
     if(xCoordinate >= Controller.frameWidth - 150)
       xCoordinate = (Controller.frameWidth -156); 
     if(yCoordinate <= -60)
      yCoordinate = -54;
     if(yCoordinate >= Controller.frameHeight - 100)
      yCoordinate = Controller.frameHeight - 95;
     xCoordinate += movingXspeed;
     yCoordinate += movingYspeed;
     
        // Change position of the holder holder.
        this.bombHolderXcoordinate = this.xCoordinate + this.offsetXBombHolder;
        this.bombHolderYcoordinate = this.yCoordinate + this.offsetYBombHolder;
        
        // Move the machine gun with ship.
        this.machineGunXcoordinate = this.xCoordinate + this.offsetXMachineGun;
        this.machineGunYcoordinate = this.yCoordinate + this.offsetYMachineGun;
    }
    
    
    /**
     * Draws ship to the screen.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    {
        g2d.drawImage(shipBodyImg, xCoordinate, yCoordinate, null);
    }
    
}
