package pt.uminho.haslab.smhbase.sharmind;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Dealer;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.interfaces.Secret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindDealer;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSharedSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SingleValueProtocolTest extends ProtocolTest {

	protected final BigInteger value;

	public SingleValueProtocolTest(int nbits, BigInteger value) {
		super(nbits);
		this.value = value;
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleValueGenerator();
	}

	public abstract Secret runProtocol(Secret secret);

	@Override
	public List<DbTest> prepareDatabases(Players players)
			throws InvalidNumberOfBits, InvalidSecretValue {
		BigInteger u = this.value;
		Dealer dealer = new SharemindDealer(this.nbits);
		SharemindSharedSecret secret = (SharemindSharedSecret) dealer.share(u);

		Player p0 = players.getPlayer(0);
		Player p1 = players.getPlayer(1);
		Player p2 = players.getPlayer(2);

		DbTest rdb0 = new Db((SharemindSecret) secret.getSecretU1(p0));
		DbTest rdb1 = new Db((SharemindSecret) secret.getSecretU2(p1));
		DbTest rdb2 = new Db((SharemindSecret) secret.getSecretU3(p2));

		List<DbTest> result = new ArrayList<DbTest>();

		result.add(rdb0);
		result.add(rdb1);
		result.add(rdb2);

		return result;
	}

	protected class Db extends DbTest {

		public Db(Secret secret) {
			super(secret);
		}

		@Override
		public void run() {
			super.protocolResult = runProtocol(super.secret);
		}

	}
}
