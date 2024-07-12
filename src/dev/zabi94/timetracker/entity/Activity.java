package dev.zabi94.timetracker.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.zabi94.timetracker.db.DBSerializable;
import dev.zabi94.timetracker.db.Data;

public class Activity extends DBSerializable {
	
	private int activityID, quarters;
	private String description;
	
	public Activity(int id) throws SQLException {
		super(id);
	}
	
	public Activity() {
		super();
	}
	
	public static Activity fromID(int id) {
		try {
			return new Activity(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void db_update() throws SQLException {
		String sql = "UPDATE activity SET description = ?, quarters = ?, activity_ID = ? WHERE ROWID = ?";
		Data.executeUpdate(sql, description, quarters, activityID, ID);
	}

	@Override
	protected void db_insert() throws SQLException {
		String sql = "INSERT INTO activity(activity_ID, description, quarters) VALUES (?,?,?) RETURNING ROWID";
		Data.executeQuery(sql, rs -> {
			Data.getRows(rs).forEach(row -> {
				this.ID = Integer.parseInt(row.get("rowid"));
			});
		}, activityID, description, quarters);
	}

	@Override
	public void db_load() throws SQLException {
		String sql = "SELECT * FROM activity WHERE ROWID = ?";
		Data.executeQuery(sql, rs -> {
			List<HashMap<String, String>> map = Data.getRows(rs);
			
			if (map.isEmpty()) throw new RuntimeException("Missing ID in DB: "+this.ID);
			
			map.forEach(row -> {
				this.activityID = Integer.parseInt(row.get("activity_ID"));
				this.description = row.get("description");
				this.quarters = Integer.parseInt(row.get("quarters"));
			});
		}, ID);
	}

	@Override
	public void db_delete() throws SQLException {
		String sql = "DELETE FROM activity WHERE ROWID = ?";
		Data.executeUpdate(sql, ID);
	}
	
	public static List<Activity> fromActivityThread(ActivityThread at) throws SQLException {
		String sql = "SELECT ROWID FROM activity WHERE activity_ID = ?";
		List<Activity> activities = new ArrayList<>();
		Data.executeQuery(sql, rs -> {
			Data.getRows(rs).stream()
					.map(hm -> hm.get("rowid"))
					.map(sid -> Integer.parseInt(sid))
					.map(id -> fromID(id))
					.forEach(a -> activities.add(a));
		}, at.db_id());
		return activities;
	}

	public int getActivityID() {
		return activityID;
	}

	public Activity setActivityID(int activityID) {
		this.activityID = activityID;
		return this;
	}

	public int getQuarters() {
		return quarters;
	}

	public Activity setQuarters(int quarters) {
		this.quarters = quarters;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Activity setDescription(String description) {
		this.description = description;
		return this;
	}

}
