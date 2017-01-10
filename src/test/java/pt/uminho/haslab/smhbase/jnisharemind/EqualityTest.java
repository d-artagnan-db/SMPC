package pt.uminho.haslab.smhbase.jnisharemind;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Dealer;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.interfaces.Secret;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindDealer;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.ProtocolTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

public class EqualityTest extends ProtocolTest {

	static final Log LOG = LogFactory.getLog(EqualityTest.class.getName());

	protected BigInteger firstValue;
	protected BigInteger secondValue;

	public EqualityTest(int nbits, BigInteger value1, BigInteger value2) {
		super(nbits);
		BasicConfigurator.configure();

		this.firstValue = value1;
		this.secondValue = value2;
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.TwoLongValuesGenerator();
	}

	protected class Db extends DbTest {

		private final Secret secondSecret;
		private final Player player;

		public Db(Secret secret, Secret second, Player p) {
			super(secret);
			secondSecret = second;
			player = p;
		}
		public byte[] longToBytes(long x) {
			ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
			buffer.putLong(x);
			return buffer.array();
		}

		public long bytesToLong(byte[] bytes) {
			ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
			buffer.put(bytes);
			buffer.flip();// need flip
			return buffer.getLong();
		}

		@Override
		public void run() {
			try {
				// please try with these two combinations

				byte[] fstValue = ((SharemindSecret) secret).getValue()
						.toByteArray();
				byte[] sndValue = ((SharemindSecret) secondSecret).getValue()
						.toByteArray();
				SharemindSecret originalSecret = ((SharemindSecret) secret);

				// byte[] fstValue = longToBytes(((SharemindSecret)
				// secret).getValue().longValue());
				// byte[] sndValue = longToBytes(((SharemindSecret)
				// secondSecret).getValue().longValue());

				BigInteger result = runProtocol(fstValue, sndValue, player);
				// LOG.debug("The result " + result);
				this.protocolResult = new SharemindSecret(
						originalSecret.getNbits(), originalSecret.getMod(),
						result, null);
			} catch (InvalidSecretValue ex) {
				LOG.debug(ex);
			}
		}

	}

	public BigInteger runProtocol(byte[] valueOne, byte[] valueTwo, Player p) {

		return new BigInteger(valueOne);
	}

	@Override
    public List<DbTest> prepareDatabases(Players players) throws InvalidNumberOfBits, InvalidSecretValue {
        BigInteger u = this.firstValue;
        BigInteger v = this.secondValue;

        Dealer dealer = new SharemindDealer(this.nbits);

        SharemindSharedSecret secretOne = (SharemindSharedSecret) dealer.share(u);
        SharemindSharedSecret secretTwo = (SharemindSharedSecret) dealer.share(v);

        Player p0 = players.getPlayer(0);
        Player p1 = players.getPlayer(1);
        Player p2 = players.getPlayer(2);

        DbTest rdb0 = new Db ((SharemindSecret)secretOne.getSecretU1(p0), (SharemindSecret)secretTwo.getSecretU1(p0), p0);
        DbTest rdb1 = new Db ((SharemindSecret)secretOne.getSecretU2(p1), (SharemindSecret)secretTwo.getSecretU2(p1), p1);
        DbTest rdb2 = new Db ((SharemindSecret)secretOne.getSecretU3(p2), (SharemindSecret)secretTwo.getSecretU3(p2), p2);

        List<DbTest> result = new ArrayList<>();

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

		SharedSecret secret = new SharemindSharedSecret(1, u1, u2, u3);
		boolean comparisonResult = this.firstValue.equals(this.secondValue);

		int expectedResult = comparisonResult ? 1 : 0;
		// assertEquals(secret.unshare().intValue(), expectedResult);
	}

}
