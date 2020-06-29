package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import java.text.DecimalFormat;
import java.util.Random;

public class MenuScreen extends ScreenAdapter {
	private final int TICKS_PER_SECOND = 30;

	private static Game game;

	private Random random;

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private OrthographicCamera orthographicCamera;

	private static Timer timer;

	private Sprite background, title, parcel, button_x, button_settings, button_achievements, button_tutorial, button_van, button_back, button_endless, buttondown_big, buttondown_small,
	button_again, buttondown_med, button_leaderboards, tutorial_1, tutorial_2, wheel_half, wheel_full;
	private Sprite[] scoreDigits, vans;

	private BitmapFont bsh_40, cbri_16, arl_10, arb_24;

	private static Music music;
	private static Sound click;

	private static int animStage;
	private static float vanX;
	private static float parcelX, parcelY, parcelVY;
	private static int parcelsLeft;

	private static int buttonDown;

	private static Menus currentMenu;

	private static float blackScreenOpacity;
	private static GameMode targetMode;

	private static int vanSelected;

	private static int level;
	private static int exp;

	private int oldLevel;
	private int oldExp;

	// Settings
	private static float soundVolume;
	private static float musicVolume;

	private static boolean kmph;

	// Tutorial
	private static int tutorialScreen;

	// Wheel anim
	private static boolean wheelAnim;

