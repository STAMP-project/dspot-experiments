package com.github.neuralnetworks.input;


import com.github.neuralnetworks.tensor.Matrix;
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
public class ScalingInputFunctionTest extends AbstractTest {
    public ScalingInputFunctionTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testScaling() {
        float[][] input = new float[][]{ new float[]{ 1, 3 }, new float[]{ -1.5F, 1.5F } };
        ScalingInputFunction si = new ScalingInputFunction();
        Matrix m = TensorFactory.matrix(input);
        si.value(m);
        Assert.assertEquals(0.0F, m.get(0, 0), 0);
        Assert.assertEquals(1.0F, m.get(0, 1), 0);
        Assert.assertEquals(0.0F, m.get(1, 0), 0);
        Assert.assertEquals(1.0F, m.get(1, 1), 0);
    }
}

