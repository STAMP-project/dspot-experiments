package hex.tree.gbm;


import GBMModel.GBMParameters;
import Model.Parameters;
import hex.Model;
import hex.genmodel.utils.DistributionFamily;
import hex.grid.Grid;
import hex.grid.GridSearch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Job;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.Vec;
import water.test.util.GridTestUtils;
import water.util.ArrayUtils;


public class GBMGridTest extends TestUtil {
    @Test
    public void testCarsGrid() {
        Grid<GBMModel.GBMParameters> grid = null;
        Frame fr = null;
        Vec old = null;
        try {
            fr = parse_test_file("smalldata/junit/cars.csv");
            fr.remove("name").remove();// Remove unique id

            old = fr.remove("cylinders");
            fr.add("cylinders", old.toCategoricalVec());// response to last column

            DKV.put(fr);
            // Setup hyperparameter search space
            final Double[] legalLearnRateOpts = new Double[]{ 0.01, 0.1, 0.3 };
            final Double[] illegalLearnRateOpts = new Double[]{ -1.0 };
            HashMap<String, Object[]> hyperParms = new HashMap<String, Object[]>() {
                {
                    put("_ntrees", new Integer[]{ 1, 2 });
                    put("_distribution", new DistributionFamily[]{ DistributionFamily.multinomial });
                    put("_max_depth", new Integer[]{ 1, 2, 5 });
                    put("_learn_rate", ArrayUtils.join(legalLearnRateOpts, illegalLearnRateOpts));
                }
            };
            // Name of used hyper parameters
            String[] hyperParamNames = hyperParms.keySet().toArray(new String[hyperParms.size()]);
            Arrays.sort(hyperParamNames);
            int hyperSpaceSize = ArrayUtils.crossProductSize(hyperParms);
            // Fire off a grid search
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = fr._key;
            params._response_column = "cylinders";
            // Get the Grid for this modeling class and frame
            Job<Grid> gs = GridSearch.startGridSearch(null, params, hyperParms);
            grid = ((Grid<GBMModel.GBMParameters>) (gs.get()));
            // Make sure number of produced models match size of specified hyper space
            Assert.assertEquals("Size of grid (models+failures) should match to size of hyper space", hyperSpaceSize, ((grid.getModelCount()) + (grid.getFailureCount())));
            // 
            // Make sure that names of used parameters match
            // 
            String[] gridHyperNames = grid.getHyperNames();
            Arrays.sort(gridHyperNames);
            Assert.assertArrayEquals("Hyper parameters names should match!", hyperParamNames, gridHyperNames);
            // 
            // Make sure that values of used parameters match as well to the specified values
            // 
            Key<Model>[] mKeys = grid.getModelKeys();
            Map<String, Set<Object>> usedHyperParams = GridTestUtils.initMap(hyperParamNames);
            for (Key<Model> mKey : mKeys) {
                GBMModel gbm = ((GBMModel) (mKey.get()));
                System.out.println((((gbm._output._scored_train[gbm._output._ntrees]._mse) + " ") + (Arrays.deepToString(ArrayUtils.zip(grid.getHyperNames(), grid.getHyperValues(gbm._parms))))));
                GridTestUtils.extractParams(usedHyperParams, gbm._parms, hyperParamNames);
            }
            // Remove illegal options
            hyperParms.put("_learn_rate", legalLearnRateOpts);
            GridTestUtils.assertParamsEqual("Grid models parameters have to cover specified hyper space", hyperParms, usedHyperParams);
            // Verify model failure
            Map<String, Set<Object>> failedHyperParams = GridTestUtils.initMap(hyperParamNames);
            for (Model.Parameters failedParams : grid.getFailedParameters()) {
                GridTestUtils.extractParams(failedHyperParams, failedParams, hyperParamNames);
            }
            hyperParms.put("_learn_rate", illegalLearnRateOpts);
            GridTestUtils.assertParamsEqual("Failed model parameters have to correspond to specified hyper space", hyperParms, failedHyperParams);
        } finally {
            if (old != null) {
                old.remove();
            }
            if (fr != null) {
                fr.remove();
            }
            if (grid != null) {
                grid.remove();
            }
        }
    }

