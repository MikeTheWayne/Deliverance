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
		loadAssets();

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
	private void loadAssets() {

		assetManager = new AssetManager();

		FileHandle assetsToLoad = Gdx.files.internal("");

		for(FileHandle f : assetsToLoad.list()) {

			if(f.name().contains(".png")) {
				assetManager.load(f.name(), Texture.class);
			}

		}

	}

}
