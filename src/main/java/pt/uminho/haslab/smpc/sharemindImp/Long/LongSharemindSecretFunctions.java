package pt.uminho.haslab.smpc.sharemindImp.Long;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.interfaces.Player;

public class LongSharemindSecretFunctions {
    
    private static final LongSharemindDealer dealer = new LongSharemindDealer();

    private long getRandom() {
        return RandomGenerator.nextLong();
    }

    private int getDestPlayer(Player player) {
        // Calculates the target player based on this player id.
        return (player.getPlayerID() + 1) % 3;
    }

    private int getRecPlayer(Player player) {
        return (((player.getPlayerID() - 1) % 3) + 3) % 3;
    }

    private long mod(long value) {
        return  LongSharemindDealer.mod(value);
    }

    public long[] reshare(long[] shares, Player player) {

        long[] randomValues = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            randomValues[i] = getRandom();
        }

        int dest = getDestPlayer(player);
        player.sendValueToPlayer(dest, randomValues);

        int rec = getRecPlayer(player);

        long[] receivedValues = player.getLongValues(rec);

        long[] results = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            long result = (shares[i] + randomValues[i]) - receivedValues[i];
            results[i] = mod(result);
        }
        return results;
    }

    public long[] reshareBit(long[] shares, Player player) {

        long[] randomValues = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            randomValues[i] = getRandom() % 2;
        }

        int dest = getDestPlayer(player);
        player.sendValueToPlayer(dest, randomValues);

        int rec = getRecPlayer(player);

        long[] receivedValues = player.getLongValues(rec);

        long[] results = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            long result = (shares[i] + randomValues[i]) - receivedValues[i];
            results[i] = mod(result) % 2;
        }
        return results;
    }

    public long[] mult(long[] s1, long[] s2, Player player) {

        long[] resharedS1 = reshare(s1, player);
        long[] resharedS2 = reshare(s2, player);

        long[] reshared = new long[resharedS1.length + resharedS2.length];
        int dest = getDestPlayer(player);

        player.sendValueToPlayer(dest, reshared);

        int rec = getRecPlayer(player);

        long[] received = player.getLongValues(rec);
        long[] results = new long[s1.length];


        for (int i = 0; i < s1.length; i++) {
            long u = received[i];
            long v = received[i + s1.length];


            long resultPart1 = resharedS1[i] * resharedS2[i];
            long resultPart2 = resharedS1[i] * v;
            long resultPart3 = u * resharedS2[i];

            long result = resultPart1 + resultPart2 + resultPart3;

            results[i] = mod(result);
        }

        return results;
    }

    public long[] multBit(long[] s1, long[] s2, Player player) {

        long[] resharedS1 = reshareBit(s1, player);
        long[] resharedS2 = reshareBit(s2, player);
        long[] reshared = new long[resharedS1.length + resharedS2.length];

        System.arraycopy(resharedS1, 0, reshared, 0, resharedS1.length);
        System.arraycopy(resharedS2, 0, reshared, resharedS1.length, resharedS2.length);

        int dest = getDestPlayer(player);

        player.sendValueToPlayer(dest, reshared);

        int rec = getRecPlayer(player);

        long[] received = player.getLongValues(rec);
        long[] results = new long[s1.length];

        for (int i = 0; i < s1.length; i++) {
            long u = received[i];
            long v = received[i + s1.length];

            long resultPart1 = resharedS1[i] * resharedS2[i];
            long resultPart2 = resharedS1[i] * v;
            long resultPart3 = u * resharedS2[i];

            long result = resultPart1 + resultPart2 + resultPart3;

            results[i] = result % 2;
        }

        return results;
    }

    public long[] equal(long[] s1, long[] s2, Player player) {

        long[] ps = new long[s2.length];
        if (player.getPlayerID() == 0) {

            long[] r1s = new long[s2.length];
            long[] r2s = new long[s2.length];

            for (int i = 0; i < s2.length; i++) {
                r1s[i] = getRandom();
                r2s[i] = mod((s1[0] - s2[i]) - r1s[i]);
            }

            player.sendValueToPlayer(1, r1s);
            player.sendValueToPlayer(2, r2s);

            for (int i = 0; i < s2.length; i++) {
                /**
                 * This number is not magical. It is the greast possible number on the ring for longs.
                 *  2^62 -1
                 * */
                ps[i] = 4611686018427387903L;
            }

        } else {

            long[] rs = player.getLongValues(0);
            long[] es = new long[s2.length];

            for (int i = 0; i < s2.length; i++) {
                es[i] = mod((s1[0] - s2[i]) + rs[i]);
            }

            if (player.getPlayerID() == 1) {
                ps = es;
            } else {

                for (int i = 0; i < es.length; i++) {
                    ps[i] = mod(-es[i]);
                }
            }
        }
        return BitConj(ps, player);
    }


    private long[] ones(int size) {

        long[] ones = new long[size];

        for (int i = 0; i < size; i++) {
            ones[i] = 1L;
        }

        return ones;
    }

    private long[] zeros(int size) {

        long[] ones = new long[size];

        for (int i = 0; i < size; i++) {
            ones[i] = 0L;
        }

        return ones;
    }

    private long[] BitConj(long[] ps, Player player) {

        long[] ones = ones(ps.length);

        for (int i = 0; i < 62; i++) {
            long[] bitVals = new long[ps.length];

            for (int j = 0; j < ps.length; j++) {

                bitVals[j] = (ps[j] >> i) & 1L;

            }

            ones = multBit(ones, bitVals, player);

        }
        return ones;
    }


    private long[] reshareToTwo(long[] shares, Player player) {

        if (player.getPlayerID() == 0) {

            long r1s[] = new long[shares.length];
            long r2s[] = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                long r1 = getRandom();
                long r2 = mod(shares[i] - r1);
                r1s[i] = r1;
                r2s[i] = r2;
            }


            player.sendValueToPlayer(1, r1s);
            player.sendValueToPlayer(2, r2s);
            return zeros(shares.length);
        } else {
            long vals[] = player.getLongValues(0);

            long[] u = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                u[i] = mod(shares[i] + vals[i]);
            }
            return u;
        }
    }

    public long[] msnzb(long[] shares, Player player) {
        return msnzbLoop(prefixOr(shares, 0, 62, player));
    }


    public long[] prefixOr(long[] shares, int low, int high, Player player) {
        if (high - low <= 1) {
            return shares;
        } else {
            int half = (low + high) / 2;
            prefixOr(shares, low, half, player);
            prefixOr(shares, half, high, player);

            long[] bitHalfs = new long[shares.length];

            for (int j = 0; j < shares.length; j++) {
                bitHalfs[j] = 1L & (shares[j] >> half);
            }

            for (int i = low; i < half; i++) {
                long[] bitIs = new long[shares.length];

                for (int j = 0; j < shares.length; j++) {
                    bitIs[j] = 1L & (shares[j] >> i);
                }

                long[] multRes = multBit(bitIs, bitHalfs, player);

                for (int j = 0; j < multRes.length; j++) {
                    long res = bitIs[j] ^ bitHalfs[j] ^ multRes[j];

                    if ((bitIs[j] == 1 && res == 0) || (bitIs[j] == 0 && res == 1)) {
                        bitIs[j] = 1L << i;
                        shares[j] ^= bitIs[j];
                    }
                }
            }
            return shares;
        }
    }

    public long[] msnzbLoop(long[] shares) {

        long[] ss = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            long u = shares[i];

            for (int j = 0; j < 62; j++) {
                long jBit = 1L & (u >> j);
                long jNextBit = 1L & (u >> j + 1);

                long result = jBit ^ jNextBit;
                if ((jBit == 1 && result == 0) || (jBit == 0 && result == 1)) {
                    u ^= 1L << j;
                }
            }
            ss[i] = u;
        }

        return ss;
    }


    public long[] overflow(long[] shares, Player player) throws InvalidSecretValue {
        long[] ps = null;
        long[] cs = new long[shares.length];

        switch (player.getPlayerID()) {
            case 0:
                ps = zeros(shares.length);
                break;
            case 1:
                ps = new long[shares.length];
                System.arraycopy(shares, 0, ps, 0, shares.length);
                break;
            case 2:
                ps = new long[shares.length];
                for (int i = 0; i < shares.length; i++) {
                    ps[i] = mod(0 - shares[i]);
                }
                break;
        }

        System.arraycopy(ps, 0, cs, 0, cs.length);

        long[] ss = msnzb(cs, player);
        long[] u3s;

        if (player.getPlayerID() == 2) {
            long[] u1s = new long[shares.length];
            long[] u2s = new long[shares.length];
            u3s = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                long[] bitShares = dealer.shareXor(ps[i]);
                u1s[i] = bitShares[0];
                u2s[i] = bitShares[1];
                u3s[i] = bitShares[2];
            }
            player.sendValueToPlayer(0, u1s);
            player.sendValueToPlayer(1, u2s);

        } else {
            u3s = player.getLongValues(2);
        }

        return shareConv(overflowLoop(shares, ss, u3s, player), player);
    }


    public long[] overflowLoop(long[] shares, long[] ss, long[] us, Player player) {

        long[] lambdas = ones(ss.length);

        for (int i = 0; i < 62; i++) {
            long[] sbits = new long[ss.length];
            long[] uBits = new long[ss.length];

            for (int j = 0; j < ss.length; j++) {
                sbits[j] = 1L & (ss[j] >> i);
                uBits[j] = 1L & (us[j] >> i);
            }
            long[] bitRes = multBit(sbits, uBits, player);

            for (int j = 0; j < ss.length; j++) {
                lambdas[j] = lambdas[j] ^ bitRes[j];
            }
        }

        long[] results = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {

            long value = shares[i];
            long lambda = lambdas[i];

            if (player.getPlayerID() == 2 && value == 0) {
                results[i] = 1L ^ lambda;
            } else {
                results[i] = lambda;
            }

        }
        return results;
    }

    public long[] shareConv(long[] shares, Player player) {

        if (player.getPlayerID() == 0) {
            long[] m12s = new long[shares.length];
            long[] m13s = new long[shares.length];
            long[] b12s = new long[shares.length];
            long[] b13s = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                long value = shares[i];
                long b = getRandom() % 2;
                long m = b ^ value;
                long m12 = getRandom() % 2;
                long m13 = mod(m - m12);
                long b12 = getRandom() % 2;
                long b13 = b ^ b12;

                m12s[i] = m12;
                m13s[i] = m13;
                b12s[i] = b12;
                b13s[i] = b13;
            }

            long[] p1ms = new long[m12s.length + b12s.length];
            long[] p2ms = new long[m13s.length + b13s.length];

            System.arraycopy(m12s, 0, p1ms, 0, m12s.length);
            System.arraycopy(b12s, 0, p1ms, m12s.length, b12s.length);

            System.arraycopy(m13s, 0, p2ms, 0, m13s.length);
            System.arraycopy(b13s, 0, p2ms, m13s.length, b13s.length);

            player.sendValueToPlayer(1, p1ms);
            player.sendValueToPlayer(2, p2ms);

            return zeros(shares.length);

        } else {
            long[] received = player.getLongValues(0);

            long[] s1s = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                s1s[i] = received[i + shares.length] ^ shares[i];
            }

            int dest = player.getPlayerID() == 2 ? 1 : 2;

            player.sendValueToPlayer(dest, s1s);

            long[] s2s = player.getLongValues(dest);

            long[] vs = new long[shares.length];

            for (int i = 0; i < shares.length; i++) {
                long s = s1s[i] ^ s2s[i];
                long m = received[i];

                if (s == 1) {
                    if (player.getPlayerID() == 1) {
                        vs[i] = mod(1 - m);
                    } else {
                        vs[i] = mod(0 - m);
                    }
                } else {
                    vs[i] = m;
                }
            }

            return vs;
        }
    }


    private long[] shiftL(long[] shares) {
        long[] results = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            results[i] = mod(shares[i] * 2);
        }
        return results;
    }


    private long[] shiftR(long[] shares, Player player) throws InvalidSecretValue {

        long[] toReshare = new long[shares.length];
        System.arraycopy(shares, 0, toReshare, 0, shares.length);
        long[] reshared = reshareToTwo(toReshare, player);

        long[] rshift = shiftL(reshared);

        long[] deltaOnes = overflow(reshared, player);
        long[] deltaTwos = overflow(rshift, player);

        long[] results = new long[shares.length];

        for (int i = 0; i < shares.length; i++) {
            long v = reshared[i] >> 61;
            results[i] = mod((v - 2 * deltaOnes[i]) + deltaTwos[i]);
        }

        return results;
    }

    public long[] greaterOrEqualThan(long[] v1, long[] v2, Player player) throws InvalidSecretValue {

        long[] diffs = new long[v1.length];

        for (int i = 0; i < v1.length; i++) {
            diffs[i] = mod(v1[i] - v2[0]);
        }
        return reshare(shiftR(diffs, player), player);

    }
}
