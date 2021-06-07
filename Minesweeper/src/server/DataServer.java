package server;

import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DataServer extends JFrame implements Runnable {

	private JTextArea jta = new JTextArea();
	
	private static final String MARK = "#";
	private final String cmdSaveGame = "save";
	private final String cmdLoadGame = "load";
	private final String cmdSaveScore = "score";
	private final String cmdShowScore = "showscore";
	private final String cmdShowGame = "showgame";
	
	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private byte[] buf = new byte[3072];
	
	public DataServer() {
		createServerPanels();
	    try {
	    	jta.append("Server started at " + new Timestamp(new Date().getTime()) + '\n');
	    	socket = new DatagramSocket(8000);
			receivePacket = new DatagramPacket(buf, buf.length);
			sendPacket = new DatagramPacket(buf, buf.length);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createServerPanels() {
		setLayout(new BorderLayout());
	    add(new JScrollPane(jta), BorderLayout.CENTER);
	    setTitle("DataServer");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		new Thread(new DataServer(), "Server").start();
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				Arrays.fill(buf, (byte)0); // initialize buffer for each iteration
				// Receive data from the client in a packet
				socket.receive(receivePacket);
				jta.append("The client host name is " 
						+ receivePacket.getAddress().getHostName() 
						+ " and port number is " + receivePacket.getPort() + "\n");
				
				String[] revData = new String(buf).trim().split(MARK);
				String cmd = revData[0];
				String data = revData[1];
				
				switch(cmd) {
					case cmdLoadGame:
						loadGameData(data);
						break;
					case cmdSaveGame:
						saveGameData(data);
						break;
					case cmdShowGame:
						showGameData();
						break;
					case cmdSaveScore:
						saveScore(data);
						break;
					case cmdShowScore:
						showScore();
						break;
				}
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadGameData(String data) {
		jta.append("loadGameData! \n");
		int rowIdx = Integer.parseInt(data);
		String sendData = JDBCUtil.loadGame(rowIdx);
        try {
        	sendPacket.setAddress(receivePacket.getAddress());
            sendPacket.setPort(receivePacket.getPort());
            sendPacket.setData(sendData.getBytes());
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveGameData(String data) {
		jta.append("saveGameData! \n");
		JDBCUtil.saveGame(timeStamp() 
				+ receivePacket.getAddress() + MARK
				+ data);
	}
	
	private void showGameData() {
		jta.append("showGameData! \n");
		String sendData = JDBCUtil.showGame();
        try {
        	sendPacket.setAddress(receivePacket.getAddress());
            sendPacket.setPort(receivePacket.getPort());
            sendPacket.setData(sendData.getBytes());
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveScore(String data) {
		jta.append("saveSore! \n");
		JDBCUtil.saveScore(timeStamp() + data);
	}
	
	private void showScore() {
		jta.append("showSore! \n");
		String sendData = JDBCUtil.showScore();
        try {
        	sendPacket.setAddress(receivePacket.getAddress());
            sendPacket.setPort(receivePacket.getPort());
            sendPacket.setData(sendData.getBytes());
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String timeStamp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" + MARK);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		String timeStamp = df.format(now);
		return timeStamp;
	}

}
