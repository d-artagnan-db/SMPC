package pt.uminho.haslab.smhbase.sharmind.helpers;

import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.interfaces.Player;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TestPlayer implements Player {

	private final int playerID;
	private final Players players;
	private final Map<Integer, Queue<BigInteger>> values;

	public TestPlayer(int playerID, Players players)
    {
        this.playerID = playerID;
        this.players = players;
        this.values = new HashMap<>();
        this.values.put(0, new LinkedList<BigInteger>());
        this.values.put(1, new LinkedList<BigInteger>());
        this.values.put(2, new LinkedList<BigInteger>());
        
    }
	@Override
	public synchronized BigInteger getValue(Integer playerID) {

		while (this.values.get(playerID).isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				throw new IllegalStateException(ex);
			}
		}

		BigInteger result = values.get(playerID).poll();

		return result;

	}

	@Override
	public int getPlayerID() {
		return this.playerID;
	}

	@Override
	public void sendValueToPlayer(int playerDest, BigInteger value) {
		// In the tests no id is required
		players.sendValue(playerDest, this.playerID, value);
	}

	@Override
	public void storeValue(Integer playerDest, Integer playerSource,
			BigInteger value) {
		this.values.get(playerSource).add(value);
	}

}