    // @Ignore("PUBDEV-1643")
    @Test
    public void testDuplicatesCarsGrid() {
        Grid grid = null;
        Frame fr = null;
        Vec old = null;
        try {
            fr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            fr.remove("name").remove();// Remove unique id

            old = fr.remove("economy");
            fr.add("economy", old);// response to last column

            DKV.put(fr);
            // Setup random hyperparameter search space
            HashMap<String, Object[]> hyperParms = new HashMap<String, Object[]>() {
                {
                    put("_distribution", new DistributionFamily[]{ DistributionFamily.gaussian });
                    put("_ntrees", new Integer[]{ 5, 5 });
                    put("_max_depth", new Integer[]{ 2, 2 });
                    put("_learn_rate", new Double[]{ 0.1, 0.1 });
                }
            };
            // Fire off a grid search
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = fr._key;
            params._response_column = "economy";
            Job<Grid> gs = GridSearch.startGridSearch(null, params, hyperParms);
            grid = gs.get();
            // Check that duplicate model have not been constructed
            Model[] models = grid.getModels();
            Assert.assertTrue("Number of returned models has to be > 0", ((models.length) > 0));
            // But all off them should be same
            Key<Model> modelKey = models[0]._key;
            for (Model m : models) {
                Assert.assertTrue("Number of constructed models has to be equal to 1", (modelKey == (m._key)));
            }
        } finally {
            if (old != null) {
                old.remove();
            }
            if (fr != null) {
                fr.remove();
            }
            if (grid != null) {
                grid.remove();
            }
        }
    }

    // @Ignore("PUBDEV-4361")
    @Test
    public void testGridAccumulation() {
        Grid grid = null;
        Frame fr = null;
        Vec old = null;
        try {
            fr = parse_test_file("smalldata/junit/cars_20mpg.csv");
            fr.remove("name").remove();// Remove unique id

            old = fr.remove("economy");
            fr.add("economy", old);// response to last column

            DKV.put(fr);
            // Setup random hyperparameter search space
            HashMap<String, Object[]> hyperParms = new HashMap<String, Object[]>() {
                {
                    put("_distribution", new DistributionFamily[]{ DistributionFamily.gaussian });
                    put("_ntrees", new Integer[]{ 2 });
                    put("_max_depth", new Integer[]{ 2 });
                    put("_learn_rate", new Double[]{ 0.1 });
                }
            };
            // Fire off a grid search
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = fr._key;
            params._response_column = "economy";
            Key<Grid> accumulating_grid = Key.make("accumulating_grid");
            Job<Grid> gs = null;
            // search once
            gs = GridSearch.startGridSearch(accumulating_grid, params, hyperParms);
            grid = gs.get();
            // search again
            gs = GridSearch.startGridSearch(accumulating_grid, params, hyperParms);
            grid = gs.get();
            // Check that duplicate model have not been constructed
            Model[] models = grid.getModels();
            Assert.assertTrue("Number of returned models has to be 1", ((models.length) == 1));
        } finally {
            if (old != null) {
                old.remove();
            }
            if (fr != null) {
                fr.remove();
            }
            if (grid != null) {
                grid.remove();
            }
        }
    }

