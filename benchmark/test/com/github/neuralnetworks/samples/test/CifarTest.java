package com.github.neuralnetworks.samples.test;


import ActivationType.Nothing;
import ActivationType.ReLU;
import ActivationType.SoftMax;
import TransferFunctionType.Max_Polling2D;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.layer.ConvolutionalLayerBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.builder.layer.PoolingLayerBuilder;
import com.github.neuralnetworks.input.SimpleFileInputProvider;
import com.github.neuralnetworks.training.TrainingInputProvider;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.events.LogTrainingListener;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.Random;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * CIFAR test
 */
// needs test data, not a good unit test
@RunWith(Parameterized.class)
@Ignore
@Deprecated
public class CifarTest {
    public CifarTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    // @Ignore
    @Test
    public void test() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123456789);
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 32, 32, 3));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 64);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setL1weightDecay(0.01f);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(Nothing);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 64);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setL1weightDecay(0.01f);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(Nothing);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(3, 64);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setL1weightDecay(0.01f);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(ReLU);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                // fullyConnectedLayerBuilder.setL1weightDecay(0.01f);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("cifar-10-batches-bin/train-data.float", "cifar-10-batches-bin/train-labels.float", (32 * 32), 10, 50000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("cifar-10-batches-bin/test-data.float", "cifar-10-batches-bin/test-labels.float", (32 * 32), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                // builder.setRand(new NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0f, 0.01f)));
                // builder.setLearningRate(0.001f);
                // builder.setMomentum(0.9f);
                builder.setEpochs(25);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(100);
            }
            BackPropagationTrainer<?> bpt = ((BackPropagationTrainer<?>) (builder.buildWithTrainer().getRight()));
            // log data
            LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true);
            ls.setLogBatchLoss(true);
            // ls.setLogWeightUpdates(true);
            ls.setLogInterval(5000);
            bpt.addEventListener(ls);
            // training
            bpt.train();
            // testing
            bpt.test();
            Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
        }
    }
}

