package com.waynegames.deliverance;

import com.badlogic.gdx.utils.Timer;

public class GameThreads {
	static final int TICKS_PER_SECOND = 30;
	static final float FRICTION_COEFFICIENT = 0.5f;
	static final float ACCELERATION_DELAY_COEFFICIENT = 0.07f;

	private static Timer timer;

	private static boolean timeStarted = false;

	static void run() {

		timer = new Timer();

		mainThread();
		secondThread();

	}

	static void stop() {

		timer.stop();

	}

	private static void mainThread() {
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {

				Van van = GameScreen.getVanObj();
				Street street = GameScreen.getStreet();

				// Accelerate van based on pressure on pedal
				// acceleration = (a - cx) - (a - p(a + F) / 10) - F, a = Current max acceleration of van, c = Current acceleration delay coefficient of van, p = pedal pressure, F = friction coefficient
				van.setSpeed(van.getSpeed() + (((van.getAcceleration() - ACCELERATION_DELAY_COEFFICIENT * van.getSpeed()) - (van.getAcceleration() - GameInput.getPedalPressure() * (van.getAcceleration() + FRICTION_COEFFICIENT) / 10f) - FRICTION_COEFFICIENT) / TICKS_PER_SECOND));

				// Braking
				van.setSpeed(van.getSpeed() - (van.getBraking() * GameInput.getBrakePressure() / 10f) / TICKS_PER_SECOND);

				// Stop van when returned to warehouse
				if(van.getX() >= street.getStartX() + street.getLength() / 2f * 200 + 1680) {
					van.setSpeed(0);

					if(GameScreen.getGameMode() == GameMode.ENDLESS && GameScreen.getParcelsLeft() > 0) {
						GameScreen.setLivesLeft(GameScreen.getLivesLeft() - GameScreen.getParcelsLeft());
					}
				}

				// Parcels
				for(Parcel p : GameScreen.getParcels()) {
					if(p != null) {
						if(p.isLanded() && !p.isClaimed()) {

							if(p.getTarget() == Math.floor((van.getX() - street.getStartX() + p.getX() + 6) / 100f) + 1) {
								GameScreen.incrementScore((int) Math.ceil(100 * GameScreen.getScoreMultiplier()) + GameScreen.getStreak());
								GameScreen.incrementParcelsHit();

								p.setScoreText("+" + (int) Math.ceil(100 * GameScreen.getScoreMultiplier()));

								if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
									GameScreen.incrementStreak();
								}
							} else{

								if(GameScreen.getGameMode() == GameMode.ENDLESS) {
									GameScreen.setLivesLeft(GameScreen.getLivesLeft() - 1);
								} else if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
									GameScreen.resetStreak();
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

						if(GameScreen.getGameMode() == GameMode.ENDLESS) {
							GameScreen.setLivesLeft(GameScreen.getLivesLeft() - 1);
						} else if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
							GameScreen.resetStreak();
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

						if(GameScreen.getGameMode() == GameMode.CHALLENGE) {
							GameScreen.checkChallengeGameOver();
						}
					} else if(GameScreen.isGameOver()) {
						// Return to menu
						GameThreads.stop();
						GameScreen.stopMusic();
						GameScreen.disposeMusic();
						GameScreen.getGame().setScreen(new MenuScreen(GameScreen.getGame(), Menus.GAMEOVER));
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

					GameScreen.calculateAverageSpeed();
				}


			}
		}, 0, 1f);
	}

	public static void setTimeStarted(boolean timeStarted) {
		GameThreads.timeStarted = timeStarted;
	}

	public static boolean isTimeStarted() {
		return timeStarted;
	}
}
