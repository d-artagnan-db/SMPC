package pt.uminho.haslab.smhbase.sharmind.helpers;

import pt.uminho.haslab.smhbase.interfaces.Players;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pt.uminho.haslab.smhbase.interfaces.Player;

public class TestPlayers implements Players {

	private final Map<Integer, Player> players;

	public TestPlayers(int nplayers) {
        this.players = new ConcurrentHashMap<>();
	}
	@Override
	public void addPlayer(Player p) {
		this.players.put(p.getPlayerID(), p);
	}

	@Override
	public Player getPlayer(int playerID) {
		return this.players.get(playerID);
	}

	@Override
	public void sendValue(int playerDest, int playerSource, BigInteger value) {
		/*
		 * The lock has to be on the player. Each player is waiting a
		 * notification on itself.
		 */
		synchronized (this.players.get(playerDest)) {
			this.players.get(playerDest).storeValue(playerDest, playerSource,
					value);
			this.players.get(playerDest).notify();
		}
	}

}
