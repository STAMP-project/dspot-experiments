package hex.tree;


import CompressedTree.TreeCoords;
import hex.genmodel.algos.gbm.GbmMojoModel;
import hex.genmodel.algos.tree.SharedTreeGraph;
import hex.genmodel.algos.tree.SharedTreeMojoModel;
import hex.genmodel.algos.tree.SharedTreeNode;
import hex.genmodel.algos.tree.SharedTreeSubgraph;
import hex.tree.gbm.GBMModel;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Scope;
import water.TestUtil;


public class CompressedTreeTest extends TestUtil {
    @Test
    public void testToSharedTreeSubgraph() throws IOException {
        int ntrees = 5;
        try {
            Scope.enter();
            GBMModel model = trainGbm(ntrees);
            GbmMojoModel mojo = ((GbmMojoModel) (model.toMojo()));
            SharedTreeGraph expectedGraph = mojo._computeGraph((-1));
            Assert.assertEquals(5, expectedGraph.subgraphArray.size());// sanity check the MOJO created graph

            for (int i = 0; i < ntrees; i++) {
                CompressedTree tree = model._output._treeKeys[i][0].get();
                Assert.assertNotNull(tree);
                CompressedTree auxTreeInfo = model._output._treeKeysAux[i][0].get();
                SharedTreeSubgraph sg = tree.toSharedTreeSubgraph(auxTreeInfo, model._output._names, model._output._domains);
                Assert.assertEquals(expectedGraph.subgraphArray.get(i), sg);
            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testNodeIdAssignment() throws IOException {
        final int ntrees = 5;
        try {
            Scope.enter();
            GBMModel model = trainGbm(ntrees);
            GbmMojoModel mojo = ((GbmMojoModel) (model.toMojo()));
            SharedTreeGraph expectedGraph = mojo._computeGraph((-1));
            Assert.assertEquals(5, expectedGraph.subgraphArray.size());// sanity check the MOJO created graph

            double[][] data = CompressedTreeTest.frameToMatrix(CompressedTreeTest.getAdaptedTrainFrame(model));
            for (int i = 0; i < ntrees; i++) {
                CompressedTree tree = model._output._treeKeys[i][0].get();
                CompressedTree auxTreeInfo = model._output._treeKeysAux[i][0].get();
                Assert.assertNotNull(tree);
                Assert.assertNotNull(auxTreeInfo);
                final SharedTreeSubgraph sg = tree.toSharedTreeSubgraph(auxTreeInfo, model._output._names, model._output._domains);
                for (double[] row : data) {
                    final double leafAssignment = SharedTreeMojoModel.scoreTree(tree._bits, row, true, model._output._domains);
                    final String nodePath = SharedTreeMojoModel.getDecisionPath(leafAssignment);
                    final int nodeId = SharedTreeMojoModel.getLeafNodeId(leafAssignment, auxTreeInfo._bits);
                    SharedTreeNode n = sg.rootNode;
                    for (int j = 0; j < (nodePath.length()); j++) {
                        n = ((nodePath.charAt(j)) == 'L') ? n.getLeftChild() : n.getRightChild();
                    }
                    Assert.assertNull(n.getLeftChild());
                    Assert.assertNull(n.getRightChild());
                    Assert.assertEquals(((("Path " + nodePath) + " in tree #") + i), n.getNodeNumber(), nodeId);
                }
            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testMakeTreeKey() {
        try {
            Scope.enter();
            CompressedTree ct = new CompressedTree(new byte[0], 123, 42, 17);
            Scope.track_generic(ct);
            DKV.put(ct);
            CompressedTree.TreeCoords tc = ct.getTreeCoords();
            Assert.assertEquals(42, tc._treeId);
            Assert.assertEquals(17, tc._clazz);
        } finally {
            Scope.exit();
        }
    }
}

