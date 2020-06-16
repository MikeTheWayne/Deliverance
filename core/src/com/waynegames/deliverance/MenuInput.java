package com.waynegames.deliverance;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class MenuInput extends InputAdapter {

	private OrthographicCamera orthographicCamera;

	MenuInput(OrthographicCamera orthographicCamera) {

		this.orthographicCamera = orthographicCamera;

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		switch (MenuScreen.getCurrentMenu()) {
			case MAIN:
				if(tY >= 110 && tY <= 190) {
					if(tX >= 145 && tX <= 305) {
						MenuScreen.setButtonDown(0);
					} else if(tX >= 335 && tX <= 495) {
						MenuScreen.setButtonDown(1);
					}
				} else if(tY <= 45) {
					if(tX <= 45) {
						MenuScreen.setButtonDown(2);
					} else if(tX >= 50 && tX <= 90) {
						MenuScreen.setButtonDown(3);
					} else if(tX >= 95 && tX <= 135) {
						MenuScreen.setButtonDown(4);
					} else if(tX >= 140 && tX <= 180) {
						MenuScreen.setButtonDown(5);
					} else if(tX >= 595) {
						MenuScreen.setButtonDown(6);
					}
				}
				break;

			case GAMEOVER:
				if(tX <= 45 && tY <= 45) {
					MenuScreen.setButtonDown(0);
				} else if(tX >= 260 && tX <= 380 && tY >= 5 && tY <= 65) {
					MenuScreen.setButtonDown(1);
				}
				break;

			case SETTINGS:
				break;
		}

		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		MenuScreen.setButtonDown(-1);

		touchDown(screenX, screenY, pointer, Input.Buttons.LEFT);

		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		MenuScreen.setButtonDown(-1);

		switch (MenuScreen.getCurrentMenu()) {
			case MAIN:
				if(tY >= 110 && tY <= 190) {
					if(tX >= 145 && tX <= 305) {
						MenuScreen.stopTimer();
						MenuScreen.getGame().setScreen(new GameScreen(MenuScreen.getGame(), GameMode.ENDLESS, 3, 3));
					} else if(tX >= 335 && tX <= 495) {
						MenuScreen.stopTimer();
						MenuScreen.getGame().setScreen(new GameScreen(MenuScreen.getGame(), GameMode.CHALLENGE, 3, 3));
					}
				} else if(tY <= 45) {
					if(tX <= 45) {

					} else if(tX >= 50 && tX <= 90) {

					} else if(tX >= 95 && tX <= 135) {

					} else if(tX >= 140 && tX <= 180) {

					} else if(tX >= 595) {
						Gdx.app.exit();
					}
				}
				break;

			case GAMEOVER:
				if(tX <= 45 && tY <= 45) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
				} else if(tX >= 260 && tX <= 380 && tY >= 5 && tY <= 65) {
					MenuScreen.stopTimer();
					MenuScreen.getGame().setScreen(new GameScreen(MenuScreen.getGame(), GameScreen.getGameMode(), 3, 3));
				}
				break;

			case SETTINGS:
				break;
		}

		return super.touchUp(screenX, screenY, pointer, button);
	}

}
