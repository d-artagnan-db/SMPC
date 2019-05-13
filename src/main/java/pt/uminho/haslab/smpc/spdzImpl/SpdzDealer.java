package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationParallel;
import dk.alexandra.fresco.framework.builder.numeric.Numeric;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import pt.uminho.haslab.smpc.helpers.MemSpdzPlayer;
import pt.uminho.haslab.smpc.spdzImpl.gates.ShareGate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SpdzDealer {
    private static ExecutorService threadPool;


    public synchronized static void initializeThreadPool(int nthreads) {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(nthreads);
        }
    }

    private class BShareApplication implements Application<List<SInt>, ProtocolBuilderNumeric>{

        private int[] inputs;
        private int playerID;

        public BShareApplication(int playerID, int[] inputs) {
            this.inputs = inputs;
            this.playerID = playerID;
        }


        public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric builder) {
            return builder.par(new ComputationParallel<List<SInt>, ProtocolBuilderNumeric>() {
                public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric par) {

                    Numeric numeric = par.numeric();

                    final List<DRes<SInt>> result = new ArrayList<DRes<SInt>>(inputs.length);

                    for (int i = 0; i < inputs.length; i++) {
                        if (BShareApplication.this.inputs != null) {
                            result.add(numeric.input(BigInteger.valueOf(BShareApplication.this.inputs[i]), 1));
                        } else {
                            result.add(numeric.input(null, 1));
                        }
                    }
                    return new DRes<List<SInt>>() {
                        public List<SInt> out() {
                            List<SInt> fres = new ArrayList<SInt>();
                            for (int i = 0; i < result.size(); i++) {
                                fres.add(result.get(i).out());
                            }
                            return fres;
                        }
                    };
                }
            });
        }
    }

    private class ShareApplication implements Application<SInt, ProtocolBuilderNumeric> {

        private int input;
        private int playerID;

        public ShareApplication(int playerID, int input) {
            this.input = input;
            this.playerID = playerID;
        }

        public DRes<SInt> buildComputation(ProtocolBuilderNumeric producer) {
            Numeric numeric = producer.numeric();
            DRes<SInt> result;
            if(playerID == 1){
                result = numeric.input(BigInteger.valueOf(input), 1);
            }else{
                result = numeric.input(null, 1);
            }

            return result;
        }
    }

    private class BUnshareApplication implements Application<List<BigInteger>, ProtocolBuilderNumeric>{

        private List<SInt> inputs;

        public BUnshareApplication(List<SInt> inputs) {
            this.inputs = inputs;
        }


        public DRes<List<BigInteger>> buildComputation(ProtocolBuilderNumeric builder) {

            return builder.par(new ComputationParallel<List<BigInteger>, ProtocolBuilderNumeric>() {
                public DRes<List<BigInteger>> buildComputation(ProtocolBuilderNumeric par) {

                    Numeric numeric = par.numeric();

                    final List<DRes<BigInteger>> result = new ArrayList<DRes<BigInteger>>(inputs.size());

                    for (int i = 0; i < inputs.size(); i++) {
                        result.add(numeric.open(inputs.get(i)));
                    }
                    return new DRes<List<BigInteger>>() {
                        public List<BigInteger> out() {
                            List<BigInteger> fres = new ArrayList<BigInteger>();
                            for (int i = 0; i < result.size(); i++) {
                                fres.add(result.get(i).out());
                            }
                            return fres;
                        }
                    };
                }
            });
        }
    }


    private class UnshareApplication implements Application<BigInteger, ProtocolBuilderNumeric> {

        private SInt input;

        public UnshareApplication(SInt input) {
            this.input = input;
        }

        public DRes<BigInteger> buildComputation(ProtocolBuilderNumeric producer) {
            ShareGate gate = new ShareGate(input);
            return producer.numeric().open(gate);

        }
    }

    private final DealerExecutor[] players;
    private final int nPlayers;

    public SpdzDealer(int nplayers, int maxBatchSize){
        this.nPlayers = nplayers;
        MemSpdzPlayer[] memPlayers = new MemSpdzPlayer[nplayers];
        //System.out.println("Creating MemSpdzPlayers");
        for(int i = 0 ; i < nplayers; i++){
            memPlayers[i] = new MemSpdzPlayer();
        }
        //System.out.println("Creating dealer executor");

        players = new DealerExecutor[nplayers];

        for(int i = 0; i < nplayers; i++){
            players[i] = new DealerExecutor(i+1, nplayers, maxBatchSize, memPlayers);
        }


    }

    public List<List<SInt>> share(int[] values){

        List<Runnable> calls = new ArrayList<Runnable>();
        List<Future> futures = new ArrayList<Future>();
        List<List<SInt>> shares = new ArrayList<List<SInt>>(values.length);
        //System.out.println("Create share application");

        for(int i = 0; i < nPlayers; i++ ){
            BShareApplication app = new BShareApplication(i+1, values);
            players[i].setApp(app);
            calls.add(players[i]);
        }
        //System.out.println("Running share");
        for (Runnable t : calls) {
            futures.add(threadPool.submit(t));
        }
        //System.out.println("Waiting for result");

        try {

            for (Future t : futures) {
                t.get();
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }

        for(int i = 0; i < nPlayers; i++){
            shares.add((List<SInt>) players[i].getResult());
        }
        return shares;
    }

    public SInt[] share(int value){
        List<Runnable> calls = new ArrayList<Runnable>();
        List<Future> futures = new ArrayList<Future>();
        SInt[] shares = new SInt[nPlayers];
        //System.out.println("Create share application");

        for(int i = 0; i < nPlayers; i++ ){
            ShareApplication app = new ShareApplication(i+1, value);
            players[i].setApp(app);
            calls.add(players[i]);
        }
        //System.out.println("Running share");
        for (Runnable t : calls) {
            futures.add(threadPool.submit(t));
        }
        //System.out.println("Waiting for result");

        try {

            for (Future t : futures) {
                    t.get();
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }

        for(int i = 0; i < nPlayers; i++){
            shares[i] = (SInt) players[i].getResult();
        }
        return shares;
    }

    public int unshare(SInt[] shares){
        List<Runnable> calls = new ArrayList<Runnable>();
        List<Future> futures = new ArrayList<Future>();
        //System.out.println("Create share application");

        for(int i = 0; i < nPlayers; i++ ){
            UnshareApplication app = new UnshareApplication(shares[i]);
            players[i].setApp(app);
            calls.add(players[i]);
        }
        ///System.out.println("Running share");
        for (Runnable t : calls) {
            futures.add(threadPool.submit(t));
        }
        //System.out.println("Waiting for result");

        try {

            for (Future t : futures) {
                t.get();
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }

        return ((BigInteger) (players[0]).getResult()).intValue();
    }

    public List<Integer> unshare(List<List<SInt>> shares){
        List<Runnable> calls = new ArrayList<Runnable>();
        List<Future> futures = new ArrayList<Future>();
        //System.out.println("Create share application");

        for(int i = 0; i < nPlayers; i++ ){
            BUnshareApplication app = new BUnshareApplication(shares.get(i));
            players[i].setApp(app);
            calls.add(players[i]);
        }
        //System.out.println("Running share");
        for (Runnable t : calls) {
            futures.add(threadPool.submit(t));
        }
        //System.out.println("Waiting for result");

        try {

            for (Future t : futures) {
                t.get();
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
        List<Integer> results = new ArrayList<Integer>();

        for(int i = 0; i < shares.get(0).size(); i++){
            results.add(((List<BigInteger>) (players[0]).getResult()).get(i).intValue());
        }
        return results;
    }

}
