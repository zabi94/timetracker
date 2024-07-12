package dev.zabi94.timetracker.gui.windows;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.GenericHandlers;
import dev.zabi94.timetracker.gui.components.ActivityListPanel.ActivityListElement;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityWindow extends JFrame {

	private static final long serialVersionUID = -8720089898713330935L;
	private static final int DW = 350;
	private static final int DH = 200;

	private final Activity activity;
	private final Optional<ActivityListElement> activityListElement;
	private final JLabel description_label = new JLabel("Descrizione", SwingConstants.LEFT);
	private final JLabel spinner_label = new JLabel("0,25 x ", SwingConstants.RIGHT);
	private final JLabel spinner_total = new JLabel("0:15h", SwingConstants.LEFT);
	private final JTextArea description = new JTextArea();
	private final JSpinner spinner = new JSpinner();
	private final JButton save = new JButton("Salva su DB");
	
	public ActivityWindow(Activity activity, ActivityListElement activityListElementIn) {
		this.activity = activity;
		this.activityListElement = Optional.ofNullable(activityListElementIn);
		
		this.setTitle("Intervento");

		this.setBounds(Utils.positionInMiddleOfMainWindow(DW, DH));
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(description_label, c);
		
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 5, 5);
		c.gridy = 1;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		description.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		description.setText(this.activity.getDescription());
		description.addKeyListener(GenericHandlers.AVOID_TABS);
		this.add(description, c);
		

		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 2;
		this.add(save,c);
		

		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 4;
		c.gridx = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		this.add(spinner_label,c);
		
		if (this.activity.db_id() < 0) {
			spinner.setValue(1);
		} else {
			spinner.setValue(this.activity.getQuarters());
		}
		spinner.setPreferredSize(new Dimension(50, spinner.getPreferredSize().height));
		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 4;
		c.gridx = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		spinner.addChangeListener(evt -> {
			int qt = (Integer) spinner.getValue();
			
			if (qt < 1) {
				qt = 1;
				spinner.setValue(qt);
			}
			
			spinner_total.setText(Utils.quartersToTime(qt));
		});
		this.add(spinner,c);
		
		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 4;
		c.gridx = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(spinner_total,c);
		
		save.addActionListener(evt -> {
			this.activity.setDescription(this.description.getText());
			this.activity.setQuarters((Integer) spinner.getValue());
			try {
				this.activity.db_persist();
				this.setVisible(false);
				this.dispose();
				MainWindow.getInstance().setDate(MainWindow.getInstance().getSelectedDate());
				this.activityListElement.ifPresent(ale -> ale.reload());
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Errore nel salvataggio: "+e.getMessage());
			}
		});
		
		this.setVisible(true);
		
	}

}
