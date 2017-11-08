package pt.uminho.haslab.smhbase.sharmind.helpers;

import java.math.BigInteger;

public class NBitsValue {
    private final int nbits;

    private final BigInteger value1;

    private final BigInteger value2;

    public NBitsValue(int nbits, BigInteger value1, BigInteger value2) {
        this.nbits = nbits;
        this.value1 = value1;
        this.value2 = value2;
    }

    public int getNBits() {
        return this.nbits;
    }

    public BigInteger getValue1() {
        return this.value1;
    }

    public BigInteger getValue2() {
        return this.value2;
    }
}
