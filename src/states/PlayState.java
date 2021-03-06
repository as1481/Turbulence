package states;

import java.awt.Font;
import java.io.*;
import java.io.InputStream;

import logicClasses.Airspace;
import logicClasses.Score;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Image;

public class PlayState extends BasicGameState {

	private Airspace airspace;
	Image cursorImg;
	public static float time;
	private Sound endOfGameSound;
	private Music gameplayMusic;
	public static TrueTypeFont font;
	private static Image controlBarImage, controlBarImage2, clockImage,
			backgroundImage, difficultyBackground, easyButton, easyHover,
			mediumButton, mediumHover, hardButton, hardHover;
	private String stringTime;
	private boolean settingDifficulty, gameEnded;
	private Score score;

	// Dancefloor variables
	private int tickCount;
	private boolean dancefloorState;

	public PlayState(int state) {

	}

	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {

		dancefloorState = true;
		tickCount = 0;

		gameEnded = false;
		settingDifficulty = true;
		time = 0;
		score = new Score();
		airspace = new Airspace();
		this.stringTime = "";

		gc.setAlwaysRender(true);
		gc.setUpdateOnlyWhenVisible(true);
		gc.setMouseCursor("res/graphics/cross.png", 12, 12);

		// Font

		try {
			InputStream inputStream = ResourceLoader
					.getResourceAsStream("res/blue_highway_font/bluehigh.ttf");
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont = awtFont.deriveFont(20f);
			font = new TrueTypeFont(awtFont, true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Music

		gameplayMusic = new Music("res/music/Aurea Carmina.ogg");
		endOfGameSound = new Sound(
				"res/music/175385__digitaldominic__scream.wav");

		// Images

		controlBarImage = new Image("res/graphics/control_bar_vertical.png");
		controlBarImage2 = new Image("res/graphics/control_bar_vertical2.png");
		clockImage = new Image("res/graphics/clock.png");
		backgroundImage = new Image("res/graphics/background.png");
		difficultyBackground = new Image("res/menu_graphics/difficulty.png");
		easyButton = new Image("res/menu_graphics/easy.png");
		easyHover = new Image("res/menu_graphics/easy_hover.png");
		mediumButton = new Image("res/menu_graphics/medium.png");
		mediumHover = new Image("res/menu_graphics/medium_hover.png");
		hardButton = new Image("res/menu_graphics/hard.png");
		hardHover = new Image("res/menu_graphics/hard_hover.png");

		// initialise the airspace object;

		// Waypoints
		airspace.newWaypoint(350, 150, "A");
		airspace.newWaypoint(400, 470, "B");
		airspace.newWaypoint(700, 60, "C");
		airspace.newWaypoint(800, 320, "D");
		airspace.newWaypoint(600, 418, "E");
		airspace.newWaypoint(500, 320, "F");
		airspace.newWaypoint(950, 188, "G");
		airspace.newWaypoint(1050, 272, "H");
		airspace.newWaypoint(900, 420, "I");
		airspace.newWaypoint(240, 250, "J");
		// EntryPoints
		airspace.newEntryPoint(150, 400);
		airspace.newEntryPoint(1200, 200);
		airspace.newEntryPoint(600, 0);
		airspace.addEntryPoint(airspace.getAirport().getTakeOffPoint());
		// Exit Points
		airspace.newExitPoint(800, 0, "1");
		airspace.newExitPoint(150, 250, "2");
		airspace.newExitPoint(1200, 350, "3");
		airspace.addExitPoint(airspace.getAirport().getLandingPoint());
		airspace.init(gc);
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {

		// Checks whether the user is still choosing the difficulty

		if (settingDifficulty) {

			int posX = Mouse.getX();
			int flippedposY = Mouse.getY();
			// Fixing posY to reflect graphics coords
			int posY = 600 - flippedposY;

			difficultyBackground.draw(0, 0);

			if (posX > 100 && posX < 275 && posY > 300 && posY < 375) {
				easyHover.draw(100, 300);
			} else {
				easyButton.draw(100, 300);
			}

			if (posX > 100 && posX < 275 && posY > 400 && posY < 475) {
				mediumHover.draw(100, 400);
			} else {
				mediumButton.draw(100, 400);
			}

			if (posX > 100 && posX < 275 && posY > 500 && posY < 575) {
				hardHover.draw(100, 500);
			} else {
				hardButton.draw(100, 500);
			}
			
		}

		else {

			g.setFont(font);

			// Drawing Side Images
			backgroundImage.draw(150, 0);

			// Alternating disco dancefloor side image
			if ((tickCount % 50) == 0) {
				if (this.dancefloorState == true) {
					this.dancefloorState = false;
				} else {
					this.dancefloorState = true;
				}

			}
			tickCount = tickCount + 1;

			if (this.dancefloorState == true) {
				controlBarImage.draw(0, 0);
			} else {
				controlBarImage2.draw(0, 0);
			}

			// Drawing Airspace and elements within it
			g.setColor(Color.black);
			airspace.render(g, gc);

			// Drawing Clock and Time
			g.setColor(Color.black);
			clockImage.draw(0, 5);
			g.drawString(this.stringTime, 25, 11);

			// Drawing Score, updating score
			g.drawString("Score:" + this.score.calculate(), 10, 28);

		}

	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {

		// Checks if the game has been retried and if it has resets the airspace

		if (gameEnded) {

			airspace.resetAirspace();
			time = 0;
			gameEnded = false;
			settingDifficulty = true;
			score = new Score();

		}

		// Checks whether the user is still choosing the difficulty

		if (settingDifficulty) {

			int posX = Mouse.getX();
			int posY = Mouse.getY();

			posY = 600 - posY;

			if ((posX > 100 && posX < 275) && (posY > 300 && posY < 375)
					&& Mouse.isButtonDown(0)) {

				airspace.setDifficultyValueOfGame(1);
				score.setDifficulty(1);
				airspace.getControls().setDifficultyValueOfGame(1);
				airspace.createAndSetSeparationRules();
				settingDifficulty = false;

			}

			if ((posX > 100 && posX < 275) && (posY > 400 && posY < 475)
					&& Mouse.isButtonDown(0)) {

				airspace.setDifficultyValueOfGame(2);
				score.setDifficulty(2);
				airspace.getControls().setDifficultyValueOfGame(2);
				airspace.createAndSetSeparationRules();
				settingDifficulty = false;

			}

			if ((posX > 100 && posX < 275) && (posY > 500 && posY < 575)
					&& Mouse.isButtonDown(0)) {

				airspace.setDifficultyValueOfGame(3);
				score.setDifficulty(3);
				airspace.getControls().setDifficultyValueOfGame(3);
				airspace.createAndSetSeparationRules();
				settingDifficulty = false;

			}
			
			Input input = gc.getInput();
			if (input.isKeyPressed(Input.KEY_ESCAPE)) {
				airspace.resetAirspace();
				gameplayMusic.stop();
				gameEnded = true;
				sbg.enterState(0);
			}

		}

		else {

			// Updating Clock and Time

			time += delta;
			score.addTime(delta);
			float decMins = time / 1000 / 60;
			int mins = (int) decMins;
			float decSecs = decMins - mins;

			int secs = Math.round(decSecs * 60);

			String stringMins = "";
			String stringSecs = "";
			if (secs == 60) {
				secs = 0;
				mins += 1;
			}
			if (mins < 10) {
				stringMins = "0" + mins;
			} else {
				stringMins = String.valueOf(mins);
			}
			if (secs < 10) {
				stringSecs = "0" + secs;
			} else {
				stringSecs = String.valueOf(secs);
			}

			this.stringTime = stringMins + ":" + stringSecs;

			// Updating Airspace

			airspace.newFlight(gc);
			airspace.update(gc);
			score.addSeparationViolated(airspace.getWarnings());
			if (airspace.getSeparationRules().getGameOverViolation() == true) {
				airspace.getSeparationRules().setGameOverViolation(false);
				airspace.resetAirspace();
				gameplayMusic.stop();
				endOfGameSound.play();
				sbg.enterState(2);
				writeScore(this.score.calculate());
				gameEnded = true;

			}

			Input input = gc.getInput();

			// Checking For Pause Screen requested in game

			if (input.isKeyPressed(Input.KEY_P)) {
				sbg.enterState(3);
			}

			if (input.isKeyPressed(Input.KEY_ESCAPE)) {
				airspace.resetAirspace();
				gameplayMusic.stop();
				gameEnded = true;
				sbg.enterState(0);
			}

			if (input.isKeyPressed(Input.KEY_C)) {
				airspace.getSeparationRules().setGameOverViolation(false);
				airspace.resetAirspace();
				gameplayMusic.stop();
				endOfGameSound.play();
				sbg.enterState(2);
				writeScore(this.score.calculate());
				gameEnded = true;
			}

			if (!gameplayMusic.playing()) {
				// Loops gameplay music based on random number created in init

				gameplayMusic.loop(1.0f, 0.5f);
			}

		}

	}

	public int getID() {
		return 1;
	}

	public void writeScore(int sc){
		try{
			File outputFile = new File("Score.txt");
			FileOutputStream fos= new FileOutputStream(outputFile);
			fos.write(sc);
			fos.close();
		}
		catch(FileNotFoundException e){
			  System.err.println("Error: " + e);
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
	}
	
	public Airspace getAirspace() {
		return airspace;
	}

	public void setAirspace(Airspace airspace) {
		this.airspace = airspace;
	}

}