package pt.uminho.haslab.smpc.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomGenerator {
    public static SecureRandom generator = new SecureRandom();
    private static BigInteger[] cache;
    private static BigInteger[] singleBit;
    public static boolean activeCache;
    public static int cacheNBits;

    private static int nValuesCacheIndex;
    private static int singleBitIndex;
    private static int totalValuesCache;

    private static int valuesCache;
    private static int[] intCache;
    private static int intCacheIndex;

    public static int nextInt(){
        return intCache[intCacheIndex++%valuesCache];
    }

    public static void initIntBatch(int nValuesCache){
        valuesCache  = nValuesCache;
        intCache = new int[nValuesCache];

         for(int i = 0;  i < nValuesCache; i++){
             intCache[i] = Math.abs(generator.nextInt());
         }
         intCacheIndex = 0;
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
