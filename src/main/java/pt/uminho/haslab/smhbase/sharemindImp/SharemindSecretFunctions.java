package pt.uminho.haslab.smhbase.sharemindImp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import pt.uminho.haslab.smhbase.exceptions.InvalidNumberOfBits;
import pt.uminho.haslab.smhbase.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smhbase.helpers.RandomGenerator;
import pt.uminho.haslab.smhbase.interfaces.Player;

public class SharemindSecretFunctions {

	private final int nbits;
	private final BigInteger mod;

	public SharemindSecretFunctions(int nbits) {
		this.nbits = nbits + 1;
		mod = BigInteger.valueOf(2).pow(this.nbits);
	}

	public SharemindSecretFunctions(int nbits, BigInteger mod) {
		this.nbits = nbits;
		this.mod = mod;
	}

	private int getDestPlayer(Player player) {
		// Calculates the target player based on this player id.
		return (player.getPlayerID() + 1) % 3;
	}

	private int getRecPlayer(Player player) {
		return BigInteger.valueOf(player.getPlayerID())
				.subtract(BigInteger.ONE).mod(BigInteger.valueOf(3)).intValue();
	}

	public List<byte[]> reshare(List<byte[]> shares, Player player) {

		List<byte[]> randomValues = new ArrayList<byte[]>();

		for (byte[] s : shares) {
			BigInteger randomValue = new BigInteger(this.nbits,
					RandomGenerator.generator);
			randomValues.add(randomValue.toByteArray());
		}

		// Calculates the target player based on this player id.
		int dest = getDestPlayer(player);
		player.sendValueToPlayer(dest, randomValues);

		int rec = getRecPlayer(player);
		List<byte[]> receivedValues = player.getValues(rec);

		List<byte[]> results = new ArrayList<byte[]>();

		for (int i = 0; i < shares.size(); i++) {
			BigInteger value = new BigInteger(shares.get(i));
			BigInteger randomValue = new BigInteger(randomValues.get(i));
			BigInteger receivedValue = new BigInteger(receivedValues.get(i));
			BigInteger result = value.add(randomValue).subtract(receivedValue);
			results.add(result.mod(mod).toByteArray());
		}

		return results;
	}

	public List<byte[]> mult(List<byte[]> s1, List<byte[]> s2, Player player) {
		List<byte[]> resharedS1 = reshare(s1, player);
		List<byte[]> resharedS2 = reshare(s2, player);

		List<byte[]> reshared = new ArrayList<byte[]>();

		reshared.addAll(resharedS1);
		reshared.addAll(resharedS2);

		int dest = getDestPlayer(player);

		player.sendValueToPlayer(dest, reshared);

		int rec = getRecPlayer(player);

		List<byte[]> received = player.getValues(rec);

		List<byte[]> results = new ArrayList<byte[]>();

		for (int i = 0; i < s1.size(); i++) {
			BigInteger receivedU = new BigInteger(received.get(i));
			BigInteger receivedV = new BigInteger(received.get(i + s1.size()));
			BigInteger resUValue = new BigInteger(resharedS1.get(i));
			BigInteger resVValue = new BigInteger(resharedS2.get(i));

			BigInteger resultPart1 = resUValue.multiply(resVValue);
			BigInteger resultPart2 = resUValue.multiply(receivedV);
			BigInteger resultPart3 = receivedU.multiply(resVValue);
			BigInteger result = resultPart1.add(resultPart2.add(resultPart3));
			results.add(result.mod(mod).toByteArray());
		}

		return results;
	}

