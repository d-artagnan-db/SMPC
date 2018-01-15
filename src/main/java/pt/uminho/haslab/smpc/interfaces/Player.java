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

    public void storeValues(Integer playerDest, Integer playerSource, long[] values);


    public BigInteger getValue(Integer originPlayerId);

    public int getPlayerID();

    public void sendValueToPlayer(Integer playerID, List<byte[]> values);
    public void sendValueToPlayer(Integer playerID, int[] secrets);
    public void sendValueToPlayer(Integer playerID, long[] secrets);

    public List<byte[]> getValues(Integer rec);
    public int[] getIntValues(Integer rec);
    public long[] getLongValues(Integer rec);





}
