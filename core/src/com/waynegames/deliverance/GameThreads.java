package com.waynegames.deliverance;

import com.badlogic.gdx.utils.Timer;

public class GameThreads {
	static final int TICKS_PER_SECOND = 30;
	static final float FRICTION_COEFFICIENT = 0.5f;

	private static Timer timer;

	static void run() {

		timer = new Timer();

		mainThread();
		secondThread();

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

				// Stop van when returned to warehouse
				if(van.getX() >= street.getStartX() + street.getLength() / 2f * 200 + 1680) {
					van.setSpeed(0);
				}

				// Move van based on speed
				van.setX(van.getX() + (van.getSpeed() * GameScreen.PIXELS_PER_METRE) / TICKS_PER_SECOND);

				// Parcels
				for(Parcel p : GameScreen.getParcels()) {
					if(p != null) {
						p.fly();
					}
				}

				// Street generation
				if(GameScreen.getHour() == 21) {
					GameScreen.setDayEnd(true);
				} else if(van.getX() - street.getStartX() > street.getLength() / 2f * 200) {
					GameScreen.setStreet(new Street( street.getStartX() + street.getLength() / 2 * 200 + 800));
				}

			}
		}, 0, 1f / TICKS_PER_SECOND);
	}

	private static void secondThread() {
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {

				// Time
				if(GameScreen.getHour() < 21) {
					if (GameScreen.getMinute() == 55) {
						GameScreen.setHour(GameScreen.getHour() + 1);
						GameScreen.setMinute(0);
					} else {
						GameScreen.setMinute(GameScreen.getMinute() + 5);
					}
				}


			}
		}, 0, 1f);
	}

}
