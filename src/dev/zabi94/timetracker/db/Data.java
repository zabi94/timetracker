package dev.zabi94.timetracker.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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
	
	private static String[] initStmts = new String[] {
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

	
	private static String[][] updateStmts = new String[][] {
			new String[] { // V1
					"""
					CREATE TABLE IF NOT EXISTS activity_bundle (
						active INT NOT NULL,
						customer CHAR(50) NOT NULL,
						description CHAR(255) NOT NULL,
						type CHAR(3) NOT NULL,
						code CHAR(20) NOT NULL,
						task INT NOT NULL
					);
					"""
			}
	};

	private static void initDb() throws SQLException {
		
		if (new File(DB_FILENAME).isFile()) return;
		
		try (Connection c = getConnection()) {
			
			for (String s:initStmts) {
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
	
	public static void prepareEnvironment() throws SQLException, IOException {
		
		initDb();
		createDBMeta();
		backupDB();
		updateDB();
		
	}

	private static void updateDB() throws SQLException {
		
		int currentDbVersion = Integer.parseInt(DbProperties.DB_VERSION.get());
		
		if (updateStmts.length > currentDbVersion) {
			
			for (int i = currentDbVersion; i < updateStmts.length; i++) {
				
				System.out.format("Aggiornamento DB a versione %d\n", currentDbVersion+1);
				
				try (Connection c = getConnection()) {
					c.setAutoCommit(false);
					try {
						c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						
						String[] newVersionStmts = updateStmts[i];
						
						for (String s: newVersionStmts) {
							PreparedStatement ps = c.prepareStatement(s);
							ps.execute();
						}
						
						DbProperties.DB_VERSION.set(""+(i+1), c);
						
						c.commit();
						
					} catch (Exception e) {
						e.printStackTrace();
						c.rollback();
						throw e;
					}
				}
			}
			
		}
		
	}

	private static void createDBMeta() throws SQLException {
		try (Connection c = getConnection()) {
			PreparedStatement ps = c.prepareStatement("""
					CREATE TABLE IF NOT EXISTS dbmeta (
						key CHAR(255) NOT NULL,
						val CHAR(255) NOT NULL
					)
					""");
			ps.execute();
			
		}
	}
	
	public static void backupDB() throws SQLException, IOException {
		
		File backupFolder = new File(DbProperties.BACKUP_FOLDER.get());
		
		backupFolder.mkdirs();
		
		File backupFile = new File(backupFolder, "BKP"+System.currentTimeMillis()+".db.bkp");
		
		backupFile.createNewFile();
		
		try (FileOutputStream out = new FileOutputStream(backupFile)) {
			Files.copy(new File(DB_FILENAME).toPath(), out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, new Object[] {});
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
			else throw new IllegalArgumentException(String.format("Invalid bind type at index %d (%s)", index, o.getClass().getName()));
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
	
	public static enum DbProperties {
		
		DB_VERSION("db_version", "0"), 
		BACKUP_FOLDER("bkp_folder", ".timetracker_bkp"), 
		WORKDAY_START("wd_start", "8:00"), 
		WORKDAY_END("wd_end", "17:00"), 
		LUNCHBREAK_START("lb_start", "13:00"), 
		LUNCHBREAK_END("lb_end", "17:00"), 
		WORK_HOURS("work_hours", "8");

		private String defaultValue, key;

		DbProperties(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}
		
		public String get() throws SQLException {
			try (Connection c = getConnection()) {
				return get(c);
			}
		}
		
		public String get(Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement("SELECT val FROM dbmeta WHERE key = ?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			String val = defaultValue;
			while (rs.next()) {
				val = rs.getString("val");
			}
			
			return val;
		}
		
		public void set(String value) throws SQLException {
			try (Connection c = getConnection()) {
				set(value, c);
			}
		}

		public void set(String value, Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement("UPDATE dbmeta SET val = ? WHERE key = ?");
			ps.setString(1, value);
			ps.setString(2, key);
			ps.execute();
			if (ps.getUpdateCount() == 0) {
				PreparedStatement ps1 = c.prepareStatement("INSERT INTO dbmeta(key,val) VALUES (?, ?)");
				ps1.setString(1, key);
				ps1.setString(2, value);
				ps1.execute();
			}
			
		}
		
		
	}
}
