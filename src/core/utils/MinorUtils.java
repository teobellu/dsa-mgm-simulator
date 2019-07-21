package core.utils;

import java.awt.Color;

public class MinorUtils {
	
	public static void sleepFor(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}

	public static String colorToString(Color color) {
		if (color.getRed() == 255 && color.getGreen() == 0 && color.getBlue() == 0)
			return "red";
		else if (color.getRed() == 0 && color.getGreen() == 255 && color.getBlue() == 0)
			return "green";
		else if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 255)
			return "blue";
		else if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 0)
			return "yellow";
		return "undef-color";
	}
}
