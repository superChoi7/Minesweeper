package game;
import javax.swing.JFrame;

import server.*;

import java.awt.Frame;

public class MinesweeperMain {
	public static int id;
	
	public static void startGame() {
		new Thread(new GameFrame(), "game" + id).start();
		id++;
	}
	
	public static void startGame(GameFrame game) {
		new Thread(game, "game" + id).start();
		id++;
	}

	public static void main(String[] args) {
//		new Thread(new DataServer(), "Server").start();
		
		MinesweeperMain.startGame();
		
		int count = 1; // count visible frame
		while (count != 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count = 0;
			for (Frame frame : JFrame.getFrames()) {
				if (frame.isVisible()) { count++ ;}
			}
		}
		
		System.out.println("GAME OVER!");
		System.exit(0);
    }
}
