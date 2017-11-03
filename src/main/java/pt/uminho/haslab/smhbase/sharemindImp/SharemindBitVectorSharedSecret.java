package pt.uminho.haslab.smhbase.sharemindImp;

import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Secret;

import java.math.BigInteger;

public class SharemindBitVectorSharedSecret
		extends
			AbstractSharemindSharedSecret {

	public SharemindBitVectorSharedSecret(int nbits, BigInteger u1,
			BigInteger u2, BigInteger u3) {
		super(nbits, u1, u2, u3);
	}

	@Override
	Secret generateSecret(BigInteger value, Player player)
			throws InvalidSecretValue {
		return new SharemindBitVectorSecret(nbits, this.power, value, player);
	}

	@Override
	public BigInteger unshare() {
		return u1.xor(u2).xor(u3);
	}

}
