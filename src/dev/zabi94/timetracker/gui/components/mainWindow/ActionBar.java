package dev.zabi94.timetracker.gui.components.mainWindow;

import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.db.Data;
import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.entity.ActivityThread;
import dev.zabi94.timetracker.gui.ErrorHandler;
import dev.zabi94.timetracker.gui.Icons;
import dev.zabi94.timetracker.gui.windows.ActivityThreadWindow;
import dev.zabi94.timetracker.gui.windows.DatePicker;
import dev.zabi94.timetracker.gui.windows.MainWindow;
import dev.zabi94.timetracker.gui.windows.UnregisteredActivities;
import dev.zabi94.timetracker.utils.Utils;

public class ActionBar extends JPanel {

	private static final long serialVersionUID = -945355766144418106L;

	private JButton addThreadButton = new JButton(Icons.NEW_ACTIVITY);
	private JButton exportMissing = new JButton(Icons.EXPORT);
	private JButton multiplyToDays = new JButton(Icons.MULTI);
	private JButton missingThreads = new JButton(Icons.HISTORY);
	
	public ActionBar() {
		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		addThreadButton.setToolTipText("Nuova attività");
		exportMissing.setToolTipText("Esporta richieste");
		multiplyToDays.setToolTipText("Inserimento multiplo");
		missingThreads.setToolTipText("Attività non registrate");
		
		addThreadButton.addActionListener(e -> {
			ActivityThread act = new ActivityThread();
			act.setStatus(RegistrationStatus.UNREGISTERED);
			act.setDate(MainWindow.getInstance().getSelectedDate());
			act.setDescription("");
			act.setCustomer("");
			new ActivityThreadWindow(act);
		});
		
		exportMissing.addActionListener(evt -> {
			int result = JOptionPane.showOptionDialog(MainWindow.getInstance(), "Esportare anche quelle già richieste?", "Export", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			String confronto = "= 0";
			if (result == 0) {
				confronto = "<= 1";
			}
			try {
				Data.executeQuery(String.format("select * from activity_thread where status %s order by customer asc, day desc", confronto), rs -> {

					StringBuilder sb = new StringBuilder();
					String lastCustomer = "";
					
					for (HashMap<String,String> row: Data.getRows(rs)) {
						String customer = row.get("customer");
						String description = row.get("description");
						if (!lastCustomer.equals(customer)) {
							sb.append("\n");
							sb.append(customer);
							sb.append(":\n");
							lastCustomer = customer;
						}
						
						sb.append("- ");
						sb.append(description);
						sb.append("\n");
					}
					
					Utils.copyText(sb.toString().trim());
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Richiesta copiata");
					
				});
			} catch (SQLException e) {
				ErrorHandler.showErrorWindow("Impossibile esportare le richieste: "+e.getMessage());
			}
		});
		
		multiplyToDays.addActionListener(evt -> {
			
			var row = MainWindow.getInstance().getSelectedRow();
			
			if (row.isEmpty()) return;
			
			ActivityThreadCard at = row.get();
			
			DatePicker.prompt(SimpleDate.today(), "Data di partenza", "OK", optDateStart -> optDateStart.ifPresent(dateStart -> {
				DatePicker.prompt(SimpleDate.today(), "Data di arrivo", "OK", optDateEnd -> optDateEnd.ifPresent(dateEnd -> {
					
					SimpleDate currDate = dateStart;
					while (currDate.getIntRepr() <= dateEnd.getIntRepr()) {
						if (currDate.dayOfWeek() < 6) { // SKipping weekends
							at.cloneActivity(currDate);
						}
						currDate = currDate.dayAfter();
					}
				}));
			}));
		});
		
		missingThreads.addActionListener(evt -> {
			UnregisteredActivities.getWindow().setVisible(true);
		});

		this.add(Box.createHorizontalStrut(10));
		this.add(addThreadButton);
		this.add(Box.createHorizontalStrut(10));
		this.add(exportMissing);
		this.add(Box.createHorizontalStrut(10));
		this.add(multiplyToDays);
		this.add(Box.createHorizontalStrut(10));
		this.add(missingThreads);
		this.add(Box.createHorizontalGlue());
		
	}
	
	public void onActivityThreadSelected(ActivityThreadCard atc) {
	}

}
