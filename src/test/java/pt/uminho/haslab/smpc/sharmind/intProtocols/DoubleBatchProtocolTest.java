package pt.uminho.haslab.smpc.sharmind.intProtocols;

import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharmind.batch.BatchProtocolTest;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;

import java.util.ArrayList;
import java.util.List;

public abstract class DoubleBatchProtocolTest extends BatchProtocolTest {

    protected final int[] firstValues;
    protected final int[] secondValues;

    public DoubleBatchProtocolTest(int nbits, int[] firstValues, int[] secondValues) {
        super(nbits);
        this.firstValues = firstValues;
        this.secondValues = secondValues;
    }

    public abstract int[] runProtocol(int[] firstShares,
                                             int[] seconShares, Player player);


    public List<BatchDbTest> prepareDatabases(Players players) throws InvalidNumberOfBits, InvalidSecretValue {
        IntSharemindDealer dealer = new IntSharemindDealer();

        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        int[] v1Shares0 = new int[firstValues.length];
        int[] v1Shares1 = new int[firstValues.length];
        int[] v1Shares2 = new int[firstValues.length];

        int[] v2Shares0 = new int[firstValues.length];
        int[] v2Shares1 = new int[firstValues.length];
        int[] v2Shares2 = new int[firstValues.length];


        for (int i = 0; i < firstValues.length; i++) {
            int[] firstSecrets = null;
            int[] secondSecrets = null;

            if(super.nbits == 1){
                firstSecrets = dealer.shareBit(firstValues[i]);
                secondSecrets = dealer.shareBit(secondValues[i]);
            }else{
                firstSecrets = dealer.share(firstValues[i]);
                secondSecrets = dealer.share(secondValues[i]);
            }

            v1Shares0[i] = firstSecrets[0];
            v1Shares1[i] = firstSecrets[1];
            v1Shares2[i] = firstSecrets[2];

            v2Shares0[i] = secondSecrets[0];
            v2Shares1[i] = secondSecrets[1];
            v2Shares2[i] = secondSecrets[2];

        }

        List<BatchDbTest> results = new ArrayList<BatchDbTest>();
        BatchDbTest rdb0 = new Db(v1Shares0, v2Shares0, p0);
        BatchDbTest rdb1 = new Db(v1Shares1, v2Shares1, p1);
        BatchDbTest rdb2 = new Db(v1Shares2, v2Shares2, p2);

        results.add(rdb0);
        results.add(rdb1);
        results.add(rdb2);

        return results;
    }

    protected class Db extends BatchDbTest {

        private final int[] firstShares;
        private final int[] secondShares;
        private int[] protocolResults;
        private final Player player;


        public Db(int[] firstShares, int[] secondShares,
                  Player player) {
            super();
            this.firstShares = firstShares;
            this.secondShares = secondShares;
            this.player = player;

        }

        public int[] getProtocolResults(){
            return this.protocolResults;
        }

        @Override
        public void run() {

            protocolResults = runProtocol(this.firstShares,
                    this.secondShares, player);
        }

    }


}
