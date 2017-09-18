package pt.uminho.haslab.smhbase.sharmind;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smhbase.sharmind.helpers.ValuesGenerator;

import java.math.BigInteger;
import java.util.Collection;

@RunWith(Parameterized.class)
public class EqualBitConjTest extends BitConj {

    public EqualBitConjTest(int nbits, BigInteger p0, BigInteger p1,
                            BigInteger p2) {
        super(nbits, p0, p1, p2);
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.BitConjEqualValuesGenerator();
    }

    @Override
    public boolean expectedResult() {
        return true;
    }

}
