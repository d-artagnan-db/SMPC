package pt.uminho.haslab.smhbase.sharmind.batch;

import java.math.BigInteger;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecretFunctions;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.BatchDbTest;

public class GreaterOrEqualThanTest extends DoubleBatchValueProtocolTest {

	public GreaterOrEqualThanTest(int nbits, List<BigInteger> firstValues,
			List<BigInteger> secondValues) {
		super(nbits, firstValues, secondValues);
	}

	@Override
	public List<byte[]> runProtocol(List<byte[]> firstShares,
			List<byte[]> secondShares, Player player) {
		try {
			SharemindSecretFunctions ssf = new SharemindSecretFunctions(nbits);
			return ssf.greaterOrEqualThan(firstShares, secondShares, player);
		} catch (InvalidNumberOfBits ex) {
			throw new IllegalStateException(ex);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}
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
			SharedSecret secret = new SharemindSharedSecret(nbits + 1, u1, u2,
					u3);
			BigInteger firstValue = this.firstValues.get(i);
			BigInteger secondValue = this.secondValues.get(i);
			int comparisonResult = firstValue.compareTo(secondValue);
			int expectedResult = comparisonResult == 0 || comparisonResult == 1
					? 0
					: 1;
			assertEquals(secret.unshare().intValue(), expectedResult);

		}
	}

}
