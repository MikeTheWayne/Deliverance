package com.waynegames.deliverance;

import java.util.Random;

public class Contract {

	private int parcels;
	private float density;
	private float scoreMultiplier;

	public Contract(int day, int difficulty) {

		Random random = new Random();

		// 25 * Log(0.4x + 1) + 20
		float targetSpeed = (25 - difficulty * 5) * (float) Math.log(0.4f * day + 1) + 20 + 10 * difficulty;
		// 0.02x + 1
		float targetScoreM = (0.02f + 0.01f * difficulty) * day + 1 + 0.25f * difficulty;

		density = (float) random.nextGaussian() * 0.225f + 1.5f;
		parcels = (int) (0.6f * targetSpeed * density);
		scoreMultiplier = random.nextFloat() * targetScoreM * 0.2f + targetScoreM * 0.9f;

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

}
