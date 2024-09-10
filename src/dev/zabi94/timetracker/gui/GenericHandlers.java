package dev.zabi94.timetracker.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GenericHandlers {
	
	public static final KeyListener AVOID_TABS = new KeyListener() {
		
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == '\t') {
				e.consume();
				e.getComponent().transferFocus();
				if (e.getComponent() instanceof JTextArea jta) {
					jta.setText(jta.getText().replaceAll("\t", ""));
				}
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
		}
	};	
	
	public static final FocusListener SELECT_ON_FOCUS = new FocusListener() {
		
		@Override
		public void focusLost(FocusEvent e) {
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			JTextField tf = (JTextField) e.getComponent();
			tf.select(0, tf.getText().length());
		}
	};
	
}
