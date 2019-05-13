package pt.uminho.haslab.smpc.sharmind.spdz;

import dk.alexandra.fresco.framework.value.SInt;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.helpers.MemNetwork;
import pt.uminho.haslab.smpc.helpers.MemSpdzPlayer;
import pt.uminho.haslab.smpc.helpers.RandomGenerator;
import pt.uminho.haslab.smpc.sharmind.helpers.ValuesGenerator;
import pt.uminho.haslab.smpc.spdzImpl.FPEvaluator;
import pt.uminho.haslab.smpc.spdzImpl.SpdzDealer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DoubleSPDZProtocolTest extends SPDZProtocolTest {

    protected List<BigInteger> firstValues;
    protected List<BigInteger> secondValues;
    protected SpdzDealer dealer;

    public DoubleSPDZProtocolTest(int nbits, List<BigInteger> firstValues, List<BigInteger> secondValues) {
        //System.out.println("Creating Double protocol");
        this.firstValues = firstValues;
        this.secondValues = secondValues;
        SpdzDealer.initializeThreadPool(3);
        dealer = new SpdzDealer(3, 1);
    }

    @Parameterized.Parameters
    public static Collection nbitsValues() {
        //System.out.println("Going to generate values");
        RandomGenerator.initBatch(32, 100);
        return ValuesGenerator.TwoValuesBatchGenerator();
    }

    public abstract List<SInt> runProtocol(List<SInt> firstShares,
                                             List<SInt> secondShares, FPEvaluator evaluator);



    @Override
    public List<SpdzDB> prepareDatabases(MemSpdzPlayer[] players){
        System.out.println("Preparing Databases");
        List<SInt> v1Sharesp0 = new ArrayList<SInt>();
        List<SInt> v1Sharesp1 = new ArrayList<SInt>();
        List<SInt> v1Sharesp2 = new ArrayList<SInt>();

        List<SInt> v2Sharesp0 = new ArrayList<SInt>();
        List<SInt> v2Sharesp1 = new ArrayList<SInt>();
        List<SInt> v2Sharesp2 = new ArrayList<SInt>();

        List<SpdzDB> results = new ArrayList<SpdzDB>();

        for (int i = 0; i < firstValues.size(); i++) {
            BigInteger u = this.firstValues.get(i);
            BigInteger v = this.secondValues.get(i);

            SInt[] secretOne =  dealer.share(u.intValue());
            SInt[] secretTwo =  dealer.share(v.intValue());

            v1Sharesp0.add(secretOne[0]);
            v1Sharesp1.add(secretOne[1]);
            v1Sharesp2.add(secretOne[2]);

            v2Sharesp0.add(secretTwo[0]);
            v2Sharesp1.add(secretTwo[1]);
            v2Sharesp2.add(secretTwo[2]);
        }

        FPEvaluator eval0 = new FPEvaluator(1, 3, 200, new MemNetwork(1, players));
        FPEvaluator eval1 = new FPEvaluator(2, 3, 200, new MemNetwork(2, players));
        FPEvaluator eval2 = new FPEvaluator(3, 3, 200, new MemNetwork(3, players));


        SpdzDB rdb0 = new Db(v1Sharesp0, v2Sharesp0, eval0);
        SpdzDB rdb1 = new Db(v1Sharesp1, v2Sharesp1, eval1);
        SpdzDB rdb2 = new Db(v1Sharesp2, v2Sharesp2, eval2);

        results.add(rdb0);
        results.add(rdb1);
        results.add(rdb2);

        return results;

    }


    protected class Db extends SpdzDB {

        private final List<SInt> secondShares;
        private final FPEvaluator evaluator;


        public Db(List<SInt> firstShares, List<SInt> secondShares, FPEvaluator evaluator) {
            super(firstShares);
            this.secondShares = secondShares;
            this.evaluator = evaluator;

        }

        @Override
        public void run() {

            super.protocolResults = runProtocol(this.secrets,
                    this.secondShares, evaluator);
        }

    }
}
