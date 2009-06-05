/*
 * Snake, the game.
 * Copyright (c) Michal Nazarewicz (mina86/AT/mina86.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mina86.snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.LinkedList;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;



/**
 * Main application class.  The Main class contains the static main()
 * methd used by JVM to execute the aplication as well as represents
 * the main window of the application.  It's constructor constructs
 * the window as well as sets window visible and starts the game.
 *
 * <p>In this version of game, snake can move through the edge of the
 * map (in which case it apperas at the other edge).  It is also
 * possble to put walls an map where snake cannot move (if it does it
 * dies).  Besides food items which make snake grow (and as a
 * consequence add points) there are bombs which appear on random
 * positions on the map.
 *
 * <p>Game supports loading maps from simple text files (which can be
 * optionally compressed using GZip) as well as skin files from images
 * supported by the virtual machine game is running.  User can also
 * customize difficulty level that is number of food items, bombs etc.
 *
 * <img style="float: right; margin: 0.75em" alt="Diagram showing how engine is designed" src="doc-files/Diagram.png">
 *
 * <p>Engine is designed in the following way: {@link Main} class
 * creates the main window and is responsible for interacting with
 * user.  It uses {@link MapLoader} class to load maps, {@link
 * SkinLoader} class to load skins, {@link PlainSkinDialog} to
 * customize plain skin and {@link DifficultyDialog} class to let user
 * customize difficulty.  It creates {@link GameController} instance
 * which then creates {@link Map} and {@link Snake} which moves on the
 * Map.  The Map object creates {@link GFXBuffer} object which is then
 * used by Main class to paint component.
 *
 * <p>When user loads new map, changes difficulty level or skin, Main
 * comunicates with GameController which then passes request to Map
 * and (if needed) reinitializes whole map creating new Snake.  Also
 * when game is paused/resumed or window is iconified/deiconified Main
 * passes request to ameControler to pause/resume game or pause/resume
 * animations.
 *
 * <p>GameController creates two threads: One for moving the snake and
 * one for animations.  Each time map needs to be redrawn it sends
 * Main object an <tt>GAME UPDATED</tt> action so that Main knows that
 * it needs to repain it's {@link Map.Component}.  On each move,
 * GameController calls {@link Snake#step(byte) Snake's step() method}
 * which behaves acordingly to the state of the map.  If snake steps
 * on a wall, bomb or itself method throws {@link SnakeDied} exception
 * so that GameController knows game has ended.  Of course, by moving
 * on the Map, Snake affect Map state.  Each time, Map is changed Map
 * object mdifies the GFXBuffer do that it always contain valid data.
 *
 * @version 0.1
 * @author Michal "<a href="http://mina86.com/">mina86</a>" Nazarewicz (mina86/AT/mina86.com)
 */
