package hex;


import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;

import static CoxPHTies.breslow;
import static CoxPHTies.efron;


public class CoxPHTest extends TestUtil {
    @Test
    public void testCoxPHEfron1Var() throws InterruptedException, ExecutionException {
        Key parsed = Key.make("coxph_efron_test_data_parsed");
        Key modelKey = Key.make("coxph_efron_test");
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = CoxPHTest.getFrameForFile(parsed, "smalldata/heart.csv");
            CoxPH job = new CoxPH();
            job.destination_key = modelKey;
            job.source = fr;
            job.start_column = fr.vec("start");
            job.stop_column = fr.vec("stop");
            job.event_column = fr.vec("event");
            job.x_columns = new int[]{ fr.find("age") };
            job.ties = efron;
            job.fork();
            job.get();
            model = DKV.get(modelKey).get();
            CoxPHTest.testHTML(model);
            Assert.assertEquals(model.coef[0], 0.0307077486571334, 1.0E-8);
            Assert.assertEquals(model.var_coef[0][0], 2.03471477951459E-4, 1.0E-8);
            Assert.assertEquals(model.null_loglik, (-298.121355672984), 1.0E-8);
            Assert.assertEquals(model.loglik, (-295.536762216228), 1.0E-8);
            Assert.assertEquals(model.score_test, 4.64097294749287, 1.0E-8);
            assert (model.iter) >= 1;
            Assert.assertEquals(model.x_mean_num[0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model.n, 172);
            Assert.assertEquals(model.total_event, 75);
            Assert.assertEquals(model.wald_test, 4.6343882547245, 1.0E-8);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHBreslow1Var() throws InterruptedException, ExecutionException {
        Key parsed = Key.make("coxph_efron_test_data_parsed");
        Key modelKey = Key.make("coxph_efron_test");
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = CoxPHTest.getFrameForFile(parsed, "smalldata/heart.csv");
            CoxPH job = new CoxPH();
            job.destination_key = modelKey;
            job.source = fr;
            job.start_column = fr.vec("start");
            job.stop_column = fr.vec("stop");
            job.event_column = fr.vec("event");
            job.x_columns = new int[]{ fr.find("age") };
            job.ties = breslow;
            job.fork();
            job.get();
            model = DKV.get(modelKey).get();
            CoxPHTest.testHTML(model);
            Assert.assertEquals(model.coef[0], 0.0306910411003801, 1.0E-8);
            Assert.assertEquals(model.var_coef[0][0], 2.03592486905101E-4, 1.0E-8);
            Assert.assertEquals(model.null_loglik, (-298.325606736463), 1.0E-8);
            Assert.assertEquals(model.loglik, (-295.745227177782), 1.0E-8);
            Assert.assertEquals(model.score_test, 4.63317821557301, 1.0E-8);
            assert (model.iter) >= 1;
            Assert.assertEquals(model.x_mean_num[0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model.n, 172);
            Assert.assertEquals(model.total_event, 75);
            Assert.assertEquals(model.wald_test, 4.62659510743282, 1.0E-8);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHEfron1VarNoStart() throws InterruptedException, ExecutionException {
        Key parsed = Key.make("coxph_efron_test_data_parsed");
        Key modelKey = Key.make("coxph_efron_test");
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = CoxPHTest.getFrameForFile(parsed, "smalldata/heart.csv");
            CoxPH job = new CoxPH();
            job.destination_key = modelKey;
            job.source = fr;
            job.start_column = null;
            job.stop_column = fr.vec("stop");
            job.event_column = fr.vec("event");
            job.x_columns = new int[]{ fr.find("age") };
            job.ties = efron;
            job.fork();
            job.get();
            model = DKV.get(modelKey).get();
            CoxPHTest.testHTML(model);
            Assert.assertEquals(model.coef[0], 0.0289468187293998, 1.0E-8);
            Assert.assertEquals(model.var_coef[0][0], 2.10975113029285E-4, 1.0E-8);
            Assert.assertEquals(model.null_loglik, (-314.148170059513), 1.0E-8);
            Assert.assertEquals(model.loglik, (-311.946958322919), 1.0E-8);
            Assert.assertEquals(model.score_test, 3.97716015008595, 1.0E-8);
            assert (model.iter) >= 1;
            Assert.assertEquals(model.x_mean_num[0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model.n, 172);
            Assert.assertEquals(model.total_event, 75);
            Assert.assertEquals(model.wald_test, 3.97164529276219, 1.0E-8);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHBreslow1VarNoStart() throws InterruptedException, ExecutionException {
        Key parsed = Key.make("coxph_efron_test_data_parsed");
        Key modelKey = Key.make("coxph_efron_test");
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = CoxPHTest.getFrameForFile(parsed, "smalldata/heart.csv");
            CoxPH job = new CoxPH();
            job.destination_key = modelKey;
            job.source = fr;
            job.start_column = null;
            job.stop_column = fr.vec("stop");
            job.event_column = fr.vec("event");
            job.x_columns = new int[]{ fr.find("age") };
            job.ties = breslow;
            job.fork();
            job.get();
            model = DKV.get(modelKey).get();
            CoxPHTest.testHTML(model);
            Assert.assertEquals(model.coef[0], 0.0289484855901731, 1.0E-8);
            Assert.assertEquals(model.var_coef[0][0], 2.11028794751156E-4, 1.0E-8);
            Assert.assertEquals(model.null_loglik, (-314.2964933669), 1.0E-8);
            Assert.assertEquals(model.loglik, (-312.095342077591), 1.0E-8);
            Assert.assertEquals(model.score_test, 3.97665282498882, 1.0E-8);
            assert (model.iter) >= 1;
            Assert.assertEquals(model.x_mean_num[0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model.n, 172);
            Assert.assertEquals(model.total_event, 75);
            Assert.assertEquals(model.wald_test, 3.97109228128153, 1.0E-8);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }
}

