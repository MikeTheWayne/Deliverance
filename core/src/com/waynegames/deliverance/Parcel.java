package com.waynegames.deliverance;

public class Parcel {
	final int PARCEL_BASE_HEIGHT = 45;

	private float x, y, z;
	private float dx, vel;

	private int target;

	private boolean claimed;
	private boolean landed;

	private String scoreText;
	private float scoreTextY;

	Parcel(float dx, float vel, int target) {

		this.x = 256;
		this.y = PARCEL_BASE_HEIGHT;
		this.z = 0;

		this.dx = dx;
		this.vel = vel;

		this.target = target;

		this.claimed = false;
		this.landed = false;

		this.scoreText = "";
		this.scoreTextY = 0f;

	}

	public void fly(float delta) {
		
		final float FLIGHT_TIME = 0.65f;

		if(z < 1) {
			z += 1 / FLIGHT_TIME * delta;
			x += (dx + vel) * delta;
			y = PARCEL_BASE_HEIGHT + GameScreen.PIXELS_PER_METRE * 2 * (5 * z - (9.81f * z * z) / 2);
		} else{
			this.landed = true;
			x -= GameScreen.getVanObj().getSpeed() * (float) GameScreen.PIXELS_PER_METRE * delta;

			if(scoreTextY < 50) {
				scoreTextY += delta * 50f;
			}
		}

	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public int getTarget() {
		return target;
	}

	public boolean isLanded() {
		return landed;
	}

	public boolean isClaimed() {
		return claimed;
	}

	public void setClaimed() {
		this.claimed = true;
	}

	public void setScoreText(String scoreText) {
		this.scoreText = scoreText;
	}

	public String getScoreText() {
		return scoreText;
	}

	public float getScoreTextY() {
		return scoreTextY;
	}
}
