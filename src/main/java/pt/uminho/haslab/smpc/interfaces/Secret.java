package pt.uminho.haslab.smpc.interfaces;

public interface Secret {

    public Secret equal(Secret v);

    public Secret greaterOrEqualThan(Secret v);
}
