package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

import javax.imageio.*;
import javax.swing.*;

public class NanoVirusMain extends JFrame {
	float						f		= 0;
	int							dur		= 0;
	long						lastime	= Calendar.getInstance()
												.getTimeInMillis();

	private final GameState		state	= new GameState(false, 0, "", 180, 30,
												0);
	int							bgSpeed	= state.getSpeed() / 2;
	int							bgx		= 0;
	final Sound					heartbeat;
	private BufferedImage		bg;
	private final VolatileImage	offscreen;
	private final Graphics2D	bufferGraphics;
	private BufferedImage		arteries;
	boolean						played;

	private NanoVirusMain() {
		super();

		try {
			bg = ImageIO.read(getClass().getResourceAsStream("bgtest.jpg"));
			arteries = ImageIO.read(getClass().getResourceAsStream(
					"arteriesTile_TEST.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		heartbeat = new Sound("bin/main/heartbeatTest.mp3");
		// Sound soundtrack = new Sound("bin/soundtrack.mp3");

		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(800, 600));
		validate();
		setVisible(true);
		bg.setAccelerationPriority(.5f);
		arteries.setAccelerationPriority(1);
		// Create an offscreen image to draw on
		offscreen = bg.createGraphics().getDeviceConfiguration()
				.createCompatibleVolatileImage(800, 600, Transparency.BITMASK);
		offscreen.setAccelerationPriority(1);
		// by doing this everything that is drawn by bufferGraphics
		// will be written on the offscreen image.
		bufferGraphics = offscreen.createGraphics();
		// bufferGraphics.clearRect(0, 0, 800, 600);
		bufferGraphics.setColor(Color.BLUE);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'p')
					state.setPaused(!state.isPaused());

				if (100 < state.getX() && state.getX() < 350)
					switch (e.getKeyChar()) {
						case 'w' :
							state.setTransition("up");
							state.setYoff(180);
							// speed ++;
							// bgSpeed = speed / 2;
							System.out.println("capture up");
							break;
						case 's' :
							// speed--;
							// bgSpeed = speed / 2;
							state.setYoff(-180);
							state.setTransition("down");
							System.out.println("capture down");
							break;
						default :
							break;
					}

			}
		});
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new NanoVirusMain().run();
	}

	/**
	 * 
	 */
	private void doTiming() {
		f++;
		long d = Calendar.getInstance().getTimeInMillis();
		dur += d - lastime;
		lastime = d;

		if (f % 60 == 0)
			System.out.println(dur / f);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		state.updateBG(this);
		bufferGraphics.drawImage(bg, -bgx, 0, 800, 600, null);
		bufferGraphics.drawImage(bg, -bgx + 800, 0, 800, 600, null);

		bufferGraphics.drawImage(arteries, -state.getX(), -state.getY(), 800,
				960, null);
		bufferGraphics.drawImage(arteries, -state.getX() + 800, -state.getY()
				- state.getYoff(), 800, 960, null);

		bufferGraphics.drawOval(50, 275, 50, 50);
		g.drawImage(offscreen, 0, 0, this);

		doTiming();
	}
	private void run() {

		Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {

				if (!state.isPaused())
					repaint();
			}
		}, 0, 30);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFrame#update(java.awt.Graphics)
	 */
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * 
	 */
	private void updateBG() {
		state.updateBG(this);
	}
}
