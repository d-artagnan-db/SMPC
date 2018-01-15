package pt.uminho.haslab.smpc.sharmind.helpers;

import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestPlayers implements Players {

    //private final Map<Integer, Player> players;
    private final Player[] players;

    public TestPlayers(int nplayers) {
        //this.players = new ConcurrentHashMap<Integer, Player>();
        players = new Player[nplayers];
    }

    public void addPlayer(Player p) {
        players[p.getPlayerID()] = p;
        //this.players.put(p.getPlayerID(), p);
    }

    public Player getPlayer(int playerID)
    {
        return players[playerID];
       // return this.players.get(playerID);
    }

    public void sendValue(int playerDest, int playerSource, BigInteger value) {
        /*
		 * The lock has to be on the player. Each player is waiting a
		 * notification on itself.
		 */
        synchronized (this.players[playerDest]) {
            this.players[playerDest].storeValue(playerDest, playerSource,
                    value);
            this.players[playerDest].notify();
        }
    }

    public void sendValues(int playerDest, int playerSource, List<byte[]> values) {

        synchronized (this.players[playerDest]) {

            this.players[playerDest].storeValues(playerDest, playerSource,
                    values);
            this.players[playerDest].notify();

        }

    }

    public void sendValues(int player, int playerSource, int[] values) {
        synchronized (this.players[player]){
            this.players[player].storeValues(player, playerSource, values);
            this.players[player].notify();
        }
    }

    public void sendValues(Integer player, int playerSource, long[] values) {
        synchronized (this.players[player]){
            this.players[player].storeValues(player, playerSource, values);
            this.players[player].notify();
        }
    }

}
