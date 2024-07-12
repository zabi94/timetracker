package dev.zabi94.timetracker.utils;

import java.awt.Rectangle;

import dev.zabi94.timetracker.gui.windows.MainWindow;

public class Utils {

	public static String quartersToTime(int quarters) {
		int hours = quarters / 4;
		String qts = "" + (15 * (quarters % 4));
		if (qts.length() < 2) qts = "00" + qts;
		qts = qts.substring(qts.length() - 2);
		return String.format("%d:%sh", hours, qts);
	}
	
	public static Rectangle positionInMiddleOfMainWindow(int w, int h) {
		Rectangle mw = MainWindow.getInstance().getBounds();
		return new Rectangle(mw.x + ((mw.width - w) / 2), mw.y + ((mw.height - h) / 2), w, h);
	}
	
}
