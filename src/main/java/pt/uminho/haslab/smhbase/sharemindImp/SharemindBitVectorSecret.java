package pt.uminho.haslab.smhbase.sharemindImp;

import java.math.BigInteger;

import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.helpers.RangeChecker;
import pt.uminho.haslab.smhbase.interfaces.Player;
import pt.uminho.haslab.smhbase.interfaces.Secret;

/*BigInteger values are in reverse, from right to left.*/
public class SharemindBitVectorSecret implements BitVectorSecret {

	private final int nbits;

	private final BigInteger mod;

	/*
	 * All the BigInteger Values must be positive The BigInteger bit
	 * representation is as the following example: nbits : 4 value : 14 bit It
	 * uses nbts+1 to represent the number where the last bit is the sign repr:
	 * 0 1 1 1 0 index : 4 3 2 1 0 Position 4 in this case is 0 because it is a
	 * positive value.
	 */
	private final BigInteger value;

	private final Player player;

	public SharemindBitVectorSecret(int nbits, BigInteger mod,
			BigInteger value, Player player) throws InvalidSecretValue {

		RangeChecker.check(nbits, value);

		this.nbits = nbits;
		this.mod = mod;
		this.value = value;
		this.player = player;
	}

	public int getNbits() {
		return this.nbits;
	}

