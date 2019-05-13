package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.network.Network;
import pt.uminho.haslab.smpc.helpers.MemNetwork;
import pt.uminho.haslab.smpc.helpers.MemSpdzPlayer;

public class DealerExecutor extends FrescoExecutor{


    private final Network network;

    public DealerExecutor(int playerID, int nPlayers, int maxBatchSize, MemSpdzPlayer[] players) {
        super(playerID, nPlayers, maxBatchSize);
        this.network = new MemNetwork(playerID, players);
    }
    public Network getNetwork() {
        return network;
    }
}
