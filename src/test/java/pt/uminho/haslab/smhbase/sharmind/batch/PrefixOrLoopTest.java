package pt.uminho.haslab.smhbase.sharmind.batch;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Dealer;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorDealer;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecretFunctions;
import pt.uminho.haslab.smhbase.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class PrefixOrLoopTest extends SingleBatchValueProtocolTest {

	public PrefixOrLoopTest(int nbits, List<BigInteger> values) {
		super(nbits, values);
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleBatchValueGenerator();
	}

	@Override
	public List<byte[]> runProtocol(List<byte[]> shares, Player player) {
		BigInteger bitMod = BigInteger.valueOf(2).pow(nbits);

		SharemindSecretFunctions ssf = new SharemindSecretFunctions(nbits,
				bitMod);
		return ssf.prefixOrLoop(shares, player);
	}

	@Override
	public List<BatchDbTest> prepareDatabases(Players players)
			throws InvalidNumberOfBits, InvalidSecretValue {

		List<byte[]> sbv1s = new ArrayList<byte[]>();
		List<byte[]> sbv2s = new ArrayList<byte[]>();
		List<byte[]> sbv3s = new ArrayList<byte[]>();
		Player p0 = players.getPlayer(0);
		Player p1 = players.getPlayer(1);
		Player p2 = players.getPlayer(2);

		for (BigInteger val : this.values) {
			BigInteger u = val;
			Dealer dealer = new SharemindBitVectorDealer(nbits);
			SharemindBitVectorSharedSecret secret = (SharemindBitVectorSharedSecret) dealer
					.share(u);
			BigInteger sbv1 = secret.getU1();
			BigInteger sbv2 = secret.getU2();
			BigInteger sbv3 = secret.getU3();

			sbv1s.add(sbv1.toByteArray());
			sbv2s.add(sbv2.toByteArray());
			sbv3s.add(sbv3.toByteArray());
		}

		/* DB is a class of SingleValueProtocolTest */
		BatchDbTest rdb0 = new Db(sbv1s, p0);
		BatchDbTest rdb1 = new Db(sbv2s, p1);
		BatchDbTest rdb2 = new Db(sbv3s, p2);

		List<BatchDbTest> result = new ArrayList<BatchDbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);
		return result;
	}

	@Override
	public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {
		List<byte[]> u1s = db1.getResult();
		List<byte[]> u2s = db2.getResult();
		List<byte[]> u3s = db3.getResult();

		for (int i = 0; i < u1s.size(); i++) {
			BigInteger u1 = new BigInteger(u1s.get(i));
			BigInteger u2 = new BigInteger(u2s.get(i));
			BigInteger u3 = new BigInteger(u3s.get(i));
			BigInteger value = values.get(i);

			SharedSecret secret = new SharemindBitVectorSharedSecret(nbits, u1,
					u2, u3);
			BigInteger result = secret.unshare();
			int half = nbits / 2;

			BigInteger bitHalfV = BigInteger.ZERO;
			BigInteger bitHalfR = BigInteger.ZERO;
			if (value.testBit(half)) {
				bitHalfV = BigInteger.ONE;
			}

			if (result.testBit(half)) {
				bitHalfR = BigInteger.ONE;
			}
			assertEquals(bitHalfV, bitHalfR);

			for (int j = 0; j < half; j++) {
				BigInteger bitI = BigInteger.ZERO;
				BigInteger bitR = BigInteger.ZERO;

				if (value.testBit(j)) {
					bitI = BigInteger.ONE;
				}

				if (result.testBit(j)) {
					bitR = BigInteger.ONE;
				}

				assertEquals(bitI.or(bitHalfV), bitR);
			}
		}
	}
}