	public MenuScreen(Game game, Menus targetMenu) {

		MenuScreen.game = game;

		random = new Random();

		animStage = 1;
		vanX = -129 * 1.5f - 500f;
		parcelX = -20;
		parcelY = 35;
		parcelVY = random.nextInt(40) + 20;

		buttonDown = -1;

		currentMenu = targetMenu;

		blackScreenOpacity = (targetMenu == Menus.GAMEOVER) ? 1f : 0f;
		targetMode = null;

		vanSelected = 0;

		level = 1;
		exp = 0;

		oldLevel = 1;
		oldExp = 0;

		soundVolume = 1f;
		musicVolume = 1f;
		kmph = false;

		tutorialScreen = 1;

		wheelAnim = false;

		// Load values from save
		read();

		// Levelling
		if(targetMenu == Menus.GAMEOVER) {
			oldLevel = level;
			oldExp = exp;

			exp += GameScreen.getScore();

			// Exp: 200 * (L - 1) ^ 1.25 + 5000
			int requiredForNextLevel = (int) (200 * Math.pow(level - 1, 1.25f) + 5000);

			do {
				if(exp >= requiredForNextLevel) {
					exp -= requiredForNextLevel;
					level++;
				}

				requiredForNextLevel = (int) (200 * Math.pow(level - 1, 1.25f) + 5000);
			} while (exp > requiredForNextLevel);

			save();
		}

		// Music & Sound
		music = Gdx.audio.newMusic(Gdx.files.internal("music/menu_music.mp3"));
		music.setVolume(musicVolume);
		music.setLooping(true);
		music.play();

		click = Gdx.audio.newSound(Gdx.files.internal("sound/click.wav"));

		// Graphics
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.orthographicCamera = new OrthographicCamera(640, 360);

		this.orthographicCamera.position.set(this.orthographicCamera.viewportWidth / 2f, this.orthographicCamera.viewportHeight / 2f, 0);
		this.orthographicCamera.update();

		// Load Fonts
		this.bsh_40 = new BitmapFont(Gdx.files.internal("fonts/bsh_40.fnt"));
		this.cbri_16 = new BitmapFont(Gdx.files.internal("fonts/cbri_16.fnt"));
		this.arl_10 = new BitmapFont(Gdx.files.internal("fonts/arl_10.fnt"));
		this.arb_24 = new BitmapFont(Gdx.files.internal("fonts/arb_24.fnt"));

		// Load menu sprites
		this.background = new Sprite(Deliverance.assetManager.get("menu_sprites/menu_background.png", Texture.class));
		this.title = new Sprite(Deliverance.assetManager.get("menu_sprites/title.png", Texture.class));
		this.parcel = new Sprite(Deliverance.assetManager.get("game_sprites/box.png", Texture.class));

		this.button_x = new Sprite(Deliverance.assetManager.get("menu_sprites/button_x.png", Texture.class));
		this.button_settings = new Sprite(Deliverance.assetManager.get("menu_sprites/button_settings.png", Texture.class));
		this.button_achievements = new Sprite(Deliverance.assetManager.get("menu_sprites/button_achievement.png", Texture.class));
		this.button_tutorial = new Sprite(Deliverance.assetManager.get("menu_sprites/button_tutorial.png", Texture.class));
		this.button_van = new Sprite(Deliverance.assetManager.get("menu_sprites/button_van.png", Texture.class));
		this.button_back = new Sprite(Deliverance.assetManager.get("menu_sprites/button_back.png", Texture.class));
		this.button_endless = new Sprite(Deliverance.assetManager.get("menu_sprites/button_play.png", Texture.class));
		this.button_again = new Sprite(Deliverance.assetManager.get("menu_sprites/button_again.png", Texture.class));
		this.button_leaderboards = new Sprite(Deliverance.assetManager.get("menu_sprites/button_leaderboards.png", Texture.class));

		this.buttondown_big = new Sprite(Deliverance.assetManager.get("menu_sprites/button_big_down.png", Texture.class));
		this.buttondown_small = new Sprite(Deliverance.assetManager.get("menu_sprites/button_small_down.png", Texture.class));
		this.buttondown_med = new Sprite(Deliverance.assetManager.get("menu_sprites/button_med_down.png", Texture.class));

		this.tutorial_1 = new Sprite(Deliverance.assetManager.get("menu_sprites/tutorial_1.png", Texture.class));
		this.tutorial_2 = new Sprite(Deliverance.assetManager.get("menu_sprites/tutorial_2.png", Texture.class));

		this.wheel_half = new Sprite(Deliverance.assetManager.get("game_sprites/wheel_half.png", Texture.class));
		this.wheel_full = new Sprite(Deliverance.assetManager.get("game_sprites/wheel_full.png", Texture.class));

		// Score digits
		this.scoreDigits = new Sprite[10];
		for(int i = 0; i < 10; i++) {
			this.scoreDigits[i] = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/score_digits.png", Texture.class), 18 * (i % 5), 22 * (i / 5), 18, 22));
		}

		// Vans
		this.vans = new Sprite[8];
		for(int i = 0; i < 8; i++) {
			this.vans[i] = new Sprite(Deliverance.assetManager.get("game_sprites/van_0" + (i + 1) + ".png", Texture.class));
		}

		// Input
		Gdx.input.setInputProcessor(new MenuInput(orthographicCamera));

		// Black screen timer
		timer = new Timer();

		timer.scheduleTask(new Timer.Task() {

			int wheelAnimDelay = 0;

			@Override
			public void run() {

				if(animStage == 1 || animStage == 3) {
					if(wheelAnimDelay++ == 2) {
						wheelAnim = !wheelAnim;
						wheelAnimDelay = 0;
					}
				}

				if(targetMode != null) {
					if(blackScreenOpacity < 1f) {
						blackScreenOpacity += 1f / (TICKS_PER_SECOND / 2f);
					} else{
						MenuScreen.stopTimer();
						music.stop();
						music.setPosition(0);
						MenuScreen.getGame().setScreen(new GameScreen(MenuScreen.getGame(), targetMode, 3, 3));
					}
				} else if(blackScreenOpacity > 0) {
					blackScreenOpacity -= 1f / (TICKS_PER_SECOND / 2f);
				}

			}
		}, 0, 1f / TICKS_PER_SECOND);

	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Animations
		switch (animStage) {
			case 1:
				vanX += (200f - vanX / 1.75f) * delta;

				if(vanX > 224) {
					animStage++;

					parcelsLeft = random.nextInt(15) + 15;
				}
				break;
			case 2:
				if(parcelX < 250) {
					parcelX += (250f * 1.9f) * delta;

					float t = parcelX / 200f;
					parcelY = 35 + (parcelVY * t - (50f * t * t) / 2f);
				} else{
					parcelsLeft--;
					parcelX = -20;
					parcelY = 35;
					parcelVY = random.nextInt(30) + 25;

					if(parcelsLeft == 0) {
						animStage++;
					}
				}
				break;
			case 3:
				vanX += Math.min(200f, (vanX - 175f) / 1.75f) * delta;

				if(vanX > 700) {
					vanX = -129 * 1.5f;
					animStage = 1;
				}
				break;
		}

		// Level bar animation
		if(oldLevel != level || oldExp != exp) {
			int requiredForNextLevel = (int) (200 * Math.pow(oldLevel - 1, 1.25f) + 5000);

			if (oldLevel == level) {
				float rateOfChange = Math.min(((exp - oldExp) + requiredForNextLevel / 40f), requiredForNextLevel / 3f);
				oldExp += rateOfChange * delta;

				if(oldExp > exp) {
					oldExp = exp;
				}
			} else {
				oldExp += requiredForNextLevel / 3f * delta;

				if(oldExp > requiredForNextLevel) {
					oldExp = 0;
					oldLevel++;
				}
			}
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);

		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);

