package dev.zabi94.timetracker.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.Iterator;

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
	
	public static void setOnClickBehaviour(Component component, Runnable onClicked) {
		component.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				onClicked.run();
			}
		});
	}
	
	public static void setOnDoubleClickBehaviour(Component component, Runnable onDoubleClicked) {
		component.addMouseListener(new MouseListener() {
			
			long lastClickTime = 0;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				long now = System.currentTimeMillis();
				if (now - lastClickTime < 400) {
					onDoubleClicked.run();
				}
				lastClickTime = now;
			}
		});
	}
	
	public static void setCloseOnLostFocus(Window window) {
		window.setAlwaysOnTop(true);
		window.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				window.setVisible(false);
				window.dispose();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				//NO-OP
			}
		});
	}

	public static Color moduleIntensity(Color from, float amount) {
		int r = from.getRed();
		int g = from.getGreen();
		int b = from.getBlue();
		
		r = Math.min((int) (r * amount), 255);
		g = Math.min((int) (g * amount), 255);
		b = Math.min((int) (b * amount), 255);
		
		return new Color(r, g, b);
	}
	
	public static String implode(String separator, Iterable<?> stuff) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = stuff.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	
}
