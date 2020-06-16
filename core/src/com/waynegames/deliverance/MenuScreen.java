package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

public class MenuScreen extends ScreenAdapter {
	private final int TICKS_PER_SECOND = 30;

	private static Game game;

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private OrthographicCamera orthographicCamera;

	private Timer timer;

	private Sprite background, title, van, parcel, button_x, button_settings, button_achievements, button_tutorial, button_van, button_back, button_endless, button_challenge, buttondown_big, buttondown_small;

	private static int animStage;
	private static float vanX;
	private static float parcelX, parcelY, parcelVY;
	private static int parcelsLeft;

	private static int buttonDown;

	private static Menus currentMenu;

	public MenuScreen(Game game) {

		MenuScreen.game = game;

		final Random random = new Random();

		animStage = 1;
		vanX = -129 * 1.5f;
		parcelX = -20;
		parcelY = 35;
		parcelVY = random.nextInt(40) + 20;

		buttonDown = -1;

		currentMenu = Menus.MAIN;

		// Graphics
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.orthographicCamera = new OrthographicCamera(640, 360);

		this.orthographicCamera.position.set(this.orthographicCamera.viewportWidth / 2f, this.orthographicCamera.viewportHeight / 2f, 0);
		this.orthographicCamera.update();

		// Load menu sprites
		this.background = new Sprite(Deliverance.assetManager.get("menu_sprites/menu_background.png", Texture.class));
		this.title = new Sprite(Deliverance.assetManager.get("menu_sprites/title.png", Texture.class));
		this.van = new Sprite(Deliverance.assetManager.get("game_sprites/van_01.png", Texture.class));
		this.parcel = new Sprite(Deliverance.assetManager.get("game_sprites/box.png", Texture.class));

		this.button_x = new Sprite(Deliverance.assetManager.get("menu_sprites/button_x.png", Texture.class));
		this.button_settings = new Sprite(Deliverance.assetManager.get("menu_sprites/button_settings.png", Texture.class));
		this.button_achievements = new Sprite(Deliverance.assetManager.get("menu_sprites/button_achievement.png", Texture.class));
		this.button_tutorial = new Sprite(Deliverance.assetManager.get("menu_sprites/button_tutorial.png", Texture.class));
		this.button_van = new Sprite(Deliverance.assetManager.get("menu_sprites/button_van.png", Texture.class));
		this.button_back = new Sprite(Deliverance.assetManager.get("menu_sprites/button_back.png", Texture.class));
		this.button_endless = new Sprite(Deliverance.assetManager.get("menu_sprites/button_endless.png", Texture.class));
		this.button_challenge = new Sprite(Deliverance.assetManager.get("menu_sprites/button_challenge.png", Texture.class));

		this.buttondown_big = new Sprite(Deliverance.assetManager.get("menu_sprites/button_big_down.png", Texture.class));
		this.buttondown_small = new Sprite(Deliverance.assetManager.get("menu_sprites/button_small_down.png", Texture.class));

		// Input
		Gdx.input.setInputProcessor(new MenuInput(orthographicCamera));

		// Animation timer
		timer = new Timer();

		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {

				switch (animStage) {
					case 1:
						vanX += (200f - vanX / 1.75f) / TICKS_PER_SECOND;

						if(vanX > 224) {
							animStage++;

							parcelsLeft = random.nextInt(15) + 15;
						}
						break;
					case 2:
						if(parcelX < 250) {
							parcelX += (250f * 1.8f) / TICKS_PER_SECOND;

							float t = parcelX / 200f;
							parcelY = 35 + (parcelVY * t - (50f * t * t) / 2f);
						} else{
							parcelsLeft--;
							parcelX = -20;
							parcelY = 35;
							parcelVY = random.nextInt(40) + 20;

							if(parcelsLeft == 0) {
								animStage++;
							}
						}
						break;
					case 3:
						vanX += Math.min(200f, (vanX - 175f) / 1.75f) / TICKS_PER_SECOND;

						if(vanX > 700) {
							vanX = -129 * 1.5f;
							animStage = 1;
						}
						break;
				}

			}
		}, 0, 1f / TICKS_PER_SECOND);

	}

	@Override
	public void render(float delta) {
		super.render(delta);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);

		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);

		spriteBatch.begin();

		// Draw background
		spriteBatch.draw(background, 0, 0);

		spriteBatch.draw(parcel, parcelX, parcelY, parcel.getWidth() * 1.5f, parcel.getHeight() * 1.5f);
		spriteBatch.draw(van, vanX, 10, van.getWidth() * 1.5f, van.getHeight() * 1.5f);

		switch (currentMenu) {

			case MAIN:
				// Title
				spriteBatch.draw(title, 70, 200);

				// Main buttons
				spriteBatch.draw(button_endless, 145, 110);
				spriteBatch.draw(button_challenge, 335, 110);

				// Small buttons
				spriteBatch.draw(button_x, 595, 5);
				spriteBatch.draw(button_settings, 5, 5);
				spriteBatch.draw(button_van, 50, 5);
				spriteBatch.draw(button_achievements, 95, 5);
				spriteBatch.draw(button_tutorial, 140, 5);

				// Button Down
				switch (buttonDown) {
					case 0:
					case 1:
						spriteBatch.draw(buttondown_big, 145 + 190 * buttonDown, 110);
						break;
					case 2:
					case 3:
					case 4:
					case 5:
						spriteBatch.draw(buttondown_small, 5 + 45 * (buttonDown - 2), 5);
						break;
					case 6:
						spriteBatch.draw(buttondown_small, 595, 5);
						break;
				}
				break;

			case SETTINGS:
				break;

		}

		spriteBatch.end();
		
	}

	@Override
	public void dispose() {
		super.dispose();

		spriteBatch.dispose();
		shapeRenderer.dispose();
	}

	public static Menus getCurrentMenu() {
		return currentMenu;
	}

	public static void setButtonDown(int buttonDown) {
		MenuScreen.buttonDown = buttonDown;
	}

	public static Game getGame() {
		return game;
	}
}
