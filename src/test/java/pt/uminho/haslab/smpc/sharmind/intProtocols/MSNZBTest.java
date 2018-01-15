package pt.uminho.haslab.smpc.sharmind.intProtocols;

import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MSNZBTest extends SingleBatchValueProtocolTest {

    public MSNZBTest(int[] values) {
        super(values);
    }

    public List<BatchDbTest> prepareDatabases(Players players) throws InvalidNumberOfBits, InvalidSecretValue {
        IntSharemindDealer dealer = new IntSharemindDealer();
        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        int[] shares0 = new int[values.length];
        int[] shares1 = new int[values.length];
        int[] shares2 = new int[values.length];
        for(int i = 0;  i < values.length; i++){
            int[] secrets = dealer.shareXor(values[i]);
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

    private BigInteger oracle(BigInteger value) {
        /*
		 * This function goes throught the bits from the end to the start and
		 * identifies the most significant bit. After that it generates a bit
		 * string with ones that has the same size as the original value.
		 */

        boolean foundMSB = false;
        StringBuilder sbt = new StringBuilder();

        for (int i = nbits - 1; i > -1; i--) {

            if (!foundMSB) {

                foundMSB = value.testBit(i);
                sbt.append(foundMSB ? 1 : 0);

            } else {
                sbt.append(0);
            }

        }
        if (sbt.toString().isEmpty()) {
            return BigInteger.ZERO;
        }
        return new BigInteger(sbt.toString(), 2);
    }

    public int[] runProtocol(int[] shares, Player player) {
        IntSharemindSecretFunctions issf = new IntSharemindSecretFunctions();
        return issf.msnzb(shares, player);
    }

    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {
        int[] db1Results = ((Db) db1).getProtocolResults();
        int[] db2Results = ((Db) db2).getProtocolResults();
        int[] db3Results = ((Db) db3).getProtocolResults();


        assertEquals(db1Results.length, db2Results.length);
        assertEquals(db2Results.length, db3Results.length);

        IntSharemindDealer dealer = new IntSharemindDealer();

        for (int i = 0; i < db1Results.length; i++) {
            int[] vals = new int[3];
            vals[0] = db1Results[i];
            vals[1] = db2Results[i];
            vals[2] = db3Results[i];
            int res  = dealer.unshareXor(vals);
            System.out.println("expected " + oracle(BigInteger.valueOf(this.values[i])) + " <-> " + res + "  <->"+   Integer.toBinaryString(res));

            assertEquals(oracle(BigInteger.valueOf(this.values[i])), BigInteger.valueOf(res));
        }

    }
}
