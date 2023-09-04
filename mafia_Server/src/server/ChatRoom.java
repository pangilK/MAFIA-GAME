package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ChatRoom {
	
	int roomNumber;
	String roomTitle;
	int maxUser;
	Vector<Member> ccUser = new Vector<>(); // 접속한 사람들
	boolean getStart = false;
	String mafiaSelect;
	String doctorSelect;
	List<String> voteList = new ArrayList<>();
	List<Integer> abilityList = new ArrayList<>();
	boolean mafiaS;
	boolean doctorS;
	
	public boolean isGetStart() {
		return getStart;
	}

	public void setGetStart(boolean getStart) {
		this.getStart = getStart;
	}

	public ChatRoom(String roomTitle,int maxUser) {
		this.roomTitle = roomTitle;
		this.maxUser = maxUser;
	}

	public boolean isMafiaS() {
		return mafiaS;
	}

	public void setMafiaS(boolean mafiaS) {
		this.mafiaS = mafiaS;
	}

	public boolean isDoctorS() {
		return doctorS;
	}

	public void setDoctorS(boolean doctorS) {
		this.doctorS = doctorS;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public void setRoomTitle(String roomTitle) {
		this.roomTitle = roomTitle;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}
	
	public Vector<Member> getCcUser() {
		return ccUser;
	}
	
	public void setCcUser(Vector<Member> ccUser) {
		this.ccUser = ccUser;
	}

	public String getMafiaSelect() {
		return mafiaSelect;
	}

	public void setMafiaSelect(String mafiaSelect) {
		this.mafiaSelect = mafiaSelect;
	}

	public String getDoctorSelect() {
		return doctorSelect;
	}

	public void setDoctorSelect(String doctorSelect) {
		this.doctorSelect = doctorSelect;
	}


	
	
}
