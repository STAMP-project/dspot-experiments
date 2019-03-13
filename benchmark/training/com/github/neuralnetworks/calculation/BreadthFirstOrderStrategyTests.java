package com.github.neuralnetworks.calculation;


import LayerOrderStrategy.ConnectionCandidate;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.test.AbstractTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 02.12.14.
 */
public class BreadthFirstOrderStrategyTests extends AbstractTest {
    @Test
    public void testLayerOrderStrategy() {
        // MLP
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 3, 4, 5 }, true);
        List<LayerOrderStrategy.ConnectionCandidate> ccc = order();
        Assert.assertEquals(4, ccc.size(), 0);
        Layer l = mlp.getOutputLayer();
        Assert.assertTrue(((ccc.get(0).connection) == (l.getConnections().get(0))));
        Assert.assertTrue(((ccc.get(1).connection) == (l.getConnections().get(1))));
        l = l.getConnections().get(0).getInputLayer();
        Assert.assertTrue(((ccc.get(2).connection) == (l.getConnections().get(0))));
        Assert.assertTrue(((ccc.get(3).connection) == (l.getConnections().get(1))));
        // Simple MLP
        mlp = NNFactory.mlp(new int[]{ 3, 4 }, true);
        ccc = new BreadthFirstOrderStrategy(mlp, mlp.getOutputLayer()).order();
        Assert.assertEquals(2, ccc.size(), 0);
        l = mlp.getOutputLayer();
        Assert.assertTrue(((ccc.get(0).connection) == (l.getConnections().get(0))));
        Assert.assertTrue(((ccc.get(1).connection) == (l.getConnections().get(1))));
        // CNN
        NeuralNetworkImpl cnn = NNFactory.convNN(new int[][]{ new int[]{ 3, 3, 2 }, new int[]{ 2, 2, 1, 1, 1, 0, 0 } }, true);
        ccc = new BreadthFirstOrderStrategy(cnn, cnn.getOutputLayer()).order();
        l = cnn.getOutputLayer();
        Assert.assertEquals(2, ccc.size(), 0);
        Assert.assertTrue(((ccc.get(0).connection) == (l.getConnections().get(0))));
        Assert.assertTrue(((ccc.get(1).connection) == (l.getConnections().get(1))));
    }
}

