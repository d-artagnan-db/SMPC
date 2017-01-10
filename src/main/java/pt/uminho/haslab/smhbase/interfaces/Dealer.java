package pt.uminho.haslab.smhbase.interfaces;

import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import java.math.BigInteger;

public interface Dealer {

	public SharedSecret share(BigInteger value) throws InvalidSecretValue;
}
