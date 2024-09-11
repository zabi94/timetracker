package dev.zabi94.timetracker.gui.windows;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.GenericHandlers;
import dev.zabi94.timetracker.gui.Icons;
import dev.zabi94.timetracker.gui.components.ActivityListPanel;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityThreadWindow extends JFrame {

	private static final long serialVersionUID = -4179628476311606780L;
	private static final int DW = 400;
	private static final int DH = 500;
	
	private final ActivityThread activity;
	private final JButton save = new JButton(Icons.SAVE);
	private final JLabel description_label = new JLabel("Descrizione", SwingConstants.LEFT);
	private final JLabel customer_label = new JLabel("Cliente", SwingConstants.LEFT);
	private final JLabel activities_label = new JLabel("Interventi", SwingConstants.LEFT);
	private final JTextArea description = new JTextArea();
	private final JTextField customer = new JTextField();
	private final ActivityListPanel activities;
	private final JComboBox<RegistrationStatus> status = new JComboBox<RegistrationStatus>(RegistrationStatus.values());
	private final ArrayList<Runnable> onChangeListeners = new ArrayList<>();
	
	public ActivityThreadWindow(ActivityThread activity) {
		this.activity = activity;
		
		activities = new ActivityListPanel(activity);
		
		this.setTitle("Attività");

		this.setBounds(Utils.positionInMiddleOfMainWindow(DW, DH));
		
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setLayout(new GridBagLayout());
		
		this.description.setLineWrap(true);
		this.description.setWrapStyleWord(true);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(customer_label, c);

		
		customer.setText(this.activity.getCustomer());
		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(customer, c);
		
		
		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 0, 5);
		c.gridy = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(description_label, c);
		
		
		description.setText(this.activity.getDescription());
		description.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 5, 5);
		c.gridy = 2;
		c.gridheight = 3;
		c.gridwidth = 3;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(description, c);
		description.addKeyListener(GenericHandlers.AVOID_TABS);

		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 7;
		c.gridx = 2;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(save,c);
		save.setToolTipText("Salva su DB");

		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 7;
		c.gridx = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		status.setSelectedItem(this.activity.getStatus());
		this.add(status,c);

		c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 5, 5);
		c.gridy = 6;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.weightx = 1;
		this.add(activities,c);

		c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 0, 5);
		c.gridy = 5;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(activities_label,c);
		
		
		
		save.addActionListener(evt -> {
			try {
				this.activity.setCustomer(this.customer.getText())
					.setDescription(description.getText())
					.setStatus((RegistrationStatus) status.getSelectedItem())
					.db_persist();
				this.setVisible(false);
				this.dispose();
				MainWindow.getInstance().setDate(MainWindow.getInstance().getSelectedDate());
				onChangeListeners.forEach(r -> r.run());
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Errore nel salvataggio: "+e.getMessage());
			}
		});
		
		this.setVisible(true);
		
	}

	public void addOnChangeListener(Runnable onChange) {
		onChangeListeners.add(onChange);
	}

}
