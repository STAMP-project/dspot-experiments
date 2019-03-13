package com.github.neuralnetworks.training.random;


import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import com.github.neuralnetworks.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 02.12.14.
 */
@RunWith(Parameterized.class)
public class NNRandomInitializerTest extends AbstractTest {
    public NNRandomInitializerTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testRandomInitializer() {
        NeuralNetworkImpl nn = NNFactory.mlp(new int[]{ 3, 2 }, true);
        NNRandomInitializer rand = new NNRandomInitializer(new MersenneTwisterRandomInitializer((-0.1F), 0.1F), 0.5F);
        rand.initialize(nn);
        for (Layer l : nn.getLayers()) {
            if (Util.isBias(l)) {
                Tensor t = getWeights();
                float[] elements = t.getElements();
                t.forEach(( i) -> assertEquals(0.5, elements[i], 0.0F));
            } else {
                Tensor t = getWeights();
                float[] elements = t.getElements();
                t.forEach(( i) -> assertTrue(((((elements[i]) >= (-0.1F)) && ((elements[i]) <= 0.1F)) && ((elements[i]) != 0))));
            }
        }
    }

    @Test
    public void testRandomInitializer1() {
        NeuralNetworkImpl nn = NNFactory.mlp(new int[]{ 3, 2 }, true);
        NNRandomInitializer rand = new NNRandomInitializer(new MersenneTwisterRandomInitializer(2.0F, 3.0F), new MersenneTwisterRandomInitializer((-2.0F), (-1.0F)));
        rand.initialize(nn);
        for (Layer l : nn.getLayers()) {
            if (Util.isBias(l)) {
                Tensor t = getWeights();
                float[] elements = t.getElements();
                t.forEach(( i) -> assertTrue((((elements[i]) >= (-2.0F)) && ((elements[i]) <= (-1.0F)))));
            } else {
                Tensor t = getWeights();
                float[] elements = t.getElements();
                t.forEach(( i) -> assertTrue((((elements[i]) >= 2.0F) && ((elements[i]) <= 3.0F))));
            }
        }
    }
}

