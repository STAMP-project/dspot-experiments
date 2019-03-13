package water.serial;


import Family.poisson;
import hex.GLMTest2;
import hex.Key;
import hex.drf.DRF.DRFModel;
import hex.gbm.GBM.GBMModel;
import java.io.IOException;
import org.junit.Test;
import water.TestUtil;


public class ModelSerializationTest extends TestUtil {
    private static int[] EIA = new int[]{  };

    @Test
    public void testSimpleModel() throws IOException {
        // Create a model
        Model model = new ModelSerializationTest.BlahModel(null, null, TestUtil.ar("ColumnBlah", "response"), new String[2][]);
        // Create a serializer, save a model and reload it
        Model loadedModel = saveAndLoad(model);
        // And compare
        TestUtil.assertModelEquals(model, loadedModel);
    }

    @Test
    public void testGBMModelMultinomial() throws IOException {
        GBMModel model = null;
        GBMModel loadedModel = null;
        try {
            model = prepareGBMModel("smalldata/iris/iris.csv", ModelSerializationTest.EIA, 4, true, 5);
            loadedModel = saveAndLoad(model);
            // And compare
            TestUtil.assertTreeModelEquals(model, loadedModel);
            TestUtil.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testGBMModelBinomial() throws IOException {
        GBMModel model = null;
        GBMModel loadedModel = null;
        try {
            model = prepareGBMModel("smalldata/logreg/prostate.csv", TestUtil.ari(0), 1, true, 5);
            loadedModel = saveAndLoad(model);
            // And compare
            TestUtil.assertTreeModelEquals(model, loadedModel);
            TestUtil.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testDRFModelMultinomial() throws IOException {
        DRFModel model = null;
        DRFModel loadedModel = null;
        try {
            model = prepareDRFModel("smalldata/iris/iris.csv", ModelSerializationTest.EIA, 4, true, 5);
            loadedModel = saveAndLoad(model);
            // And compare
            TestUtil.assertTreeModelEquals(model, loadedModel);
            TestUtil.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testDRFModelBinomial() throws IOException {
        DRFModel model = null;
        DRFModel loadedModel = null;
        try {
            model = prepareDRFModel("smalldata/logreg/prostate.csv", TestUtil.ari(0), 1, true, 5);
            loadedModel = saveAndLoad(model);
            // And compare
            TestUtil.assertTreeModelEquals(model, loadedModel);
            TestUtil.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testGLMModel() throws IOException {
        GLMModel model = null;
        GLMModel loadedModel = null;
        try {
            model = prepareGLMModel("smalldata/cars.csv", ModelSerializationTest.EIA, 4, poisson);
            loadedModel = saveAndLoad(model);
            TestUtil.assertModelBinaryEquals(model, loadedModel);
            GLMTest2.testHTML(loadedModel);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    static class BlahModel extends Model {
        // static final int DEBUG_WEAVER = 1;
        final Key[] keys;

        final VarImp varimp;

        public BlahModel(Key selfKey, Key dataKey, String[] names, String[][] domains) {
            super(selfKey, dataKey, names, domains, null, null);
            keys = new Key[3];
            varimp = new VarImp.VarImpRI(TestUtil.arf(1.0F, 1.0F, 1.0F));
        }

        @Override
        protected float[] score0(double[] data, float[] preds) {
            throw new RuntimeException("TODO Auto-generated method stub");
        }
    }
}

