package pt.uminho.haslab.smhbase.sharmind;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

public class ShareConvTest extends ProtocolTest {

	private final BigInteger valueOne;
	private final BigInteger valueTwo;
	private final BigInteger valueThree;

	public ShareConvTest(int nbits, BigInteger valueOne, BigInteger valueTwo,
			BigInteger valueThree) {
		super(nbits);
		this.valueOne = valueOne;
		this.valueTwo = valueTwo;
		this.valueThree = valueThree;
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.ShareConvGenerator();
	}

	private class Db extends DbTest {

		public Db(SharemindSecret secret) {
			super(secret);
		}

		@Override
		public void run() {
			super.protocolResult = ((SharemindSecret) super.secret).shareConv();
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

		SharemindSharedSecret secret = new SharemindSharedSecret(nbits, u1, u2,
				u3);

		assertEquals(u1, BigInteger.ZERO);
		assertEquals(nbits, secret.getNbits());
		assertEquals(valueOne.xor(valueTwo).xor(valueThree), secret.unshare());

	}

}
