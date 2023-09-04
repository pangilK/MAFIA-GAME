package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// 연결된 client정보를 저장하여 입력과 출력을 담당할 class
public class ServerTask implements Runnable {

	Socket client;
	ServerController sc;
	PrintWriter printer;
	BufferedReader reader;
	boolean isRun = true;	
	Database db;
	List<String> job;
	int iIs;
	int c;
	
	public ServerTask(Socket client, ServerController sc) {
		this.client = client;
		this.sc = sc;
	
		try {
			OutputStream os = client.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter writer = new BufferedWriter(osw);
			printer = new PrintWriter(writer,true);
			
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
		} catch (IOException e) {
			sc.printText("Client 연결 오류 : " + e.getMessage());
		}
	}
	
	// receive 역할을 담당
	public void run() {
		sc.printText(client.getRemoteSocketAddress()+" receive 시작");
		
		String dbs; // 데이터베이스 클래스에서 호출해서 보내줄 스트링
		
		String id;
		String pw;
		String nick;
		String img;
		
		String title;
		int roomNumber;
		
		String msg;
		
		Member member;
		while(isRun) {
			try {

				String receiveData = reader.readLine();
				sc.printText(receiveData);
				if(receiveData == null) {
					break;
				}
				
				String[] data = receiveData.split(",");
				String code = data[0];
		
				
				System.out.println(Arrays.toString(data));
				switch(code) {
				
					case "login" : // 로그인
							id = data[1].trim();
							pw = data[2].trim();
							db = new Database();
							
							dbs = db.logincheck(id,pw);
							if(dbs.equals("failed")) {
								printer.println(dbs+",");
							}
							if(dbs.equals("success")) {
								boolean isOk = true;
								for(Member m : sc.mlist) {
									if(m.getId().equals(id)) {
										printer.println("alreadyFailed,");
										isOk = false;
										break;
									}
								}
								if(isOk) {
									printer.println(dbs);
									
									String info = db.loadInfo(id);
									printer.println("info"+","+info);
									String mInfo[] = info.split(",");
									sc.clients.put(mInfo[0], printer);
									member = new Member(mInfo[0],id,Integer.valueOf(mInfo[1]),mInfo[3],mInfo[2]);
									sc.mlist.add(member);
									
									if(sc.rlist.size() != 0 ) {
										for(int i = 0 ; i < sc.rlist.size() ; i++ ) {
											String roomtitle = sc.rlist.get(i).getRoomTitle();
											String ccUser = String.valueOf(
												sc.rlist.get(i).ccUser.size()
													);
											String maxUser = String.valueOf(
												sc.rlist.get(i).getMaxUser()
												);
											String roomNum = String.valueOf(
													sc.rlist.get(i).getRoomNumber()
												);
											sc.printText("createRoom"+","+
												roomtitle+","+ccUser+","+maxUser+","+roomNum
												);
											printer.println(
												"createRoom"+","+
												roomtitle+","+ccUser+","+maxUser+","+roomNum
												);
										}
									}
									sc.printText(id+"님이 로그인 하셨습니다.");
								}
							}
							
							break;
							
					case "join" : // 회원가입
							id = data[1].trim();
							pw = data[2].trim();
							nick = data[3].trim();
							img = data[4].trim();
							db = new Database();
							dbs = db.JoinCheck(id, pw, nick,img);
							printer.println(dbs+",");
							sc.printText(nick+"님이 회원가입하셨습니다.");
							break;
							
					case "idCheck" : // 아이디 중복체크
							id = data[1].trim();
							db = new Database();
							dbs = db.IdCheck(id);
							printer.println(dbs+",");
							break;
							
					case "createRoom" : // 방 생성
							sc.printText(receiveData);
							title = data[1].trim();
							int maxUser = Integer.valueOf(data[2].trim());
							nick = data[3].trim();
							
							ChatRoom room = new ChatRoom(title,maxUser);
							if(sc.rlist.size() > 0) {
								room.setRoomNumber(sc.rlist.get(sc.rlist.size()-1).getRoomNumber()+1);								
							}else {								
								room.setRoomNumber(1); 
							}
							sc.rlist.add(room);
							
							member = findMember(nick);
							room.ccUser.add(member);
							
							printer.println("joinRoom,"+room.getRoomNumber()+","+nick);
						
							broadCast(
								"createRoom"+","+
								room.getRoomTitle()+","+ 			    // 방제목
								String.valueOf(room.ccUser.size())+","+ // 현재인원
								String.valueOf(room.getMaxUser())+","+  // 최대인원
								String.valueOf(room.getRoomNumber())
							);
							break;
							
					case "joinRoom" : // 방 입장
							sc.printText(receiveData);
							int RoomNumber = Integer.valueOf(data[1].trim());
							nick = data[2].trim();
							
							room = findRoom(RoomNumber);
							member = findMember(nick);
							if(room.ccUser.size() == room.maxUser || room.isGetStart()) {
								printer.println("roomJoinFail,");
							}else {
								room.ccUser.add(member);
								updateRoom(room);
								updateRoom2(room,nick);
								printer.println("joinRoom,"+room.getRoomNumber()+","+nick);
								sc.printText(nick+"님이"+room.getRoomNumber()+"번방에 입장하셨습니다.");							
							}
							
							break;
							
					case "joinOut" : // 회원탈퇴할때 db에서 삭제
							break;
							
					case "logout" :  // 로그아웃일때 DB에 업데이트 후 종료
							break;
							
					case "gameEnd" : // 게임이 끝났을때 DB에 승리횟수 업데이트
							break;
							
					case "roomChat" :
							String arr[] = receiveData.split(",");
							roomNumber = Integer.valueOf(arr[1]);
							nick = arr[2];
							msg = arr[3];
							room = findRoom(roomNumber);
							roomCast(msg, room,nick);

							break;
							
					case "gameStart" :
							String game[] = receiveData.split(",");
							roomNumber = Integer.valueOf(game[1]);
							nick = game[2];
							boolean ok = false;
							room = findRoom(roomNumber);
							
							if(nick.equals(room.ccUser.get(0).getNickname())) {
								if(room.ccUser.size() > 4) {
									room.setGetStart(true);
									roomStart(room);
									ok = true;			
								}else {
									// 3명 이상이 아닐땐 게임시작 X
									printer.println("startFail,");
								}
							}else {
								// 방장이 아닐때 실행 불가
								printer.println("noRoomManager,");
							}
							
							if(ok) {
								job = new ArrayList<String>();		
								job.add("마피아");
								job.add("경찰");
								job.add("시민");
								job.add("의사");
								job.add("마피아");
								if(room.ccUser.size()>=6)  job.add("시민");
								if(room.ccUser.size()>=7)  job.add("마피아");
								if(room.ccUser.size()>=8)  job.add("시민");
								if(room.ccUser.size()>=9)  job.add("시민");
								if(room.ccUser.size()>=10) job.add("마피아");
								Collections.shuffle(job);
								for(int i = 0 ; i < room.ccUser.size(); i++) {
									room.ccUser.get(i).setJob(job.get(i));
								}
								gameStart(room);
								jobCast(room);
							}
							
							break;
							
					case "joinRoomHello" :
							String hi[] = receiveData.split(",");
							roomNumber = Integer.valueOf(hi[1]);
							nick = hi[2];
							room = findRoom(roomNumber);
							roomHi(room,nick);
							break;
					
					case "gameUtil":
	                     	String gameUtil[] = receiveData.split(",");
	                     	String state = gameUtil[1];
	                     	String selectNick = gameUtil[2];
		                    String myNick = gameUtil[3];
		                    roomNumber = Integer.valueOf(gameUtil[4]);
		                    room = findRoom(roomNumber);
		                    String job = findMember(myNick).getJob();
		                    
		                    if(state.equals("낮")) {
			                    room.voteList.add(selectNick);
		                    } // end day
		                    
		                    if(state.equals("밤")) {
		                    	switch(job) {
		                    	
		                    		case "마피아" :
		                    			room.setMafiaSelect(selectNick);
		                    			room.setMafiaS(true);
		                    			break;
		                    			
		                    		case "의사" :
		                    			room.setDoctorSelect(selectNick);
		                    			room.setDoctorS(true);
		                    			break;
		                    			
		                    		case "경찰"  :
		                    			String sj = findMember(selectNick).getJob();
		                    			if(sj.equals("마피아"))  printer.println("mafia,");	                    			
				                    	if(!sj.equals("마피아")) printer.println("noMafia,");
		                    			break;
		                    		
		                    		case "시민"  :
		                    			printer.println("noJob,");
		                    			break;
		                    			
		                    	}
			                    
		                    } // end night
	                     
	                     break;
	                     
				   case "vote" :
					   	String vote[] = receiveData.split(",");
					   	roomNumber = Integer.valueOf(vote[1]);
					   	room = findRoom(roomNumber);
	                    int aliveCount = getRoomAlive(room);
	                    if(room.voteList.size() == aliveCount) {
	                    	String name = vote(room);
	                    	if(name.equals("noDie")) {
	                    		roomData(room,"nobodyDie,");
	                    	}else {
	                    		findMember(name).setAlive("die");
	                    		roomData(room,"voteDie,"+name);
	                    	}
	                    }
	                    aliveList(room);
	                    gameEndCheck(room);
						break;
						
				   case "ability" :
					   	String abil[] = receiveData.split(",");
					   	roomNumber = Integer.valueOf(abil[1]);
					   	room = findRoom(roomNumber);

						   	if(room.isDoctorS() && room.isMafiaS()) {
						   	   String doctorSelect = room.getDoctorSelect();
						   	   String mafiaSelect = room.getMafiaSelect();
						   	   if(doctorSelect.equals(mafiaSelect) && mafiaSelect != null) {
						   		   roomData(room,"noKill");
						   		   roomMDClear(room);
						   	   }else {
						   		   roomData(room,"kill,"+mafiaSelect);
						   		   findMember(mafiaSelect).setAlive("die");
						   		   roomMDClear(room);
						   	   }
						   	}
						   	
						   	if(!room.isDoctorS() && room.isMafiaS()) {
						   		String mafiaSelect = room.getMafiaSelect();
						   		if(mafiaSelect != null) {
							   		roomData(room,"kill,"+mafiaSelect);
							   		findMember(mafiaSelect).setAlive("die");
							   		roomMDClear(room);
						   		}
						   	}
					   	
					   	aliveList(room);
	                    gameEndCheck(room);
					    break;
	                     
	               case "aliveChat" :
	            	    String aliveChat[] = receiveData.split(",");
						roomNumber = Integer.valueOf(aliveChat[1]);
						nick = aliveChat[2];
						msg = aliveChat[3];
						String alive = aliveChat[4];
						for(int i = 0 ; i < sc.rlist.size(); i++) {
							int rN = sc.rlist.get(i).getRoomNumber();
							if(rN == roomNumber) {
								if(alive.equals("alive")) {
									roomCast(msg, sc.rlist.get(i),nick);									
								}else if(alive.equals("die")){
									dieCast(msg,sc.rlist.get(i),nick);
								}
							}
						}
						break;
						
	               case "endDay" :
	            	    String endDay[] = receiveData.split(",");
						roomNumber = Integer.valueOf(endDay[1]);
						nick = endDay[2];
						room = findRoom(roomNumber);
						if(nick.equals(room.ccUser.get(0).getNickname())) {
							roomData(room,"voteTime,");
						}
	            	   	break;
				}
				
				
			} catch (IOException e) {
				sc.printText("client 연결 종료 : " + e.getMessage());
				isRun = false;
			}
			
		}// while
		
	}
	
