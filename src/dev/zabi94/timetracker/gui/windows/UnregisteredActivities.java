package dev.zabi94.timetracker.gui.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.components.SelectableListElementController.SelectableListController;
import dev.zabi94.timetracker.utils.Utils;

public class UnregisteredActivities extends JPanel {

	private static final int DW = 300;
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
				UregisteredActivityRow row = new UregisteredActivityRow(this, at, slc);
				this.add(row);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		Dimension osize = parentWindow.getSize();
		parentWindow.setSize(0,0);
		parentWindow.setSize(osize);
	}
	
	public class UregisteredActivityRow extends JPanel {

		private static final long serialVersionUID = -2604515117042567536L;
		
		public UregisteredActivityRow(UnregisteredActivities parent, ActivityThread thread, SelectableListController<UregisteredActivityRow> slc) throws SQLException {
			this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			
			JLabel description = new JLabel(thread.getDescription());
			JLabel time = new JLabel(Utils.quartersToTime(thread.getQuarters()));
			JLabel date = new JLabel(thread.getDate().toString());
			JLabel customer = new JLabel(thread.getCustomer());
			JPanel marker = new JPanel();
			marker.setMaximumSize(new Dimension(10, 10));
			marker.setBackground(thread.getStatus().getColor());
			
			this.add(marker);
			this.add(Box.createHorizontalStrut(10));
			this.add(date);
			this.add(Box.createHorizontalStrut(10));
			this.add(customer);
			this.add(Box.createHorizontalStrut(10));
			this.add(description);
			this.add(Box.createHorizontalGlue());
			this.add(time);
			
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			slc.enroll(this, () -> {}, () -> {
				ActivityThreadWindow atw = new ActivityThreadWindow(thread);
				atw.addOnChangeListener(() -> parent.reload());
			});
			
			this.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) this.getPreferredSize().getHeight()));
		}
		
	}

}
