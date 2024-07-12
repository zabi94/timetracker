package dev.zabi94.timetracker.gui.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.gui.ErrorHandler;
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
	

	public DatePicker(SimpleDate sd) {
		this.setLayout(new GridBagLayout());
		this.setTitle("Seleziona data");
		this.setResizable(false);
		this.setBounds(Utils.positionInMiddleOfMainWindow(DW, DH));
		
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
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 10, 10, 0);
		c.weightx = 1;
		month.setText(sd.month()+"");
		this.add(month, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.gridx = 2;
		c.insets = new Insets(0, 10, 10, 10);
		c.weightx = 2;
		year.setText(sd.year()+"");
		this.add(year, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 2;
		c.gridx = 2;
		c.insets = new Insets(0, 10, 10, 10);
		this.add(confirm, c);
		

		confirm.addActionListener(evt -> {
			try {
				int dayVal = Integer.parseInt(day.getText());
				int monthVal = Integer.parseInt(month.getText());
				int yearVal = Integer.parseInt(year.getText());
				
				this.setVisible(false);
				this.dispose();
				SimpleDate date = new SimpleDate(dayVal, monthVal, yearVal);
				MainWindow.getInstance().setDate(date);
			} catch (Exception e) {
				ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
			}
		});
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setVisible(true);
	}
}
