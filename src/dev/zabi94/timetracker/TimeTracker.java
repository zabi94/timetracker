package dev.zabi94.timetracker;

import java.sql.SQLException;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import dev.zabi94.timetracker.db.Data;
import dev.zabi94.timetracker.gui.windows.MainWindow;

public class TimeTracker {

	public static void main(String[] args) throws SQLException {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			if ("GTK+".equals(System.getProperty("tt_graphics"))) {
				for (LookAndFeelInfo lfi: UIManager.getInstalledLookAndFeels()) {
					if (lfi.getName().equals("GTK+")) {
						UIManager.setLookAndFeel(lfi.getClassName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Data.initDb();
		
		MainWindow.getInstance();
	}

}
