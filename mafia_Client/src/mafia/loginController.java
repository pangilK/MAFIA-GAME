package mafia;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class loginController extends Client {
	
	@FXML private TextField txtId;
	@FXML private PasswordField txtPw;
	@FXML private Button btnLogin,btnJoin;
	private static Stage loginStage;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Platform.runLater(()->{
			startClient();
			txtId.requestFocus();
		});
		
		// 아이디 입력창에서 tab키를 누르면 비밀번호 입력창으로 포커스 이동
		txtId.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.TAB) {
	            txtPw.requestFocus();
	        }
	    });
		
		// 아이디 입력창에서 엔터를 누르면 로그인 버튼 실행
		txtId.setOnAction(event -> {
	        btnLogin.fire();
	    });

		// 비밀번호 입력창에서 엔터를 누르면 로그인 버튼 실행
	    txtPw.setOnAction(event -> {
	        btnLogin.fire();
	    });
		
		// 로그인 버튼 클릭시 입출력 스트림으로 아이디 패스워드 보내주기
		btnLogin.setOnAction(e->{
			// 엔터 눌러졌을때도 되게 메소드로 바꾸기
			String id = txtId.getText().trim();
			if(id.equals("")) {
				txtId.requestFocus();
				return;
			}
			String pw = txtPw.getText().trim();
			if(pw.equals("")) {
				txtPw.requestFocus();
				return;
			}
			send("login,"+id+","+pw);
		});
	
		
		// 조인 버튼 클릭시 회원가입 창 열어주기
		btnJoin.setOnAction(e->{
			try {
				Parent root = (Parent) FXMLLoader.load(getClass().getResource("join.fxml"));
				stage2 = new Stage();
				stage2.setScene(new Scene(root));
				stage2.show();
				stage2.setResizable(false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
	}
	
	public static void setJoinStage(Stage stage) {
		   loginStage = stage;
	}
		      
	public static void closeJoinforLogin() {
		Platform.runLater(() -> {
		  if (loginStage != null) {
		     loginStage.close();
		      }
		  });
	}

}

//ㅎㅇㅎㅇ
