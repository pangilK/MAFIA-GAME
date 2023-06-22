package mafia;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
    private Timer timer;
    private int count; // 타이머 카운트다운 값
    static String timerStr;

    public MyTimer(int count) {
    	this.count = count;
        timer = new Timer();
    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (count > 0) {
                	int sec = count%60;
                	int min = count/60;
                	timerStr = min+" : "+sec;
                    //System.out.println(timerStr);
                    //send(timer);
                    count--;
                } else {
                    timer.cancel();
                    System.out.println("타이머 종료");
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000); // 1초마다 실행
    }

    public static void main(String[] args) {
        MyTimer myTimer = new MyTimer(300);
        myTimer.start();
    }
    
    public static String send() {
    	return timerStr;
    }
}
