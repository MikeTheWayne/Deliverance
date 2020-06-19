package com.waynegames.deliverance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class MenuInput extends InputAdapter {

	private OrthographicCamera orthographicCamera;

	private boolean dragged;

	MenuInput(OrthographicCamera orthographicCamera) {

		this.orthographicCamera = orthographicCamera;

		dragged = false;

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

				// Sliders
				if(tX >= 10 && tX <= 470) {
					if(tY >= 240 && tY <= 270) {
						MenuScreen.setSoundVolume((tX - 40) / 400);
					} else if(tY >= 160 && tY <= 190) {
						MenuScreen.setMusicVolume((tX - 40) / 400);
					}
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

			case TUTORIAL:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setButtonDown(0);
				} else {
					if(!dragged) {
						MenuScreen.setTutorialScreen(MenuScreen.getTutorialScreen() + 1);
					}
				}
		}

		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		dragged = true;

		MenuScreen.setButtonDown(-1);

		touchDown(screenX, screenY, pointer, Input.Buttons.LEFT);

		dragged = false;

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
						MenuScreen.setCurrentMenu(Menus.TUTORIAL);
					} else if(tX >= 595) {
						Gdx.app.exit();
					}
				}
				break;

			case GAMEOVER:
				if(tX <= 45 && tY <= 45) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
					MenuScreen.save();
				} else if(tX >= 260 && tX <= 380 && tY >= 5 && tY <= 65) {
					MenuScreen.setTargetMode(GameScreen.getGameMode());
					MenuScreen.save();
				}
				break;

			case SETTINGS:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
					MenuScreen.save();
				}

				if(tX >= 40 && tX <= 70 && tY >= 100 && tY <= 130) {
					MenuScreen.invertKmph();
				}
				break;

			case CUSTOMISATION:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
					MenuScreen.save();
				}
				break;

			case TUTORIAL:
				if(tX <= 45 && tY >= 315) {
					MenuScreen.setCurrentMenu(Menus.MAIN);
					MenuScreen.setTutorialScreen(1);
				}
				break;
		}

		return super.touchUp(screenX, screenY, pointer, button);
	}

}
