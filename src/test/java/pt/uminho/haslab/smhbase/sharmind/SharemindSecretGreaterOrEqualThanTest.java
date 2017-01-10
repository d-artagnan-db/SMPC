package pt.uminho.haslab.smhbase.sharmind;

import java.math.BigInteger;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import static junit.framework.TestCase.assertEquals;
import pt.uminho.haslab.smhbase.interfaces.Secret;

public class SharemindSecretGreaterOrEqualThanTest
		extends
			DoubleValueProtocolTest {

	public SharemindSecretGreaterOrEqualThanTest(int nbits, BigInteger value1,
			BigInteger value2) {
		super(nbits, value1, value2);

	}

	@Override
	public Secret runProtocol(Secret firstSecret, Secret secondSecret) {
		return ((SharemindSecret) firstSecret).greaterOrEqualThan(secondSecret);

	}

	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

		SharedSecret secret = new SharemindSharedSecret(nbits + 1, u1, u2, u3);

		int comparisonResult = this.firstValue.compareTo(this.secondValue);

		int expectedResult = comparisonResult == 0 || comparisonResult == 1
				? 0
				: 1;
		assertEquals(secret.unshare().intValue(), expectedResult);
	}

}
