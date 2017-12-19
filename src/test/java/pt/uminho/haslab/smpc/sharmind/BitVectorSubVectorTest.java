package pt.uminho.haslab.smpc.sharmind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class BitVectorSubVectorTest {

    private final int nbits;
    private final BigInteger value;
    private final int start;
    private final int end;

    public BitVectorSubVectorTest(int nBits, BigInteger value, int start,
                                  int end) {
        this.nbits = nBits;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.SubVectorGenerator();
    }

    /*
     * Resturns the correct result that subVector should return. Even thought
     * this algorithm is almost equal to the subvector, the subvector
     * implementation might change and it always has to return the same value as
     * this oracle.
     */
    private BigInteger oracle() {

        StringBuilder sb = new StringBuilder();

        for (int i = start; i < end; i++) {
            int bit = 0;
            if (value.testBit(i)) {
                bit = 1;
            }
            sb.append(bit);
        }

        BigInteger finalValue;
        /*
		 * BigInteger values are in reverse, from right to left. The loop above
		 * this line cant be made in reverse or it will count the sign bit. we
		 * assume always positive
		 */
        String bitString = sb.reverse().toString();

        if (bitString.isEmpty()) {
			/* when the end and start are 0 */
            finalValue = BigInteger.ZERO;
        } else {
            finalValue = new BigInteger(bitString, 2);
        }

        return finalValue;
    }

    @Test
    public void testSubVector() throws InvalidSecretValue {
        BigInteger mod = BigInteger.valueOf(2).pow(nbits);
        SharemindBitVectorSecret sbv = new SharemindBitVectorSecret(nbits, mod,
                value, null);
        SharemindBitVectorSecret res = (SharemindBitVectorSecret) sbv
                .subVector(start, end);

		/* The result value can never be greater than the original value */
        assertEquals(res.getValue().compareTo(value) == -1
                || res.getValue().compareTo(value) == 0, true);
        assertEquals(oracle(), res.getValue());
		/*
		 * The number of bits must be 1 if the interval is 0. It always requires
		 * 1 bit
		 */
        assertEquals(res.getNbits(), (end - start) == 0 ? 1 : end - start);
    }
}
