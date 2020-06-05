package com.waynegames.deliverance;

import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class ShopBanner {

	private String name;

	private Color foregroundColour;
	private Color backgroundColour;

	ShopBanner(String streetName) {

		final String[] shopType = new String[] {"FOOD & WINE", "KEBABS", "BARBERS", "NEWSAGENT", "OFF LICENCE", "CONVENIENCE", "STORES", "SUPERMARKET", "BAKERY", "GROCERIES", "SALON", "PHARMACY", "TAKEAWAY"};

		Random random = new Random();

		name = streetName.toUpperCase() + " " + shopType[random.nextInt(shopType.length)];

		if(random.nextInt(2) == 0) {
			foregroundColour = new Color(random.nextInt(128) / 255f, random.nextInt(128) / 255f, random.nextInt(128) / 255f, 1f);
			backgroundColour = new Color(1f, 1f, 1f, 1f);
		} else{
			foregroundColour = new Color(1f, 1f, 1f, 1f);
			backgroundColour = new Color(random.nextInt(128) / 255f, random.nextInt(128) / 255f, random.nextInt(128) / 255f, 1f);
		}

	}

	public String getName() {
		return name;
	}

	public Color getForegroundColour() {
		return foregroundColour;
	}

	public Color getBackgroundColour() {
		return backgroundColour;
	}

}
