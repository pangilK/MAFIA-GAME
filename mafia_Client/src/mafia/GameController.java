package mafia;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class GameController extends Client{
	
	@FXML private Label lbl1, lbl2, lbl3, lbl4, lbl5, lbl6, lbl7, lbl8, lbl9, nickname, myJob,
						timer,stateLabel;
	@FXML private Button sendBtn, voteBtn, startBtn, aliveBtn;
	@FXML private ImageView prof1, prof2, prof3, prof4, prof5, prof6, prof7, prof8, prof9, myProf,
		blood1, blood2, blood3, blood4, blood5, blood6, blood7, blood8, blood9, blood0;
	@FXML private TextArea chatArea;
	@FXML private TextField textField;
	@FXML private ComboBox<String> comboBox;
	ObservableList<String> personList ;
	
	MyTimer myTimer = new MyTimer(300);
	
	private Timer timerT;		// 타이머
	int dayCount, nightCount, voteCount; 			// 타이머 카운트다운 값(시간)
    static String timerStr;		// 타이머 텍스트
    boolean isRun = true;		//
    boolean mafiaKill = false; 	// 마피아 능력 사용 여부
    boolean isKilled = false; 	// 마피아의 투표 및 의사의 능력 발동에 의해 시민 사망 여부 표시
    boolean gameState = false;
    boolean setting = true;
    String deadPerson ="닉네임"; 	// 죽은 사람 닉네임 저장
    List<Label> labelList = new ArrayList<>();				// 레이블 접근용 리스트
    List<ImageView> profileList = new ArrayList<>();
    List<ImageView> bloodList = new ArrayList<>();			// 블러드 이미지 접근용 리스트
    int day = 0;
    String roomNum;
    //Vector<String> member = new Vector<>();
    boolean pass =false;
    List<String> ccUser;
    List<String> ccUserImage;
    String state;
    
	
	public void createList() {
		
		labelList.add(lbl1);
		labelList.add(lbl2);
		labelList.add(lbl3);
		labelList.add(lbl4);
		labelList.add(lbl5);
		labelList.add(lbl6);
		labelList.add(lbl7);
		labelList.add(lbl8);
		labelList.add(lbl9);	
		//labelList.add(nickname);
		
		profileList.add(prof1);
		profileList.add(prof2);
		profileList.add(prof3);
		profileList.add(prof4);
		profileList.add(prof5);
		profileList.add(prof6);
		profileList.add(prof7);
		profileList.add(prof8);
		profileList.add(prof9);
		//profileList.add(prof);
		
		bloodList.add(blood1);
		bloodList.add(blood2);
		bloodList.add(blood3);
		bloodList.add(blood4);
		bloodList.add(blood5);
		bloodList.add(blood6);
		bloodList.add(blood7);
		bloodList.add(blood8);
		bloodList.add(blood9);
		//bloodList.add(blood0);
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createList();
		nickname.setText(nick);
		voteBtn.setDisable(true);
		myProf.setImage(new Image(img));
		send("joinRoomHello,"+roomNumber+","+nick);
		aliveBtn.setDisable(true);
		aliveBtn.setVisible(false);
		//스타트 버튼 클릭 이벤트 
		blood0.setVisible(false);
		
		startBtn.setOnAction(e->{
			send("gameStart,"+roomNumber+","+nick);
		});
		
		textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
            	sendBtn.fire();
            	aliveBtn.fire();
            }
        });
		
		sendBtn.setOnAction(e->{
			String msg = textField.getText();
			if(msg.equals("")) {
				return;
			}
			send("roomChat,"+roomNumber+","+nick+","+msg);
           	textField.setText("");
		});;
		
		aliveBtn.setOnAction(e->{
			String msg = textField.getText();
				if(msg.equals("")) {
					return;
				}
				send("aliveChat,"+roomNumber+","+nick+","+msg+","+alive);
		       	textField.setText("");
		});
		setting();
		
		voteBtn.setOnAction(e->{
			String selectNick = comboBox.getSelectionModel().getSelectedItem();
			if(selectNick.equals("")) {
				return;
			}
			send("gameUtil,"+state+","+selectNick+","+nick+","+roomNumber);
			voteBtn.setDisable(true);
		});
	}
	
	
	public void gameStart() { 
		alive = "alive";
		gameState = true;
		setting = false;
		Platform.runLater(()->{
			startBtn.setDisable(true);
			sendBtn.setDisable(true);
			sendBtn.setVisible(false);
			// 게임이 시작된 이후로 사망자 채팅으로 하기위해 원래 SEND버튼을 막고
			// 사망자 채팅의 기능을하는 버튼으로 대체 --> ALIVEBTN
			aliveBtn.setDisable(false);
			aliveBtn.setVisible(true);
			// 그 후 게임이 시작되고나서 방에 있는 유저를 콤보박스에 투표 or 능력 사용을위해 콤보박스에 세팅
			personList = FXCollections.observableArrayList(ccUser);
			personList.add(nick);
			comboBox.setItems(personList);
			
			textField.setDisable(true);
			
			chatArea.appendText("-------------- *게임을 시작합니다* -------------- \n");
			// chatArea.appendText("-------------- *직업 분배중입니다* -------------- \n");
			chatArea.appendText("-------------- *직업을 확인하세요* ------------- \n");
			
			textField.setDisable(false);
		});
	    night();
	}
	
	
	public void night() {
		if(gameState) {
			day++;
			state = "밤";
			Platform.runLater(()->{
				comboBox.getSelectionModel().clearSelection();
				voteBtn.setText("ABILITY");
				voteBtn.setDisable(false);
				aliveBtn.setDisable(true);
				textField.setDisable(true);
				setState();
			});
			timerT = new Timer();
			nightCount = 20;
			TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	                if (nightCount > 0) {
	                    int sec = nightCount % 60;
	                    int min = nightCount / 60;
	                    final String timerStr = min + " : " + sec;
	                    Platform.runLater(() -> timer.setText(timerStr));
	                    nightCount--;
	                } else {
	                    timerT.cancel();
	                    Platform.runLater(() -> timer.setText("0:00"));
	                    if(alive.equals("alive") && ( job.equals("마피아") || job.equals("의사")) ) {
	                    	send("ability,"+roomNumber);	                    	
	                    }
	                    day();
	                }
	            }
	        };
	        Platform.runLater(()->{
		        chatArea.appendText("           * 밤이 되었습니다. *\n");
		        chatArea.appendText("     * 마피아는 죽일 시민을 선택해주세요. *\n");
	        });
			timerT.schedule(task,0,1000);
		}
	}
	
	public void day() {
		if(gameState) {		
			state = "낮";
			Platform.runLater(()->{
				aliveBtn.setDisable(false);
				comboBox.getSelectionModel().clearSelection();
				voteBtn.setText("VOTE");
				voteBtn.setDisable(true);
				textField.setDisable(false);
				setState();
			});
			timerT = new Timer();
			dayCount = 120;
			TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	                if (dayCount > 0) {
	                    int sec = dayCount % 60;
	                    int min = dayCount / 60;
	                    final String timerStr = min + " : " + sec;
	
	                    Platform.runLater(() -> timer.setText(timerStr));
	                    dayCount--;
	                } else {
	                    timerT.cancel();
	                    Platform.runLater(() -> timer.setText("0:00"));
	                    //send("endDay,"+roomNumber+","+nick);
	                    voteTime();
	                }
	            }
	        };
	        Platform.runLater(()->{
		        chatArea.appendText("           * 낮이 되었습니다. *\n");
		        chatArea.appendText("  * 시민들은 회의를 통해 마피아를 색출해주세요. *\n");
	        });
	        timerT.schedule(task,0,1000);
		}

	}
	
	public void voteTime() {
		if(gameState) {
			Platform.runLater(()->{
				comboBox.getSelectionModel().clearSelection();
				voteBtn.setText("VOTE");
				voteBtn.setDisable(false);
			});
			timerT = new Timer();
			voteCount = 20;
			TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	                if (voteCount > 0) {
	                    int sec = voteCount % 60;
	                    int min = voteCount / 60;
	                    final String timerStr = min + " : " + sec;
	
	                    Platform.runLater(() -> timer.setText(timerStr));
	                    voteCount--;
	                } else {
	                    timerT.cancel();
	                    // 타이머시간이 다되면 서버에서 투표결과처리
	                    if(comboBox.getSelectionModel().getSelectedItem() == null && 
	                       alive.equals("alive")) {
	                    	send("gameUtil,"+state+","+"무효표,"+nick+","+roomNumber);
	                    }
	                    Platform.runLater(() -> {
	                    	timer.setText("0:00");
	                    	voteBtn.setDisable(true);
	                    });
	                    voteResult();
	                }
	            }
	        };
	        Platform.runLater(()->{
	        	chatArea.appendText("           * 투표시간이 되었습니다. *\n");
	            chatArea.appendText("     * 투표를 통해 죽일 사람을 선정해주세요. *\n");
	        });
	        timerT.schedule(task,0,1000);
		}
	}

	// 일반 채팅
	public void chat(String nick, String msg) {
		Platform.runLater(()->{			
			chatArea.appendText(nick+" : "+msg+"\n");
		});
	}
	
	// 사망자 채팅
	public void diechat(String nick, String msg) {
		Platform.runLater(()->{			
			chatArea.appendText("DEAD - "+nick+" : "+msg+"\n");
		});
	}
	
	// 시스템 메시지
	public void	announce(String msg) {
		Platform.runLater(()->{
			chatArea.appendText(msg+"\n");
		});
	}
	
	public void getJob(String job) {
		Platform.runLater(()->{
			myJob.setText(job);
		});
	}
	
	public void setComboBox() {
		Platform.runLater(()->{		
			personList.clear();
			personList = FXCollections.observableArrayList(ccUser);
			personList.add(nick);
			comboBox.setItems(personList);
		});
	}
	
	public void voteX() {
		Platform.runLater(()->{
			voteBtn.setDisable(true);
			voteBtn.setVisible(false);
		});
	}
	
	public void setting() {
	    Thread thread = new Thread(() -> {
	        while (setting) {
	            synchronized (rlist) {
	                for (Room room : rlist) {
	                    if (room.getRoomNumber() == roomNumber) {
	                        ccUser = room.getCcUser();
	                        ccUserImage = room.getCcUserImage();
	                        Platform.runLater(() -> updateProfile(ccUser,ccUserImage));
	                        break;
	                    }
	                }
	            }
	            try {
	                Thread.sleep(1000); // 1초마다 갱신
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    });
	    thread.setDaemon(true);
	    thread.start();
	}
	private void updateProfile(List<String> ccUser,List<String> ccUserImage) {
	       Platform.runLater(() -> {
	           for (int i = 0; i < labelList.size(); i++) {
	               if (i < ccUser.size()) {
	                   String userNick = ccUser.get(i);
	                   Label label = labelList.get(i);
	                   String img = ccUserImage.get(i);
	                   label.setText(userNick);
	                   bloodList.get(i).setVisible(false);
	                   // 이미지 경로를 가져와서 프로필 이미지 업데이트
	                   // image.setImage(new Image("imgs/mafia5.png"));
	                   profileList.get(i).setImage(new Image(img));
	               } else {
	                   labelList.get(i).setText("");
	               }
	           }
	       });
	   }
	   
	
	public void die(String nick){
		Platform.runLater(() -> {
		  for(int i = 0; i < ccUser.size(); i++) {
			  if(ccUser.get(i).equals(nick)) {
				  bloodList.get(i).setVisible(true);
			  }
		  }
		});
	}
	
	public void myDie() {
		Platform.runLater(()->{
			blood0.setVisible(true);
		});
	}
	
	public void endGame() {
		gameState = false;
		Platform.runLater(()->{
			day = 0;
			setting = true;
			setting();
			chatArea.clear();
			timerT.cancel();
			aliveBtn.setDisable(true);
			aliveBtn.setVisible(false);
			sendBtn.setDisable(false);
			sendBtn.setVisible(true);
			stateLabel.setText("게임시작 전");
			dayCount = 0;
			nightCount = 0;
			voteCount = 0;
			timer.setText("0:00");
			startBtn.setDisable(false);
			blood0.setVisible(false);
		});
	}
	
	public void setState() {
		if(day == 1 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("첫날 밤"));
		if(day == 1 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("첫날 낮"));
		if(day == 2 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("둘째날 밤"));
		if(day == 2 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("둘째날 낮"));
		if(day == 3 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("셋째날 밤"));
		if(day == 3 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("셋째날 낮"));
		if(day == 4 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("넷째날 밤"));
		if(day == 4 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("넷째날 낮"));
		if(day == 5 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("다섯째날 밤"));
		if(day == 5 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("다섯째날 낮"));
		if(day == 6 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("여섯째날 밤"));
		if(day == 6 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("여섯째날 낮"));
		if(day == 7 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("일곱째날 밤"));
		if(day == 7 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("일곱째날 낮"));
		if(day == 8 && state.equals("밤")) Platform.runLater(() -> stateLabel.setText("여덟째날 밤"));
		if(day == 8 && state.equals("낮")) Platform.runLater(() -> stateLabel.setText("여덟째날 낮"));
	}
	
	public void voteResult() {
		if(alive.equals("alive")) {
        	send("vote,"+roomNumber+","+nick);
        }
		night();
	}
}
