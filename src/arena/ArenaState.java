package arena;

public enum ArenaState {
	WAITING("Waiting"), 
	PREGAME("Give player items"),
	INGAME("Playing"), 
	ENDGAME("Ending");

	public final String name;

	ArenaState(String name) {
		this.name = name;
	}
}