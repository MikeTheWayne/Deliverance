package com.waynegames.deliverance;

public class Parcel {
	final int PARCEL_BASE_HEIGHT = 45;

	private float x, y, z;
	private float dx, vel;

	private int target;

	private boolean claimed = false;
	private boolean landed = false;

	Parcel(float dx, float vel, int target) {

		this.x = 256;
		this.y = PARCEL_BASE_HEIGHT;
		this.z = 0;

		this.dx = dx;
		this.vel = vel;

		this.target = target;

	}

	public void fly() {
		
		final float FLIGHT_TIME = 0.65f;

		if(z < 1) {
			z += 1 / FLIGHT_TIME / GameThreads.TICKS_PER_SECOND;
			x += (dx + vel) / GameThreads.TICKS_PER_SECOND;
			y = PARCEL_BASE_HEIGHT + GameScreen.PIXELS_PER_METRE * 2 * (5 * z - (9.81f * z * z) / 2);
		} else{
			this.landed = true;
			x -= GameScreen.getVanObj().getSpeed() * (float) GameScreen.PIXELS_PER_METRE / (float) GameThreads.TICKS_PER_SECOND;
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
}
