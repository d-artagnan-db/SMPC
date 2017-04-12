package pt.uminho.haslab.smhbase.sharmind.helpers;

import java.util.List;
import pt.uminho.haslab.smhbase.interfaces.Secret;

public abstract class BatchDbTest extends Thread {

	protected final List<byte[]> secrets;
	protected List<byte[]> protocolResults;

	public BatchDbTest(List<byte[]> secrets) {
		this.secrets = secrets;
	}

	public List<byte[]> getResult() {
		return this.protocolResults;
	}
}
