package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameTile extends JPanel {
	
	public static Map<Integer, String> mp = new HashMap<Integer, String>() {{
		put(0, "./minesweepertiles/0.png");
		put(1, "./minesweepertiles/1.png");
		put(2, "./minesweepertiles/2.png");
		put(3, "./minesweepertiles/3.png");
		put(4, "./minesweepertiles/4.png");
		put(5, "./minesweepertiles/5.png");
		put(6, "./minesweepertiles/6.png");
		put(7, "./minesweepertiles/7.png");
		put(8, "./minesweepertiles/8.png");
		put(9, "./minesweepertiles/9.png");		
		put(10, "./minesweepertiles/10.png");		
		put(11, "./minesweepertiles/11.png");		
		put(12, "./minesweepertiles/12.png");		
	}};
	private static final String MARK = ",";
	
	private Image img;
	private TileState curState;
	private TileState expState;
	private boolean isMine = false;
	
	public int x;
	public int y;
	
	public GameTile(TileState state, int x, int y) {
		setCurState(state);
		setExpState(state);
		img = new ImageIcon(mp.get(state.value)).getImage();
		this.x = x; 
		this.y = y;
		setPreferredSize(new Dimension(15, 15));
        setLayout(null);
	}

	@Override
	public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
	
	public void repaintImg() {
		img = new ImageIcon(mp.get(curState.value)).getImage();
		repaint();
	}


	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}


	public TileState getCurState() {
		return curState;
	}

	public void setCurState(TileState curState) {
		this.curState = curState;
	}

	public TileState getExpState() {
		return expState;
	}

	public void setExpState(TileState expState) {
		this.expState = expState;
	}
	
	public String serialize() {
		return getCurState().value + MARK 
			   + getExpState().value + MARK
			   + x + MARK + y;
	}

	public static GameTile deserialize(String data) {
		return new GameTile(data);
	}

	private GameTile(String data) {
		String[] splits = data.split(",");
		this.setCurState(TileState.getEnumByValue(Integer.valueOf(splits[0])));
		this.setExpState(TileState.getEnumByValue(Integer.valueOf(splits[1])));
		this.x = Integer.valueOf(splits[2]);
		this.y = Integer.valueOf(splits[3]);
		this.img = new ImageIcon(mp.get(getCurState().value)).getImage();
		this.isMine = getExpState() == TileState.BOOM;
//		repaintImg();
		setPreferredSize(new Dimension(15, 15));
        setLayout(null);
	}
	
}
