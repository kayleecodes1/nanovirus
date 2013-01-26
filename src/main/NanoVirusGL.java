package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.swing.*;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;

public class NanoVirusGL implements GLEventListener {
	float					f			= 0;
	int						dur			= 0;
	long					lastime		= Calendar.getInstance()
												.getTimeInMillis();

	private final boolean	paused		= false;
	// private final VolatileImage offscreen;
	// private final Graphics2D bufferGraphics;
	private BufferedImage	arteries;
	private int				x			= 0;
	private BufferedImage	bg;
	int						bgx			= 0;
	private String			transition	= "";
	private float			y			= 180;
	private final int		speed		= 30;
	int						bgSpeed		= speed / 2;
	private int				yoff;
	private Texture			earthTexture;

	public static void main(String[] args) {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);

		Frame frame = new JFrame("NanoVirus");
		frame.setSize(800, 600);
		frame.add(canvas);
		frame.setVisible(true);

		// by default, an AWT Frame doesn't do anything when you click
		// the close button; this bit of code will terminate the program when
		// the window is asked to close
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		canvas.addGLEventListener(new NanoVirusGL());
		FPSAnimator animator = new FPSAnimator(canvas, 30);
		animator.add(canvas);
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		update();
		render(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.getGL().setSwapInterval(1);

		try {
			InputStream stream = getClass().getResourceAsStream("bgtest2.png");
			TextureData data = TextureIO.newTextureData(
					drawable.getGLProfile(), stream, false, "png");
			earthTexture = TextureIO.newTexture(data);
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
	}

	private void render(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL.GL_TEXTURE_2D);

		// Begin drawing triangle sides

		earthTexture.enable(gl);
		earthTexture.bind(gl);

		// draw a triangle filling the window
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(0.0f, 1.0f, 1.0f); // Top vertex
		gl.glTexCoord2f(-1.0f, -1.0f);
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom left vertex
		gl.glTexCoord2f(1.0f, -1.0f);
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom right vertex

		gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	private void update() {
		if (150 < x && x < 400) {
			x += speed;
			bgx += bgSpeed;
		} else {
			transition = "";
			x += speed;
			y = 180;
			yoff = 0;
			bgx += bgSpeed;
		}
		// if (x > 400)
		// paused = !paused;
		if (transition.equals("up")) {
			float dy = (0 - y) / (400 - x);

			y += dy * speed * 2;
			yoff = 180;
		} else if (transition.equals("down")) {
			float dy = (y - 360) / (400 - x);

			y -= dy * speed * 2;
			yoff = -180;
		}
		if (x >= 800)
			x = x - 800;
		if (bgx >= 800)
			bgx = bgx - 800;
	}
}