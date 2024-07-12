package dev.zabi94.timetracker.gui.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.AppStyle;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.windows.ActivityWindow;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityListPanel extends JPanel {

	private static final long serialVersionUID = -7309068584080489346L;
	
	private final ActivityThread thread;
	
	public ActivityListPanel(ActivityThread thread) {
		
		this.thread = thread;
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		reloadActivities();
	}
	
	private void reloadActivities() {
		for (Component c:this.getComponents()) {
			this.remove(c);
		}
		
		try {
			int i = 0;
			for (Activity act: thread.activities()) {
				this.add(new ActivityListElement(act, i%2!=0));
				this.add(Box.createVerticalStrut(3));
				i++;
			}
			this.add(Box.createVerticalGlue());
		} catch (SQLException e) {
			ErrorHandler.showErrorWindow("Impossibile caricare interventi: "+e.getMessage());
		}
		
	}
	
	public static class ActivityListElement extends JPanel implements MouseListener {

		private static final long serialVersionUID = 6333444663467719369L;
		
		private final Activity activity;
		
		private final JLabel description = new JLabel(), time = new JLabel();
		
		private final boolean zebra;
		
		private boolean hovered = false, selected = false;
		private long lastClick;
		
		public ActivityListElement(Activity activity, boolean zebra) {
			this.activity = activity;
			this.zebra = zebra;
			
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			
			reload();
			
			this.add(description);
			this.add(Box.createHorizontalGlue());
			this.add(time);
			
			this.addMouseListener(this);
			updateBackground();
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if (System.currentTimeMillis() - lastClick < 300) {
				lastClick = System.currentTimeMillis();
				new ActivityWindow(activity, this);
				return;
			}
			lastClick = System.currentTimeMillis();
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

		public void reload() {
			try {
				this.activity.db_load();
				int quarters = this.activity.getQuarters();
				time.setText(Utils.quartersToTime(quarters));
				this.description.setText(activity.getDescription().replace('\n', ' '));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
		}
		
	}

}
