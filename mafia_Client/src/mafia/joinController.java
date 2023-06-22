package mafia;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class joinController extends Client{
	
	@FXML private Button btnJoin,btnCancel,btnCheck;
	@FXML private TextField nickname,txtId;
	@FXML private PasswordField txtPw,txtRePw;
	
	// 프로필 이미지
	@FXML private ImageView profileImg;
	@FXML private ComboBox profileImgCombo;
	// 프로필 이미지 URL 목록을 담을 리스트
	private ObservableList<String> profileImageURl;
	
	
	// 서버에 보내줬을때 구분할 필드
	String idCheck = "idCheck"; // 아이디 중복확인
	String join = "join"; 		// 회원가입 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Platform.runLater(()->{
			txtId.requestFocus();
		});
		
		txtId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                txtPw.requestFocus();
            }
        });
		
		txtPw.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                txtRePw.requestFocus();
            }
        });
		
		txtRePw.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                nickname.requestFocus();
            }
        });
		
		txtId.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnJoin.fire();
            }
        });
		
		txtPw.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnJoin.fire();
            }
        });
		
		txtRePw.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnJoin.fire();
            }
        });
		
		nickname.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnJoin.fire();
            }
        });
		
		// profileImgCombo에 항목 추가
		profileImageURl = profileImgCombo.getItems();
		profileImageURl.addAll(
		        "imgs/mafia1.png", "imgs/mafia2.png", "imgs/mafia3.png",
		        "imgs/mafia4.png", "imgs/mafia5.png", "imgs/mafia6.png",
		        "imgs/mafia7.png", "imgs/mafia8.png", "imgs/mafia9.png",
		        "imgs/mafia10.png"
		);

		 // ComboBox에서 선택한 텍스트에 맞게 profileImg 업데이트
		 profileImgCombo.setOnAction(e -> {
		        String selectedImageUrl = (String)profileImgCombo.getSelectionModel().getSelectedItem();
		        if (selectedImageUrl != null) {
		            Platform.runLater(()->{
		                String imageUrl = getClass().getResource(selectedImageUrl).toExternalForm();
		                profileImg.setImage(new Image(imageUrl));
		                });
		        }
		 });
		
		// 체크 버튼 누를시 아이디 중복확인해주기
		btnCheck.setOnAction(e->{
			Platform.runLater(()->{
				String id = txtId.getText().trim();
				System.out.println("btnCheck click");
				System.out.println(id);				
				send(idCheck+","+id);
			});
		});
		
		// 회원가입 버튼 누를시 정보 보내주기
		btnJoin.setOnAction(e->{
			System.out.println("btnJoin click");
			String nick = nickname.getText().trim();
			String id = txtId.getText().trim();
			String pw = txtPw.getText().trim();
			String rePw = txtRePw.getText().trim();
			
			// 프로필 이미지 
			Image image = profileImg.getImage();
			String profileImg = image.getUrl();

			if(!pw.equals(rePw)) {
				// 다시 알림창 띄우기 얼러트로
				txtPw.requestFocus();
				txtPw.setText("");
				txtRePw.setText("");
				return;
			}
			send(join+","+id+","+pw+","+nick+","+profileImg);
		});
		
	
		
		// 닫기 버튼 누를시 스테이지 닫기
		btnCancel.setOnAction(e->{
			stage2.close();
		});
	}
	

	
}
