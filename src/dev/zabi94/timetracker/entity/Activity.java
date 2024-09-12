package dev.zabi94.timetracker.entity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.zabi94.timetracker.db.DBAutoSerializable;
import dev.zabi94.timetracker.db.Data;
import dev.zabi94.timetracker.db.autoload.AutoEntity;
import dev.zabi94.timetracker.db.autoload.AutoField;

@AutoEntity("activity")
public class Activity extends DBAutoSerializable {
	
	@AutoField("activity_ID")
	private Integer activityID;
	
	@AutoField("quarters")
	private Integer quarters;
	
	@AutoField("description")
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

	
	public static List<Activity> fromActivityThread(ActivityThread at) throws SQLException {
		String sql = "SELECT ROWID FROM activity WHERE activity_ID = ?";
		List<Activity> activities = new ArrayList<>();
		Data.executeQuery(sql, rs -> {
			Data.getRows(rs).stream()
					.map(hm -> hm.get("rowid"))
					.map(Integer::parseInt)
					.map(Activity::fromID)
					.forEach(activities::add);
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
