package dev.zabi94.timetracker.gui.components.unregisteredActivities;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.gui.windows.ActivityThreadWindow;
import dev.zabi94.timetracker.utils.Utils;

public class UregisteredActivityRow extends JPanel {

	private static final long serialVersionUID = -2604515117042567536L;
	private static final Dimension MARKER_DIM = new Dimension(16, 16);
	
	public UregisteredActivityRow(ActivityThread thread, SelectableListController<UregisteredActivityRow> slc) throws SQLException {
		this.setLayout(new GridBagLayout());
		
		JLabel description = new JLabel(thread.getDescription());
		JLabel time = new JLabel(Utils.quartersToTime(thread.getQuarters()));
		JLabel date = new JLabel(thread.getDate().toString());
		JLabel customer = new JLabel(thread.getCustomer());
		JPanel marker = new JPanel();
		marker.setMaximumSize(MARKER_DIM);
		marker.setMinimumSize(MARKER_DIM);
		marker.setPreferredSize(MARKER_DIM);
		marker.setSize(MARKER_DIM);
		marker.setBackground(thread.getStatus().getColor());
		marker.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getForeground()));

		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, getBackground().darker()),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		slc.enroll(this, () -> {}, () -> {
			new ActivityThreadWindow(thread);
		});

		date.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		description.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		customer.setFont(customer.getFont().deriveFont(Font.BOLD));
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridheight = 2;
		c.insets = new Insets(5, 5, 5, 5);
		this.add(marker, c);
		
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.ipadx = 10;
		this.add(date, c);
		
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.weightx = 1;
		this.add(customer, c);
		
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(time, c);
		
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(description, c);
		
		int rowHeight = (int) this.getPreferredSize().getHeight() + 10;
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
		this.setMinimumSize(new Dimension(0, rowHeight));
		this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(), rowHeight));
		
		JPopupMenu change_state_menu = new JPopupMenu();
		
		for (RegistrationStatus rs:RegistrationStatus.values()) {

			JMenuItem mi = new JMenuItem(rs.toString());

			if (thread.getStatus() == rs) {
				mi.setEnabled(false);
			}

			mi.addActionListener(evt -> {
				thread.setStatus(rs);
				try {
					thread.db_persist();
				} catch (SQLException e) {
					ErrorHandler.showErrorWindow("Impossibile cambiare stato: "+e.getMessage());
				}
			});

			change_state_menu.add(mi);

		}
		
		this.setComponentPopupMenu(change_state_menu);
		
	}
	
}