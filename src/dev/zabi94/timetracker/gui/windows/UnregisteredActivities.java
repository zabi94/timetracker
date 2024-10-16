package dev.zabi94.timetracker.gui.windows;

import java.awt.Component;
import java.sql.SQLException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dev.zabi94.timetracker.db.DBSerializable;
import dev.zabi94.timetracker.entity.Activity;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ReloadHandler;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.gui.components.unregisteredActivities.UregisteredActivityRow;
import dev.zabi94.timetracker.utils.Utils;

public class UnregisteredActivities extends JPanel {

	private static final int DW = 500;
	private static final int DH = 400;
	private static final long serialVersionUID = -40529971298085401L;
	
	private final JFrame parentWindow; 
	
	public UnregisteredActivities(JFrame parentWindow) {
		this.parentWindow = parentWindow;
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5), 
				BorderFactory.createEtchedBorder()
		));
		reload();
		Consumer<DBSerializable> onChange = s -> reload();
		
		ReloadHandler.subscribeType(Activity.class, onChange, this);
		ReloadHandler.subscribeType(ActivityThread.class, onChange, this);
	}
	
	public static JFrame getWindow() {
		JFrame jf = new JFrame();
		
		UnregisteredActivities ua = new UnregisteredActivities(jf);
		
		JScrollPane jsp = new JScrollPane(ua, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jf.add(jsp);
		
		jf.setTitle("Attività non registrate");
		jf.setBounds(Utils.positionInMiddleOfMainWindow(DW, DH));
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		return jf;
	}

	public void reload() {
		for (Component c: this.getComponents()) {
			this.remove(c);
		}
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		SelectableListController<UregisteredActivityRow> slc = new SelectableListController<>();
		ActivityThread.findUnregistered().forEach(at -> {
			try {
				UregisteredActivityRow row = new UregisteredActivityRow(at, slc);
				this.add(row);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		Utils.refreshComponent(parentWindow);
	}

}
