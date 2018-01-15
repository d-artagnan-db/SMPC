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

public abstract class SingleBatchValueProtocolTest extends BatchProtocolTest {

    protected final List<BigInteger> values;

    public SingleBatchValueProtocolTest(int nbits, List<BigInteger> values) {
        super(nbits);
        //this.values = values;
        this.values = new ArrayList<BigInteger>();

        this.values.add(0, BigInteger.valueOf(604973553));
        this.nbits = 31;

        //System.out.println("Input secret is "+ this.values.get(0) + " <-> " + Integer.toBinaryString(this.values.get(0).intValue()));


    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        RandomGenerator.initBatch(6, 100);
        return ValuesGenerator.SingleBatchValueGenerator();
    }

    public abstract List<byte[]> runProtocol(List<byte[]> shares, Player player);

    @Override
    public List<BatchDbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        Dealer dealer = new SharemindDealer(this.nbits);
        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        List<byte[]> sharesp0 = new ArrayList<byte[]>();
        List<byte[]> sharesp1 = new ArrayList<byte[]>();
        List<byte[]> sharesp2 = new ArrayList<byte[]>();

        for (BigInteger value : values) {
            SharemindSharedSecret secret = (SharemindSharedSecret) dealer
                    .share(value);

            sharesp0.add(secret.getU1().toByteArray());
            sharesp1.add(secret.getU2().toByteArray());
            sharesp2.add(secret.getU3().toByteArray());
        }

        BatchDbTest rdb0 = new Db(sharesp0, p0);
        BatchDbTest rdb1 = new Db(sharesp1, p1);
        BatchDbTest rdb2 = new Db(sharesp2, p2);

        List<BatchDbTest> result = new ArrayList<BatchDbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;
    }

    protected class Db extends BatchDbTest {

        private final Player player;

        public Db(List<byte[]> shares, Player player) {
            super(shares);
            this.player = player;
        }

        @Override
        public void run() {
            super.protocolResults = runProtocol(super.secrets, player);
        }

    }
}
