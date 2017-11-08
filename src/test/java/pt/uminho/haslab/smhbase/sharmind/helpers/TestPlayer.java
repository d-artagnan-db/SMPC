package pt.uminho.haslab.smhbase.sharmind.helpers;

import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;

import java.math.BigInteger;
import java.util.*;

public class TestPlayer implements Player {

	private final int playerID;
	private final Players players;
	private final Map<Integer, Queue<BigInteger>> values;
	private final Map<Integer, Queue<List<byte[]>>> batchValues;

	public TestPlayer(int playerID, Players players) {
		this.playerID = playerID;
		this.players = players;
		this.values = new HashMap<Integer, Queue<BigInteger>>();
		this.values.put(0, new LinkedList<BigInteger>());
		this.values.put(1, new LinkedList<BigInteger>());
		this.values.put(2, new LinkedList<BigInteger>());

		this.batchValues = new HashMap<Integer, Queue<List<byte[]>>>();
		this.batchValues.put(0, new LinkedList<List<byte[]>>());
		this.batchValues.put(1, new LinkedList<List<byte[]>>());
		this.batchValues.put(2, new LinkedList<List<byte[]>>());

	}

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

	public int getPlayerID() {
		return this.playerID;
	}

	public void sendValueToPlayer(int playerDest, BigInteger value) {
		// In the tests no id is required
		players.sendValue(playerDest, this.playerID, value);
	}

	public void storeValue(Integer playerDest, Integer playerSource,
			BigInteger value) {
		this.values.get(playerSource).add(value);
	}

	public void sendValueToPlayer(Integer playerID, List<byte[]> values) {
		players.sendValues(playerID, this.playerID, values);
		// this.batchValues.get(playerID).add(values);
	}

	public synchronized List<byte[]> getValues(Integer playerID) {
		// System.out.println(playerID);
		// System.out.println(this.batchValues.get(playerID));
		while (this.batchValues.get(playerID).isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				throw new IllegalStateException(ex);
			}

		}
		return this.batchValues.get(playerID).poll();
	}

	public void storeValues(Integer playerDest, Integer playerSource,
			List<byte[]> values) {
		this.batchValues.get(playerSource).add(values);
	}

}
