package pt.uminho.haslab.smhbase.sharmind;

import java.math.BigInteger;
import java.util.Collection;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.sharemindImp.SharemindBitVectorSecret;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

@RunWith(Parameterized.class)
public class MSNZBLoopTest {

	private final int nbits;
	private final BigInteger value;

	public MSNZBLoopTest(int nBits, BigInteger value) {

		this.nbits = nBits;
		this.value = value;
	}
	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.SingleValueGenerator();
	}

	/*
	 * Resturns the correct result that subVector should return. Even thought
	 * this algorithm is almost equal to the msnzbloop, the msnzbloop
	 * implementation might change and it always has to return the same value as
	 * this oracle.
	 */
	private BigInteger oracle() {
		StringBuilder sbt = new StringBuilder();

		for (int i = 0; i < nbits - 1; i++) {

			boolean iBit = value.testBit(i);
			boolean nextBit = value.testBit(i + 1);

			boolean result = iBit ^ nextBit;

			if (result) {
				sbt.append(1);
			} else {
				sbt.append(0);
			}
		}

		boolean finalBit = value.testBit(nbits - 1);
		if (finalBit) {
			sbt.append(1);
		} else {
			sbt.append(0);
		}

		return new BigInteger(sbt.reverse().toString(), 2);
	}

	@Test
	public void testMSNZBLoop() throws InvalidSecretValue {
		BigInteger mod = BigInteger.valueOf(2).pow(nbits);
		SharemindBitVectorSecret secret = new SharemindBitVectorSecret(nbits,
				mod, value, null);

		SharemindBitVectorSecret msnzb = (SharemindBitVectorSecret) secret
				.msnzLoop();
		BigInteger oracleResult = oracle();
		assertEquals(oracleResult, msnzb.getValue());
		assertEquals(nbits, msnzb.getNbits());
	}
}
