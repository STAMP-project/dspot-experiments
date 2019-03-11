package com.github.neuralnetworks.architecture;


import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by chass on 02.12.14.
 */
public class NeuralNetworkImplTest extends AbstractTest {
    @Test
    public void testRemoveLayer() {
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 3, 4, 5 }, true);
        Assert.assertEquals(5, mlp.getLayers().size(), 0);
        Layer currentOutput = mlp.getOutputLayer();
        mlp.removeLayer(mlp.getOutputLayer());
        Assert.assertEquals(3, mlp.getLayers().size(), 0);
        Assert.assertEquals(true, (currentOutput != (mlp.getOutputLayer())));
    }
}