		spriteBatch.begin();

		// Draw background
		spriteBatch.draw(background, 0, 0);

		spriteBatch.draw(parcel, parcelX, parcelY, parcel.getWidth() * 1.5f, parcel.getHeight() * 1.5f);
		spriteBatch.draw(vans[vanSelected], vanX, 10, vans[vanSelected].getWidth() * 1.5f, vans[vanSelected].getHeight() * 1.5f);

		// Wheel animation
		if(wheelAnim) {
			if(vanSelected == 0 || vanSelected == 6) {
				spriteBatch.draw(wheel_half, vanX + 20 * 1.5f, 10, wheel_half.getWidth() * 1.5f, wheel_half.getHeight() * 1.5f);
				spriteBatch.draw(wheel_half, vanX + 109 * 1.5f, 10, wheel_half.getWidth() * 1.5f, wheel_half.getHeight() * 1.5f);
			} else{
				spriteBatch.draw(wheel_full, vanX + 20 * 1.5f, 10, wheel_full.getWidth() * 1.5f, wheel_full.getHeight() * 1.5f);
				spriteBatch.draw(wheel_full, vanX + 95 * 1.5f, 10, wheel_full.getWidth() * 1.5f, wheel_full.getHeight() * 1.5f);
			}
		}

		spriteBatch.end();

		// Screen tint
		if(currentMenu != Menus.MAIN) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			shapeRenderer.setColor(0, 0, 0, 0.5f);
			shapeRenderer.rect(0, 0, 640, 360);

			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		switch (currentMenu) {

			case GAMEOVER:
				// Level bar
				shapeRenderer.setColor(24 / 255f, 75 / 255f, 175 / 255f, 1f);
				shapeRenderer.rect(120, 100, 400, 30);

				Color levelL1 = new Color(40 / 255f, 120 / 255f, 200 / 255f, 1f);
				Color levelR1 = new Color(100 / 255f, 168 / 255f, 255 / 255f, 1f);
				shapeRenderer.rect(122, 102, 396, 26, levelL1, levelR1, levelR1, levelL1);

				int nextLevelRequiredExp1 = (int) (200 * Math.pow(oldLevel - 1, 1.25f) + 5000);
				shapeRenderer.setColor(40 / 255f, 40 / 255f, 40 / 255f, 1f);
				shapeRenderer.rect(122 + (oldExp / (float) (nextLevelRequiredExp1)) * 396, 102, 396 * (1 - (oldExp / (float) (nextLevelRequiredExp1))), 26);

				break;

			case CUSTOMISATION:
				shapeRenderer.setColor(24 / 255f, 75 / 255f, 175 / 255f, 1f);

				// Van boxes
				shapeRenderer.rect(20, 170, 602, 2);
				shapeRenderer.rect(20, 90, 600, 2);
				shapeRenderer.rect(20, 10, 600, 2);

				for(int i = 0; i < 5; i++) {
					shapeRenderer.rect(20 + i * 150, 10, 2, 160);
				}

				// Van selection box
				shapeRenderer.setColor(1f, 1f, 1f, 1f);
				shapeRenderer.rect(20 + (vanSelected % 4) * 150, 170 - (float) (vanSelected / 4) * 80, 152, 2);
				shapeRenderer.rect(20 + (vanSelected % 4) * 150, 90 - (float) (vanSelected / 4) * 80, 152, 2);
				shapeRenderer.rect(20 + (vanSelected % 4) * 150, 90 - (float) (vanSelected / 4) * 80, 2, 80);
				shapeRenderer.rect(170 + (vanSelected % 4) * 150, 90 - (float) (vanSelected / 4) * 80, 2, 80);

				// Level bar
				shapeRenderer.setColor(24 / 255f, 75 / 255f, 175 / 255f, 1f);
				shapeRenderer.rect(200, 270, 400, 30);

				Color levelL = new Color(40 / 255f, 120 / 255f, 200 / 255f, 1f);
				Color levelR = new Color(100 / 255f, 168 / 255f, 255 / 255f, 1f);
				shapeRenderer.rect(202, 272, 396, 26, levelL, levelR, levelR, levelL);

				int nextLevelRequiredExp = (int) (200 * Math.pow(level - 1, 1.25f) + 5000);
				shapeRenderer.setColor(40 / 255f, 40 / 255f, 40 / 255f, 1f);
				shapeRenderer.rect(202 + (exp / (float) (nextLevelRequiredExp)) * 396, 272, 396 * (1 - (exp / (float) (nextLevelRequiredExp))), 26);

				break;

			case SETTINGS:
				// Slider backgrounds
				shapeRenderer.setColor(24 / 255f, 75 / 255f, 175 / 255f, 1f);
				shapeRenderer.rect(40, 240, 400, 30);
				shapeRenderer.rect(40, 160, 400, 30);

				// Slider foregrounds
				Color soundL = new Color(0, 0.5f, 0, 1f);
				Color soundR = new Color(0.5f, 1f, 0.5f, 1f);
				Color musicL = new Color(0, 0, 0.5f, 1f);
				Color musicR = new Color(0.5f, 0.5f, 1f, 1f);
				shapeRenderer.rect(42, 242, 396, 26, soundL, soundR, soundR, soundL);
				shapeRenderer.rect(42, 162, 396, 26, musicL, musicR, musicR, musicL);

				shapeRenderer.setColor(40 / 255f, 40 / 255f, 40 / 255f, 1f);
				shapeRenderer.rect(42 + 396 * (soundVolume), 242, 396 * (1 - soundVolume), 26);
				shapeRenderer.rect(42 + 396 * (musicVolume), 162, 396 * (1 - musicVolume), 26);

				// Radio buttons
				shapeRenderer.setColor(24 / 255f, 75 / 255f, 175 / 255f, 1f);
				shapeRenderer.rect(40, 100, 30, 30);
				shapeRenderer.setColor(1f, 1f, 1f, 1f);
				shapeRenderer.rect(42, 102, 26, 26);

				if(kmph) {
					shapeRenderer.setColor(0f, 0f, 0f, 0f);
					shapeRenderer.rect(46, 106, 18, 18);
				}
				break;
		}

