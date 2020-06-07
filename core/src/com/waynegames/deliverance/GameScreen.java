package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen extends ScreenAdapter {
	static final int PIXELS_PER_METRE = 20;

	private Game game;

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private OrthographicCamera orthographicCamera;

	private BitmapFont cbri_12, arl_10, arb_12, arb_12_2;

	private Sprite van, box, house, fence, road, speedo, dial, roadGap, store, warehouse, warehouse_rival, tree, logo_mercury, clock;
	private Sprite[] pedals;

	private static Van vanObj;
	private static Parcel[] parcels;

	private static Street street;
	private ShopBanner[] shopBanners;

	private static int hour, minute;
	private static boolean dayEnd;

	GameScreen(Deliverance game) {

		this.game = game;

		vanObj = new Van();
		parcels = new Parcel[100];
		shopBanners = new ShopBanner[2];

		hour = 9;
		minute = 0;

		dayEnd = false;

		street = new Street(2400);

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

		// Load game sprites
		this.van = new Sprite(Deliverance.assetManager.get("game_sprites/van_01.png", Texture.class));
		this.box = new Sprite(Deliverance.assetManager.get("game_sprites/box.png", Texture.class));
		this.house = new Sprite(Deliverance.assetManager.get("game_sprites/house_01.png", Texture.class));
		this.fence = new Sprite(Deliverance.assetManager.get("game_sprites/fence_01.png", Texture.class));
		this.road = new Sprite(Deliverance.assetManager.get("game_sprites/road.png", Texture.class));

		this.roadGap = new Sprite(Deliverance.assetManager.get("game_sprites/roadgap.png", Texture.class));
		this.store = new Sprite(Deliverance.assetManager.get("game_sprites/store_01.png", Texture.class));
		this.warehouse = new Sprite(Deliverance.assetManager.get("game_sprites/warehouse.png", Texture.class));
		this.warehouse_rival = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/warehouse.png", Texture.class), 62, 176, 476, 130));
		this.tree = new Sprite(Deliverance.assetManager.get("game_sprites/tree.png", Texture.class));

		this.logo_mercury = new Sprite(Deliverance.assetManager.get("game_sprites/logo_mercury.png", Texture.class));

		this.speedo = new Sprite(Deliverance.assetManager.get("game_sprites/speedometer.png", Texture.class));
		this.dial = new Sprite(Deliverance.assetManager.get("game_sprites/speedodial.png", Texture.class));
		this.clock = new Sprite(Deliverance.assetManager.get("game_sprites/clock.png", Texture.class));

		this.pedals = new Sprite[4];
		for(int i = 0; i < 4; i++) {
			this.pedals[i] = new Sprite(new TextureRegion(Deliverance.assetManager.get("game_sprites/pedals.png", Texture.class), i * 80, 0, 80, 120));
		}

		// Set the input processor
		Gdx.input.setInputProcessor(new GameInput(orthographicCamera));

	}

	@Override
	public void render(float delta) {
		super.render(delta);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);

		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);

		// Draw Sky
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(135 / 255f, 206 / 255f, 235 / 255f, 1.0f);
		shapeRenderer.rect(0, 0, 640, 360);
		shapeRenderer.end();

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
		} else if(vanObj.getX() <= street.getStartX()) {
			spriteBatch.draw(store, street.getStartX() - (int) Math.floor(vanObj.getX()) - 800, 32);
			spriteBatch.draw(store, street.getStartX() - (int) Math.floor(vanObj.getX()), 32, -250, 200);
			spriteBatch.draw(roadGap, street.getStartX() - (int) Math.floor(vanObj.getX()) - 550, 19);
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
			}
		}

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

		// UI
		// Pedal
		spriteBatch.draw(pedals[(int) (GameInput.getPedalPressure() / 10f * 4)], 10, 10, 80, 120 - 60 * (GameInput.getPedalPressure() / 10f));

		// Speedometer
		spriteBatch.draw(speedo, 0, 300);
		spriteBatch.draw(dial, 10, 328, 21, 2, 22, 4, 1.0f, 1.0f, 65f - (vanObj.getSpeed() * 2.237f) / 100f * 310f, true);

		// Clock
		spriteBatch.draw(clock, 270, 330);

		spriteBatch.end();

		// Clock numbering
		lcdNumber(292, hour / 10);
		lcdNumber(305, hour % 10);
		lcdNumber(324, minute / 10);
		lcdNumber(337, minute % 10);

	}

	@Override
	public void dispose() {
		super.dispose();

		spriteBatch.dispose();
		shapeRenderer.dispose();
		cbri_12.dispose();
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
		spriteBatch.draw(logo_mercury, x + 441, 135);

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
		GameScreen.dayEnd = dayEnd;
	}
}