	public List<byte[]> equal(List<byte[]> s1, List<byte[]> s2, Player player) {

		List<byte[]> ps = new ArrayList<byte[]>();
		if (player.getPlayerID() == 0) {
			List<byte[]> r1s = new ArrayList<byte[]>();
			List<byte[]> r2s = new ArrayList<byte[]>();

			for (int i = 0; i < s1.size(); i++) {
				BigInteger value = new BigInteger(s1.get(i));
				BigInteger received = new BigInteger(s2.get(i));

				BigInteger r1 = new BigInteger(this.nbits,
						RandomGenerator.generator);

				BigInteger r2 = value.subtract(received).subtract(r1).mod(mod);
				r1s.add(r1.toByteArray());
				r2s.add(r2.toByteArray());
			}
			player.sendValueToPlayer(1, r1s);
			player.sendValueToPlayer(2, r2s);

			byte[] p = mod.subtract(BigInteger.ONE).toByteArray();
			for (byte[] s11 : s1) {
				ps.add(p);
			}

		} else {
			List<byte[]> rs = player.getValues(0);
			List<byte[]> es = new ArrayList<byte[]>();

			for (int i = 0; i < s1.size(); i++) {
				BigInteger value = new BigInteger(s1.get(i));
				BigInteger received = new BigInteger(s2.get(i));
				BigInteger r = new BigInteger(rs.get(i));

				BigInteger e = value.subtract(received).add(r).mod(mod);
				es.add(e.toByteArray());
			}
			if (player.getPlayerID() == 1) {
				ps = es;
			} else {
				for (byte[] e : es) {
					byte[] p = BigInteger.ZERO.subtract(new BigInteger(e))
							.mod(mod).toByteArray();
					ps.add(p);

				}
			}
		}

		return BitConj(ps, player);

	}

	private List<byte[]> ones(int size) {

		List<byte[]> ones = new ArrayList<byte[]>();

		for (int i = 0; i < size; i++) {
			ones.add(BigInteger.ONE.toByteArray());
		}

		return ones;
	}

	private List<byte[]> zeros(int size) {

		List<byte[]> ones = new ArrayList<byte[]>();

		for (int i = 0; i < size; i++) {
			ones.add(BigInteger.ZERO.toByteArray());
		}

		return ones;
	}

	private List<byte[]> BitConj(List<byte[]> ps, Player player) {
		BigInteger bitMod = BigInteger.valueOf(2);
		List<byte[]> ones = ones(ps.size());

		SharemindSecretFunctions ssfbit = new SharemindSecretFunctions(1,
				bitMod);

		for (int i = 0; i < this.nbits; i++) {
			List<byte[]> bitVals = new ArrayList<byte[]>();

			for (byte[] val : ps) {
				int bitVal = 0;
				BigInteger value = new BigInteger(val);

				if (value.testBit(i)) {
					bitVal = 1;
				}
				bitVals.add(BigInteger.valueOf(bitVal).toByteArray());
			}
			ones = ssfbit.mult(ones, bitVals, player);
		}
		return ones;
	}

	public List<byte[]> reshareToTwo(List<byte[]> shares, Player player) {
		List<byte[]> u = new ArrayList<byte[]>();

		if (player.getPlayerID() == 0) {
			List<byte[]> r1s = new ArrayList<byte[]>();
			List<byte[]> r2s = new ArrayList<byte[]>();

			for (byte[] val : shares) {
				BigInteger value = new BigInteger(val);
				BigInteger r1 = new BigInteger(nbits, RandomGenerator.generator);
				BigInteger r2 = value.subtract(r1).mod(mod);
				r1s.add(r1.toByteArray());
				r2s.add(r2.toByteArray());

			}

			u = zeros(shares.size());
			player.sendValueToPlayer(1, r1s);
			player.sendValueToPlayer(2, r2s);

		} else {
			List<byte[]> vals = player.getValues(0);

			for (int i = 0; i < shares.size(); i++) {
				BigInteger value = new BigInteger(shares.get(i));
				BigInteger r = new BigInteger(vals.get(i));

				u.add(value.add(r).mod(mod).toByteArray());
			}

		}
		return u;
	}

