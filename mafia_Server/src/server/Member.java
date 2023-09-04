package server;

public class Member {
	
	// 닉네임
	String nickname;
	// 아이디
	String Id;
	// 승리횟수
	int winCount = 0;
	// 이미지 경로
	String image;
	// 마지막 접속 시간
	String date;
	
	String job;
	String alive;
	int count ;
	

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Member(String nickname, String id, int winCount,String date,String image) {
		this.nickname = nickname;
		this.Id = id;
		this.winCount = winCount;
		this.date = date;
		this.image = image;
	}

	public Member(Member member) {
		// TODO Auto-generated constructor stub
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getAlive() {
		return alive;
	}

	public void setAlive(String alive) {
		this.alive = alive;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
	
	
}
