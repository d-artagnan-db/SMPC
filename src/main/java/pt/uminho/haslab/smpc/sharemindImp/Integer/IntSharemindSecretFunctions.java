package pt.uminho.haslab.smpc.sharemindImp.Integer;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.interfaces.Player;

public class IntSharemindSecretFunctions {

    private static final IntSharemindDealer dealer = new IntSharemindDealer();

    private int getRandom() {
        return RandomGenerator.nextInt();
    }

    private int getDestPlayer(Player player) {
        // Calculates the target player based on this player id.
        return (player.getPlayerID() + 1) % 3;
    }

    private int getRecPlayer(Player player) {
        return (((player.getPlayerID() - 1) % 3) + 3) % 3;
    }

    private int mod(int value) {
        return  IntSharemindDealer.mod(value);
    }

    public int[] reshare(int[] shares, Player player) {

        int[] randomValues = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            randomValues[i] = getRandom();
        }

        int dest = getDestPlayer(player);
        player.sendValueToPlayer(dest, randomValues);

        int rec = getRecPlayer(player);

        int[] receivedValues = player.getIntValues(rec);

        int[] results = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            int result = (shares[i] + randomValues[i]) - receivedValues[i];
            results[i] = mod(result);
        }
        return results;
    }

    public int[] reshareBit(int[] shares, Player player) {

        int[] randomValues = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            randomValues[i] = getRandom() % 2;
        }

        int dest = getDestPlayer(player);
        player.sendValueToPlayer(dest, randomValues);

        int rec = getRecPlayer(player);

        int[] receivedValues = player.getIntValues(rec);

        int[] results = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            int result = (shares[i] + randomValues[i]) - receivedValues[i];
            results[i] = mod(result) % 2;
        }
        return results;
    }

    public int[] mult(int[] s1, int[] s2, Player player) {

        int[] resharedS1 = reshare(s1, player);
        int[] resharedS2 = reshare(s2, player);

        int[] reshared = new int[resharedS1.length + resharedS2.length];
        int dest = getDestPlayer(player);

        player.sendValueToPlayer(dest, reshared);

        int rec = getRecPlayer(player);

        int[] received = player.getIntValues(rec);
        int[] results = new int[s1.length];


        for (int i = 0; i < s1.length; i++) {
            int u = received[i];
            int v = received[i + s1.length];


            int resultPart1 = resharedS1[i] * resharedS2[i];
            int resultPart2 = resharedS1[i] * v;
            int resultPart3 = u * resharedS2[i];

            int result = resultPart1 + resultPart2 + resultPart3;

            results[i] = mod(result);
        }

        return results;
    }

    public int[] multBit(int[] s1, int[] s2, Player player) {

        int[] resharedS1 = reshareBit(s1, player);
        int[] resharedS2 = reshareBit(s2, player);
        int[] reshared = new int[resharedS1.length + resharedS2.length];
        System.arraycopy(resharedS1, 0, reshared, 0, resharedS1.length);
        System.arraycopy(resharedS2, 0, reshared, resharedS1.length, resharedS2.length);
        int dest = getDestPlayer(player);

        player.sendValueToPlayer(dest, reshared);

        int rec = getRecPlayer(player);

        int[] received = player.getIntValues(rec);
        int[] results = new int[s1.length];

        for (int i = 0; i < s1.length; i++) {
            int u = received[i];
            int v = received[i + s1.length];


            int resultPart1 = resharedS1[i] * resharedS2[i];
            int resultPart2 = resharedS1[i] * v;
            int resultPart3 = u * resharedS2[i];

            int result = resultPart1 + resultPart2 + resultPart3;

            results[i] = result % 2;
        }

        return results;
    }

    public int[] equal(int[] s1, int[] s2, Player player) {

        int[] ps = new int[s2.length];
        if (player.getPlayerID() == 0) {

            int[] r1s = new int[s2.length];
            int[] r2s = new int[s2.length];

            for (int i = 0; i < s2.length; i++) {
                r1s[i] = getRandom();
                r2s[i] = mod((s1[0] - s2[i]) - r1s[i]);
            }

            player.sendValueToPlayer(1, r1s);
            player.sendValueToPlayer(2, r2s);

            for (int i = 0; i < s2.length; i++) {
                /**
                 * This number is not magical. It is the greatest possible number on the ring for ints.
                 *  2^30 -1
                 * */
                ps[i] = 1073741823;
            }
        } else {

            int[] rs = player.getIntValues(0);
            int[] es = new int[s2.length];

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


    private int[] ones(int size) {

        int[] ones = new int[size];

        for (int i = 0; i < size; i++) {
            ones[i] = 1;
        }

        return ones;
    }

    private int[] zeros(int size) {

        int[] ones = new int[size];

        for (int i = 0; i < size; i++) {
            ones[i] = 0;
        }

        return ones;
    }

    private int[] BitConj(int[] ps, Player player) {

        int[] ones = ones(ps.length);

        for (int i = 0; i < 30; i++) {
            int[] bitVals = new int[ps.length];

            for (int j = 0; j < ps.length; j++) {

                bitVals[j] = (ps[j] >> i) & 1;

            }

            ones = multBit(ones, bitVals, player);


        }

        return ones;
    }


    private int[] reshareToTwo(int[] shares, Player player) {

        if (player.getPlayerID() == 0) {

            int r1s[] = new int[shares.length];
            int r2s[] = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                int r1 = getRandom();
                int r2 = mod(shares[i] - r1);
                r1s[i] = r1;
                r2s[i] = r2;
            }


            player.sendValueToPlayer(1, r1s);
            player.sendValueToPlayer(2, r2s);
            return zeros(shares.length);
        } else {
            int vals[] = player.getIntValues(0);

            int[] u = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                u[i] = mod(shares[i] + vals[i]);
            }

            return u;
        }
    }

    public int[] msnzb(int[] shares, Player player) {
        return msnzbLoop(prefixOr(shares, 0, 30, player));
    }


    public int[] prefixOr(int[] shares, int low, int high, Player player) {
        if (high - low <= 1) {
            return shares;
        } else {
            int half = (low + high) / 2;
            prefixOr(shares, low, half, player);
            prefixOr(shares, half, high, player);

            int[] bitHalfs = new int[shares.length];

            for (int j = 0; j < shares.length; j++) {
                bitHalfs[j] = 1 & (shares[j] >> half);
            }

            for (int i = low; i < half; i++) {
                int[] bitIs = new int[shares.length];

                for (int j = 0; j < shares.length; j++) {
                    bitIs[j] = 1 & (shares[j] >> i);
                }

                int[] multRes = multBit(bitIs, bitHalfs, player);

                for (int j = 0; j < multRes.length; j++) {
                    int res = bitIs[j] ^ bitHalfs[j] ^ multRes[j];

                    if ((bitIs[j] == 1 && res == 0) || (bitIs[j] == 0 && res == 1)) {
                        bitIs[j] = 1 << i;
                        shares[j] ^= bitIs[j];
                    }
                }
            }
            return shares;
        }
    }

    public int[] msnzbLoop(int[] shares) {

        int[] ss = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            int u = shares[i];

            for (int j = 0; j < 30; j++) {
                int jBit = 1 & (u >> j);
                int jNextBit = 1 & (u >> j + 1);

                int result = jBit ^ jNextBit;
                if ((jBit == 1 && result == 0) || (jBit == 0 && result == 1)) {
                    u ^= 1 << j;
                }
            }
            ss[i] = u;
        }

        return ss;
    }


    public int[] overflow(int[] shares, Player player) throws InvalidSecretValue {
        int[] ps = null;
        int[] cs = new int[shares.length];

        switch (player.getPlayerID()) {
            case 0:
                ps = zeros(shares.length);
                break;
            case 1:
                ps = new int[shares.length];
                System.arraycopy(shares, 0, ps, 0, shares.length);
                break;
            case 2:
                ps = new int[shares.length];
                for (int i = 0; i < shares.length; i++) {
                    ps[i] = mod(0 - shares[i]);
                }
                break;
        }

        System.arraycopy(ps, 0, cs, 0, cs.length);

        int[] ss = msnzb(cs, player);
        int[] u3s;

        if (player.getPlayerID() == 2) {
            int[] u1s = new int[shares.length];
            int[] u2s = new int[shares.length];
            u3s = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                int[] bitShares = dealer.shareXor(ps[i]);
                u1s[i] = bitShares[0];
                u2s[i] = bitShares[1];
                u3s[i] = bitShares[2];
            }
            player.sendValueToPlayer(0, u1s);
            player.sendValueToPlayer(1, u2s);

        } else {
            u3s = player.getIntValues(2);
        }

        return shareConv(overflowLoop(shares, ss, u3s, player), player);
    }


    public int[] overflowLoop(int[] shares, int[] ss, int[] us, Player player) {

        int[] lambdas = ones(ss.length);

        for (int i = 0; i < 30; i++) {
            int[] sbits = new int[ss.length];
            int[] uBits = new int[ss.length];

            for (int j = 0; j < ss.length; j++) {
                sbits[j] = 1 & (ss[j] >> i);
                uBits[j] = 1 & (us[j] >> i);
            }
            int[] bitRes = multBit(sbits, uBits, player);

            for (int j = 0; j < ss.length; j++) {
                lambdas[j] = lambdas[j] ^ bitRes[j];
            }
        }

        int[] results = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {

            int value = shares[i];
            int lambda = lambdas[i];

            if (player.getPlayerID() == 2 && value == 0) {
                results[i] = 1 ^ lambda;
            } else {
                results[i] = lambda;
            }

        }
        return results;
    }

    public int[] shareConv(int[] shares, Player player) {

        if (player.getPlayerID() == 0) {
            int[] m12s = new int[shares.length];
            int[] m13s = new int[shares.length];
            int[] b12s = new int[shares.length];
            int[] b13s = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                int value = shares[i];
                int b = getRandom() % 2;
                int m = b ^ value;
                int m12 = getRandom() % 2;
                int m13 = mod(m - m12);
                int b12 = getRandom() % 2;
                int b13 = b ^ b12;

                m12s[i] = m12;
                m13s[i] = m13;
                b12s[i] = b12;
                b13s[i] = b13;
            }

            int[] p1ms = new int[m12s.length + b12s.length];
            int[] p2ms = new int[m13s.length + b13s.length];

            System.arraycopy(m12s, 0, p1ms, 0, m12s.length);
            System.arraycopy(b12s, 0, p1ms, m12s.length, b12s.length);

            System.arraycopy(m13s, 0, p2ms, 0, m13s.length);
            System.arraycopy(b13s, 0, p2ms, m13s.length, b13s.length);

            player.sendValueToPlayer(1, p1ms);
            player.sendValueToPlayer(2, p2ms);

            return zeros(shares.length);

        } else {
            int[] received = player.getIntValues(0);

            int[] s1s = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                s1s[i] = received[i + shares.length] ^ shares[i];
            }

            int dest = player.getPlayerID() == 2 ? 1 : 2;

            player.sendValueToPlayer(dest, s1s);

            int[] s2s = player.getIntValues(dest);

            int[] vs = new int[shares.length];

            for (int i = 0; i < shares.length; i++) {
                int s = s1s[i] ^ s2s[i];
                int m = received[i];

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


    private int[] shiftL(int[] shares) {
        int[] results = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            results[i] = mod(shares[i] * 2);
        }
        return results;
    }


    private int[] shiftR(int[] shares, Player player) throws InvalidSecretValue {

        int[] toReshare = new int[shares.length];
        System.arraycopy(shares, 0, toReshare, 0, shares.length);
        int[] reshared = reshareToTwo(toReshare, player);

        int[] rshift = shiftL(reshared);

        int[] deltaOnes = overflow(reshared, player);
        int[] deltaTwos = overflow(rshift, player);

        int[] results = new int[shares.length];

        for (int i = 0; i < shares.length; i++) {
            int v = reshared[i] >> 29;
            results[i] = mod((v - 2 * deltaOnes[i]) + deltaTwos[i]);
        }

        return results;
    }

    public int[] greaterOrEqualThan(int[] v1, int[] v2, Player player) throws InvalidSecretValue {

        int[] diffs = new int[v1.length];

        for (int i = 0; i < v1.length; i++) {
            diffs[i] = mod(v1[i] - v2[0]);
        }
        return reshare(shiftR(diffs, player), player);

    }
}
