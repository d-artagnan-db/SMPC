package pt.uminho.haslab.smhbase.sharemindImp;

import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Player;

import java.math.BigInteger;

public class SharemindSharedSecret extends AbstractSharemindSharedSecret {

	public SharemindSharedSecret(int nbits, BigInteger u1, BigInteger u2,
			BigInteger u3) {
		super(nbits, u1, u2, u3);
	}

	@Override
	SharemindSecret generateSecret(BigInteger value, Player player) {
		try {
			return new SharemindSecret(this.nbits, this.power, value, player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}
	}

	public BigInteger unshare() {
		return u1.add(u2).add(u3).mod(this.power);
	}

}
