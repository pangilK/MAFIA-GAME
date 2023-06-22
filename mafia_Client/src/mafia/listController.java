package mafia;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class listController extends Client{
	
	@FXML private Label nickName,winCount,Date; 	// 로그인한 사람 정보
	@FXML private ImageView profile; 				// 로그인한 사람 프로필 이미지
	@FXML private TextField txtTitle; 				// 방 제목 
	@FXML private TableView<Room> roomList; 		// 방 리스트
	@FXML private ComboBox<Integer> personCombo; 	// 방 만들때 최대 인원수를 보여줄 콤보 박스
	@FXML private Button btnCreateRoom,btnCancel;   // 방을 만들 버튼과 게임 종료 버튼
	
	ObservableList<Integer> personList;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Platform.runLater(()->{
			nickName.setText(nick);
			winCount.setText(win);
			Date.setText(date);
			profile.setImage(new Image(img));
			personList =  FXCollections.observableArrayList(5,6,7,8,9,10);
			personCombo.setItems(personList);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setColumn();
		});
		
		
		// 방 생성 버튼 클릭 시 이벤트
		btnCreateRoom.setOnAction(e->{
			String title = txtTitle.getText();
			if(title.equals("")) {
				txtTitle.requestFocus();
				return;
			}
			int maxClients = personCombo.getSelectionModel().getSelectedItem();
			if(maxClients != personCombo.getSelectionModel().getSelectedItem()) {
				personCombo.requestFocus();
				return;
			}
			send("createRoom,"+title+","+maxClients+","+nick);
		});
		
		// 방리스트에서 클릭시
		roomList.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				int clickCount = event.getClickCount();
				MouseButton btn = event.getButton();
				if(btn == MouseButton.PRIMARY && clickCount == 2) {
					Room room = roomList.getSelectionModel().getSelectedItem();
					// roomNumber = room.getRoomNumber();
					send("joinRoom,"+room.getRoomNumber()+","+nick);
				}
			}
			
		});
		
		
		// 게임 종료 버튼 클릭시 스테이지 닫기
		btnCancel.setOnAction(e->{
			stage3.close();
			send("logout,"+nick);
		});
		
	}

	public void setColumn() {
		Platform.runLater(()->{
			Field[] fields = Room.class.getDeclaredFields();
			int columnCount = fields.length;
			ObservableList<TableColumn<Room, ?>>  list = roomList.getColumns();
			for (int i = 0; i < columnCount-2; i++) {
				String fieldName = fields[i].getName();
				TableColumn<Room, ?> tColumn = list.get(i);
				tColumn.setCellValueFactory(new PropertyValueFactory<>(String.valueOf(fieldName)));
				tColumn.setEditable(false);
			}
			
			roomList.setItems(rlist);
			roomList.setEditable(false);			
		});
	}
	public void send(String msg) {
		printer.println(msg);
	}
	
	
}

