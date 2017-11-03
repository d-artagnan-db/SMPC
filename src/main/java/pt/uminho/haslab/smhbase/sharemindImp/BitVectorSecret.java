package pt.uminho.haslab.smhbase.sharemindImp;

import pt.uminho.haslab.smhbase.interfaces.Secret;

public interface BitVectorSecret extends Secret {

	public Secret bitConj();

	public BitVectorSecret prefixOr();

}
