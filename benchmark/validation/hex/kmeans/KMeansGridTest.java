package hex.kmeans;


import KMeans.Initialization;
import KMeansModel.KMeansParameters;
import Model.Parameters;
import hex.Model;
import hex.grid.Grid;
import hex.grid.GridSearch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Job;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;
import water.test.util.GridTestUtils;
import water.util.ArrayUtils;


public class KMeansGridTest extends TestUtil {
    @Test
    public void testIrisGrid() {
        Grid<KMeansModel.KMeansParameters> grid = null;
        Frame fr = null;
        try {
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            // 4-dimensional hyperparameter search
            HashMap<String, Object[]> hyperParms = new HashMap<>();
            // Search over this range of K's
            Integer[] legalKOpts = new Integer[]{ 1, 2, 3, 4, 5 };
            Integer[] illegalKOpts = new Integer[]{ 0 };
            hyperParms.put("_k", ArrayUtils.join(legalKOpts, illegalKOpts));
            // Search over this range of the init enum
            hyperParms.put("_init", new KMeans.Initialization[]{ Initialization.Random, Initialization.PlusPlus, Initialization.Furthest });
            // Search over this range of the init enum
            hyperParms.put("_seed", new Long[]{ 1L, 123456789L, 987654321L });
            // Name of used hyper parameters
            String[] hyperParamNames = hyperParms.keySet().toArray(new String[hyperParms.size()]);
            Arrays.sort(hyperParamNames);
            int hyperSpaceSize = ArrayUtils.crossProductSize(hyperParms);
            // Create default parameters
            KMeansModel.KMeansParameters params = new KMeansModel.KMeansParameters();
            params._train = fr._key;
            // Fire off a grid search and get result
            Job<Grid> gs = GridSearch.startGridSearch(null, params, hyperParms);
            grid = ((Grid<KMeansModel.KMeansParameters>) (gs.get()));
            // Make sure number of produced models match size of specified hyper space
            Assert.assertEquals("Size of grid should match to size of hyper space", hyperSpaceSize, ((grid.getModelCount()) + (grid.getFailureCount())));
            // 
            // Make sure that names of used parameters match
            // 
            String[] gridHyperNames = grid.getHyperNames();
            Arrays.sort(gridHyperNames);
            Assert.assertArrayEquals("Hyper parameters names should match!", hyperParamNames, gridHyperNames);
            // 
            // Make sure that values of used parameters match as well to the specified values
            // 
            Map<String, Set<Object>> usedModelParams = GridTestUtils.initMap(hyperParamNames);
            Model[] ms = grid.getModels();
            for (Model m : ms) {
                KMeansModel kmm = ((KMeansModel) (m));
                System.out.println((((kmm._output._tot_withinss) + " ") + (Arrays.deepToString(ArrayUtils.zip(grid.getHyperNames(), grid.getHyperValues(kmm._parms))))));
                GridTestUtils.extractParams(usedModelParams, kmm._parms, hyperParamNames);
            }
            hyperParms.put("_k", legalKOpts);
            GridTestUtils.assertParamsEqual("Grid models parameters have to cover specified hyper space", hyperParms, usedModelParams);
            // Verify model failure
            Map<String, Set<Object>> failedHyperParams = GridTestUtils.initMap(hyperParamNames);
            for (Model.Parameters failedParams : grid.getFailedParameters()) {
                GridTestUtils.extractParams(failedHyperParams, ((KMeansModel.KMeansParameters) (failedParams)), hyperParamNames);
            }
            hyperParms.put("_k", illegalKOpts);
            GridTestUtils.assertParamsEqual("Failed model parameters have to correspond to specified hyper space", hyperParms, failedHyperParams);
        } finally {
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
        try {
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            fr.remove("class").remove();
            DKV.put(fr);
            // Setup hyperparameter search space
            HashMap<String, Object[]> hyperParms = new HashMap<>();
            hyperParms.put("_k", new Integer[]{ 3, 3, 3 });
            hyperParms.put("_init", new KMeans.Initialization[]{ Initialization.Random, Initialization.Random, Initialization.Random });
            hyperParms.put("_seed", new Long[]{ 123456789L, 123456789L, 123456789L });
            // Fire off a grid search
            KMeansModel.KMeansParameters params = new KMeansModel.KMeansParameters();
            params._train = fr._key;
            // Get the Grid for this modeling class and frame
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
            if (fr != null) {
                fr.remove();
            }
            if (grid != null) {
                grid.remove();
            }
        }
    }

    @Test
    public void testUserPointsCarsGrid() {
        Grid grid = null;
        Frame fr = null;
        Frame init = ArrayUtils.frame(ard(ard(5.0, 3.4, 1.5, 0.2), ard(7.0, 3.2, 4.7, 1.4), ard(6.5, 3.0, 5.8, 2.2)));
        try {
            fr = parse_test_file("smalldata/iris/iris_wheader.csv");
            fr.remove("class").remove();
            DKV.put(fr);
            // Setup hyperparameter search space
            HashMap<String, Object[]> hyperParms = new HashMap<>();
            hyperParms.put("_k", new Integer[]{ 3 });
            hyperParms.put("_init", new KMeans.Initialization[]{ Initialization.Random, Initialization.PlusPlus, Initialization.User, Initialization.Furthest });
            hyperParms.put("_seed", new Long[]{ 123456789L });
            // Fire off a grid search
            KMeansModel.KMeansParameters params = new KMeansModel.KMeansParameters();
            params._train = fr._key;
            params._user_points = init._key;
            // Get the Grid for this modeling class and frame
            Job<Grid> gs = GridSearch.startGridSearch(null, params, hyperParms);
            grid = gs.get();
            // Check that duplicate model have not been constructed
            Integer numModels = grid.getModels().length;
            System.out.println((("Grid consists of " + numModels) + " models"));
            Assert.assertTrue((numModels == 4));
        } finally {
            if (fr != null) {
                fr.remove();
            }
            if (init != null) {
                init.remove();
            }
            if (grid != null) {
                grid.remove();
            }
        }
    }
}