		shapeRenderer.end();
		spriteBatch.begin();

		switch (currentMenu) {

			case MAIN:
				// Title
				spriteBatch.draw(title, 70, 200);

				// Main buttons
				spriteBatch.draw(button_endless, 240, 110);
				//spriteBatch.draw(button_challenge, 335, 110);

				// Small buttons
				spriteBatch.draw(button_tutorial, 550, 5);
				spriteBatch.draw(button_x, 595, 5);
				spriteBatch.draw(button_settings, 5, 5);
				spriteBatch.draw(button_van, 50, 5);
				spriteBatch.draw(button_achievements, 95, 5);
				spriteBatch.draw(button_leaderboards, 140, 5);

				// Button Down
				switch (buttonDown) {
					case 0:
					//case 1:
						spriteBatch.draw(buttondown_big, 240 + 190 * buttonDown, 110);
						break;
					case 2:
					case 3:
					case 4:
					case 5:
						spriteBatch.draw(buttondown_small, 5 + 45 * (buttonDown - 2), 5);
						break;
					case 6:
					case 7:
						spriteBatch.draw(buttondown_small, 550 + 45 * (buttonDown - 6), 5);
						break;
				}

				// Tooltips
				switch (buttonDown) {
					case 0:
						drawTooltip(240, 110, "Begin game", true);
						break;
					/*case 1:
						drawTooltip(335, 110, "3 day challenge, unlimited misses", true);
						break;*/
					case 2:
						drawTooltip(5, 45, "Settings", false);
						break;
					case 3:
						drawTooltip(50, 45, "Van Customisation", false);
						break;
					case 4:
						drawTooltip(95, 45, "Achievements", false);
						break;
					case 5:
						drawTooltip(140, 45, "Leaderboards", false);
						break;
					case 6:
						drawTooltip(550, 45, "Tutorial", false);
						break;
					case 7:
						drawTooltip(595, 45, "Exit", false);
						break;
				}
				break;

			case GAMEOVER:

				// Title
				GlyphLayout gameOverGlyph = new GlyphLayout(bsh_40, "GAME OVER");
				bsh_40.draw(spriteBatch, gameOverGlyph, 320 - gameOverGlyph.width / 2f, 350);

				// Score
				String scoreString = String.valueOf(GameScreen.getScore());

				for(int i = 0; i < scoreString.length(); i++) {
					spriteBatch.draw(scoreDigits[Integer.parseInt(scoreString.substring(i, i + 1))], 320 - 9 * scoreString.length() + 18 * i, 280 - 22);
				}

				// Statistics
				DecimalFormat df = new DecimalFormat("0.00");

				GlyphLayout parcelsDelivered = new GlyphLayout(cbri_16, "Parcels Delivered: " + GameScreen.getParcelsHit());
				GlyphLayout accuracy = new GlyphLayout(cbri_16, "Accuracy: " + df.format(GameScreen.getParcelsHit() / (float) GameScreen.getParcelsThrown() * 100) + "%");
				GlyphLayout averageSpeed = new GlyphLayout(cbri_16, "Average Speed: " + ((kmph) ? df.format(GameScreen.getAverageSpeed() * 2.237 * 1.609) + "kmph" : df.format(GameScreen.getAverageSpeed() * 2.237) + "mph"));

				cbri_16.draw(spriteBatch, parcelsDelivered, 320 - parcelsDelivered.width / 2f, 220);
				cbri_16.draw(spriteBatch, accuracy, 320 - accuracy.width / 2f, 200);
				cbri_16.draw(spriteBatch, averageSpeed, 320 - averageSpeed.width / 2f, 180);

				// Level
				arl_10.draw(spriteBatch, "LEVEL", 120, 140);
				arb_24.draw(spriteBatch, oldLevel + "", 156, 151);

				// Exp: 200 * (L - 1) ^ 1.25 + 5000
				int nextLevelRequiredExp1 = (int) (200 * Math.pow(oldLevel - 1, 1.25f) + 5000);
				GlyphLayout expGlyph1 = new GlyphLayout(cbri_16, oldExp + "/" + nextLevelRequiredExp1);

				cbri_16.draw(spriteBatch, expGlyph1, 520 - expGlyph1.width, 145);

				// Buttons
				spriteBatch.draw(button_back, 5, 5);
				spriteBatch.draw(button_again, 260, 5);

				// Button Down
				switch (buttonDown) {
					case 0:
						spriteBatch.draw(buttondown_small, 5, 5);
						break;
					case 1:
						spriteBatch.draw(buttondown_med, 260, 5, 120, 60);
						break;
				}

				break;

			case SETTINGS:

				// Title
				GlyphLayout settingsGlyph = new GlyphLayout(bsh_40, "SETTINGS");
				bsh_40.draw(spriteBatch, settingsGlyph, 320 - settingsGlyph.width / 2f, 350);

				// Setting headers
				cbri_16.draw(spriteBatch, "Sound Volume", 40, 285);
				cbri_16.draw(spriteBatch, "Music Volume", 40, 205);
				cbri_16.draw(spriteBatch, "Use kmph units", 80, 120);

				// Slider percentages
				cbri_16.draw(spriteBatch, (int) (soundVolume * 100) + "%", 455, 260);
				cbri_16.draw(spriteBatch, (int) (musicVolume * 100) + "%", 455, 180);

				// Buttons
				spriteBatch.draw(button_back, 5, 315);

				// Button Down
				switch (buttonDown) {
					case 0:
						spriteBatch.draw(buttondown_small, 5, 315);
						break;
				}

				break;

			case CUSTOMISATION:

				// Title
				GlyphLayout customGlyph = new GlyphLayout(bsh_40, "VAN");
				bsh_40.draw(spriteBatch, customGlyph, 320 - customGlyph.width / 2f, 350);

				// Level
				arl_10.draw(spriteBatch, "LEVEL", 200, 310);
				arb_24.draw(spriteBatch, level + "", 236, 321);

				// Exp: 200 * (L - 1) ^ 1.25 + 5000
				int nextLevelRequiredExp = (int) (200 * Math.pow(level - 1, 1.25f) + 5000);
				GlyphLayout expGlyph = new GlyphLayout(cbri_16, exp + "/" + nextLevelRequiredExp);

				cbri_16.draw(spriteBatch, expGlyph, 600 - expGlyph.width, 315);

				// Van
				spriteBatch.draw(vans[vanSelected], 50, 200);

				// Vans
				for(int i = 0; i < vans.length; i++) {
					spriteBatch.draw(vans[i], 30 + (i % 4) * 150, 100 - (float) (i / 4) * 80);
				}

				// Van level lock text
				for (int i = 1; i < 8; i++) {
					if (level < i * 10) {
						GlyphLayout lvlReqGlyph = new GlyphLayout(cbri_16, "Lvl " + i * 10);
						cbri_16.draw(spriteBatch, lvlReqGlyph, 168 - lvlReqGlyph.width + (i % 4) * 150, 168 - (float) (i / 4) * 80);
					}
				}

				// Van stats
				// 2.2 + 1.85 * log (x / 64 + 1)	60 <= x <= 100
				float acc = 2f + Math.min(2.1f * (float) Math.log((level - 1) / 64f + 1), 1.4f);

				// Acceleration: -ln(a - 26.822c) + ln(a) / c
				float maxSpeed = (2.237f * acc) / GameThreads.ACCELERATION_DELAY_COEFFICIENT;
				float accel = (float) -Math.log(acc - 26.822f * GameThreads.ACCELERATION_DELAY_COEFFICIENT) / GameThreads.ACCELERATION_DELAY_COEFFICIENT + (float) Math.log(acc) / GameThreads.ACCELERATION_DELAY_COEFFICIENT;
				float brake = 6f + Math.min(5f * (float) Math.log((level - 1) / 50f + 1), 3f);

				DecimalFormat df2 = new DecimalFormat("0.00");

				cbri_16.draw(spriteBatch, "Top Speed:", 200, 255);
				cbri_16.draw(spriteBatch, "Acceleration " + ((kmph) ? "(0-100)" : "(0-60):"), 200, 235);
				cbri_16.draw(spriteBatch, "Braking:", 200, 215);

				cbri_16.draw(spriteBatch, ((kmph) ? df2.format(maxSpeed * 1.609) + "kmph" : df2.format(maxSpeed) + "mph"), 350, 255);
				cbri_16.draw(spriteBatch, df2.format(accel) + "s", 350, 235);
				cbri_16.draw(spriteBatch, df2.format(brake) + "(m/s)/s", 350, 215);

				// Buttons
				spriteBatch.draw(button_back, 5, 315);

				// Button Down
				switch (buttonDown) {
					case 0:
						spriteBatch.draw(buttondown_small, 5, 315);
						break;
					case 1:
					case 2:
					case 3:
						spriteBatch.draw(buttondown_small, 480 + (buttonDown - 1) * 45, 180);
						break;
				}

				break;

			case TUTORIAL:
				// Title
				GlyphLayout tutorialTitleGlyph = new GlyphLayout(bsh_40, "HOW TO PLAY");
				bsh_40.draw(spriteBatch, tutorialTitleGlyph, 320 - tutorialTitleGlyph.width / 2f, 350);

				// Buttons
				spriteBatch.draw(button_back, 5, 315);

				// Tutorial text
				switch (tutorialScreen) {
					//case 7:
						//cbri_16.draw(spriteBatch, "Every parcel that lands at the correct house increases the score of the next delivery by one. \nThis is reset every time you miss or fail a delivery.", 20, 60);
					case 5:
						//cbri_16.draw(spriteBatch, "In challenge mode, you have three days to deliver as many parcels as possible.", 20, 100);
						tutorialScreen = 8;
					case 4:
						cbri_16.draw(spriteBatch, "You have three lives. If you deliver a parcel to the wrong house, or fail to \ndeliver a parcel, you lose a life! The game ends when you miss three times.", 20, 180);
					//case 4:
						//cbri_16.draw(spriteBatch, "There are two game modes.", 20, 180);
						//tutorialScreen = 5;
					case 3:
						cbri_16.draw(spriteBatch, "Each parcel that lands in the correct place increases your score.", 20, 220);
					case 2:
						cbri_16.draw(spriteBatch, "You deliver parcels by throwing them at the correct house, while driving past. You don't \nhave the time to stop!", 20, 260);
					case 1:
						cbri_16.draw(spriteBatch, "Welcome to Deliverance! Your goal is to deliver as many parcels as possible, as accurately \nand quickly as possible.", 20, 300);
						break;

					case 11:
						GlyphLayout tutorialGlyph4 = new GlyphLayout(cbri_16, "Red contracts are the most difficult, but come with a large score multiplier as a reward.", Color.WHITE, 160, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph4, 370, 90);
					case 10:
						GlyphLayout tutorialGlyph3 = new GlyphLayout(cbri_16, "Green contracts are the easiest, but come with the lowest reward.", Color.WHITE, 120, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph3, 220, 90);
					case 9:
						GlyphLayout tutorialGlyph2 = new GlyphLayout(cbri_16, "These determine how many parcels you have to deliver, and the average number of delivery targets per 10 houses (density).", Color.WHITE, 160, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph2, 10, 200);
					case 8:
						spriteBatch.draw(tutorial_2, 170, 96);

						GlyphLayout tutorialGlyph1 = new GlyphLayout(cbri_16, "You select a contract at the start of each day.", Color.WHITE, 160, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph1, 10, 300);
						break;

					case 19:
						GlyphLayout tutorialGlyph12 = new GlyphLayout(cbri_16, "The speedometer is in the top left", Color.WHITE, 150, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph12, 10, 260);
					case 18:
						GlyphLayout tutorialGlyph11 = new GlyphLayout(cbri_16, "Your score is shown in the top right, along with lives left (in endless mode).", Color.WHITE, 250, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph11, 380, 300);
					case 17:
						GlyphLayout tutorialGlyph10 = new GlyphLayout(cbri_16, "The house numbers are displayed on the front of each house.", Color.WHITE, 150, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph10, 480, 180);
					case 16:
						GlyphLayout tutorialGlyph9 = new GlyphLayout(cbri_16, "The HUD and manifest display your next three targets.", Color.WHITE, 150, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph9, 480, 250);
					case 15:
						GlyphLayout tutorialGlyph8 = new GlyphLayout(cbri_16, "The clock shows how much time you have left.", Color.WHITE, 200, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph8, 180, 300);
					case 14:
						GlyphLayout tutorialGlyph7 = new GlyphLayout(cbri_16, "To throw a parcel, press down on the van, and swipe in the direction you want to throw the parcel. The further you swipe left or right, the further the parcel will fly.", Color.WHITE, 285, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph7, 350, 90);
					case 13:
						GlyphLayout tutorialGlyph6 = new GlyphLayout(cbri_16, "The brake pedal allows you to slow down quickly. Use this to avoid missing a house!", Color.WHITE, 150, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph6, 180, 90);
					case 12:
						spriteBatch.draw(tutorial_1, 170, 96);

						GlyphLayout tutorialGlyph5 = new GlyphLayout(cbri_16, "Hold down the accelerator (the big pedal) to drive forward. The further up the screen you move your finger, the more you accelerate.", Color.WHITE, 160, Align.left, true);
						cbri_16.draw(spriteBatch, tutorialGlyph5, 10, 150);
						break;

					case 25:
						cbri_16.draw(spriteBatch, "Enjoy playing Deliverance! Give a 5* review if you do, it really helps!", 20, 100);
					case 24:
						cbri_16.draw(spriteBatch, "Getting a higher score will help you level up. Levelling up lets you can upgrade your van in \nthe Van Customisation screen, and unlocks new van styles.", 20, 140);
					case 23:
						cbri_16.draw(spriteBatch, "Try to get the highest score you can! Share your score with your friends, and challenge them to \nbeat your score.", 20, 180);
					case 22:
						cbri_16.draw(spriteBatch, "The game will end when you fail 3 parcel deliveries.", 20, 220);
					case 21:
						cbri_16.draw(spriteBatch, "After 21:00, you can no longer deliver parcels. You must carry on driving past the end of the \nstreet, and then back to the warehouse, where your van will stop and the day will end.", 20, 260);
					case 20:
						cbri_16.draw(spriteBatch, "The game starts and ends at the warehouse. You must drive a short distance before you reach \nthe first street.", 20, 300);
						break;
				}

				// Tap screen text
				GlyphLayout infoGlyph = new GlyphLayout(cbri_16, "TAP SCREEN TO CONTINUE");
				cbri_16.draw(spriteBatch, infoGlyph, 320 - infoGlyph.width / 2f, 5 + infoGlyph.height);

				// Button Down
				switch (buttonDown) {
					case 0:
						spriteBatch.draw(buttondown_small, 5, 315);
						break;
				}
				break;

		}

