package com.github.neuralnetworks.samples.test;


import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.calculation.OutputError;
import com.github.neuralnetworks.calculation.operations.OperationsFactory;
import com.github.neuralnetworks.input.MultipleNeuronsSimpleOutputError;
import com.github.neuralnetworks.input.ScalingInputFunction;
import com.github.neuralnetworks.samples.iris.IrisInputProvider;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.events.LogTrainingListener;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Iris test
 */
// Iris input is not inplemented in Core, i.e. not necessary to test.
@RunWith(Parameterized.class)
@Ignore
@Deprecated
public class IrisTest {
    public IrisTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testMLPSigmoidBP() {
        // create the network
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 4, 2, 3 }, true);
        mlp.setLayerCalculator(CalculationFactory.lcSigmoid(mlp, OperationsFactory.softmaxFunction()));
        // training and testing data providers
        IrisInputProvider trainInputProvider = new IrisInputProvider();
        trainInputProvider.addInputModifier(new ScalingInputFunction());
        IrisInputProvider testInputProvider = new IrisInputProvider();
        testInputProvider.addInputModifier(new ScalingInputFunction());
        OutputError outputError = new MultipleNeuronsSimpleOutputError();
        // trainer
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(mlp, trainInputProvider, testInputProvider, outputError, new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl((-0.01F), 0.01F), 0.5F), 0.02F, 0.7F, 0.0F, 0.0F, 0.0F, 150, 1, 5000);
        // log data
        bpt.addEventListener(new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName()));
        // early stopping
        // bpt.addEventListener(new EarlyStoppingListener(testInputProvider, 100, 0.015f));
        // bpt.addEventListener(new FFNBPCalculationEventListener());
        // train
        bpt.train();
        // test
        bpt.test();
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    @Test
    public void testMLPReLUBP() {
        // create the network
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 4, 10, 10, 3 }, true);
        mlp.setLayerCalculator(CalculationFactory.lcRelu(mlp, OperationsFactory.softmaxFunction()));
        // training and testing data providers
        IrisInputProvider trainInputProvider = new IrisInputProvider();
        trainInputProvider.addInputModifier(new ScalingInputFunction());
        IrisInputProvider testInputProvider = new IrisInputProvider();
        testInputProvider.addInputModifier(new ScalingInputFunction());
        OutputError outputError = new MultipleNeuronsSimpleOutputError();
        // trainer
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(mlp, trainInputProvider, testInputProvider, outputError, new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl((-0.01F), 0.01F), 0.5F), 0.02F, 0.7F, 0.0F, 0.0F, 0.0F, 150, 1, 10000);
        // log data
        LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName());
        ls.setLogEpochs(false);
        bpt.addEventListener(ls);
        // early stopping
        // bpt.addEventListener(new EarlyStoppingListener(testInputProvider, 100, 0.015f));
        // bpt.addEventListener(new FFNBPCalculationEventListener());
        // train
        bpt.train();
        // test
        bpt.test();
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }
}

