package dev.zabi94.timetracker.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.AppStyle;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.windows.ActivityThreadWindow;
import dev.zabi94.timetracker.gui.windows.ActivityWindow;
import dev.zabi94.timetracker.gui.windows.MainWindow;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityThreadCard extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = -2666663882262854246L;
	private static final Dimension MAX_SIZE = new Dimension(Integer.MAX_VALUE, 20);
	private static final Font ROW_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	
	private final ActivityThread thread;
	private final boolean zebra;

	private boolean selected = false, hovered = false;
	private long lastClick;

	private final JLabel customer_label = new JLabel("", SwingConstants.CENTER);
	private final JLabel description_label = new JLabel();
	private final JLabel timecount_label = new JLabel();
	private final JPopupMenu contextual_menu = new JPopupMenu();
	
	public ActivityThreadCard(ActivityThread at, boolean zebraType) {
		this.thread = at;
		this.zebra = zebraType;
		
		this.setBorder(new EmptyBorder(2, 5, 2, 5));
		this.setLayout(new GridBagLayout());
		this.setMaximumSize(MAX_SIZE);
		this.setSize(this.getPreferredSize());
		
		if (zebraType) {
			this.setBackground(AppStyle.BG_ZEBRA_1); 
		} else {
			this.setBackground(AppStyle.BG_ZEBRA_2); 
		}
		
		customer_label.setText(thread.getCustomer());
		customer_label.setPreferredSize(new Dimension(100, customer_label.getPreferredSize().height));
		try {
			timecount_label.setText(Utils.quartersToTime(at.getQuarters()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		description_label.setText(thread.getDescription());
		customer_label.setFont(ROW_FONT);
		description_label.setFont(ROW_FONT);
		timecount_label.setFont(ROW_FONT);
		
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
		c.fill = GridBagConstraints.BOTH;
		Component box = new JPanel();
		box.setBackground(at.getStatus().getColor());
		this.add(box, c);
		
		JMenuItem editThread = new JMenuItem("Modifica");
		contextual_menu.add(editThread);
		editThread.addActionListener(evt -> this.openWindow());
		
		JMenuItem addActivity = new JMenuItem("Aggiungi intervento");
		contextual_menu.add(addActivity);
		addActivity.addActionListener(evt -> this.openNewActivityWindow());
		
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
	
	public void init() {
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (System.currentTimeMillis() - lastClick < 300) {
			openWindow();
			lastClick = System.currentTimeMillis();
			return;
		}

		lastClick = System.currentTimeMillis();
		MainWindow.getInstance().selectRow(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.hovered = true;
		updateBackground();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.hovered = false;
		updateBackground();
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateBackground();
	}
	
	private void updateBackground() {
		if (selected) {
			this.setBackground(AppStyle.BG_SELECTED);
		} else if (hovered) {
			this.setBackground(AppStyle.BG_HOVER);
		} else if (zebra) {
			this.setBackground(AppStyle.BG_ZEBRA_1);
		} else {
			this.setBackground(AppStyle.BG_ZEBRA_2);
		}
	}
	
	public boolean isSelected() {
		return selected;
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

}
