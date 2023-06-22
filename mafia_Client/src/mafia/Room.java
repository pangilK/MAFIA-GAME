package mafia;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Vector;

public class Room {
	
	public int roomNumber; 		// 방번호
	public String title;  			// 제목
	public int currentClients; 	// 현재인원수
	public int maxUser; 		// 최대인원수
	List<String> ccUser = new Vector<>(); 
	List<String> ccUserImage = new Vector<>();
	
	public List<String> getCcUserImage() {
		return ccUserImage;
	}
	
	public Room(String title, int currentClients, int maxUser,int roomNumber) {
		this.roomNumber = roomNumber;
		this.title = title;
		this.currentClients = currentClients;
		this.maxUser = maxUser;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCurrentClients() {
		return currentClients;
	}
	public void setCurrentClients(int currentClients) {
		this.currentClients = currentClients;
	}
	public int getMaxUser() {
		return maxUser;
	}
	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}
	
	public List<String> getCcUser() {
		return ccUser;
	}
	
	

	

}
