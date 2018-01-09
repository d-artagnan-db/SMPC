package pt.uminho.haslab.smpc.sharemindImp;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;


public class IntSharemindDealer {

    public static int mod(int value){
        int ring = (int) Math.pow(2, 31);
        return (value % ring);
    }

    public int[] share(int secret) throws InvalidSecretValue {

        if(!(secret >= 0 && secret < Math.pow(2, 31))){
            throw new InvalidSecretValue("Arguments must greaterThan 0 and lesser than 31 bits");
        }
        int[] res = new int[3];
        res[0] = mod(RandomGenerator.nextInt());
        res[1] = mod(RandomGenerator.nextInt());
        res[2] = mod((secret - res[0] - res[1]));

        return res;
    }

    public int[] shareBit(int secret) throws InvalidSecretValue {

        if(!(secret >= 0 && secret < Math.pow(2, 31))){
            throw new InvalidSecretValue("Arguments must greaterThan 0 and lesser than 31 bits");
        }
        int[] res = new int[3];
        res[0] = RandomGenerator.nextInt() %2;
        res[1] = RandomGenerator.nextInt() % 2;
        res[2] =  Math.abs((secret - res[0] - res[1]))%2;

        return res;
    }

    public int unshare(int[] values){
        return mod(values[0] + values[1] + values[2]);
    }

    public int unshareBit(int[] values){
        return (values[0] + values[1] + values[2])%2;
    }

}