	// 전체에게 데이터날리기
	public void broadCast(String msg) {
		for(PrintWriter p : sc.clients.values()) {
			p.println(msg);
		}
	}
	
	// 방 내에서 전체 채팅
	public void roomCast(String msg ,ChatRoom room, String nick) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String nickName = room.ccUser.get(i).getNickname();
			PrintWriter p = sc.clients.get(nickName);
			p.println("chat,"+room+","+msg+","+nick);
		}
	}
	
	// 직업 가르켜주기
	public void jobCast(ChatRoom room) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String nickName = room.ccUser.get(i).getNickname();
			String job = room.ccUser.get(i).getJob();
			PrintWriter p = sc.clients.get(nickName);
			p.println("job,"+job);
		}
	}
	
	// 룸에 입장했을때 알려주기
	public void roomHi(ChatRoom room,String nick) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String nickName = room.ccUser.get(i).getNickname();
			PrintWriter p = sc.clients.get(nickName);
			p.println("roomHi,"+room+","+nick);
		}
	}
	
	// 룸에 있는 인원에게 데이터 전송
	public void roomData(ChatRoom room,String msg) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String nickName = room.ccUser.get(i).getNickname();
			PrintWriter p = sc.clients.get(nickName);
			p.println(msg);
		}
	}
	
	// 룸이 시작하는 메소드
	public void roomStart(ChatRoom room) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String nickName = room.ccUser.get(i).getNickname();
			PrintWriter p = sc.clients.get(nickName);
			p.println("gameStart");
		}
	}

	// 사망자 채팅
	public void dieCast(String msg , ChatRoom room,String nick) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			String alive = room.ccUser.get(i).getAlive();
			String nick22 = room.ccUser.get(i).getNickname();
			if(alive.equals("die")) {
				PrintWriter p = sc.clients.get(nick22);
				p.println("deadChat,"+room+","+msg+","+nick);
			}
		}
	}
	
	// 게임이 시작되면 전부다 살아있는 상태로 바꾸기
	public void gameStart(ChatRoom room) {
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			room.ccUser.get(i).setAlive("alive");
		}
	}
	
	// 방번호를 받아 방을 찾아주기
	public ChatRoom findRoom(int roomNumber) {
		ChatRoom room = null;
		for(ChatRoom r : sc.rlist) {
			if(r.getRoomNumber() == roomNumber) {
				room = r;
				break;
			}
		}
		return room;
	}
	
	// 닉네임을 받아 멤버를 찾아주기
	public Member findMember(String nick) {
		Member member = null;
		for(Member m : sc.mlist) {
			if(m.getNickname().equals(nick)) {
				member = m;
				break;
			}
		}
		return member;
	}
	
	// 방을 받아 룸을 업데이트 시키는 함수
	// ex) data = updateRoom,방번호,방에현재접속해있는인원,방에협재접속해있는인원의닉네임,방에협재접속해있는인원의닉네임....
	public void updateRoom(ChatRoom room) {
		String data = "updateRoom,"+room.getRoomNumber()+","+room.ccUser.size()+",";
		for(int i = 0 ; i < room.ccUser.size(); i++) {
			if(i == room.ccUser.size() -1) {
				data += room.ccUser.get(i).getNickname();
			}else {
				data += room.ccUser.get(i).getNickname()+",";
			}
		}
		broadCast(data);
	}
	
	// 방을 받아 방에 있는 사람들 프로필 이미지를 보내주는 함수
	public void updateRoom2(ChatRoom room, String nick) {
		String data = "updateProf,"+room.getRoomNumber()+","+nick+","+room.ccUser.size()+",";
		for(int i = 0 ; i < room.ccUser.size(); i++) {
			if(i == room.ccUser.size() -1) {
				data += room.ccUser.get(i).getImage();
			}else {
				data += room.ccUser.get(i).getImage()+",";
			}
		}
		broadCast(data);
	}
	
	public String vote(ChatRoom room) {
		int max = 0;
		int secondMax = 0;
		int a = 0;
		String die = null;
		for(int i = 0 ; i < room.voteList.size(); i++) {
			a = Collections.frequency(room.voteList, room.voteList.get(i));
            if (a > max) {
                secondMax = max;
                max = a;
                die = room.voteList.get(i);
            } else if (a > secondMax && a != max) {
                secondMax = a;
            }
            
		}
		if(max == secondMax) {
            die = "noDie" ;
            room.voteList.clear();
            return die;
        }else {
        	room.voteList.clear();
        	return die;        	
        }
	}

	public int getRoomAlive(ChatRoom room) {
		int aliveCount = 0;
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			if(room.ccUser.get(i).getAlive().equals("alive")) {
				aliveCount++;
			}
		}
		return aliveCount;
	}
	
	public int getRoomMafiaAlive(ChatRoom room) {
		int aliveCount = 0;
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			if(room.ccUser.get(i).getAlive().equals("alive") && 
			   room.ccUser.get(i).getJob().equals("마피아")) {
				aliveCount++;
			}
		}
		return aliveCount;
	}
	
	public int getRoomDoctorAlive(ChatRoom room) {
		int aliveCount = 0;
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			if(room.ccUser.get(i).getAlive().equals("alive") && 
			   room.ccUser.get(i).getJob().equals("의사")) {
				aliveCount++;
			}
		}
		return aliveCount; 
	}
	public int getRoomCitizenAlive(ChatRoom room) {
		int aliveCount = 0;
		for(int i = 0 ; i < room.ccUser.size() ; i++) {
			if(room.ccUser.get(i).getAlive().equals("alive") && 
			   !room.ccUser.get(i).getJob().equals("마피아")) {
				aliveCount++;
			}
		}
		return aliveCount;
	}
	
	public String ability(ChatRoom room) {
		String mafiaS = room.getMafiaSelect();
		String docterS = room.getDoctorSelect();
		if(mafiaS.equals(docterS)) {
			return "noDie";
		}
		return mafiaS;
	}
	
	public void aliveList(ChatRoom room) {
		String data = "";
		for(int i = 0; i<room.ccUser.size();i++) {
			if(room.ccUser.get(i).getAlive().equals("alive")) {
				if(i == room.ccUser.size() -1) {
					data += room.ccUser.get(i).getNickname();
				}else {
					data += room.ccUser.get(i).getNickname()+",";
				}								
			}
		}
		// aliveList,닉네임,닉네임,닉네임,닉네임,닉네임
	    for (int i = 0; i < room.ccUser.size(); i++) {
	        String nickName = room.ccUser.get(i).getNickname();
	        PrintWriter p = sc.clients.get(nickName);
	        p.println("aliveList,"+room.getRoomNumber()+","+data);
	    }
	}
	
	public void roomMDClear(ChatRoom room) {
		room.setDoctorSelect(null);
		room.setMafiaSelect(null);
	}
	
	public void gameEndCheck(ChatRoom room) {
		int mafiaCount = 0;
		int citizenCount = 0;
		for(int i = 0; i < room.ccUser.size(); i++) {
			Member user = room.ccUser.get(i);
			if(user.getJob().equals("마피아") && user.getAlive().equals("alive")) { 
				mafiaCount++;
			}
			if(!user.getJob().equals("마피아") && user.getAlive().equals("alive")) {
				citizenCount++;
			}
		}
		if(mafiaCount == 0) {
			roomData(room,"citizenWin,");
			sc.printText("시민승");
		}
		if(citizenCount == 0) {
			roomData(room,"mafiaWin,");
			sc.printText("마피아승");
		}
		if(mafiaCount == 1 && citizenCount == 1) {
			roomData(room,"mafiaWin2,");
			sc.printText("마피아승");
		}
		if(mafiaCount > citizenCount) {
			roomData(room,"mafiaWin2,");
			sc.printText("마피아승");
		}
	}

}
