package pt.uminho.haslab.smhbase.sharemindImp;

import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.helpers.RandomGenerator;
import pt.uminho.haslab.smhbase.interfaces.SharedSecret;
import java.math.BigInteger;
import pt.uminho.haslab.smhbase.helpers.RangeChecker;

public class SharemindDealer extends AbstractSharemindDealer {

	public SharemindDealer(int origNbits) throws InvalidNumberOfBits {

		/* The modulus ring (2^(n+1)) */
		super(origNbits + 1);

		/* The modulus ring (2^(n+1)) */
		power = BigInteger.valueOf(2).pow(this.nbits);
	}

	@Override
	public SharedSecret share(BigInteger value) throws InvalidSecretValue {
		RangeChecker.check(this.nbits - 1, value);

		BigInteger u1 = new BigInteger(this.nbits, RandomGenerator.generator);
		BigInteger u2 = new BigInteger(this.nbits, RandomGenerator.generator);
		BigInteger u3 = value.subtract(u1).subtract(u2).mod(this.power);

		return new SharemindSharedSecret(this.nbits, u1, u2, u3);
	}

}
