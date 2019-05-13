package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;

import java.io.IOException;

public abstract class FrescoExecutor<ResourcePoolT extends ResourcePool> implements Runnable{

    private final int playerID;
    private final int nPlayers;
    private final int maxBatchSize;
    private Object result;
    protected NetworkConfiguration config;

    private  Application<Object, ProtocolBuilderNumeric> app;


    public FrescoExecutor(int playerID, int nPlayers, int maxBatchSize) {
        this.playerID = playerID;
        this.nPlayers = nPlayers;
        this.maxBatchSize = maxBatchSize;
    }

    public void setApp(Application<Object, ProtocolBuilderNumeric> app){
        this.app = app;
    }
    public abstract Network getNetwork();


    public Object getResult(){
        return result;
    }

    public void run() {
        FrescoContext<ResourcePoolT, ProtocolBuilderNumeric> context = new FrescoContext<ResourcePoolT, ProtocolBuilderNumeric>(this.playerID, this.nPlayers, this.maxBatchSize);
        Network network = getNetwork();
        result = context.getSce().runApplication(app, context.getResourcePool(), network);
        try {
            context.closeContext();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        //System.out.println("PlayerID has result " +  result.toString());
    }
}