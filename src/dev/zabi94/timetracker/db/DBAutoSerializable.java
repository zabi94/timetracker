package dev.zabi94.timetracker.db;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.zabi94.timetracker.db.autoload.AutoEntity;
import dev.zabi94.timetracker.db.autoload.AutoField;
import dev.zabi94.timetracker.utils.Counter;
import dev.zabi94.timetracker.utils.Utils;

public abstract class DBAutoSerializable extends DBSerializable {
	
	public DBAutoSerializable(int id) throws SQLException {
		super(id);
	}
	
	public DBAutoSerializable() {
		super();
	}
	
	private void validate() {
		if (!this.getClass().isAnnotationPresent(AutoEntity.class)) throw new IllegalStateException("La classe non ha l'annotazione AutoEntity");
	}

	@Override
	protected void db_update() throws SQLException {
		validate();
		
		String tableName = this.getClass().getAnnotation(AutoEntity.class).value();
		StringBuilder sql = new StringBuilder();
		
		sql.append("UPDATE ").append(tableName).append(" SET ");
		
		try {
			Map<String, Object> fields = getValues(this); 
			Object[] bind = new Object[fields.size() + 1];
			Counter c = new Counter();
			fields.forEach((k,v) -> {
				sql.append(k).append(" = ?").append(",");
				bind[c.next()] = v;
			});
			
			bind[c.next()] = db_id();
			
			String body = sql.toString();
			body = body.substring(0, body.length() - 1) + " WHERE ROWID = ?";
			Data.executeUpdate(body, bind);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}

	@Override
	protected void db_insert() throws SQLException {
		validate();
		
		String tableName = this.getClass().getAnnotation(AutoEntity.class).value();
		StringBuilder sql = new StringBuilder();
		
		sql.append("INSERT INTO ").append(tableName).append("(");
		
		try {
			Map<String, Object> fields = getValues(this); 
			List<String> names = new ArrayList<>();
			Object[] values = new Object[fields.size()];
			Counter c = new Counter();
			fields.forEach((k,v) -> {
				int index = c.next();
				names.add(k);
				values[index] = v;
			});
			
			sql.append(Utils.implode(",", names)).append(") VALUES (");
			sql.append('?').repeat(",?", fields.size() - 1).append(") RETURNING ROWID");
			
			Data.executeQuery(sql.toString(), rs -> {
				Data.getRows(rs).forEach(row -> {
					this.ID = Integer.parseInt(row.get("rowid"));
				});
			}, values);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void db_load() throws SQLException {
		validate();
		
		String tableName = this.getClass().getAnnotation(AutoEntity.class).value();
		String sql = "SELECT * FROM "+tableName+" WHERE ROWID = ?";
		Data.executeQuery(sql, rs -> {
			
			try {
				if (!rs.next()) throw new RuntimeException("Missing ID in DB: "+this.ID);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			try {
				for (String k: getValues(this).keySet()) {
					Object v = rs.getObject(k);
					setValue(k, this, v);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}, ID);
	}

	@Override
	public void db_drop() throws SQLException {
		validate();
		String tableName = this.getClass().getAnnotation(AutoEntity.class).value();
		String sql = "DELETE FROM "+tableName+" WHERE ROWID = ?";
		Data.executeUpdate(sql, ID);
	}

	private static void setValue(String field, Object entity, Object value) throws IllegalArgumentException, IllegalAccessException {
		for (Field g:entity.getClass().getDeclaredFields()) {
			if (g.isAnnotationPresent(AutoField.class)) {
				g.setAccessible(true);
				
				AutoField af = g.getAnnotation(AutoField.class);
				if (af.value().equals(field)) {

					if (!value.getClass().isAssignableFrom(g.getType())) {
						System.out.println(value);
						System.out.println(g.getType().getName());
						System.out.println(g.getName());
						System.out.println(field);
						throw new IllegalAccessException(String.format("Non posso assegnare un valore %s ad un campo %s (%s)", value.getClass().getName(), g.getType().getName(), g.getName()));
					}
					
					g.set(entity, value);
					return;
				}
			}
		}
	}
	
	private static Map<String, Object> getValues(Object entity) throws IllegalArgumentException, IllegalAccessException {
		
		HashMap<String,Object> result = new HashMap<String, Object>();
		
		for (Field g:entity.getClass().getDeclaredFields()) {
			if (g.isAnnotationPresent(AutoField.class)) {
				g.setAccessible(true);
				AutoField af = g.getAnnotation(AutoField.class);
				result.put(af.value(), g.get(entity));
			}
		}
		
		return result;
	}

}
