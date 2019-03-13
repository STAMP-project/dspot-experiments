package com.github.neuralnetworks.builder;


import ActivationType.ReLU;
import ActivationType.SoftMax;
import TransferFunctionType.Max_Polling2D;
import com.github.neuralnetworks.architecture.Conv2DConnection;
import com.github.neuralnetworks.architecture.FullyConnected;
import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetwork;
import com.github.neuralnetworks.architecture.NeuralNetworkImpl;
import com.github.neuralnetworks.architecture.Subsampling2DConnection;
import com.github.neuralnetworks.builder.layer.ConvolutionalLayerBuilder;
import com.github.neuralnetworks.builder.layer.FullyConnectedLayerBuilder;
import com.github.neuralnetworks.builder.layer.InputLayerBuilder;
import com.github.neuralnetworks.builder.layer.PoolingLayerBuilder;
import com.github.neuralnetworks.training.Trainer;
import com.github.neuralnetworks.training.random.RandomInitializerImpl;
import com.github.neuralnetworks.util.Pair;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class NeuralNetworkBuilderTest {
    @Test
    public void testConstruction() {
        NeuralNetworkBuilder builder = new NeuralNetworkBuilder();
        Random r = new Random(123456789);
        // network
        {
            builder.addLayerBuilder(new InputLayerBuilder("inputLayer", 32, 32, 3));
            // first part
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 16);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasL2weightDecay(1.0E-4F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // second part
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 20);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasL2weightDecay(1.0E-4F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // third part
            {
                ConvolutionalLayerBuilder convolutionalLayerBuilder = new ConvolutionalLayerBuilder(5, 20);
                convolutionalLayerBuilder.setPaddingSize(2);
                convolutionalLayerBuilder.setStrideSize(1);
                convolutionalLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                convolutionalLayerBuilder.setLearningRate(0.01F);
                convolutionalLayerBuilder.setMomentum(0.9F);
                convolutionalLayerBuilder.setBiasLearningRate(0.01F);
                convolutionalLayerBuilder.setBiasL2weightDecay(1.0E-4F);
                convolutionalLayerBuilder.setActivationType(ReLU);
                builder.addLayerBuilder(convolutionalLayerBuilder);
                PoolingLayerBuilder poolingLayerBuilder = new PoolingLayerBuilder(2);
                poolingLayerBuilder.setTransferFunctionType(Max_Polling2D);
                poolingLayerBuilder.setStrideSize(2);
                builder.addLayerBuilder(poolingLayerBuilder);
            }
            // fully connected layer
            {
                FullyConnectedLayerBuilder fullyConnectedLayerBuilder = new FullyConnectedLayerBuilder(10);
                fullyConnectedLayerBuilder.setWeightInitializer(new RandomInitializerImpl(r, (-0.01F), 0.01F));
                fullyConnectedLayerBuilder.setLearningRate(0.01F);
                fullyConnectedLayerBuilder.setMomentum(0.9F);
                fullyConnectedLayerBuilder.setBiasLearningRate(0.01F);
                fullyConnectedLayerBuilder.setBiasL2weightDecay(1.0E-4F);
                fullyConnectedLayerBuilder.setActivationType(SoftMax);
                builder.addLayerBuilder(fullyConnectedLayerBuilder);
            }
        }
        Pair<NeuralNetworkImpl, Trainer<NeuralNetwork>> pair = builder.buildWithTrainer();
        NeuralNetworkImpl nn = ((NeuralNetworkImpl) (pair.getRight().getNeuralNetwork()));
        Layer input = nn.getInputLayer();
        Assert.assertEquals(1, input.getConnections().size());
        Conv2DConnection conv1c = ((Conv2DConnection) (input.getConnections().get(0)));
        Assert.assertEquals(3, conv1c.getInputFilters());
        Assert.assertEquals(16, conv1c.getOutputFilters());
        Assert.assertEquals(5, conv1c.getFilterRows());
        Assert.assertEquals(5, conv1c.getFilterColumns());
        Assert.assertEquals(32, conv1c.getInputFeatureMapRows());
        Assert.assertEquals(32, conv1c.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv1c.getRowStride());
        Assert.assertEquals(1, conv1c.getColumnStride());
        Assert.assertEquals(2, conv1c.getOutputColumnPadding());
        Assert.assertEquals(2, conv1c.getOutputRowPadding());
        Assert.assertEquals(32, conv1c.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(32, conv1c.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((28 * 28) * 16), conv1c.getOutputUnitCount());
        Conv2DConnection conv1b = ((Conv2DConnection) (conv1c.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(1, conv1b.getInputFilters());
        Assert.assertEquals(16, conv1b.getOutputFilters());
        Assert.assertEquals(1, conv1b.getFilterRows());
        Assert.assertEquals(1, conv1b.getFilterColumns());
        Assert.assertEquals(28, conv1b.getInputFeatureMapRows());
        Assert.assertEquals(28, conv1b.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv1b.getRowStride());
        Assert.assertEquals(1, conv1b.getColumnStride());
        Assert.assertEquals(2, conv1b.getOutputColumnPadding());
        Assert.assertEquals(2, conv1b.getOutputRowPadding());
        Assert.assertEquals(((28 * 28) * 16), conv1b.getOutputUnitCount());
        Assert.assertEquals(((32 * 32) * 16), conv1b.getOutputUnitCountWithPadding());
        Subsampling2DConnection pool1 = ((Subsampling2DConnection) (conv1c.getOutputLayer().getConnections().get(2)));
        Assert.assertEquals(16, pool1.getFilters());
        Assert.assertEquals(2, pool1.getColumnStride());
        Assert.assertEquals(2, pool1.getRowStride());
        Assert.assertEquals(16, pool1.getOutputFeatureMapRows());
        Assert.assertEquals(16, pool1.getOutputFeatureMapColumns());
        Assert.assertEquals(16, pool1.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(16, pool1.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((32 * 32) * 16), pool1.getInputUnitCount());
        Assert.assertEquals(((16 * 16) * 16), pool1.getOutputUnitCount());
        Conv2DConnection conv2c = ((Conv2DConnection) (pool1.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(16, conv2c.getInputFilters());
        Assert.assertEquals(20, conv2c.getOutputFilters());
        Assert.assertEquals(5, conv2c.getFilterRows());
        Assert.assertEquals(5, conv2c.getFilterColumns());
        Assert.assertEquals(16, conv2c.getInputFeatureMapRows());
        Assert.assertEquals(16, conv2c.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv2c.getRowStride());
        Assert.assertEquals(1, conv2c.getColumnStride());
        Assert.assertEquals(2, conv2c.getOutputColumnPadding());
        Assert.assertEquals(2, conv2c.getOutputRowPadding());
        Assert.assertEquals(12, conv2c.getOutputFeatureMapRows());
        Assert.assertEquals(12, conv2c.getOutputFeatureMapColumns());
        Assert.assertEquals(16, conv2c.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(16, conv2c.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((12 * 12) * 20), conv2c.getOutputUnitCount());
        Assert.assertEquals(((16 * 16) * 20), conv2c.getOutputUnitCountWithPadding());
        Conv2DConnection conv2b = ((Conv2DConnection) (conv2c.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(1, conv2b.getInputFilters());
        Assert.assertEquals(20, conv2b.getOutputFilters());
        Assert.assertEquals(1, conv2b.getFilterRows());
        Assert.assertEquals(1, conv2b.getFilterColumns());
        Assert.assertEquals(12, conv2b.getInputFeatureMapRows());
        Assert.assertEquals(12, conv2b.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv2b.getRowStride());
        Assert.assertEquals(1, conv2b.getColumnStride());
        Assert.assertEquals(2, conv2b.getOutputColumnPadding());
        Assert.assertEquals(2, conv2b.getOutputRowPadding());
        Assert.assertEquals(12, conv2b.getOutputFeatureMapRows());
        Assert.assertEquals(12, conv2b.getOutputFeatureMapColumns());
        Assert.assertEquals(16, conv2b.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(16, conv2b.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((12 * 12) * 20), conv2b.getOutputUnitCount());
        Assert.assertEquals(((16 * 16) * 20), conv2b.getOutputUnitCountWithPadding());
        Subsampling2DConnection pool2 = ((Subsampling2DConnection) (conv2c.getOutputLayer().getConnections().get(2)));
        Assert.assertEquals(20, pool2.getFilters());
        Assert.assertEquals(2, pool2.getColumnStride());
        Assert.assertEquals(2, pool2.getRowStride());
        Assert.assertEquals(8, pool2.getOutputFeatureMapRows());
        Assert.assertEquals(8, pool2.getOutputFeatureMapColumns());
        Assert.assertEquals(8, pool2.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(8, pool2.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((16 * 16) * 20), pool2.getInputUnitCount());
        Assert.assertEquals(((8 * 8) * 20), pool2.getOutputUnitCount());
        Conv2DConnection conv3c = ((Conv2DConnection) (pool2.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(20, conv3c.getInputFilters());
        Assert.assertEquals(20, conv3c.getOutputFilters());
        Assert.assertEquals(5, conv3c.getFilterRows());
        Assert.assertEquals(5, conv3c.getFilterColumns());
        Assert.assertEquals(8, conv3c.getInputFeatureMapRows());
        Assert.assertEquals(8, conv3c.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv3c.getRowStride());
        Assert.assertEquals(1, conv3c.getColumnStride());
        Assert.assertEquals(2, conv3c.getOutputColumnPadding());
        Assert.assertEquals(2, conv3c.getOutputRowPadding());
        Assert.assertEquals(4, conv3c.getOutputFeatureMapRows());
        Assert.assertEquals(4, conv3c.getOutputFeatureMapColumns());
        Assert.assertEquals(8, conv3c.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(8, conv3c.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((4 * 4) * 20), conv3c.getOutputUnitCount());
        Assert.assertEquals(((8 * 8) * 20), conv3c.getOutputUnitCountWithPadding());
        Conv2DConnection conv3b = ((Conv2DConnection) (conv3c.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(1, conv3b.getInputFilters());
        Assert.assertEquals(20, conv3b.getOutputFilters());
        Assert.assertEquals(1, conv3b.getFilterRows());
        Assert.assertEquals(1, conv3b.getFilterColumns());
        Assert.assertEquals(4, conv3b.getInputFeatureMapRows());
        Assert.assertEquals(4, conv3b.getInputFeatureMapColumns());
        Assert.assertEquals(1, conv3b.getRowStride());
        Assert.assertEquals(1, conv3b.getColumnStride());
        Assert.assertEquals(2, conv3b.getOutputColumnPadding());
        Assert.assertEquals(2, conv3b.getOutputRowPadding());
        Assert.assertEquals(4, conv3b.getOutputFeatureMapRows());
        Assert.assertEquals(4, conv3b.getOutputFeatureMapColumns());
        Assert.assertEquals(8, conv3b.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(8, conv3b.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((4 * 4) * 20), conv3b.getOutputUnitCount());
        Assert.assertEquals(((8 * 8) * 20), conv3b.getOutputUnitCountWithPadding());
        Subsampling2DConnection pool3 = ((Subsampling2DConnection) (conv3c.getOutputLayer().getConnections().get(2)));
        Assert.assertEquals(20, pool3.getFilters());
        Assert.assertEquals(2, pool3.getColumnStride());
        Assert.assertEquals(2, pool3.getRowStride());
        Assert.assertEquals(4, pool3.getOutputFeatureMapRows());
        Assert.assertEquals(4, pool3.getOutputFeatureMapColumns());
        Assert.assertEquals(4, pool3.getOutputFeatureMapRowsWithPadding());
        Assert.assertEquals(4, pool3.getOutputFeatureMapColumnsWithPadding());
        Assert.assertEquals(((8 * 8) * 20), pool3.getInputUnitCount());
        Assert.assertEquals(((4 * 4) * 20), pool3.getOutputUnitCount());
        FullyConnected fc1 = ((FullyConnected) (pool3.getOutputLayer().getConnections().get(1)));
        Assert.assertEquals(((4 * 4) * 20), fc1.getInputUnitCount());
        Assert.assertEquals(10, fc1.getOutputUnitCount());
    }
}

