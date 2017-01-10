package pt.uminho.haslab.smhbase.interfaces;

import java.math.BigInteger;

public interface Player {

	public void sendValueToPlayer(int playerId, BigInteger value);

	public void storeValue(Integer playerDest, Integer playerSource,
			BigInteger value);

	public BigInteger getValue(Integer originPlayerId);

	public int getPlayerID();

}