		spriteBatch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// Van level locking
		if(currentMenu == Menus.CUSTOMISATION) {
			shapeRenderer.setColor(0, 0, 0, 0.4f);

			for (int i = 1; i < 8; i++) {
				if (level < i * 10) {
					shapeRenderer.rect(22 + (i % 4) * 150, 92 - (float) (i / 4) * 80, 148, 78);
				}
			}
		}

		// Black Screen
		shapeRenderer.setColor(0, 0, 0, blackScreenOpacity);
		shapeRenderer.rect(0, 0, 640, 360);

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
	}

	@Override
	public void dispose() {
		super.dispose();

		timer.stop();

		spriteBatch.dispose();
		shapeRenderer.dispose();
		music.dispose();
	}

	@Override
	public void pause() {
		super.pause();

		music.stop();
		music.setPosition(0);
	}

	@Override
	public void resume() {
		super.resume();

		music.play();
	}

	private void drawTooltip(float x, float y, String text, boolean dangle) {

		GlyphLayout ttGlyph = new GlyphLayout(cbri_16, text, new Color(1f, 1f, 1f, 1f), 150, Align.left, true);

		spriteBatch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		shapeRenderer.setColor(0, 0, 0, 0.6f);
		shapeRenderer.rect(x, y - ((dangle) ? ttGlyph.height + 10 : 0), ttGlyph.width + 10, ttGlyph.height + 10);

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		spriteBatch.begin();
		cbri_16.draw(spriteBatch, ttGlyph, x + 5, y + ((dangle) ? -5 : 5 + ttGlyph.height));
	}



	public void read() {

		try {
			FileHandle file = Gdx.files.local("menusave.txt");

			if(file.exists()) {
				String[] fileContents = file.readString().split(";");

				int count = 0;

				// Settings
				MenuScreen.musicVolume = Float.parseFloat(fileContents[count++]);
				MenuScreen.soundVolume = Float.parseFloat(fileContents[count++]);
				MenuScreen.kmph = Boolean.parseBoolean(fileContents[count++]);

				// Customisation
				MenuScreen.vanSelected = Integer.parseInt(fileContents[count++]);
				MenuScreen.level = Integer.parseInt(fileContents[count++]);
				MenuScreen.exp = Integer.parseInt(fileContents[count++]);

			} else{
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
			save();
		}

	}

	public static void save() {

		FileHandle file = Gdx.files.local("menusave.txt");

		file.writeString("", false);

		// Settings
		file.writeString(MenuScreen.musicVolume + ";", true);
		file.writeString(MenuScreen.soundVolume + ";", true);
		file.writeString(MenuScreen.kmph + ";", true);

		// Customisation
		file.writeString(MenuScreen.vanSelected + ";", true);
		file.writeString(MenuScreen.level + ";", true);
		file.writeString(MenuScreen.exp + ";", true);

	}

	public static Menus getCurrentMenu() {
		return currentMenu;
	}

	public static void setCurrentMenu(Menus currentMenu) {
		MenuScreen.currentMenu = currentMenu;
	}

	public static void setButtonDown(int buttonDown) {
		MenuScreen.buttonDown = buttonDown;
	}

	public static Game getGame() {
		return game;
	}

	public static void stopTimer() {
		if(timer != null) {
			timer.stop();
		}
	}

	public static void setTargetMode(GameMode targetMode) {
		MenuScreen.targetMode = targetMode;
	}

	public static int getVanSelected() {
		return vanSelected;
	}

	public static void setVanSelected(int vanSelected) {
		MenuScreen.vanSelected = vanSelected;
	}

	public static float getSoundVolume() {
		return soundVolume;
	}

	public static float getMusicVolume() {
		return musicVolume;
	}

	public static void setSoundVolume(float soundVolume) {

		if(soundVolume < 0) {
			soundVolume = 0;
		} else if(soundVolume > 1f) {
			soundVolume = 1f;
		}

		MenuScreen.soundVolume = soundVolume;
	}

	public static void setMusicVolume(float musicVolume) {

		if(musicVolume < 0) {
			musicVolume = 0;
		} else if(musicVolume > 1f) {
			musicVolume = 1f;
		}

		MenuScreen.musicVolume = musicVolume;

		music.setVolume(musicVolume);
	}

	public static void invertKmph() {
		kmph = !kmph;
	}

	public static boolean isKmph() {
		return kmph;
	}

	public static int getTutorialScreen() {
		return tutorialScreen;
	}

	public static void setTutorialScreen(int tutorialScreen) {
		if(tutorialScreen > 25) {
			currentMenu = Menus.MAIN;
			tutorialScreen = 1;
		}

		MenuScreen.tutorialScreen = tutorialScreen;
	}

	public static int getLevel() {
		return level;
	}

	public static void playClick() {
		click.play(0.35f * soundVolume);
	}
}
