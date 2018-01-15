package pt.uminho.haslab.smpc.sharmind.intProtocols;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer.mod;


@RunWith(Parameterized.class)
public class ShareUnshareTest {

    private final int value;

    public ShareUnshareTest(int value){
        this.value = mod(Math.abs(value));
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        return ValuesGenerator.SingleIntValueGenerator(1000);
    }



    @Test
    public void shareSecret() throws InvalidSecretValue {

        IntSharemindDealer dealer = new IntSharemindDealer();

        int[] secrets = dealer.share(value);
        int decodedValue = dealer.unshare(secrets);

        assertEquals(value, decodedValue);


    }
}
