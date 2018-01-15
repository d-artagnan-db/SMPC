package pt.uminho.haslab.smpc.sharemindImp.BigInteger;

import pt.uminho.haslab.smpc.interfaces.Secret;

public interface BitVectorSecret extends Secret {

    public Secret bitConj();

    public BitVectorSecret prefixOr();

}
