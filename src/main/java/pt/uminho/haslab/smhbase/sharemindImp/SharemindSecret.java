package pt.uminho.haslab.smhbase.sharemindImp;

import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.helpers.RandomGenerator;
import pt.uminho.haslab.smhbase.helpers.RangeChecker;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Secret;

import java.math.BigInteger;

/**
 * When running this protocols trough sockets the order of the values when they
 * are sent and read as to be guaranteed somehow.
 */
public class SharemindSecret implements Secret {

    private final int nbits;

    private final BigInteger mod;

    private final BigInteger value;

    private final Player player;

    public SharemindSecret(int nbits, BigInteger mod, BigInteger value,
                           Player player) throws InvalidSecretValue {
        RangeChecker.check(nbits, value);

        this.nbits = nbits;
        this.mod = mod;
        this.value = value;
        this.player = player;
    }

    public BigInteger getMod() {
        return mod;
    }

    public int getNbits() {
        return nbits;
    }

    public SharemindSecret reshare() {

        BigInteger randomValue = new BigInteger(this.nbits,
                RandomGenerator.generator);

        // Calculates the target player based on this player id.
        int dest = (player.getPlayerID() + 1) % 3;

        player.sendValueToPlayer(dest, randomValue);

        int rec = BigInteger.valueOf(player.getPlayerID())
                .subtract(BigInteger.ONE).mod(BigInteger.valueOf(3)).intValue();

        BigInteger receivedValue = player.getValue(rec);

        BigInteger result = this.value.add(randomValue).subtract(receivedValue);

        try {
            return new SharemindSecret(nbits, mod, result.mod(mod), player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }

    }

    public SharemindSecret mult(SharemindSecret v) {
        SharemindSecret resharedU = this.reshare();
        SharemindSecret resharedV = v.reshare();

        // Calculates the target player based on this player id.
        int dest = (player.getPlayerID() + 1) % 3;

        BigInteger resUValue = resharedU.getValue();
        BigInteger resVValue = resharedV.getValue();

        player.sendValueToPlayer(dest, resUValue);
        player.sendValueToPlayer(dest, resVValue);

        // Calculates the target player based on this player id.
        int rec = BigInteger.valueOf(player.getPlayerID())
                .subtract(BigInteger.ONE).mod(BigInteger.valueOf(3)).intValue();

        BigInteger receivedU = player.getValue(rec);
        BigInteger receivedV = player.getValue(rec);

        BigInteger resultPart1 = resUValue.multiply(resVValue);
        BigInteger resultPart2 = resUValue.multiply(receivedV);
        BigInteger resultPart3 = receivedU.multiply(resVValue);

        BigInteger result = resultPart1.add(resultPart2.add(resultPart3));

        try {
            return new SharemindSecret(nbits, mod, result.mod(mod), player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    public BigInteger getValue() {
        return this.value;
    }

    /*
     * TODO: Compare mods and nbits to see if they match and throw exception if
     * not
     */
    public Secret equal(Secret v) {
        SharemindSecret received = (SharemindSecret) v;

        BigInteger p;

        if (this.player.getPlayerID() == 0) {
            BigInteger r1 = new BigInteger(this.nbits,
                    RandomGenerator.generator);
            BigInteger r2 = this.value.subtract(received.getValue())
                    .subtract(r1).mod(mod);

            this.player.sendValueToPlayer(1, r1);
            this.player.sendValueToPlayer(2, r2);

			/*
             * This protocol is sending a value and since it does not wait for
			 * any other value then it starts the next protocol which will
			 * return
			 */

            p = mod.subtract(BigInteger.ONE);

        } else {

            BigInteger r = this.player.getValue(0);

            BigInteger e = this.value.subtract(received.getValue()).add(r)
                    .mod(mod);

            if (this.player.getPlayerID() == 1) {
                p = e;
            } else {
                p = BigInteger.ZERO.subtract(e).mod(mod);
            }

        }

        SharemindBitVectorSecret sbvc;
        try {
            sbvc = new SharemindBitVectorSecret(this.nbits, mod, p, this.player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }

        return sbvc.bitConj();

    }

    public Secret reshareToTwo() {
        BigInteger u;
        if (player.getPlayerID() == 0) {
            BigInteger r1 = new BigInteger(nbits, RandomGenerator.generator);
            BigInteger r2 = value.subtract(r1).mod(mod);
            player.sendValueToPlayer(1, r1);
            player.sendValueToPlayer(2, r2);
            u = BigInteger.ZERO;
        } else {
            BigInteger r = player.getValue(0);
            u = this.value.add(r).mod(mod);
        }
        try {
            return new SharemindSecret(nbits, mod, u, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* TODO: Check pre condition, u1,u2 and u3 can either b1 one or zero */
    public Secret shareConv() {

        BigInteger v;

        if (player.getPlayerID() == 0) {
            BigInteger b = new BigInteger(1, RandomGenerator.generator);
            BigInteger m = b.xor(value);
            BigInteger m12 = new BigInteger(nbits, RandomGenerator.generator);
            BigInteger m13 = m.subtract(m12).mod(mod);
            BigInteger b12 = new BigInteger(1, RandomGenerator.generator);
            BigInteger b13 = b.xor(b12);

			/*
			 * the order of this values has to be guarenteed somehow by the
			 * player
			 */
            player.sendValueToPlayer(1, m12);
            player.sendValueToPlayer(2, m13);
            player.sendValueToPlayer(1, b12);
            player.sendValueToPlayer(2, b13);
            v = BigInteger.ZERO;

        } else {
            BigInteger m = player.getValue(0);
            BigInteger b = player.getValue(0);
            BigInteger s1 = b.xor(value);
            int dest = 2;

            if (player.getPlayerID() == 2) {
                dest = 1;
            }

            player.sendValueToPlayer(dest, s1);
            BigInteger s2 = player.getValue(dest);

            BigInteger s = s1.xor(s2);

            if (s.equals(BigInteger.ONE)) {

                if (player.getPlayerID() == 1) {
                    v = BigInteger.ONE.subtract(m).mod(mod);
                } else {
                    v = BigInteger.ZERO.subtract(m).mod(mod);
                }

            } else {
                v = m;

            }

        }

        try {
            return new SharemindSecret(nbits, mod, v, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Secret overflow() {

        BigInteger p = null;

        switch (player.getPlayerID()) {
            case 0:
                p = BigInteger.ZERO;
                break;
            case 1:
                p = value;
                break;
            case 2:
                p = BigInteger.ZERO.subtract(value).mod(mod);
                break;
        }

        SharemindBitVectorSecret s;
        try {
            s = (SharemindBitVectorSecret) new SharemindBitVectorSecret(nbits,
                    mod, p, player).msnzb();
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }

        BigInteger u;
        if (player.getPlayerID() == 2) {
            SharemindBitVectorDealer dealer;
            SharemindBitVectorSharedSecret share;

            try {
                dealer = new SharemindBitVectorDealer(nbits);
                share = (SharemindBitVectorSharedSecret) dealer.share(p);

            } catch (InvalidNumberOfBits ex) {
                throw new IllegalStateException(ex);

            } catch (InvalidSecretValue ex) {
                throw new IllegalStateException(ex);
            }
            player.sendValueToPlayer(0, share.getU1());
            player.sendValueToPlayer(1, share.getU2());
            u = share.getU3();
        } else {
            u = player.getValue(2);
        }

        return overflowLoop(s.getValue(), u).shareConv();

    }

    public SharemindSecret overflowLoop(BigInteger s, BigInteger u) {

        // Every player sets lambda to one making a global xored 1.
        BigInteger lambda = BigInteger.ONE;

        for (int i = 0; i < nbits; i++) {
            SharemindSecret sBit = bitSecret(s.testBit(i));
            SharemindSecret uBit = bitSecret(u.testBit(i));
            SharemindSecret bitRes = sBit.mult(uBit);

            lambda = lambda.xor(bitRes.getValue());
        }

        // this.value contains the original value of u3.
        if (player.getPlayerID() == 2 && this.value.equals(BigInteger.ZERO)) {
            lambda = BigInteger.ONE.xor(lambda);
        }
        try {
            /**
             * Even though it uses just one bit, it returns a SharemindSecret
             * with the original number of bits to be converted to z_2n by the
             * shareConv protocol that uses the instance nbits and mod to do the
             * conversion.
             */
            return new SharemindSecret(nbits, mod, lambda, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    private SharemindSecret bitSecret(boolean bit) {
        BigInteger bValue = BigInteger.ZERO;
        BigInteger bMod = BigInteger.valueOf(2).pow(1);

        if (bit) {
            bValue = BigInteger.ONE;
        }

        try {
            return new SharemindSecret(1, bMod, bValue, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    /*
     * When using the shiftL by any protocol it is required to know and pay
     * attention that an extra bit is used to store the values and the mod is
     * also in those nbits.
     */
    public SharemindSecret shiftL(int shiftLeftNBits) {
        BigInteger shiftedValue = this.value.multiply(
                BigInteger.valueOf(2).pow(shiftLeftNBits)).mod(mod);
        try {
            return new SharemindSecret(this.nbits, mod, shiftedValue, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* TODO: Throw exception if shiftN > nbits */
    public SharemindSecret shiftR(int shiftN) {
        SharemindSecret reshared = (SharemindSecret) this.reshareToTwo();

        SharemindSecret shifted = reshared.shiftL(this.nbits - shiftN);

        SharemindSecret deltaOne = (SharemindSecret) reshared.overflow();
        SharemindSecret deltaTwo = (SharemindSecret) shifted.overflow();

        BigInteger v = reshared.getValue().shiftRight(shiftN);

        BigInteger powerValue = BigInteger.valueOf(2).pow(nbits - shiftN);

        BigInteger result = v
                .subtract(powerValue.multiply(deltaOne.getValue()))
                .add(deltaTwo.getValue()).mod(mod);

        try {
            return new SharemindSecret(nbits, mod, result, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    /*
     * TODO: Compare mods and nbits to see if they match and throw exception if
     * not. Returns 0 if this value is greater or equal to v.
     */
    public Secret greaterOrEqualThan(Secret v) {
        try {
            BigInteger vValue = ((SharemindSecret) v).getValue();

            BigInteger diff = this.value.subtract(vValue).mod(mod);

            return new SharemindSecret(nbits, mod, diff, player).shiftR(
                    nbits - 1).reshare();
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

}
