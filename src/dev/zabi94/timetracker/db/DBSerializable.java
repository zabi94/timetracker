package dev.zabi94.timetracker.db;

import java.sql.SQLException;
import java.util.List;

import dev.zabi94.timetracker.gui.ReloadHandler;

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
		ReloadHandler.markChanged(this);
	}

	protected abstract void db_update() throws SQLException;
	
	protected abstract void db_insert() throws SQLException;
	
	public abstract void db_load() throws SQLException;
	
	protected abstract void db_drop() throws SQLException;
	
	public final void db_delete() throws SQLException {
		db_delete();
		ReloadHandler.markChanged(this);
	}
	
	public int db_id() {
		return ID;
	}
	
	public List<DBSerializable> getChildren() {
		return List.of();
	}
	
}
