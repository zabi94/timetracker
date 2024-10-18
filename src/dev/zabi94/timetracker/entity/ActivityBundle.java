package dev.zabi94.timetracker.entity;

import java.awt.Color;
import java.sql.SQLException;

import dev.zabi94.timetracker.db.DBAutoSerializable;
import dev.zabi94.timetracker.db.autoload.AutoEntity;
import dev.zabi94.timetracker.db.autoload.AutoField;

@AutoEntity("activity_bundle")
public class ActivityBundle extends DBAutoSerializable {

	@AutoField("active")
	private Integer active;

	@AutoField("customer")
	private String customer;

	@AutoField("description")
	private String description;
	
	@AutoField("type")
	private String type;

	@AutoField("code")
	private String code;

	@AutoField("task")
	private String task;

	public ActivityBundle() {
		super();
	}
	
	public ActivityBundle(int id) throws SQLException {
		super(id);
	}

	public boolean isActive() {
		return active == 1;
	}

	public ActivityBundle setActive(boolean active) {
		this.active = active ? 1 : 0;
		return this;
	}

	public String getCustomer() {
		return customer;
	}

	public ActivityBundle setCustomer(String customer) {
		this.customer = customer;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ActivityBundle setDescription(String description) {
		this.description = description;
		return this;
	}

	public BundleType getType() {
		return BundleType.forCode(this.type);
	}

	public ActivityBundle setType(BundleType type) {
		this.type = type.code;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ActivityBundle setCode(String code) {
		this.code = code;
		return this;
	}

	public String getTask() {
		return task;
	}

	public ActivityBundle setTask(String task) {
		this.task = task;
		return this;
	}
	
	public static enum BundleType {

		LOTUS("LTS", new Color(0x7fb927)), 
		ECO("ECO", new Color(0xa3c1e0)), 
		UNKNOWN("", Color.orange);

		public final String code;
		public final Color color;

		BundleType(String code, Color color) {
			this.code = code;
			this.color = color;
		}

		public static BundleType forCode(String type) {
			for (BundleType t:BundleType.values()) {
				if (t.code.equals(type)) return t;
			}
			return null;
		}
		
	}
	

}
