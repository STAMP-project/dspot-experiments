package hex.tree.xgboost;


import XGBoostModel.XGBoostParameters;
import biz.k11i.xgboost.Predictor;
import biz.k11i.xgboost.gbm.GBTree;
import biz.k11i.xgboost.tree.RegTree;
import biz.k11i.xgboost.tree.RegTreeNode;
import hex.genmodel.algos.tree.SharedTreeGraph;
import hex.genmodel.algos.tree.SharedTreeSubgraph;
import java.io.ByteArrayInputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.DKV;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


public class XGBoostTreeConverterTest extends TestUtil {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void convertXGBoostTree_weather() throws Exception {
        Frame tfr = null;
        XGBoostModel model = null;
        Scope.enter();
        try {
            // Parse frame into H2O
            tfr = parse_test_file("./smalldata/junit/weather.csv");
            String response = "PressureChange";
            Scope.track(tfr.replace(tfr.find(response), tfr.vecs()[tfr.find(response)].toCategoricalVec()));
            DKV.put(tfr);
            XGBoostModel.XGBoostParameters parms = new XGBoostModel.XGBoostParameters();
            parms._ntrees = 1;
            parms._max_depth = 3;
            parms._train = tfr._key;
            parms._response_column = response;
            parms._reg_lambda = 0;
            model = trainModel().get();
            final GBTree booster = ((GBTree) (new Predictor(new ByteArrayInputStream(model.model_info()._boosterBytes)).getBooster()));
            final RegTree tree = booster.getGroupedTrees()[0][0];
            final RegTreeNode[] nodes = tree.getNodes();
            Assert.assertNotNull(nodes);
            final SharedTreeGraph sharedTreeGraph = model.convert(0, "down");
            Assert.assertNotNull(sharedTreeGraph);
            Assert.assertEquals(parms._ntrees, sharedTreeGraph.subgraphArray.size());
            final SharedTreeSubgraph sharedTreeSubgraph = sharedTreeGraph.subgraphArray.get(0);
            Assert.assertEquals(parms._max_depth, sharedTreeSubgraph.nodesArray.get(((sharedTreeSubgraph.nodesArray.size()) - 1)).getDepth());
        } finally {
            Scope.exit();
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void convertXGBoostTree_airlines() throws Exception {
        Frame tfr = null;
        XGBoostModel model = null;
        Scope.enter();
        try {
            // Parse frame into H2O
            tfr = parse_test_file("./smalldata/testng/airlines_train.csv");
            String response = "IsDepDelayed";
            Scope.track(tfr.replace(tfr.find(response), tfr.vecs()[tfr.find(response)].toCategoricalVec()));
            DKV.put(tfr);
            XGBoostModel.XGBoostParameters parms = new XGBoostModel.XGBoostParameters();
            parms._ntrees = 1;
            parms._max_depth = 5;
            parms._ignored_columns = new String[]{ "fYear", "fMonth", "fDayofMonth", "fDayOfWeek", "UniqueCarrier", "Dest" };
            parms._train = tfr._key;
            parms._response_column = response;
            parms._reg_lambda = 0;
            model = trainModel().get();
            final GBTree booster = ((GBTree) (new Predictor(new ByteArrayInputStream(model.model_info()._boosterBytes)).getBooster()));
            final RegTree tree = booster.getGroupedTrees()[0][0];
            final RegTreeNode[] nodes = tree.getNodes();
            Assert.assertNotNull(nodes);
            final SharedTreeGraph sharedTreeGraph = model.convert(0, "NO");
            Assert.assertNotNull(sharedTreeGraph);
            Assert.assertEquals(parms._ntrees, sharedTreeGraph.subgraphArray.size());
            final SharedTreeSubgraph sharedTreeSubgraph = sharedTreeGraph.subgraphArray.get(0);
            Assert.assertEquals(parms._max_depth, sharedTreeSubgraph.nodesArray.get(((sharedTreeSubgraph.nodesArray.size()) - 1)).getDepth());
        } finally {
            Scope.exit();
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.delete();

        }
    }

    @Test
    public void convertXGBoostTree_airlines_wrong_tree_class() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("There should be no tree class specified for regression.");
        Frame tfr = null;
        XGBoostModel model = null;
        Scope.enter();
        try {
            // Parse frame into H2O
            tfr = parse_test_file("./smalldata/testng/airlines_train.csv", "NA", 1, new byte[]{ 4, 2, 2, 2, 2, 4, 4, 4, 3 });
            String response = "Distance";
            DKV.put(tfr);
            XGBoostModel.XGBoostParameters parms = new XGBoostModel.XGBoostParameters();
            parms._ntrees = 1;
            parms._max_depth = 5;
            parms._ignored_columns = new String[]{ "fYear", "fMonth", "fDayofMonth", "fDayOfWeek", "UniqueCarrier", "Dest" };
            parms._train = tfr._key;
            parms._response_column = response;
            model = trainModel().get();
            final GBTree booster = ((GBTree) (new Predictor(new ByteArrayInputStream(model.model_info()._boosterBytes)).getBooster()));
            final RegTree tree = booster.getGroupedTrees()[0][0];
            final SharedTreeGraph sharedTreeGraph = model.convert(0, "NO");
            Assert.assertNotNull(sharedTreeGraph);
            Assert.assertEquals(parms._ntrees, sharedTreeGraph.subgraphArray.size());
            final SharedTreeSubgraph sharedTreeSubgraph = sharedTreeGraph.subgraphArray.get(0);
            Assert.assertEquals(parms._max_depth, sharedTreeSubgraph.nodesArray.get(((sharedTreeSubgraph.nodesArray.size()) - 1)).getDepth());
        } finally {
            Scope.exit();
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.delete();

        }
    }
}

