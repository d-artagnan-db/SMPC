package pt.uminho.haslab.smpc.sharemindImp;

import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.helpers.RangeChecker;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;

import java.math.BigInteger;

public class SharemindDealer extends AbstractSharemindDealer {

    public SharemindDealer(int origNbits) throws InvalidNumberOfBits {

		/* The modulus ring (2^(n+1)) */
        super(origNbits + 1);

		/* The modulus ring (2^(n+1)) */
        power = BigInteger.valueOf(2).pow(this.nbits);
    }

    public SharedSecret share(BigInteger value) throws InvalidSecretValue {
        RangeChecker.check(this.nbits - 1, value);

        BigInteger u1 = new BigInteger(this.nbits, RandomGenerator.generator);
        BigInteger u2 = new BigInteger(this.nbits, RandomGenerator.generator);
        BigInteger u3 = value.subtract(u1).subtract(u2).mod(this.power);

        return new SharemindSharedSecret(this.nbits, u1, u2, u3);
    }

}
