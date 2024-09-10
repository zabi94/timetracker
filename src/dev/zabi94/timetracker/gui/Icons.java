package dev.zabi94.timetracker.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {
	
	private static final int ICON_SIZE = 20;

	public static final Icon ARROW_LEFT = fromResource("/icons/arrow_left.png", ICON_SIZE);
	public static final Icon ARROW_RIGHT = fromResource("/icons/arrow_right.png", ICON_SIZE);
	public static final Icon NEW_ACTIVITY = fromResource("/icons/new_activity.png", ICON_SIZE);
	public static final Icon EXPORT = fromResource("/icons/export.png", ICON_SIZE);
	public static final Icon SAVE = fromResource("/icons/save.png", ICON_SIZE);
	public static final Icon SAVE_AND_CLOSE = fromResource("/icons/save_close.png", ICON_SIZE);
	public static final Icon MULTI = fromResource("/icons/multi.png", ICON_SIZE);
	public static final Icon LINK = fromResource("/icons/link.png", ICON_SIZE);
	public static final Icon EDIT = fromResource("/icons/edit.png", ICON_SIZE);
	
	private static ImageIcon fromResource(String resource, int size) {
		
		Image img = null;
		
		try {
			img = ImageIO.read(Icons.class.getResourceAsStream(resource));
		} catch (IOException e) {
			e.printStackTrace();
			img = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
			img.getGraphics().setColor(Color.RED);
			img.getGraphics().fillRect(0, 0, size, size);
		}
		return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}
	
}