    // @Ignore("PUBDEV-1648")
    @Test
    public void testRandomCarsGrid() {
        Grid grid = null;
        GBMModel gbmRebuilt = null;
        Frame fr = null;
        Vec old = null;
        try {
            fr = parse_test_file("smalldata/junit/cars.csv");
            fr.remove("name").remove();
            old = fr.remove("economy (mpg)");
            fr.add("economy (mpg)", old);// response to last column

            DKV.put(fr);
            // Setup random hyperparameter search space
            HashMap<String, Object[]> hyperParms = new HashMap<>();
            hyperParms.put("_distribution", new DistributionFamily[]{ DistributionFamily.gaussian });
            // Construct random grid search space
            Random rng = new Random();
            Integer ntreesDim = (rng.nextInt(4)) + 1;
            Integer maxDepthDim = (rng.nextInt(4)) + 1;
            Integer learnRateDim = (rng.nextInt(4)) + 1;
            Integer[] ntreesArr = interval(1, 25);
            ArrayList<Integer> ntreesList = new ArrayList<>(Arrays.asList(ntreesArr));
            Collections.shuffle(ntreesList);
            Integer[] ntreesSpace = new Integer[ntreesDim];
            for (int i = 0; i < ntreesDim; i++) {
                ntreesSpace[i] = ntreesList.get(i);
            }
            Integer[] maxDepthArr = interval(1, 10);
            ArrayList<Integer> maxDepthList = new ArrayList<>(Arrays.asList(maxDepthArr));
            Collections.shuffle(maxDepthList);
            Integer[] maxDepthSpace = new Integer[maxDepthDim];
            for (int i = 0; i < maxDepthDim; i++) {
                maxDepthSpace[i] = maxDepthList.get(i);
            }
            Double[] learnRateArr = interval(0.01, 1.0, 0.01);
            ArrayList<Double> learnRateList = new ArrayList<>(Arrays.asList(learnRateArr));
            Collections.shuffle(learnRateList);
            Double[] learnRateSpace = new Double[learnRateDim];
            for (int i = 0; i < learnRateDim; i++) {
                learnRateSpace[i] = learnRateList.get(i);
            }
            hyperParms.put("_ntrees", ntreesSpace);
            hyperParms.put("_max_depth", maxDepthSpace);
            hyperParms.put("_learn_rate", learnRateSpace);
            // Fire off a grid search
            GBMModel.GBMParameters params = new GBMModel.GBMParameters();
            params._train = fr._key;
            params._response_column = "economy (mpg)";
            // Get the Grid for this modeling class and frame
            Job<Grid> gs = GridSearch.startGridSearch(null, params, hyperParms);
            grid = gs.get();
            System.out.println(("ntrees search space: " + (Arrays.toString(ntreesSpace))));
            System.out.println(("max_depth search space: " + (Arrays.toString(maxDepthSpace))));
            System.out.println(("learn_rate search space: " + (Arrays.toString(learnRateSpace))));
            // Check that cardinality of grid
            Model[] ms = grid.getModels();
            Integer numModels = ms.length;
            System.out.println((("Grid consists of " + numModels) + " models"));
            Assert.assertTrue((numModels == ((ntreesDim * maxDepthDim) * learnRateDim)));
            // Pick a random model from the grid
            HashMap<String, Object[]> randomHyperParms = new HashMap<>();
            randomHyperParms.put("_distribution", new DistributionFamily[]{ DistributionFamily.gaussian });
            Integer ntreeVal = ntreesSpace[rng.nextInt(ntreesSpace.length)];
            randomHyperParms.put("_ntrees", new Integer[]{ ntreeVal });
            Integer maxDepthVal = maxDepthSpace[rng.nextInt(maxDepthSpace.length)];
            randomHyperParms.put("_max_depth", maxDepthSpace);
            Double learnRateVal = learnRateSpace[rng.nextInt(learnRateSpace.length)];
            randomHyperParms.put("_learn_rate", learnRateSpace);
            // TODO: GBMModel gbmFromGrid = (GBMModel) g2.model(randomHyperParms).get();
            // Rebuild it with it's parameters
            params._distribution = DistributionFamily.gaussian;
            params._ntrees = ntreeVal;
            params._max_depth = maxDepthVal;
            params._learn_rate = learnRateVal;
            GBM gbm = new GBM(params);
            gbmRebuilt = gbm.trainModel().get();
            Assert.assertTrue(gbm.isStopped());
            // Make sure the MSE metrics match
            // double fromGridMSE = gbmFromGrid._output._scored_train[gbmFromGrid._output._ntrees]._mse;
            double rebuiltMSE = gbmRebuilt._output._scored_train[gbmRebuilt._output._ntrees]._mse;
            // System.out.println("The random grid model's MSE: " + fromGridMSE);
            System.out.println(("The rebuilt model's MSE: " + rebuiltMSE));
            // assertEquals(fromGridMSE, rebuiltMSE);
        } finally {
            if (old != null)
                old.remove();

            if (fr != null)
                fr.remove();

            if (grid != null)
                grid.remove();

            if (gbmRebuilt != null)
                gbmRebuilt.remove();

        }
    }
}