	public List<byte[]> shareConv(List<byte[]> shares, Player player) {

		List<byte[]> vs = new ArrayList<byte[]>();

		if (player.getPlayerID() == 0) {

			List<byte[]> m12s = new ArrayList<byte[]>();
			List<byte[]> m13s = new ArrayList<byte[]>();
			List<byte[]> b12s = new ArrayList<byte[]>();
			List<byte[]> b13s = new ArrayList<byte[]>();

			for (byte[] share : shares) {
				BigInteger value = new BigInteger(share);
				BigInteger b = new BigInteger(1, RandomGenerator.generator);
				BigInteger m = b.xor(value);
				BigInteger m12 = new BigInteger(nbits,
						RandomGenerator.generator);
				BigInteger m13 = m.subtract(m12).mod(mod);
				BigInteger b12 = new BigInteger(1, RandomGenerator.generator);
				BigInteger b13 = b.xor(b12);

				m12s.add(m12.toByteArray());
				m13s.add(m13.toByteArray());
				b12s.add(b12.toByteArray());
				b13s.add(b13.toByteArray());

			}
			// messages to player 1
			List<byte[]> p1ms = new ArrayList<byte[]>();
			// messages to player 2
			List<byte[]> p2ms = new ArrayList<byte[]>();
			p1ms.addAll(m12s);
			p1ms.addAll(b12s);

			p2ms.addAll(m13s);
			p2ms.addAll(b13s);

			player.sendValueToPlayer(1, p1ms);
			player.sendValueToPlayer(2, p2ms);
			vs = zeros(shares.size());
		} else {

			List<byte[]> received = player.getValues(0);

			List<byte[]> ms = received.subList(0, shares.size());
			List<byte[]> bs = received.subList(shares.size(), received.size());
			List<byte[]> s1s = new ArrayList<byte[]>();

			for (int i = 0; i < shares.size(); i++) {
				BigInteger b = new BigInteger(bs.get(i));
				BigInteger value = new BigInteger(shares.get(i));
				BigInteger s1 = b.xor(value);
				s1s.add(s1.toByteArray());
			}

			int dest = 2;

			if (player.getPlayerID() == 2) {
				dest = 1;
			}

			player.sendValueToPlayer(dest, s1s);
			List<byte[]> s2s = player.getValues(dest);
			List<BigInteger> ss = new ArrayList<BigInteger>();

			for (int i = 0; i < shares.size(); i++) {
				BigInteger s1 = new BigInteger(s1s.get(i));
				BigInteger s2 = new BigInteger(s2s.get(i));
				BigInteger s = s1.xor(s2);
				ss.add(s);
			}

			for (int i = 0; i < shares.size(); i++) {
				BigInteger s = ss.get(i);
				BigInteger m = new BigInteger(ms.get(i));

				if (s.equals(BigInteger.ONE)) {
					if (player.getPlayerID() == 1) {
						vs.add(BigInteger.ONE.subtract(m).mod(mod)
								.toByteArray());
					} else {
						vs.add(BigInteger.ZERO.subtract(m).mod(mod)
								.toByteArray());
					}
				} else {
					vs.add(m.toByteArray());
				}
			}

		}
		return vs;
	}

	private int getHalf() {
		return this.nbits / 2;
	}

	public List<byte[]> prefixOr(List<byte[]> shares, Player player) {

		if (this.nbits == 1) {
			return shares;
		} else {

			int half = getHalf();
			SubVector firstSub = subVector(shares, 0, half);
			SubVector secondSub = subVector(shares, half, nbits);

			SharemindSecretFunctions ssfFirst = new SharemindSecretFunctions(
					firstSub.nbits, firstSub.getMod());
			SharemindSecretFunctions ssfSecond = new SharemindSecretFunctions(
					secondSub.nbits, secondSub.getMod());

			List<byte[]> firstHalf = ssfFirst.prefixOr(firstSub.getShares(),
					player);
			List<byte[]> secondHalf = ssfSecond.prefixOr(secondSub.getShares(),
					player);

			SubVector firstHalfToJoin = new SubVector(firstHalf,
					firstSub.getNbits());
			SubVector secondHalfToJoin = new SubVector(secondHalf,
					secondSub.getNbits());

			SubVector joined = jointWith(firstHalfToJoin, secondHalfToJoin);

			return prefixOrLoop(joined.getShares(), player);
		}
	}

