package pt.uminho.haslab.smhbase.sharmind;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;
import static junit.framework.TestCase.assertEquals;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;

public class OverflowProtocolTest extends ProtocolTest {

	private final BigInteger valueOne;
	private final BigInteger valueTwo;
	private final BigInteger valueThree;

	public OverflowProtocolTest(int nbits, BigInteger u2, BigInteger u3) {
		super(nbits);
		this.valueOne = BigInteger.ZERO;
		this.valueTwo = u2;
		this.valueThree = u3;

	}
	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.TwoValuesGenerator();
	}

	private class Db extends DbTest {

		public Db(SharemindSecret secret) {
			super(secret);
		}

		@Override
		public void run() {
			super.protocolResult = ((SharemindSecret) super.secret).overflow();
		}

	}

	@Override
	public List<DbTest> prepareDatabases(Players players)
			throws InvalidSecretValue {
		Player p0 = players.getPlayer(0);
		Player p1 = players.getPlayer(1);
		Player p2 = players.getPlayer(2);

		BigInteger mod = BigInteger.valueOf(2).pow(nbits);
		SharemindSecret u1 = new SharemindSecret(nbits, mod, valueOne, p0);
		SharemindSecret u2 = new SharemindSecret(nbits, mod, valueTwo, p1);
		SharemindSecret u3 = new SharemindSecret(nbits, mod, valueThree, p2);

		Db rdb0 = new Db(u1);
		Db rdb1 = new Db(u2);
		Db rdb2 = new Db(u3);

		List<DbTest> result = new ArrayList<DbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);

		return result;
	}
	@Override
	public void condition(DbTest db1, DbTest db2, DbTest db3) {
		BigInteger u1 = ((SharemindSecret) db1.getResult()).getValue();
		BigInteger u2 = ((SharemindSecret) db2.getResult()).getValue();
		BigInteger u3 = ((SharemindSecret) db3.getResult()).getValue();

		SharemindSharedSecret secret = new SharemindSharedSecret(1, u1, u2, u3);

		BigInteger mod = BigInteger.valueOf(2).pow(nbits);
		int compared = valueTwo.compareTo(BigInteger.ZERO.subtract(valueThree)
				.mod(mod));

		if (!valueThree.equals(BigInteger.ZERO)
				&& (compared == 1 || compared == 0)) {
			assertEquals(BigInteger.ONE, secret.unshare());
		} else {
			assertEquals(BigInteger.ZERO, secret.unshare());

		}

	}

}
