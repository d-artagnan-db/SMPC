package pt.uminho.haslab.smpc.sharemindImp.Long;

import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;

public class LongSharemindDealer {

    public static long mod(long value){

        long ring = (long) Math.pow(2, 62);
        return ((value % ring) + ring) % ring;
    }

    public long[] share(long secret) throws InvalidSecretValue {

        if(!(secret >= 0 && secret < Math.pow(2, 63))){
            throw new InvalidSecretValue("Arguments must greaterThan 0 and lesser than 63 bits");
        }

        long[] res = new long[3];
        res[0] = mod(RandomGenerator.nextLong());
        res[1] = mod(RandomGenerator.nextLong());
        res[2] = mod((secret - res[0] - res[1]));

        return res;
    }

    public long[] shareBit(long secret) throws InvalidSecretValue {

        if(!(secret >= 0 && secret < Math.pow(2, 63))){
            throw new InvalidSecretValue("Arguments must greaterThan 0 and lesser than 63 bits");
        }

        long[] res = new long[3];
        res[0] = RandomGenerator.nextLong() % 2;
        res[1] = RandomGenerator.nextLong() % 2;
        res[2] =  Math.abs(secret - res[0] - res[1])%2;

        return res;
    }

    public long[] shareXor(long secret) throws InvalidSecretValue{
        if(!(secret >= 0 && secret < Math.pow(2, 63))){
            throw new InvalidSecretValue("Arguments must greaterThan 0 and lesser than 63 bits");
        }

        long[] res = new long[3];
        res[0] = mod(RandomGenerator.nextLong());
        res[1] = mod(RandomGenerator.nextLong());
        res[2] = mod((secret ^ res[0] ^ res[1]));

        return res;
    }

    public long unshareXor(long[] values){
        return values[0]  ^ values[1] ^ values[2];
    }
    public long unshare(long[] values){
        return mod(values[0] + values[1] + values[2]);
    }

    public long unshareBit(long[] values){
        return (values[0] + values[1] + values[2])%2;
    }
}
