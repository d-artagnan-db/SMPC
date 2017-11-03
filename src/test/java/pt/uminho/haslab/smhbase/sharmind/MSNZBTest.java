package pt.uminho.haslab.smhbase.sharmind;

import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.*;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorDealer;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MSNZBTest extends SingleValueProtocolTest {

	public MSNZBTest(int nbits, BigInteger value) {
		super(nbits, value);
	}

	@Override
	public Secret runProtocol(Secret secret) {
		return ((SharemindBitVectorSecret) secret).msnzb();
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

		DbTest rdb0 = new Db(sbv1);
		DbTest rdb1 = new Db(sbv2);
		DbTest rdb2 = new Db(sbv3);

		List<DbTest> result = new ArrayList<DbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);

		return result;
	}

	private BigInteger oracle() {
		/*
		 * This function goes throught the bits from the end to the start and
		 * identifies the most significant bit. After that it generates a bit
		 * string with ones that has the same size as the original value.
		 */

		boolean foundMSB = false;
		StringBuilder sbt = new StringBuilder();

		for (int i = nbits - 1; i > -1; i--) {

			if (!foundMSB) {

				foundMSB = value.testBit(i);
				sbt.append(foundMSB ? 1 : 0);

			} else {
				sbt.append(0);
			}

		}
		if (sbt.toString().isEmpty()) {
			return BigInteger.ZERO;
		}
		return new BigInteger(sbt.toString(), 2);
	}

	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindBitVectorSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindBitVectorSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindBitVectorSecret) db3.getResult()).getValue();
		SharedSecret secret = new SharemindBitVectorSharedSecret(nbits, u1, u2,
				u3);
		BigInteger result = secret.unshare();

		assertEquals(oracle(), result);
	}

}
