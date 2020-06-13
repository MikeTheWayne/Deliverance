package com.waynegames.deliverance;

import java.util.Random;

public class Contract {

	private int parcels;
	private float density;
	private float scoreMultiplier;
	private float failureMultiplier;

	public Contract(int day, int difficulty) {

		Random random = new Random();

		float targetSpeed = 1f;

		switch (GameScreen.getGameMode()) {

			case ENDLESS:
				// 25 * Log(0.4 * x + 1) + 20
				targetSpeed = (25 - difficulty * 5) * (float) Math.log(0.4f * day + 1) + 20 + 10 * difficulty;
				break;

			case CHALLENGE:
				break;

		}

		density = (float) Math.floor((random.nextGaussian() * 0.225f + 1.5f) * 100) / 100f;
		parcels = (int) (0.6f * targetSpeed * density);
		scoreMultiplier = 1f;
		failureMultiplier = 1f;

	}

	public int getParcels() {
		return parcels;
	}

	public float getDensity() {
		return density;
	}

	public float getScoreMultiplier() {
		return scoreMultiplier;
	}

	public float getFailureMultiplier() {
		return failureMultiplier;
	}

}
