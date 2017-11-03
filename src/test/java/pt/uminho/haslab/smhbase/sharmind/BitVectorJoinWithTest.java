package pt.uminho.haslab.smhbase.sharmind;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class BitVectorJoinWithTest {

	private final int nbits;
	private final BigInteger value;

	public BitVectorJoinWithTest(int nbits, BigInteger value) {
		this.nbits = nbits;
		this.value = value;
	}

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleValueGenerator();
	}

	@Test
	public void testJoinWith() throws InvalidSecretValue {

		SharemindBitVectorSecret origValue = new SharemindBitVectorSecret(
				nbits, null, value, null);
		int half = nbits / 2;
		SharemindBitVectorSecret firstHalf = (SharemindBitVectorSecret) origValue
				.subVector(0, half);
		SharemindBitVectorSecret secondHalf = (SharemindBitVectorSecret) origValue
				.subVector(half, nbits);

		SharemindBitVectorSecret composed = firstHalf.joinWith(secondHalf);

		if (nbits <= 1) {
			// if nbits == 0 or 1 then each subvector will have eacha t least 1
			// bit.
			assertEquals(2, composed.getNbits());
			if (value.equals(BigInteger.ONE)) {
				/*
				 * if the original value is 1 than the first half will be 0 and
				 * the second half will be 1, when joined it will be 10 wich is
				 * 0 and not 1.
				 */
				assertEquals(composed.getValue().intValue(), 2);
			}

		} else {
			assertEquals(nbits, composed.getNbits());
			assertEquals(composed.getValue(), this.value);
		}
	}
}
