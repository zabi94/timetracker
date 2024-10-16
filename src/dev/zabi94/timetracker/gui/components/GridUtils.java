package dev.zabi94.timetracker.gui.components;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class GridUtils {

	private static JPanel boxOf(int axis, Component... components) {
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, axis));
		
		for (Component c:components) jp.add(c);
		
		return jp;
	}
	
	public static JPanel rowOf(Component... components) {
		return boxOf(BoxLayout.LINE_AXIS, components);
	}
	
	public static JPanel columnOf(Component... components) {
		return boxOf(BoxLayout.PAGE_AXIS, components);
	}
	
}
