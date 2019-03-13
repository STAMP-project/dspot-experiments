package com.github.neuralnetworks.samples.test;


import ActivationType.Nothing;
import ActivationType.ReLU;
import ActivationType.SoftMax;
import TransferFunctionType.Max_Polling2D;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.types.NNFactory;
import com.github.neuralnetworks.builder.NeuralNetworkBuilder;
import com.github.neuralnetworks.builder.layer.ConvolutionalLayerBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.builder.layer.PoolingLayerBuilder;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.calculation.operations.OperationsFactory;
import com.github.neuralnetworks.input.MultipleNeuronsSimpleOutputError;
import com.github.neuralnetworks.input.SimpleFileInputProvider;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.TrainingInputProvider;
import com.github.neuralnetworks.training.backpropagation.BackPropagationTrainer;
import com.github.neuralnetworks.training.events.EarlySynchronizeEventListener;
import com.github.neuralnetworks.training.events.LogTrainingListener;
import com.github.neuralnetworks.training.random.MersenneTwisterRandomInitializer;
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
 * MNIST test
 */
// TODO ignored since test needs image data which is not available for everyone, i.e. test will break teamcity build
@RunWith(Parameterized.class)
@Ignore
@Deprecated
public class MnistTest {
    public MnistTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    /**
     * 1-layer mlp softmax
     */
    @Test
    public void test0() {
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 784, 10 }, true);
        mlp.setLayerCalculator(CalculationFactory.lcRelu(mlp, OperationsFactory.softmaxFunction()));
        TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
        TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(mlp, trainInputProvider, testInputProvider, new MultipleNeuronsSimpleOutputError(), new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, (-0.01F), 0.01F)), 0.05F, 0.5F, 0.0F, 0.0F, 0.0F, 10, 1000, 3);
        LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true);
        ls.setLogBatchLoss(false);
        ls.setLogInterval(1000);
        bpt.addEventListener(ls);
        bpt.train();
        bpt.test();
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    /**
     * 2-layer mlp relu+softmax
     */
    @Test
    public void test1() {
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        NeuralNetworkImpl mlp = NNFactory.mlp(new int[]{ 784, 300, 10 }, true);
        mlp.setLayerCalculator(CalculationFactory.lcRelu(mlp, OperationsFactory.softmaxFunction()));
        TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
        TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
        BackPropagationTrainer<?> bpt = TrainerFactory.backPropagation(mlp, trainInputProvider, testInputProvider, new MultipleNeuronsSimpleOutputError(), new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, (-0.01F), 0.01F)), 0.05F, 0.5F, 0.0F, 0.0F, 0.0F, 10, 1000, 1);
        bpt.addEventListener(new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true));
        bpt.train();
        bpt.test();
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    /**
     * MNIST small LeNet network
     */
    @Test
    public void test2() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
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
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 1.0F, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
            }
        }
        BackPropagationTrainer<?> bpt = ((BackPropagationTrainer<?>) (builder.buildWithTrainer().getRight()));
        // log data
        LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true);
        ls.setLogBatchLoss(true);
        ls.setLogInterval(5000);
        bpt.addEventListener(ls);
        // 
        // EarlyStoppingListener es = new EarlyStoppingListener(bpt.getTrainingInputProvider(), 0, "Train");
        // es.setMiniBatchSize(1000);
        // es.setOutputFile(new File("TrainError.txt"));
        // bpt.addEventListener(es);
        // 
        // EarlyStoppingListener es2 = new EarlyStoppingListener(bpt.getTestingInputProvider(), 0, "Test");
        // es2.setMiniBatchSize(1000);
        // es2.setOutputFile(new File("TestError.txt"));
        // bpt.addEventListener(es2);
        // 
        // training
        bpt.train();
        // testing
        bpt.test();
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    @Test
    public void test3() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(300);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
            }
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
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    @Test
    public void test4() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
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
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(300);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
            }
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
        Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
    }

    @Test
    public void test5() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 4);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(Nothing);
                poolingLayerBuilder.setStrideSize(1);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 10);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(ReLU);
                poolingLayerBuilder.setStrideSize(1);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
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

    @Test
    public void test6() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(Nothing);
                poolingLayerBuilder.setStrideSize(1);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 12);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(Nothing);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setActivationType(ReLU);
                poolingLayerBuilder.setStrideSize(1);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(300);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
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

    @Test
    public void test7() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
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
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 12);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
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
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(80);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.1F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.1F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.1F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.1F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
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

    @Test
    public void test8() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 6);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.07F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.1F);
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
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 14);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.07F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.1F);
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
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(140);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.1F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.1F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(70);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.1F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.1F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.1F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.1F);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
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

    // @Ignore
    @Test
    public void test() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123);
        byte[] seed = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 28, 28, 1));
            // conv
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 20);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                // convolutionalLayerBuilder.setLearningRate(0.07f);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setBiasLearningRate(0.1f);
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
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 50);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                // convolutionalLayerBuilder.setLearningRate(0.07f);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setBiasLearningRate(0.1f);
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
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(4, 500);
                convolutionalLayerBuilder.setPaddingSize(0);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0.0F, 0.01F));
                // convolutionalLayerBuilder.setLearningRate(0.07f);
                convolutionalLayerBuilder.setMomentum(0.9F);
                // convolutionalLayerBuilder.setBiasLearningRate(0.1f);
                convolutionalLayerBuilder.setBiasMomentum(0.9F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
            }
            // fc
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, 0, 0.01F));
                // fullyConnectedLayerBuilder.setLearningRate(0.1f);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                // fullyConnectedLayerBuilder.setBiasLearningRate(0.1f);
                fullyConnectedLayerBuilder.setBiasMomentum(0.9F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
            // trainer
            {
                TrainingInputProvider trainInputProvider = new SimpleFileInputProvider("mnist/train-images.float", "mnist/train-labels.float", (28 * 28), 10, 60000);
                TrainingInputProvider testInputProvider = new SimpleFileInputProvider("mnist/t10k-images.float", "mnist/t10k-labels.float", (28 * 28), 10, 10000);
                builder.setTrainingSet(trainInputProvider);
                builder.setTestingSet(testInputProvider);
                builder.setRand(new com.github.neuralnetworks.training.random.NNRandomInitializer(new MersenneTwisterRandomInitializer(seed, 0.0F, 0.01F)));
                builder.setLearningRate(0.001F);
                builder.setMomentum(0.9F);
                builder.setEpochs(1);
                builder.setTrainingBatchSize(100);
                builder.setTestBatchSize(1000);
            }
            BackPropagationTrainer<?> bpt = ((BackPropagationTrainer<?>) (builder.buildWithTrainer().getRight()));
            // log data
            LogTrainingListener ls = new LogTrainingListener(Thread.currentThread().getStackTrace()[1].getMethodName(), false, true);
            ls.setLogBatchLoss(true);
            ls.setLogInterval(5000);
            // EarlyStoppingListener es = new EarlyStoppingListener(bpt.getTrainingInputProvider(), 0, "Train");
            // es.setMiniBatchSize(1000);
            // es.setOutputFile(new File("TrainError.txt"));
            // bpt.addEventListener(es);
            // 
            // EarlyStoppingListener es2 = new EarlyStoppingListener(bpt.getTestingInputProvider(), 0, "Test");
            // es2.setMiniBatchSize(1000);
            // es2.setOutputFile(new File("TestError.txt"));
            // bpt.addEventListener(es2);
            EarlySynchronizeEventListener earlySynchronizeEventListener = new EarlySynchronizeEventListener(bpt);
            bpt.addEventListener(earlySynchronizeEventListener);
            bpt.addEventListener(ls);
            // training
            bpt.train();
            // testing
            bpt.test();
            Assert.assertEquals(0, bpt.getOutputError().getTotalNetworkError(), 0.1);
        }
    }
}

