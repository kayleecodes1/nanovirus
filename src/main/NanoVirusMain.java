package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

import javax.imageio.*;
import javax.swing.*;

public class NanoVirusMain extends JPanel {

	private static NanoVirusMain	main;

	private static JProgressBar		jprog;

	Random							rand	= new Random();

	/** current total running time in frames */
	float							f		= 0;

	/** current total running time in milliseconds */
	int								dur		= 0;

	/** the last time we updated the world and/or painted the display */
	long							lastime	= Calendar.getInstance().getTimeInMillis();

	/** the global game state */
	private final static GameState	state	= new GameState(false, 0, "", 180, 30, 0);

	/** the speed at which the background scrolls, in pixels per frame */
	float							bgSpeed	= state.getSpeed() / 8.0f;

	/** the distance in pixels the background is translated to the left */
	int								bgx		= 0;

	/** the heartbeat sound effect clip player */
	final Sound						heartbeat;

	/** the image used as the background */
	private BufferedImage			bg;

	/** the backing buffer used to double buffer the display
	 * all drawing should happen here, and then copy it to the canvas */
	private final VolatileImage		offscreen;

	/** a graphics object to draw to the backing buffer */
	private final Graphics2D		bufferGraphics;

	/** the image used for the blood vessels */
	private BufferedImage			arteries;

	/** flag that indicates whether the hearbeat sound clip has just played */
	boolean							played;

	private BufferedImage			red;

	private static BufferedImage	virusImage;

	private BufferedImage			white;

	private static BufferedImage	nanoVirusDown;

	private BufferedImage			nanoVirusSide;

	private static BufferedImage	nanoVirus;

	private NanoVirusMain() {
		super();

		try {
			//load the background and blood vessel images
			bg = ImageIO.read(getClass().getResourceAsStream("background.jpg"));
			arteries = ImageIO.read(getClass().getResourceAsStream("arteriesTile_TEST.png"));
			red = ImageIO.read(getClass().getResourceAsStream("redBloodCell.png"));
			white = ImageIO.read(getClass().getResourceAsStream("whiteBloodCell.png"));
			nanoVirus = ImageIO.read(getClass().getResourceAsStream("nanoVirus.png"));
			nanoVirusDown = ImageIO.read(getClass().getResourceAsStream("nanoVirusDown.png"));
			nanoVirusSide = ImageIO.read(getClass().getResourceAsStream("nanoVirusSide.png"));
			virusImage = nanoVirusSide;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//load the heatbeat sound effect
		heartbeat = new Sound("bin/main/heartbeatTest.mp3");
		// Sound soundtrack = new Sound("bin/soundtrack.mp3");

		//set up the window
		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(800, 600));

		//	pack();

		setVisible(true);

		//try to use hardware acceleration
		bg.setAccelerationPriority(.5f);
		arteries.setAccelerationPriority(1);

		// Create an offscreen image to draw on
		offscreen = bg.createGraphics().getDeviceConfiguration()
				.createCompatibleVolatileImage(800, 600, Transparency.BITMASK);
		offscreen.setAccelerationPriority(1);

		// by doing this everything that is drawn by bufferGraphics
		// will be written on the offscreen image.
		bufferGraphics = offscreen.createGraphics();

		//setup eventhandlers for the window

	}
	/** @param args */
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		main = new NanoVirusMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), SwingConstants.HORIZONTAL));
		frame.getContentPane().add(main);
		jprog = new JProgressBar(SwingConstants.VERTICAL, 0, 1000);
		frame.getContentPane().add(jprog);
		frame.setMinimumSize(new Dimension(820, 600));
		frame.pack();
		frame.setVisible(true);
		frame.addKeyListener(new KeyListener() {

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

				// if the state is within the descision window
				if (100 < state.getX() && state.getX() < 350)
					switch (e.getKeyChar()) {
					case 'w':
						//choose upper path
						state.setTransition("up");
						virusImage = nanoVirus;
						state.setYoff(180);
						// speed ++;
						// bgSpeed = speed / 2;
						jprog.setValue((int) (jprog.getValue() * 1.1 + 1));
						System.out.println("capture up");
						break;
					case 's':
						//choose lower path
						// speed--;
						// bgSpeed = speed / 2;
						virusImage = nanoVirusDown;
						state.setYoff(-180);
						state.setTransition("down");
						System.out.println("capture down");
						break;
					default:
						// stay in middle path
						break;
					}
			}
		});

		main.run();  // run the game
	}

	/** time the update/repaint and print an average frame rate every 60 frames */
	private void doTiming() {
		f++;
		long d = Calendar.getInstance().getTimeInMillis();
		dur += d - lastime;
		lastime = d;
		if (f % 60 == 0)
			System.out.println(1000 / (dur / f) + " fps");
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics) */
	@Override
	public void paint(Graphics g) {

		// update the game state
		updateState();

		//------------draw everything to the buffer
		//draw the background
		bufferGraphics.drawImage(bg, -bgx, 0, 800, 600, null);
		bufferGraphics.drawImage(bg, -bgx + 800, 0, 800, 600, null);

		//draw the blood vessels
		bufferGraphics.drawImage(arteries, -state.getX(), -state.getY(), 800, 960, null);
		bufferGraphics.drawImage(arteries, -state.getX() + 800, -state.getY() - state.getYoff(), 800, 960, null);

		for (Enemy e : state.getCurrentEnemies())
			bufferGraphics.drawImage(red, (int) (700 - state.getX() + (rand.nextFloat() - .5) * 5),
					(int) (250 - state.getY() + (rand.nextFloat() - .5) * 5) + e.getY() * 180, 85, 85, null);

		for (Enemy e : state.getNextEnemies())
			bufferGraphics.drawImage(red, (int) (700 - state.getX() + (rand.nextFloat() - .5) * 5),
					(int) (-state.getYoff() + 250 - state.getY() + (rand.nextFloat() - .5) * 5) + e.getY() * 180, 85,
					85, null);
		//		bufferGraphics.drawImage(red, (int) (700 - state.getX() + (rand.nextFloat() - .5) * 5),
		//				(int) (250 - state.getY() + (rand.nextFloat() - .5) * 5) + 0 * 180, 85, 85, null);

		//draw the player 
		bufferGraphics.drawImage(virusImage, 50, 275, 85, 85, null);

		bufferGraphics.setColor(Color.green);
		bufferGraphics.fillRect(785, 600 - 6 / 10 * jprog.getValue(), 15, 6 / 10 * jprog.getValue());
		jprog.repaint();
		//-----------draw the buffer to the canvas
		g.drawImage(offscreen, 0, 0, this);

		//do the timing calculations
		doTiming();
	}

	private void run() {

		//set up an infinite loop of updates/repaints
		//idealy every 30 milliseconds
		Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {

				if (!state.isPaused())
					repaint();
			}
		}, 0, 30);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JFrame#update(java.awt.Graphics) */
	@Override
	public void update(Graphics g) {
		paint(g);
		//just call te paint method
	}

	/** update the game state */
	private void updateState() {

		state.updateBG(this);
	}
}
