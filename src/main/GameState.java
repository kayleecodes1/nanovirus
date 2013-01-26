package main;
import java.util.ArrayList;
import java.util.Random;

public class GameState {
	
	private boolean	paused;			// Is the game paused?
	private int		x;				// The the x- and y-coordinate of the
	private int		y;				// 		nanovirus within the current tunnel image.
	private String	transition;		// The current transition of the nanovirus ["up", "down", or else].
	private int		speed;			// The speed of the nanovirus.
	private int		yoff;			// The y-offset of the next tunnel image.
	
	private boolean 			enemiesSpawned;	// Have enemies been spawned for the current tunnel?
	private ArrayList<Enemy>	enemiesCurrent;	// The enemies in the current tunnel.
	private ArrayList<Enemy>	enemiesNext;	// The enemies in the next tunnel.
	
	// The main GameState constructor.
	public GameState(boolean paused, int x, String transition, int y,
			int speed, int yoff) {
		this.paused = paused;
		this.x = x;
		this.y = y;
		this.transition = transition;
		this.speed = speed;
		this.yoff = yoff;
		this.enemiesCurrent = new ArrayList<Enemy>();
		this.enemiesNext = new ArrayList<Enemy>();
	}
	
	// ** MEMBER VARIABLE ACCESS METHODS **//
	public boolean isPaused() {
		return paused;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getTransition() {
		return transition;
	}
	public int getSpeed() {
		return speed;
	}
	public int getYoff() {
		return yoff;
	}
	public boolean getEnemiesSpawned() {
		return enemiesSpawned;
	}
	public ArrayList<Enemy> getCurrentEnemies() {
		return enemiesCurrent;
	}
	public ArrayList<Enemy> getNextEnemies() {
		return enemiesNext;
	}
	
	//** MEMBER VARIABLE MODIFICATION METHODS **//
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setTransition(String transition) {
		this.transition = transition;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public void setYoff(int yoff) {
		this.yoff = yoff;
	}
	public void setEnemiesSpawned(boolean enemiesSpawned) {
		this.enemiesSpawned = enemiesSpawned;
	}
	public void setCurrentEnemies(ArrayList<Enemy> currentEnemies) {
		this.enemiesCurrent = currentEnemies;
	}
	public void setNextEnemies(ArrayList<Enemy> nextEnemies) {
		this.enemiesNext = nextEnemies;
	}
	
	/**
	 * @param nanoVirusMain
	 * 
	 * 		updateBG: 	Updates the background, effectively "moving"
	 * 					the nanovirus around the game world.
	 */
	void updateBG(NanoVirusMain nanoVirusMain) {
		
		// The nanovirus moves slower before transition points ...
		if (getX() < 150)
			setX((int) (getX() + (150 + 5 - getX()) / 100.0 * getSpeed()));
		// ... otherwise the nanovirus moves at normal speed.
		else {
			setX(getX() + getSpeed());
			nanoVirusMain.bgx += nanoVirusMain.bgSpeed;
		}
		
		// If the nanovirus has passed into the next tunnel section ...
		if (getX() >= 800) {
			
			// Reset the nanovirus x-coordinate for the new tunnel.
			setX(getX() - 800);
			// Reset the transition.
			setTransition("");
			// Reset the sound trigger.
			nanoVirusMain.played = false;
			// Reset the enemy spawner.
			setEnemiesSpawned(false);
		}
		// Reset the background x-coordinate if it's offscreen.
		if (nanoVirusMain.bgx >= 800)
			nanoVirusMain.bgx = nanoVirusMain.bgx - 800;
		
		// If the nanovirus is in the reaction area ...
		if (150 < getX() && getX() < 350 && !nanoVirusMain.played) {
			
			// If the heartbeat sound has not played yet, play it.
			if(!nanoVirusMain.played) {
				nanoVirusMain.played = true;
				nanoVirusMain.heartbeat.play();
			}
			
			// If the next enemies have not been spawned yet, spawn them.
			if(!getEnemiesSpawned()) {
				setCurrentEnemies(enemiesNext);
				setNextEnemies(generateEnemySet());
				setEnemiesSpawned(true);
			}
		}
		
		// If the nanovirus is transitioning up, adjust its y-coordinate
		// so that it will reach the upper tunnel by x == 600.
		if (getTransition().equals("up")) {
			
			setY(getY() + (0 - getY()) / (600 - getX()) * getSpeed());
			
		// If the nanovirus is transitioning down, adjust its y-coordinate
		// so that it will reach the lower tunnel by x == 600.
		} else if (getTransition().equals("down")) {
			
			setY(getY() - (getY() - 360) / (600 - getX()) * getSpeed());			
			
		// If the nanovirus is staying in the middle tunnel,
		// reset its y-coordinate and y-offset.
		} else {
			
			setY(180);
			setYoff(0);
		}
	}
	
	/**
	 * 		generateEnemySet: 	Generates a new set of enemies based
	 * 							on the current GameState.
	 */
	private ArrayList<Enemy> generateEnemySet() {
		
		// Set up a new ArrayList of Enemies.
		ArrayList<Enemy> newEnemySet = new ArrayList<Enemy>();
		
		// Determine the difficulty of the rows.
		Random randRoller = new Random();
		// The initial chances for row difficulty.
		int easyChance = 35;
		int mediumChance = 50;
		int hardChance = 15;
		// Go through each row and determine the enemies in that row.
		for(int y = 0; y < 3; y++) {
			
			int roll = randRoller.nextInt(easyChance + mediumChance + hardChance);
			// Generate an easy set of enemies for this row.
			if(roll < easyChance) {
				
				// An easy row randomly picks from a pool of:
				// 3 red blood cell, 4 nothing, 1 white blood cell
				generateEnemyRow(3, 4, 1, y, newEnemySet);
				
				// Increase the chances of the other difficulty types.
				mediumChance += easyChance;
				hardChance += easyChance;
				
			// Generate a medium set of enemies for this row.
			} else if(roll < (easyChance + mediumChance)) {
				
				// A medium row randomly picks from a pool of:
				// 2 red blood cell, 3 nothing, 2 white blood cell
				generateEnemyRow(2, 3, 2, y, newEnemySet);
				
				// Increase the chances of the other difficulty types.
				easyChance += mediumChance;
				hardChance += mediumChance;
			
			// Generate a hard set of enemies for this row.
			} else {
				
				// A hard row randomly picks from a pool of:
				// 1 red blood cell, 2 nothing, 3 white blood cell
				generateEnemyRow(1, 2, 3, y, newEnemySet);
				
				// Increase the chances of the other difficulty types.
				easyChance += hardChance;
				mediumChance += hardChance;
			}
		}
		
		// Return the generated set of enemies.
		return newEnemySet;
	}
	
	/**
	 * 		@param red, @param nothing, @param white, @param y, @param enemySet
	 * 
	 * 		generateEnemyRow: 	Generates a row of enemies based on a given
	 * 							pool of elements to pick from and adds these 
	 * 							enemies to the given ArrayList.
	 */
	private void generateEnemyRow(int red, int nothing, int white, int y, ArrayList<Enemy> enemySet) {
		
		// Randomly pick 3 elements for the row from the given pool.
		Random randRoller = new Random();
		for(int x = 0; x < 3; x++) {
			
			int roll = randRoller.nextInt(red + nothing + white);
			// Add a red blood cell to the row and remove one from the pool.
			if(roll < red) {
				enemySet.add(new Enemy(0 , x, y));
				red -= 1; if(red < 0){ red = 0; }
			// Add nothing to the row and remove one from the pool.
			} else if(roll < (red + nothing)) {
				nothing -= 1; if(nothing < 0){ nothing = 0; }
			// Add a white blood cell to the row and remove one from the pool.
			} else {
				enemySet.add(new Enemy(1 , x, y));
				white -= 1; if(white < 0){ white = 0; }
			}
		}
	}
}