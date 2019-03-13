package hex.drf;


import hex.drf.DRF.DRFModel;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import water.TestUtil;
import water.api.AUC;
import water.fvec.Frame;
import water.fvec.RebalanceDataSet;
import water.util.Log;


public class DRFTest extends TestUtil {
    abstract static class PrepData {
        abstract int prep(Frame fr);
    }

    @Test
    public void testClassIris1() throws Throwable {
        // iris ntree=1
        // the DRF should  use only subset of rows since it is using oob validation
        basicDRFTestOOBE("./smalldata/iris/iris.csv", "iris.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 1, DRFTest.a(DRFTest.a(25, 0, 0), DRFTest.a(0, 17, 1), DRFTest.a(1, 2, 15)), DRFTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"));
    }

    @Test
    public void testClassIris5() throws Throwable {
        // iris ntree=50
        basicDRFTestOOBE("./smalldata/iris/iris.csv", "iris.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                return (fr.numCols()) - 1;
            }
        }, 5, DRFTest.a(DRFTest.a(41, 0, 0), DRFTest.a(0, 39, 3), DRFTest.a(0, 4, 41)), DRFTest.s("Iris-setosa", "Iris-versicolor", "Iris-virginica"));
    }

    @Test
    public void testClassCars1() throws Throwable {
        // cars ntree=1
        basicDRFTestOOBE("./smalldata/cars.csv", "cars.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                UKV.remove(fr.remove("name")._key);
                return fr.find("cylinders");
            }
        }, 1, DRFTest.a(DRFTest.a(0, 0, 0, 0, 0), DRFTest.a(0, 62, 0, 7, 0), DRFTest.a(0, 1, 0, 0, 0), DRFTest.a(0, 0, 0, 31, 0), DRFTest.a(0, 0, 0, 0, 40)), DRFTest.s("3", "4", "5", "6", "8"));
    }

    @Test
    public void testClassCars5() throws Throwable {
        basicDRFTestOOBE("./smalldata/cars.csv", "cars.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                UKV.remove(fr.remove("name")._key);
                return fr.find("cylinders");
            }
        }, 5, DRFTest.a(DRFTest.a(3, 0, 0, 0, 0), DRFTest.a(0, 173, 2, 9, 0), DRFTest.a(0, 1, 1, 0, 0), DRFTest.a(0, 2, 2, 68, 2), DRFTest.a(0, 0, 0, 2, 88)), DRFTest.s("3", "4", "5", "6", "8"));
    }

    @Test
    public void testConstantCols() throws Throwable {
        try {
            basicDRFTestOOBE("./smalldata/poker/poker100", "poker.hex", new DRFTest.PrepData() {
                @Override
                int prep(Frame fr) {
                    for (int i = 0; i < 7; i++)
                        UKV.remove(fr.remove(3)._key);

                    return 3;
                }
            }, 1, null, null);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            /* pass */
        }
    }

    @Test
    public void testCreditProstate1() throws Throwable {
        basicDRFTestOOBE("./smalldata/logreg/prostate.csv", "prostate.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                UKV.remove(fr.remove("ID")._key);
                return fr.find("CAPSULE");
            }
        }, 1, DRFTest.a(DRFTest.a(62, 19), DRFTest.a(31, 22)), DRFTest.s("0", "1"));
    }

    @Test
    public void testAirlines() throws Throwable {
        basicDRFTestOOBE("./smalldata/airlines/allyears2k_headers.zip", "airlines.hex", new DRFTest.PrepData() {
            @Override
            int prep(Frame fr) {
                UKV.remove(fr.remove("DepTime")._key);
                UKV.remove(fr.remove("ArrTime")._key);
                UKV.remove(fr.remove("ActualElapsedTime")._key);
                UKV.remove(fr.remove("AirTime")._key);
                UKV.remove(fr.remove("ArrDelay")._key);
                UKV.remove(fr.remove("DepDelay")._key);
                UKV.remove(fr.remove("Cancelled")._key);
                UKV.remove(fr.remove("CancellationCode")._key);
                UKV.remove(fr.remove("CarrierDelay")._key);
                UKV.remove(fr.remove("WeatherDelay")._key);
                UKV.remove(fr.remove("NASDelay")._key);
                UKV.remove(fr.remove("SecurityDelay")._key);
                UKV.remove(fr.remove("LateAircraftDelay")._key);
                UKV.remove(fr.remove("IsArrDelayed")._key);
                return fr.find("IsDepDelayed");
            }
        }, 50, DRFTest.a(DRFTest.a(13941, 6946), DRFTest.a(5885, 17206)), DRFTest.s("NO", "YES"));
    }

    @Test
    public void testReproducibility() {
        Frame tfr = null;
        final int N = 5;
        double[] mses = new double[N];
        Scope.enter();
        try {
            // Load data, hack frames
            tfr = TestUtil.parseFrame(Key.make("air.hex"), "./smalldata/covtype/covtype.20k.data");
            // rebalance to 256 chunks
            Key dest = Key.make("df.rebalanced.hex");
            RebalanceDataSet rb = new RebalanceDataSet(tfr, dest, 256);
            H2O.submitTask(rb);
            rb.join();
            tfr.delete();
            tfr = DKV.get(dest).get();
            for (int i = 0; i < N; ++i) {
                DRF parms = new DRF();
                parms.source = tfr;
                parms.response = tfr.lastVec();
                parms.nbins = 1000;
                parms.ntrees = 1;
                parms.max_depth = 8;
                parms.mtries = -1;
                parms.min_rows = 10;
                parms.classification = false;
                parms.seed = 1234;
                // Build a first model; all remaining models should be equal
                DRFModel drf = parms.fork().get();
                mses[i] = drf.mse();
                drf.delete();
            }
        } finally {
            if (tfr != null)
                tfr.delete();

        }
        Scope.exit();
        for (int i = 0; i < (mses.length); ++i) {
            Log.info(((("trial: " + i) + " -> mse: ") + (mses[i])));
        }
        for (int i = 0; i < (mses.length); ++i) {
            Assert.assertEquals(mses[i], mses[0], 1.0E-15);
        }
    }

    public static class repro {
        @Ignore
        @Test
        public void testAirline() throws InterruptedException {
            Frame tfr = null;
            Frame test = null;
            Scope.enter();
            try {
                // Load data, hack frames
                tfr = TestUtil.parseFrame(Key.make("air.hex"), "/users/arno/sz_bench_data/train-1m.csv");
                test = TestUtil.parseFrame(Key.make("airt.hex"), "/users/arno/sz_bench_data/test.csv");
                for (int i : new int[]{ 0, 1, 2 }) {
                    tfr.vecs()[i] = tfr.vecs()[i].toEnum();
                    test.vecs()[i] = test.vecs()[i].toEnum();
                }
                DRF parms = new DRF();
                parms.source = tfr;
                parms.validation = test;
                // parms.ignored_cols_by_name = new int[]{4,5,6};
                // parms.ignored_cols_by_name = new int[]{0,1,2,3,4,5,7};
                parms.response = tfr.lastVec();
                parms.nbins = 20;
                parms.ntrees = 100;
                parms.max_depth = 20;
                parms.mtries = -1;
                parms.sample_rate = 0.667F;
                parms.min_rows = 10;
                parms.classification = true;
                parms.seed = 12;
                DRFModel drf = parms.fork().get();
                Frame pred = drf.score(test);
                AUC auc = new AUC();
                auc.vactual = test.lastVec();
                auc.vpredict = pred.lastVec();
                auc.invoke();
                Log.info(("Test set AUC: " + (auc.data().AUC)));
                drf.delete();
            } finally {
                if (tfr != null)
                    tfr.delete();

                if (test != null)
                    test.delete();

            }
            Scope.exit();
        }
    }
}

