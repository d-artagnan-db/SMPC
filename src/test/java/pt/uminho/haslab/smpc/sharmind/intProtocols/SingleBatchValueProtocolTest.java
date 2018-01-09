package pt.uminho.haslab.smpc.sharmind.intProtocols;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharmind.batch.BatchProtocolTest;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SingleBatchValueProtocolTest extends BatchProtocolTest {

    protected final int[] values;


    public SingleBatchValueProtocolTest(int[] values){
        super(31); //The number of bits should not be used in these tests
        this.values = values;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.SingleIntBatchValueGenerator(1,1000);
    }


    public abstract int[] runProtocol(int[] shares, Player player);

    public List<BatchDbTest> prepareDatabases(Players players) throws InvalidNumberOfBits, InvalidSecretValue {
        IntSharemindDealer dealer = new IntSharemindDealer();
        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        int[] shares0 = new int[values.length];
        int[] shares1 = new int[values.length];
        int[] shares2 = new int[values.length];

        for(int i = 0;  i < values.length; i++){
                int[] secrets = dealer.share(values[i]);
                shares0[i] = secrets[0];
                shares1[i] = secrets[1];
                shares2[i] = secrets[2];
        }

        BatchDbTest rdb0 = new Db(shares0, p0);
        BatchDbTest rdb1 = new Db(shares1, p1);
        BatchDbTest rdb2 = new Db(shares2, p2);


        List<BatchDbTest> result = new ArrayList<BatchDbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;
    }


    protected class Db extends BatchDbTest {

        private final Player player;
        private final int[] shares;
        private int[] protocolResults;

        public Db(int[] shares, Player player) {
            super();
            this.player = player;
            this.shares = shares;
        }

        int[] getProtocolResults(){
            return this.protocolResults;
        }

        @Override
        public void run() {
            protocolResults = runProtocol(shares, player);
        }

    }
}
