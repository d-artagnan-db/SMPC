package pt.uminho.haslab.smpc.sharmind;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Dealer;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.interfaces.Secret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.DbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DoubleValueProtocolTest extends ProtocolTest {

    protected BigInteger firstValue;
    protected BigInteger secondValue;

    public DoubleValueProtocolTest(int nbits, BigInteger value1,
                                   BigInteger value2) {
        super(nbits);
        this.firstValue = value1;
        this.secondValue = value2;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.TwoValuesGenerator();
    }

    public abstract Secret runProtocol(Secret firstSecret, Secret secondSecret);

    @Override
    public List<DbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        BigInteger u = this.firstValue;
        BigInteger v = this.secondValue;

        Dealer dealer = new SharemindDealer(this.nbits);

        SharemindSharedSecret secretOne = (SharemindSharedSecret) dealer
                .share(u);
        SharemindSharedSecret secretTwo = (SharemindSharedSecret) dealer
                .share(v);

        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        DbTest rdb0 = new Db((SharemindSecret) secretOne.getSecretU1(p0),
                (SharemindSecret) secretTwo.getSecretU1(p0));
        DbTest rdb1 = new Db((SharemindSecret) secretOne.getSecretU2(p1),
                (SharemindSecret) secretTwo.getSecretU2(p1));
        DbTest rdb2 = new Db((SharemindSecret) secretOne.getSecretU3(p2),
                (SharemindSecret) secretTwo.getSecretU3(p2));

        List<DbTest> result = new ArrayList<DbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;
    }

    protected class Db extends DbTest {

        private final Secret secondSecret;

        public Db(Secret secret, Secret second) {
            super(secret);
            this.secondSecret = second;

        }

        @Override
        public void run() {
            super.protocolResult = runProtocol(this.secret, this.secondSecret);
        }

    }
}
