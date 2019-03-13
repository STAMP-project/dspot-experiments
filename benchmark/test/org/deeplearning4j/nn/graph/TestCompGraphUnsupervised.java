package org.deeplearning4j.nn.graph;


import Activation.SIGMOID;
import Activation.TANH;
import WeightInit.XAVIER;
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.learning.config.Adam;


public class TestCompGraphUnsupervised extends BaseDL4JTest {
    @Test
    public void testVAE() throws Exception {
        for (WorkspaceMode wsm : new WorkspaceMode[]{ WorkspaceMode.NONE, WorkspaceMode.ENABLED }) {
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.001)).weightInit(XAVIER).inferenceWorkspaceMode(wsm).trainingWorkspaceMode(wsm).graphBuilder().addInputs("in").addLayer("vae1", new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(16).decoderLayerSizes(16).activation(TANH).pzxActivationFunction(SIGMOID).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution(Activation.SIGMOID)).build(), "in").addLayer("vae2", new VariationalAutoencoder.Builder().nIn(32).nOut(8).encoderLayerSizes(16).decoderLayerSizes(16).activation(TANH).pzxActivationFunction(SIGMOID).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution(Activation.TANH)).build(), "vae1").setOutputs("vae2").build();
            ComputationGraph cg = new ComputationGraph(conf);
            cg.init();
            DataSetIterator ds = new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(8, true, 12345), 3);
            Map<String, INDArray> paramsBefore = new HashMap<>();
            // Pretrain first layer
            for (Map.Entry<String, INDArray> e : cg.paramTable().entrySet()) {
                paramsBefore.put(e.getKey(), e.getValue().dup());
            }
            cg.pretrainLayer("vae1", ds);
            for (Map.Entry<String, INDArray> e : cg.paramTable().entrySet()) {
                if (e.getKey().startsWith("vae1")) {
                    Assert.assertNotEquals(paramsBefore.get(e.getKey()), e.getValue());
                } else {
                    Assert.assertEquals(paramsBefore.get(e.getKey()), e.getValue());
                }
            }
            int count = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(cg.params(), Conditions.isNan())).getInt(0);
            Assert.assertEquals(0, count);
            // Pretrain second layer
            for (Map.Entry<String, INDArray> e : cg.paramTable().entrySet()) {
                paramsBefore.put(e.getKey(), e.getValue().dup());
            }
            cg.pretrainLayer("vae2", ds);
            for (Map.Entry<String, INDArray> e : cg.paramTable().entrySet()) {
                if (e.getKey().startsWith("vae2")) {
                    Assert.assertNotEquals(paramsBefore.get(e.getKey()), e.getValue());
                } else {
                    Assert.assertEquals(paramsBefore.get(e.getKey()), e.getValue());
                }
            }
            count = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(cg.params(), Conditions.isNan())).getInt(0);
            Assert.assertEquals(0, count);
        }
    }

    @Test
    public void compareImplementations() throws Exception {
        for (WorkspaceMode wsm : new WorkspaceMode[]{ WorkspaceMode.NONE, WorkspaceMode.ENABLED }) {
            MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.001)).weightInit(XAVIER).inferenceWorkspaceMode(wsm).trainingWorkspaceMode(wsm).list().layer(new VariationalAutoencoder.Builder().nIn(784).nOut(32).encoderLayerSizes(16).decoderLayerSizes(16).activation(TANH).pzxActivationFunction(SIGMOID).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution(Activation.SIGMOID)).build()).layer(new VariationalAutoencoder.Builder().nIn(32).nOut(8).encoderLayerSizes(16).decoderLayerSizes(16).activation(TANH).pzxActivationFunction(SIGMOID).reconstructionDistribution(new org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution(Activation.TANH)).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf2);
            net.init();
            ComputationGraph cg = net.toComputationGraph();
            cg.getConfiguration().setInferenceWorkspaceMode(wsm);
            cg.getConfiguration().setTrainingWorkspaceMode(wsm);
            DataSetIterator ds = new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(1, true, 12345), 1);
            Nd4j.getRandom().setSeed(12345);
            net.pretrainLayer(0, ds);
            ds = new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(1, true, 12345), 1);
            Nd4j.getRandom().setSeed(12345);
            cg.pretrainLayer("0", ds);
            Assert.assertEquals(net.params(), cg.params());
        }
    }
}

