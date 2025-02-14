package com.waynegames.deliverance;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameInput extends InputAdapter {

	private OrthographicCamera orthographicCamera;

	private static float pedalPressure, brakePressure;
	private int pedalPressurePointer, parcelThrowPointer;

	private float initX, initY;

	GameInput(OrthographicCamera orthographicCamera) {

		this.orthographicCamera = orthographicCamera;

		pedalPressure = 0;
		this.pedalPressurePointer = -1;
		this.parcelThrowPointer = -1;

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		if(!GameScreen.isGameOver() && GameScreen.getBlackScreenOpacity() >= 1f) {

			if(tX < 100 && tY > 315) {
				// Save game
				GameScreen.save();

				// Return to menu
				GameThreads.stop();
				GameScreen.stopMusic();
				GameScreen.disposeMusic();
				GameScreen.getGame().setScreen(new MenuScreen(GameScreen.getGame(), Menus.MAIN));
			}

			if(GameScreen.getGameMode() == GameMode.ENDLESS) {

				// Contract selection
				if (tY >= 75 && tY <= 225) {
					if (tX >= 110 && tX <= 230) {
						GameScreen.selectContract(0);
						GameScreen.resetHardContractsInARow();
					} else if (tX >= 260 && tX <= 380) {
						GameScreen.selectContract(1);
						GameScreen.resetHardContractsInARow();
					} else if (tX >= 410 && tX <= 530) {
						GameScreen.selectContract(2);
						GameScreen.incrementHardContractsInARow();
					}
				}

			} else if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
				GameScreen.incrementDay();
			}

		} else {
			// Main game

			if(GameScreen.isTutorial()) {
				GameScreen.nextTutorial();
			} else {
				if (tX < 90 && tY < 160) {
					pedalPressure = Math.min(tY / 13f, 10f);
					this.pedalPressurePointer = pointer;
					GameThreads.setTimeStarted(true);
					GameScreen.playMusic();
				} else if (tX < 160 && tY < 120) {
					brakePressure = Math.min(tY / 9f, 10f);
					this.pedalPressurePointer = pointer;
				} else if ((tX >= 200 && tX <= 450 && tY <= 120) || (MenuScreen.isTapThrow() && (tX > 160 || tY > 160))) {
					this.initX = tX;
					this.initY = tY;
					this.parcelThrowPointer = pointer;
				}
			}

		}

		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		if(!GameScreen.isTutorial()) {
			if (tX < 90 && tY < 160 && pointer == pedalPressurePointer) {
				pedalPressure = Math.min(tY / 13f, 10f);
				brakePressure = 0;
			} else if (tX < 160 && tY < 120 && pointer == pedalPressurePointer) {
				brakePressure = Math.min(tY / 9f, 10f);
				pedalPressure = 0;
			}
		}

		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		if(pointer == pedalPressurePointer) {
			pedalPressure = 0;
			brakePressure = 0;
			this.pedalPressurePointer = -1;
		} else if (pointer == parcelThrowPointer) {

			float fingerDistance = (float) Math.sqrt(Math.pow(initX - tX, 2) + Math.pow(initY - tY, 2));

			// Throw parcel
			if(GameScreen.getHour() < 21 && GameScreen.getStreet().getTargets().size() > 0 && GameScreen.getParcelsLeft() > 0 && (fingerDistance > 10 || MenuScreen.isTapThrow())) {
				for (int i = 0; i < GameScreen.getParcels().length; i++) {
					if (GameScreen.getParcels()[i] == null || GameScreen.getParcels()[i].getX() < -100 || i == GameScreen.getParcels().length - 1) {
						GameScreen.setParcel(new Parcel((MenuScreen.isTapThrow()) ? 0 : tX - initX, GameScreen.getVanObj().getSpeed(), GameScreen.getStreet().getTargets().get(0)), i);
						break;
					}
				}

				GameScreen.removeTarget();
			}

			this.parcelThrowPointer = -1;

		}

		return super.touchUp(screenX, screenY, pointer, button);
	}

	public static float getPedalPressure() {
		return pedalPressure;
	}

	public static float getBrakePressure() {
		return brakePressure;
	}
}
