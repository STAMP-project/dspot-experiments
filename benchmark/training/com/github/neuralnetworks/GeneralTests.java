package com.github.neuralnetworks;


import com.github.neuralnetworks.architecture.FullyConnected;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.calculation.LayerCalculatorImpl;
import com.github.neuralnetworks.calculation.operations.ConnectionCalculatorTensorFunctions;
import com.github.neuralnetworks.calculation.operations.OperationsFactory;
import com.github.neuralnetworks.input.SimpleInputProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.backpropagation.BackPropagationLayerCalculatorImpl;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 02.12.14.
 * tests from ivans test classes which could not be assigned to any specific test class
 */
@RunWith(Parameterized.class)
public class GeneralTests extends AbstractTest {
    public GeneralTests(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testDropoutConstruction() {
        NeuralNetworkImpl mlp = CalculationFactory.mlpSigmoid(new int[]{ 2, 2, 1 }, false);
        new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl((-0.5F), 0.5F)).initialize(mlp);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(mlp, new SimpleInputProvider(new float[][]{ new float[]{ 0.0F, 0.0F } }, new float[][]{ new float[]{ 0.0F } }), null, null, null, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 1, 1, 1);
        LayerCalculatorImpl lc = ((LayerCalculatorImpl) (mlp.getLayerCalculator()));
        Layer hidden = mlp.getLayers().stream().filter(( l) -> (l != (mlp.getInputLayer())) && (l != (mlp.getOutputLayer()))).findFirst().get();
        Assert.assertTrue(OperationsFactory.hasDropout(lc.getConnectionCalculator(hidden)));
        FullyConnected fc = ((FullyConnected) (mlp.getOutputLayer().getConnections().get(0)));
        float w = fc.getWeights().get(0, 0);
        BackPropagationLayerCalculatorImpl bplc = ((BackPropagationLayerCalculatorImpl) (bpt.getBPLayerCalculator()));
        ConnectionCalculatorTensorFunctions tf = ((ConnectionCalculatorTensorFunctions) (bplc.getConnectionCalculator(hidden)));
        Assert.assertTrue(tf.getActivationFunctions().stream().filter(( f) -> OperationsFactory.isMask(f)).findAny().isPresent());
        bpt.train();
        Assert.assertTrue((!(OperationsFactory.hasDropout(lc.getConnectionCalculator(hidden)))));
        Assert.assertEquals((w / 2), fc.getWeights().get(0, 0), 0);
    }
}

