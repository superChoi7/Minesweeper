package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JDBCUtil {
	
	private static final String MARK = "#";
	private static final String url = "jdbc:sqlite:data.db";
	private static int NUM_HIGHEST = 5;
	private static int NUM_GAMES = 7;
	
	public static void saveScore(String data) {
		String[] dataArr = data.split(MARK);
		try {
			Connection conn = DriverManager.getConnection(url);
			
			PreparedStatement insertStmt = 
					conn.prepareStatement("INSERT INTO scores (time, score) VALUES (?, ?)");
			insertStmt.setString(1, dataArr[0]);
			insertStmt.setString(2, dataArr[1]);
			insertStmt.execute();
			
			// delete extra scores if lines of data exceeds 5
			PreparedStatement queryStmt = 
					conn.prepareStatement("SELECT * FROM scores");
			
			ResultSet rset = queryStmt.executeQuery();
			int numRows = 0;
			while (rset.next()) {
				numRows++;
			}
			
			if (numRows > NUM_HIGHEST) {
				PreparedStatement deleteStmt = 
						conn.prepareStatement("DELETE FROM scores WHERE score = (SELECT MIN(score) FROM scores);");
				deleteStmt.execute();
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String showScore() {
		String data = "";
		try {
			Connection conn = DriverManager.getConnection(url);
			
			PreparedStatement queryStmt = 
					conn.prepareStatement("SELECT * from scores ORDER BY score DESC LIMIT 5;");
			
			ResultSet rset = queryStmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			
			int numColumns = rsmd.getColumnCount();
			// column name 
			for (int i = 1; i <= numColumns; i++) {
				Object o = rsmd.getColumnName(i);
				data += o.toString() + "\t\t";
			}
			data += "\n";
			// rank
			while (rset.next()) {
				for (int i = 1; i <= numColumns; i++) {
					Object o = rset.getObject(i);
					data += o.toString() + "\t";
				}
				data += "\n";
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void saveGame(String data) {
		String[] dataArr = data.split(MARK);
		
		try {
			Connection conn = DriverManager.getConnection(url);
			
			PreparedStatement insertStmt = 
					conn.prepareStatement("INSERT INTO games (time, ip, data) VALUES (?, ?, ?)");
			insertStmt.setString(1, dataArr[0]);
			insertStmt.setString(2, dataArr[1]);
			insertStmt.setString(3, dataArr[2]);
			insertStmt.execute();
			
			// delete extra game record if lines of data exceeds 4
			PreparedStatement queryStmt = 
					conn.prepareStatement("SELECT * FROM games");
			ResultSet rset = queryStmt.executeQuery();
			int numRows = 0;
			while (rset.next()) {
				numRows++;
			}
			
			if (numRows > NUM_GAMES) {
				PreparedStatement deleteStmt = 
						conn.prepareStatement("DELETE FROM games WHERE time = (SELECT MIN(time) FROM games);");
				deleteStmt.execute();
			}
			
//			System.out.println(dataArr[2]);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String showGame() {
		String data = "";
		try {
			Connection conn = DriverManager.getConnection(url);
			PreparedStatement queryStmt = 
					conn.prepareStatement("SELECT * from games ORDER BY time DESC;");
			
			ResultSet rset = queryStmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			int numColumns = rsmd.getColumnCount() - 1;
			
			while (rset.next()) {
				for (int i = 1; i <= numColumns; i++) {
					Object o = rset.getObject(i);
					data += o.toString() + "\t";
				}
				data += "\n";
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static String loadGame(int rowIdx) {
		String data = "";
		try {
			Connection conn = DriverManager.getConnection(url);
			PreparedStatement queryStmt = 
					conn.prepareStatement("SELECT * from games ORDER BY time DESC;");
			
			ResultSet rset = queryStmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();

			int i = 0;
			while (i != rowIdx) {
				rset.next();
				i++;
			}
			Object o = rset.getObject(3);
			data += o.toString();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

}
