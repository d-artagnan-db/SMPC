package pt.uminho.haslab.smpc.helpers;

import dk.alexandra.fresco.framework.network.CloseableNetwork;

import java.io.IOException;

public class MemNetwork implements CloseableNetwork {


    private final MemSpdzPlayer[] players;
    private final int playerID;

    public MemNetwork(int playerID, MemSpdzPlayer[] players){
        this.players = players;
        this.playerID = playerID;

    }


    public void send(int destPlayerID, byte[] data) {
        //System.out.println("Send from " +playerID + " to " + destPlayerID + " payload with size " + data.length);
        players[destPlayerID-1].storeValue(playerID, data);
    }

    public byte[] receive(int origPlayerID) {
        //System.out.println("Player " + playerID + " received from " + origPlayerID );

        return this.players[playerID-1].receive(origPlayerID);
    }

    public int getNoOfParties() {
        return players.length;
    }

    public void close() throws IOException {

    }
}
