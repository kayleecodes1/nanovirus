package main;

public class Enemy {
	
	private int	type;	// 0 - Red Blood Cell, 1 - White Blood Cell
	private int	x;		// The x position of this enemy [0 - left, 1 - middle, 2 - right].
	private int y;		// The tunnel this enemy occupies [0 - top, 1 - middle, 2 - bottom]
	
	// The main Enemy constructor.
	public Enemy(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	// ** MEMBER VARIABLE ACCESS METHODS **//
	public int getType() {
		return type;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	//** MEMBER VARIABLE MODIFICATION METHODS **/
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
}