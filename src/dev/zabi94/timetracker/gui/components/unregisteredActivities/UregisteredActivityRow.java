package dev.zabi94.timetracker.gui.components.unregisteredActivities;

import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
	private static final Dimension MARKER_DIM = new Dimension(12, 12);
	private static final Dimension DESCRIPTION_MAX_DIM = new Dimension(220, Integer.MAX_VALUE);
	private static final Dimension CUSTOMER_MAX_DIM = new Dimension(60, Integer.MAX_VALUE);
	
	public UregisteredActivityRow(ActivityThread thread, SelectableListController<UregisteredActivityRow> slc) throws SQLException {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
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
		
		description.setMaximumSize(DESCRIPTION_MAX_DIM);
		description.setPreferredSize(new Dimension(DESCRIPTION_MAX_DIM.width, description.getSize().height));
		
		customer.setMaximumSize(CUSTOMER_MAX_DIM);
		customer.setPreferredSize(new Dimension(CUSTOMER_MAX_DIM.width, customer.getSize().height));
		
		this.add(marker);
		this.add(Box.createHorizontalStrut(10));
		this.add(date);
		this.add(Box.createHorizontalStrut(10));
		this.add(customer);
		this.add(Box.createHorizontalStrut(10));
		this.add(description);
		this.add(Box.createHorizontalGlue());
		this.add(time);
		
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, getBackground().darker()),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		slc.enroll(this, () -> {}, () -> {
			new ActivityThreadWindow(thread);
		});
		
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