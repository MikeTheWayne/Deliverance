package com.waynegames.deliverance;

import com.badlogic.gdx.utils.Timer;

public class GameThreads {
	static final int TICKS_PER_SECOND = 30;
	static final float FRICTION_COEFFICIENT = 0.5f;

	private static Timer timer;

	private static boolean timeStarted = false;

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

				// Braking
				van.setSpeed(van.getSpeed() - (van.getBraking() * GameInput.getBrakePressure() / 10f) / TICKS_PER_SECOND);

				// Stop van when returned to warehouse
				if(van.getX() >= street.getStartX() + street.getLength() / 2f * 200 + 1680) {
					van.setSpeed(0);

					if(GameScreen.getGameMode() == GameMode.ENDLESS && GameScreen.getParcelsLeft() > 0) {
						GameScreen.setLivesLeft(GameScreen.getLivesLeft() - GameScreen.getParcelsLeft());
					}
				}

				// Move van based on speed
				van.setX(van.getX() + (van.getSpeed() * GameScreen.PIXELS_PER_METRE) / TICKS_PER_SECOND);

				// Parcels
				for(Parcel p : GameScreen.getParcels()) {
					if(p != null) {
						p.fly();

						if(p.isLanded() && !p.isClaimed()) {

							System.out.println((Math.floor((van.getX() - street.getStartX() + p.getX()) / 100f) + 1) + " " + p.getTarget());

							if(p.getTarget() == Math.floor((van.getX() - street.getStartX() + p.getX()) / 100f) + 1) {
								GameScreen.incrementScore(100);
							} else{
								GameScreen.incrementScore(-250);

								if(GameScreen.getGameMode() == GameMode.ENDLESS) {
									GameScreen.setLivesLeft(GameScreen.getLivesLeft() - 1);
								}
							}

							p.setClaimed();
						}
					}
				}

				// Remove parcel from manifest if passed
				if(GameScreen.getStreet().getTargets().size() > 0) {
					if(street.getTargets().get(0) < ((van.getX() - street.getStartX()) / 200f) * 2 + 1 && GameScreen.getHour() < 21) {
						GameScreen.removeTarget();
						GameScreen.incrementScore(-250);

						if(GameScreen.getGameMode() == GameMode.ENDLESS) {
							GameScreen.setLivesLeft(GameScreen.getLivesLeft() - 1);
						}
					}
				}

				// Street generation
				if(GameScreen.getHour() == 21 || GameScreen.getParcelsLeft() == 0) {
					GameScreen.setDayEnd(true);
				} else if(van.getX() - street.getStartX() > street.getLength() / 2f * 200) {
					GameScreen.setStreet(new Street( street.getStartX() + street.getLength() / 2 * 200 + 800));
				}

				// Black screen opacity
				if(van.getX() >= street.getStartX() + street.getLength() / 2f * 200 + 1680 || GameScreen.isGameOver()) {
					if(GameScreen.getBlackScreenOpacity() < 1) {
						GameScreen.setBlackScreenOpacity(GameScreen.getBlackScreenOpacity() + 1f / (TICKS_PER_SECOND / 2f));
					} else if(GameScreen.isGameOver()) {
						// Return to menu
					} else if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
						GameScreen.incrementDay();
					}

				} else if(GameScreen.getBlackScreenOpacity() > 0 && GameScreen.getHour() < 21) {
					GameScreen.setBlackScreenOpacity(GameScreen.getBlackScreenOpacity() - 1f / (TICKS_PER_SECOND / 2f));
				}

			}
		}, 0, 1f / TICKS_PER_SECOND);
	}

	private static void secondThread() {
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {

				// Time
				if(GameScreen.getHour() < 21 && timeStarted) {
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

	public static void setTimeStarted(boolean timeStarted) {
		GameThreads.timeStarted = timeStarted;
	}
}
