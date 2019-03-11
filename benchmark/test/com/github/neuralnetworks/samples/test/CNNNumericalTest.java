package com.github.neuralnetworks.samples.test;


import ActivationType.SoftMax;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.layer.ConvolutionalLayerBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.input.SimpleFileInputProvider;
import com.github.neuralnetworks.training.TrainingInputProvider;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.events.LogTrainingListener;
import com.github.neuralnetworks.training.random.DummyFixedInitializer;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test CNNs numerically
 */
// needs test data, no good unit test
@RunWith(Parameterized.class)
@Ignore
@Deprecated
public class CNNNumericalTest {
    public CNNNumericalTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    /**
     * Test all possible numbers manually within a small CNN
     */
    @Test
    public void testSmallNet() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 5, 5, 1));
            // Convolutional Layer One (input vectors 5x5 + patch size 2x2+ stride 1 + 3 feature maps = 4x4x3 output)
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(2, 3);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new DummyFixedInitializer());
                convolutionalLayerBuilder.setBiasLearningRate(0.002F);
                convolutionalLayerBuilder.setBiasL2weightDecay(0);
                builder.addLayerBuilder(convolutionalLayerBuilder);
            }
            // Convolutional Layer Two (input vectors 4x4x3 + patch size 2x2+ stride 1 + 3 feature maps = 3x3x3 output)
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(2, 3);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new DummyFixedInitializer());
                convolutionalLayerBuilder.setBiasLearningRate(0.002F);
                convolutionalLayerBuilder.setBiasL2weightDecay(0);
                // convolutionalLayerBuilder.setActivationType(ActivationType.Sigmoid);
                builder.addLayerBuilder(convolutionalLayerBuilder);
            }
            // FC layer (3x3x3 input + 3 output)
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(3);
                fullyConnectedLayerBuilder.setL2weightDecay(0.0F);
                fullyConnectedLayerBuilder.setWeightInitializer(new DummyFixedInitializer());
                fullyConnectedLayerBuilder.setBiasLearningRate(0.002F);
                fullyConnectedLayerBuilder.setBiasL2weightDecay(0);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
        }
        // NN global config
        {
            TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (5 * 5), 10, 2);
            TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (5 * 5), 10, 2);
            builder.setTrainingSet(trainInputProvider);
            builder.setTestingSet(testInputProvider);
            builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new DummyFixedInitializer()));
            builder.setLearningRate(0.001F);
            builder.setMomentum(0.9F);
            builder.setEpochs(1);
            builder.setTrainingBatchSize(1);
            builder.setTestBatchSize(1000);
        }
        BackPropagationTrainer<?> bpt = ((BackPropagationTrainer<?>) (builder.buildWithTrainer().getRight()));
        // log data
        LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true);
        ls.setLogBatchLoss(true);
        ls.setLogInterval(5000);
        bpt.addEventListener(ls);
        // training
        bpt.train();
        // testing
        bpt.test();
    }
}

