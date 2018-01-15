package pt.uminho.haslab.smpc.interfaces;

import java.math.BigInteger;
import java.util.List;

public interface Players {

    public void sendValue(int player, int valueID, BigInteger value);

    public Player getPlayer(int player);

    public void addPlayer(Player player);

    public void sendValues(int player, int playerSource, List<byte[]> values);
    public void sendValues(int player, int playerSource, int[]  values);
    void sendValues(Integer playerID, int playerSource, long[] secrets);
}
