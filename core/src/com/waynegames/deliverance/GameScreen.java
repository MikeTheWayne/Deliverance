package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
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

import java.text.DecimalFormat;
import java.util.Random;

public class GameScreen extends ScreenAdapter {
	static final int PIXELS_PER_METRE = 20;
	private static final int FIRST_STREET_START_X = 2400;

	private static Game game;

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private OrthographicCamera orthographicCamera;

	private BitmapFont cbri_12, arl_10, arb_12, arb_12_2, cls_10, arb_24, arl_24, cbri_16;

	private Sprite van, box, house, fence, road, speedo, dial, roadGap, store, warehouse, warehouse_rival, tree, clock, lifedown, button_exit, wheel_half, wheel_full, gapfog;
	private Sprite[] pedals, scoreDigits, logos;

	// Music
	private static Music music;

	// Game Values
	private static float blackScreenOpacity;

	private static int daySeed;

	private static Van vanObj;
	private static Parcel[] parcels;

	private static Street street;
	private ShopBanner[] shopBanners;

	private static int hour, minute;
	private static int day;
	private static boolean dayEnd;

	private static int parcelsLeft;
	private static float parcelDensity;

	private static int score;

	private static GameMode gameMode;

	private static int livesLeft;
	private static int maxDays;

	private int maxLives;

	private static boolean gameOver;

	private static Contract[] contracts;

	private static float scoreMultiplier;

	private static int streak;

	// Statistics
	private static int parcelsThrown;
	private static int parcelsHit;
	private static float averageSpeed;
	private static int drivingSeconds;

	// Wheel anim
	private boolean wheelAnim;
	private int wheelAnimDelay;

	GameScreen(Game game, GameMode gameMode, int lives, int days) {

		GameScreen.game = game;

		vanObj = new Van();
		parcels = new Parcel[100];
		shopBanners = new ShopBanner[2];

		hour = 21;
		minute = 0;

		day = 0;

		dayEnd = true;

		score = 0;

		blackScreenOpacity = 1f;

		parcelsLeft = 300;
		parcelDensity = 1.5f;

		street = new Street(FIRST_STREET_START_X);

		Random random = new Random();

		daySeed = random.nextInt(1048576);

		GameScreen.gameMode = gameMode;

		this.maxLives = lives;

		livesLeft = lives;
		maxDays = days;

		gameOver = false;

		scoreMultiplier = 1f;

		streak = 0;

		parcelsThrown = 0;
		parcelsHit = 0;
		averageSpeed = 0;
		drivingSeconds = 0;

		wheelAnim = false;
		wheelAnimDelay = 0;

		generateContracts();

		// Load from save, if it exists
		read();

		// Music
		music = Gdx.audio.newMusic(Gdx.files.internal("music/deliverance_music.mp3"));
		music.setVolume(MenuScreen.getMusicVolume());
		music.setLooping(false);

		// Graphics
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.orthographicCamera = new OrthographicCamera(640, 360);

		this.orthographicCamera.position.set(this.orthographicCamera.viewportWidth / 2f, this.orthographicCamera.viewportHeight / 2f, 0);
		this.orthographicCamera.update();

		// Load Fonts
		this.cbri_12 = new BitmapFont(Gdx.files.internal("fonts/cbri_12.fnt"));
		this.arl_10 = new BitmapFont(Gdx.files.internal("fonts/arl_10.fnt"));
		this.arb_12 = new BitmapFont(Gdx.files.internal("fonts/arb_12.fnt"));
		this.arb_12_2 = new BitmapFont(Gdx.files.internal("fonts/arb_12.fnt"));
		this.cls_10 = new BitmapFont(Gdx.files.internal("fonts/cls_10.fnt"));
		this.arb_24 = new BitmapFont(Gdx.files.internal("fonts/arb_24.fnt"));
		this.arl_24 = new BitmapFont(Gdx.files.internal("fonts/arl_24.fnt"));
		this.cbri_16 = new BitmapFont(Gdx.files.internal("fonts/cbri_16.fnt"));

		// Load game sprites
		this.van = new Sprite(Deliverance.assetManager.get("game_sprites/van_0" + (MenuScreen.getVanSelected() + 1) + ".png", Texture.class));
		this.box = new Sprite(Deliverance.assetManager.get("game_sprites/box.png", Texture.class));
		this.house = new Sprite(Deliverance.assetManager.get("game_sprites/house_01.png", Texture.class));
		this.fence = new Sprite(Deliverance.assetManager.get("game_sprites/fence_01.png", Texture.class));
		this.road = new Sprite(Deliverance.assetManager.get("game_sprites/road.png", Texture.class));

		this.roadGap = new Sprite(Deliverance.assetManager.get("game_sprites/roadgap.png", Texture.class));
		this.gapfog = new Sprite(Deliverance.assetManager.get("game_sprites/gapfog.png", Texture.class));
		this.store = new Sprite(Deliverance.assetManager.get("game_sprites/store_01.png", Texture.class));
		this.warehouse = new Sprite(Deliverance.assetManager.get("game_sprites/warehouse.png", Texture.class));
		this.warehouse_rival = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/warehouse.png", Texture.class), 62, 176, 476, 130));
		this.tree = new Sprite(Deliverance.assetManager.get("game_sprites/tree.png", Texture.class));

		this.logos = new Sprite[8];
		this.logos[0] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_hurlers.png", Texture.class));
		this.logos[1] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_drop.png", Texture.class));
		this.logos[2] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_congo.png", Texture.class));
		this.logos[3] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_fnf.png", Texture.class));
		this.logos[4] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_werf.png", Texture.class));
		this.logos[5] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_imperial.png", Texture.class));
		this.logos[6] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_oops.png", Texture.class));
		this.logos[7] = new Sprite(Deliverance.assetManager.get("game_sprites/logo_gold.png", Texture.class));

		this.speedo = new Sprite(Deliverance.assetManager.get("game_sprites/speedometer" + ((MenuScreen.isKmph()) ? "_kmph" : "") + ".png", Texture.class));
		this.dial = new Sprite(Deliverance.assetManager.get("game_sprites/speedodial.png", Texture.class));
		this.clock = new Sprite(Deliverance.assetManager.get("game_sprites/clock.png", Texture.class));

		this.lifedown = new Sprite(Deliverance.assetManager.get("game_sprites/lifedown.png", Texture.class));

		this.button_exit = new Sprite(Deliverance.assetManager.get("game_sprites/button_exit.png", Texture.class));

		this.wheel_half = new Sprite(Deliverance.assetManager.get("game_sprites/wheel_half.png", Texture.class));
		this.wheel_full = new Sprite(Deliverance.assetManager.get("game_sprites/wheel_full.png", Texture.class));

		this.pedals = new Sprite[4];
		for(int i = 0; i < 4; i++) {
			this.pedals[i] = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/pedals.png", Texture.class), i * 80, 0, 80, 120));
		}

		this.scoreDigits = new Sprite[10];
		for(int i = 0; i < 10; i++) {
			this.scoreDigits[i] = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/score_digits.png", Texture.class), 18 * (i % 5), 22 * (i / 5), 18, 22));
		}

		// Set the input processor
		Gdx.input.setInputProcessor(new GameInput(orthographicCamera));

		// Start the threads
		GameThreads.run();

	}