public final class Main extends JFrame
	implements WindowListener, ActionListener, KeyListener {


	/**
	 * Main program function.
	 *
	 * @param args program arguments
	 */
	public static void main(String args[]) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
				public void run() { new Main(); }
			});
	}



	/** The component on which map is painted. */
	private Map.Component component;
	/** Game controler. */
	private GameController game;
	/** Label at the bottom of the window with score etc. */
	private JLabel scoreLabel;


	/**
	 * Updates information on the scoreLabel.  scoreLabel contains the
	 * score, move number and snake's speed.  Moreover, if game is
	 * paused it displays a apropriate message.
	 */
	protected void updateScoreLabel() {
		if (game.isPaused()) {
			scoreLabel.setText("Game paused, press P to resume");
			return;
		}
		int points = game.getPoints();
		int move = game.getMove();
		double speed = game.getSpeed();
		String text = String.format("Score: %4d    Move: %4d    Speed: %4.3g",
		                            points, move, speed);
		scoreLabel.setText(text);
	}


	/** The "Paused" check box menu item. */
	private JCheckBoxMenuItem miPause;
	/** Radio menu items representing configuration presets. */
	private JRadioButtonMenuItem miPresets[];
	/** Current preset or -1 if no preset. */
	private int preset = GameConfiguration.DEFAULT_CONFIG;
	/** Map type. <tt>"plain"</tt> from plain map, <tt>"wall"</tt>
	 * for wall, <tt>null</tt> for custom. */
	private String chosenMap = "plain";



	/**
	 * Constructor which creates frame, sets it visible and starts
	 * auto game.
	 */
	private Main() {
		super("Snake");

		/* Create game */
		Skin skin = new PlainSkin();
		GameConfiguration config = new GameConfiguration();
		game = new GameController(config, skin, MapLoader.plain((short)30));
		game.addActionListener(this);
		component = new Map.Component(game.getBuffer());

		/* Create panel */
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component, BorderLayout.CENTER);
		scoreLabel = new JLabel("Score: 0   Move: 0   Speed: 0");
		panel.add(scoreLabel, BorderLayout.SOUTH);
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);

		/* Create menu bar */
		JMenuBar menubar;
		JMenu menu, submenu;
		JMenuItem mi;
		JRadioButtonMenuItem rmi;
		ButtonGroup group;

		menubar = new JMenuBar();
		setJMenuBar(menubar);

		/* Game Menu */
		menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_G);
		menubar.add(menu);

		/* New Game */
		mi = new JMenuItem("New Game");
		mi.setActionCommand("NEW GAME");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_N);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
		                                         KeyEvent.CTRL_MASK));
		menu.add(mi);

		/* High Scores */
		mi = new JMenuItem("High Scores");
		mi.setActionCommand("HIGH SCORES");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_H);
		menu.add(mi);

		/* Pause Game */
		miPause = new JCheckBoxMenuItem("Pause Game");
		miPause.setActionCommand("PAUSE");
		miPause.addActionListener(this);
		miPause.setMnemonic(KeyEvent.VK_P);
		miPause.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
		miPause.setSelected(false);
		menu.add(miPause);

		/* Map Submenu */
		menu.addSeparator();
		submenu = new JMenu("Map");
		submenu.setMnemonic(KeyEvent.VK_M);
		menu.add(submenu);

		mi = new JMenuItem("Plain");
		mi.setActionCommand("MAP PLAIN");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_P);
		submenu.add(mi);

		mi = new JMenuItem("Wall");
		mi.setActionCommand("MAP WALL");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_W);
		submenu.add(mi);

		mi = new JMenuItem("Load map");
		mi.setActionCommand("MAP CUSTOM");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_C);
		submenu.add(mi);

		/* Dificulty Subenu */
		submenu = new JMenu("Dificulty");
		submenu.setMnemonic(KeyEvent.VK_D);
		menu.add(submenu);
		group = new ButtonGroup();

		miPresets =
			new JRadioButtonMenuItem[GameConfiguration.configNames.length];
		for (int i = 0; i < miPresets.length; ++i) {
			String cfg = GameConfiguration.configNames[i];
			miPresets[i] = new JRadioButtonMenuItem(cfg);
			miPresets[i].setActionCommand("DIF " + i);
			miPresets[i].addActionListener(this);
			miPresets[i].setMnemonic(cfg.charAt(0));
			miPresets[i].setSelected(i == GameConfiguration.DEFAULT_CONFIG);
			group.add(miPresets[i]);
			submenu.add(miPresets[i]);
		}

		submenu.addSeparator();
		rmi = new JRadioButtonMenuItem("Custom");
		rmi.setActionCommand("DIF CUSTOM");
		rmi.addActionListener(this);
		rmi.setMnemonic(KeyEvent.VK_C);
		group.add(rmi);
		submenu.add(rmi);

		/* Quit Game */
		menu.addSeparator();
		mi = new JMenuItem("Quit Game");
		mi.setActionCommand("QUIT");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_Q);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
		                                         KeyEvent.CTRL_MASK));
		menu.add(mi);

		/* Apperence Menu */
		menu = new JMenu("Apperence");
		menu.setMnemonic(KeyEvent.VK_A);
		menubar.add(menu);

		/* Skin Submenu */
		submenu = new JMenu("Skin");
		submenu.setMnemonic(KeyEvent.VK_S);
		menu.add(submenu);

		mi = new JMenuItem("Plain skin");
		mi.setActionCommand("PLAIN SKIN");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_P);
		submenu.add(mi);

		mi = new JMenuItem("Load skin");
		mi.setActionCommand("SELECT SKIN");
		mi.addActionListener(this);
		mi.setMnemonic(KeyEvent.VK_L);
		submenu.add(mi);

		/* Look and Feel Sub Menu */
		Comparator<String> comparator = new Comparator<String>() {
			public boolean equals(Object e) { return this == e; }
			public int compare(String s1, String s2) {
				String n1 = s1.substring(s1.lastIndexOf('.') + 1);
				String n2 = s2.substring(s2.lastIndexOf('.') + 1);
				int res = n1.compareTo(n2);
				return res == 0 ? s1.compareTo(s2) : res;
			}
		};
		TreeSet<String> set = new TreeSet<String>(comparator);
		UIManager.LookAndFeelInfo LAFarr[];
		LAFarr = UIManager.getInstalledLookAndFeels();
		if (LAFarr!=null) {
			for (UIManager.LookAndFeelInfo laf : LAFarr) {
				set.add(laf.getClassName());
			}
		}

		/* Add Look and Feel menu items */
		String currentLAF = UIManager.getLookAndFeel().getClass().getName();
		if (set.size()>1) {
			submenu = new JMenu("Look and Feel");
			submenu.setMnemonic(KeyEvent.VK_L);
			menu.add(submenu);
			group = new ButtonGroup();

			int chrSet = 0;
			for (String laf : set) {
				String name = laf.substring(laf.lastIndexOf('.')+1);
				name = name.substring(0, name.length()-11);
				rmi = new JRadioButtonMenuItem(name);
				name = name.toUpperCase();
				for (int i = 0; i<name.length(); ++i) {
					char ch = name.charAt(i);
					if (!Character.isUpperCase(ch)) continue;
					if ((chrSet & (1<<(ch - 'A')))!=0) continue;
					rmi.setMnemonic(ch);
					chrSet |= 1 << (ch - 'A');
					break;
				}
				group.add(rmi);
				submenu.add(rmi);
				rmi.setActionCommand("LAF " + laf);
				rmi.addActionListener(this);
				rmi.setSelected(currentLAF.equals(laf));
			}
		}


		/* Open window */
		setContentPane(panel);
		addKeyListener(this);
		addWindowListener(this);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		/* Run */
		game.startGame(true);
	}



	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();


		if (cmd.startsWith("LAF ")) {
			String laf = cmd.substring(4);
			try { synchronized (game) {
					boolean pause = game.isRunning();
					if (pause) game.pauseGame();
					UIManager.setLookAndFeel(laf);
					SwingUtilities.updateComponentTreeUI(this);
					pack();
					if (pause) game.resumeGame();
				} }
			catch (Exception exc) { exc.printStackTrace(); }

		//} else if (cmd.equals("GAME_STARTING")) {

		} else if (cmd.equals("GAME PAUSED")) {
			if (!deactivated_paused) {
				/* if deactivated_paused is true then scrollLabel has
				 * already a eaningful text (ie. info to activate
				 * window to resume) */
				updateScoreLabel();
			}
			scoreLabel.repaint();

		} else if (cmd.equals("GAME RESUMING")) {
			updateScoreLabel();
			scoreLabel.repaint();

		} else if (cmd.equals("GAME FINISHED")) {
			gameFinished();

		} else if (cmd.equals("GAME UPDATED")) {
			if (!deactivated_paused) updateScoreLabel();
			repaint();


		} else if (cmd.equals("NEW GAME")) {
			game.restartGame(false);

		} else if (cmd.equals("HIGH SCORES")) {
			HighScoreDialog.display(this);

		} else if (cmd.equals("PAUSE")) {
			deactivated_paused = false;
			game.setPaused(miPause.isSelected());

		} else if (cmd.equals("QUIT")) {
			game.stopGame();
			dispose();


		} else if (cmd.equals("PLAIN SKIN")) {
			PlainSkinDialog dialog = new PlainSkinDialog(this);
			if (game.getSkin() instanceof PlainSkin) {
				dialog.set(((PlainSkin)game.getSkin()).getColors());
			}
			dialog.setVisible(true);
			Skin s = dialog.getSkin();
			dialog.dispose();
			if (s==null) return;

			game.getMap().changeSkin(s, game.getSnake());
			component.setBuffer(game.getMap().getBuffer());
			pack();
			repaint();

		} else if (cmd.equals("SELECT SKIN")) {
			Skin s = SkinLoader.showDialog(this);
			if (s==null) return;

			game.getMap().changeSkin(s, game.getSnake());
			component.setBuffer(game.getMap().getBuffer());
			pack();
			repaint();


		} else if (cmd.equals("MAP PLAIN") || cmd.equals("MAP WALL")) {
			String val = JOptionPane.showInputDialog(this, "Enter size", "30");
			if (val==null) return;
			val = val.trim();
			if (val.length()==0) return;
			short size = 0;
			try { size = Short.decode(val); }
			catch (NumberFormatException exc) { return; }
			if (size<5) return;

			boolean walls[][] = cmd.charAt(4)=='P'
				? MapLoader.plain(size)
				: MapLoader.walls(size);

			chosenMap = cmd.charAt(4)=='P' ? "plain" : "wall";
			game.resetGame(walls);
			component.setBuffer(game.getMap().getBuffer());
			pack();
			repaint();

		} else if (cmd.equals("MAP CUSTOM")) {
			boolean walls[][] = MapLoader.showDialog(this);
			if (walls==null) return;

			chosenMap = null;
			game.resetGame(walls);
			component.setBuffer(game.getMap().getBuffer());
			pack();
			repaint();


		} else if (cmd.equals("DIF CUSTOM")) {
			DifficultyDialog dg = preset == -1
				? new DifficultyDialog(this, game.getConfig())
				: new DifficultyDialog(this, preset);
			dg.setVisible(true);
			dg.dispose();
			if (dg.isCancled()) {
				if (preset!=-1) miPresets[preset].setSelected(true);
				return;
			}
			if ((preset = dg.getPreset())!=-1) {
				miPresets[preset].setSelected(true);
			}
			game.resetGame(new GameConfiguration(dg.getValues()));
			repaint();

		} else if (cmd.startsWith("DIF ")) {
			preset = Integer.parseInt(cmd.substring(4));
			game.resetGame(new GameConfiguration(preset));
			repaint();

		}
	}


	/** Holds loaded high score. */
	private ConfigFile highScores = null;

	/**
	 * Loads high score or returns one already loaded.
	 *
	 * @return high score file.
	 */
	private ConfigFile getHighScores() {
		if (highScores!=null) {
			return highScores;
		}

		String path = System.getProperty("user.home");
		if (path==null) {
			path = System.getProperty("user.dir", "");
		}
		File file = new File(path, ".snake.hs");

		try {
			highScores = new ConfigFile(file);
		}
		catch (IOException exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(this, "Could not load High Scores",
			                              null, JOptionPane.WARNING_MESSAGE);
			highScores = new ConfigFile();
			highScores.setFile(file);
		}

		return highScores;
	}

	/**
	 * Saves high scores.
	 */
	private void saveHighScores() {
		if (highScores==null) {
			return;
		}
		try {
			highScores.save(true);
		}
		catch (IOException exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(this, "Could not save High Scores",
			                              null, JOptionPane.WARNING_MESSAGE);
		}
	}


	/**
	 * Class representing single entry in high score.  Ie. it holds
	 * user name and number of points.
	 */
	public static final class HSEntry {
		/** Number of points user got. */
		public int points;
		/** User's name. */
		public String name;

		/**
		 * Creates new entry with given values.
		 *
		 * @param p number of points.
		 * @param n user name.
		 */
		public HSEntry(int p, String n) {
			points = p;
			name = n;
		}


		public String toString() {
			return points + ", " + name;
		}
	}


	/**
	 * Returns list of scores for given map and difficulty preset.
	 *
	 * @param map map's name (<tt>"plain"</tt> or <tt>"wall"</tt>).
	 * @param level difficulty level.
	 * @return list of scores for given map.
	 */
	public LinkedList<HSEntry> getHighScore(String map, int level) {
		LinkedList<HSEntry> list = new LinkedList<HSEntry>();
		ConfigFile hs = getHighScores();
		String cfg = map + "." + level + ".";
		try {
			for (int i = 0; i < 5; ++i) {
				String str[] = hs.getString(cfg + i).split(",", 2);
				list.add(new HSEntry(Integer.parseInt(str[0]), str[1].trim()));
			}
		}
		catch (Exception exc) { }

		return list;
	}

	/**
	 * Sets high scores for given map and difficulty.
	 *
	 * @param map map's name (<tt>"plain"</tt> or <tt>"wall"</tt>).
	 * @param level difficulty level.
	 * @param scores list of scores for given map.
	 */
	private void setHighScore(String map, int level,
	                          LinkedList<HSEntry> scores) {
		ConfigFile hs = getHighScores();
		String cfg = map + "." + level + ".";
		int i = 0;
		for (HSEntry entry : scores) {
			hs.put(cfg + i, entry.toString());
			if (++i == 5) break;
		}
	}



	/**
	 * Method run when {@link GameController} sends <tt>GAME
	 * FINISHED</tt> action.
	 */
	private void gameFinished() {
		int points = game.getPoints();

		/* There's no high score for custom levels */
		if (preset == -1 || chosenMap == null) {
			double pc = points * 100 / game.getMap().getWidth() /
				game.getMap().getHeight();
			String msg[] = new String[2];
			msg[0] = "Your score is " + game.getPoints() + ". ";
			if      (pc < 1) msg[1] = "Are you joking?.";
			else if (pc < 4) msg[1] = "You could do better then that.";
			else if (pc < 8) msg[1] = "A bit more practise and you could call it something.";
			else if (pc <12) msg[1] = "Not so bad.";
			else if (pc <16) msg[1] = "That's more like it!";
			else if (pc <20) msg[1] = "Pretty good!";
			else             msg[1] = "You're snake's master. [:";
			JOptionPane.showMessageDialog(this, msg, "Game Over",
			                              JOptionPane.INFORMATION_MESSAGE);
			game.restartGame(true);
			return;
		}

		/* Get user's position at high score */
		LinkedList<HSEntry> hs = getHighScore(chosenMap, preset);
		int pos = 0;
		for (HSEntry entry : hs) {
			if (entry.points < points) break;
			++pos;
		}

		/* User did not enter High Score */
		if (pos>=5) {
			String msg[] = new String[2];
			msg[0] = "Your score is " + game.getPoints() + ".";
			msg[1] = "That's not enough to enter High Score.";
			JOptionPane.showMessageDialog(this, msg, "Game Over",
			                              JOptionPane.INFORMATION_MESSAGE);
			game.restartGame(true);
			HighScoreDialog.display(this, chosenMap, preset);
			return;
		}

		/* User entered high score */
		String msg[] = new String[3];
		msg[0] = "Your score is " + game.getPoints() + ".";
		msg[1] = "You have entered High Score!";
		msg[2] = "Please enter your name:";

		String name = JOptionPane.showInputDialog(this, msg);
		if (name==null || name.trim().length()==0) {
			game.restartGame(true);
			return;
		}

		/* Save high scores */
		hs.add(pos, new HSEntry(points, name));
		setHighScore(chosenMap, preset, hs);
		saveHighScores();
		game.restartGame(true);
		HighScoreDialog.display(this, chosenMap, preset);
	}



	/** Whether game was paused because window was deactivated. */
	private boolean deactivated_paused = false;

	/**
	 * Pauses game if it's running and it's not an auto game.
	 *
	 * @param e WindowEvent object.
	 * @see #windowActivated(WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
		if (game.isAutoGame()) return;
		synchronized (game) {
			if (!game.isRunning()) return;
			deactivated_paused = true;
			scoreLabel.setText("Game paused, activate window to resume");
			game.pauseGame();
		}
	}

	/**
	 * Resumes game if it was previously paused by windowDeactivated().
	 *
	 * @param e WindowEvent object.
	 * @see #windowDeactivated(WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
		if (game.isAutoGame()) return;
		synchronized (game) {
			if (deactivated_paused && game.isPaused()) {
				game.resumeGame();
			}
			deactivated_paused = false;
		}
	}

	public void windowClosed(WindowEvent e) { }

	/**
	 * Firs <tt>QUIT</tt> action event.  This way real work is done by
	 * the actionPerformed() method, so the code is not doubled.
	 *
	 * @param e WindowEvent object.
	 */
	public void windowClosing(WindowEvent e) {
		actionPerformed(new ActionEvent(this, 0, "QUIT"));
	}

	/**
	 * Resumes animations and game if it was paused by windowIconified().
	 *
	 * @param e WindowEvent object.
	 * @see #windowIconified(WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
		game.animations().on();
		if (!game.isAutoGame()) return;
		synchronized (game) {
			if (!game.isRunning()) return;
			deactivated_paused = true;
			game.pauseGame();
		}
	}

	/**
	 * Pauses animations and game if it's running.
	 *
	 * @param e WindowEvent object.
	 * @see #windowDeiconified(WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
		game.animations().off();
		if (!game.isAutoGame()) return;
		synchronized (game) {
			if (deactivated_paused && game.isPaused()) {
				game.resumeGame();
			}
			deactivated_paused = false;
		}
	}

	public void windowOpened(WindowEvent e) { }


	public void keyTyped(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }

	/**
	 * Reacts on the arrow keys and sets snake's direction
	 * approrietly.
	 *
	 * @param e KeyEvent object.
	 * @see GameController#setDirection(byte)
	 */
	public void keyPressed(KeyEvent e) {
		byte dir;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_KP_LEFT:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
		case KeyEvent.VK_H:
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_4:
			dir = Direction.LEFT;
			break;

		case KeyEvent.VK_KP_RIGHT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
		case KeyEvent.VK_K:
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_6:
			dir = Direction.RIGHT;
			break;

		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
		case KeyEvent.VK_U:
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_8:
			dir = Direction.UP;
			break;

		case KeyEvent.VK_KP_DOWN:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
		case KeyEvent.VK_J:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_2:
			dir = Direction.DOWN;
			break;

		default:
			return;
		}

		game.setDirection(dir);
	}


	/**
	 * Creates file filter which accepts directories and files with
	 * given extension or extensio with ".gz" suffix.
	 *
	 * @param description file filter description.
	 * @param ext file extension (without dot) to accept.
	 * @return FileFilter object
	 */
	public static FileFilter createFileFilter(final String description,
	                                          final String ext) {
		return new FileFilter() {
			public String getDescription() { return description; }
			public boolean accept(File f) {
				return f.isDirectory() ||
					(f.isFile() &&
					 (f.getName().endsWith("." + ext) ||
					  f.getName().endsWith("." + ext + ".gz")));
				}
			};
	}

	/**
	 * Creates plain file chooser with directory set to current
	 * working directory.  The directory used is taken from the
	 * <tt>System.getProperty("user.dir")</tt>.
	 *
	 * @return JFileChooser object.
	 */
	public static JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser();
		try {
			File dir = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(dir);
		}
		catch (Exception exc) { }
		return chooser;
	}

	/**
	 * Creates file chooser with given file filter added and directory
	 * set to current working directory.  The directory used is taken
	 * from the <tt>System.getProperty("user.dir")</tt>.
	 *
	 * @param description file filter description.
	 * @param ext file extension (without dot) to accept.
	 * @return JFileChooser object.
	 */
	public static JFileChooser createFileChooser(final String description,
	                                             final String ext) {
		JFileChooser chooser = createFileChooser();
		chooser.addChoosableFileFilter(createFileFilter(description, ext));
		return chooser;
	}



}
