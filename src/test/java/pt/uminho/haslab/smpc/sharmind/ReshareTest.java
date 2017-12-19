package pt.uminho.haslab.smpc.sharmind;

import pt.uminho.haslab.smpc.interfaces.Secret;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.DbTest;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

public class ReshareTest extends SingleValueProtocolTest {

    public ReshareTest(int nbits, BigInteger value) {
        super(nbits, value);
    }

    @Override
    public SharemindSecret runProtocol(Secret secret) {
        return ((SharemindSecret) secret).reshare();
    }

    @Override
    public void condition(DbTest db1, DbTest db2, DbTest db3) {
        BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
        BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
        BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

        SharedSecret secret = new SharemindSharedSecret(nbits + 1, u1, u2, u3);
        assertEquals(secret.unshare(), this.value);
    }

}
