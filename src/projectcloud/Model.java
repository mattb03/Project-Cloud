package projectcloud;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * GAME LOGIC
 * 
 */

public class Model {
    
    // for random number generator.
    private Random random;
    
    // used for seting mouse position.
    private Robot robot;
    
    // Player - airship that is managed by player.
    private PlayerShip player;
    
    // Enemy turrets.
    private ArrayList<EnemyTurret> enemyTurretList = new ArrayList<EnemyTurret>();
    
    // Power rings
    private ArrayList<PowerRing> powerRingList = new ArrayList<PowerRing>();
    
    //Trees
    private ArrayList<Tree> treeList = new ArrayList<Tree>();
    
    // Explosions
    private ArrayList<Animation> explosionsList;
    private BufferedImage explosionAnimImg;
    
    // List of all the machine gun bullets.
    private ArrayList<Bullet> bulletsList;
    
    // List of all the tower gun bullets.
    private ArrayList<GBullet> gBulletsList;
    
    // List of all the bombss.
    private ArrayList<Bomb> bombsList;
    

    // Image for the sky color.
    private BufferedImage backgroundImg;
    
    // Image of mouse cursor.
    private BufferedImage mouseCursorImg;
    
    // Font that we will use to write statistic to the screen.
    private Font font;
    
    // Statistics (destroyed enemies, health)
    private int destroyedEnemies;
    
    private int health;
    
    //private boolean enemyFiring = false;

