package com.waynegames.deliverance;

import java.util.Random;

public class Street {

	private Random random = new Random();

	private int length;
	private int startX;
	private String name;

	Street(int startX) {

		this.length = random.nextInt(16) * 10 + 40;
		this.name = streetNameGenerator();
		this.startX = startX;

	}

	private String streetNameGenerator() {

		final String[] vowels = new String[] {"a", "e", "i", "o", "u"};
		final String[] consonants = new String[] {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"};

		StringBuilder stringBuilder = new StringBuilder();

		for(int i = 0; i < 3 + random.nextInt(10); i++) {
			stringBuilder.append(((i % 2 == 0) ? consonants[random.nextInt(consonants.length)] : vowels[random.nextInt(vowels.length)]));
		}

		return stringBuilder.toString();

	}

	public int getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	public int getStartX() {
		return startX;
	}

}
