package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.SInt;

import java.util.List;


public class FPEvaluator<ResourcePoolT extends ResourcePool> {

    private final FrescoContext<ResourcePoolT, ProtocolBuilderNumeric> context;
    private final Network network;



    public FPEvaluator(int playerID, int nPlayers, int maxBatchSize, Network network) {
        context = new FrescoContext<ResourcePoolT, ProtocolBuilderNumeric>(playerID, nPlayers, maxBatchSize);
        this.network = network;

    }


    public FrescoContext<ResourcePoolT, ProtocolBuilderNumeric> getContext(){
        return this.context;
    }
    public List<SInt> run(Application<List<SInt>, ProtocolBuilderNumeric> app) {
        return context.getSce().runApplication(app, context.getResourcePool(), network);

    }
}
