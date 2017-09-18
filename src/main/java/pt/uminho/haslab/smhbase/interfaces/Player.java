package pt.uminho.haslab.smhbase.interfaces;

import java.math.BigInteger;
import java.util.List;

public interface Player {

    public void sendValueToPlayer(int playerId, BigInteger value);

    public void storeValue(Integer playerDest, Integer playerSource,
                           BigInteger value);

    public void storeValues(Integer playerDest, Integer playerSource,
                            List<byte[]> values);

    public BigInteger getValue(Integer originPlayerId);

    public int getPlayerID();

    public void sendValueToPlayer(Integer playerID, List<byte[]> values);

    public List<byte[]> getValues(Integer rec);

}
