package dev.zabi94.timetracker.gui.components;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.gui.windows.ActivityThreadWindow;
import dev.zabi94.timetracker.gui.windows.ActivityWindow;
import dev.zabi94.timetracker.gui.windows.MainWindow;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityThreadCard extends JPanel {

	private static final long serialVersionUID = -2666663882262854246L;
	private static final Dimension MAX_SIZE = new Dimension(Integer.MAX_VALUE, 40);
	private static final Dimension MARKER_SIZE = new Dimension(16, 16);
	private static final Font ROW_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

	private final ActivityThread thread;
	private final SelectableListElementController<ActivityThreadCard> listElementController;

	private final JLabel customer_label = new JLabel("", SwingConstants.CENTER);
	private final JLabel description_label = new JLabel();
	private final JLabel timecount_label = new JLabel();
	private final JPopupMenu contextual_menu = new JPopupMenu();

	public ActivityThreadCard(ActivityThread at, SelectableListController<ActivityThreadCard> listController) {
		this.thread = at;

		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, getBackground().darker()), 
				new EmptyBorder(2, 5, 2, 5)
		));
		this.setLayout(new GridBagLayout());
		this.setMaximumSize(MAX_SIZE);
		this.setSize(this.getPreferredSize());
		
		listElementController = listController.enroll(this, () -> MainWindow.getInstance().selectRow(this), () -> this.openWindow());

		customer_label.setText(thread.getCustomer());
		customer_label.setMinimumSize(new Dimension(80, customer_label.getPreferredSize().height));
		customer_label.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));
		try {
			timecount_label.setText(Utils.quartersToTime(at.getQuarters()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		description_label.setText(thread.getDescription());
		customer_label.setFont(ROW_FONT);
		description_label.setFont(ROW_FONT);
		timecount_label.setFont(ROW_FONT);

		Action copyToClipboard = new AbstractAction("Copia") {

			private static final long serialVersionUID = -5394988313390293590L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Utils.copyText(thread.getDescription().trim());
			}
		};

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control C"), "copyToClipboard");
		this.getActionMap().put("copyToClipboard", copyToClipboard);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.ipadx = 20;
		this.add(customer_label, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(description_label, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(timecount_label, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(0, 20, 0, 0);
		JPanel box = new JPanel();
		box.setMaximumSize(MARKER_SIZE);
		box.setMinimumSize(MARKER_SIZE);
		box.setPreferredSize(MARKER_SIZE);
		box.setSize(MARKER_SIZE);
		box.setBackground(at.getStatus().getColor());
		box.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getForeground()));
		this.add(box, c);

		JMenuItem editThread = new JMenuItem("Modifica");
		contextual_menu.add(editThread);
		editThread.addActionListener(evt -> this.openWindow());

		JMenuItem addActivity = new JMenuItem("Aggiungi intervento");
		contextual_menu.add(addActivity);
		addActivity.addActionListener(evt -> this.openNewActivityWindow());

		JMenuItem copyDescription = new JMenuItem(copyToClipboard);
		contextual_menu.add(copyDescription);

		contextual_menu.addSeparator();

		JMenu change_state_submenu = new JMenu("Cambia stato");

		contextual_menu.add(change_state_submenu);

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
				MainWindow.getInstance().setDate(MainWindow.getInstance().getSelectedDate());
			});

			change_state_submenu.add(mi);

		}

		contextual_menu.addSeparator();

		JMenuItem deleteThread = new JMenuItem("Elimina attività");
		contextual_menu.add(deleteThread);
		deleteThread.addActionListener(evt -> {
			if ((evt.getModifiers() & 2) == 0) {
				ErrorHandler.showErrorWindow("Tieni premuto CTRL per effettuare questa azione");
				return;
			}
			try {
				this.deleteRow();
				MainWindow.getInstance().setDate(MainWindow.getInstance().getSelectedDate());
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Errore nell'eliminazione dell'attività: "+e.getMessage());
			}
		});

		this.setComponentPopupMenu(contextual_menu);

	}

	public void openWindow() {
		new ActivityThreadWindow(this.thread);
	}

	public void openNewActivityWindow() {
		Activity a = new Activity();
		a.setActivityID(this.thread.db_id());
		a.setDescription("");
		a.setQuarters(0);
		new ActivityWindow(a, null);
	}

	public void deleteRow() throws SQLException {
		this.thread.db_delete();
	}

	public void cloneActivity(SimpleDate date) {
		try {
			ActivityThread at = new ActivityThread();
			at.setDate(date);
			at.setCustomer(thread.getCustomer());
			at.setDescription(thread.getDescription());
			at.setStatus(thread.getStatus());
			at.db_persist();

			int id = at.db_id();
			for (Activity activity: thread.activities()) {
				Activity na = new Activity();
				na.setActivityID(id);
				na.setDescription(activity.getDescription());
				na.setQuarters(activity.getQuarters());
				na.db_persist();
			}
		} catch (SQLException e) {
			ErrorHandler.showErrorWindow("Impossibile clonare attività");
			return;
		}
	}
	
	public boolean isSelected() {
		return this.listElementController.isSelected();
	}
	
}
