package game;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameTimer extends JPanel implements Runnable {

	public static int id;
	
	private static final int UNIT_SIDE = 15;
	private static final int TIMER_WIDTH = UNIT_SIDE * 16;
	private static final int TIMER_HEIGHT = UNIT_SIDE * 2;
	private boolean isRunning;
	
	private int timeLeft;	
	JPanel timerPanel;
	private JLabel timerLabel;
	
	public GameTimer(int secs) {
		this.timeLeft = secs;
		isRunning = true;
		createTimerPanel();
	}
	
	private void createTimerPanel() {
		timerLabel = new JLabel("Time Remaining: " + timeLeft);
		add(timerLabel);
		setPreferredSize(new Dimension(TIMER_WIDTH, TIMER_HEIGHT));
	}
	
	public boolean isRunning() { return isRunning; }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning() && timeLeft > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeLeft--;
			timerLabel.setText("Time Remaining: " + timeLeft);
		}
		isRunning = false;
	}
	
	public void stop() {
		isRunning = false;
	}
	
	public void resume() {
		isRunning = true;
	}

	public int getScore() { return timeLeft; }
	
	public void setScore(int score) { timeLeft = score; }
	
	public void setMessage(String str) { timerLabel.setText(str); }
}
