package com.waynegames.deliverance;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameInput extends InputAdapter {

	private OrthographicCamera orthographicCamera;

	private static float pedalPressure;
	private int pedalPressurePointer;

	GameInput(OrthographicCamera orthographicCamera) {

		this.orthographicCamera = orthographicCamera;

		pedalPressure = 0;
		this.pedalPressurePointer = -1;

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		if(tX < 90 && tY < 130) {
			pedalPressure = tY / 13f;
			this.pedalPressurePointer = pointer;
		}

		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		Vector3 input = new Vector3(screenX, screenY, 0);

		this.orthographicCamera.unproject(input);

		float tX = input.x;
		float tY = input.y;

		if(tX < 90 && tY < 130 && pointer == pedalPressurePointer) {
			pedalPressure = tY / 13f;
		}

		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		if(pointer == pedalPressurePointer) {
			pedalPressure = 0;
			this.pedalPressurePointer = -1;
		}

		return super.touchUp(screenX, screenY, pointer, button);
	}

	public static float getPedalPressure() {
		return pedalPressure;
	}

}
