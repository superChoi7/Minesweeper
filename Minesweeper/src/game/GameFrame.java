package game;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

public class GameFrame extends JFrame implements Runnable, Serializable {
	
	public static int id;
	public int score;
	
	private static final int UNIT_SIDE = 15; // pixel
	private static final int FRAME_WIDTH = UNIT_SIDE * 16;
	private static final int FRAME_HEIGHT = UNIT_SIDE * 23;
	private static final int TIME = 1000;
	private static final String MARK = "%";
	
	private GameTimer timer;
	private GameBoard game;
	private Thread timerThread;
	private DataClient client;
	JFrame loadFrame;
	
	public GameFrame() {
		createFrame();
		
		timer = new GameTimer(TIME);
		game = new GameBoard();
		timerThread = new Thread(timer, "timer" + id);
		client = new DataClient("game" + id);
		
		createPanels(getContentPane());
		createMenuBar();
		setVisible(true);

		id++;
	}
	
	private void createFrame() {
		setTitle("Minesweeper");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindowAction();
			}
		});
	}
	
	private void createPanels(Container mainPanel) {
		mainPanel.add(timer);
		mainPanel.add(game);
	}
	
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.setPreferredSize(new Dimension(FRAME_WIDTH, (int) (UNIT_SIDE * 1.5)));
		setJMenuBar(menuBar);
	}
	
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createNewItem());
		menu.add(createOpenItem());
		menu.add(createSaveItem());
		menu.add(createRankItem());
		menu.add(createExitItem());
		return menu;
	}
	
	private JMenuItem createNewItem() {
		JMenuItem item = new JMenuItem("New");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MinesweeperMain.startGame();
			}
		});
		
		return item;
	}
	
	private JMenuItem createOpenItem() {
		JMenuItem item = new JMenuItem("Open");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showGameAction();
			}
		});
		
		return item;
	}
	
	private JMenuItem createSaveItem() {
		JMenuItem item = new JMenuItem("Save");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveGameAction();
			}
		});
		
		return item;
	}
	
	private JMenuItem createRankItem() {
		JMenuItem item = new JMenuItem("Rank");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		item.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showRankAction();
			}
		});
		
		return item;
	}
	
	private JMenuItem createExitItem() {
		JMenuItem item = new JMenuItem("Exit");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		item.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeWindowAction();
			}
		});
		
		return item;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!game.isOver() && !game.isWin()) {
			timerThread.start();
		}
		
		while (timer.isRunning() 
				&& !game.isOver() 
				&& !game.isWin()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		gameOver();
	}
	
	public void gameOver() {
		
		timer.stop();
		
		// wait timer to stop
		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (timer.getScore() == 0) {
			game.setMessage("Game LOST!");
		} else {
			if (game.isOver()) {
				timer.setScore(0);
				game.setMessage("Game LOST!");
			} else if (game.isWin()) {
				game.setMessage("Game WIN!");
			}
		}
		
		// close game board and save scores
		if (timer.getScore() == 0 
			|| game.isOver() 
			|| game.isWin()) {
			game.closeBoard();
			timer.setMessage("Scores: " + timer.getScore());
			score = timer.getScore();
			client.sendScore(String.valueOf(score));
		}
		
	}
	
	private void saveGameAction() {
		String gameStateData = serialize();
		client.saveGameData(gameStateData);
	}
	
	private void showGameAction() {
		loadFrame = new JFrame();
		loadFrame.setTitle("Load Games");
		loadFrame.setSize(400, 200);
		loadFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		loadFrame.setResizable(false);
		loadFrame.setVisible(true);
		
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("Load Games");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 21));
		titlePanel.add(titleLabel);
		
		String[] gameRecords = client.showGameData().split("\n");
		JPanel loadPanel = new JPanel(new BorderLayout());
		
		// selection panel
		DefaultListModel<String> listModel = new DefaultListModel<>();
		for(int i = 0; i < gameRecords.length; i++) {
			String record = "Game " + String.valueOf(i + 1) + ": " 
							+ gameRecords[i];
			listModel.addElement(record);
		}
		JList<String> list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(list);
		
		// execute load game
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadGameAction(list.getSelectedIndex());
	        }
	    } );
		JPanel buttonPane = new JPanel();
		buttonPane.add(loadButton);
		
		loadPanel.add(listScrollPane, BorderLayout.CENTER);
		loadPanel.add(buttonPane, BorderLayout.PAGE_END);
		loadPanel.setPreferredSize(new Dimension(350, 110));
		loadPanel.setOpaque(true);
		
		loadFrame.add(titlePanel);
		loadFrame.add(loadPanel);
	}
	
	private void loadGameAction(int rowIdx) {
		String gameStateData = client.loadGameData(rowIdx);
		GameFrame game = GameFrame.deserialize(gameStateData);
		MinesweeperMain.startGame(game);
	}
	
	private void showRankAction() {
		JFrame rankFrame = new JFrame();
		rankFrame.setTitle("Rank");
		rankFrame.setSize(300, 200);
		rankFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
		rankFrame.setResizable(false);
		rankFrame.setVisible(true);
		
		String rankData = client.showScore();
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel("Rank");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 21));
		titlePanel.add(titleLabel);
		
		JTextArea jta = new JTextArea();
		jta.append(rankData.toUpperCase());
		jta.setEditable(false);
		
		rankFrame.add(titlePanel);
		rankFrame.add(jta);
	}
	
	private void closeWindowAction() {
		timer.stop();
		setVisible(false);
		dispose();
	}
	
	private String serialize() {
		int timeData = timer.getScore();
		String gameData = game.serialize();
		
		return timeData + MARK + gameData;
	}
	
	public static GameFrame deserialize(String gameStateData) {
		return new GameFrame(gameStateData);
	}
	
	private GameFrame(String gameStateData) {
		String[] splits = gameStateData.split(MARK);
		
		int timeData = Integer.parseInt(splits[0]);
		String gameData = splits[1];
		
		createFrame();
		
		this.timer = new GameTimer(timeData);
		this.game = GameBoard.deseialize(gameData);
		this.timerThread = new Thread(this.timer, "timer" + id);
		this.client = new DataClient("game" + id);
		
		createPanels(this.getContentPane());
		createMenuBar();
		setVisible(true);

		id++;
	}
	
}

