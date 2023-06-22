package mafia;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public abstract class Abstract {
	
	public static Stage stage; 	// 메인화면
	public static Stage stage1; // 로그인
	public static Stage stage2; // 회원가입
	public static Stage stage3; // 방리스트
	
	// 테이블뷰에쓸 방리스트
	public static ObservableList<Room> rlist = FXCollections.observableArrayList(); 
	
	// 알림 창들
	public static Stage stage4; // 로그인알림창
	
	public static Socket server;
	public static InetAddress ip;
	public static int port;
	public static PrintWriter printer;
	public static BufferedReader br;
	
	public static String nick,win,img,date,alive;
	public static int roomNumber;
	
	public static String msg,job;
	
}