	public class SubVector {

		private final List<byte[]> shares;
		private final int nbits;
		private final BigInteger mod;

		public SubVector(List<byte[]> shares, int nbits) {

			this.shares = shares;
			this.nbits = nbits;
			mod = BigInteger.valueOf(2).pow(nbits);
		}

		public List<byte[]> getShares() {
			return shares;
		}

		public int getNbits() {
			return nbits;
		}

		public BigInteger getMod() {
			return mod;
		}
	}

	public SubVector subVector(List<byte[]> shares, int start, int end) {

		List<StringBuilder> sbs = new ArrayList<StringBuilder>();

		for (byte[] share : shares) {
			StringBuilder sb = new StringBuilder();

			for (int i = start; i < end; i++) {
				int bit = 0;
				BigInteger value = new BigInteger(share);
				if (value.testBit(i)) {
					bit = 1;
				}
				sb.append(bit);
			}
			sbs.add(sb);

		}

		List<byte[]> finalValues = new ArrayList<byte[]>();
		int nBits = end - start;

		for (int i = 0; i < shares.size(); i++) {
			/*
			 * BigInteger values are in reverse, from right to left. The loop
			 * above this line cant be made in reverse or it will count the sign
			 * bit. we assume always positive
			 */
			String bitString = sbs.get(i).reverse().toString();

			if (bitString.isEmpty()) {
				/*
				 * when the end and start are 0 the biginteger must be zero and
				 * the secret must have one bit. or it will fail in the prefix
				 * or protocol. And it also makes sense that the number of bits
				 * be at least 1.
				 */
				finalValues.add(BigInteger.ZERO.toByteArray());
				nBits = 1;
			} else {
				finalValues.add(new BigInteger(bitString, 2).toByteArray());
			}

		}
		return new SubVector(finalValues, nBits);
	}

