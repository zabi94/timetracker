package dev.zabi94.timetracker.db;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Data {
	
	private static final String DB_FILENAME = "timetracker.db";
	
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:"+DB_FILENAME);
	}

	public static void initDb() throws SQLException {
		
		if (new File(DB_FILENAME).isFile()) return;
		
		try (Connection c = getConnection()) {
			String sql[] = new String[] {
					"""
					create table activity_thread (
						description CHAR(255) NOT NULL,
						customer CHAR(50) NOT NULL,
						day INT NOT NULL,
						status INT NOT NULL DEFAULT 0
					)""",
					"""
					create table activity (
						activity_ID INT NOT NULL REFERENCES activity_thread(ID), 
						description CHAR(255) NOT NULL,
						quarters INT NOT NULL
					)
					""",
					"""
					create table activity_thread_notes (
						activity_thread_ID INT NOT NULL REFERENCES activity_thread(ID), 
						description CHAR(255) NOT NULL
					)
					""",
					"""
					create table activity_notes (
						activity_ID INT NOT NULL REFERENCES activity(ID),
						description CHAR(255) NOT NULL
					)
					"""
			};
			
			for (String s:sql) {
				try {
					PreparedStatement ps = c.prepareStatement(s);
					ps.execute();
				} catch (Exception e) {
					System.out.println(s);
					throw e;
				}
			}
		}
	}
	
	public static int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql);
	}
	
	public static int executeUpdate(String sql, Object... bind) throws SQLException {
		try (Connection c = getConnection()) {
			PreparedStatement ps = c.prepareStatement(sql);
			bindPS(ps, bind);
			return ps.executeUpdate();
		}
	}
	
	public static int executeMultiUpdate(String sql, Object[]... bindings) throws SQLException {
		try (Connection c = getConnection()) {
			PreparedStatement ps = c.prepareStatement(sql);
			int totalRows = 0;
			for (Object[] bind: bindings) {
				bindPS(ps, bind);
				totalRows += ps.executeUpdate();
			}
			return totalRows;
		}
	}
	
	public static void executeQuery(String sql, Consumer<ResultSet> onComplete, Object... bind) throws SQLException {
		try (Connection c = getConnection()) {
			PreparedStatement ps = c.prepareStatement(sql);
			bindPS(ps, bind);
			ResultSet rs = ps.executeQuery();
			onComplete.accept(rs);
		}
	}
	
	public static void executeQuery(String sql, Consumer<ResultSet> onComplete) throws SQLException {
		executeQuery(sql, onComplete, new Object[] {});
	}
	
	private static void bindPS(PreparedStatement ps, Object... bind) throws SQLException {
		int index = 0;
		for (Object o:bind) {
			index++;
			if (o instanceof String s) ps.setString(index, s);
			else if (o instanceof Double d) ps.setDouble(index, d);
			else if (o instanceof Float d) ps.setFloat(index, d);
			else if (o instanceof Integer d) ps.setInt(index, d);
			else if (o instanceof Byte d) ps.setByte(index, d);
			else if (o instanceof Date d) ps.setDate(index, d);
			else throw new IllegalArgumentException("Invalid bind type at index "+index);
		}
	}
	
	public static List<HashMap<String, String>> getRows(ResultSet rs) {
		List<HashMap<String, String>> result = new ArrayList<>();
		try {
			while (rs.next()) {
				HashMap<String, String> hm = new HashMap<>();
				ResultSetMetaData md = rs.getMetaData();
				int cols = md.getColumnCount();
				for (int i = 1; i <= cols; i++) {
					String name = md.getColumnLabel(i);
					String value = rs.getString(i);
					hm.put(name, value);
				}
				result.add(hm);
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
