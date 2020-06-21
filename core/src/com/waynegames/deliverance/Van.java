package com.waynegames.deliverance;

public class Van {

	private float x;
	private float speed;

	private float acceleration;
	private float braking;

	Van () {
		this.x = 0;
		this.speed = 0;

		// 2.2 + 1.85 * log (x / 64 + 1)	60 <= x <= 100
		this.acceleration = 2f + Math.min(2.1f * (float) Math.log((MenuScreen.getLevel() - 1) / 64f + 1), 1.4f);
		// 6 + 5 * log (x / 50 + 1) 		6 <= x <= 9
		this.braking = 6f + Math.min(5f * (float) Math.log((MenuScreen.getLevel() - 1) / 50f + 1), 3f);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;

		if(speed < 0) {
			this.speed = 0;
		}
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public float getBraking() {
		return braking;
	}
}
