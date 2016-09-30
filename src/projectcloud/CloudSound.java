package projectcloud;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum CloudSound {
	
	SELECT("../ProjectCloud/src/projectcloud/resources/audio/131658__bertrof__game-sound-selection.wav"),
	BOMB("../ProjectCloud/src/projectcloud/resources/audio/155235_zangrutz_bomb-small.wav"),
	TURRETFIRE("../ProjectCloud/src/projectcloud/resources/audio/221560__alaskarobotics__laser-shot.wav"),
	TURRETEXPLODE("../ProjectCloud/src/projectcloud/resources/audio/275897__n-audioman__blip.wav"),
	SHIPFIRE("../ProjectCloud/src/projectcloud/resources/audio/277218__thedweebman__8-bit-laser.wav"),
	SHIPEXPLODE("../ProjectCloud/src/projectcloud/resources/audio/170146_timgormly_8-bit-explosion.wav"),
	IMPACT("../ProjectCloud/src/projectcloud/resources/audio/257569__udderdude__bfxr5.wav"),
	POINT("../ProjectCloud/src/projectcloud/resources/audio/270303__littlerobotsoundfactory__collect-point-01.wav"),
	HEALTH("../ProjectCloud/src/projectcloud/resources/audio/162387__zandernoriega__health-1.wav"),
	POWERUP("../ProjectCloud/src/projectcloud/resources/audio/257225__javierzumer__8bit-powerup.wav"),
	MENU("../ProjectCloud/src/projectcloud/resources/audio/Euphoria_seamless_loop.wav"),
	GAMESTART("../ProjectCloud/src/projectcloud/resources/audio/243020_plasterbrain_game-start.wav"),
	/**
	 * Its_on_PCloud and Euphoria_seamless_loop are original compositions by Jamail Chachere.
	 */
	MUSIC("../ProjectCloud/src/projectcloud/resources/audio/Its_on_PCloud.wav");
	
	/**
	 * Credit to sound effects in respective order:
	 * 
	 * https://freesound.org/people/Bertrof/sounds/131658/
	 * http://www.freesound.org/people/Zangrutz/sounds/155235/
	 * https://www.freesound.org/people/AlaskaRobotics/sounds/221560/
	 * https://www.freesound.org/people/n_audioman/sounds/275897/
	 * https://www.freesound.org/people/TheDweebMan/sounds/277218/
	 * https://www.freesound.org/people/TheDweebMan/sounds/277218/
	 * http://www.freesound.org/people/Udderdude/sounds/257569/
	 * https://freesound.org/people/LittleRobotSoundFactory/sounds/270303/
	 * http://www.freesound.org/people/zandernoriega/sounds/162387/
	 * http://www.freesound.org/people/JavierZumer/sounds/257225/
	 * Jamail Chachere
	 * http://www.freesound.org/people/plasterbrain/sounds/243020/
	 * Jamail Chachere
	 * 
	 * 
	 */
	
	 /**
	  * Nested class for specifying volume
	  *
	  */
		public static enum Volume {
			MUTE, LOW, MEDIUM, HIGH
		}
		   
		public static Volume volume = Volume.LOW;
		
	/**
	 * Global Clip.
	 */
	private Clip clip;
	
	//Constructor for each clip
	
	/**
	 * Constructor for creating the sound clip objects.
	 * @param soundFilename is a string of the file location
	 */
	CloudSound(String soundFilename){
		try {
	         // Use URL (instead of File) to read from disk and JAR.
	         File in = new File(soundFilename);
	         // Set up an audio input stream piped from the sound file.
	         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
	         // Get a clip resource.
	         clip = AudioSystem.getClip();
	         // Open audio stream and load samples from the audio input stream.
	         clip.open(audioInputStream);
	      } catch (UnsupportedAudioFileException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (LineUnavailableException e) {
	         e.printStackTrace();
	      }
	}
	
	/**
	 *  Plays a clip once, after stopping a previous instance of the same clip.  
	 */
	public void play() {
		 if (volume != Volume.MUTE) {
	         if (clip.isRunning())
	            clip.stop();   // Stop the player if it is still running
	         clip.setFramePosition(0); // rewind to the beginning
	         clip.start();     // Start playing
	      }
	}
	
	/**
	 * Stops any audio clip, especially useful for stopping loops.
	 */
	public void stop(){
		if (clip.isRunning()){ 
			clip.stop();
			clip.setFramePosition(0);
		}
	}
	
	/**
	 * Method designed for sound and music loop; It repeats an audio clip infinitely.
	 */
	public void loop(){
		if (volume != Volume.MUTE) {
			if(clip.isRunning()){
				clip.stop(); //This is used if a pause menu is to ever be drawn
			} else{
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
		}
	}
	
	/**
	 * Calls the constructor for all the elements
	 */
	public static void init() {
	      values(); // calls the constructor for all the elements
	   }
}

