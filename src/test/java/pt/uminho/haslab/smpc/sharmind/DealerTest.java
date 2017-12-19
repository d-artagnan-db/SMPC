package pt.uminho.haslab.smpc.sharmind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Dealer;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;
import pt.uminho.haslab.smpc.sharemindImp.SharemindBitVectorDealer;
import pt.uminho.haslab.smpc.sharemindImp.SharemindDealer;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class DealerTest {

    // Tests will run for numbers that use 80 bits at most.

    private final BigInteger value;
    private final int nbits;

    public DealerTest(int nbits, BigInteger value) {
        this.value = value;
        this.nbits = nbits;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.SingleValueGenerator();
    }

    @Test
    public void shareSecret() throws InvalidNumberOfBits, InvalidSecretValue {

        Dealer dealer = new SharemindDealer(nbits);

        SharedSecret secret = dealer.share(value);

        assertEquals(secret.unshare().equals(value), true);

    }

    @Test
    public void shareBitVectorSecret() throws InvalidNumberOfBits,
            InvalidSecretValue {

        Dealer dealer = new SharemindBitVectorDealer(nbits);

        SharedSecret secret = dealer.share(value);

        assertEquals(secret.unshare().equals(value), true);

    }
}
