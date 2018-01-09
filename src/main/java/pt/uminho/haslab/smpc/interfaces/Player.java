package pt.uminho.haslab.smpc.interfaces;

import java.math.BigInteger;
import java.util.List;

public interface Player {

    public void sendValueToPlayer(int playerId, BigInteger value);

    public void storeValue(Integer playerDest, Integer playerSource,
                           BigInteger value);

    public void storeValues(Integer playerDest, Integer playerSource,
                            List<byte[]> values);

    public void storeValues(Integer playerDest, Integer playerSource, int[] values);

    public BigInteger getValue(Integer originPlayerId);

    public int getPlayerID();

    public void sendValueToPlayer(Integer playerID, List<byte[]> values);

    public List<byte[]> getValues(Integer rec);

    public void sendValueToPlayer(Integer playerID, int[] secrets);

    public  int[] getIntValues(Integer rec);

}
