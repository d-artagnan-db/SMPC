package pt.uminho.haslab.smpc.spdzImpl.gates;

import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePool;
import dk.alexandra.fresco.suite.spdz.gates.SpdzNativeProtocol;

public class ShareGate extends SpdzNativeProtocol<SInt> {

    private SInt out;

    public ShareGate(SInt out) {
        this.out = out;
    }
    public EvaluationStatus evaluate(int round, SpdzResourcePool resourcePool, Network network) {
        return EvaluationStatus.IS_DONE;
    }

    public SInt out() {
        return out;
    }
}
