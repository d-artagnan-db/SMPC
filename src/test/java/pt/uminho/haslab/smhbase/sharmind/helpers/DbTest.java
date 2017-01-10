package pt.uminho.haslab.smhbase.sharmind.helpers;

import pt.uminho.haslab.smhbase.interfaces.Secret;

public abstract class DbTest extends Thread {

	protected final Secret secret;
	protected Secret protocolResult;

	public DbTest(Secret secret) {
		this.secret = secret;
	}

	public Secret getResult() {
		return this.protocolResult;
	}

}
