package com.github.neuralnetworks.training.backpropagation;


import com.github.neuralnetworks.architecture.FullyConnected;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.Subsampling2DConnection;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.calculation.LayerCalculatorImpl;
import com.github.neuralnetworks.calculation.operations.OperationsFactory;
import com.github.neuralnetworks.input.MultipleNeuronsOutputError;
import com.github.neuralnetworks.input.SimpleInputProvider;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class GradientCheckTest extends AbstractTest {
    // public GradientCheckTest(Runtime conf)
    // {
    // configureGlobalRuntimeEnvironment(conf);
    // }
    public GradientCheckTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testGradCheck0() {
        NeuralNetworkImpl nn = CalculationFactory.mlpSigmoid(new int[]{ 2, 1 }, true);
        SimpleInputProvider inputProvider = new SimpleInputProvider(new float[][]{ new float[]{ 0.9501293F, 0.23113851F } }, new float[][]{ new float[]{ 0.6068426F } });
        FullyConnected fc = ((FullyConnected) (nn.getInputLayer().getConnections().get(0)));
        fc.getWeights().set(2.9652872F, 0, 0);
        fc.getWeights().set((-0.49251214F), 0, 1);
        FullyConnected b = ((FullyConnected) (nn.getOutputLayer().getConnections().get(1)));
        b.getWeights().set(4.4270425F, 0);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck1() {
        NeuralNetworkImpl nn = CalculationFactory.mlpSigmoid(new int[]{ 2, 3, 4, 3 }, true);
        Random r = new Random();
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        if ((conf.getRandomSeed()) != (-1)) {
            r.setSeed(conf.getRandomSeed());
        }
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(r, (-0.5F), 0.5F)).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 1.0E-5F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck2() {
        NeuralNetworkImpl nn = NNFactory.mlp(new int[]{ 2, 2 }, true);
        nn.setLayerCalculator(CalculationFactory.lcSigmoid(nn, OperationsFactory.softmaxFunction()));
        SimpleInputProvider inputProvider = new SimpleInputProvider(new float[][]{ new float[]{ 0.9501293F, 0.23113851F } }, new float[][]{ new float[]{ 1, 0 } });
        FullyConnected fc = ((FullyConnected) (nn.getInputLayer().getConnections().get(0)));
        Matrix w1 = fc.getWeights();
        w1.set((-4.7176814F), 0, 0);
        w1.set((-0.5417942F), 0, 1);
        w1.set(3.1491342F, 1, 0);
        w1.set(1.1310014F, 1, 1);
        FullyConnected bc = ((FullyConnected) (nn.getOutputLayer().getConnections().get(1)));
        Matrix wb = bc.getWeights();
        wb.set(2.568014F, 0, 0);
        wb.set((-0.42652804F), 1, 0);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 0.001);
    }

    @Test
    public void testGradCheck3() {
        NeuralNetworkImpl nn = NNFactory.mlp(new int[]{ 2, 3, 2 }, true);
        nn.setLayerCalculator(CalculationFactory.lcSigmoid(nn, OperationsFactory.softmaxFunction()));
        Random r = new Random();
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        if ((conf.getRandomSeed()) != (-1)) {
            r.setSeed(conf.getRandomSeed());
        }
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(r, (-0.5F), 0.5F)).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck4() {
        NeuralNetworkImpl nn = NNFactory.mlp(new int[]{ 2, 4, 2 }, true);
        nn.setLayerCalculator(CalculationFactory.lcWeightedSum(nn, OperationsFactory.softmaxFunction()));
        Random r = new Random();
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        if ((conf.getRandomSeed()) != (-1)) {
            r.setSeed(conf.getRandomSeed());
        }
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(r, 0.0F, 0.1F)).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck5() {
        NeuralNetworkImpl nn = NNFactory.convNN(new int[][]{ new int[]{ 6, 6, 2 }, new int[]{ 2, 2, 2, 1, 1, 0, 0 }, new int[]{ 2, 2, 1, 1, 0, 0 }, new int[]{ 6 }, new int[]{ 2 } }, true);
        nn.setLayerCalculator(CalculationFactory.lcSigmoid(nn, null));
        CalculationFactory.lcMaxPooling(nn);
        Random r = new Random();
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        if ((conf.getRandomSeed()) != (-1)) {
            r.setSeed(conf.getRandomSeed());
        }
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(r, (-0.5F), 0.5F)).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck6() {
        NeuralNetworkImpl nn = NNFactory.convNN(new int[][]{ new int[]{ 6, 6, 2 }, new int[]{ 2, 2, 2, 1, 1, 0, 0 }, new int[]{ 2, 2, 1, 1, 0, 0 }, new int[]{ 8 }, new int[]{ 2 } }, true);
        nn.setLayerCalculator(CalculationFactory.lcSigmoid(nn, null));
        CalculationFactory.lcAveragePooling(nn);
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        new com.github.neuralnetworks.training.random.NNRandomInitializer(((conf.getRandomSeed()) == (-1) ? new RandomInitializerImpl((-0.5F), 0.5F) : new RandomInitializerImpl((-0.5F), 0.5F, conf.randomSeed))).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheck7() {
        NeuralNetworkImpl nn = NNFactory.convNN(new int[][]{ new int[]{ 8, 8, 2 }, new int[]{ 2, 2, 2, 1, 1, 0, 0 }, new int[]{ 2, 2, 1, 1, 0, 0 }, new int[]{ 2, 2, 2, 1, 1, 0, 0 }, new int[]{ 8 }, new int[]{ 2 } }, true);
        nn.setLayerCalculator(CalculationFactory.lcRelu(nn, OperationsFactory.softmaxFunction()));
        CalculationFactory.lcMaxPooling(nn);
        GradientCheckTest.GCRuntimeConfiguration conf = ((GradientCheckTest.GCRuntimeConfiguration) (Environment.getInstance().getRuntimeConfiguration()));
        new com.github.neuralnetworks.training.random.NNRandomInitializer(((conf.getRandomSeed()) == (-1) ? new RandomInitializerImpl(0.0F, 0.09F) : new RandomInitializerImpl(0.0F, 0.09F, conf.getRandomSeed()))).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 1.0E-4);
    }

    @Test
    public void testGradCheckLRN() {
        NeuralNetworkImpl nn = NNFactory.convNN(new int[][]{ new int[]{ 6, 6, 2 }, new int[]{ 2, 2, 2, 1, 1, 0, 0 }, new int[]{ 2, 2, 1, 1, 0, 0 }, new int[]{ 8 }, new int[]{ 2 } }, true);
        nn.setLayerCalculator(CalculationFactory.lcSigmoid(nn, null));
        CalculationFactory.lcMaxPooling(nn);
        Layer convLayer = nn.getInputLayer().getConnections().get(0).getOutputLayer();
        Subsampling2DConnection cs = ((Subsampling2DConnection) (convLayer.getConnections().get(2)));
        convLayer.getConnections().remove(cs);
        Layer lrnLayer = new Layer();
        cs.setInputLayer(lrnLayer);
        new com.github.neuralnetworks.architecture.RepeaterConnection(convLayer, lrnLayer, convLayer.getUnitCount(convLayer.getConnections()));
        nn.addLayer(lrnLayer);
        LayerCalculatorImpl lc = ((LayerCalculatorImpl) (nn.getLayerCalculator()));
        lc.addConnectionCalculator(lrnLayer, OperationsFactory.lrnConnectionCalculator(2, 5, 1.0E-4F, 0.75F));
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl((-0.2F), 0.2F)).initialize(nn);
        SimpleInputProvider inputProvider = randomInputProvider(nn, true);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(nn, inputProvider, null, new MultipleNeuronsOutputError(), null, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, inputProvider.getInput().length, inputProvider.getInput().length, 1);
        GradientCheck gc = new GradientCheck(bpt, 0.001F);
        try {
            gc.compute();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(((gc.getMaxDelta()) != 0));
        Assert.assertTrue(((gc.getMaxDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinDelta()) != 0));
        Assert.assertTrue(((gc.getMinDelta()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMaxGradient()) != 0));
        Assert.assertTrue(((gc.getMaxGradient()) != (Float.NaN)));
        Assert.assertTrue(((gc.getMinGradient()) != 0));
        Assert.assertTrue(((gc.getMinGradient()) != (Float.NaN)));
        Assert.assertEquals(0, ((gc.getMaxDelta()) / (inputProvider.getInput().length)), 0.001);
    }

    private static class GCRuntimeConfiguration extends RuntimeConfiguration {
        private static final long serialVersionUID = 1L;

        private long randomSeed = -1;

        public long getRandomSeed() {
            return randomSeed;
        }

        public void setRandomSeed(long randomSeed) {
            this.randomSeed = randomSeed;
        }
    }
}

