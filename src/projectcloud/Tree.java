package projectcloud;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tree {
 // For creating new trees
    private static final long timeBetweenNewEnemiesInit = Controller.secInNanosec * 7;
    public static long timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
    public static long timeOfLastCreatedEnemy = 0;
    
    // Health of the tree.
    public int health;
    
    // Position of the tree on the screen.
    public int xCoordinate;
    public int yCoordinate;
    
    //is tree on left of screen
    
    public boolean spawnleft;
    public boolean spawnright;

    // Moving speed and direction.
    private static final double movingXspeedInit = -4;
    private static final double movingYspeedInit = 4; //ADDED BY ME
    private static double movingXspeed = movingXspeedInit;
    private static double movingYspeed = movingYspeedInit; //ADDED BY ME
    
    // Images of tree. Images are loaded and set in Game class in LoadContent() method.
    public static BufferedImage treeImg;
    public static BufferedImage originalTree;

    /**
     * Initialize tree.
     * 
     * @param xCoordinate Starting x coordinate of tree.
     * @param yCoordinate Starting y coordinate of tree.
     * @param shipBodyImg Image of tree body.
     */
    public void Initialize(int xCoordinate, int yCoordinate)
    {
        health = 30;
        
        // Sets tree position.
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        
        //
        if(xCoordinate < (Controller.frameWidth/2)){
         this.spawnleft = true;
        } else {
         this.spawnright = true;
        }
        
       
        
        if (spawnleft){
        Tree.movingXspeed = -4;
        } else if (spawnright){
         Tree.movingXspeed = 4;
        }
        
        Tree.movingYspeed = 2;
    }
    
    /**
     * It sets speed and time between enemies to the initial properties.
     */
    public static void restartEnemy(){
        Tree.timeBetweenNewEnemies = timeBetweenNewEnemiesInit;
        Tree.timeOfLastCreatedEnemy = 0;
        Tree.movingXspeed = movingXspeedInit;
    }
    
    
    /**
     * It increase tree speed and decrease time between new enemies.
     */
    public static void speedUp(){
        if(Tree.timeBetweenNewEnemies > Controller.secInNanosec)
            Tree.timeBetweenNewEnemies -= Controller.secInNanosec / 100;
        
        Tree.movingXspeed -= 0.25;
    }
    
    
    /**
     * Checks if the tree is left the screen.
     * 
     * @return true if the tree is left the screen, false otherwise.
     */
    public boolean isLeftScreen()
    {
        if(xCoordinate < 0 - treeImg.getWidth()) // When the entire tree is out of the screen.
            return true;
        else
            return false;
    }
    // ADDED BY ME
    public boolean isRightScreen()
    {
        if(xCoordinate > (Controller.frameWidth )) // When the entire tree is out of the screen.
            return true;
        else
            return false;
    }
    public boolean isBottomScreen(){
     if(yCoordinate > (Controller.frameHeight + treeImg.getHeight())) // When the entire ree is out of the screen.
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
      double w = (double) treeImg.getWidth() * 1.15;
      double h = (double) treeImg.getHeight() * 1.15;
      if (treeImg.getHeight() <= (int)(originalTree.getWidth() * 0.25) && treeImg.getHeight() <= (int)(originalTree.getWidth() * 0.50))
      {
        w = (double) treeImg.getWidth() * 1.10;
        h = (double) treeImg.getHeight() * 1.10;
        this.treeImg = resizeImg(this.treeImg, (int)w, (int)h);
      }
      else if (treeImg.getHeight() >= (int)(originalTree.getWidth() * 0.50) && treeImg.getHeight() >= (int)(originalTree.getWidth() * 0.50))
      {
        
        w = (double) treeImg.getWidth() * 1.02;
        h = (double) treeImg.getHeight() * 1.02;
        this.treeImg = resizeImg(this.treeImg, (int)w, (int)h);
      }
      
      this.treeImg = resizeImg(this.treeImg, (int)w, (int)h);
      
    }
    
    /*
     * Restarts image's dimensions
     * 
     */
    
    public void restartSize(int w, int h)
    {
      this.treeImg = resizeImg(this.originalTree, (int)originalTree.getWidth(), (int)originalTree.getHeight());
    }
    
    
    //ADDED BY ME    
    /**
     * Updates position of tree, animations.
     */
    public void Update()
    {
        // Move tree on x coordinate.
        xCoordinate += movingXspeed;
        yCoordinate += movingYspeed;// ADDED BY ME
    }
       
    /**
     * Draws tree to the screen.
     * 
     * @param g2d Graphics2D
     */
    public void Draw(Graphics2D g2d)
    { 
        g2d.drawImage(treeImg, xCoordinate, yCoordinate, null);
    }
}
