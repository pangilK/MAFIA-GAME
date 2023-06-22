package mafia;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public abstract class Client extends Abstract implements Initializable{
	
	Stage stage;
	GameController gc;
	Room room;
	// 시작할때 서버에 연결
	public void startClient() {
		try {
			String ip = "10.100.205.16";
			// this.ip = InetAddress.getByName(ip);
			server = new Socket(ip, 5001);
			printer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())), true);
			br = new BufferedReader(new InputStreamReader(server.getInputStream()));
		} catch (IOException e) {
			stopClient();
			return;
		}
		receive();
	}
	
	// 서버에 메세지 보내기
	public void send(String msg) {
		printer.println(msg);
	}
	
	// 서버에서 데이터를 받고 처리할 곳
	private void receive() {
		Thread t = new Thread(()->{
			while(true) {
				try {
					String receiveData = br.readLine();
					System.out.println("receiveData : "+receiveData);
		            String[] data = receiveData.split(",");
		            String code = data[0];

		            switch (code) {
		                case "success":
		                    success();
		                    Platform.runLater(()->{
		                        loginController.closeJoinforLogin();
		                    }); 

		                    break;
		                    
		                case "alreadyFailed":
		                    showAlert("로그인 실패", "이미 로그인되어 있는 계정입니다.", AlertType.WARNING);
		                    break;
		                    
		                case "failed":
		                    showAlert("로그인 실패", "아이디와 비밀번호를 다시 확인해주세요.", AlertType.INFORMATION);
		                    break;
		                    
		                case "JoinOk":
		                    showAlert("회원가입 완료", "회원가입이 완료되었습니다.", AlertType.INFORMATION);
		                    break;
		                    
		                case "JoinNo":
		                    showAlert("회원가입 실패", "이미 사용 중인 아이디이거나 닉네임입니다.", AlertType.INFORMATION);
		                    break;
		                    
		                case "idCheckOk":
		                    showAlert("사용 가능한 아이디", "사용 가능한 아이디입니다.", AlertType.INFORMATION);
		                    break;
		                    
		                case "idCheckNo":
		                    showAlert("중복된 아이디", "이미 사용 중인 아이디입니다.", AlertType.INFORMATION);
		                    break;
		                    
		                case "createRoom":
		                    String title = data[1];
		                    int currentClients = Integer.valueOf(data[2]);
		                    int maxClients = Integer.valueOf(data[3]);
		                    int roomNum = Integer.valueOf(data[4]);
		                    Room room = new Room(title, currentClients, maxClients, roomNum);
		                    rlist.add(room);
		                    break;
		                    
		                case "info":
		                    nick = data[1];
		                    win = data[2];
		                    img = data[3];
		                    date = data[4];
		                    break;
		                    
		                case "updateRoom":
		                    updateRoom(receiveData);
		                    break;
		                    
		                case "updateProf":
		                    updateProf(receiveData);
		                    break;
		                    
		                case "joinRoom":
		                    roomNum = Integer.valueOf(data[1]);
		                    roomNumber = roomNum;
		                    joinRoom();
		                    break;
		                    
		                case "roomJoinFail":
		                    showAlert("방에 입장할 수 없습니다.", "이미 게임이 시작되었거나 방이 가득 찼습니다.", AlertType.WARNING);
		                    break;
		                    
		                case "chat" :
		                    String msg = data[2];
		                    String nick = data[3];
		                    gc.chat(nick, msg);
		                    break;
		                    
		                case "gameStart" :
		                    gc.gameStart();
		                    break;
		                    
		                case "roomHi" :
		                    Toolkit toolkit = Toolkit.getDefaultToolkit();
		                    toolkit.beep();
		                    break;
		                    
		                case "job" :
		                    String job = data[1];
		                    this.job = job;
		                    gc.getJob(job);
		                    break;
		                    
		                case "noRoomManager":
		                    showAlert("게임을 시작할 수 없습니다.", "방장이 아닙니다.", AlertType.WARNING);
		                    break;
		                    
		                case "mafia":
		                    gc.announce("조사한 사람이 마피아입니다.");
		                    break;
		                    
		                case "noMafia":
		                    gc.announce("조사한 사람은 마피아가 아닙니다.");
		                    break;
		                    
		                case "deadChat":
		                    String deadMsg = data[2];
		                    String deadNick = data[3];
		                    gc.diechat(deadNick, deadMsg);
		                    break;
		                    
		                case "nobodyDie":
		                    gc.announce("투표로 아무도 사망하지 않았습니다.");
		                    // gc.night();
		                    break;
		                    
		                case "voteDie":
		                    String name = data[1];
		                    if (name.equals(this.nick)) {
		                        alive = "die";
		                        gc.voteX();
		                        gc.myDie();
		                    }
		                    gc.announce("투표로 " + name + "님이 사망하셨습니다.");
		                    //  gc.night();
		                    break;
		                    
		                case "startFail":
		                    showAlert("게임 시작이 불가능합니다.", "게임을 시작하기 위해선 최소 5명이 필요합니다.", AlertType.WARNING);
		                    break;
		                    
		                case "aliveList":
		                    gc.setComboBox();
		                    break;
		                    
		                case "kill":
		                    room = findRoom(roomNumber);
		                    for (int i = 0; i < room.ccUser.size(); i++) {
		                        nick = room.ccUser.get(i);
		                        if (nick.equals(data[1])) {
		                            gc.die(data[1]);
		                            break;
		                        }
		                    }
		                    gc.announce("----------- " + data[1] + "님이 마피아의 총에 맞아 사망하셨습니다. -----------");
		                    if (data[1].equals(this.nick)) {
		                        alive = "die";
		                        gc.myDie();
		                        gc.voteX();
		                    }
		                    // gc.day();
		                    break;
		                    
		                case "noKill":
		                    gc.announce("밤에 아무 일도 일어나지 않았습니다.");
		                    //  gc.day();
		                    break;
		                    
		                case "mafiaWin":
		                    showAlert("마피아가 승리했습니다", "아무도 마피아를 막을 수 없었습니다!", AlertType.INFORMATION);
		                    gc.endGame();
		                    break;
		                    
		                case "mafiaWin2":
		                    showAlert("마피아가 승리했습니다", "이미 남은 시민 한 명도 마피아에게 죽었습니다!", AlertType.INFORMATION);
		                    gc.endGame();
		                    break;
		                    
		                case "citizenWin":
		                    showAlert("시민이 승리했습니다", "마피아로부터 도시를 안전하게 지켜냈습니다!", AlertType.INFORMATION);
		                    gc.endGame();
		                    break;
		                    
		                case "voteTime":
		                    gc.voteTime();
		                    break;
		                    
		                case "null" :
		                	break;
		                   
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		            break;
		        }
		    }
		});
		t.setDaemon(true);
		t.start();
	}



	// 로그인 성공 시
	public void success() {
		Platform.runLater(()->{
			try {	
				FXMLLoader loader = new FXMLLoader(getClass().getResource("list.fxml"));
				Parent root = loader.load();
				stage3 = new Stage();
				stage3.setScene(new Scene(root));
				stage3.setResizable(false);
				stage3.show();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void joinRoom() {
		Platform.runLater(()->{
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
				Parent root = loader.load();
				gc = loader.getController();
				stage4 = new Stage();
				stage4.setScene(new Scene(root));
				stage4.setResizable(false);
				stage4.show();
				Platform.runLater(()->{
					stage3.close();
				});
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
	}
	
	public void stopClient() {
		if(server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e) {}
		}
	}

	public void showAlert(String Title, String message, AlertType alertType) {
		 Platform.runLater(() -> {
		        Alert alert = new Alert(alertType);
		        alert.setHeaderText(Title);
		        alert.setContentText(message);
		        alert.showAndWait();
		 });		
	}
	
	public Room findRoom(int roomNumber) {
		Room room = null;
		for(Room r : rlist) {
			if(r.getRoomNumber() == roomNumber) {
				room = r;
				break;
			}
		}
		return room;
	}
	
	public void updateRoom(String data) {
		String arr[] = data.split(",");
		int roomNumber = Integer.valueOf(arr[1]);
		int ccUser = Integer.valueOf(arr[2]);
		Room room = null;
		for(int i = 0 ; i < rlist.size() ; i++) {
			int rN = rlist.get(i).getRoomNumber();
			if(rN == roomNumber) {
				room = rlist.get(i);
				room.setCurrentClients(ccUser);
				break;
			}
		}
	
		for(int x = 3 ; x < arr.length ; x++) {
			if(!room.ccUser.contains(arr[x]) && !arr[x].equals(nick)) {
				room.ccUser.add(arr[x]);						
			}
			if(x == arr.length -1) {
				System.out.println("ccUser : " + room.ccUser);
			}
		}			
	}
	
	public void updateProf(String data) {
		String arr[] = data.split(",");
		int roomNumber = Integer.valueOf(arr[1]);
		Room room = findRoom(roomNumber);
		for(int x = 4 ; x < arr.length ; x++) {
			if(!room.ccUserImage.contains(arr[x]) && !arr[x].equals(img)) {
				room.ccUserImage.add(arr[x]);						
			}
		}			
	}


}
