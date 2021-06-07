package game;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameBoard extends JPanel {

	private static final int UNIT_SIDE = 15; // pixel
	private static final int MINE_SIDE_NUM = 16;
	private static final int MINE_TOT_NUM = 40;
	private static final int BOARD_SIDE = UNIT_SIDE * MINE_SIDE_NUM;
	private static final int BOARD_HEIGHT = UNIT_SIDE * (MINE_SIDE_NUM + 2);
	private static final String MARK = "&";
	private static final String MARK_INNER = ";";
	private int MINE_NUM = MINE_TOT_NUM;
	private int TILE_REMAIN = MINE_SIDE_NUM * MINE_SIDE_NUM;
	
	private GameTile[][] tiles = new GameTile[MINE_SIDE_NUM][MINE_SIDE_NUM];
	private JPanel boardPanel;
	private JLabel mineRemainLabel;
	private boolean isOver = false;
	private boolean isWin = false;
	private MouseActionListener listener;
	
	class MouseActionListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int x = e.getPoint().y / UNIT_SIDE;
			int y = e.getPoint().x / UNIT_SIDE;
			if (SwingUtilities.isLeftMouseButton(e)) {
				leftMouseAction(x, y);
			} else if (SwingUtilities.isRightMouseButton(e)) {
				rightMouseAction(x, y);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
	
	public GameBoard() {
		initTiles();
		createGamePanels();
		addTilesToBoard();
		generateGameBoard();
	}
	
	private void createGamePanels() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setPreferredSize(new Dimension(BOARD_SIDE, BOARD_HEIGHT));
		
		boardPanel = new JPanel(new GridLayout(MINE_SIDE_NUM, MINE_SIDE_NUM));
		boardPanel.setPreferredSize(new Dimension(BOARD_SIDE, BOARD_SIDE));
		listener = new MouseActionListener();
		boardPanel.addMouseListener(listener);
		
		JPanel mineRemainPanel = new JPanel();
		mineRemainPanel.setPreferredSize(new Dimension(BOARD_SIDE, UNIT_SIDE * 2));
		mineRemainLabel = new JLabel(String.valueOf(MINE_TOT_NUM));
		mineRemainPanel.add(mineRemainLabel);
		
		add(boardPanel);
		add(mineRemainPanel);
	}
	
	private void initTiles() {
		for(int i = 0; i < MINE_SIDE_NUM; i++) {
			for(int j = 0; j < MINE_SIDE_NUM; j++) {
				tiles[i][j] = new GameTile(TileState.TILE, i, j);
			}
		}
	}
	
	private void addTilesToBoard() {
		for(int i = 0; i < MINE_SIDE_NUM; i++) {
			for(int j = 0; j < MINE_SIDE_NUM; j++) {
				boardPanel.add(tiles[i][j]);
			}
		}
	}
	
	private void generateGameBoard() {
		generateMines();
		setTilesState();
	}
	
	private void generateMines() {
		int i = 0;
		while (i < MINE_TOT_NUM) {
			int x = (int) (Math.random() * (MINE_SIDE_NUM - 1));
			int y = (int) (Math.random() * (MINE_SIDE_NUM - 1));
			if (!tiles[x][y].isMine()) {
				tiles[x][y].setMine(true);
				i++;
			}
		}
	}
	
	private void setTilesState() {
		for(int i = 0; i < MINE_SIDE_NUM; i++) {
			for(int j = 0; j < MINE_SIDE_NUM; j++) {
				if (tiles[i][j].isMine()) {
					tiles[i][j].setExpState(TileState.BOOM);
				} else {
					int minesNum = countMines(i, j);
					tiles[i][j].setExpState(TileState.getEnumByValue(minesNum));
				}
			}
		}
	}
	
	private int countMines(int i, int j) {
		int[][] dir = {{-1, -1}, {-1, 0}, {-1, 1}, 
					   {0, -1}, {0, 1}, 
					   {1, -1}, {1, 0}, {1, 1}};
		int minesNum = 0;
		for(int k = 0; k < 8; k++) {
			int x = i + dir[k][0];
			int y = j + dir[k][1];
			if (x >= 0 && x < MINE_SIDE_NUM &&
				y >= 0 && y < MINE_SIDE_NUM) {
				if (tiles[x][y].isMine()) {
					minesNum++;
				}
			}
		}
		return minesNum;
	}
	
	private void leftMouseAction(int x, int y) {
		if (tiles[x][y].getCurState() == TileState.TILE) {
			if (tiles[x][y].isMine()) {
				isOver = true;
				tiles[x][y].setCurState(tiles[x][y].getExpState());
				tiles[x][y].repaintImg();
			} else {
				clearBlanks(x, y);
			}
		}
	}
	
	private void rightMouseAction(int x, int y) {
		if (MINE_NUM > 0) {
			if (tiles[x][y].getCurState() == TileState.TILE) {
				MINE_NUM--;
				tiles[x][y].setCurState(TileState.FLAG);
				tiles[x][y].repaintImg();
				mineRemainLabel.setText(String.valueOf(MINE_NUM));
			} else if (tiles[x][y].getCurState() == TileState.FLAG) {
				MINE_NUM++;
				tiles[x][y].setCurState(TileState.TILE);
				tiles[x][y].repaintImg();
				mineRemainLabel.setText(String.valueOf(MINE_NUM));
			}
		}
	}
	
	private void clearBlanks(int i, int j) {
		Queue<GameTile> queue = new ArrayDeque<>();
		int[][] dir = {{-1, -1}, {-1, 0}, {-1, 1}, 
					   {0, -1}, {0, 1}, 
					   {1, -1}, {1, 0}, {1, 1}};
		queue.add(tiles[i][j]);
		
		boardPanel.setVisible(false); // no flash in panel
		while (!queue.isEmpty()) {
			GameTile tile = queue.poll();
			int x = tile.x;
			int y = tile.y;
			if (tiles[x][y].getCurState() == TileState.TILE) {
				tiles[x][y].setCurState(tiles[x][y].getExpState());
				tiles[x][y].repaintImg();
				TILE_REMAIN--;
				if (tiles[x][y].getExpState() == TileState.ZERO) {
					for(int k = 0; k < 8; k++) {
						int xx = x + dir[k][0];
						int yy = y + dir[k][1];
						if (xx >= 0 && xx < MINE_SIDE_NUM &&
							yy >= 0 && yy < MINE_SIDE_NUM &&
							tiles[xx][yy].getCurState() == TileState.TILE) {
								queue.add(tiles[xx][yy]);
						}
					}
				}
			}
		}
		boardPanel.setVisible(true);
		
		if (TILE_REMAIN == MINE_TOT_NUM) {
			isWin = true;
		}
	}

	public void setMessage(String str) { mineRemainLabel.setText(str); }
	
	public boolean isOver() { return isOver; }
	
	public boolean isWin() { return isWin; }
	
	public void closeBoard() {
		// remove listener
		boardPanel.removeMouseListener(listener);
		// show board result
		showBoardResult();
	}
	
	private void showBoardResult() {
		boardPanel.setVisible(false);
		for(int i = 0; i < MINE_SIDE_NUM; i++) {
			for(int j = 0; j < MINE_SIDE_NUM; j++) {
				if (tiles[i][j].isMine() && 
					tiles[i][j].getCurState() == TileState.TILE) {
						tiles[i][j].setCurState(tiles[i][j].getExpState());
						tiles[i][j].repaintImg();
				} else if (!tiles[i][j].isMine() && 
						   tiles[i][j].getCurState() == TileState.FLAG) {
					tiles[i][j].setCurState(TileState.WRONGFLAG);
					tiles[i][j].repaintImg();
				}
			}
		}
		boardPanel.setVisible(true);
	}

	public String serialize() {
		boolean isOver = isOver();
		boolean isWin = isWin();
		String tilesData = serializeTiles();
		
		return isOver + MARK
			   + isWin + MARK
			   + tilesData;
	}
	
	private String serializeTiles() {
		List<String> tileList = new ArrayList<>();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				tileList.add(tiles[i][j].serialize());
			}
		}
		return String.join(MARK_INNER, tileList);
	}
	
	private void deserializeTiles(String tilesData) {
		
	}
	
	public static GameBoard deseialize(String gameData) {
		return new GameBoard(gameData);
	}
	
	private GameBoard(String gameData) {
		String[] splits = gameData.split(MARK);
		
		this.isOver = Boolean.parseBoolean(splits[0]);
		this.isWin = Boolean.parseBoolean(splits[1]);
		
		String tilesData = splits[2];
		List<String> tileSplits = new ArrayList<>(Arrays.asList(tilesData.split(MARK_INNER)));
		Iterator<String> iterator = tileSplits.iterator();
		for (int i = 0; i < this.tiles.length; i++) {
			for (int j = 0; j < this.tiles[i].length; j++) {
				this.tiles[i][j] = GameTile.deserialize((String) iterator.next());
			}
		}
		
		createGamePanels();
		addTilesToBoard();
	}
}
