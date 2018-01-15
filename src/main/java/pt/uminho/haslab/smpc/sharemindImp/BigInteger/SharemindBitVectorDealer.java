package pt.uminho.haslab.smpc.sharemindImp.BigInteger;

import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RangeChecker;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;

import java.math.BigInteger;

public class SharemindBitVectorDealer extends AbstractSharemindDealer {

    public SharemindBitVectorDealer(int nbits) throws InvalidNumberOfBits {
        super(nbits);
        power = BigInteger.valueOf(2).pow(nbits);

    }

    public SharedSecret share(BigInteger value) throws InvalidSecretValue {
        RangeChecker.check(this.nbits, value);
        BigInteger u1 = BigInteger.ZERO; // new BigInteger(this.nbits, RandomGenerator.generator);
        BigInteger u2 = BigInteger.ZERO;  //new BigInteger(this.nbits, RandomGenerator.generator);
        BigInteger u3 = value.xor(u1).xor(u2);

        return new SharemindBitVectorSharedSecret(nbits, u1, u2, u3);
    }

}
