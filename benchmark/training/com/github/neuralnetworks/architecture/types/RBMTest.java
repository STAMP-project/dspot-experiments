package com.github.neuralnetworks.architecture.types;


import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.input.SimpleInputProvider;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.tensor.ValuesProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.rbm.CDTrainerBase;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RBMTest extends AbstractTest {
    public RBMTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testRBMLayerCalculator1() {
        RBM rbm = NNFactory.rbm(2, 2, false);
        rbm.setLayerCalculator(CalculationFactory.lcSigmoid(rbm, null));
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.1F, 0, 0);
        cg1.set(0.8F, 0, 1);
        cg1.set(0.4F, 1, 0);
        cg1.set(0.6F, 1, 1);
        ValuesProvider vp = TensorFactory.tensorProvider(rbm, 1, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix visible = vp.get(rbm.getVisibleLayer());
        visible.set(0.35F, 0, 0);
        visible.set(0.9F, 0, 1);
        Set<Layer> calculated = new HashSet<Layer>();
        calculated.add(rbm.getVisibleLayer());
        rbm.getLayerCalculator().calculate(rbm, rbm.getHiddenLayer(), calculated, vp);
        Matrix hidden = vp.get(rbm.getHiddenLayer());
        Assert.assertEquals(0.68, hidden.get(0, 0), 0.01);
        Assert.assertEquals(0.6637, hidden.get(0, 1), 0.01);
    }

    @Test
    public void testRBMLayerCalculator2() {
        RBM rbm = NNFactory.rbm(2, 2, false);
        rbm.setLayerCalculator(CalculationFactory.lcSigmoid(rbm, null));
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.1F, 0, 0);
        cg1.set(0.8F, 1, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set(0.6F, 1, 1);
        ValuesProvider vp = TensorFactory.tensorProvider(rbm, 1, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix hidden = vp.get(rbm.getHiddenLayer());
        hidden.set(0.35F, 0, 0);
        hidden.set(0.9F, 0, 1);
        Set<Layer> calculated = new HashSet<Layer>();
        calculated.add(rbm.getHiddenLayer());
        rbm.getLayerCalculator().calculate(rbm, rbm.getVisibleLayer(), calculated, vp);
        Matrix visible = vp.get(rbm.getVisibleLayer());
        Assert.assertEquals(0.68, visible.get(0, 0), 0.01);
        Assert.assertEquals(0.6637, visible.get(0, 1), 0.01);
    }

    @Test
    public void testRBMLayerCalculator3() {
        RBM rbm = NNFactory.rbm(3, 2, true);
        rbm.setLayerCalculator(CalculationFactory.lcSigmoid(rbm, null));
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set((-0.5F), 0, 2);
        cg1.set((-0.3F), 1, 0);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 1, 2);
        Matrix cgb1 = rbm.getHiddenBiasConnections().getWeights();
        cgb1.set((-0.4F), 0, 0);
        cgb1.set(0.2F, 1, 0);
        ValuesProvider vp = TensorFactory.tensorProvider(rbm, 1, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix visible = vp.get(rbm.getVisibleLayer());
        visible.set(1.0F, 0, 0);
        visible.set(0.0F, 0, 1);
        visible.set(1.0F, 0, 2);
        Matrix hiddenBias = vp.get(rbm.getHiddenBiasConnections().getInputLayer());
        hiddenBias.set(1, 0);
        Set<Layer> calculated = new HashSet<Layer>();
        calculated.add(rbm.getVisibleLayer());
        rbm.getLayerCalculator().calculate(rbm, rbm.getHiddenLayer(), calculated, vp);
        Matrix hidden = vp.get(rbm.getHiddenLayer());
        Assert.assertEquals(0.332, hidden.get(0, 0), 0.001);
        Assert.assertEquals(0.525, hidden.get(0, 1), 0.001);
    }

    @Test
    public void testRBMLayerCalculator4() {
        RBM rbm = NNFactory.rbm(2, 3, true);
        rbm.setLayerCalculator(CalculationFactory.lcSigmoid(rbm, null));
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 1, 0);
        cg1.set((-0.5F), 2, 0);
        cg1.set((-0.3F), 0, 1);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 2, 1);
        Matrix cgb1 = rbm.getVisibleBiasConnections().getWeights();
        cgb1.set((-0.4F), 0, 0);
        cgb1.set(0.2F, 1, 0);
        ValuesProvider vp = TensorFactory.tensorProvider(rbm, 1, Environment.getInstance().getRuntimeConfiguration().getUseDataSharedMemory());
        Matrix hidden = vp.get(rbm.getHiddenLayer());
        hidden.set(1.0F, 0, 0);
        hidden.set(0.0F, 0, 1);
        hidden.set(1.0F, 0, 2);
        Set<Layer> calculated = new HashSet<Layer>();
        calculated.add(rbm.getHiddenLayer());
        rbm.getLayerCalculator().calculate(rbm, rbm.getVisibleLayer(), calculated, vp);
        Matrix visible = vp.get(rbm.getVisibleLayer());
        Assert.assertEquals(0.332, visible.get(0, 0), 0.001);
        Assert.assertEquals(0.525, visible.get(0, 1), 0.001);
    }

    @Test
    public void testOneStepContrastiveDivergence() {
        RBM rbm = NNFactory.rbm(3, 2, true);
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set((-0.5F), 0, 2);
        cg1.set((-0.3F), 1, 0);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 1, 2);
        Matrix cgb1 = rbm.getVisibleBiasConnections().getWeights();
        cgb1.set(0.0F, 0, 0);
        cgb1.set(0.0F, 1, 0);
        cgb1.set(0.0F, 2, 0);
        Matrix cgb2 = rbm.getHiddenBiasConnections().getWeights();
        cgb2.set((-0.4F), 0, 0);
        cgb2.set(0.2F, 1, 0);
        CDTrainerBase t = TrainerFactory.cdSigmoidTrainer(rbm, new SimpleInputProvider(new float[][]{ new float[]{ 1, 0, 1 } }), null, null, null, 1.0F, 0.0F, 0.0F, 0.0F, 1, 1, 1, true);
        t.train();
        Assert.assertEquals(0.52276707, cgb1.get(0, 0), 1.0E-5);
        Assert.assertEquals((-0.54617375), cgb1.get(1, 0), 1.0E-5);
        Assert.assertEquals(0.51522285, cgb1.get(2, 0), 1.0E-5);
        Assert.assertEquals(((-0.4) - 0.08680013), cgb2.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.2 - 0.02693379), cgb2.get(1, 0), 1.0E-5);
        Assert.assertEquals((0.2 + 0.13203661), cg1.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.4 - 0.22863509), cg1.get(0, 1), 1.0E-5);
        Assert.assertEquals(((-0.5) + 0.12887852), cg1.get(0, 2), 1.0E-5);
        Assert.assertEquals(((-0.3) + 0.26158813), cg1.get(1, 0), 1.0E-5);
        Assert.assertEquals((0.1 - 0.3014404), cg1.get(1, 1), 1.0E-5);
        Assert.assertEquals((0.2 + 0.25742438), cg1.get(1, 2), 1.0E-5);
    }

    @Test
    public void testTwoStepContrastiveDivergence() {
        RBM rbm = NNFactory.rbm(3, 2, true);
        Matrix cg1 = rbm.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set((-0.5F), 0, 2);
        cg1.set((-0.3F), 1, 0);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 1, 2);
        Matrix cgb1 = rbm.getVisibleBiasConnections().getWeights();
        cgb1.set(0.0F, 0, 0);
        cgb1.set(0.0F, 1, 0);
        cgb1.set(0.0F, 2, 0);
        Matrix cgb2 = rbm.getHiddenBiasConnections().getWeights();
        cgb2.set((-0.4F), 0, 0);
        cgb2.set(0.2F, 1, 0);
        CDTrainerBase t = TrainerFactory.cdSigmoidTrainer(rbm, new SimpleInputProvider(new float[][]{ new float[]{ 1, 0, 1 }, new float[]{ 1, 1, 0 } }), null, null, null, 1.0F, 0.0F, 0.0F, 0.0F, 1, 1, 1, false);
        t.train();
        Assert.assertEquals(0.86090606, cgb1.get(0, 0), 1.0E-5);
        Assert.assertEquals(0.089616358, cgb1.get(1, 0), 1.0E-5);
        Assert.assertEquals((-0.11872697), cgb1.get(2, 0), 1.0E-5);
        Assert.assertEquals((-0.3744152), cgb2.get(0, 0), 1.0E-5);
        Assert.assertEquals(0.0663045, cgb2.get(1, 0), 1.0E-5);
        Assert.assertEquals(0.5768927, cg1.get(0, 0), 1.0E-5);
        Assert.assertEquals(0.5328304, cg1.get(0, 1), 1.0E-5);
        Assert.assertEquals((-0.619481), cg1.get(0, 2), 1.0E-5);
        Assert.assertEquals(0.0543526, cg1.get(1, 0), 1.0E-5);
        Assert.assertEquals(0.0669599, cg1.get(1, 1), 1.0E-5);
        Assert.assertEquals(0.0833487, cg1.get(1, 2), 1.0E-5);
    }
}

