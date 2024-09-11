package dev.zabi94.timetracker.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.zabi94.timetracker.RegistrationStatus;
import dev.zabi94.timetracker.db.DBSerializable;
import dev.zabi94.timetracker.db.Data;
import dev.zabi94.timetracker.db.SimpleDate;
import dev.zabi94.timetracker.gui.ErrorHandler;

public class ActivityThread extends DBSerializable {
	
	private String description, customer;
	private RegistrationStatus status;
	private SimpleDate date;
	
	public ActivityThread(int id) throws SQLException {
		super(id);
	}
	
	public ActivityThread() {
		super();
	}

	@Override
	protected void db_update() throws SQLException {
		String sql = "UPDATE activity_thread SET description = ?, customer = ?, day = ?, status = ? WHERE ROWID = ?";
		Data.executeUpdate(sql, description, customer, date.getIntRepr(), status.ordinal(), ID);
	}

	@Override
	protected void db_insert() throws SQLException {
		String sql = "INSERT INTO activity_thread(description, customer, day, status) VALUES (?,?,?,?) RETURNING ROWID";
		Data.executeQuery(sql, rs -> {
			Data.getRows(rs).forEach(row -> {
				this.ID = Integer.parseInt(row.get("rowid"));
			});
		}, description, customer, date.getIntRepr(), status.ordinal());
	}

	@Override
	public void db_load() throws SQLException {
		String sql = "SELECT * FROM activity_thread WHERE ROWID = ?";
		Data.executeQuery(sql, rs -> {
			List<HashMap<String, String>> map = Data.getRows(rs);
			
			if (map.isEmpty()) throw new RuntimeException("Missing ID in DB: "+this.ID);
			
			map.forEach(row -> {
				this.date = SimpleDate.parse(Integer.parseInt(row.get("day")));
				this.description = row.get("description");
				this.customer = row.get("customer");
				this.status = RegistrationStatus.values()[Integer.parseInt(row.get("status"))];
			});
		}, ID);
	}

	@Override
	public void db_delete() throws SQLException {
		String sql = "DELETE FROM activity_thread WHERE ROWID = ?";
		Data.executeUpdate(sql, ID);
		sql = "DELETE FROM activity WHERE activity_ID = ?";
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
		return status;
	}

	public ActivityThread setStatus(RegistrationStatus status) {
		this.status = status;
		return this;
	}

	public SimpleDate getDate() {
		return date;
	}

	public ActivityThread setDate(SimpleDate date) {
		this.date = date;
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
