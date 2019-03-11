package hex.coxph;


import CoxPHModel.CoxPHParameters;
import CoxPHModel.CoxPHParameters.CoxPHTies;
import hex.StringPair;
import org.junit.Assert;
import org.junit.Test;
import water.Key;
import water.MRTask;
import water.Scope;
import water.TestUtil;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.NewChunk;
import water.fvec.Vec;


public class CoxPHTest extends TestUtil {
    @Test
    public void testCoxPHEfron1Var() {
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = parse_test_file("smalldata/coxph_test/heart.csv");
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = "start";
            parms._stop_column = "stop";
            parms._response_column = "event";
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant" };
            parms._ties = CoxPHTies.efron;
            Assert.assertEquals("Surv(start, stop, event) ~ age", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            model = builder.trainModel().get();
            Assert.assertEquals(model._output._coef[0], 0.0307077486571334, 1.0E-8);
            Assert.assertEquals(model._output._var_coef[0][0], 2.03471477951459E-4, 1.0E-8);
            Assert.assertEquals(model._output._null_loglik, (-298.121355672984), 1.0E-8);
            Assert.assertEquals(model._output._loglik, (-295.536762216228), 1.0E-8);
            Assert.assertEquals(model._output._score_test, 4.64097294749287, 1.0E-8);
            Assert.assertTrue(((model._output._iter) >= 1));
            Assert.assertEquals(model._output._x_mean_num[0][0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model._output._n, 172);
            Assert.assertEquals(model._output._total_event, 75);
            Assert.assertEquals(model._output._wald_test, 4.6343882547245, 1.0E-8);
            Assert.assertEquals(model._output._var_cumhaz_2_matrix.rows(), 110);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHEfron1VarScoring() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file("smalldata/coxph_test/heart.csv"));
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = "start";
            parms._stop_column = "stop";
            parms._response_column = "event";
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant" };
            parms._ties = CoxPHTies.efron;
            Assert.assertEquals("Surv(start, stop, event) ~ age", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            CoxPHModel model = ((CoxPHModel) (Scope.track_generic(builder.trainModel().get())));
            Assert.assertNotNull(model);
            Frame linearPredictors = Scope.track(model.score(fr));
            Assert.assertEquals(fr.numRows(), linearPredictors.numRows());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testCoxPHBreslow1Var() {
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = parse_test_file("smalldata/coxph_test/heart.csv");
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = "start";
            parms._stop_column = "stop";
            parms._response_column = "event";
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant" };
            parms._ties = CoxPHTies.breslow;
            Assert.assertEquals("Surv(start, stop, event) ~ age", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            model = builder.trainModel().get();
            Assert.assertEquals(model._output._coef[0], 0.0306910411003801, 1.0E-8);
            Assert.assertEquals(model._output._var_coef[0][0], 2.03592486905101E-4, 1.0E-8);
            Assert.assertEquals(model._output._null_loglik, (-298.325606736463), 1.0E-8);
            Assert.assertEquals(model._output._loglik, (-295.745227177782), 1.0E-8);
            Assert.assertEquals(model._output._score_test, 4.63317821557301, 1.0E-8);
            Assert.assertTrue(((model._output._iter) >= 1));
            Assert.assertEquals(model._output._x_mean_num[0][0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model._output._n, 172);
            Assert.assertEquals(model._output._total_event, 75);
            Assert.assertEquals(model._output._wald_test, 4.62659510743282, 1.0E-8);
            Assert.assertEquals(model._output._var_cumhaz_2_matrix.rows(), 110);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHEfron1VarNoStart() {
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = parse_test_file("smalldata/coxph_test/heart.csv");
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = null;
            parms._stop_column = "stop";
            parms._response_column = "event";
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant", "start" };
            parms._ties = CoxPHTies.efron;
            Assert.assertEquals("Surv(stop, event) ~ age", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            model = builder.trainModel().get();
            Assert.assertEquals(model._output._coef[0], 0.0289468187293998, 1.0E-8);
            Assert.assertEquals(model._output._var_coef[0][0], 2.10975113029285E-4, 1.0E-8);
            Assert.assertEquals(model._output._null_loglik, (-314.148170059513), 1.0E-8);
            Assert.assertEquals(model._output._loglik, (-311.946958322919), 1.0E-8);
            Assert.assertEquals(model._output._score_test, 3.97716015008595, 1.0E-8);
            Assert.assertTrue(((model._output._iter) >= 1));
            Assert.assertEquals(model._output._x_mean_num[0][0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model._output._n, 172);
            Assert.assertEquals(model._output._total_event, 75);
            Assert.assertEquals(model._output._wald_test, 3.97164529276219, 1.0E-8);
            Assert.assertEquals(model._output._var_cumhaz_2_matrix.rows(), 110);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHBreslow1VarNoStart() {
        CoxPHModel model = null;
        Frame fr = null;
        try {
            fr = parse_test_file("smalldata/coxph_test/heart.csv");
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = null;
            parms._stop_column = "stop";
            parms._response_column = "event";
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant", "start" };
            parms._ties = CoxPHTies.breslow;
            Assert.assertEquals("Surv(stop, event) ~ age", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            model = builder.trainModel().get();
            Assert.assertEquals(model._output._coef[0], 0.0289484855901731, 1.0E-8);
            Assert.assertEquals(model._output._var_coef[0][0], 2.11028794751156E-4, 1.0E-8);
            Assert.assertEquals(model._output._null_loglik, (-314.2964933669), 1.0E-8);
            Assert.assertEquals(model._output._loglik, (-312.095342077591), 1.0E-8);
            Assert.assertEquals(model._output._score_test, 3.97665282498882, 1.0E-8);
            Assert.assertTrue(((model._output._iter) >= 1));
            Assert.assertEquals(model._output._x_mean_num[0][0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model._output._n, 172);
            Assert.assertEquals(model._output._total_event, 75);
            Assert.assertEquals(model._output._wald_test, 3.97109228128153, 1.0E-8);
            Assert.assertEquals(model._output._var_cumhaz_2_matrix.rows(), 110);
        } finally {
            if (fr != null)
                fr.delete();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void testCoxPHEfron1Interaction() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file("smalldata/coxph_test/heart.csv"));
            // Decompose a "age" column into two components: "age1" and "age2"
            Frame ext = new MRTask() {
                @Override
                public void map(Chunk c, NewChunk nc0, NewChunk nc1) {
                    for (int i = 0; i < (c._len); i++) {
                        double v = c.atd(i);
                        if ((i % 2) == 0) {
                            nc0.addNum(v);
                            nc1.addNum(1);
                        } else {
                            nc0.addNum(1);
                            nc1.addNum(v);
                        }
                    }
                }
            }.doAll(new byte[]{ Vec.T_NUM, Vec.T_NUM }, fr.vec("age")).outputFrame(Key.<Frame>make(), new String[]{ "age1", "age2" }, null);
            Scope.track(ext);
            fr.add(ext);
            CoxPHModel.CoxPHParameters parms = new CoxPHModel.CoxPHParameters();
            parms._calc_cumhaz = true;
            parms._train = fr._key;
            parms._start_column = "start";
            parms._stop_column = "stop";
            parms._response_column = "event";
            // We create interaction pair from the "age" components
            parms._interaction_pairs = new StringPair[]{ new StringPair("age1", "age2") };
            parms._interactions_only = new String[]{ "age1", "age2" };
            // Exclude the original "age" column
            parms._ignored_columns = new String[]{ "id", "year", "surgery", "transplant", "age" };
            parms._ties = CoxPHTies.efron;
            Assert.assertEquals("Surv(start, stop, event) ~ age1:age2", parms.toFormula(fr));
            CoxPH builder = new CoxPH(parms);
            CoxPHModel model = ((CoxPHModel) (Scope.track_generic(builder.trainModel().get())));
            // Expect the same result as we used "age"
            Assert.assertEquals(model._output._coef[0], 0.0307077486571334, 1.0E-8);
            Assert.assertEquals(model._output._var_coef[0][0], 2.03471477951459E-4, 1.0E-8);
            Assert.assertEquals(model._output._null_loglik, (-298.121355672984), 1.0E-8);
            Assert.assertEquals(model._output._loglik, (-295.536762216228), 1.0E-8);
            Assert.assertEquals(model._output._score_test, 4.64097294749287, 1.0E-8);
            Assert.assertTrue(((model._output._iter) >= 1));
            Assert.assertEquals(model._output._x_mean_num[0][0], (-2.48402655078554), 1.0E-8);
            Assert.assertEquals(model._output._n, 172);
            Assert.assertEquals(model._output._total_event, 75);
            Assert.assertEquals(model._output._wald_test, 4.6343882547245, 1.0E-8);
        } finally {
            Scope.exit();
        }
    }
}

