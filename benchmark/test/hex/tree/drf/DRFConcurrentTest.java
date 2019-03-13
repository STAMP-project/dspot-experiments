package hex.tree.drf;


import org.junit.Test;
import water.fvec.Frame;

import static water.TestUtil.<init>;


public class DRFConcurrentTest extends TestUtil {
    @Test
    public void testBuildSingle() {
        Scope.enter();
        try {
            Frame fr = parse_test_file(Key.make("prostate_single.hex"), "smalldata/logreg/prostate.csv");
            fr.remove("ID").remove();
            Scope.track(fr);
            DKV.put(fr);
            DRFConcurrentTest.buildXValDRF(fr, "AGE");
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testBuildConcurrent() {
        Scope.enter();
        try {
            Frame fr = parse_test_file(Key.make("prostate_concurrent.hex"), "smalldata/logreg/prostate.csv");
            Scope.track(fr);
            fr.remove("ID").remove();
            DKV.put(fr);
            DRFConcurrentTest.TrainSingleFun fun = new DRFConcurrentTest.TrainSingleFun(fr);
            H2O.submitTask(new LocalMR(fun, 100)).join();
        } finally {
            Scope.exit();
        }
    }

    private static class TrainSingleFun extends MrFun<DRFConcurrentTest.TrainSingleFun> {
        private final Frame _train;

        private TrainSingleFun(Frame train) {
            _train = train;
        }

        public TrainSingleFun() {
            _train = null;
        }

        @Override
        protected void map(int id) {
            assert (_train) != null;
            DRFConcurrentTest.buildXValDRF(_train, "AGE");
        }
    }
}

