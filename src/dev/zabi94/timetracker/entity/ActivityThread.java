package dev.zabi94.timetracker.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.db.DBAutoSerializable;
import dev.zabi94.timetracker.db.Data;
import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.db.autoload.AutoEntity;
import dev.zabi94.timetracker.db.autoload.AutoField;
import dev.zabi94.timetracker.gui.ErrorHandler;

@AutoEntity("activity_thread")
public class ActivityThread extends DBAutoSerializable {

	@AutoField("customer")
	private String customer;

	@AutoField("description")
	private String description;

	@AutoField("status")
	private Integer status;

	@AutoField("day")
	private Integer date;
	
	public ActivityThread(int id) throws SQLException {
		super(id);
	}
	
	public ActivityThread() {
		super();
	}

	@Override
	public void db_delete() throws SQLException {
		super.db_delete();
		String sql = "DELETE FROM activity WHERE activity_ID = ?";
		Data.executeUpdate(sql, ID);
	}

	public String getDescription() {
		return description;
	}

	public ActivityThread setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getCustomer() {
		return customer;
	}

	public ActivityThread setCustomer(String customer) {
		this.customer = customer;
		return this;
	}

	public RegistrationStatus getStatus() {
		return RegistrationStatus.values()[status];
	}

	public ActivityThread setStatus(RegistrationStatus status) {
		this.status = status.ordinal();
		return this;
	}

	public SimpleDate getDate() {
		return SimpleDate.parse(date);
	}

	public ActivityThread setDate(SimpleDate date) {
		this.date = date.getIntRepr();
		return this;
	}
	
	public static ActivityThread fromID(int id) {
		try {
			return new ActivityThread(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<ActivityThread> findByDate(SimpleDate date) throws SQLException {
		String sql = "SELECT ROWID FROM activity_thread WHERE day = ?";
		List<ActivityThread> activities = new ArrayList<>();
		Data.executeQuery(sql, rs -> {
			Data.getRows(rs).stream()
					.map(hm -> hm.get("rowid"))
					.map(sid -> Integer.parseInt(sid))
					.map(id -> fromID(id))
					.forEach(a -> activities.add(a));
		}, date.getIntRepr());
		return activities;
	}
	
	public static List<ActivityThread> findUnregistered() {
		
		List<ActivityThread> activityList = new ArrayList<>();
		
		try {
			Data.executeQuery("select ROWID, * from activity_thread where status <= 2 order by day desc", rs -> {

				for (HashMap<String,String> row: Data.getRows(rs)) {
					int id = Integer.parseInt(row.get("rowid"));
					try {
						activityList.add(new ActivityThread(id));
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
				
			});
		} catch (Exception e) {
			ErrorHandler.showErrorWindow("Impossibile esportare le richieste: "+e.getMessage());
		}
		
		return activityList;
	}
	
	public List<Activity> activities() throws SQLException {
		return Activity.fromActivityThread(this);
	}
	
	public int getQuarters() throws SQLException {
		return activities().stream().mapToInt(a -> a.getQuarters()).sum();
	}

}
