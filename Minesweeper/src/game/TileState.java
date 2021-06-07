package game;

public enum TileState {
	ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), 
	SEVEN(7), EIGHT(8), BOOM(9), TILE(10), FLAG(11), WRONGFLAG(12);
	
    public final int value;

    private TileState(int value) {
        this.value = value;
    }
    
    public static TileState getEnumByValue(int val){
        for(TileState e : TileState.values()) {
            if(e.value == val) 
            	return e;
        }
        return null;
    }
}
