package hex.tree;


import GBMModel.GBMParameters;
import GLMModel.GLMParameters.Family;
import SharedTreeModel.SharedTreeOutput;
import TreeHandler.TreeProperties;
import hex.genmodel.algos.tree.SharedTreeNode;
import hex.genmodel.algos.tree.SharedTreeSubgraph;
import hex.glm.GLMModel;
import hex.schemas.TreeV3;
import hex.tree.gbm.GBMModel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Frame;

import static water.TestUtil.<init>;


public class TreeHandlerTest extends TestUtil {
    @Test
    public void testSharedTreeSubgraphConversion() {
        Frame tfr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/airlines/allyears2k_headers.zip");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "Dest";
            parms._ntrees = 1;
            parms._seed = 0;
            model = trainModel().get();
            final SharedTreeModel.SharedTreeOutput sharedTreeOutput = model._output;
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._ntrees);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeys.length);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeysAux.length);
            final int treeIndex = 0;// Not a tree number, numbered from 0

            final int treeClass = 0;
            final CompressedTree auxCompressedTree = sharedTreeOutput._treeKeysAux[treeIndex][treeClass].get();
            final SharedTreeSubgraph sharedTreeSubgraph = sharedTreeOutput._treeKeys[treeIndex][treeClass].get().toSharedTreeSubgraph(auxCompressedTree, sharedTreeOutput._names, sharedTreeOutput._domains);
            Assert.assertNotNull(sharedTreeSubgraph);
            final TreeHandler.TreeProperties treeProperties = TreeHandler.convertSharedTreeSubgraph(sharedTreeSubgraph);
            Assert.assertNotNull(treeProperties);
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), treeProperties._descriptions.length);
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), treeProperties._thresholds.length);
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), treeProperties._features.length);
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), treeProperties._nas.length);
            final Pattern rootNodeSplitColPattern = Pattern.compile(".* and splits on column '.+'.*");
            Assert.assertTrue(rootNodeSplitColPattern.matcher(treeProperties._descriptions[0]).matches());
            final int[] leftChildren = treeProperties._leftChildren;
            final int[] rightChildren = treeProperties._rightChildren;
            Assert.assertEquals(leftChildren.length, rightChildren.length);
            final SharedTreeNode rootNode = sharedTreeSubgraph.rootNode;
            final Deque<SharedTreeNode> discoverednodes = new ArrayDeque<>();
            discoverednodes.push(rootNode);
            int nonRootNodesFound = 0;
            for (int i = 0; i < (leftChildren.length); i++) {
                final SharedTreeNode sharedTreeNode = discoverednodes.pollLast();
                final SharedTreeNode leftChild = sharedTreeNode.getLeftChild();
                final SharedTreeNode rightChild = sharedTreeNode.getRightChild();
                if ((leftChildren[i]) != (-1)) {
                    Assert.assertEquals(leftChildren[i], leftChild.getNodeNumber());
                    discoverednodes.push(sharedTreeNode.getLeftChild());
                    nonRootNodesFound++;
                }
                if ((rightChildren[i]) != (-1)) {
                    Assert.assertEquals(rightChildren[i], rightChild.getNodeNumber());
                    discoverednodes.push(sharedTreeNode.getRightChild());
                    nonRootNodesFound++;
                }
            }
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), (nonRootNodesFound + 1));// +1 for the root node that is not represented explicitely

        } finally {
            Scope.exit();
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

        }
    }

    @Test
    public void testSharedTreeSubgraphConversion_inclusiveLevelsIris() {
        Frame tfr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/iris/iris2.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._ntrees = 1;
            parms._seed = 0;
            model = trainModel().get();
            final SharedTreeModel.SharedTreeOutput sharedTreeOutput = model._output;
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._ntrees);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeys.length);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeysAux.length);
            final int treeNumber = 0;
            final int treeClass = 0;
            final CompressedTree auxCompressedTree = sharedTreeOutput._treeKeysAux[treeNumber][treeClass].get();
            final SharedTreeSubgraph sharedTreeSubgraph = sharedTreeOutput._treeKeys[treeNumber][treeClass].get().toSharedTreeSubgraph(auxCompressedTree, sharedTreeOutput._names, sharedTreeOutput._domains);
            Assert.assertNotNull(sharedTreeSubgraph);
            final TreeHandler.TreeProperties treeProperties = TreeHandler.convertSharedTreeSubgraph(sharedTreeSubgraph);
            Assert.assertNotNull(treeProperties);
            final String[] nodeDescriptions = treeProperties._descriptions;
            Assert.assertEquals(sharedTreeSubgraph.nodesArray.size(), nodeDescriptions.length);
            for (String nodeDescription : nodeDescriptions) {
                Assert.assertFalse(nodeDescription.isEmpty());
            }
        } finally {
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

        }
    }

    @Test
    public void testSharedTreeSubgraphConversion_argumentValidationMultinomial() {
        Frame tfr = null;
        GBMModel model = null;
        GLMModel nonTreeBasedModel = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/iris/iris2.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._response_column = "response";
            parms._ntrees = 1;
            parms._seed = 0;
            model = trainModel().get();
            final TreeHandler treeHandler = new TreeHandler();
            final TreeV3 args = new TreeV3();
            args.model = new water.api.schemas3.KeyV3.ModelKeyV3(Key.make());
            // Non-existing key
            boolean exceptionThrown = false;
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Given model does not exist"));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            exceptionThrown = false;
            nonTreeBasedModel = new GLMModel(Key.make(), new GLMModel.GLMParameters(Family.binomial), null, null, 1, 1, 1);
            DKV.put(nonTreeBasedModel);
            args.model = new water.api.schemas3.KeyV3.ModelKeyV3(nonTreeBasedModel._key);
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("Given model is not tree-based."));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            exceptionThrown = false;
            // Invalid tree index
            args.tree_number = 1;
            args.tree_class = tfr.vec(parms._response_column).domain()[0];
            args.model = new water.api.schemas3.KeyV3.ModelKeyV3(model._key);
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertEquals("Invalid tree index: 1. Tree index must be in range [0, 0].", e.getMessage());
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            exceptionThrown = false;
            // Invalid tree class
            args.tree_number = 0;
            args.tree_class = "NonExistingCategoricalLevel";
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("There is no such tree class. Given categorical level does not exist in response column: NonExistingCategoricalLevel"));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            exceptionThrown = false;
            // Tree number < 0
            args.tree_number = -1;
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains((("Invalid tree number: " + (args.tree_number)) + ". Tree number must be >= 0.")));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
        } finally {
            Scope.exit();
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

            if (nonTreeBasedModel != null)
                nonTreeBasedModel.remove();

        }
    }

    @Test
    public void testSharedTreeSubgraphConversion_argumentValidationRegression() {
        Frame tfr = null;
        GBMModel regressionModel = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/iris/iris2.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._ntrees = 1;
            parms._seed = 0;
            parms._response_column = "Sepal.Length";
            final TreeV3 args = new TreeV3();
            regressionModel = trainModel().get();
            args.model = new water.api.schemas3.KeyV3.ModelKeyV3(regressionModel._key);
            args.tree_class = "NonExistingClass";
            final TreeHandler treeHandler = new TreeHandler();
            boolean exceptionThrown = false;
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("There are no tree classes for Regression."));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (regressionModel != null)
                regressionModel.remove();

            Scope.exit();
        }
    }

    @Test
    public void testSharedTreeSubgraphConversion_argumentValidationBinomial() {
        Frame tfr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/testng/airlines_train.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._ntrees = 1;
            parms._seed = 0;
            parms._response_column = "IsDepDelayed";
            // Test incorrect tree request
            final TreeV3 args = new TreeV3();
            model = trainModel().get();
            args.model = new water.api.schemas3.KeyV3.ModelKeyV3(model._key);
            args.tree_class = "YES";
            // If the tree class name is specified, it must be the tree class built exactly
            final TreeHandler treeHandler = new TreeHandler();
            boolean exceptionThrown = false;
            try {
                treeHandler.getTree(3, args);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("For binomial, only one tree class has been built per each iteration: NO"));
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            args.tree_class = "NO";
            final TreeV3 correctlySpecifiedClassTree = treeHandler.getTree(3, args);
            Assert.assertNotNull(correctlySpecifiedClassTree);
            args.tree_class = "";
            final TreeV3 noClassTree = treeHandler.getTree(3, args);
            Assert.assertNotNull(noClassTree);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

            Scope.exit();
        }
    }

    @Test
    public void testNaHandling_airlines_train() {
        Frame tfr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/testng/airlines_train.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._ntrees = 1;
            parms._seed = 0;
            parms._response_column = "IsDepDelayed";
            // Test incorrect tree request
            model = trainModel().get();
            final SharedTreeSubgraph sharedTreeSubgraph = model.getSharedTreeSubgraph(0, 0);
            final TreeHandler.TreeProperties treeProperties = TreeHandler.convertSharedTreeSubgraph(sharedTreeSubgraph);
            Assert.assertNotNull(treeProperties);
            final SharedTreeNode rootNode = sharedTreeSubgraph.rootNode;
            final String[] noExcludedSplits = new String[]{  };
            final int naSplits = checkNaPath(rootNode, noExcludedSplits);
            // Count number of nonNull NA splits generate by TreeHandler.convertSharedTreeSubgraph and compare with number
            // of NA splits detected while walking the tree. Must be the same number.
            int nonNullNaSplits = 0;
            for (String naSplitDescription : treeProperties._nas) {
                if (naSplitDescription != null)
                    nonNullNaSplits++;

            }
            Assert.assertEquals(naSplits, nonNullNaSplits);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

            Scope.exit();
        }
    }

    @Test
    public void testNaHandling_cars() {
        Frame tfr = null;
        GBMModel model = null;
        Scope.enter();
        try {
            tfr = parse_test_file("./smalldata/junit/cars_nice_header.csv");
            DKV.put(tfr);
            GBMModel.GBMParameters parms = new GBMModel.GBMParameters();
            parms._train = tfr._key;
            parms._ignored_columns = new String[]{ "name", "economy", "displacement", "weight", "acceleration", "year" };// Cylinders-only

            parms._response_column = "power";// Regression

            parms._ntrees = 1;
            parms._seed = 0;
            model = trainModel().get();
            final SharedTreeModel.SharedTreeOutput sharedTreeOutput = model._output;
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._ntrees);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeys.length);
            Assert.assertEquals(parms._ntrees, sharedTreeOutput._treeKeysAux.length);
            final SharedTreeSubgraph sharedTreeSubgraph = model.getSharedTreeSubgraph(0, 0);
            Assert.assertNotNull(sharedTreeSubgraph);
            final TreeHandler.TreeProperties treeProperties = TreeHandler.convertSharedTreeSubgraph(sharedTreeSubgraph);
            Assert.assertNotNull(treeProperties);
            final SharedTreeNode rootNode = sharedTreeSubgraph.rootNode;
            final String[] noExcludedSplits = new String[]{  };
            final int naSplits = checkNaPath(rootNode, noExcludedSplits);
            // Count number of nonNull NA splits generate by TreeHandler.convertSharedTreeSubgraph and compare with number
            // of NA splits detected while walking the tree. Must be the same number.
            int nonNullNaSplits = 0;
            for (String naSplitDescription : treeProperties._nas) {
                if (naSplitDescription != null)
                    nonNullNaSplits++;

            }
            Assert.assertEquals(naSplits, nonNullNaSplits);
        } finally {
            if (tfr != null)
                tfr.remove();

            if (model != null)
                model.remove();

        }
    }
}

