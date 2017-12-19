package pt.uminho.haslab.smpc.sharemindImp;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.interfaces.Secret;
import pt.uminho.haslab.smpc.interfaces.SharedSecret;

import java.math.BigInteger;

public abstract class AbstractSharemindSharedSecret implements SharedSecret {

    protected final BigInteger u1;
    protected final BigInteger u2;
    protected final BigInteger u3;
    protected final int nbits;
    protected final BigInteger power;

    /* Check if number of bits is greater than 0 */
    public AbstractSharemindSharedSecret(int nbits, BigInteger u1,
                                         BigInteger u2, BigInteger u3) {
        this.u1 = u1;
        this.u2 = u2;
        this.u3 = u3;
        this.nbits = nbits;
        this.power = BigInteger.valueOf(2).pow(nbits);

    }

    public BigInteger getU1() {
        return u1;
    }

    public BigInteger getU2() {
        return u2;
    }

    public BigInteger getU3() {
        return u3;
    }

    public int getNbits() {
        return this.nbits;
    }

    abstract Secret generateSecret(BigInteger value, Player players)
            throws InvalidSecretValue;

    public Secret getSecretU1(Player players) throws InvalidSecretValue {

        return this.generateSecret(u1, players);

    }

    public Secret getSecretU2(Player players) throws InvalidSecretValue {
        return this.generateSecret(u2, players);
    }

    public Secret getSecretU3(Player players) throws InvalidSecretValue {
        return this.generateSecret(u3, players);
    }

}
