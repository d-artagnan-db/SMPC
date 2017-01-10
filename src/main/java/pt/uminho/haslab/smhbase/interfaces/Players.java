package pt.uminho.haslab.smhbase.interfaces;

import java.math.BigInteger;

public interface Players {

	public void sendValue(int player, int valueID, BigInteger value);

	public Player getPlayer(int player);

	public void addPlayer(Player player);

}
