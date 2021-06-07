package game;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DataClient extends JFrame {

	private JTextField jtf = new JTextField();
	private JTextArea jta = new JTextArea();
	
	private static final String MARK = "#";
	private final String cmdLoadGame = "load" + MARK;
	private final String cmdSaveGame = "save" + MARK;
	private final String cmdSaveScore = "score" + MARK;
	private final String cmdShowScore = "showscore" + MARK;
	private final String cmdShowGame = "showgame" + MARK;
	
	private InetAddress serverAddress; // server address
	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private byte[] buf = new byte[3072];
	
	public DataClient(String id) {
		createClientPanels();
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName("localhost");
			sendPacket = new DatagramPacket(buf, buf.length, serverAddress, 8000);
			receivePacket = new DatagramPacket(buf, buf.length);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void createClientPanels() {
		// Panel p to hold the label and text field
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JLabel("Enter"), BorderLayout.WEST);
		p.add(jtf, BorderLayout.CENTER);
		jtf.setHorizontalAlignment(JTextField.RIGHT);
				
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(new JScrollPane(jta), BorderLayout.CENTER);
		
		setTitle("DatagramClient");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setVisible(true);
	};
	
	public void sendScore(String scoreData) {
		try {
			Arrays.fill(buf, (byte)0);
			byte[] sendData = (cmdSaveScore + scoreData).trim().getBytes();
			sendPacket.setData(sendData);
			socket.send(sendPacket);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	
	public String showScore() {
		String revData = "";
		try {
			Arrays.fill(buf, (byte)0);
			sendPacket.setData((cmdShowScore + "0").trim().getBytes());
			socket.send(sendPacket);
			
			socket.receive(receivePacket);
			revData = new String(buf).trim();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return revData;
	}
	
	public void saveGameData(String gameData) {
		try {
			Arrays.fill(buf, (byte)0);
			byte[] sendData = (cmdSaveGame + gameData).trim().getBytes();
			sendPacket.setData(sendData);
//			System.out.println(sendData.length);
			socket.send(sendPacket);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}	
	}

	public String showGameData() {
		String revData = "";
		try {
			Arrays.fill(buf, (byte)0);
			sendPacket.setData((cmdShowGame + "0").trim().getBytes());
			socket.send(sendPacket);
			
			socket.receive(receivePacket);
			revData = new String(buf).trim();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return revData;
	}
	
	public String loadGameData(int rowIdx) {
		String revData = "";
		try {
			Arrays.fill(buf, (byte)0);
			byte[] sendData = (cmdLoadGame + rowIdx).trim().getBytes();
			sendPacket.setData(sendData);
			socket.send(sendPacket);
			
			socket.receive(receivePacket);
			revData = new String(buf).trim();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return revData;
	}
	
}
