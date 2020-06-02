package com.waynegames.deliverance;

public class Van {

	private float x;
	private float speed;

	private float acceleration;
	private float accelDelayCoefficient;

	Van () {
		this.x = 0;
		this.speed = 0;

		this.acceleration = 2.0f;
		this.accelDelayCoefficient = 0.07f;
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

	public float getAccelDelayCoefficient() {
		return accelDelayCoefficient;
	}
}
