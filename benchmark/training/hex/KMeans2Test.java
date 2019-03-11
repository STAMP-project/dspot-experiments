package hex;


import Job.JobState;
import Sys.KMEAN;
import hex.KMeans2.Initialization;
import hex.KMeans2.KMeans2Model;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.FVecTest;
import water.fvec.Frame;
import water.fvec.ParseDataset2;
import water.util.Log;


public class KMeans2Test extends TestUtil {
    private static final long SEED = 8683452581122892189L;

    private static final double SIGMA = 3;

    @Test
    public void test1Dimension() {
        double[] data = new double[]{ 1.2, 5.6, 3.7, 0.6, 0.1, 2.6 };
        double[][] rows = new double[data.length][1];
        for (int i = 0; i < (rows.length); i++)
            rows[i][0] = data[i];

        Frame frame = TestUtil.frame(new String[]{ "C0" }, rows);
        KMeans2 algo;
        try {
            algo = new KMeans2();
            algo.source = frame;
            algo.k = 2;
            algo.initialization = Initialization.Furthest;
            algo.max_iter = 100;
            algo.seed = KMeans2Test.SEED;
            algo.invoke();
            KMeans2Model res = UKV.get(algo.dest());
            KMeans2Test.testHTML(res);
            Assert.assertTrue(((res.get_params().state) == (JobState.DONE)));// HEX-1817

            double[][] clusters = res.centers;
            Assert.assertEquals(1.125, clusters[0][0], 1.0E-6);
            Assert.assertEquals(4.65, clusters[1][0], 1.0E-6);
            res.delete();
        } finally {
            frame.delete();
        }
    }

    @Test
    public void testGaussian() {
        testGaussian(10000);
    }

    @Test
    public void testAirline() {
        Key dest = Key.make("dest");
        Frame frame = TestUtil.parseFrame(dest, "smalldata/airlines/allyears2k.zip");
        KMeans2 algo = new KMeans2();
        algo.source = frame;
        algo.k = 8;
        algo.initialization = Initialization.Furthest;
        algo.max_iter = 100;
        algo.seed = KMeans2Test.SEED;
        Timer t = new Timer();
        algo.invoke();
        Log.debug(KMEAN, ("ms= " + t));
        KMeans2Model res = UKV.get(algo.dest());
        KMeans2Test.testHTML(res);
        Assert.assertEquals(algo.k, res.centers.length);
        frame.delete();
        res.delete();
    }

    @Test
    public void testSphere() {
        Key dest = Key.make("dest");
        Frame frame = TestUtil.parseFrame(dest, "smalldata/syn_sphere2.csv");
        KMeans2 algo = new KMeans2();
        algo.source = frame;
        algo.k = 3;
        algo.initialization = Initialization.Furthest;
        algo.max_iter = 100;
        algo.seed = KMeans2Test.SEED;
        Timer t = new Timer();
        algo.invoke();
        Log.debug(KMEAN, ("ms= " + t));
        KMeans2Model res = UKV.get(algo.dest());
        KMeans2Test.testHTML(res);
        Assert.assertEquals(algo.k, res.centers.length);
        frame.delete();
        res.delete();
    }

    @Test
    public void testCentroids() {
        String data = "1, 0, 0\n" + ("0, 1, 0\n" + "0, 0, 1\n");
        Frame fr = null;
        try {
            Key k = FVecTest.makeByteVec("yada", data);
            fr = ParseDataset2.parse(Key.make(), new Key[]{ k });
            for (boolean normalize : new boolean[]{ false, true }) {
                for (Initialization init : new Initialization[]{ Initialization.None, Initialization.PlusPlus, Initialization.Furthest }) {
                    KMeans2 parms = new KMeans2();
                    parms.source = fr;
                    parms.k = 3;
                    parms.normalize = normalize;
                    parms.max_iter = 100;
                    parms.initialization = init;
                    parms.seed = 0;
                    parms.invoke();
                    KMeans2Model kmm = UKV.get(parms.dest());
                    Assert.assertTrue(((((kmm.centers[0][0]) + (kmm.centers[0][1])) + (kmm.centers[0][2])) == 1));
                    Assert.assertTrue(((((kmm.centers[1][0]) + (kmm.centers[1][1])) + (kmm.centers[1][2])) == 1));
                    Assert.assertTrue(((((kmm.centers[2][0]) + (kmm.centers[2][1])) + (kmm.centers[2][2])) == 1));
                    Assert.assertTrue(((((kmm.centers[0][0]) + (kmm.centers[1][0])) + (kmm.centers[2][0])) == 1));
                    Assert.assertTrue(((((kmm.centers[0][0]) + (kmm.centers[1][0])) + (kmm.centers[2][0])) == 1));
                    Assert.assertTrue(((((kmm.centers[0][0]) + (kmm.centers[1][0])) + (kmm.centers[2][0])) == 1));
                    KMeans2Test.testHTML(kmm);
                    kmm.delete();
                }
            }
        } finally {
            if (fr != null)
                fr.delete();

        }
    }

    @Test
    public void testNAColLast() {
        String[] datas = new String[]{ new String(("1, 0, ?\n"// 33% NA in col 3
         + ("0, 2, 0\n" + "0, 0, 3\n"))), new String(("1, ?, 0\n"// 33% NA in col 2
         + ("0, 2, 0\n" + "0, 0, 3\n"))), new String(("?, 0, 0\n"// 33% NA in col 1
         + ("0, 2, 0\n" + "0, 0, 3\n"))) };
        Frame fr = null;
        for (String data : datas) {
            try {
                Key k = FVecTest.makeByteVec("yada", data);
                fr = ParseDataset2.parse(Key.make(), new Key[]{ k });
                for (boolean drop_na : new boolean[]{ false, true }) {
                    for (boolean normalize : new boolean[]{ false, true }) {
                        for (Initialization init : new Initialization[]{ Initialization.None, Initialization.PlusPlus, Initialization.Furthest }) {
                            KMeans2 parms = new KMeans2();
                            parms.source = fr;
                            parms.k = 3;
                            parms.normalize = normalize;
                            parms.max_iter = 100;
                            parms.initialization = init;
                            parms.drop_na_cols = drop_na;
                            parms.seed = 0;
                            parms.invoke();
                            KMeans2Model kmm = UKV.get(parms.dest());
                            KMeans2Test.testHTML(kmm);
                            kmm.delete();
                        }
                    }
                }
            } finally {
                if (fr != null)
                    fr.delete();

            }
        }
    }
}

