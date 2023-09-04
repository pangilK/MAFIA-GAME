package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {
	Connection conn = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	String url = "jdbc:mysql://주소:포트/이름";  // 데이터베이스경로
	String user = "계정이름"; // 계정 이름  ex :
	String pass = "비밀번호"; // 비밀번호  ex : 12345
	
	
	Database() {	// Database 객체 생성 시 데이터베이스 서버랑 연결
		try {	
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, pass);
			stmt = conn.createStatement();
			System.out.println("[Server] MySQL 서버 연동 성공");
		} catch(Exception e) {
			System.out.println("[Server] MySQL 서버 연동 실패> " + e.toString());
		}
	}
	
	public String logincheck(String id , String pw) {
		// mId mNick mPw winCount img
		try {
			stmt = conn.createStatement();
			String sql = "select mId,mPw from member_tbl";
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String userId = rs.getString(1);
				String pass = rs.getString(2);
				if(userId.equals(id) && pass.equals(pw)) {
					return "success";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs,stmt);
		}
		return "failed";
	}
	
	public String IdCheck(String id) {
		try {
			stmt = conn.createStatement();
			String sql = "SELECT mId FROM member_tbl";
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String name = rs.getString(1);
				if(name.equals(id)) {					
					close(rs,stmt);
					return "idCheckNo";
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs,stmt);
		}
		return "idCheckOk";
	}
	
	public String JoinCheck(String id, String pw , String nick,String img) {
		List<String> ok = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			String sql = "SELECT mId,mNick FROM member_tbl";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String mId = rs.getString(1);
				String nickName = rs.getString(2);
				ok.add(mId+","+nickName);
			}
			
			if(ok.size() == 0) {
				sql = "INSERT INTO member_tbl VALUES(?,?,?,?,?,now());";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);   	// 아이디
				pstmt.setString(2, nick); 	// 닉네임
				pstmt.setString(3, pw);   	// 비밀번호
				pstmt.setInt(4, 0);			// 승리횟수
				pstmt.setString(5, img); 	// 이미지경로
				pstmt.executeUpdate();
				return "JoinOk";
			}
			
			for(int i = 0 ; i < ok.size(); i++) {
				String arr[] = ok.get(i).split(",");
				if(!arr[0].equals(id) && !arr[1].equals(nick)) {
						sql = "INSERT INTO member_tbl VALUES(?,?,?,?,?,now());";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, id);   	// 아이디
						pstmt.setString(2, nick); 	// 닉네임
						pstmt.setString(3, pw);   	// 비밀번호
						pstmt.setInt(4, 0);			// 승리횟수
						pstmt.setString(5, img); 	// 이미지경로
						pstmt.executeUpdate();
						return "JoinOk";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs,stmt);
		}
		return "JoinNo";
	}
	
	public String loadInfo(String id){
		String nick = "";
		String winCount = "";
		String img = " ";
		String date = "";
		try {
			stmt = conn.createStatement();
			String sql = "SELECT mNick,winCount,img,date FROM member_tbl WHERE mId = '"+id+"'";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				nick = rs.getString("mNick");
				winCount = rs.getString("winCount");
				img = rs.getString("img");
				Date dateTime = rs.getTimestamp("date");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				date = formatter.format(dateTime);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(stmt,rs);
		}
		
		return nick+","+winCount+","+img+","+date;
	}
	
	
	public void close(AutoCloseable... closers) {
		for(AutoCloseable closer : closers) {
			if(closer != null) {
				try {
					closer.close();
				} catch (Exception e) {}
			}
		}
	}
}
