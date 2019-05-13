package pt.uminho.haslab.smpc.sharmind.spdz;

import dk.alexandra.fresco.framework.value.SInt;

import java.util.List;

public abstract class SpdzDB extends Thread {

    protected final List<SInt> secrets;
    protected List<SInt> protocolResults;


    public SpdzDB(List<SInt> secrets) {
        this.secrets = secrets;
    }

    public List<SInt> getResult() {
        return this.protocolResults;
    }


}
