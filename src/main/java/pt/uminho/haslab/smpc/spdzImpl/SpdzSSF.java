package pt.uminho.haslab.smpc.spdzImpl;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationParallel;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import pt.uminho.haslab.smpc.spdzImpl.gates.ShareGate;

import java.util.ArrayList;
import java.util.List;

public class SpdzSSF {

    private static  DRes<List<SInt>> getListDRes(final List<DRes<SInt>> protoRes) {
        return new DRes<List<SInt>>() {
            public List<SInt> out() {
                List<SInt> fres = new ArrayList<SInt>(protoRes.size());
                for (SInt res : fres) {
                    protoRes.add(res.out());
                }
                return fres;
            }
        };
    }

    private static class EqualApplication implements Application<List<SInt>, ProtocolBuilderNumeric> {

        private List<SInt> s1;
        private List<SInt> s2;

        public EqualApplication(List<SInt> s1, List<SInt> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric producer) {

            return producer.par(new ComputationParallel<List<SInt>, ProtocolBuilderNumeric>() {
                public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric par) {

                    final List<DRes<SInt>> protoRes = new ArrayList<DRes<SInt>>(s1.size());

                    for (int i = 0; i < s1.size(); i++) {
                        protoRes.add(par.comparison().equals(new ShareGate(s1.get(i)), new ShareGate(s2.get(i))));
                    }

                    return getListDRes(protoRes);
                }
            });

        }
    }


    private static class LEQApplication implements Application<List<SInt>, ProtocolBuilderNumeric> {

        private List<SInt> s1;
        private List<SInt> s2;

        public LEQApplication(List<SInt> s1, List<SInt> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric producer) {

            return producer.par(new ComputationParallel<List<SInt>, ProtocolBuilderNumeric>() {
                public DRes<List<SInt>> buildComputation(ProtocolBuilderNumeric par) {

                    final List<DRes<SInt>> protoRes = new ArrayList<DRes<SInt>>(s1.size());

                    for (int i = 0; i < s1.size(); i++) {
                        protoRes.add(par.comparison().compareLEQ(new ShareGate(s1.get(i)), new ShareGate(s2.get(i))));
                    }


                    return getListDRes(protoRes);
                }
            });
        }

    }

    public static List<SInt> equals(List<SInt> s1, List<SInt> s2, FPEvaluator evaluator) {
        EqualApplication app = new EqualApplication(s1, s2);
        return evaluator.run(app);
    }


    public static List<SInt> lessThanOrEqualTo(List<SInt> s1, List<SInt> s2, FPEvaluator evaluator) {
        LEQApplication app = new LEQApplication(s1, s2);
        return evaluator.run(app);
    }
}
