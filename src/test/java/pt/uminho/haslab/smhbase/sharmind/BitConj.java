package pt.uminho.haslab.smhbase.sharmind;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public abstract class BitConj extends ProtocolTest {

	private final BigInteger p0;
	private final BigInteger p1;
	private final BigInteger p2;

	public BitConj(int nbits, BigInteger p0, BigInteger p1, BigInteger p2) {
		super(nbits);
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public List<DbTest> prepareDatabases(Players players)
			throws InvalidSecretValue {

		Player pl0 = players.getPlayer(0);
		Player pl1 = players.getPlayer(1);
		Player pl2 = players.getPlayer(2);

		BigInteger modPower = BigInteger.valueOf(2).pow(this.nbits);

		SharemindBitVectorSecret sbvs1 = new SharemindBitVectorSecret(
				this.nbits, modPower, this.p0, pl0);
		SharemindBitVectorSecret sbvs2 = new SharemindBitVectorSecret(
				this.nbits, modPower, this.p1, pl1);
		SharemindBitVectorSecret sbvs3 = new SharemindBitVectorSecret(
				this.nbits, modPower, this.p2, pl2);

		DbTest rdb0 = new BitConj.BitConjDbTest(sbvs1);
		DbTest rdb1 = new BitConj.BitConjDbTest(sbvs2);
		DbTest rdb2 = new BitConj.BitConjDbTest(sbvs3);

		List<DbTest> result = new ArrayList<DbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);

		return result;

	}

	public abstract boolean expectedResult();

	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

		SharedSecret secret = new SharemindSharedSecret(1, u1, u2, u3);

		assertEquals(secret.unshare().equals(BigInteger.ONE), expectedResult());
	}

	private class BitConjDbTest extends DbTest {

		public BitConjDbTest(SharemindBitVectorSecret secret) {
			super(secret);
		}

		@Override
		public void run() {
			super.protocolResult = ((SharemindBitVectorSecret) super.secret)
					.bitConj();
		}

	}

}
