package pt.uminho.haslab.smpc.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class RandomGenerator {
    public static SecureRandom generator = new SecureRandom();
    private static BigInteger[] cache;
    private static BigInteger[] singleBit;
    public static boolean activeCache;
    public static int cacheNBits;

    private static int nValuesCacheIndex;
    private static int singleBitIndex;
    private static int totalValuesCache;


    private static int[] intCache;
    private static int intCacheIndex;

    private static long[] longCache;
    private static int longCacheIndex;

    public static int intMod(int value) {
        int ring = (int) Math.pow(2, 29);
        return ((value % ring) + ring) % ring;

    }



    public static int nextInt(){
        if (intCache != null) {
            return intCache[Math.abs(intCacheIndex++ % intCache.length)];
        } else {
            return intMod(generator.nextInt());
        }
    }

    public static void initIntBatch(int nValuesCache){
        intCache = new int[nValuesCache];

        for(int i = 0;  i < nValuesCache; i++){
            intCache[i] = intMod(generator.nextInt());
        }
        intCacheIndex = 0;
    }

    public static long longMod(long value) {
        long ring = (long) Math.pow(2, 61);
        return ((value % ring) + ring) % ring;

    }

    public static long nextLong(){
        if (longCache != null) {
            return longCache[Math.abs(longCacheIndex++ % longCache.length)];
        } else {
            return longMod(generator.nextLong());
        }
    }



    public static void initLongBatch(int nValuesCache){
        longCache = new long[nValuesCache];

        for(int i = 0;  i < nValuesCache; i++){
            longCache[i] = longMod(generator.nextLong());
        }
        longCacheIndex = 0;
    }

    public static void initBatch(int nBits, int nValuesCache){
        cache = new BigInteger[nValuesCache];
        singleBit = new BigInteger[nValuesCache];
        totalValuesCache = nValuesCache;
        

        for(int i =0; i < nValuesCache; i++){
            cache[i] = new BigInteger(nBits, generator);
        }
      
        
        for(int i = 0 ; i < nValuesCache; i++){
            singleBit[i] = new BigInteger(1, generator);
        }
        
        cacheNBits = nBits;
        activeCache = true;
    }


    public static synchronized BigInteger getRandom(){

        return cache[nValuesCacheIndex++ % totalValuesCache];
    }
    
    public static synchronized BigInteger geRandomSingleBit(){
        return  singleBit[singleBitIndex++ % totalValuesCache];
    }


}