	public SubVector jointWith(SubVector firstHalfVector,
			SubVector secondHalfVector) {
		List<byte[]> firstHalf = firstHalfVector.getShares();
		int firstHalfNBits = firstHalfVector.getNbits();
		List<byte[]> secondHalf = secondHalfVector.getShares();
		int secondHalfNBits = secondHalfVector.getNbits();

		List<byte[]> results = new ArrayList<byte[]>();
		int nBits = firstHalfNBits + secondHalfNBits;
		for (int i = 0; i < firstHalf.size(); i++) {
			BigInteger fhValue = new BigInteger(firstHalf.get(i));
			BigInteger shValue = new BigInteger(secondHalf.get(i));

			StringBuilder sbt = new StringBuilder();

			for (int j = 0; j < firstHalfNBits; j++) {
				int bit = 0;

				if (fhValue.testBit(j)) {
					bit = 1;
				}
				sbt.append(bit);
			}

			for (int j = 0; j < secondHalfNBits; j++) {
				int bit = 0;

				if (shValue.testBit(j)) {
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
			results.add(composed.toByteArray());
		}

		return new SubVector(results, nBits);

	}

	public List<byte[]> prefixOrLoop(List<byte[]> shares, Player p) {

		List<byte[]> bitHalfs = new ArrayList<byte[]>();
		List<byte[]> originals = new ArrayList<byte[]>(shares);
		BigInteger bitMod = BigInteger.valueOf(2);

		for (byte[] share : shares) {

			BigInteger bitHalf = BigInteger.ZERO;
			BigInteger value = new BigInteger(share);
			if (value.testBit(getHalf())) {
				bitHalf = BigInteger.ONE;
			}
			bitHalfs.add(bitHalf.toByteArray());
		}
		SharemindSecretFunctions ssf = new SharemindSecretFunctions(1, bitMod);

		for (int i = 0; i < getHalf(); i++) {

			List<byte[]> bitIs = new ArrayList<byte[]>();

			for (int j = 0; j < shares.size(); j++) {
				BigInteger value = new BigInteger(shares.get(j));
				BigInteger bitI = BigInteger.ZERO;

				if (value.testBit(i)) {
					bitI = BigInteger.ONE;
				}
				bitIs.add(bitI.toByteArray());
			}

			List<byte[]> multRes = ssf.mult(bitIs, bitHalfs, p);

			for (int j = 0; j < multRes.size(); j++) {
				BigInteger bitI = new BigInteger(bitIs.get(j));
				BigInteger multR = new BigInteger(multRes.get(j));
				BigInteger bitHalf = new BigInteger(bitHalfs.get(j));

				BigInteger finalR = bitI.xor(bitHalf).xor(multR);

				if (!finalR.equals(bitI)) {
					BigInteger original = new BigInteger(originals.get(j));
					originals.set(j, original.flipBit(i).toByteArray());
				}
			}
		}
		return originals;

	}

	public List<byte[]> msnzb(List<byte[]> shares, Player p) {

		return msnzLoop(prefixOr(shares, p));

	}

	/*
	 * Most Non Significant Zero Bit Turns a bit array of the type 00111 to
	 * 00100. Turns every bit one to zero. For that it uses a xor. This
	 * operation does not require any round of communication.
	 */
	public List<byte[]> msnzLoop(List<byte[]> shares) {

		List<byte[]> ss = new ArrayList<byte[]>();

		for (byte[] val : shares) {
			BigInteger u = new BigInteger(val);
			BigInteger s = new BigInteger(val);
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
			ss.add(s.toByteArray());

		}
		return ss;
	}

	public List<byte[]> overflow(List<byte[]> shares, Player player)
			throws InvalidNumberOfBits, InvalidSecretValue {

		List<byte[]> ps = new ArrayList<byte[]>();

		for (byte[] share : shares) {

			switch (player.getPlayerID()) {
				case 0 :
					ps.add(BigInteger.ZERO.toByteArray());
					break;
				case 1 :
					ps.add(share);
					break;
				case 2 :
					BigInteger value = new BigInteger(share);
					BigInteger p = BigInteger.ZERO.subtract(value).mod(mod);
					ps.add(p.toByteArray());
					break;
			}
		}

		SharemindSecretFunctions ssf = new SharemindSecretFunctions(nbits, mod);
		List<byte[]> ss = ssf.msnzb(ps, player);
		List<byte[]> u3s;
		if (player.getPlayerID() == 2) {
			SharemindBitVectorDealer dealer;
			dealer = new SharemindBitVectorDealer(nbits);
			List<byte[]> u1s = new ArrayList<byte[]>();
			List<byte[]> u2s = new ArrayList<byte[]>();
			u3s = new ArrayList<byte[]>();
			for (byte[] p : ps) {
				BigInteger bp = new BigInteger(p);
				SharemindBitVectorSharedSecret share = (SharemindBitVectorSharedSecret) dealer
						.share(bp);
				u1s.add(share.getU1().toByteArray());
				u2s.add(share.getU2().toByteArray());
				u3s.add(share.getU3().toByteArray());
			}

			player.sendValueToPlayer(0, u1s);
			player.sendValueToPlayer(1, u2s);
		} else {
			u3s = player.getValues(2);
		}
		return shareConv(overflowLoop(shares, ss, u3s, player), player);
	}

	public List<byte[]> overflowLoop(List<byte[]> shares, List<byte[]> ss,
			List<byte[]> us, Player player) {

		// Every player sets lambda to one making a global xored 1.
		List<BigInteger> lambdas = new ArrayList<BigInteger>();

		for (int i = 0; i < ss.size(); i++) {
			lambdas.add(BigInteger.ONE);
		}
		BigInteger bMod = BigInteger.valueOf(2).pow(1);

		SharemindSecretFunctions ssf = new SharemindSecretFunctions(1, bMod);

		for (int i = 0; i < nbits; i++) {

			List<byte[]> sBits = new ArrayList<byte[]>();
			List<byte[]> uBits = new ArrayList<byte[]>();

			for (int j = 0; j < ss.size(); j++) {
				BigInteger s = new BigInteger(ss.get(j));
				BigInteger u = new BigInteger(us.get(j));

				byte[] sBit = bitSecret(s.testBit(i));
				byte[] uBit = bitSecret(u.testBit(i));
				sBits.add(sBit);
				uBits.add(uBit);
			}
			List<byte[]> bitRes = ssf.mult(sBits, uBits, player);

			for (int j = 0; j < bitRes.size(); j++) {
				BigInteger bitR = new BigInteger(bitRes.get(j));
				lambdas.set(j, lambdas.get(j).xor(bitR));

			}

		}
		List<byte[]> results = new ArrayList<byte[]>();
		for (int i = 0; i < shares.size(); i++) {
			BigInteger value = new BigInteger(shares.get(i));
			BigInteger lambda = lambdas.get(i);
			if (player.getPlayerID() == 2 && value.equals(BigInteger.ZERO)) {
				results.add(BigInteger.ONE.xor(lambda).toByteArray());
			} else {
				results.add(lambda.toByteArray());
			}

		}
		return results;
	}

	private byte[] bitSecret(boolean bit) {
		BigInteger bValue = BigInteger.ZERO;

		if (bit) {
			bValue = BigInteger.ONE;
		}

		return bValue.toByteArray();

	}

	/*
	 * When using the shiftL by any protocol it is required to know and pay
	 * attention that an extra bit is used to store the values and the mod is
	 * also in those nbits.
	 */
	public List<byte[]> shiftL(List<byte[]> shares, int shiftLeftNBits) {
		List<byte[]> results = new ArrayList<byte[]>();

		for (byte[] share : shares) {
			BigInteger value = new BigInteger(share);
			BigInteger toShift = BigInteger.valueOf(2).pow(shiftLeftNBits);
			BigInteger shiftedValue = value.multiply(toShift).mod(mod);
			results.add(shiftedValue.toByteArray());
		}

		return results;
	}

	/* TODO: Throw exception if shiftN > nbits */
	public List<byte[]> shiftR(List<byte[]> shares, int shiftN, Player player)
			throws InvalidNumberOfBits, InvalidSecretValue {

		List<byte[]> reshared = reshareToTwo(shares, player);

		List<byte[]> shifted = shiftL(reshared, nbits - shiftN);

		List<byte[]> desltaOnes = overflow(reshared, player);
		List<byte[]> deltaTwos = overflow(shifted, player);

		List<byte[]> results = new ArrayList<byte[]>();

		for (int i = 0; i < shares.size(); i++) {
			BigInteger v = new BigInteger(reshared.get(i));
			v = v.shiftRight(shiftN);
			BigInteger powerValue = BigInteger.valueOf(2).pow(nbits - shiftN);
			BigInteger deltaOne = new BigInteger(desltaOnes.get(i));
			BigInteger deltaTwo = new BigInteger(deltaTwos.get(i));
			BigInteger result = v.subtract(powerValue.multiply(deltaOne))
					.add(deltaTwo).mod(mod);
			results.add(result.toByteArray());
		}

		return results;

	}

	public List<byte[]> greaterOrEqualThan(List<byte[]> v1, List<byte[]> v2,
			Player player) throws InvalidNumberOfBits, InvalidSecretValue {
		List<byte[]> diffs = new ArrayList<byte[]>();

		for (int i = 0; i < v1.size(); i++) {
			BigInteger b1v = new BigInteger(v1.get(i));
			BigInteger b2v = new BigInteger(v2.get(i));
			BigInteger diff = b1v.subtract(b2v).mod(mod);
			diffs.add(diff.toByteArray());
		}

		return reshare(shiftR(diffs, nbits - 1, player), player);
	}

}
