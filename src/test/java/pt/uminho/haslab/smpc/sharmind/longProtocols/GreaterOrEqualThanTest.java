package pt.uminho.haslab.smpc.sharmind.longProtocols;

import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.exceptions.InvalidSecretValue;
import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.sharemindImp.Long.LongSharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.Long.LongSharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class GreaterOrEqualThanTest extends DoubleBatchProtocolTest {

    public GreaterOrEqualThanTest(long[] firstValues, long[] secondValues) {
        super(63, firstValues, secondValues);
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        RandomGenerator.initLongBatch(100);
        return ValuesGenerator.LongBatchValuesGenerator(100,100);
    }

    public long[] runProtocol(long[] firstShares, long[] secondShares, Player player) {
        try {
            LongSharemindSecretFunctions ssf = new LongSharemindSecretFunctions();
            return ssf.greaterOrEqualThan(firstShares, secondShares, player);
        } catch (InvalidSecretValue ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {
        long[] db1Results = ((Db) db1).getProtocolResults();
        long[] db2Results = ((Db) db2).getProtocolResults();
        long[] db3Results = ((Db) db3).getProtocolResults();

        assertEquals(db1Results.length, db2Results.length);
        assertEquals(db2Results.length, db3Results.length);

        LongSharemindDealer dealer = new LongSharemindDealer();

        for(int i = 0; i < db1Results.length; i++){
            long[] shares =  new long[3];
            shares[0] = db1Results[i];
            shares[1] = db2Results[i];
            shares[2] = db3Results[i];

            long result = dealer.unshare(shares);


            boolean comparisonResult = firstValues[i] >= secondValues[i];
            long expectedResult = comparisonResult ? 0 : 1;
            assertEquals(expectedResult, result);

        }

    }
}
