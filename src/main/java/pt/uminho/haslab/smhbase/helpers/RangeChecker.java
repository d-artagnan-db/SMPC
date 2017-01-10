package pt.uminho.haslab.smhbase.helpers;

import java.math.BigInteger;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;

public class RangeChecker {

	public static void check(int nbits, BigInteger value)
			throws InvalidSecretValue {
		BigInteger max = BigInteger.valueOf(2).pow(nbits)
				.subtract(BigInteger.ONE);
		BigInteger min = BigInteger.ZERO;
		if (value.compareTo(max) == 1) {
			String msg = "Argument value must be lesser than (2^(origNbits))-1";
			throw new InvalidSecretValue(msg);
		} else if (value.compareTo(min) == -1) {
			String msg = "Argument value must greaterThan 0";
			throw new InvalidSecretValue(msg);
		}
	}
}
