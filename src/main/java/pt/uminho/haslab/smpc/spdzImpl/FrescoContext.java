package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.Party;
import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.builder.ProtocolBuilder;
import dk.alexandra.fresco.framework.builder.numeric.field.BigIntegerFieldDefinition;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.SecureComputationEngineImpl;
import dk.alexandra.fresco.framework.sce.evaluator.BatchEvaluationStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.BatchedProtocolEvaluator;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.util.AesCtrDrbg;
import dk.alexandra.fresco.framework.util.ModulusFinder;
import dk.alexandra.fresco.suite.ProtocolSuite;
import dk.alexandra.fresco.suite.spdz.SpdzProtocolSuite;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePoolImpl;
import dk.alexandra.fresco.suite.spdz.storage.SpdzDataSupplier;
import dk.alexandra.fresco.suite.spdz.storage.SpdzDummyDataSupplier;
import dk.alexandra.fresco.suite.spdz.storage.SpdzOpenedValueStoreImpl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class FrescoContext <ResourcePoolT extends ResourcePool, BuilderT extends ProtocolBuilder>  {

    private final ProtocolSuite<ResourcePoolT, BuilderT> protocol;
    private final ResourcePoolT resourcePool;
    private final ProtocolEvaluator<ResourcePoolT> evaluator;
    private final SecureComputationEngine<ResourcePoolT, BuilderT> sce;
    private final int playerID;
    //private final NetworkConfiguration config;

    public FrescoContext(int playerId, int nplayers, int maxBatchSize){

        this.playerID = playerId;
        protocol =  (ProtocolSuite<ResourcePoolT, BuilderT>) new SpdzProtocolSuite(64);
        BigInteger modulus = ModulusFinder.findSuitableModulus(512);
        SpdzDataSupplier supplier = new SpdzDummyDataSupplier(playerId, nplayers,
                new BigIntegerFieldDefinition(modulus), modulus);
        resourcePool = (ResourcePoolT) new SpdzResourcePoolImpl(playerId, nplayers, new SpdzOpenedValueStoreImpl(), supplier,
                new AesCtrDrbg(new byte[32]));

        EvaluationStrategy evalStrategy = EvaluationStrategy.SEQUENTIAL_BATCHED;
        BatchEvaluationStrategy<ResourcePoolT> stat = evalStrategy.getStrategy();
        evaluator = new BatchedProtocolEvaluator<ResourcePoolT>(stat, protocol, maxBatchSize);

        sce = new SecureComputationEngineImpl<ResourcePoolT, BuilderT>(protocol, evaluator);

    }

    public int getPlayerID() {
        return playerID;
    }

    public ProtocolSuite<ResourcePoolT, BuilderT> getProtocol() {
        return protocol;
    }

    public ResourcePoolT getResourcePool() {
        return resourcePool;
    }

    public ProtocolEvaluator<ResourcePoolT> getEvaluator() {
        return evaluator;
    }


    public SecureComputationEngine<ResourcePoolT, BuilderT> getSce() {
        return sce;
    }

    public void closeContext() throws IOException {
        sce.shutdownSCE();
    }
}
