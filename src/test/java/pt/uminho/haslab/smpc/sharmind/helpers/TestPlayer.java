package pt.uminho.haslab.smpc.sharmind.helpers;

import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;

import java.math.BigInteger;
import java.util.*;

public class TestPlayer implements Player {

    private final int playerID;
    private final Players players;

    private final List<Queue<Integer[]>> intValues;
    private final List<Queue<Long[]>> longValues;

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

        intValues = new ArrayList<Queue<Integer[]>>();
        intValues.add(new LinkedList<Integer[]>());
        intValues.add(new LinkedList<Integer[]>());
        intValues.add(new LinkedList<Integer[]>());

        longValues = new ArrayList<Queue<Long[]>>();
        longValues.add(new LinkedList<Long[]>());
        longValues.add(new LinkedList<Long[]>());
        longValues.add(new LinkedList<Long[]>());

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
    }

    public synchronized List<byte[]> getValues(Integer playerID) {
        while (this.batchValues.get(playerID).isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }

        }
        return this.batchValues.get(playerID).poll();
    }

    public void sendValueToPlayer(Integer playerID, int[] secrets) {
        players.sendValues(playerID, this.playerID, secrets);

    }

    public void sendValueToPlayer(Integer playerID, long[] secrets) {
        players.sendValues(playerID, this.playerID, secrets);

    }

    public synchronized int[] getIntValues(Integer playerID) {
        while (this.intValues.get(playerID).isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
        Integer[] theValues = intValues.get(playerID).poll();
        int[] resValues = new int[theValues.length];

        for(int i = 0; i < theValues.length; i++){
            resValues[i] = theValues[i];
        }
        return resValues;
    }

    public synchronized  long[] getLongValues(Integer playerID) {
        while (this.longValues.get(playerID).isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
        Long[] theValues = longValues.get(playerID).poll();
        long[] resValues = new long[theValues.length];

        for(int i = 0; i < theValues.length; i++){
            resValues[i] = theValues[i];
        }
        return resValues;

    }

    public void storeValues(Integer playerDest, Integer playerSource,
                            List<byte[]> values) {
        this.batchValues.get(playerSource).add(values);
    }

    public void storeValues(Integer playerDest, Integer playerSource, int[] values) {
        Integer[] vals = new Integer[values.length];
        for(int i = 0; i < values.length; i++){
            vals[i] = values[i];
        }
        this.intValues.get(playerSource).add(vals);
    }

    public void storeValues(Integer playerDest, Integer playerSource, long[] values) {
        Long[] vals = new Long[values.length];
        for(int i = 0; i < values.length; i++){
            vals[i] = values[i];
        }
        this.longValues.get(playerSource).add(vals);
    }

}
