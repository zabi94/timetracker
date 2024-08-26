package dev.zabi94.timetracker.utils;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
	
	public static void copyText(String text) {
		StringSelection cp = new StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(cp, cp);
	}
	
	
	public static void setOnCloseBehaviour(Window window, Runnable onClosed) {
		window.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				onClosed.run();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}
	
}
