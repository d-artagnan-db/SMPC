package pt.uminho.haslab.smpc.sharmind.batch;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.BigInteger.SharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharemindImp.BigInteger.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ShareConvTest extends BatchProtocolTest {

    private final List<BigInteger> valuesOne;
    private final List<BigInteger> valuesTwo;
    private final List<BigInteger> valuesThree;

    public ShareConvTest(int nbits, List<BigInteger> valuesOnes,
                         List<BigInteger> valuesTwo, List<BigInteger> valuesThree) {
        super(nbits);
        this.valuesOne = valuesOnes;
        this.valuesTwo = valuesTwo;
        this.valuesThree = valuesThree;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.shareBatchConvGenerator();
    }

    @Override
    public List<BatchDbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        List<byte[]> u1s = new ArrayList<byte[]>();
        List<byte[]> u2s = new ArrayList<byte[]>();
        List<byte[]> u3s = new ArrayList<byte[]>();

        for (int i = 0; i < valuesOne.size(); i++) {
            u1s.add(valuesOne.get(i).toByteArray());
            u2s.add(valuesTwo.get(i).toByteArray());
            u3s.add(valuesThree.get(i).toByteArray());
        }
        Db rdb0 = new Db(u1s, p0);
        Db rdb1 = new Db(u2s, p1);
        Db rdb2 = new Db(u3s, p2);

        List<BatchDbTest> result = new ArrayList<BatchDbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;
    }

    @Override
    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {

        List<byte[]> db1Results = db1.getResult();
        List<byte[]> db2Results = db2.getResult();
        List<byte[]> db3Results = db3.getResult();

        assertEquals(db1Results.size(), db2Results.size());
        assertEquals(db2Results.size(), db3Results.size());
        for (int i = 0; i < db1Results.size(); i++) {

            BigInteger u1 = new BigInteger(db1Results.get(i));
            BigInteger u2 = new BigInteger(db2Results.get(i));
            BigInteger u3 = new BigInteger(db3Results.get(i));
            SharemindSharedSecret secret = new SharemindSharedSecret(nbits, u1,
                    u2, u3);
            assertEquals(u1, BigInteger.ZERO);
            assertEquals(nbits, secret.getNbits());
            BigInteger valueOne = valuesOne.get(i);
            BigInteger valueTwo = valuesTwo.get(i);
            BigInteger valueThree = valuesThree.get(i);
            assertEquals(valueOne.xor(valueTwo).xor(valueThree),
                    secret.unshare());

        }

    }

    private class Db extends BatchDbTest {

        private final Player player;

        public Db(List<byte[]> secrets, Player player) {
            super(secrets);
            this.player = player;
        }

        @Override
        public void run() {
            SharemindSecretFunctions ssf = new SharemindSecretFunctions(nbits);
            super.protocolResults = ssf.shareConv(super.secrets, player);
        }

    }

}
