package com.waynegames.deliverance;

import com.badlogic.gdx.utils.Timer;

public class GameThreads {
	static final int TICKS_PER_SECOND = 40;
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
				Street street = GameScreen.getStreet();

				// Accelerate van based on pressure on pedal
				// acceleration = (a - cx) - (a - p(a + F) / 10) - F, a = Current max acceleration of van, c = Current acceleration delay coefficient of van, p = pedal pressure, F = friction coefficient
				van.setSpeed(van.getSpeed() + (((van.getAcceleration() - van.getAccelDelayCoefficient() * van.getSpeed()) - (van.getAcceleration() - GameInput.getPedalPressure() * (van.getAcceleration() + FRICTION_COEFFICIENT) / 10f) - FRICTION_COEFFICIENT) / TICKS_PER_SECOND));

				// Move van based on speed
				van.setX(van.getX() + (van.getSpeed() * GameScreen.PIXELS_PER_METRE) / TICKS_PER_SECOND);

				// Parcels
				for(Parcel p : GameScreen.getParcels()) {
					if(p != null) {
						p.fly();
					}
				}

				// Street generation
				if(van.getX() - street.getStartX() > street.getLength() / 2f * 200) {
					GameScreen.setStreet(new Street( street.getStartX() + street.getLength() / 2 * 200 + 800));
				}

			}
		}, 0, 1f / TICKS_PER_SECOND);
	}

}
