package pt.uminho.haslab.smhbase.sharmind;

import java.math.BigInteger;
import java.util.Collection;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

public class DiffBitConjTest extends BitConj {

	@Parameterized.Parameters
	public static Collection nbitsValues() {
		return ValuesGenerator.BitConjDiffValuesGenerator();
	}

	public DiffBitConjTest(int nbits, BigInteger p0, BigInteger p1,
			BigInteger p2) {
		super(nbits, p0, p1, p2);
	}

	@Override
	public boolean expectedResult() {
		return false;
	}

}
