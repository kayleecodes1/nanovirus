package main;

import java.io.*;

import javazoom.jl.player.*;

public class Sound {
	public String		filepath;

	private InputStream	s1			= null;
	private Player		myplayer	= null;
	private boolean		succ		= false;

	// Constructor that takes the name of an MP3 file
	public Sound(String filepath) {
		this.filepath = filepath;
	}

	// TESTING
	public static void main(String blah[]) {
		Sound heartbeat = new Sound("bin/main/heartbeatTest.mp3");
		Sound soundtrack = new Sound("bin/main/soundtrack.mp3");
		// heartbeat.play();
		soundtrack.loop();
	}

	// Plays this sound on loop
	public void loop() {
		play();
		while (succ)
			if (myplayer.isComplete()) {
				succ = false;
				loop();
			}
	}

	// Plays this sound once
	void play() {
		try {
			s1 = new FileInputStream(filepath);
			myplayer = new Player(s1);
		} catch (Exception l) {
			System.out.println("f**kError : " + l.getMessage());
		}

		if (myplayer != null && s1 != null)

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						myplayer.play();
						succ = true;
					} catch (Exception j) {
						System.out.println("Couldn't play because: "
								+ j.getMessage());
					}
				}
			}).start();

	}

	// Stops this sound
	public void stop() {
		myplayer.close();
	}
}