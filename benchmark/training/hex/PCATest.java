package hex;


import Job.JobState;
import hex.pca.PCAModel;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.FVecTest;
import water.fvec.Frame;
import water.fvec.ParseDataset2;


public class PCATest extends TestUtil {
    public final double threshold = 1.0E-6;

    @Test
    public void testBasic() throws InterruptedException, ExecutionException {
        boolean standardize = true;
        PCAModel model = null;
        Frame fr = null;
        try {
            Key kraw = Key.make("basicdata.raw");
            FVecTest.makeByteVec(kraw, "x1,x2,x3\n0,1.0,-120.4\n1,0.5,89.3\n2,0.3333333,291.0\n3,0.25,-2.5\n4,0.20,-2.5\n5,0.1666667,-123.4\n6,0.1428571,-0.1\n7,0.1250000,18.3");
            fr = ParseDataset2.parse(Key.make("basicdata.hex"), new Key[]{ kraw });
            Key kpca = Key.make("basicdata.pca");
            invoke();
            model = DKV.get(kpca).get();
            Job.JobState jstate = model.get_params().state;
            Assert.assertTrue((jstate == (JobState.DONE)));// HEX-1817

            testHTML(model);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testLinDep() throws InterruptedException, ExecutionException {
        Key kdata = Key.make("depdata.hex");
        PCAModel model = null;
        Frame fr = null;
        double[] sdev_R = new double[]{ 1.414214, 0 };
        try {
            Key kraw = Key.make("depdata.raw");
            FVecTest.makeByteVec(kraw, "x1,x2\n0,0\n1,2\n2,4\n3,6\n4,8\n5,10");
            fr = ParseDataset2.parse(kdata, new Key[]{ kraw });
            Key kpca = Key.make("depdata.pca");
            invoke();
            model = DKV.get(kpca).get();
            testHTML(model);
            for (int i = 0; i < (model.sdev().length); i++)
                Assert.assertEquals(sdev_R[i], model.sdev()[i], threshold);

        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testArrests() throws InterruptedException, ExecutionException {
        double tol = 0.25;
        boolean standardize = true;
        PCAModel model = null;
        Frame fr = null;
        double[] sdev_R = new double[]{ 1.5748783, 0.9948694, 0.5971291, 0.4164494 };
        double[][] eigv_R = new double[][]{ new double[]{ -0.5358995, 0.4181809, -0.3412327, 0.6492278 }, new double[]{ -0.5831836, 0.1879856, -0.2681484, -0.74340748 }, new double[]{ -0.2781909, -0.8728062, -0.3780158, 0.13387773 }, new double[]{ -0.5434321, -0.1673186, 0.8177779, 0.08902432 } };
        try {
            Key ksrc = Key.make("arrests.hex");
            fr = PCATest.getFrameForFile(ksrc, "smalldata/pca_test/USArrests.csv", null);
            // Build PCA model on all columns
            Key kdst = Key.make("arrests.pca");
            invoke();
            model = DKV.get(kdst).get();
            testHTML(model);
            // Compare standard deviation and eigenvectors to R results
            checkSdev(sdev_R, model.sdev());
            checkEigvec(eigv_R, model.eigVec());
            // Score original data set using PCA model
            // Key kscore = Key.make("arrests.score");
            // Frame score = PCAScoreTask.score(df, model._eigVec, kscore);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }
}

