package com.github.neuralnetworks.input;


import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainingInputData;
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
public class SimpleInputProviderTest extends AbstractTest {
    public SimpleInputProviderTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testSimpleInputProvider() {
        SimpleInputProvider sip = new SimpleInputProvider(new float[][]{ new float[]{ 1, 1, 1 }, new float[]{ 0, 0, 0 } }, new float[][]{ new float[]{ 1, 1 }, new float[]{ 0, 0 } });
        TrainingInputData ti = new com.github.neuralnetworks.training.TrainingInputDataImpl(TensorFactory.tensor(2, 3), TensorFactory.tensor(2, 2));
        sip.populateNext(ti);
        Assert.assertEquals(1, ti.getInput().get(0, 0), 0);
        Assert.assertEquals(1, ti.getInput().get(0, 1), 0);
        Assert.assertEquals(1, ti.getInput().get(0, 2), 0);
        Assert.assertEquals(0, ti.getInput().get(1, 0), 0);
        Assert.assertEquals(0, ti.getInput().get(1, 1), 0);
        Assert.assertEquals(0, ti.getInput().get(1, 2), 0);
        Assert.assertEquals(1, ti.getTarget().get(0, 0), 0);
        Assert.assertEquals(1, ti.getTarget().get(0, 1), 0);
        Assert.assertEquals(0, ti.getTarget().get(1, 0), 0);
        Assert.assertEquals(0, ti.getTarget().get(1, 1), 0);
    }
}