    public Model()
    {
        Controller.gameState = Controller.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                Controller.gameState = Controller.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Setter
     */
    private void Initialize()
    {
        random = new Random();
        
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        player = new PlayerShip(Controller.frameWidth / 4, Controller.frameHeight / 4);
        
        enemyTurretList = new ArrayList<EnemyTurret>();
        
        treeList = new ArrayList<Tree>();
        
        powerRingList = new ArrayList<PowerRing>();
        
        explosionsList = new ArrayList<Animation>();
        
        bulletsList = new ArrayList<Bullet>();
        
        gBulletsList = new ArrayList<GBullet>();
        
        bombsList = new ArrayList<Bomb>();

        font = new Font("ariel", Font.BOLD, 20);
        
        destroyedEnemies = 0;
        health = player.health;
        CloudSound.MUSIC.loop();

    }
    
    /**
     * Load game files (images).
     */
    private void LoadContent()
    {
        try 
        {
            // Images of environment
            URL backgroundImgUrl = this.getClass().getResource("/projectcloud/resources/images/GameField.jpg/");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
            URL turretBodyImgUrl = this.getClass().getResource("/projectcloud/resources/images/tower2.png");
            EnemyTurret.turretBodyImg = ImageIO.read(turretBodyImgUrl);
            EnemyTurret.originalTurret = ImageIO.read(turretBodyImgUrl);
            
            //Load image for power ring
            URL powerRingImgUrl = this.getClass().getResource("/projectcloud/resources/images/ring.png");
            PowerRing.powerRingImg = ImageIO.read(powerRingImgUrl);
            PowerRing.originalRing = ImageIO.read(powerRingImgUrl);
            
            // Images of bomb
            URL bombImgUrl = this.getClass().getResource("/projectcloud/resources/images/bomb.png");
            Bomb.bombImg = ImageIO.read(bombImgUrl);
            
            // Image of explosion animation.
            URL explosionAnimImgUrl = this.getClass().getResource("/projectcloud/resources/images/explosion_anim.png");
            explosionAnimImg = ImageIO.read(explosionAnimImgUrl);
            
            // Image of mouse cursor.
            URL mouseCursorImgUrl = this.getClass().getResource("/projectcloud/resources/images/crosshair.png");
            mouseCursorImg = ImageIO.read(mouseCursorImgUrl);
            
            // Ship machine gun bullet.
            URL bulletImgUrl = this.getClass().getResource("/projectcloud/resources/images/RedShot.png");//bullet.png
            Bullet.bulletImg = ImageIO.read(bulletImgUrl);
            
            // Tower machine gun bullet.
            URL gbulletImgUrl = this.getClass().getResource("/projectcloud/resources/images/GreenShot.png");//bullet.png
            GBullet.gbulletImg = ImageIO.read(gbulletImgUrl);
            
            //Tree
            URL treeImgUrl = this.getClass().getResource("/projectcloud/resources/images/Tree.png");
            Tree.treeImg = ImageIO.read(treeImgUrl);
            Tree.originalTree = ImageIO.read(treeImgUrl);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Restart game & reset some variables.
     */
    public void RestartGame()
    {
    	CloudSound.MUSIC.stop();
    	CloudSound.MUSIC.loop();
        player.Reset(Controller.frameWidth / 4, Controller.frameHeight / 4);
        
        EnemyTurret.restartEnemy();
        Tree.restartEnemy();
        
        Bullet.timeOfLastCreatedBullet = 0;
        Bomb.timeOfLastCreatedBomb = 0;
        // Empty all the lists.
        treeList.clear();
        enemyTurretList.clear();
        powerRingList.clear();
        bulletsList.clear();
        gBulletsList.clear();
        bombsList.clear();
        explosionsList.clear();
        
        // Statistics
        destroyedEnemies = 0;
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime elapsed time in nanosecs.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        /* Player */
        // When player is destroyed and all explosions are finished showing we change game status.
        if( !isPlayerAlive() && explosionsList.isEmpty() ){
            Controller.gameState = Controller.GameState.GAMEOVER;
            return; 
        }
        // when ammo runs out, game over
        if(player.numberOfAmmo <= 0 && 
           player.numberOfBombs <= 0 && 
           bulletsList.isEmpty() && 
           bombsList.isEmpty() && 
           explosionsList.isEmpty())
        {
            Controller.gameState = Controller.GameState.GAMEOVER;
            return;
        }
        // If player is alive we update him.
        if(isPlayerAlive()){
            isPlayerShooting(gameTime, mousePosition);
            isTowerShooting(gameTime);
            didPlayerLaunchBomb(gameTime, mousePosition);
            //gobackhere
            player.isMoving();
            player.Update();
            
        }
        
        /* Bullets */
        updateBullets();
        
        //GBullets*/
        updateGBullet(gameTime);
        
        /* Bombs */
        updateBombs(gameTime); // It also checks for collisions (if any of the bombs hit any of the enemies).
        
        /* Enemies */
        createEnemyTurret(gameTime);
        updateEnemies(gameTime);
        
        createTree(gameTime);
        updateTrees();
        /*Rings*/
        createPowerRing(gameTime);
        updateRings();
        
        /* Explosions */
        updateExplosions();
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g2d Graphics2D
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        // Image for background color.
        g2d.drawImage(backgroundImg, 0, 0, Controller.frameWidth, Controller.frameHeight, null);

        
        if(isPlayerAlive())
            player.Draw(g2d);
        
        // Draws all the enemies.
        for(int i = 0; i < enemyTurretList.size(); i++)
        {
            enemyTurretList.get(i).Draw(g2d);
        }
        
        // Draws all the rings.
        for(int i = 0; i < powerRingList.size(); i++)
        {
            powerRingList.get(i).Draw(g2d);
        }
        
        //Draws all the trees
        for(int i = 0; i < treeList.size(); i++)
        {
            treeList.get(i).Draw(g2d);
        }
        
        // Draws all the bullets. 
        for(int i = 0; i < bulletsList.size(); i++)
        {
            bulletsList.get(i).Draw(g2d);
        }
        
        // Draws all the gBullets
        for(int i = 0; i < gBulletsList.size(); i++)
        {
            gBulletsList.get(i).Draw(g2d);
        }
        
        // Draws all the bombs. 
        for(int i = 0; i < bombsList.size(); i++)
        {
            bombsList.get(i).Draw(g2d);
        }
        // Draw all explosions.
        for(int i = 0; i < explosionsList.size(); i++)
        {
            explosionsList.get(i).Draw(g2d);
        }
        
        // Draw stats
        g2d.setFont(font);
        g2d.setColor(Color.darkGray);
        
        g2d.drawString(formatTime(gameTime), Controller.frameWidth/2 - 45, 21);
        g2d.drawString("DESTROYED: " + destroyedEnemies, 10, 21);
        g2d.drawString("BOMBS: "   + player.numberOfBombs, 10, 81);
        g2d.drawString("AMMO: "      + player.numberOfAmmo, 10, 101);
        g2d.drawString("HEALTH: "      + player.health, 10, 121);
        

        // Mouse cursor
        if(isPlayerAlive())
            drawCursor(g2d, mousePosition);
    }
    
    /**
     * Draws stats at game over.
     * 
     * @param g2d Graphics2D
     * @param gameTime Elapsed game time.
     */
    public void DrawStatistic(Graphics2D g2d, long gameTime){
    	CloudSound.MUSIC.stop();
        g2d.drawString("Time: " + formatTime(gameTime),                Controller.frameWidth/2 - 50, Controller.frameHeight/3 + 80);
        g2d.drawString("Bombs left: "      + player.numberOfBombs, Controller.frameWidth/2 - 55, Controller.frameHeight/3 + 105);
        g2d.drawString("Ammo left: "         + player.numberOfAmmo,    Controller.frameWidth/2 - 55, Controller.frameHeight/3 + 125);
        g2d.drawString("Destroyed enemies: " + destroyedEnemies,       Controller.frameWidth/2 - 65, Controller.frameHeight/3 + 150);
        g2d.setFont(font);
        g2d.drawString("Statistics: ",                                 Controller.frameWidth/2 - 75, Controller.frameHeight/3 + 60);
    }
    
    /**
     * Draws mouse cursor.
     * 
     * @param g2d Graphics2D
     * @param mousePosition Position of the mouse.
     */
    private void drawCursor(Graphics2D g2d, Point mousePosition)
    {      
    	// We substract half of the cursor image so that will be drawn in center of the y mouse coordinate.
        g2d.drawImage(mouseCursorImg, mousePosition.x, mousePosition.y - mouseCursorImg.getHeight()/2, null); 
        
    }
    
    /**
     * Format given time into 00:00 format.
     * 
     * @param time Time that is in nanoseconds.
     * @return Time in 00:00 format.
     */
    private static String formatTime(long time){
            // Given time in seconds.
            int sec = (int)(time / Controller.milisecInNanosec / 1000);

            // Given time in minutes and seconds.
            int min = sec / 60;
            sec = sec - (min * 60);

            String minString, secString;

            if(min <= 9)
                minString = "0" + Integer.toString(min);
            else
                minString = "" + Integer.toString(min);

            if(sec <= 9)
                secString = "0" + Integer.toString(sec);
            else
                secString = "" + Integer.toString(sec);

            return minString + ":" + secString;
    }
      
    /*
     * 
     * Methods for updating the game. 
     * 
     */
    
     
    /**
     * Check if player is alive. If not, set game over status.
     * 
     * @return True if player is alive, false otherwise.
     */
    private boolean isPlayerAlive()
    {
        if(player.health <= 0){
            return false;
        }
        return true;
    }
    
    /**
     * Checks if the player is shooting with the machine gun and creates bullets if he shooting.
     * 
     * @param gameTime Game time.
     */
    private void isPlayerShooting(long gameTime, Point mousePosition)
    {
        if(player.isShooting(gameTime))
        {
        	CloudSound.SHIPFIRE.play();
            Bullet.timeOfLastCreatedBullet = gameTime;
            player.numberOfAmmo--;
            
            Bullet b = new Bullet(player.machineGunXcoordinate, player.machineGunYcoordinate, mousePosition);
            bulletsList.add(b);
        }
    }
    
    
    
    /**
     * Checks if the Tower is shooting with the machine gun and creates bullets if he shooting.
     * 
     * @param gameTime Game time.
     * @param et 
     */
    
    private void isTowerShooting(long gameTime)
    {
    	 for(int i = 0; i < enemyTurretList.size(); i++)
         {
             EnemyTurret et = enemyTurretList.get(i);
             	if(et.isFiring(gameTime)){        		
             			GBullet.timeOfLastCreatedBullet = gameTime;
                		GBullet gb = new GBullet(et.machineGunXcoordinate, et.machineGunYcoordinate, player);
                		//for(int k = 0; k < 4; k++){
                			gBulletsList.add(gb);  
                		//}
             	}
         }
    }
    
    /**
     * Checks if the player is fired the bomb and creates it if he did.
     * It also checks if player can fire the bomb.
     * 
     * @param gameTime Game time.
     */
    private void didPlayerLaunchBomb(long gameTime, Point mousePosition)
    {
        if(player.isFiredBomb(gameTime))
        {
            Bomb.timeOfLastCreatedBomb = gameTime;
            player.numberOfBombs--;
            
            Bomb r = new Bomb();
            r.Initialize(player.bombHolderXcoordinate, player.bombHolderYcoordinate, mousePosition);
            bombsList.add(r);
        }
    }
    
  /*  private void didTurretFire(long gameTime, PlayerShip player){
    	if(player.isFiredBomb(gameTime))
        {
            Bomb.timeOfLastCreatedBomb = gameTime;
            player.numberOfBombs--;
            
            Bomb r = new Bomb();
            r.Initialize(player.bombHolderXcoordinate, player.bombHolderYcoordinate, mousePosition);
            bombsList.add(r);
        }
    }
    */
    /**
     * Creates a new enemy if it's time.
     * 
     * @param gameTime Game time.
     */
    private void createEnemyTurret(long gameTime)
    {
        if(gameTime - EnemyTurret.timeOfLastCreatedEnemy >= EnemyTurret.timeBetweenNewEnemies)
        {
            EnemyTurret et = new EnemyTurret();
            int xCoordinate = random.nextInt(Controller.frameWidth);
         
            int yCoordinate = (int)(Controller.frameHeight * 0.60);
    		
            et.Initialize(xCoordinate, yCoordinate);
            // Add created enemy to the list of enemies.
            enemyTurretList.add(et);
            
            // Speed up enemy speed and appearance.
            EnemyTurret.speedUp();
            
            // Sets new time for last created enemy.
            EnemyTurret.timeOfLastCreatedEnemy = gameTime;
        }
    }
    
    private void createTree(long gameTime)
    {
        if(gameTime - Tree.timeOfLastCreatedEnemy >= Tree.timeBetweenNewEnemies)
        {
            Tree t = new Tree();
            int xCoordinate = random.nextInt(Controller.frameWidth);

            int yCoordinate = (int)(Controller.frameHeight * 0.60);
            t.Initialize(xCoordinate, yCoordinate);
            treeList.add(t);
            
            Tree.speedUp();

            Tree.timeOfLastCreatedEnemy = gameTime;
        }
    }
    
    /**
     * Creates a new ring if it's time.
     * 
     * @param gameTime Game time.
     */
    private void createPowerRing(long gameTime)
    {
     if(gameTime - PowerRing.timeOfLastCreatedEnemy >= (PowerRing.timeBetweenNewEnemies*5)) 
        {
            PowerRing pr = new PowerRing();
            
            int xCoordinate = random.nextInt(Controller.frameWidth);//CHANGED BY ME
            int yCoordinate = random.nextInt((Controller.frameHeight/2));
            pr.Initialize(xCoordinate, yCoordinate);
    
            powerRingList.add(pr);
            
            // Speed up enemy speed and appearance.
            PowerRing.speedUp();
            
            // Sets new time for last created enemy.
            PowerRing.timeOfLastCreatedEnemy = gameTime;
        }
    }
    
    
    /**
     * Updates all enemies.
     * Move the turret
     * Updates turret 
     * Checks if enemy was destroyed.
     * Checks if any enemy collision with player.
     */
    private void updateEnemies(long gameTime)
    {
        for(int i = 0; i < enemyTurretList.size(); i++)
        {
            EnemyTurret et = enemyTurretList.get(i);
            
            et.sizeUp();
            et.Update();
            
            // Is crashed?
            Rectangle playerRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, player.shipBodyImg.getWidth(), player.shipBodyImg.getHeight());
            Rectangle enemyRectangel = new Rectangle(et.xCoordinate, et.yCoordinate, EnemyTurret.turretBodyImg.getWidth(), EnemyTurret.turretBodyImg.getHeight());
            
            if(playerRectangel.intersects(enemyRectangel) || ((playerRectangel.intersects(enemyRectangel) && et.turretBodyImg.getHeight() >= (et.originalTurret.getHeight() * 0.60)))) {
                player.health -= 50;
                CloudSound.IMPACT.play();
                // Remove turret from the list.
                et.resetLimit();
                enemyTurretList.remove(i);
                
                // resets turret size
                et.restartSize(115, 212/*et.originalTurret.getWidth(), et.originalTurret.getHeight()*/);
                
               
                // Add explosion of player ship.
                if (!isPlayerAlive())
                	CloudSound.SHIPEXPLODE.play();
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, player.xCoordinate + exNum*60, player.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
                // Add explosion of enemy turret.
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, et.xCoordinate + exNum*60, et.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
                
                break;
            }
            
            // Check health.
            if(et.health <= 0)
            {
            	//GBullet gb = new GBullet();
        		//gb.Initialize(et.xCoordinate, et.yCoordinate, player.xCoordinate, player.yCoordinate);
        		//gbulletsList.add(gb);
            	
                // Add explosion of turret.
                Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, et.xCoordinate, et.yCoordinate - explosionAnimImg.getHeight()/3, 0); // Substring 1/3 explosion image height (explosionAnimImg.getHeight()/3) so that explosion is drawn more at the center of the turret.
                explosionsList.add(expAnim);
                
                // Increase the destroyed enemies counter.
                destroyedEnemies++;
                CloudSound.TURRETEXPLODE.play();
                // Remove turret from the list.
                enemyTurretList.remove(i);
                //reset turret size
                et.restartSize(115, 212);
                
                //reset fire limit
                et.resetLimit();
                // turret was destroyed so we can move to next turret.
                continue;
            }
                        
            //if the enemy turret left the screen, destroy it. 
            if(et.isLeftScreen() || et.isRightScreen() || et.isBottomScreen())
            {
            	//reset turret size
            	et.restartSize(115, 212/*et.originalTurret.getWidth(), et.originalTurret.getHeight()*/);
            	et.resetLimit();
                enemyTurretList.remove(i);

            }
            if(et.turretBodyImg.getWidth() > 400 && et.turretBodyImg.getHeight() > 600)
            {
            	et.resetLimit();
            	et.restartSize(115, 212/*et.originalTurret.getWidth(), et.originalTurret.getHeight()*/);
            	enemyTurretList.remove(i);
              
            }
        }
    }
    /**
     * Update trees
     * 
     */
    private void updateTrees()
    {
        for(int i = 0; i < treeList.size(); i++)
        {
            Tree t = treeList.get(i);
            
            t.Update();
            t.sizeUp();
            // Is crashed?
            Rectangle playerRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, player.shipBodyImg.getWidth(), player.shipBodyImg.getHeight());
            Rectangle enemyRectangel = new Rectangle(t.xCoordinate, t.yCoordinate, Tree.treeImg.getWidth(), Tree.treeImg.getHeight());
            if(playerRectangel.intersects(enemyRectangel) || (playerRectangel.intersects(enemyRectangel) && (t.treeImg.getHeight() >= (t.originalTree.getHeight() * 0.60)))){
            	CloudSound.IMPACT.play();
                player.health -= 50;
                
                // Remove turret from the list.
                treeList.remove(i);
                //reset tree size
                t.restartSize(151,320); //
                
                // Add explosion of player ship.
                if(!isPlayerAlive())
                	CloudSound.SHIPEXPLODE.play();
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, player.xCoordinate + exNum*60, player.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
                // Add explosion of tree.
                for(int exNum = 0; exNum < 3; exNum++){
                    Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, t.xCoordinate + exNum*60, t.yCoordinate - random.nextInt(100), exNum * 200 +random.nextInt(100));
                    explosionsList.add(expAnim);
                }
               
                break;
            }
            
            // Check health.
            if(t.health <= 0){
                // Add explosion of tree (because trees explode lol).
                Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, t.xCoordinate, t.yCoordinate - explosionAnimImg.getHeight()/3, 0); 
             // Substring 1/3 explosion image height (explosionAnimImg.getHeight()/3) so that explosion is drawn more at the center of the turret.
                explosionsList.add(expAnim);
                CloudSound.TURRETEXPLODE.play();
                
                // Remove tree from the list.
                treeList.remove(i);
                //tree size resets
                t.restartSize(151,320); //
                
                // tree was destroyed so we can move to next tree.
                continue;
            }
            //if the enemy turret left the screen, destroy it. 
            if(t.isLeftScreen() || t.isRightScreen() || t.isBottomScreen())
            {
            	//reset tree size
            	t.restartSize(151,320); 
                treeList.remove(i);

            }
            if(t.treeImg.getWidth() > 300 && t.treeImg.getHeight() > 600)
            {
            	//reset tree size
              t.restartSize(151,320);
              treeList.remove(i);
            }
        }
    }
    /**
     * Updates all rings.
     * Move the ring and checks if it left the screen.
     * Checks if any ring collision with player.
     */
    
    private void updateRings()
    {
        for(int i = 0; i < powerRingList.size(); i++)
        {
            PowerRing pr = powerRingList.get(i);
            
            pr.Update();
            pr.sizeUp();
            
            // player went through ring?
            Rectangle playerRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, player.shipBodyImg.getWidth(), player.shipBodyImg.getHeight());
            Rectangle enemyRectangel = new Rectangle(pr.xCoordinate, pr.yCoordinate, PowerRing.powerRingImg.getWidth(), PowerRing.powerRingImg.getHeight());
            if(playerRectangel.intersects(enemyRectangel)){
            	player.health += 25;
                CloudSound.HEALTH.play();
                pr.restartSize(200, 200);
                powerRingList.remove(i);
            }
            
            // Check health.
            if(pr.health <= 0){
                Animation expAnim = new Animation(explosionAnimImg, 134, 134, 12, 45, false, pr.xCoordinate, pr.yCoordinate - explosionAnimImg.getHeight()/3, 0);
                explosionsList.add(expAnim);
                //reset size
                pr.restartSize(200, 200);
                powerRingList.remove(i);
                continue;
            }
            
            if(pr.isLeftScreen() || pr.isRightScreen() || pr.isBottomScreen())
            {
            	//reset size
            	pr.restartSize(200, 200);
                powerRingList.remove(i);
            }
            if(pr.powerRingImg.getWidth() > 400 && pr.powerRingImg.getHeight() > 400)
            {
              pr.restartSize(200, 200);
              powerRingList.remove(i);
            }
        }
    }
    
    /**
     * Update bullets. 
     * It moves bullets.
     * Checks if the bullet is left the screen.
     * Checks if any bullets is hit any enemy.
     */
    private void updateBullets()
    {
        for(int i = 0; i < bulletsList.size(); i++)
        {
            Bullet bullet = bulletsList.get(i);
            
            // Move the bullet.
            bullet.Update();
            
            // Is left the screen?
            if(bullet.isItLeftScreen()){
                bulletsList.remove(i);
                // Bullet have left the screen so we removed it from the list and now we can continue to the next bullet.
                continue;
            }
            
            // Did hit any enemy?
            // Rectangle of the bullet image.
            Rectangle bulletRectangle = new Rectangle((int)bullet.xCoordinate, (int)bullet.yCoordinate, Bullet.bulletImg.getWidth(), Bullet.bulletImg.getHeight());
            // Go trough all enemies.
            for(int j = 0; j < enemyTurretList.size(); j++)
            {
                EnemyTurret et = enemyTurretList.get(j);

                // Current enemy rectangle.
                Rectangle enemyRectangel = new Rectangle(et.xCoordinate, et.yCoordinate, EnemyTurret.turretBodyImg.getWidth(), EnemyTurret.turretBodyImg.getHeight());

                // Is current bullet over current enemy?
                if(bulletRectangle.intersects(enemyRectangel))
                {
                    // Bullet hit the enemy so we reduce his health.
                    et.health -= Bullet.damagePower;
                    
                    // Bullet was also destroyed so we remove it.
                    bulletsList.remove(i);
                    
                    // That bullet hit enemy so we don't need to check other enemies.
                    break;
                }
            }
            for(int j = 0; j < treeList.size(); j++)
            {
                Tree t = treeList.get(j);

                // Current enemy rectangle.
                Rectangle enemyRectange2 = new Rectangle(t.xCoordinate, t.yCoordinate, Tree.treeImg.getWidth(), Tree.treeImg.getHeight());

                // Is current bullet over current enemy?
                if(bulletRectangle.intersects(enemyRectange2))
                {
                    // Bullet hit the enemy so we reduce his health.
                    t.health -= Bullet.damagePower;
                    
                    // Bullet was also destroyed so we remove it.
                    bulletsList.remove(i);
                    
                    // That bullet hit enemy so we don't need to check other enemies.
                    break;
                }
            }
        }
    }

    
    /**
     * Update bombs. 
     * Checks if bomb hit an enemy
     * 
     * @param gameTime Game time.
     */
    private void updateBombs(long gameTime)
    {
        for(int i = 0; i < bombsList.size(); i++)
        {
            Bomb bomb = bombsList.get(i);

            bomb.Update();

            if(bomb.isItLeftScreen())
            {
                bombsList.remove(i);
                continue;
            }
            if( checkIfBombHitEnemy(bomb) )
                bombsList.remove(i);
        }
        
    }
    
    
    private void updateGBullet(long gameTime)
    {
        for(int i = 0; i < gBulletsList.size(); i++)
        {
            GBullet gb = gBulletsList.get(i);

            gb.Update();

            if(gb.isItLeftScreen())
            {
                gBulletsList.remove(i);
                continue;
            }
            if(gb.isItLeftScreen()){
                gBulletsList.remove(i);
                
                continue;
        }
            
            // Did hit player?
            // Rectangle of the bullet image.
            Rectangle bulletRectangle = new Rectangle((int)gb.xCoordinate, (int)gb.yCoordinate, GBullet.gbulletImg.getWidth(), GBullet.gbulletImg.getHeight());
            // Go trough all enemies.
            for(int j = 0; j < enemyTurretList.size(); j++)
            {
                EnemyTurret et = enemyTurretList.get(j);

                // Current enemy rectangle.
                Rectangle enemyRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, player.shipBodyImg.getWidth(), player.shipBodyImg.getHeight());

                // Is current bullet over current enemy?
                if(bulletRectangle.intersects(enemyRectangel))
                {
                    // Bullet hit the enemy so we reduce his health.
                    player.health -= GBullet.damagePower;
                    CloudSound.IMPACT.play();
                    // Bullet was also destroyed so we remove it.
                    gBulletsList.remove(i);
                    
                    // That bullet hit enemy so we don't need to check other enemies.
                    break;
                }
            }
        }
    }
    
    /**
     * Checks if the given bomb hit any of the enemies.
     * 
     * @param bomb Bomb to check.
     * @return True if it hit any of enemies, false otherwise.
     */
    private boolean checkIfBombHitEnemy(Bomb bomb)
    {
        boolean didItHitEnemy = false;
        
        Rectangle bombRectangle = new Rectangle(bomb.xCoordinate, bomb.yCoordinate, Bomb.bombImg.getWidth(), Bomb.bombImg.getHeight());
        
        for(int j = 0; j < enemyTurretList.size(); j++)
        {
            EnemyTurret et = enemyTurretList.get(j);


            Rectangle enemyRectangel = new Rectangle(et.xCoordinate, et.yCoordinate, EnemyTurret.turretBodyImg.getWidth(), EnemyTurret.turretBodyImg.getHeight());

            if(bombRectangle.intersects(enemyRectangel))
            {
                didItHitEnemy = true;
                CloudSound.BOMB.play();
                et.health -= Bomb.damagePower;

                break;
            }
        }
        for(int j = 0; j < treeList.size(); j++)
        {
            Tree t = treeList.get(j);

            Rectangle treeRectange2 = new Rectangle(t.xCoordinate, t.yCoordinate, Tree.treeImg.getWidth(), Tree.treeImg.getHeight());

            if(bombRectangle.intersects(treeRectange2))
            {
             
             didItHitEnemy = true;
                // Bullet hit the enemy so we reduce his health.
             	CloudSound.BOMB.play();
                t.health -= Bomb.damagePower;
            
                break;
            }
        }
        
        return didItHitEnemy;
    }
    
   /* private boolean checkIfGBulletHitPlayer(GBullet gb)
    {
        boolean didItHitPlayer = false;
        
        Rectangle GBulletRectangle = new Rectangle((int)gb.xCoordinate, (int)gb.yCoordinate, gb.gbulletImg.getWidth(), gb.gbulletImg.getHeight());
        
        for(int j = 0; j < gBulletsList.size(); j++)
        {
            Rectangle enemyRectangel = new Rectangle(player.xCoordinate, player.yCoordinate, GBullet.gbulletImg.getWidth(), GBullet.gbulletImg.getHeight());

            if(GBulletRectangle.intersects(enemyRectangel))
            {
                didItHitPlayer = true;
                player.health -= gb.damagePower;

                break;
            }
        }
        return didItHitPlayer;
    }
    */

    /**
     * Updates all the animations of an explosion and remove the animation when is over.
     */
    private void updateExplosions()
    {
        for(int i = 0; i < explosionsList.size(); i++)
        {
            // If the animation is over we remove it from the list.
            if(!explosionsList.get(i).active)
                explosionsList.remove(i);
        }
    }
}
