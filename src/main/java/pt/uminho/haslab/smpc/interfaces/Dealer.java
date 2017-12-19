package pt.uminho.haslab.smpc.interfaces;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;

import java.math.BigInteger;

public interface Dealer {

    public SharedSecret share(BigInteger value) throws InvalidSecretValue;
}
