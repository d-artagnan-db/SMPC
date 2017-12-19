package pt.uminho.haslab.smpc.sharmind;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Dealer;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Players;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.DbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class SecureShiftRightTest extends ProtocolTest {

    private final int nshift;
    private final BigInteger value;

    public SecureShiftRightTest(int nbits, int nshift, BigInteger value) {
        super(nbits);
        this.nshift = nshift;
        this.value = value;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.shiftValueGenerator();
    }

    @Override
    public List<DbTest> prepareDatabases(Players players)
            throws InvalidNumberOfBits, InvalidSecretValue {
        BigInteger u = this.value;
        Dealer dealer = new SharemindDealer(this.nbits);
        SharemindSharedSecret secret = (SharemindSharedSecret) dealer.share(u);

        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        DbTest rdb0 = new Db((SharemindSecret) secret.getSecretU1(p0));
        DbTest rdb1 = new Db((SharemindSecret) secret.getSecretU2(p1));
        DbTest rdb2 = new Db((SharemindSecret) secret.getSecretU3(p2));

        List<DbTest> result = new ArrayList<DbTest>();

        result.add(rdb0);
        result.add(rdb1);
        result.add(rdb2);

        return result;

    }

    public BigInteger oracle() {
        BigInteger shiftedLeft = this.value.shiftRight(nshift);
        StringBuilder sbt = new StringBuilder();

		/*
         * Do not forget that sharemind protocol uses n+1 bits to store the
		 * values
		 */
        for (int i = 0; i < nbits; i++) {
            boolean testBit = shiftedLeft.testBit(i);
            int bit = 0;
            if (testBit) {
                bit = 1;
            }
            sbt.append(bit);
        }
        String result = sbt.reverse().toString();
        if (result.isEmpty()) {
            result = "0";
        }

        return new BigInteger(sbt.toString(), 2);
    }

    @Override
    public void condition(DbTest db1, DbTest db2, DbTest db3) {
        BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
        BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
        BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();
        SharedSecret secret = new SharemindSharedSecret(nbits + 1, u1, u2, u3);
        assertEquals(oracle(), secret.unshare()); // Templates.
    }

    private class Db extends DbTest {

        public Db(SharemindSecret secret) {
            super(secret);
        }

        @Override
        public void run() {
            super.protocolResult = ((SharemindSecret) super.secret)
                    .shiftR(nshift);
        }

    }

}
