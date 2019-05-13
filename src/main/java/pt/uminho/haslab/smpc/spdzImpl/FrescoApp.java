package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.builder.ProtocolBuilder;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;


public interface FrescoApp<ResourcePoolT extends ResourcePool, BuilderT extends ProtocolBuilder> {

    public <ResourcePoolT extends ResourcePool> void runApplication(
            SecureComputationEngine<ResourcePoolT, BuilderT> sce,
            ResourcePoolT resourcePool, Network network);
}
