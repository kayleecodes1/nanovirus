package main;

public class GameState {
	private boolean	paused;
	private int		x;
	private String	transition;
	private int		y;
	private int		speed;
	private int		yoff;
	public GameState(boolean paused, int x, String transition, int y,
			int speed, int yoff) {
		this.paused = paused;
		this.x = x;
		this.transition = transition;
		this.y = y;
		this.speed = speed;
		this.yoff = yoff;
	}
	public int getSpeed() {
		return speed;
	}
	public String getTransition() {
		return transition;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getYoff() {
		return yoff;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public void setTransition(String transition) {
		this.transition = transition;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setYoff(int yoff) {
		this.yoff = yoff;
	}
	/**
	 * @param nanoVirusMain
	 *            TODO
	 * 
	 */
	void updateBG(NanoVirusMain nanoVirusMain) {
		if (getX() < 150)
			setX((int) (getX() + (150 + 5 - getX()) / 100.0 * getSpeed()));
		else {
			setX(getX() + getSpeed());
			nanoVirusMain.bgx += nanoVirusMain.bgSpeed;
		}
		if (getX() >= 800)
			setX(getX() - 800);
		if (nanoVirusMain.bgx >= 800)
			nanoVirusMain.bgx = nanoVirusMain.bgx - 800;

		if (150 < getX() && getX() < 350 && !nanoVirusMain.played) {
			nanoVirusMain.played = true;
			nanoVirusMain.heartbeat.play();

		}
		if (150 < getX() && getX() < 600) {
		} else {
			setTransition("");
			nanoVirusMain.played = false;
		}
		// if (x > 350)
		// paused = !paused;
		if (getTransition().equals("up"))
			setY(getY() + (0 - getY()) / (600 - getX()) * getSpeed());
		// yoff = 180;
		else if (getTransition().equals("down"))
			setY(getY() - (getY() - 360) / (600 - getX()) * getSpeed());
		// yoff = -180;
		else {
			// if (y > 180)
			// y--;
			// else if (y < 180)
			// y++;
			//
			// if (yoff > 0)
			// yoff--;
			// else if (yoff < 0)
			// yoff++;

			setY(180);
			setYoff(0);

		}
	}
}