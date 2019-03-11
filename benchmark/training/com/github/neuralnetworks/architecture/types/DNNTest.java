package com.github.neuralnetworks.architecture.types;


import com.github.neuralnetworks.architecture.Layer;
import com.github.neuralnetworks.architecture.NeuralNetwork;
import com.github.neuralnetworks.calculation.CalculationFactory;
import com.github.neuralnetworks.calculation.LayerCalculatorImpl;
import com.github.neuralnetworks.calculation.operations.OperationsFactory;
import com.github.neuralnetworks.input.SimpleInputProvider;
import com.github.neuralnetworks.tensor.Matrix;
import com.github.neuralnetworks.tensor.Tensor;
import com.github.neuralnetworks.tensor.TensorFactory;
import com.github.neuralnetworks.tensor.ValuesProvider;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.training.DNNLayerTrainer;
import com.github.neuralnetworks.training.OneStepTrainer;
import com.github.neuralnetworks.training.TrainerFactory;
import com.github.neuralnetworks.training.rbm.CDTrainerBase;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class DNNTest extends AbstractTest {
    public DNNTest(RuntimeConfiguration conf) {
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testDBNConstruction() {
        DBN dbn = NNFactory.dbn(new int[]{ 4, 4, 4, 4 }, false);
        Assert.assertEquals(4, dbn.getLayers().size(), 0);
        Assert.assertEquals(3, dbn.getNeuralNetworks().size(), 0);
        Assert.assertEquals(2, dbn.getFirstNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(2, dbn.getNeuralNetwork(1).getLayers().size(), 0);
        Assert.assertEquals(2, dbn.getLastNeuralNetwork().getLayers().size(), 0);
        dbn = NNFactory.dbn(new int[]{ 4, 4, 4, 4 }, true);
        Assert.assertEquals(7, dbn.getLayers().size(), 0);
        Assert.assertEquals(3, dbn.getNeuralNetworks().size(), 0);
        Assert.assertEquals(4, dbn.getFirstNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(4, dbn.getNeuralNetwork(0).getLayers().size(), 0);
        Assert.assertEquals(4, dbn.getLastNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(true, ((dbn.getFirstNeuralNetwork().getHiddenBiasConnections()) != null));
        Assert.assertEquals(true, ((dbn.getFirstNeuralNetwork().getVisibleBiasConnections()) != null));
        Assert.assertEquals(true, ((dbn.getNeuralNetwork(1).getHiddenBiasConnections()) != null));
        Assert.assertEquals(true, ((dbn.getNeuralNetwork(1).getVisibleBiasConnections()) != null));
        Assert.assertEquals(true, ((dbn.getLastNeuralNetwork().getHiddenBiasConnections()) != null));
        Assert.assertEquals(true, ((dbn.getLastNeuralNetwork().getVisibleBiasConnections()) != null));
        Assert.assertEquals(false, dbn.getLayers().contains(dbn.getFirstNeuralNetwork().getVisibleBiasConnections().getInputLayer()));
        Assert.assertEquals(false, dbn.getLayers().contains(dbn.getNeuralNetwork(1).getVisibleBiasConnections().getInputLayer()));
        Assert.assertEquals(false, dbn.getLayers().contains(dbn.getLastNeuralNetwork().getVisibleBiasConnections().getInputLayer()));
        Assert.assertEquals(true, ((dbn.getFirstNeuralNetwork().getHiddenLayer()) == (dbn.getNeuralNetwork(1).getVisibleLayer())));
        Assert.assertEquals(true, ((dbn.getNeuralNetwork(1).getHiddenLayer()) == (dbn.getLastNeuralNetwork().getVisibleLayer())));
        Assert.assertEquals(true, dbn.getOutputLayer().equals(dbn.getLastNeuralNetwork().getHiddenLayer()));
    }

    @Test
    public void testStackedAutoencoderConstruction() {
        StackedAutoencoder sae = NNFactory.sae(new int[]{ 5, 4, 3 }, false);
        Assert.assertEquals(3, sae.getLayers().size(), 0);
        Assert.assertEquals(2, sae.getNeuralNetworks().size(), 0);
        Assert.assertEquals(3, sae.getFirstNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(3, sae.getLastNeuralNetwork().getLayers().size(), 0);
        sae = NNFactory.sae(new int[]{ 5, 4, 3 }, true);
        Assert.assertEquals(5, sae.getLayers().size(), 0);
        Assert.assertEquals(2, sae.getNeuralNetworks().size(), 0);
        Assert.assertEquals(5, sae.getFirstNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(5, sae.getLastNeuralNetwork().getLayers().size(), 0);
        Assert.assertEquals(false, sae.getLayers().contains(sae.getFirstNeuralNetwork().getOutputLayer()));
        Assert.assertEquals(false, sae.getLayers().contains(sae.getLastNeuralNetwork().getOutputLayer()));
        Assert.assertEquals(true, sae.getOutputLayer().equals(sae.getLastNeuralNetwork().getHiddenLayer()));
        Assert.assertEquals(true, ((sae.getFirstNeuralNetwork().getHiddenLayer()) == (sae.getLastNeuralNetwork().getInputLayer())));
    }

    @Test
    public void testDBNCalculation() {
        DBN dbn = NNFactory.dbn(new int[]{ 3, 2, 2 }, true);
        dbn.setLayerCalculator(CalculationFactory.lcWeightedSum(dbn, null));
        RBM firstRBM = dbn.getFirstNeuralNetwork();
        Tensor t = getWeights();
        float[] e1 = t.getElements();
        t.forEach(( i) -> e1[i] = 0.2F);
        t = firstRBM.getVisibleBiasConnections().getWeights();
        float[] e2 = t.getElements();
        t.forEach(( i) -> e2[i] = 0.1F);
        t = firstRBM.getHiddenBiasConnections().getWeights();
        float[] e3 = t.getElements();
        t.forEach(( i) -> e3[i] = 0.3F);
        RBM secondRBM = dbn.getLastNeuralNetwork();
        t = secondRBM.getMainConnections().getWeights();
        float[] e4 = t.getElements();
        t.forEach(( i) -> e4[i] = 0.4F);
        t = secondRBM.getVisibleBiasConnections().getWeights();
        float[] e5 = t.getElements();
        t.forEach(( i) -> e5[i] = 0.8F);
        t = secondRBM.getHiddenBiasConnections().getWeights();
        float[] e6 = t.getElements();
        t.forEach(( i) -> e6[i] = 0.5F);
        Set<Layer> calculatedLayers = new HashSet<>();
        calculatedLayers.add(dbn.getInputLayer());
        ValuesProvider results = TensorFactory.tensorProvider(dbn, 1, true);
        results.get(dbn.getInputLayer()).set(1, 0, 0);
        results.get(dbn.getInputLayer()).set(0, 0, 1);
        results.get(dbn.getInputLayer()).set(1, 0, 2);
        dbn.getLayerCalculator().calculate(dbn, dbn.getOutputLayer(), calculatedLayers, results);
        Assert.assertEquals(1.06, results.get(dbn.getOutputLayer()).get(0, 0), 1.0E-5);
        Assert.assertEquals(1.06, results.get(dbn.getOutputLayer()).get(0, 1), 1.0E-5);
    }

    @Test
    public void testSAECalculation() {
        StackedAutoencoder sae = NNFactory.sae(new int[]{ 3, 2, 2 }, true);
        sae.setLayerCalculator(CalculationFactory.lcWeightedSum(sae, null));
        Autoencoder firstAE = sae.getFirstNeuralNetwork();
        Tensor t = getWeights();
        float[] e1 = t.getElements();
        t.forEach(( i) -> e1[i] = 0.2F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (firstAE.getConnection(firstAE.getHiddenBiasLayer(), firstAE.getHiddenLayer()))).getWeights();
        float[] e2 = t.getElements();
        t.forEach(( i) -> e2[i] = 0.3F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (firstAE.getConnection(firstAE.getHiddenLayer(), firstAE.getOutputLayer()))).getWeights();
        float[] e3 = t.getElements();
        t.forEach(( i) -> e3[i] = 0.8F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (firstAE.getConnection(firstAE.getOutputBiasLayer(), firstAE.getOutputLayer()))).getWeights();
        float[] e4 = t.getElements();
        t.forEach(( i) -> e4[i] = 0.9F);
        Autoencoder secondAE = sae.getLastNeuralNetwork();
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (secondAE.getConnection(secondAE.getInputLayer(), secondAE.getHiddenLayer()))).getWeights();
        float[] e5 = t.getElements();
        t.forEach(( i) -> e5[i] = 0.4F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (secondAE.getConnection(secondAE.getHiddenBiasLayer(), secondAE.getHiddenLayer()))).getWeights();
        float[] e6 = t.getElements();
        t.forEach(( i) -> e6[i] = 0.5F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (secondAE.getConnection(secondAE.getHiddenLayer(), secondAE.getOutputLayer()))).getWeights();
        float[] e7 = t.getElements();
        t.forEach(( i) -> e7[i] = 0.7F);
        t = ((com.github.neuralnetworks.architecture.FullyConnected) (secondAE.getConnection(secondAE.getOutputBiasLayer(), secondAE.getOutputLayer()))).getWeights();
        float[] e8 = t.getElements();
        t.forEach(( i) -> e8[i] = 0.9F);
        Set<Layer> calculatedLayers = new HashSet<>();
        calculatedLayers.add(sae.getInputLayer());
        ValuesProvider results = TensorFactory.tensorProvider(sae, 1, true);
        results.get(sae.getInputLayer()).set(1, 0, 0);
        results.get(sae.getInputLayer()).set(0, 0, 1);
        results.get(sae.getInputLayer()).set(1, 0, 2);
        sae.getLayerCalculator().calculate(sae, sae.getOutputLayer(), calculatedLayers, results);
        Assert.assertEquals(1.06, results.get(sae.getOutputLayer()).get(0, 0), 1.0E-5);
        Assert.assertEquals(1.06, results.get(sae.getOutputLayer()).get(0, 1), 1.0E-5);
    }

    @Test
    public void testDNNLayerTrainer() {
        DBN dbn = NNFactory.dbn(new int[]{ 3, 2, 2 }, true);
        dbn.setLayerCalculator(CalculationFactory.lcSigmoid(dbn, null));
        RBM firstRBM = dbn.getFirstNeuralNetwork();
        Matrix cg1 = firstRBM.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set((-0.5F), 0, 2);
        cg1.set((-0.3F), 1, 0);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 1, 2);
        Matrix cgb1 = firstRBM.getVisibleBiasConnections().getWeights();
        cgb1.set(0.0F, 0, 0);
        cgb1.set(0.0F, 1, 0);
        cgb1.set(0.0F, 2, 0);
        Matrix cgb2 = firstRBM.getHiddenBiasConnections().getWeights();
        cgb2.set((-0.4F), 0, 0);
        cgb2.set(0.2F, 1, 0);
        SimpleInputProvider inputProvider = new SimpleInputProvider(new float[][]{ new float[]{ 1, 0, 1 } });
        CDTrainerBase firstTrainer = TrainerFactory.cdSigmoidTrainer(firstRBM, null, null, null, null, 1.0F, 0.0F, 0.0F, 0.0F, 1, 1, 1, true);
        RBM secondRBM = dbn.getLastNeuralNetwork();
        CDTrainerBase secondTrainer = TrainerFactory.cdSigmoidTrainer(secondRBM, null, null, null, null, 1.0F, 0.0F, 0.0F, 0.0F, 1, 1, 1, true);
        Map<NeuralNetwork, OneStepTrainer<?>> layerTrainers = new HashMap<>();
        layerTrainers.put(firstRBM, firstTrainer);
        layerTrainers.put(secondRBM, secondTrainer);
        DNNLayerTrainer trainer = TrainerFactory.dnnLayerTrainer(dbn, layerTrainers, inputProvider, null, null);
        trainer.train();
        Assert.assertEquals((0.2 + 0.13203661), cg1.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.4 - 0.22863509), cg1.get(0, 1), 1.0E-5);
        Assert.assertEquals(((-0.5) + 0.12887852), cg1.get(0, 2), 1.0E-5);
        Assert.assertEquals(((-0.3) + 0.26158813), cg1.get(1, 0), 1.0E-5);
        Assert.assertEquals((0.1 - 0.3014404), cg1.get(1, 1), 1.0E-5);
        Assert.assertEquals((0.2 + 0.25742438), cg1.get(1, 2), 1.0E-5);
        Assert.assertEquals(0.52276707, cgb1.get(0, 0), 1.0E-5);
        Assert.assertEquals((-0.54617375), cgb1.get(1, 0), 1.0E-5);
        Assert.assertEquals(0.51522285, cgb1.get(2, 0), 1.0E-5);
        Assert.assertEquals(((-0.4) - 0.08680013), cgb2.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.2 - 0.02693379), cgb2.get(1, 0), 1.0E-5);
    }

    @Test
    public void testDNNLayerTrainer2() {
        DBN dbn = NNFactory.dbn(new int[]{ 3, 3, 2 }, true);
        dbn.setLayerCalculator(CalculationFactory.lcSigmoid(dbn, null));
        RBM firstRBM = dbn.getFirstNeuralNetwork();
        LayerCalculatorImpl lc = ((LayerCalculatorImpl) (dbn.getLayerCalculator()));
        lc.addConnectionCalculator(firstRBM.getHiddenLayer(), OperationsFactory.weightedSum());
        Matrix m1 = firstRBM.getMainConnections().getWeights();
        m1.set(1, 0, 0);
        m1.set(0, 0, 1);
        m1.set(0, 0, 2);
        m1.set(0, 1, 0);
        m1.set(1, 1, 1);
        m1.set(0, 1, 2);
        m1.set(0, 2, 0);
        m1.set(0, 2, 1);
        m1.set(1, 2, 2);
        RBM secondRBM = dbn.getLastNeuralNetwork();
        Matrix cg1 = secondRBM.getMainConnections().getWeights();
        cg1.set(0.2F, 0, 0);
        cg1.set(0.4F, 0, 1);
        cg1.set((-0.5F), 0, 2);
        cg1.set((-0.3F), 1, 0);
        cg1.set(0.1F, 1, 1);
        cg1.set(0.2F, 1, 2);
        Matrix cgb1 = secondRBM.getVisibleBiasConnections().getWeights();
        cgb1.set(0.0F, 0, 0);
        cgb1.set(0.0F, 1, 0);
        cgb1.set(0.0F, 2, 0);
        Matrix cgb2 = secondRBM.getHiddenBiasConnections().getWeights();
        cgb2.set((-0.4F), 0, 0);
        cgb2.set(0.2F, 1, 0);
        SimpleInputProvider inputProvider = new SimpleInputProvider(new float[][]{ new float[]{ 1, 0, 1 } });
        CDTrainerBase firstTrainer = TrainerFactory.cdSigmoidTrainer(firstRBM, null, null, null, null, 0.0F, 0.0F, 0.0F, 0.0F, 0, 1, 1, true);
        CDTrainerBase secondTrainer = TrainerFactory.cdSigmoidTrainer(secondRBM, null, null, null, null, 1.0F, 0.0F, 0.0F, 0.0F, 1, 1, 1, true);
        Map<NeuralNetwork, OneStepTrainer<?>> layerTrainers = new HashMap<>();
        layerTrainers.put(firstRBM, firstTrainer);
        layerTrainers.put(secondRBM, secondTrainer);
        DNNLayerTrainer trainer = TrainerFactory.dnnLayerTrainer(dbn, layerTrainers, inputProvider, null, null);
        trainer.train();
        Assert.assertEquals((0.2 + 0.13203661), cg1.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.4 - 0.22863509), cg1.get(0, 1), 1.0E-5);
        Assert.assertEquals(((-0.5) + 0.12887852), cg1.get(0, 2), 1.0E-5);
        Assert.assertEquals(((-0.3) + 0.26158813), cg1.get(1, 0), 1.0E-5);
        Assert.assertEquals((0.1 - 0.3014404), cg1.get(1, 1), 1.0E-5);
        Assert.assertEquals((0.2 + 0.25742438), cg1.get(1, 2), 1.0E-5);
        Assert.assertEquals(0.52276707, cgb1.get(0, 0), 1.0E-5);
        Assert.assertEquals((-0.54617375), cgb1.get(1, 0), 1.0E-5);
        Assert.assertEquals(0.51522285, cgb1.get(2, 0), 1.0E-5);
        Assert.assertEquals(((-0.4) - 0.08680013), cgb2.get(0, 0), 1.0E-5);
        Assert.assertEquals((0.2 - 0.02693379), cgb2.get(1, 0), 1.0E-5);
    }
}

