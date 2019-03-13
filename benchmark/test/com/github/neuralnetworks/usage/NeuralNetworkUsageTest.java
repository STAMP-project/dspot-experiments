package com.github.neuralnetworks.usage;


import ActivationType.Nothing;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class NeuralNetworkUsageTest {
    public NeuralNetworkUsageTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testClassifyVector() throws Exception {
        // create a network
        NeuralNetworkBuilder neuralNetworkBuilder = new NeuralNetworkBuilder();
        neuralNetworkBuilder.addLayerBuilder(new InputLayerBuilder("input", 2, 2, 1));
        FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(2);
        fullyConnectedLayerBuilder.setAddBias(false);
        fullyConnectedLayerBuilder.setActivationType(Nothing);
        neuralNetworkBuilder.addLayerBuilder(fullyConnectedLayerBuilder);
        NeuralNetworkImpl neuralNetwork = neuralNetworkBuilder.build();
        for (Layer layer : neuralNetwork.getLayers()) {
            getWeights().setElements(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        }
        // create a test vector
        float[] testVector = new float[]{ 1, 2, 3, 4 };
        // test
        NeuralNetworkUsage neuralNetworkUsage = new NeuralNetworkUsage(neuralNetwork);
        float[] target = neuralNetworkUsage.classifyVector(testVector);
        Assert.assertArrayEquals("The output of the neural network isn't correct!", new float[]{ 30, 70 }, target, 0);
    }
}

