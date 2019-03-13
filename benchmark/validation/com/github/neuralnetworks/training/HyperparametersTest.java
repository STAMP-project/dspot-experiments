package com.github.neuralnetworks.training;


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
public class HyperparametersTest extends AbstractTest {
    public HyperparametersTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testHyperparameters() {
        Hyperparameters hp = new Hyperparameters();
        hp.setDefaultLearningRate(0.1F);
        Object o = new Object();
        hp.setLearningRate(o, 0.5F);
        Assert.assertEquals(0.1F, hp.getDefaultLearningRate(), 0);
        Assert.assertEquals(0.5F, hp.getLearningRate(o), 0);
        Assert.assertEquals(0.1F, hp.getLearningRate(new Object()), 0);
    }
}

