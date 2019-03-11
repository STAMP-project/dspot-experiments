package com.github.neuralnetworks.calculation.operations;


import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.backpropagation.LossFunction;
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
public class SoftmaxLossTest extends AbstractTest {
    public SoftmaxLossTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testSoftmaxLoss() {
        Tensor activation = TensorFactory.tensor(2, 2);
        Tensor target = TensorFactory.tensor(2, 2);
        Tensor result = TensorFactory.tensor(2, 2);
        activation.set(2.0F, 0, 0);
        activation.set(4.0F, 0, 1);
        activation.set(6.0F, 1, 0);
        activation.set(8.0F, 1, 1);
        target.set(1.0F, 0, 0);
        target.set(2.0F, 0, 1);
        target.set(3.0F, 1, 0);
        target.set(4.0F, 1, 1);
        LossFunction lf = OperationsFactory.softmaxLoss();
        lf.getLossFunctionDerivative(activation, target, result);
        Assert.assertEquals((-1), result.get(0, 0), 0);
        Assert.assertEquals((-2), result.get(0, 1), 0);
        Assert.assertEquals((-3), result.get(1, 0), 0);
        Assert.assertEquals((-4), result.get(1, 1), 0);
    }

    @Test
    public void testSoftNegativeLogProbabilty() {
        Tensor activation = TensorFactory.tensor(2, 2);
        activation.set(0.99923074F, 0, 0);
        activation.set(7.692802E-4F, 0, 1);
        activation.set(0.99923074F, 1, 0);
        activation.set(7.692802E-4F, 1, 1);
        Tensor target = TensorFactory.tensor(2, 2);
        target.set(1, 0, 0);
        target.set(0, 0, 1);
        target.set(1, 1, 0);
        target.set(0, 1, 1);
        LossFunction sf = OperationsFactory.softmaxLoss();
        float error = sf.getLossFunction(activation, target);
        Assert.assertEquals(7.695762213886436E-4, (error / 2), 1.0E-6);
    }
}

