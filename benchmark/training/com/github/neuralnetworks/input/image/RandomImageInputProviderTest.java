package com.github.neuralnetworks.input.image;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 02.12.14.
 */
@RunWith(Parameterized.class)
public class RandomImageInputProviderTest extends AbstractTest {
    public RandomImageInputProviderTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testImageInputProvider() {
        RandomImageInputProvider ri = new RandomImageInputProvider(16, 256, 256, 150528, 100);
        ri.getProperties().setAugmentedImagesBufferSize(1024);
        ri.getProperties().setImagesBulkSize(32);
        ri.getProperties().setGroupByChannel(true);
        ri.getProperties().setSubtractMean(true);
        ri.getProperties().setParallelPreprocessing(true);
        ri.getProperties().setScaleColors(true);
        CompositeAugmentStrategy cas = new CompositeAugmentStrategy();
        cas.addStrategy(new VerticalFlipAugmentStrategy());
        cas.addStrategy(new Point5CropAugmentStrategy(224, 224));
        ri.getProperties().setAugmentStrategy(cas);
        Tensor t = TensorFactory.tensor(32, 224, 224, 3);
        ri.getNextInput(t);
        ri.getNextInput(t);
        synchronized(ri.getLock()) {
            Assert.assertEquals(0, ((new java.util.HashSet(ri.getAugmentedToRaw().values()).size()) % 16), 0);
        }
        if (ri.getProperties().getSubtractMean()) {
            t.forEach(( i) -> assertTrue((((t.getElements()[i]) >= (-1)) && ((t.getElements()[i]) <= 1))));
        } else {
            t.forEach(( i) -> assertTrue((((t.getElements()[i]) >= 0) && ((t.getElements()[i]) <= 1))));
        }
        for (float[] arr : ri.getValues()) {
            Assert.assertEquals(150528, arr.length);
            for (float f : arr) {
                if (ri.getProperties().getSubtractMean()) {
                    Assert.assertTrue(((f >= (-1)) && (f <= 1)));
                } else {
                    Assert.assertTrue(((f >= 0) && (f <= 1)));
                }
            }
        }
    }
}

