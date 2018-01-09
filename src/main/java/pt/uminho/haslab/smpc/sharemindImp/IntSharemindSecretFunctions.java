package pt.uminho.haslab.smpc.sharemindImp;

import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.interfaces.Player;

public class IntSharemindSecretFunctions {

    private static final int ring = (int) Math.pow(2, 32);

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
        return value % ring;
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
            results[i] = Math.abs(result) % 2;
        }
        return results;
    }

    public int[] mult(int[] s1, int[] s2, Player player) {

        int[] resharedS1 = reshare(s1, player);
        int[] resharedS2 = reshare(s2, player);

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

        // System.out.println(player.getPlayerID() + " reshared values are "+ reshared);

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

        int[] ps = new int[s1.length];
        if (player.getPlayerID() == 0) {

            int[] r1s = new int[s1.length];
            int[] r2s = new int[s2.length];

            for (int i = 0; i < s1.length; i++) {
                r1s[i] = getRandom();
                r2s[i] = mod((s1[i] - s2[i]) - r1s[i]);
            }

            player.sendValueToPlayer(1, r1s);
            player.sendValueToPlayer(2, r2s);

            for (int i = 0; i < s1.length; i++) {
                ps[i] = (int) (Math.pow(2, 31) - 1);
            }
        } else {

            int[] rs = player.getIntValues(0);
            int[] es = new int[s1.length];

            for (int i = 0; i < s1.length; i++) {
                es[i] = mod((s1[i] - s2[i]) + rs[i]);
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

    private int[] BitConj(int[] ps, Player player) {

        int[] ones = ones(ps.length);

        for (int i = 0; i < 31; i++) {
            int[] bitVals = new int[ps.length];

            for (int j = 0; j < ps.length; j++) {

                bitVals[j] = (ps[j] >> i) & 1;

            }

            ones = multBit(ones, bitVals, player);


        }

        return ones;
    }


}
