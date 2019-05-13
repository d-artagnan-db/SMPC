package pt.uminho.haslab.smpc.sharmind.spdz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;
import pt.uminho.haslab.smpc.spdzImpl.SpdzDealer;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class SpdzBatchDealerTest {

    protected final int[] values;

    public SpdzBatchDealerTest(int nbits, List<BigInteger> value){
        values = new int[value.size()];
        for(int i = 0; i < value.size(); i++){
            values[i] = value.get(i).intValue();
        }
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        RandomGenerator.initBatch(6, 100);
        return ValuesGenerator.SingleBatchValueGenerator();
    }

    @Test
    public void shareSecret(){
        SpdzDealer.initializeThreadPool(3);
        SpdzDealer dealer = new SpdzDealer(3,3);

        dealer.share(values);
        List<Integer> res = dealer.unshare(dealer.share(values));

        for(int i = 0; i < values.length; i++){
            assertEquals(values[i], res.get(i).intValue());
        }
    }
}
