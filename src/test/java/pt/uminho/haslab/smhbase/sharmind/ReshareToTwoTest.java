package pt.uminho.haslab.smhbase.sharmind;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.interfaces.Secret;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

public class ReshareToTwoTest extends SingleValueProtocolTest {

	public ReshareToTwoTest(int nbits, BigInteger value) {
		super(nbits, value);
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleValueGenerator();
	}

	@Override
	public Secret runProtocol(Secret secret) {
		return ((SharemindSecret) secret).reshareToTwo();
	}

	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

		SharedSecret secret = new SharemindSharedSecret(nbits + 1, u1, u2, u3);
		assertEquals(u1, BigInteger.ZERO);
		assertEquals(secret.unshare(), this.value);
	}
}