	@Override
	public void render(float delta) {
		super.render(delta);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);

		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);

		// Move van based on speed
		vanObj.setX(vanObj.getX() + (vanObj.getSpeed() * GameScreen.PIXELS_PER_METRE) * delta);

		// Animate parcels
		for(Parcel p : parcels) {
			if(p != null) {
				p.fly(delta);
			}
		}

		// Wheel animation
		if(vanObj.getSpeed() > 0) {
			if (wheelAnimDelay++ >= 8 - (vanObj.getSpeed() / 4f)) {
				wheelAnim = !wheelAnim;
				wheelAnimDelay = 0;
			}
		}

		// Draw Sky
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		if(hour < 19) {
			shapeRenderer.setColor(135 / 255f, 206 / 255f, 235 / 255f, 1.0f);
			shapeRenderer.rect(0, 0, 640, 360);
		} else{
			float sunset = ((float) hour - 19) / 2f + (float) minute / 120f;
			Color sunsetBL = new Color((135 + 68 * sunset) / 255f, (206 - 1 * sunset) / 255f, (235 - 201 * sunset) / 255f, 1.0f);
			Color sunsetBR = new Color((135 + 3 * sunset) / 255f, (206 - 180 * sunset) / 255f, (235 - 122 * sunset) / 255f, 1.0f);
			Color sunsetTR = new Color((135 - 56 * sunset) / 255f, (206 - 185 * sunset) / 255f, (235 - 111 * sunset) / 255f, 1.0f);
			Color sunsetTL = new Color((135 + 3 * sunset) / 255f, (206 - 180 * sunset) / 255f, (235 - 122 * sunset) / 255f, 1.0f);
			shapeRenderer.rect(0, 0, 640, 360, sunsetBL, sunsetBR, sunsetTR, sunsetTL);
		}

		shapeRenderer.end();

		// Draw Background
		drawBackgroundScenery();

		spriteBatch.begin();

		// Road
		for(int x = 0; x < 12; x++) {
			spriteBatch.draw(road, x * 60 - (vanObj.getX() % 60), 0);
		}

		// Houses
		for (int x = 0; x < 5; x++) {
			if(vanObj.getX() + x * 200 >= street.getStartX() && vanObj.getX() - street.getStartX() + x * 200 <= street.getLength() / 2f * 200) {
				// House
				spriteBatch.draw(house, x * 200 - ((int) Math.floor(vanObj.getX()) % 200), 32);

				// House number
				// n = (vX - sX) / 200 + x
				GlyphLayout houseNumberOdd = new GlyphLayout(cbri_12, "" + (int) (Math.floor((vanObj.getX() - street.getStartX()) / 200f + x) * 2 + 1));
				GlyphLayout houseNumberEven = new GlyphLayout(cbri_12, "" + (int) (Math.floor((vanObj.getX() - street.getStartX()) / 200f + x) * 2 + 2));
				cbri_12.draw(spriteBatch, houseNumberOdd, x * 200 - ((int) Math.floor(vanObj.getX()) % 200) + 17 - houseNumberOdd.width / 2, 112);
				cbri_12.draw(spriteBatch, houseNumberEven, x * 200 - ((int) Math.floor(vanObj.getX()) % 200) + 183 - houseNumberEven.width / 2, 112);
			}
		}

		spriteBatch.end();

		// Industrial Starting Area
		if(vanObj.getX() <= 2200) {
			drawIndustrialArea(-vanObj.getX());
		} else if(dayEnd && vanObj.getX() >= street.getStartX() + street.getLength() / 2f * 200) {
			drawIndustrialArea(street.getStartX() + street.getLength() / 2f * 200 + 250 - vanObj.getX());
		}

		spriteBatch.begin();

		// Street gap
		if(vanObj.getX() + 640 >= street.getStartX() + street.getLength() / 2f * 200) {
			spriteBatch.draw(store, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()), 32);
			spriteBatch.draw(store, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 800, 32, -250, 200);
			spriteBatch.draw(roadGap, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 250, 19);
			spriteBatch.draw(gapfog, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 250, 19);
		} else if(vanObj.getX() <= street.getStartX()) {
			spriteBatch.draw(store, street.getStartX() - (int) Math.floor(vanObj.getX()) - 800, 32);
			spriteBatch.draw(store, street.getStartX() - (int) Math.floor(vanObj.getX()), 32, -250, 200);
			spriteBatch.draw(roadGap, street.getStartX() - (int) Math.floor(vanObj.getX()) - 550, 19);
			spriteBatch.draw(gapfog, street.getStartX() - (int) Math.floor(vanObj.getX()) - 550, 19);
		}

		spriteBatch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// Shop banner colouring
		if(vanObj.getX() + 640 >= street.getStartX() + street.getLength() / 2f * 200) {
			generateShopBanners();
			shapeRenderer.setColor(shopBanners[0].getBackgroundColour());
			shapeRenderer.rect(street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 49, 94, 200, 20);
			shapeRenderer.setColor(shopBanners[1].getBackgroundColour());
			shapeRenderer.rect(street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 800 - 49, 94, -200, 20);
		} else if(vanObj.getX() <= street.getStartX()) {
			generateShopBanners();
			shapeRenderer.setColor(shopBanners[0].getBackgroundColour());
			shapeRenderer.rect(street.getStartX() - (int) Math.floor(vanObj.getX()) - 800 + 49, 94, 200, 20);
			shapeRenderer.setColor(shopBanners[1].getBackgroundColour());
			shapeRenderer.rect(street.getStartX() - (int) Math.floor(vanObj.getX()) - 49, 94, -200, 20);
		} else{
			shopBanners[0] = null;
			shopBanners[1] = null;
		}

		shapeRenderer.end();
		spriteBatch.begin();

		// Shop banner text
		if(vanObj.getX() + 640 >= street.getStartX() + street.getLength() / 2f * 200) {
			GlyphLayout shop1Glyph = new GlyphLayout(arb_12, shopBanners[0].getName());
			GlyphLayout shop2Glyph = new GlyphLayout(arb_12_2, shopBanners[1].getName());
			arb_12.setColor(shopBanners[0].getForegroundColour());
			arb_12.draw(spriteBatch, shop1Glyph, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 50 + 100 - shop1Glyph.width / 2, 103 + shop1Glyph.height / 2);
			arb_12_2.setColor(shopBanners[1].getForegroundColour());
			arb_12_2.draw(spriteBatch, shop2Glyph, street.getStartX() + street.getLength() / 2f * 200 - (int) Math.floor(vanObj.getX()) + 800 - 50 - 100 - shop2Glyph.width / 2, 103 + shop2Glyph.height / 2);
		} else if(vanObj.getX() <= street.getStartX()) {
			GlyphLayout shop1Glyph = new GlyphLayout(arb_12, shopBanners[0].getName());
			GlyphLayout shop2Glyph = new GlyphLayout(arb_12_2, shopBanners[1].getName());
			arb_12.setColor(shopBanners[0].getForegroundColour());
			arb_12.draw(spriteBatch, shop1Glyph, street.getStartX() - (int) Math.floor(vanObj.getX()) - 800 + 50 + 100 - shop1Glyph.width / 2, 103 + shop1Glyph.height / 2);
			arb_12_2.setColor(shopBanners[1].getForegroundColour());
			arb_12_2.draw(spriteBatch, shop2Glyph, street.getStartX() - (int) Math.floor(vanObj.getX()) - 50 - 100 - shop2Glyph.width / 2, 103 + shop2Glyph.height / 2);
		}

		// Warehouse
		if(vanObj.getX() <= 800) {
			drawWarehouse(-vanObj.getX());
		} else if(dayEnd && vanObj.getX() >= street.getStartX() + street.getLength() / 2f * 200 + 1800 - 800) {
			drawWarehouse(street.getStartX() + street.getLength() / 2f * 200 + 1800 - vanObj.getX());
		}

		// Parcels
		for(Parcel p : parcels) {
			if(p != null) {
				spriteBatch.draw(box, p.getX(), p.getY(), 13 + 13 / 3f * (1 - p.getZ()), 15 + 15 / 3f * (1 - p.getZ()));

				if(p.isLanded()) {
					cls_10.setColor(1f, 1f, 0f, 1f - p.getScoreTextY() / 50f);
					cls_10.draw(spriteBatch, p.getScoreText(), p.getX(), p.getY() + p.getScoreTextY() + 15);
				}
			}
		}

		cls_10.setColor(1f, 1f, 1f, 1f);

		// Fences
		for(int x = 0; x < 5; x++) {
			if(vanObj.getX() + x * 200 >= street.getStartX() && vanObj.getX() - street.getStartX() + x * 200 <= street.getLength() / 2f * 200) {
				spriteBatch.draw(fence, x * 200 - ((int) Math.floor(vanObj.getX()) % 200), 32);
			}
		}

		// Street name glyph
		GlyphLayout streetName = new GlyphLayout(arl_10, street.getName().toUpperCase() + " STREET");

		spriteBatch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// Street name sign
		if((street.getStartX() + 50) - vanObj.getX() <= 640) {
			shapeRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapeRenderer.rect((street.getStartX() + 50) - (int) Math.floor(vanObj.getX()), 36, streetName.width + 12, 12);
			shapeRenderer.rect((street.getStartX() + 50 + 10) - (int) Math.floor(vanObj.getX()), 29, 2, 7);
			shapeRenderer.rect((street.getStartX() + 50 + (streetName.width + 12) - 10) - (int) Math.floor(vanObj.getX()), 29, 2, 7);
			shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			shapeRenderer.rect((street.getStartX() + 50) - (int) Math.floor(vanObj.getX()), 37, streetName.width + 10, 10);
		}

		shapeRenderer.end();
		spriteBatch.begin();

		// Street name
		if((street.getStartX() + 50) - vanObj.getX() <= 640) {
			arl_10.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			arl_10.draw(spriteBatch, streetName, (street.getStartX() + 50) - (int) Math.floor(vanObj.getX()) + 5, 45);
		}

		// Van
		spriteBatch.draw(van, 256, 3);

		// Wheel animation
		if(wheelAnim) {
			if(MenuScreen.getVanSelected() == 0 || MenuScreen.getVanSelected() == 6) {
				spriteBatch.draw(wheel_half, 256 + 20, 3);
				spriteBatch.draw(wheel_half, 256 + 109, 3);
			} else{
				spriteBatch.draw(wheel_full, 256 + 20, 3);
				spriteBatch.draw(wheel_full, 256 + 95, 3);
			}
		}

		spriteBatch.end();

		// Sunset Darkness
		if(hour >= 19) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			float sunset = ((float) hour - 19) / 2f + (float) minute / 120f;
			shapeRenderer.setColor(0, 0, 0, sunset / 4f);
			shapeRenderer.rect(0, 0, 640, 360);

			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}

		spriteBatch.begin();

		// UI
		// Pedal
		spriteBatch.draw(pedals[(int) (GameInput.getPedalPressure() / 10f * 3)], 10, 10, 80, 120 - 60 * (GameInput.getPedalPressure() / 10f));

		// Brake
		spriteBatch.draw(pedals[(int) (GameInput.getBrakePressure() / 10f * 3)], 100, 10, 60, 80 - 40 * (GameInput.getBrakePressure() / 10f));

		// Speedometer
		spriteBatch.draw(speedo, 0, 300);
		spriteBatch.draw(dial, 10, 328, 21, 2, 22, 4, 1.0f, 1.0f, 65f - (vanObj.getSpeed() * 2.237f) / 100f * 310f, true);

		// Clock
		spriteBatch.draw(clock, 270, 330);

		// Score
		drawScore();

		// Draw lives lost
		if(gameMode == GameMode.ENDLESS) {

			for(int i = 0; i < maxLives; i++) {
				spriteBatch.setColor(1f, 1f, 1f, ((livesLeft > i) ? 0.2f : 1.0f));
				spriteBatch.draw(lifedown, 638 - 20 * maxLives + i * 20, 313);
			}

			spriteBatch.setColor(1f, 1f, 1f, 1f);
		}

		spriteBatch.end();

		// Clock numbering
		lcdNumber(292, hour / 10);
		lcdNumber(305, hour % 10);
		lcdNumber(324, minute / 10);
		lcdNumber(337, minute % 10);

		// Manifest
		if(hour < 21) {
			drawManifest();
			drawManifestHUD();
		}

		// Black Screen
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		shapeRenderer.setColor(0, 0, 0, blackScreenOpacity);
		shapeRenderer.rect(0, 0, 640, 360);

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

		// Contract selection
		if(!gameOver && blackScreenOpacity >= 1f && ((gameMode == GameMode.ENDLESS && dayEnd) || (hour == 21))) {

			if(gameMode == GameMode.ENDLESS) {

				float sat1 = 0.3f;
				float sat2 = 0.2f;
				float bCol = 0.8f;
				float tCol = 0.5f;

				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

				// Draw contract boxes
				shapeRenderer.rect(110, 75, 120, 150, new Color(sat1, bCol, sat1, 1f), new Color(sat1, bCol, sat1, 1f), new Color(sat2, tCol, sat2, 1f), new Color(sat2, tCol, sat2, 1f));
				shapeRenderer.rect(260, 75, 120, 150, new Color(sat1, sat1, bCol, 1f), new Color(sat1, sat1, bCol, 1f), new Color(sat2, sat2, tCol, 1f), new Color(sat2, sat2, tCol, 1f));
				shapeRenderer.rect(410, 75, 120, 150, new Color(bCol, sat1, sat1, 1f), new Color(bCol, sat1, sat1, 1f), new Color(tCol, sat2, sat2, 1f), new Color(tCol, sat2, sat2, 1f));

				shapeRenderer.end();

				spriteBatch.begin();
				cbri_16.setColor(1f, 1f, 1f, 1f);
				arb_12.setColor(1f, 1f, 1f, 1f);

				// Draw contract information
				for (int i = 0; i < 3; i++) {
					DecimalFormat df = new DecimalFormat("0.00");

					cbri_16.draw(spriteBatch, "Parcels: " + contracts[i].getParcels(), 110 + 150 * i + 5, 225 - 5);
					cbri_16.draw(spriteBatch, "Density: " + df.format(contracts[i].getDensity()), 110 + 150 * i + 5, 225 - 21);
					cbri_16.draw(spriteBatch, "Score: x" + df.format(contracts[i].getScoreMultiplier()), 110 + 150 * i + 5, 225 - 53);

					GlyphLayout acceptGlyph = new GlyphLayout(arb_12, "ACCEPT");
					arb_12.draw(spriteBatch, acceptGlyph, 170 + 150 * i - acceptGlyph.width / 2f, 75 + 5 + acceptGlyph.height);
				}

				spriteBatch.end();
			}

			spriteBatch.begin();

			// Draw day
			arb_24.setColor(1f, 1f, 1f, 1f);

			GlyphLayout dayGlyph = new GlyphLayout(arb_24, "DAY " + (day + 1));
			arb_24.draw(spriteBatch, dayGlyph, 320 - dayGlyph.width / 2f, 300);

			// Instructions
			GlyphLayout infoGlyph;
			if(gameMode == GameMode.ENDLESS) {
				infoGlyph = new GlyphLayout(cbri_16, "SELECT A CONTRACT TO CONTINUE");
			} else{
				infoGlyph = new GlyphLayout(cbri_16, "TAP SCREEN TO CONTINUE");
			}
			cbri_16.draw(spriteBatch, infoGlyph, 320 - infoGlyph.width / 2f, 10 + infoGlyph.height);

			// Save & exit button
			spriteBatch.draw(button_exit, 5, 315);

			spriteBatch.end();

		}

	}

	@Override
	public void dispose() {
		super.dispose();

		GameThreads.stop();

		spriteBatch.dispose();
		shapeRenderer.dispose();
		cbri_12.dispose();
		music.dispose();
	}

	@Override
	public void pause() {
		super.pause();
		music.pause();
	}

	@Override
	public void resume() {
		super.resume();

		if(GameThreads.isTimeStarted() && hour < 21) {
			music.play();
		}
	}

	private void generateShopBanners() {
		if(shopBanners[0] == null || shopBanners[1] == null) {
			shopBanners[0] = new ShopBanner(street.getName());
			shopBanners[1] = new ShopBanner(street.getName());
		}
	}

	/**
	 * Displays an LCD digit, by blacking out a part of the default "8" digit on the clock
	 *
	 * @param x
	 * 		The x position of the digit
	 * @param number
	 * 		The number which the digit must represent
	 */
	private void lcdNumber(float x, int number) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(162 / 255f, 180 / 255f, 154 / 255f, 1.0f);

		// T    shapeRenderer.rect(x + 1, 335 + 19, 7, 2);
		// M    shapeRenderer.rect(x + 1, 335 + 10, 7, 2);
		// B    shapeRenderer.rect(x + 1, 335, 7, 2);
		// TL   shapeRenderer.rect(x, 335 + 12, 2, 7);
		// BL   shapeRenderer.rect(x, 335 + 3, 2, 7);
		// TR   shapeRenderer.rect(x + 7, 335 + 12, 2, 7);
		// BR   shapeRenderer.rect(x + 7, 335 + 3, 2, 7);

		switch (number) {
			case 0:
				shapeRenderer.rect(x + 1, 335 + 10, 7, 2);
				break;
			case 1:
				shapeRenderer.rect(x + 1, 335 + 19, 7, 2);
				shapeRenderer.rect(x + 1, 335 + 10, 7, 2);
				shapeRenderer.rect(x + 1, 335, 7, 2);
				shapeRenderer.rect(x, 335 + 12, 2, 7);
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				break;
			case 2:
				shapeRenderer.rect(x , 335 + 12, 2, 7);
				shapeRenderer.rect(x + 7, 335 + 3, 2, 7);
				break;
			case 3:
				shapeRenderer.rect(x , 335 + 12, 2, 7);
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				break;
			case 4:
				shapeRenderer.rect(x + 1, 335 + 19, 7, 2);
				shapeRenderer.rect(x + 1, 335, 7, 2);
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				break;
			case 5:
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				shapeRenderer.rect(x + 7, 335 + 12, 2, 7);
				break;
			case 6:
				shapeRenderer.rect(x + 7, 335 + 12, 2, 7);
				break;
			case 7:
				shapeRenderer.rect(x + 1, 335 + 10, 7, 2);
				shapeRenderer.rect(x + 1, 335, 7, 2);
				shapeRenderer.rect(x, 335 + 12, 2, 7);
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				break;
			case 9:
				shapeRenderer.rect(x, 335 + 3, 2, 7);
				break;
		}

		shapeRenderer.end();
	}

	/**
	 * Draws a warehouse, complete with logo and company vans
	 *
	 * @param x
	 * 		The x position of the warehouse
	 */
	private void drawWarehouse(float x) {
		spriteBatch.draw(tree, x - 10, 32);
		spriteBatch.draw(warehouse, x, 0);
		spriteBatch.draw(logos[MenuScreen.getVanSelected()], x + 441, 135);

		spriteBatch.draw(van, x + 240, 32, 0, 0, van.getWidth(), van.getHeight(), 0.7f, 0.7f, 0f);
		spriteBatch.draw(van, x + 340, 32, 0, 0, van.getWidth(), van.getHeight(), 0.7f, 0.7f, 0f);
		spriteBatch.draw(van, x + 440, 32, 0, 0, van.getWidth(), van.getHeight(), 0.7f, 0.7f, 0f);
	}

	/**
	 * Draws an industrial area, with rival delivery companies hidden behind trees and foliage
	 *
	 * @param x
	 * 		The starting position of the industrial area
	 */
	private void drawIndustrialArea(float x) {

		// Rival warehouses
		spriteBatch.begin();
		spriteBatch.draw(warehouse_rival, x + 600, 32);
		spriteBatch.draw(warehouse_rival, x + 1100, 32);
		spriteBatch.draw(logos[(MenuScreen.getVanSelected() + 1 + ((vanObj.getX() > 10000) ? 2 : 0)) % 7], x + 600 + 441 - 62, 135 + 32 - 54);
		spriteBatch.draw(logos[(MenuScreen.getVanSelected() + 2 + ((vanObj.getX() > 10000) ? 2 : 0)) % 7], x + 1100 + 441 - 62, 135 + 32 - 54);
		spriteBatch.end();

		// Shrubbery
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(16 / 255f, 40 / 255f, 6 / 255f, 1.0f);
		shapeRenderer.rect(x, 32, 1800, 80);
		shapeRenderer.end();

		// Trees
		spriteBatch.begin();
		for(int i = 0; i < 19; i++) {
			spriteBatch.draw(tree, x + 550 + i * 55, 32);
		}
		spriteBatch.end();

	}

	private void drawManifest() {

		int x = 570;
		int y = 260;

		// Outline
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0f, 0f, 0f, 1f);
		shapeRenderer.rect(x - 1, y - 1, 72, 54);
		shapeRenderer.setColor(1f, 1f, 1f, 1f);
		shapeRenderer.rect(x, y, 70, 52);
		shapeRenderer.setColor(0f, 0f, 0f, 1f);
		for(int i = 0; i < 3; i++) {
			shapeRenderer.rect(x, y + 12 + i * 13, 72, 1);
		}
		shapeRenderer.rect(x + 22, y + 12, 1, 40);
		shapeRenderer.end();

		// Text
		spriteBatch.begin();

		cls_10.setColor(0f, 0f, 0f, 1f);

		for(int i = 0; i < 3; i++) {
			if(street.getTargets().size() > i) {
				GlyphLayout target = new GlyphLayout(cls_10, street.getTargets().get(i) + "");
				cls_10.draw(spriteBatch, target, x + 11 - target.width / 2f, y + 45 - 13 * i + target.height / 2f);
				cls_10.draw(spriteBatch, street.getName().substring(0, 1).toUpperCase() + street.getName().substring(1, 3) + ". St", x + 26, y + 45 - 13 * i + target.height / 2f);
			}
		}

		GlyphLayout parcelsLeftGlyph = new GlyphLayout(cls_10, "Pcls: " + parcelsLeft);
		cls_10.draw(spriteBatch, parcelsLeftGlyph, x + 4, y + 9);

		spriteBatch.end();

	}

	private void drawManifestHUD() {
		spriteBatch.begin();

		arb_24.setColor(1f, 1f, 1f, 1f);
		arl_24.setColor(1f, 1f, 1f, 1f);

		if(street.getTargets().size() > 0) {
			GlyphLayout hud_1 = new GlyphLayout(arb_24, street.getTargets().get(0) + "");
			arb_24.draw(spriteBatch, hud_1, 320 - hud_1.width / 2f, 300 + hud_1.height / 2f);
		}

		for(int i = 0; i < 2; i++) {
			if(street.getTargets().size() > i + 1) {
				GlyphLayout hud_i = new GlyphLayout(arl_24, street.getTargets().get(i + 1) + "");
				arl_24.draw(spriteBatch, hud_i, 370 - hud_i.width / 2f + 50 * i, 300 + hud_i.height / 2f);
			}
		}

		spriteBatch.end();
	}

	/**
	 * Draws the score, using the digit sprites
	 */
	private void drawScore() {

		int x = 635;
		int y = 355;

		String scoreString = String.valueOf(score);

		for(int i = 0; i < scoreString.length(); i++) {
			spriteBatch.draw(scoreDigits[Integer.parseInt(scoreString.substring(scoreString.length() - 1 - i, scoreString.length() - i))], x - 18 * (i + 1), y - 22);
		}

	}

	/**
	 * Draws slowly drifting, blurred silhouettes of buildings in the background
	 */
	private void drawBackgroundScenery() {

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		for(int j = 0; j < 20; j++) {

			float x = (-vanObj.getX() + j * 5000 + (daySeed * j) % 5000) / 10;

			for (int i = 0; i < 10; i++) {
				shapeRenderer.setColor(62 / 255f, 94 / 255f, 107 / 255f, 0.1f * i);
				shapeRenderer.rect(x - 10 + i, 30 - 10 + i, ((daySeed * j) % 100) + 50 + 20 - i * 2, ((daySeed * j) % 100) + 180 + 20 - i * 2);
			}
		}

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);

	}

	public static Van getVanObj() {
		return vanObj;
	}

	public static Parcel[] getParcels() {
		return parcels;
	}

	public static void setParcel(Parcel parcel, int index) {
		parcels[index] = parcel;
	}

	public static Street getStreet() {
		return street;
	}

	public static void setStreet(Street street) {
		GameScreen.street = street;
	}

	public static int getMinute() {
		return minute;
	}

	public static int getHour() {
		return hour;
	}

	public static void setMinute(int minute) {
		GameScreen.minute = minute;
	}

	public static void setHour(int hour) {
		GameScreen.hour = hour;
	}

	public static void setDayEnd(boolean dayEnd) {
		if(!GameScreen.dayEnd && dayEnd) {
			GameScreen.generateContracts();
		}

		GameScreen.dayEnd = dayEnd;
	}

	public static int getParcelsLeft() {
		return parcelsLeft;
	}

	public static void removeTarget() {
		street.getTargets().remove(0);
		parcelsLeft--;
		parcelsThrown++;
	}

	public static float getParcelDensity() {
		return parcelDensity;
	}

	public static void incrementScore(int increment) {
		score += increment;

		if(score < 0) {
			score = 0;
		}
	}

	public static float getBlackScreenOpacity() {
		return blackScreenOpacity;
	}

	public static void setBlackScreenOpacity(float blackScreenOpacity) {
		GameScreen.blackScreenOpacity = blackScreenOpacity;
	}

	public static void checkChallengeGameOver() {
		if(day == maxDays) {
			gameOver = true;
		}
	}

	public static void incrementDay() {

		GameThreads.setTimeStarted(false);

		hour = 9;
		minute = 0;

		day++;

		vanObj.setX(0);

		dayEnd = false;

		street = new Street(FIRST_STREET_START_X);

		Random random = new Random();

		daySeed = random.nextInt(1048576);

		if(gameMode == GameMode.CHALLENGE) {
			parcelsLeft = 300;
		}

	}

	public static GameMode getGameMode() {
		return gameMode;
	}

	public static int getLivesLeft() {
		return livesLeft;
	}

	public static void setLivesLeft(int livesLeft) {
		GameScreen.livesLeft = livesLeft;

		if(livesLeft <= 0) {
			gameOver = true;
		}
	}

	public static boolean isGameOver() {
		return gameOver;
	}

	public static void generateContracts() {
		contracts = new Contract[3];
		for(int i = 0; i < 3; i++) {
			contracts[i] = new Contract(day, i);
		}
	}

	public static void selectContract(int contract) {
		parcelsLeft = contracts[contract].getParcels();
		parcelDensity = contracts[contract].getDensity();
		scoreMultiplier = contracts[contract].getScoreMultiplier();

		incrementDay();
	}


	public void read() {

		try {
			FileHandle file = Gdx.files.local("gamesave" + ((GameScreen.gameMode == GameMode.ENDLESS) ? "E" : "C") + ".txt");

			if(file.exists()) {
				String[] fileContents = file.readString().split(";");

				int count = 0;

				// Core values
				GameScreen.day = Integer.parseInt(fileContents[count++]);
				GameScreen.score = Integer.parseInt(fileContents[count++]);
				GameScreen.livesLeft = Integer.parseInt(fileContents[count++]);
				GameScreen.streak = Integer.parseInt(fileContents[count++]);

				// Statistics
				GameScreen.parcelsThrown = Integer.parseInt(fileContents[count++]);
				GameScreen.parcelsHit = Integer.parseInt(fileContents[count++]);
				GameScreen.averageSpeed = Float.parseFloat(fileContents[count++]);
				GameScreen.drivingSeconds = Integer.parseInt(fileContents[count++]);

				// Contracts
				for(int i = 0; i < 3; i++) {
					GameScreen.contracts[i].fileLoadOverride(Integer.parseInt(fileContents[count++]), Float.parseFloat(fileContents[count++]), Float.parseFloat(fileContents[count++]));
				}

				// Delete file
				file.delete();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void save() {

		FileHandle file = Gdx.files.local("gamesave" + ((GameScreen.gameMode == GameMode.ENDLESS) ? "E" : "C") + ".txt");

		file.writeString("", false);

		// Core values
		file.writeString(GameScreen.day + ";", true);
		file.writeString(GameScreen.score + ";", true);
		file.writeString(GameScreen.livesLeft + ";", true);
		file.writeString(GameScreen.streak + ";", true);

		// Statistics
		file.writeString(GameScreen.parcelsThrown + ";", true);
		file.writeString(GameScreen.parcelsHit + ";", true);
		file.writeString(GameScreen.averageSpeed + ";", true);
		file.writeString(GameScreen.drivingSeconds + ";", true);

		// Contracts
		for(int i = 0; i < 3; i++) {
			file.writeString(GameScreen.contracts[i].getParcels() + ";", true);
			file.writeString(GameScreen.contracts[i].getDensity() + ";", true);
			file.writeString(GameScreen.contracts[i].getScoreMultiplier() + ";", true);
		}

	}

	public static float getScoreMultiplier() {
		return scoreMultiplier;
	}

	public static Game getGame() {
		return game;
	}

	public static int getScore() {
		return score;
	}

	public static void incrementParcelsHit() {
		parcelsHit++;
	}

	public static void calculateAverageSpeed() {
		averageSpeed += (vanObj.getSpeed() - averageSpeed) / ++drivingSeconds;
	}

	public static int getParcelsHit() {
		return parcelsHit;
	}

	public static int getParcelsThrown() {
		return parcelsThrown;
	}

	public static float getAverageSpeed() {
		return averageSpeed;
	}

	public static int getStreak() {
		return streak;
	}

	public static void incrementStreak() {
		streak++;
	}

	public static void resetStreak() {
		streak = 0;
	}

	public static void playMusic() {
		if(!music.isPlaying() && hour == 9) {
			music.play();
		}
	}
}
