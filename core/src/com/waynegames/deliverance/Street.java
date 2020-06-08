package com.waynegames.deliverance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Street {

	private Random random = new Random();

	private int length;
	private int startX;
	private String name;

	private List<Integer> targets;

	Street(int startX) {

		this.length = random.nextInt(16) * 10 + 40;
		this.name = streetNameGenerator();
		this.startX = startX;

		this.targets = generateTargets(Math.min((int) (GameScreen.getParcelDensity() * length / 10f), GameScreen.getParcelsLeft()));

	}

	private String streetNameGenerator() {

		final String[] vowels = new String[] {"a", "e", "i", "o", "u"};
		final String[] consonants = new String[] {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"};

		StringBuilder stringBuilder = new StringBuilder();

		int nameLength = 3 + random.nextInt(5);
		for(int i = 0; i < nameLength; i++) {
			stringBuilder.append(((i % 2 == 0) ? consonants[random.nextInt(consonants.length)] : vowels[random.nextInt(vowels.length)]));
		}

		return stringBuilder.toString();

	}

	private ArrayList<Integer> generateTargets(int parcels) {

		ArrayList<Integer> returnList = new ArrayList<>();

		for(int i = 0; i < this.length; i++) {
			if(random.nextInt(this.length - i) < parcels) {
				returnList.add(i);
				parcels--;
			}
		}

		return returnList;

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
