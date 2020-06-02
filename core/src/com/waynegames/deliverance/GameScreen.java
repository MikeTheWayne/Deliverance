package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

	private Sprite van, box, house, fence, road;
	private Sprite[] pedals;

	private static Van vanObj;

	GameScreen(Deliverance game) {

		this.game = game;

		vanObj = new Van();

		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.orthographicCamera = new OrthographicCamera(640, 360);

		this.orthographicCamera.position.set(this.orthographicCamera.viewportWidth / 2f, this.orthographicCamera.viewportHeight / 2f, 0);
		this.orthographicCamera.update();

		// Load game sprites
		this.van = new Sprite(Deliverance.assetManager.get("game_sprites/van_01.png", Texture.class));
		this.box = new Sprite(Deliverance.assetManager.get("game_sprites/box.png", Texture.class));
		this.house = new Sprite(Deliverance.assetManager.get("game_sprites/house_01.png", Texture.class));
		this.fence = new Sprite(Deliverance.assetManager.get("game_sprites/fence_01.png", Texture.class));
		this.road = new Sprite(Deliverance.assetManager.get("game_sprites/road.png", Texture.class));

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

		// Houses & fences
		for(int x = 0; x < 5; x++) {
			spriteBatch.draw(house, x * 200 - (vanObj.getX() % 200), 32);
			spriteBatch.draw(fence, x * 200 - (vanObj.getX() % 200), 32);
		}

		// Van
		spriteBatch.draw(van, 256, 3);

		// UI
		// Pedals
		spriteBatch.draw(pedals[(int) (GameInput.getPedalPressure() / 10f * 4)], 10, 10, 80, 120 - 60 * (GameInput.getPedalPressure() / 10f));

		spriteBatch.end();

	}

	public static Van getVanObj() {
		return vanObj;
	}
}
