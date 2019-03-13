package water;


import GLMModel.GLMParameters.Family.poisson;
import Model.Output;
import Model.Parameters;
import ModelMetrics.MetricBuilder;
import hex.Model;
import hex.glm.GLMModel;
import hex.tree.CompressedTree;
import hex.tree.drf.DRFModel;
import hex.tree.gbm.GBMModel;
import hex.tree.isofor.IsolationForestModel;
import java.io.IOException;
import org.junit.Test;


public class ModelSerializationTest extends TestUtil {
    private static String[] ESA = new String[]{  };

    @Test
    public void testSimpleModel() throws IOException {
        // Create a model
        ModelSerializationTest.BlahModel.BlahParameters params = new ModelSerializationTest.BlahModel.BlahParameters();
        ModelSerializationTest.BlahModel.BlahOutput output = new ModelSerializationTest.BlahModel.BlahOutput(false, false, false);
        Model model = new ModelSerializationTest.BlahModel(Key.make("BLAHModel"), params, output);
        DKV.put(model._key, model);
        // Create a serializer, save a model and reload it
        Model loadedModel = null;
        try {
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testGBMModelMultinomial() throws IOException {
        GBMModel model;
        GBMModel loadedModel = null;
        try {
            model = prepareGBMModel("smalldata/iris/iris.csv", ModelSerializationTest.ESA, "C5", true, 5);
            CompressedTree[][] trees = ModelSerializationTest.getTrees(model);
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
            CompressedTree[][] loadedTrees = ModelSerializationTest.getTrees(loadedModel);
            ModelSerializationTest.assertTreeEquals("Trees have to be binary same", trees, loadedTrees);
        } finally {
            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testGBMModelBinomial() throws IOException {
        GBMModel model;
        GBMModel loadedModel = null;
        try {
            model = prepareGBMModel("smalldata/logreg/prostate.csv", ar("ID"), "CAPSULE", true, 5);
            CompressedTree[][] trees = ModelSerializationTest.getTrees(model);
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
            CompressedTree[][] loadedTrees = ModelSerializationTest.getTrees(loadedModel);
            ModelSerializationTest.assertTreeEquals("Trees have to be binary same", trees, loadedTrees);
        } finally {
            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testDRFModelMultinomial() throws IOException {
        DRFModel model;
        DRFModel loadedModel = null;
        try {
            model = prepareDRFModel("smalldata/iris/iris.csv", ModelSerializationTest.ESA, "C5", true, 5);
            CompressedTree[][] trees = ModelSerializationTest.getTrees(model);
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
            CompressedTree[][] loadedTrees = ModelSerializationTest.getTrees(loadedModel);
            ModelSerializationTest.assertTreeEquals("Trees have to be binary same", trees, loadedTrees);
        } finally {
            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testDRFModelBinomial() throws IOException {
        DRFModel model = null;
        DRFModel loadedModel = null;
        try {
            model = prepareDRFModel("smalldata/logreg/prostate.csv", ar("ID"), "CAPSULE", true, 5);
            CompressedTree[][] trees = ModelSerializationTest.getTrees(model);
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
            CompressedTree[][] loadedTrees = ModelSerializationTest.getTrees(loadedModel);
            ModelSerializationTest.assertTreeEquals("Trees have to be binary same", trees, loadedTrees);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testIsolationForestModel() throws IOException {
        IsolationForestModel model = null;
        IsolationForestModel loadedModel = null;
        try {
            model = prepareIsoForModel("smalldata/logreg/prostate.csv", ar("ID", "CAPSULE"), 5);
            CompressedTree[][] trees = ModelSerializationTest.getTrees(model);
            loadedModel = saveAndLoad(model);
            // And compare
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
            CompressedTree[][] loadedTrees = ModelSerializationTest.getTrees(loadedModel);
            ModelSerializationTest.assertTreeEquals("Trees have to be binary same", trees, loadedTrees);
        } finally {
            if (model != null)
                model.delete();

            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    @Test
    public void testGLMModel() throws IOException {
        GLMModel model;
        GLMModel loadedModel = null;
        try {
            model = prepareGLMModel("smalldata/junit/cars.csv", ModelSerializationTest.ESA, "power (hp)", poisson);
            loadedModel = saveAndLoad(model);
            ModelSerializationTest.assertModelBinaryEquals(model, loadedModel);
        } finally {
            if (loadedModel != null)
                loadedModel.delete();

        }
    }

    /**
     * Dummy model to test model serialization
     */
    static class BlahModel extends Model<ModelSerializationTest.BlahModel, ModelSerializationTest.BlahModel.BlahParameters, ModelSerializationTest.BlahModel.BlahOutput> {
        public BlahModel(Key selfKey, ModelSerializationTest.BlahModel.BlahParameters params, ModelSerializationTest.BlahModel.BlahOutput output) {
            super(selfKey, params, output);
        }

        @Override
        public MetricBuilder makeMetricBuilder(String[] domain) {
            return null;
        }

        @Override
        protected double[] score0(double[] data, double[] preds) {
            return new double[0];
        }

        static class BlahParameters extends Model.Parameters {
            public String algoName() {
                return "Blah";
            }

            public String fullName() {
                return "Blah";
            }

            public String javaName() {
                return ModelSerializationTest.BlahModel.class.getName();
            }

            @Override
            public long progressUnits() {
                return 0;
            }
        }

        static class BlahOutput extends Model.Output {
            public BlahOutput(boolean hasWeights, boolean hasOffset, boolean hasFold) {
                super(hasWeights, hasOffset, hasFold);
            }
        }
    }
}

