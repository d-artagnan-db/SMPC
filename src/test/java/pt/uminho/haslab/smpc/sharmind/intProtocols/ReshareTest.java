package pt.uminho.haslab.smpc.sharmind.intProtocols;

import pt.uminho.haslab.smpc.interfaces.Player;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindDealer;
import pt.uminho.haslab.smpc.sharemindImp.Integer.IntSharemindSecretFunctions;
import pt.uminho.haslab.smpc.sharmind.helpers.BatchDbTest;


import static junit.framework.TestCase.assertEquals;

public class ReshareTest extends SingleBatchValueProtocolTest {

    public ReshareTest(int[] values) {
        super(values);
    }

    public int[] runProtocol(int[] shares, Player player) {
        IntSharemindSecretFunctions issf = new IntSharemindSecretFunctions();
        return issf.reshare(shares, player);
    }

    public void condition(BatchDbTest db1, BatchDbTest db2, BatchDbTest db3) {

        int[] db1Results = ((Db) db1).getProtocolResults();
        int[] db2Results = ((Db) db2).getProtocolResults();
        int[] db3Results = ((Db) db3).getProtocolResults();


        assertEquals(db1Results.length, db2Results.length);
        assertEquals(db2Results.length, db3Results.length);

        IntSharemindDealer dealer = new IntSharemindDealer();

        for (int i = 0; i < db1Results.length; i++) {
            int[] vals = new int[3];
            vals[0] = db1Results[i];
            vals[1] = db2Results[i];
            vals[2] = db3Results[i];
            int res  = dealer.unshare(vals);

            // System.out.println("Secret result "+ secret.unshare());
            // System.out.println("Original val "+ this.values.get(i));
            assertEquals(res, this.values[i]);
        }
    }
    
    public static void main(String[] args){
    }
}