	@Override
	public Secret bitConj() {
		BigInteger bitMod = BigInteger.valueOf(2);

		/*
		 * Every player creates a Secret with the value 1 that makes a global
		 * SharedSecret with value 1. PyLab has an error in this function.
		 */
		SharemindSecret u;
		try {
			u = new SharemindSecret(1, bitMod, BigInteger.ONE, this.player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}

		/*
		 * Bit Conjunction can be made from left to right or right to left and
		 * the result is the same. In this case we do it right to left.
		 */
		for (int i = 0; i < this.nbits; i++) {

			int bitVal = 0;

			if (value.testBit(i)) {
				bitVal = 1;
			}

			SharemindSecret v;
			try {
				v = new SharemindSecret(1, bitMod, BigInteger.valueOf(bitVal),
						this.player);
			} catch (InvalidSecretValue ex) {
				throw new IllegalStateException(ex);
			}

			u = u.mult(v);
		}
		return u;
	}

	public BigInteger getValue() {
		return this.value;
	}

	@Override
	public Secret equal(Secret v) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/*
	 * This protocol turns a binary array 001010 to 001111. It turns all the
	 * bits after the first one into ones also.
	 * 
	 * It uses the or operator. In this case the bit array is divided in three
	 * arrays by the Sharemind share protocol. Each array is supposed to be on
	 * one machine. And they need to communicate to run the secure 'or'
	 * operation between the bits
	 */
	@Override
	public BitVectorSecret prefixOr() {
		if (this.nbits == 1) {
			return this;
		} else {
			int half = this.getHalf();

			SharemindBitVectorSecret firstHalf = (SharemindBitVectorSecret) this
					.subVector(0, half).prefixOr();
			SharemindBitVectorSecret sendHalf = (SharemindBitVectorSecret) this
					.subVector(half, nbits).prefixOr();

			SharemindBitVectorSecret p = firstHalf.joinWith(sendHalf);

			return p.prefixOrLoop();
		}
	}

	public SharemindBitVectorSecret prefixOrLoop() {

		BigInteger bitHalf = BigInteger.ZERO;
		BigInteger bitMod = BigInteger.valueOf(2);
		BigInteger original = this.value;

		if (value.testBit(getHalf())) {
			bitHalf = BigInteger.ONE;
		}

		SharemindSecret secretH;
		try {
			secretH = new SharemindSecret(1, bitMod, bitHalf, this.player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}

		for (int i = 0; i < getHalf(); i++) {

			BigInteger bitI = BigInteger.ZERO;

			if (value.testBit(i)) {
				bitI = BigInteger.ONE;
			}

			SharemindSecret secretI;
			try {
				secretI = new SharemindSecret(1, bitMod, bitI, this.player);
			} catch (InvalidSecretValue ex) {
				throw new IllegalStateException(ex);
			}

			BigInteger multRes = secretI.mult(secretH).getValue();
			BigInteger finalRes = bitI.xor(bitHalf).xor(multRes);

			if (!finalRes.equals(bitI)) {
				original = original.flipBit(i);
			}

		}

		try {
			return new SharemindBitVectorSecret(nbits, this.mod, original,
					player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}
	}

	public SharemindBitVectorSecret joinWith(SharemindBitVectorSecret secondHalf) {

		// first half value(fhValue)
		BigInteger fhValue = this.getValue();
		BigInteger shValue = secondHalf.getValue();

		StringBuilder sbt = new StringBuilder();

		for (int i = 0; i < this.getNbits(); i++) {
			int bit = 0;
			if (fhValue.testBit(i)) {
				bit = 1;
			}
			sbt.append(bit);
		}
		for (int i = 0; i < secondHalf.getNbits(); i++) {
			int bit = 0;

			if (shValue.testBit(i)) {
				bit = 1;
			}
			sbt.append(bit);
		}
		String bitString = sbt.reverse().toString();
		BigInteger composed;

		if (bitString.isEmpty()) {
			composed = BigInteger.ZERO;
		} else {
			composed = new BigInteger(bitString, 2);
		}

		int nBits = this.getNbits() + secondHalf.getNbits();
		BigInteger bMod = BigInteger.valueOf(2).pow(nBits);
		try {
			return new SharemindBitVectorSecret(nBits, bMod, composed,
					this.player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}
	}

	public BitVectorSecret subVector(int start, int end) {
		StringBuilder sb = new StringBuilder();

		for (int i = start; i < end; i++) {
			int bit = 0;
			if (value.testBit(i)) {
				bit = 1;
			}
			sb.append(bit);
		}

		BigInteger finalValue;
		/*
		 * BigInteger values are in reverse, from right to left. The loop above
		 * this line cant be made in reverse or it will count the sign bit. we
		 * assume always positive
		 */
		String bitString = sb.reverse().toString();
		int nBits = end - start;

		if (bitString.isEmpty()) {
			/*
			 * when the end and start are 0 the biginteger must be zero and the
			 * secret must have one bit. or it will fail in the prefix or
			 * protocol. And it also makes sense that the number of bits be at
			 * least 1.
			 */
			finalValue = BigInteger.ZERO;
			nBits = 1;

		} else {
			finalValue = new BigInteger(bitString, 2);
		}
		BigInteger vMod = BigInteger.valueOf(2).pow(nBits);
		try {
			return new SharemindBitVectorSecret(nBits, vMod, finalValue, player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}

	}

	/*
	 * Most Non Significant Zero Bit Turns a bit array of the type 00111 to
	 * 00100. Turns every bit one to zero. For that it uses a xor. This
	 * operation does not require any round of communication.
	 */
	public BitVectorSecret msnzb() {
		return ((SharemindBitVectorSecret) prefixOr()).msnzLoop();
	}

	public BitVectorSecret msnzLoop() {

		BigInteger u = value;
		BigInteger s = value;

		for (int i = 0; i < this.nbits - 1; i++) {
			boolean iBit = u.testBit(i);
			boolean iNextBit = u.testBit(i + 1);

			// Xor operation in boolean
			boolean resultBit = iBit ^ iNextBit;

			if (resultBit) {
				s = s.setBit(i);
			} else {
				s = s.clearBit(i);
			}
		}

		try {
			return new SharemindBitVectorSecret(nbits, mod, s, player);
		} catch (InvalidSecretValue ex) {
			throw new IllegalStateException(ex);
		}
	}

	private int getHalf() {
		return this.nbits / 2;
	}

	@Override
	public Secret greaterOrEqualThan(Secret v) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
