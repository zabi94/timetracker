package dev.zabi94.timetracker.gui.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.GenericHandlers;
import dev.zabi94.timetracker.utils.Utils;

public class DatePicker extends JDialog {

	private static final long serialVersionUID = -7545484154639587086L;
	private static final int DW = 200;
	private static final int DH = 130;

	private final JTextField day = new JTextField();
	private final JTextField month = new JTextField();
	private final JTextField year = new JTextField();
	private final JButton confirm = new JButton("Vai");
	private final JLabel label = new JLabel("Data:");
	

	private DatePicker(SimpleDate sd, String title, String buttonLabel, ResultConsumer action) {
		this.setLayout(new GridBagLayout());
		this.setTitle("Seleziona data");
		this.setResizable(false);
		this.setBounds(Utils.positionInMiddleOfMainWindow(DW, DH));

		Utils.setOnCloseBehaviour(this, () -> action.onSelection(Optional.empty()));
		
		Action go = new AbstractAction("Vai") {
			
			private static final long serialVersionUID = -3139366553620929845L;

			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					int dayVal = Integer.parseInt(day.getText());
					int monthVal = Integer.parseInt(month.getText());
					int yearVal = Integer.parseInt(year.getText());
					
					setVisible(false);
					dispose();
					SimpleDate date = new SimpleDate(dayVal, monthVal, yearVal);
					action.onSelection(Optional.of(date));
				} catch (Exception e) {
					ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
				}
			}
		};
		
		this.rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "submit");
		this.rootPane.getActionMap().put("submit", go);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 3;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(label, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.gridx = 0;
		c.insets = new Insets(0, 10, 10, 0);
		c.weightx = 1;
		day.setText(sd.day()+"");
		this.add(day, c);
		day.addFocusListener(GenericHandlers.SELECT_ON_FOCUS);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 10, 10, 0);
		c.weightx = 1;
		month.setText(sd.month()+"");
		this.add(month, c);
		month.addFocusListener(GenericHandlers.SELECT_ON_FOCUS);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.gridx = 2;
		c.insets = new Insets(0, 10, 10, 10);
		c.weightx = 2;
		year.setText(sd.year()+"");
		this.add(year, c);
		year.addFocusListener(GenericHandlers.SELECT_ON_FOCUS);
		
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 2;
		c.gridx = 2;
		c.insets = new Insets(0, 10, 10, 10);
		this.add(confirm, c);
		
		confirm.setAction(go);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setVisible(true);
		
	}
	
	public static void prompt(SimpleDate sd, String title, String buttonLabel, ResultConsumer action) {
		new DatePicker(sd, title, buttonLabel, action);
	}
	
	
	@FunctionalInterface
	public static interface ResultConsumer {
		
		public void onSelection(Optional<SimpleDate> selection);
		
	}
	
	
}
