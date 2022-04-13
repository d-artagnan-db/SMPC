package pt.uminho.haslab.smpc.sharmind.intProtocols;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class EqualityTest extends DoubleBatchProtocolTest {

    public EqualityTest(int[] firstValues, int[] secondValues)
    {
        super(31, firstValues, secondValues);
    }

    /* Overrides default */
    @Parameterized.Parameters
    public static Collection nbitsValues() {
        RandomGenerator.initIntBatch(100);
        return ValuesGenerator.IntBatchValuesGenerator(100, 100);
    }


    public int[] runProtocol(int[] firstShares, int[] secondShares, Player player) {
        IntSharemindSecretFunctions issf = new IntSharemindSecretFunctions();
        return issf.equal(firstShares, secondShares, player);
    }

    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {
        int[] db1Results = ((Db) db1).getProtocolResults();
        int[] db2Results = ((Db) db2).getProtocolResults();
        int[] db3Results = ((Db) db3).getProtocolResults();

        assertEquals(db1Results.length, db2Results.length);
        assertEquals(db2Results.length, db3Results.length);

        IntSharemindDealer dealer = new IntSharemindDealer();
        for(int i = 0; i < db1Results.length; i++){
            int[] shares =  new int[3];
            shares[0] = db1Results[i];
            shares[1] = db2Results[i];
            shares[2] = db3Results[i];

            int result = dealer.unshareBit(shares);

            boolean comparisonResult = firstValues[i] == secondValues[i];
            int expectedResult = comparisonResult ? 1 : 0;
            assertEquals(expectedResult, result);

        }
    }
}
