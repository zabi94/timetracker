package dev.zabi94.timetracker.gui;

import javax.swing.JOptionPane;

import dev.zabi94.timetracker.gui.windows.MainWindow;

public class ErrorHandler {

	public static void showErrorWindow(String message) {
		JOptionPane.showMessageDialog(MainWindow.getInstance(), message, "Errore", JOptionPane.ERROR_MESSAGE);
	}
}
