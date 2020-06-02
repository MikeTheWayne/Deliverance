package com.waynegames.deliverance;

import com.badlogic.gdx.utils.Timer;

public class GameThreads {
	static final int TICKS_PER_SECOND = 30;
	static final float FRICTION_COEFFICIENT = 0.5f;

	private static Timer timer;

	static void run() {

		timer = new Timer();

		mainThread();

	}

	private static void mainThread() {
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {

				Van van = GameScreen.getVanObj();

				// Accelerate van based on pressure on pedal
				// acceleration = (a - cx) - (a - p(a + F) / 10) - F, a = Current max acceleration of van, c = Current acceleration delay coefficient of van, p = pedal pressure, F = friction coefficient
				van.setSpeed(van.getSpeed() + (((van.getAcceleration() - van.getAccelDelayCoefficient() * van.getSpeed()) - (van.getAcceleration() - GameInput.getPedalPressure() * (van.getAcceleration() + FRICTION_COEFFICIENT) / 10f) - FRICTION_COEFFICIENT) / TICKS_PER_SECOND));

				// Move van based on speed
				van.setX(van.getX() + (van.getSpeed() * GameScreen.PIXELS_PER_METRE) / TICKS_PER_SECOND);

			}
		}, 0, 1f / TICKS_PER_SECOND);
	}

}
