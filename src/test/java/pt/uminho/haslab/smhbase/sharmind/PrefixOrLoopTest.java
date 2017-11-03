package pt.uminho.haslab.smhbase.sharmind;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.*;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorDealer;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class PrefixOrLoopTest extends SingleValueProtocolTest {

	public PrefixOrLoopTest(int nbits, BigInteger value) {
		super(nbits, value);
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleValueGenerator();
	}

	@Override
	public Secret runProtocol(Secret secret) {
		return ((SharemindBitVectorSecret) secret).prefixOrLoop();
	}

	@Override
	public List<DbTest> prepareDatabases(Players players)
			throws InvalidNumberOfBits, InvalidSecretValue {
		BigInteger u = this.value;
		Dealer dealer = new SharemindBitVectorDealer(nbits);
		SharemindBitVectorSharedSecret secret = (SharemindBitVectorSharedSecret) dealer
				.share(u);

		Player p0 = players.getPlayer(0);
		Player p1 = players.getPlayer(1);
		Player p2 = players.getPlayer(2);

		SharemindBitVectorSecret sbv1 = (SharemindBitVectorSecret) secret
				.getSecretU1(p0);
		SharemindBitVectorSecret sbv2 = (SharemindBitVectorSecret) secret
				.getSecretU2(p1);
		SharemindBitVectorSecret sbv3 = (SharemindBitVectorSecret) secret
				.getSecretU3(p2);

		/* DB is a class of SingleValueProtocolTest */
		DbTest rdb0 = new Db(sbv1);
		DbTest rdb1 = new Db(sbv2);
		DbTest rdb2 = new Db(sbv3);

		List<DbTest> result = new ArrayList<DbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);
		return result;
	}

	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindBitVectorSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindBitVectorSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindBitVectorSecret) db3.getResult()).getValue();
		SharedSecret secret = new SharemindBitVectorSharedSecret(nbits, u1, u2,
				u3);

		BigInteger result = secret.unshare();
		int half = nbits / 2;
		/* bitHalfValue */
		BigInteger bitHalfV = BigInteger.ZERO;
		BigInteger bitHalfR = BigInteger.ZERO;

		if (value.testBit(half)) {
			bitHalfV = BigInteger.ONE;
		}

		if (result.testBit(half)) {
			bitHalfR = BigInteger.ONE;
		}
		assertEquals(bitHalfV, bitHalfR);

		for (int i = 0; i < half; i++) {
			BigInteger bitI = BigInteger.ZERO;
			BigInteger bitR = BigInteger.ZERO;

			if (value.testBit(i)) {
				bitI = BigInteger.ONE;
			}

			if (result.testBit(i)) {
				bitR = BigInteger.ONE;
			}

			assertEquals(bitI.or(bitHalfV), bitR);

		}

	}
}
