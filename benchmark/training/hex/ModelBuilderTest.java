package hex;


import H2O.OptArgs;
import Model.Output;
import Model.Parameters;
import ModelMetrics.MetricBuilder;
import Vec.T_CAT;
import Vec.T_NUM;
import _parms._trgt;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;

import static H2O.NUMCPUS;
import static _parms._msg;


public class ModelBuilderTest extends TestUtil {
    @Test
    public void testRebalancePubDev5400() {
        try {
            Scope.enter();
            // create a frame where only the last chunk has data and the rest is empty
            final int nChunks = NUMCPUS;
            final int nRows = nChunks * 1000;
            double[] colA = new double[nRows];
            String[] resp = new String[nRows];
            for (int i = 0; i < (colA.length); i++) {
                colA[i] = i % 7;
                resp[i] = ((i % 3) == 0) ? "A" : "B";
            }
            long[] layout = new long[nChunks];
            layout[(nChunks - 1)] = colA.length;
            final Frame train = Scope.track(new TestFrameBuilder().withName("testFrame").withColNames("ColA", "Response").withVecTypes(T_NUM, T_CAT).withDataForCol(0, colA).withDataForCol(1, resp).withChunkLayout(layout).build());
            Assert.assertEquals(nChunks, train.anyVec().nChunks());
            Assert.assertEquals(colA.length, train.numRows());
            ModelBuilderTest.DummyModelParameters parms = new ModelBuilderTest.DummyModelParameters("Rebalance Test", Key.make("rebalance-test"));
            parms._train = train._key;
            ModelBuilder<?, ?, ?> mb = new ModelBuilderTest.DummyModelBuilder(parms);
            // the frame looks ideal (it has as many chunks as desired)
            Assert.assertEquals(nChunks, mb.desiredChunks(train, true));
            // expensive init - should include rebalance
            mb.init(true);
            // check that dataset was rebalanced
            long[] espc = mb.train().anyVec().espc();
            Assert.assertEquals((nChunks + 1), espc.length);
            Assert.assertEquals(nRows, espc[nChunks]);
            for (int i = 0; i < (espc.length); i++)
                Assert.assertEquals((i * 1000), espc[i]);

        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testRebalanceMulti() {
        Assume.assumeTrue(((H2O.getCloudSize()) > 1));
        try {
            Scope.enter();
            double[] colA = new double[1000000];
            String[] resp = new String[colA.length];
            for (int i = 0; i < (colA.length); i++) {
                colA[i] = i % 7;
                resp[i] = ((i % 3) == 0) ? "A" : "B";
            }
            final Frame train = Scope.track(// single chunk
            new TestFrameBuilder().withName("testFrame").withColNames("ColA", "Response").withVecTypes(T_NUM, T_CAT).withDataForCol(0, colA).withDataForCol(1, resp).withChunkLayout(colA.length).build());
            Assert.assertEquals(1, train.anyVec().nChunks());
            ModelBuilderTest.DummyModelParameters parms = new ModelBuilderTest.DummyModelParameters("Rebalance Test", Key.make("rebalance-test"));
            parms._train = train._key;
            ModelBuilder<?, ?, ?> mb = new ModelBuilderTest.DummyModelBuilder(parms) {
                @Override
                protected String getSysProperty(String name, String def) {
                    if (name.equals("rebalance.ratio.multi"))
                        return "0.5";

                    if (name.equals("rebalance.enableMulti"))
                        return "true";

                    if (name.startsWith(((OptArgs.SYSTEM_PROP_PREFIX) + "rebalance")))
                        throw new IllegalStateException(("Unexpected property: " + name));

                    return super.getSysProperty(name, def);
                }
            };
            // the rebalance logic should spread the Frame across the whole cluster (>> single node CPUs)
            final int desiredChunks = mb.desiredChunks(train, false);
            Assert.assertTrue((desiredChunks > (4 * (NUMCPUS))));
            // expensive init - should include rebalance
            mb.init(true);
            // check that dataset was rebalanced
            final int rebalancedChunks = mb.train().anyVec().nonEmptyChunks();
            Assert.assertEquals(desiredChunks, rebalancedChunks);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testMakeUnknownModel() {
        try {
            ModelBuilder.make("invalid", null, null);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("Algorithm 'invalid' is not registered. Available algos: []", e.getMessage());// core doesn't have any algos

        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bulkBuildModels() throws Exception {
        Job j = new Job(null, null, "BulkBuilding");
        Key key1 = Key.make(((j._key) + "-dummny-1"));
        Key key2 = Key.make(((j._key) + "-dummny-2"));
        try {
            j.start(new ModelBuilderTest.BulkRunner(j), 10).get();
            Assert.assertEquals("Computed Dummy 1", DKV.getGet(key1).toString());
            Assert.assertEquals("Computed Dummy 2", DKV.getGet(key2).toString());
        } finally {
            DKV.remove(key1);
            DKV.remove(key2);
        }
    }

    public static class BulkRunner extends H2O.H2OCountedCompleter<ModelBuilderTest.BulkRunner> {
        private water.Job _j;

        private BulkRunner(Job j) {
            _j = j;
        }

        @Override
        public void compute2() {
            ModelBuilder<?, ?, ?>[] builders = new ModelBuilder<?, ?, ?>[]{ new ModelBuilderTest.DummyModelBuilder(new ModelBuilderTest.DummyModelParameters("Dummy 1", Key.make(((_j._key) + "-dummny-1")))), new ModelBuilderTest.DummyModelBuilder(new ModelBuilderTest.DummyModelParameters("Dummy 2", Key.make(((_j._key) + "-dummny-2")))) };
            /* sequential */
            /* increment by 1 */
            ModelBuilder.bulkBuildModels("dummy-group", _j, builders, 1, 1);
            // check that progress is as expected
            Assert.assertEquals(0.2, _j.progress(), 0.001);
            tryComplete();
        }
    }

    public static class DummyModelOutput extends Model.Output {}

    public static class DummyModelParameters extends Model.Parameters {
        private String _msg;

        private water.Key _trgt;

        public DummyModelParameters(String msg, Key trgt) {
            _msg = msg;
            _trgt = trgt;
        }

        @Override
        public String fullName() {
            return "dummy";
        }

        @Override
        public String algoName() {
            return "dummy";
        }

        @Override
        public String javaName() {
            return ModelBuilderTest.DummyModelBuilder.class.getName();
        }

        @Override
        public long progressUnits() {
            return 1;
        }
    }

    public static class DummyModel extends Model<ModelBuilderTest.DummyModel, ModelBuilderTest.DummyModelParameters, ModelBuilderTest.DummyModelOutput> {
        public DummyModel(Key<ModelBuilderTest.DummyModel> selfKey, ModelBuilderTest.DummyModelParameters parms, ModelBuilderTest.DummyModelOutput output) {
            super(selfKey, parms, output);
        }

        @Override
        public MetricBuilder makeMetricBuilder(String[] domain) {
            return null;
        }

        @Override
        protected double[] score0(double[] data, double[] preds) {
            return preds;
        }
    }

    public static class DummyModelBuilder extends ModelBuilder<ModelBuilderTest.DummyModel, ModelBuilderTest.DummyModelParameters, ModelBuilderTest.DummyModelOutput> {
        public DummyModelBuilder(ModelBuilderTest.DummyModelParameters parms) {
            super(parms);
            init(false);
        }

        @Override
        protected Driver trainModelImpl() {
            return new Driver() {
                @Override
                public void computeImpl() {
                    DKV.put(_trgt, new water.parser.BufferedString(("Computed " + (_msg))));
                }
            };
        }

        @Override
        public ModelCategory[] can_build() {
            return new ModelCategory[0];
        }

        @Override
        public boolean isSupervised() {
            return false;
        }
    }
}

