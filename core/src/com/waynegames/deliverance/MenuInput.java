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
					} else if(tX >= 540 && tX <= 590) {
						MenuScreen.setButtonDown(6);
					} else if(tX >= 595) {
						MenuScreen.setButtonDown(7);
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
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setButtonDown(0);
				}
				break;

			case CUSTOMISATION:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setButtonDown(0);
				}

				if(tX >= 20 && tX <= 620 && tY >= 10 && tY <= 170) {
					MenuScreen.setVanSelected((int) (((tX - 20) / 150) + 4 * Math.ceil((80 - (tY - 10)) / 80)));

				}
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
						MenuScreen.setTargetMode(GameMode.ENDLESS);
					} else if(tX >= 335 && tX <= 495) {
						MenuScreen.setTargetMode(GameMode.CHALLENGE);
					}
				} else if(tY <= 45) {
					if(tX <= 45) {
						MenuScreen.setCurrentMenu(Menus.SETTINGS);
					} else if(tX >= 50 && tX <= 90) {
						MenuScreen.setCurrentMenu(Menus.CUSTOMISATION);
					} else if(tX >= 95 && tX <= 135) {
						// Achievements
					} else if(tX >= 140 && tX <= 180) {
						// Leaderboards
					} else if(tX >= 540 && tX <= 590) {

					} else if(tX >= 595) {
						Gdx.app.exit();
					}
				}
				break;

			case GAMEOVER:
				if(tX <= 45 && tY <= 45) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
				} else if(tX >= 260 && tX <= 380 && tY >= 5 && tY <= 65) {
					MenuScreen.setTargetMode(GameScreen.getGameMode());
				}
				break;

			case SETTINGS:
			case CUSTOMISATION:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
				}
				break;
		}

		return super.touchUp(screenX, screenY, pointer, button);
	}

}
