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

public class MenuScreen extends ScreenAdapter {

	private Game game;

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private OrthographicCamera orthographicCamera;

	private Sprite background, van;

	public MenuScreen(Deliverance game) {

		this.game = game;

		// Graphics
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		this.orthographicCamera = new OrthographicCamera(640, 360);

		this.orthographicCamera.position.set(this.orthographicCamera.viewportWidth / 2f, this.orthographicCamera.viewportHeight / 2f, 0);
		this.orthographicCamera.update();

		// Load menu sprites
		this.background = new Sprite(Deliverance.assetManager.get("menu_sprites/menu_background.png", Texture.class));
		this.van = new Sprite(Deliverance.assetManager.get("game_sprites/van_01.png", Texture.class));

	}

	@Override
	public void render(float delta) {
		super.render(delta);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		Gdx.gl.glClearColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);

		shapeRenderer.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);

		// Draw background
		spriteBatch.begin();
		spriteBatch.draw(background, 0, 0);
		spriteBatch.end();
		
	}

	@Override
	public void dispose() {
		super.dispose();

		spriteBatch.dispose();
		shapeRenderer.dispose();
	}
}
