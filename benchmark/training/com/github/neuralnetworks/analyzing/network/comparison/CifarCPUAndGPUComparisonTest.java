package com.github.neuralnetworks.analyzing.network.comparison;


import ActivationType.Nothing;
import ActivationType.ReLU;
import ActivationType.SoftMax;
import TransferFunctionType.Max_Polling2D;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.layer.ConvolutionalLayerBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.builder.layer.PoolingLayerBuilder;
import com.github.neuralnetworks.input.MultipleNeuronsSimpleOutputError;
import com.github.neuralnetworks.input.RandomInputProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.TrainingInputProvider;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import java.io.File;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CifarCPUAndGPUComparisonTest extends AbstractTest {
    private static final Logger logger = LoggerFactory.getLogger(CifarCPUAndGPUComparisonTest.class);

    private TrainingInputProvider testInputProvider;

    public CifarCPUAndGPUComparisonTest() {
        // create input provider
        testInputProvider = new RandomInputProvider(3, 32, 10, new Random(123));// new SimpleInputProvider(new float[][] { input }, new float[][] { target });

    }

    // @After
    // public void after(){
    // OpenCLCore.getInstance().finalizeDeviceAll();
    // }
    @Test
    public void testFc() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        long seed = 123;
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 32, 32, 3));
            // fully connected layer
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setLearningRate(0.001F);
                fullyConnectedLayerBuilder.setL1weightDecay(0.03F);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed));
                fullyConnectedLayerBuilder.setBiasLearningRate(0.002F);
                fullyConnectedLayerBuilder.setBiasL1weightDecay(0);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                builder.setTrainingSet(testInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setError(new MultipleNeuronsSimpleOutputError());
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(10);
                builder.setTestBatchSize(1000);
            }
        }
        CPUAndGPUComparison cpuAndGPUComparison = new CPUAndGPUComparison();
        cpuAndGPUComparison.getComparison().getSimilarNetworkWeightsComparison().setProblemFilesDirForVadim(new File(((("CifarCPUAndGPUComparison" + (File.separator)) + "testFC") + (File.separator))));
        try {
            cpuAndGPUComparison.compare(builder);
        } catch (DifferentNetworksException e) {
            CifarCPUAndGPUComparisonTest.logger.error("", e);
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConvFC() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        long seed = 123;
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 32, 32, 3));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 30);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(1.0F, 0.01F, seed));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                builder.setTrainingSet(testInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(10);
                builder.setTestBatchSize(1000);
            }
        }
        CPUAndGPUComparison cpuAndGPUComparison = new CPUAndGPUComparison();
        cpuAndGPUComparison.getComparison().getSimilarNetworkWeightsComparison().setProblemFilesDirForVadim(new File(((("CifarCPUAndGPUComparison" + (File.separator)) + "testConfFC") + (File.separator))));
        try {
            cpuAndGPUComparison.compare(builder);
        } catch (DifferentNetworksException e) {
            CifarCPUAndGPUComparisonTest.logger.error("", e);
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testConvPoolFC() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        long seed = 123456789;
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 32, 32, 4));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 64);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0, 0.01F, seed));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed));
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
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 64);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0, 0.01F, seed));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed));
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
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0, 0.01F, seed));
                convolutionalLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(0.0F, 0.01F, seed));
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
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(0, 0.01F, seed));
                fullyConnectedLayerBuilder.setBiasWeightInitializer(new RandomInitializerImpl(0, 0.01F, seed));
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
                builder.setTrainingSet(testInputProvider);
                builder.setTestingSet(testInputProvider);
                // builder.setRand(new NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0f, 0.01f)));
                // builder.setLearningRate(0.001f);
                // builder.setMomentum(0.9f);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(10);
                builder.setTestBatchSize(128);
            }
        }
        CPUAndGPUComparison cpuAndGPUComparison = new CPUAndGPUComparison();
        cpuAndGPUComparison.getComparison().getSimilarNetworkWeightsComparison().setProblemFilesDirForVadim(new File(((("CifarCPUAndGPUComparison" + (File.separator)) + "testConfPoolFC") + (File.separator))));
        cpuAndGPUComparison.getComparison().getSimilarNetworkWeightsComparison().setMaxDifference(1.0E-5F);
        try {
            cpuAndGPUComparison.compare(builder);
        } catch (DifferentNetworksException e) {
            CifarCPUAndGPUComparisonTest.logger.error("", e);
            Assert.assertTrue(e.getMessage(), false);
        }
    }
}

