package com.github.neuralnetworks.calculation.operations;


import Constants.WEIGHTS_PROVIDER;
import com.github.neuralnetworks.architecture.Connections;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetwork;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.calculation.ConnectionCalculator;
import com.github.neuralnetworks.calculation.LayerCalculatorImpl;
import com.github.neuralnetworks.calculation.operations.opencl.OpenCLArrayReference;
import com.github.neuralnetworks.calculation.operations.opencl.OpenCLArrayReferenceManager;
import com.github.neuralnetworks.calculation.operations.opencl.OpenCLCore;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.Tensor.TensorIterator;
import com.github.neuralnetworks.tensor.ValuesProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.Trainer;
import com.github.neuralnetworks.training.backpropagation.BackPropagationConnectionCalculatorImpl;
import com.github.neuralnetworks.training.backpropagation.BackPropagationLayerCalculatorImpl;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.backpropagation.WeightUpdates;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.Pair;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import com.github.neuralnetworks.util.RuntimeConfiguration.CalculationProvider;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class BPCalculationProvidersComparisonTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(BPCalculationProvidersComparisonTest.class);

    private BPCalculationProvidersComparisonTest.TestConfiguration conf;

    public BPCalculationProvidersComparisonTest(BPCalculationProvidersComparisonTest.TestConfiguration conf) {
        this.conf = conf;
    }

    @Test
    public void compare() {
        // obtain values with conf1;
        Environment.getInstance().setRuntimeConfiguration(conf.conf1);
        Pair<NeuralNetworkImpl, Trainer<NeuralNetwork>> neuralNetworkTrainerPair1 = conf.builder.buildWithTrainer();
        BackPropagationTrainer<?> trainer1 = ((BackPropagationTrainer<?>) (neuralNetworkTrainerPair1.getRight()));
        trainer1.setRandomInitializer(new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(new Random(conf.randomSeed), (-0.01F), 0.01F)));
        trainer1.train();
        ValuesProvider conf1Activations = trainer1.getActivations();
        ValuesProvider conf1Backpropagation = trainer1.getBackpropagation();
        ValuesProvider conf1Weights = neuralNetworkTrainerPair1.getLeft().getProperties().getParameter(WEIGHTS_PROVIDER);
        // obtain values with conf2;
        Environment.getInstance().setRuntimeConfiguration(conf.conf2);
        Pair<NeuralNetworkImpl, Trainer<NeuralNetwork>> neuralNetworkTrainerPair2 = conf.builder.buildWithTrainer();
        BackPropagationTrainer<?> trainer2 = ((BackPropagationTrainer<?>) (neuralNetworkTrainerPair2.getRight()));
        trainer2.setRandomInitializer(new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(new Random(conf.randomSeed), (-0.01F), 0.01F)));
        trainer2.train();
        ValuesProvider conf2Activations = trainer2.getActivations();
        ValuesProvider conf2Backpropagation = trainer2.getBackpropagation();
        ValuesProvider conf2Weights = neuralNetworkTrainerPair2.getLeft().getProperties().getParameter(WEIGHTS_PROVIDER);
        // compare activations
        Iterator<Tensor> ita1 = conf1Activations.getTensors().iterator();
        Iterator<Tensor> ita2 = conf2Activations.getTensors().iterator();
        while ((ita1.hasNext()) && (ita2.hasNext())) {
            Tensor t1 = ita1.next();
            Tensor t2 = ita2.next();
            TensorIterator t1it = t1.iterator();
            TensorIterator t2it = t2.iterator();
            while ((t1it.hasNext()) && (t2it.hasNext())) {
                int t1index = t1it.next();
                int t2index = t2it.next();
                Assert.assertTrue(((t1.getElements()) != (t2.getElements())));
                Assert.assertTrue(((t1.getElements()[t1index]) != (Float.NaN)));
                Assert.assertTrue(((t2.getElements()[t2index]) != (Float.NaN)));
                Assert.assertTrue(((((t1.getElements()[t1index]) != 0) && ((t2.getElements()[t2index]) != 0)) || (((t1.getElements()[t1index]) == 0) && ((t2.getElements()[t2index]) == 0))));
                try {
                    Assert.assertEquals(0, Math.abs(((t1.getElements()[t1index]) - (t2.getElements()[t2index]))), 1.0E-6F);
                } catch (AssertionError ex) {
                    String message = "ACT ";
                    Object key = conf2Activations.getKey(t2);
                    if (key instanceof Layer) {
                        message += getName();
                        LayerCalculatorImpl lc = ((LayerCalculatorImpl) (trainer2.getNeuralNetwork().getLayerCalculator()));
                        if ((lc.getConnectionCalculator(((Layer) (key)))) instanceof ConnectionCalculatorImpl) {
                            ConnectionCalculatorImpl cc = ((ConnectionCalculatorImpl) (lc.getConnectionCalculator(((Layer) (key)))));
                            List<String> functions = new ArrayList<>();
                            for (TensorFunction f : cc.getInputModifierFunctions()) {
                                functions.add(f.getClass().getSimpleName());
                            }
                            for (ConnectionCalculator c : cc.getInputFunctions()) {
                                functions.add(c.getClass().getSimpleName());
                            }
                            for (TensorFunction f : cc.getActivationFunctions()) {
                                functions.add(f.getClass().getSimpleName());
                            }
                            message += " " + (functions.stream().collect(Collectors.joining("->")));
                        }
                    } else {
                        message += key.getClass().getSimpleName();
                    }
                    message += ((((Arrays.toString(t1it.getCurrentPosition())) + "; TARGET->ACTUAL: ") + (t1.getElements()[t1index])) + "->") + (t2.getElements()[t2index]);
                    BPCalculationProvidersComparisonTest.logger.error(message);
                    if ((Environment.getInstance().getRuntimeConfiguration().getCalculationProvider()) == (CalculationProvider.OPENCL)) {
                        for (OpenCLArrayReference ref : OpenCLArrayReferenceManager.getInstance().getArrayReferences(t2)) {
                            OpenCLCore.getInstance().checkFloatBuf(ref.getId(), t1.getElements());
                        }
                    }
                    throw ex;
                }
            } 
        } 
        // compare bp activations
        Iterator<Tensor> itbp1 = conf1Backpropagation.getTensors().iterator();
        Iterator<Tensor> itbp2 = conf2Backpropagation.getTensors().iterator();
        while ((itbp1.hasNext()) && (itbp2.hasNext())) {
            Tensor t1 = itbp1.next();
            Tensor t2 = itbp2.next();
            TensorIterator t1it = t1.iterator();
            TensorIterator t2it = t2.iterator();
            while ((t1it.hasNext()) && (t2it.hasNext())) {
                int t1index = t1it.next();
                int t2index = t2it.next();
                Assert.assertTrue(((t1.getElements()) != (t2.getElements())));
                Assert.assertTrue(((t1.getElements()[t1index]) != (Float.NaN)));
                Assert.assertTrue(((t2.getElements()[t2index]) != (Float.NaN)));
                Assert.assertTrue(((((t1.getElements()[t1index]) != 0) && ((t2.getElements()[t2index]) != 0)) || (((t1.getElements()[t1index]) == 0) && ((t2.getElements()[t2index]) == 0))));
                try {
                    Assert.assertEquals(0, Math.abs(((t1.getElements()[t1index]) - (t2.getElements()[t2index]))), 1.0E-6F);
                } catch (AssertionError ex) {
                    String message = "BP ";
                    Object key = conf2Backpropagation.getKey(t2);
                    if (key instanceof Layer) {
                        message += getName();
                        BackPropagationLayerCalculatorImpl lc = ((BackPropagationLayerCalculatorImpl) (trainer2.getBPLayerCalculator()));
                        if ((lc.getConnectionCalculator(((Layer) (key)))) instanceof BackPropagationConnectionCalculatorImpl) {
                            BackPropagationConnectionCalculatorImpl cc = ((BackPropagationConnectionCalculatorImpl) (lc.getConnectionCalculator(((Layer) (key)))));
                            List<String> functions = new ArrayList<>();
                            for (TensorFunction f : cc.getInputModifierFunctions()) {
                                functions.add(f.getClass().getSimpleName());
                            }
                            for (ConnectionCalculator c : cc.getInputFunctions()) {
                                functions.add(c.getClass().getSimpleName());
                            }
                            for (TensorFunction f : cc.getActivationFunctions()) {
                                functions.add(f.getClass().getSimpleName());
                            }
                            message += " " + (functions.stream().collect(Collectors.joining("->")));
                        }
                    } else {
                        message += key.getClass().getSimpleName();
                    }
                    message += ((((Arrays.toString(t1it.getCurrentPosition())) + "; TARGET->ACTUAL: ") + (t1.getElements()[t1index])) + "->") + (t2.getElements()[t2index]);
                    BPCalculationProvidersComparisonTest.logger.error(message);
                    if ((Environment.getInstance().getRuntimeConfiguration().getCalculationProvider()) == (CalculationProvider.OPENCL)) {
                        for (OpenCLArrayReference ref : OpenCLArrayReferenceManager.getInstance().getArrayReferences(t2)) {
                            OpenCLCore.getInstance().checkFloatBuf(ref.getId(), t1.getElements());
                        }
                    }
                    throw ex;
                }
            } 
        } 
        // compare weights
        Iterator<Tensor> itw1 = conf1Weights.getTensors().iterator();
        Iterator<Tensor> itw2 = conf2Weights.getTensors().iterator();
        while ((itw1.hasNext()) && (itw2.hasNext())) {
            Tensor t1 = itw1.next();
            Tensor t2 = itw2.next();
            TensorIterator t1it = t1.iterator();
            TensorIterator t2it = t2.iterator();
            while ((t1it.hasNext()) && (t2it.hasNext())) {
                int t1index = t1it.next();
                int t2index = t2it.next();
                Assert.assertTrue(((t1.getElements()) != (t2.getElements())));
                Assert.assertTrue(((t1.getElements()[t1index]) != (Float.NaN)));
                Assert.assertTrue(((t2.getElements()[t2index]) != (Float.NaN)));
                Assert.assertTrue(((((t1.getElements()[t1index]) != 0) && ((t2.getElements()[t2index]) != 0)) || (((t1.getElements()[t1index]) == 0) && ((t2.getElements()[t2index]) == 0))));
                try {
                    Assert.assertEquals(0, Math.abs(((t1.getElements()[t1index]) - (t2.getElements()[t2index]))), 1.0E-6F);
                } catch (AssertionError ex) {
                    Connections c = ((Connections) (conf2Weights.getKey(t2)));
                    WeightUpdates wu = trainer2.getWeightUpdates().get(c);
                    String message = (((((((((("WU " + (getName())) + "->") + (getName())) + " ") + (wu.getClass().getSimpleName())) + " ") + (Arrays.toString(t1it.getCurrentPosition()))) + "; TARGET->ACTUAL: ") + (t1.getElements()[t1index])) + "->") + (t2.getElements()[t2index]);
                    BPCalculationProvidersComparisonTest.logger.error(message);
                    if ((Environment.getInstance().getRuntimeConfiguration().getCalculationProvider()) == (CalculationProvider.OPENCL)) {
                        for (OpenCLArrayReference ref : OpenCLArrayReferenceManager.getInstance().getArrayReferences(t2)) {
                            OpenCLCore.getInstance().checkFloatBuf(ref.getId(), t1.getElements());
                        }
                    }
                    throw ex;
                }
            } 
        } 
    }

    private static class TestConfiguration implements Serializable {
        private static final long serialVersionUID = 1L;

        private RuntimeConfiguration conf1;

        private RuntimeConfiguration conf2;

        private NeuralNetworkBuilder builder;

        private long randomSeed;

        public TestConfiguration(RuntimeConfiguration conf1, RuntimeConfiguration conf2, NeuralNetworkBuilder builder, long randomSeed) {
            super();
            this.conf1 = conf1;
            this.conf2 = conf2;
            this.builder = builder;
            this.randomSeed = randomSeed;
        }
    }
}

