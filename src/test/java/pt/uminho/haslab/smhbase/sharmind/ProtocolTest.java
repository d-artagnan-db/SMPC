package pt.uminho.haslab.smhbase.sharmind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Players;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.DbTest;
import pt.uminho.haslab.smhbase.sharmind.helpers.TestPlayer;
import pt.uminho.haslab.smhbase.sharmind.helpers.TestPlayers;

import java.util.List;

@RunWith(Parameterized.class)
public abstract class ProtocolTest {

	protected final int nbits;

	public ProtocolTest(int nbits) {
		this.nbits = nbits;
	}

	public Players setupPlayers() {

		TestPlayers players = new TestPlayers(3);

		TestPlayer p0 = new TestPlayer(0, players);
		TestPlayer p1 = new TestPlayer(1, players);
		TestPlayer p2 = new TestPlayer(2, players);

		players.addPlayer(p0);
		players.addPlayer(p1);
		players.addPlayer(p2);

		return players;
	}

	/*
	 * Has to return a list with three DbTest.
	 */
	public abstract List<DbTest> prepareDatabases(Players players)
			throws InvalidNumberOfBits, InvalidSecretValue;

	/*
	 * Must contain the assert statement.
	 */
	public abstract void condition(DbTest db1, DbTest db2, DbTest db3);

	@Test
	public void protocol() throws InterruptedException, InvalidNumberOfBits,
			InvalidSecretValue {
		Players players = setupPlayers();
		List<DbTest> workers = prepareDatabases(players);

		DbTest rdb0 = workers.get(0);
		DbTest rdb1 = workers.get(1);
		DbTest rdb2 = workers.get(2);

		rdb0.start();
		rdb1.start();
		rdb2.start();

		rdb0.join();
		rdb1.join();
		rdb2.join();

		condition(rdb0, rdb1, rdb2);

	}

	protected abstract class ProtocoleDbTest extends DbTest {

		public ProtocoleDbTest(SharemindSecret secret) {
			super(secret);
		}
	}
}
