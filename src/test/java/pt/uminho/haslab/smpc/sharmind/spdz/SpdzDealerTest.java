package pt.uminho.haslab.smpc.sharmind.spdz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;
import pt.uminho.haslab.smpc.spdzImpl.SpdzDealer;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer.mod;

@RunWith(Parameterized.class)
public class SpdzDealerTest {

    private final int value;

    public SpdzDealerTest(int value){
        this.value = mod(Math.abs(value));
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.SingleIntValueGenerator(1000);
    }


    @Test
    public void shareSecret(){
        SpdzDealer.initializeThreadPool(3);
        SpdzDealer dealer = new SpdzDealer(3,3);
        int res = dealer.unshare(dealer.share(value));
        assertEquals(value, res);
        System.out.println("Result is "+res);

    }




}
