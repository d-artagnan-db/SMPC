package pt.uminho.haslab.smpc.spdzImpl.gates;

import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePool;
import dk.alexandra.fresco.suite.spdz.gates.SpdzNativeProtocol;

import java.util.List;

public class ListGate extends SpdzNativeProtocol<List<SInt>> {
    private List<SInt> out;

    public ListGate(List<SInt> out) {
        this.out = out;
    }

    public EvaluationStatus evaluate(int round, SpdzResourcePool resourcePool, Network network) {
         return EvaluationStatus.IS_DONE;
    }

    public List<SInt> out() {
        return out;
    }
}
