package dev.zabi94.timetracker.db;

import java.sql.SQLException;

public abstract class DBSerializable {
	
	protected int ID = -1;
	
	public DBSerializable(int id) throws SQLException {
		if (id < 0) throw new IllegalArgumentException("Negative ID");
		this.ID = id;
		db_load();
	}
	
	public DBSerializable() {
		// NO OP
	}
	
	public void db_persist() throws SQLException {
		if (db_id() > 0) {
			db_update();
		} else {
			db_insert();
		}
	}

	protected abstract void db_update() throws SQLException;
	
	protected abstract void db_insert() throws SQLException;
	
	public abstract void db_load() throws SQLException;
	
	public abstract void db_delete() throws SQLException;
	
	public int db_id() {
		return ID;
	}
	
}
