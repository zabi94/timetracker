package dev.zabi94.timetracker.gui.components;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.ReloadHandler;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.gui.windows.ActivityWindow;
import dev.zabi94.timetracker.utils.Utils;

public class ActivityListInActivityThreadPanel extends JPanel {

	private static final long serialVersionUID = -7309068584080489346L;
	
	private final ActivityThread thread;
	
	public ActivityListInActivityThreadPanel(ActivityThread thread) {
		
		this.thread = thread;
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		reloadActivities();
		
		ReloadHandler.subscribeObjectAndChildren(thread, s -> reloadActivities(), this);
	}
	
	private void reloadActivities() {
		for (Component c:this.getComponents()) {
			this.remove(c);
		}
		
		SelectableListController<ActivityListElement> slc = new SelectableListController<>();
		
		try {
			for (Activity act: thread.activities()) {
				this.add(new ActivityListElement(act, slc));
			}
		} catch (SQLException e) {
			ErrorHandler.showErrorWindow("Impossibile caricare interventi: "+e.getMessage());
		}
		
	}
	
	public static class ActivityListElement extends JPanel {

		private static final long serialVersionUID = 6333444663467719369L;
		
		private final Activity activity;
		
		private final JLabel description = new JLabel(), time = new JLabel();
		
		public ActivityListElement(Activity activity, SelectableListController<ActivityListElement> slc) {
			this.activity = activity;

			slc.enroll(this, () -> {}, () -> new ActivityWindow(activity, this));
			
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			
			reload();
			
			this.add(description);
			this.add(Box.createHorizontalGlue());
			this.add(time);
			
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			ReloadHandler.subscribeObject(activity, s -> reload(), this);
			
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
