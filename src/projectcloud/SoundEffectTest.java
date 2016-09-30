package projectcloud;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
   
// Testing the SoundEffect enum in a Swing application
@SuppressWarnings("serial")
public class SoundEffectTest extends JFrame {
   
   // Constructor
   public SoundEffectTest() {
      // Pre-load all the sound files
      CloudSound.init();
      CloudSound.volume = CloudSound.Volume.LOW;  // un-mute
   
      // Set up UI components
      Container cp = this.getContentPane();
      cp.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
      JButton btnSound1 = new JButton("Select");
      btnSound1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.SELECT.play();
         }
      });
      cp.add(btnSound1);
      JButton btnSound2 = new JButton("BOMB");
      btnSound2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.BOMB.play();
         }
      });
      cp.add(btnSound2);
      JButton btnSound3 = new JButton("T-Fire");
      btnSound3.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.TURRETFIRE.play();
         }
      });
      cp.add(btnSound3);
      JButton btnSound4 = new JButton("T-Explo");
      btnSound4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.TURRETEXPLODE.play();
         }
      });
      cp.add(btnSound4);
      JButton btnSound5 = new JButton("Ship Fire");
      btnSound5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.SHIPFIRE.play();
         }
      });
      cp.add(btnSound5);
      JButton btnSound6 = new JButton("Ship Explode");
      btnSound6.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.SHIPEXPLODE.play();
         }
      });
      cp.add(btnSound6);
      JButton btnSound7 = new JButton("Point Collect");
      btnSound7.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.POINT.play();
         }
      });
      cp.add(btnSound7);
      JButton btnSound8 = new JButton("Menu");
      btnSound8.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.MENU.loop();
         }
      });
      cp.add(btnSound8);
      JButton btnSound9 = new JButton("Music");
      btnSound9.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.MUSIC.loop();
         }
      });
      cp.add(btnSound9);
      JButton btnSound10 = new JButton("Gamestart");
      btnSound10.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.GAMESTART.play();
         }
      });
      cp.add(btnSound10);
      JButton btnSound11 = new JButton("Power-up");
      btnSound11.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.POWERUP.play();
         }
      });
      cp.add(btnSound11);
      JButton btnSound12 = new JButton("health");
      btnSound12.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.HEALTH.play();
         }
      });
      cp.add(btnSound12);
      JButton btnSound13 = new JButton("impact");
      btnSound13.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            CloudSound.IMPACT.play();
         }
      });
      cp.add(btnSound13);
      JButton btnSound14 = new JButton("MUTE");
      btnSound14.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
        	 if(CloudSound.volume == CloudSound.Volume.MUTE){
        		 CloudSound.volume = CloudSound.Volume.LOW;
        	 } else {
        		 CloudSound.volume = CloudSound.Volume.MUTE;
        	 }
         }
      });
      cp.add(btnSound14);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setTitle("Test SoundEffect");
      this.pack();
      this.setVisible(true);
   }
   
   public static void main(String[] args) {
      new SoundEffectTest();
   }
}