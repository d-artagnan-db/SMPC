package pt.uminho.haslab.smpc.sharmind.batch;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.interfaces.Dealer;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.sharemindImp.BigInteger.SharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.BigInteger.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DoubleBatchValueProtocolTest extends BatchProtocolTest {

    protected List<BigInteger> firstValues;
    protected List<BigInteger> secondValues;

    public DoubleBatchValueProtocolTest(int nbits,
                                        List<BigInteger> firstValues, List<BigInteger> secondValues) {
        super(nbits);
        this.firstValues = firstValues;
        this.secondValues = secondValues;

    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {

        RandomGenerator.initBatch(32, 10000);
        return ValuesGenerator.TwoValuesBatchGenerator();
    }

    public abstract List<byte[]> runProtocol(List<byte[]> firstShares,
                                             List<byte[]> secondShares, Player player);

    @Override
    public List<BatchDbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        Dealer dealer = new SharemindDealer(this.nbits);

        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        List<byte[]> v1Sharesp0 = new ArrayList<byte[]>();
        List<byte[]> v1Sharesp1 = new ArrayList<byte[]>();
        List<byte[]> v1Sharesp2 = new ArrayList<byte[]>();

        List<byte[]> v2Sharesp0 = new ArrayList<byte[]>();
        List<byte[]> v2Sharesp1 = new ArrayList<byte[]>();
        List<byte[]> v2Sharesp2 = new ArrayList<byte[]>();

        List<BatchDbTest> results = new ArrayList<BatchDbTest>();

        for (int i = 0; i < firstValues.size(); i++) {
            BigInteger u = this.firstValues.get(i);
            BigInteger v = this.secondValues.get(i);

            SharemindSharedSecret secretOne = (SharemindSharedSecret) dealer
                    .share(u);
            SharemindSharedSecret secretTwo = (SharemindSharedSecret) dealer
                    .share(v);

            v1Sharesp0.add(secretOne.getU1().toByteArray());
            v1Sharesp1.add(secretOne.getU2().toByteArray());
            v1Sharesp2.add(secretOne.getU3().toByteArray());

            v2Sharesp0.add(secretTwo.getU1().toByteArray());
            v2Sharesp1.add(secretTwo.getU2().toByteArray());
            v2Sharesp2.add(secretTwo.getU3().toByteArray());
        }

        BatchDbTest rdb0 = new Db(v1Sharesp0, v2Sharesp0, p0);
        BatchDbTest rdb1 = new Db(v1Sharesp1, v2Sharesp1, p1);
        BatchDbTest rdb2 = new Db(v1Sharesp2, v2Sharesp2, p2);

        results.add(rdb0);
        results.add(rdb1);
        results.add(rdb2);

        return results;

    }

    protected class Db extends BatchDbTest {

        private final List<byte[]> secondShares;
        private final Player player;

        public Db(List<byte[]> firstShares, List<byte[]> secondShares,
                  Player player) {
            super(firstShares);
            this.secondShares = secondShares;
            this.player = player;

        }

        @Override
        public void run() {

            super.protocolResults = runProtocol(this.secrets,
                    this.secondShares, player);
        }

    }

}
