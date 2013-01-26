package main;

public class Enemy {
	
	private int	type;	// 0 - Red Blood Cell, 1 - White Blood Cell
	private int	x;		// The x-coordinate of this enemy.
	private int y;		// The y-coordinate of this enemy.
	
	// The main Enemy constructor.
	public Enemy(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}