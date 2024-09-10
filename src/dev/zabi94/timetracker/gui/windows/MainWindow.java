package dev.zabi94.timetracker.gui.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.Icons;
import dev.zabi94.timetracker.gui.components.ActionBar;
import dev.zabi94.timetracker.gui.components.ActivityThreadCard;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.utils.Utils;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -7804053722158533599L;
	private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	private static final int DW = 800;
	private static final int DH = 600;
	
	private static MainWindow INSTANCE;

	private JLabel date_label = new JLabel("", SwingConstants.CENTER);
	private JLabel date_total_time = new JLabel("");
	private JPanel activity_panel = new JPanel();
	private JButton date_before = new JButton(Icons.ARROW_LEFT);
	private JButton date_after = new JButton(Icons.ARROW_RIGHT);
	private JButton date_today = new JButton("Oggi");
	private ActionBar actionBar;
	private SimpleDate date;
	private SelectableListController<ActivityThreadCard> listController;
	
	public MainWindow(SimpleDate date) throws SQLException {
		
		this.setTitle("Time Tracker");
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.date = date;
		this.actionBar = new ActionBar();
		
		this.date_before.setToolTipText("Giorno precedente");
		this.date_after.setToolTipText("Giorno successivo");

		int sw = getToolkit().getScreenSize().width;
		int sh = getToolkit().getScreenSize().height;
		
		this.setBounds((sw - DW)/2, (sh - DH)/2, DW, DH);
		this.setLayout(new GridBagLayout());
		date_label.setText(date.toString());
		date_label.setBackground(Color.gray);
		date_label.setFont(TITLE_FONT);
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		this.add(date_label, c);

		
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.NORTHWEST;
		this.add(date_before, c);
		date_before.addActionListener(evt -> {
			try {
				this.date = this.date.dayBefore();
				this.loadContents(this.date);
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
			}
		});

		
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 2;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.NORTHEAST;
		this.add(date_after, c);
		date_after.addActionListener(evt -> {
			try {
				this.date = this.date.dayAfter();
				this.loadContents(this.date);
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
			}
		});

		
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.CENTER;
		this.add(date_today, c);
		date_today.addActionListener(evt -> {
			try {
				this.date = SimpleDate.today();
				this.loadContents(this.date);
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
			}
		});

		
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 2;
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.PAGE_END;
		this.add(date_total_time, c);

		
		c = new GridBagConstraints();
		c.gridy = 2;
		c.weighty = 1;
		c.gridheight = 2;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		activity_panel.setLayout(new BoxLayout(activity_panel, BoxLayout.Y_AXIS));
		activity_panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder()));
		this.add(activity_panel, c);
		
		c = new GridBagConstraints();
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		this.add(actionBar, c);
		
		activity_panel.add(Box.createVerticalGlue());
		
		Utils.setOnClickBehaviour(date_label, () -> {
			final SimpleDate current = INSTANCE.getSelectedDate();
			DatePicker.prompt(current, "Seleziona data", "vai", dateSelected -> MainWindow.getInstance().setDate(dateSelected.orElse(current)));
		});
		
		loadContents(date);
		
		this.setVisible(true);
		
	}

	
	public void selectRow(ActivityThreadCard atc) {
		actionBar.onActivityThreadSelected(atc);
	}
	
	public Optional<ActivityThreadCard> getSelectedRow() {
		if (listController == null) return Optional.empty();
		return Optional.of(listController.getSelectedElement());
	}
	
	public SimpleDate getSelectedDate() {
		return date;
	}
	
	private void loadContents(SimpleDate date) throws SQLException {
		
		date_label.setText(date.toString());
		
		
		for (Component c: activity_panel.getComponents()) {
			activity_panel.remove(c);
		}

		selectRow(null);

		int[] quarters = {0};
		
		listController = new SelectableListController<ActivityThreadCard>(t -> {});
		
		ActivityThread.findByDate(date).forEach(at -> {
			ActivityThreadCard atc = new ActivityThreadCard(at, listController);
			activity_panel.add(atc);
			try {
				quarters[0] += at.getQuarters();
			} catch (SQLException e) {
				quarters[0] = Integer.MIN_VALUE;
			}
		});
		
		this.date_total_time.setText(Utils.quartersToTime(quarters[0]));
		
		selectRow(null);
		
		Dimension a = this.getSize();
		this.setSize(a.width+1, a.height);
		this.setSize(a);
		
	}
	
	public static MainWindow getInstance() {
		if (INSTANCE == null) {
			try {
				INSTANCE = new MainWindow(SimpleDate.today());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return INSTANCE;
	}


	public void setDate(SimpleDate date) {
		this.date = date;
		try {
			loadContents(date);
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorHandler.showErrorWindow("Impossibile cambiare data: "+e.getMessage());
		}
	}
	
}
