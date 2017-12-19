package pt.uminho.haslab.smpc.sharmind;

import pt.uminho.haslab.smpc.interfaces.Secret;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.DbTest;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

public class SharemindSecretEqualityTest extends DoubleValueProtocolTest {

    public SharemindSecretEqualityTest(int nbits, BigInteger value1,
                                       BigInteger value2) {
        super(nbits, value1, value2);

    }

    @Override
    public Secret runProtocol(Secret firstSecret, Secret secondSecret) {

        return ((SharemindSecret) firstSecret).equal(secondSecret);

    }

    @Override
    public void condition(DbTest db1, DbTest db2, DbTest db3) {
        BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
        BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
        BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

        SharedSecret secret = new SharemindSharedSecret(1, u1, u2, u3);
        boolean comparisonResult = this.firstValue.equals(this.secondValue);

        int expectedResult = comparisonResult ? 1 : 0;

        assertEquals(secret.unshare().intValue(), expectedResult);

    }

}
