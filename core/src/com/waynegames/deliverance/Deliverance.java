package com.waynegames.deliverance;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class Deliverance extends Game {

	static AssetManager assetManager;
	
	@Override
	public void create () {

		// Load image assets
		loadAssets(new String[] {"game_sprites/", "menu_sprites/"});

		// Load the game screen
		//setScreen(new GameScreen(this, GameMode.ENDLESS, 3, 3));
		setScreen(new MenuScreen(this));

		// Start game threads
		GameThreads.run();

	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}

	/**
	 * Loops through the files in the assets folder, and loads all of the ".png" images
	 */
	private void loadAssets(String[] folders) {

		assetManager = new AssetManager();

		for(String s : folders) {
			FileHandle assetsToLoad = Gdx.files.internal(s);

			for (FileHandle f : assetsToLoad.list()) {

				if (f.name().contains(".png")) {
					assetManager.load(f.path(), Texture.class);
				}

			}
		}

		assetManager.finishLoading();

	}

}
