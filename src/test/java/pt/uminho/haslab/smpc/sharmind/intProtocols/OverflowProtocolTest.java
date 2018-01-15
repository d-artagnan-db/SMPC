package pt.uminho.haslab.smpc.sharmind.intProtocols;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharmind.batch.BatchProtocolTest;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class OverflowProtocolTest extends BatchProtocolTest {


    @Parameterized.Parameters
    public static Collection nbitsValues() {
        //RandomGenerator.initIntBatch(10000);
        return ValuesGenerator.TwoValuesIntBatchGenerator(100,100);
    }


    private final int[] valuesOne;
    private final int[] valuesTwo;
    private final int[]  valuesThree;

    public OverflowProtocolTest(int[] u2s, int[] u3s) {
        super(31);

        valuesOne = new int[u2s.length];
        valuesTwo = u2s;
        valuesThree = u3s;
        for(int i = 0; i < u2s.length; i++){
            valuesOne[i] = 0;
        }

    }


    @Override
    public List<BatchDbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);


        BatchDbTest rdb0 = new Db(valuesOne, p0);
        BatchDbTest rdb1 = new Db(valuesTwo, p1);
        BatchDbTest rdb2 = new Db(valuesThree, p2);

        List<BatchDbTest> result = new ArrayList<BatchDbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;
    }

    private class Db extends BatchDbTest {

        private final Player player;

        private final int[] shares;
        private int[] protocolResults;

        public Db(int[] firstShares, Player player) {
            super(null);
            this.player = player;
            this.shares = firstShares;
        }
        int[] getProtocolResults(){
            return this.protocolResults;
        }

        @Override
        public void run() {
            try {
                IntSharemindSecretFunctions ssf = new IntSharemindSecretFunctions();
                protocolResults = ssf.overflow(shares, player);
            } catch (InvalidSecretValue ex) {
                throw new IllegalStateException(ex);
            }
        }

    }



    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {
        int[] res1 = ((Db) db1).getProtocolResults();
        int[] res2 = ((Db) db2).getProtocolResults();
        int[] res3 = ((Db) db3).getProtocolResults();

        for(int i = 0; i < res1.length; i++){
            int[] shares = new int[3];
            shares[0] = res1[i];
            shares[1] = res2[i];
            shares[2] = res3[i];

            BigInteger valueTwo = BigInteger.valueOf(valuesTwo[i]);
            BigInteger valueThree = BigInteger.valueOf(valuesThree[i]);

            IntSharemindDealer dealer = new IntSharemindDealer();

            BigInteger mod = BigInteger.valueOf(2).pow(30);
            int compared = valueTwo.compareTo(BigInteger.ZERO.subtract(
                    valueThree).mod(mod));

            int res = dealer.unshareBit(shares);

            if (!valueThree.equals(BigInteger.ZERO)
                    && (compared == 1 || compared == 0)) {

                assertEquals(1, res);
            } else {
                assertEquals(0, res);

            }
        }

    }
}
