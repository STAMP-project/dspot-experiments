/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.nn.modelimport.keras.configurations;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.nn.layers.recurrent.LSTM;
import org.deeplearning4j.nn.modelimport.keras.KerasModel;
import org.deeplearning4j.nn.modelimport.keras.KerasSequentialModel;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.impl.ActivationHardSigmoid;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.io.ClassPathResource;


public class FullModelComparisons {
    ClassLoader classLoader = FullModelComparisons.class.getClassLoader();

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void lstmTest() throws IOException, InterruptedException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        String modelPath = "modelimport/keras/fullconfigs/lstm/lstm_th_keras_2_config.json";
        String weightsPath = "modelimport/keras/fullconfigs/lstm/lstm_th_keras_2_weights.h5";
        ClassPathResource modelResource = new ClassPathResource(modelPath, classLoader);
        ClassPathResource weightsResource = new ClassPathResource(weightsPath, classLoader);
        KerasSequentialModel kerasModel = new KerasModel().modelBuilder().modelJsonInputStream(modelResource.getInputStream()).weightsHdf5FilenameNoRoot(weightsResource.getFile().getAbsolutePath()).enforceTrainingConfig(false).buildSequential();
        MultiLayerNetwork model = kerasModel.getMultiLayerNetwork();
        model.init();
        System.out.println(model.summary());
        // 1. Layer
        LSTM firstLstm = ((LSTM) (model.getLayer(0)));
        org.deeplearning4j.nn.conf.layers.LSTM firstConf = ((org.deeplearning4j.nn.conf.layers.LSTM) (firstLstm.conf().getLayer()));
        // "unit_forget_bias": true
        TestCase.assertTrue(((firstConf.getForgetGateBiasInit()) == 1.0));
        TestCase.assertTrue(((firstConf.getGateActivationFn()) instanceof ActivationHardSigmoid));
        TestCase.assertTrue(((firstConf.getActivationFn()) instanceof ActivationTanH));
        int nIn = 12;
        int nOut = 96;
        // Need to convert from IFCO to CFOI order
        // 
        INDArray W = firstLstm.getParam("W");
        TestCase.assertTrue(Arrays.equals(W.shape(), new long[]{ nIn, 4 * nOut }));
        TestCase.assertEquals(W.getDouble(0, 288), (-0.30737767), 1.0E-7);
        TestCase.assertEquals(W.getDouble(0, 289), (-0.5845409), 1.0E-7);
        TestCase.assertEquals(W.getDouble(1, 288), (-0.44083247), 1.0E-7);
        TestCase.assertEquals(W.getDouble(11, 288), 0.017539706, 1.0E-7);
        TestCase.assertEquals(W.getDouble(0, 96), 0.2707935, 1.0E-7);
        TestCase.assertEquals(W.getDouble(0, 192), (-0.19856165), 1.0E-7);
        TestCase.assertEquals(W.getDouble(0, 0), 0.15368782, 1.0E-7);
        INDArray RW = firstLstm.getParam("RW");
        TestCase.assertTrue(Arrays.equals(RW.shape(), new long[]{ nOut, 4 * nOut }));
        TestCase.assertEquals(RW.getDouble(0, 288), 0.15112677, 1.0E-7);
        INDArray b = firstLstm.getParam("b");
        TestCase.assertTrue(Arrays.equals(b.shape(), new long[]{ 1, 4 * nOut }));
        TestCase.assertEquals(b.getDouble(0, 288), (-0.36940336), 1.0E-7);// Keras I

        TestCase.assertEquals(b.getDouble(0, 96), 0.6031118, 1.0E-7);// Keras F

        TestCase.assertEquals(b.getDouble(0, 192), (-0.13569744), 1.0E-7);// Keras O

        TestCase.assertEquals(b.getDouble(0, 0), (-0.2587392), 1.0E-7);// Keras C

        // 2. Layer
        LSTM secondLstm = ((LSTM) (getUnderlying()));
        org.deeplearning4j.nn.conf.layers.LSTM secondConf = ((org.deeplearning4j.nn.conf.layers.LSTM) (secondLstm.conf().getLayer()));
        // "unit_forget_bias": true
        TestCase.assertTrue(((secondConf.getForgetGateBiasInit()) == 1.0));
        TestCase.assertTrue(((firstConf.getGateActivationFn()) instanceof ActivationHardSigmoid));
        TestCase.assertTrue(((firstConf.getActivationFn()) instanceof ActivationTanH));
        nIn = 96;
        nOut = 96;
        W = secondLstm.getParam("W");
        TestCase.assertTrue(Arrays.equals(W.shape(), new long[]{ nIn, 4 * nOut }));
        TestCase.assertEquals(W.getDouble(0, 288), (-0.7559755), 1.0E-7);
        RW = secondLstm.getParam("RW");
        TestCase.assertTrue(Arrays.equals(RW.shape(), new long[]{ nOut, 4 * nOut }));
        TestCase.assertEquals(RW.getDouble(0, 288), (-0.33184892), 1.0E-7);
        b = secondLstm.getParam("b");
        TestCase.assertTrue(Arrays.equals(b.shape(), new long[]{ 1, 4 * nOut }));
        TestCase.assertEquals(b.getDouble(0, 288), (-0.2223678), 1.0E-7);
        TestCase.assertEquals(b.getDouble(0, 96), 0.73556226, 1.0E-7);
        TestCase.assertEquals(b.getDouble(0, 192), (-0.63227624), 1.0E-7);
        TestCase.assertEquals(b.getDouble(0, 0), 0.06636357, 1.0E-7);
        File dataDir = testDir.newFolder();
        SequenceRecordReader reader = new CSVSequenceRecordReader(0, ";");
        new ClassPathResource("deeplearning4j-modelimport/data/", classLoader).copyDirectory(dataDir);
        reader.initialize(new NumberedFileInputSplit(((dataDir.getAbsolutePath()) + "/sequences/%d.csv"), 0, 282));
        DataSetIterator dataSetIterator = new org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator(reader, 1, (-1), 12, true);
        List<Double> preds = new LinkedList<>();
        while (dataSetIterator.hasNext()) {
            DataSet dataSet = dataSetIterator.next();
            INDArray sequence = dataSet.getFeatures().get(NDArrayIndex.point(0)).transpose();
            INDArray bsSequence = sequence.reshape(1, 4, 12);// one batch

            INDArray permuteSequence = bsSequence.permute(0, 2, 1);
            INDArray pred = model.output(permuteSequence);
            TestCase.assertTrue(Arrays.equals(pred.shape(), new long[]{ 1, 1 }));
            preds.add(pred.getDouble(0, 0));
        } 
        INDArray dl4jPredictions = Nd4j.create(preds);
        ClassPathResource predResource = new ClassPathResource("modelimport/keras/fullconfigs/lstm/predictions.npy", classLoader);
        INDArray kerasPredictions = Nd4j.createFromNpyFile(predResource.getFile());
        for (int i = 0; i < 283; i++) {
            TestCase.assertEquals(kerasPredictions.getDouble(i), dl4jPredictions.getDouble(i), 1.0E-7);
        }
        INDArray ones = Nd4j.ones(1, 12, 4);
        INDArray predOnes = model.output(ones);
        TestCase.assertEquals(predOnes.getDouble(0, 0), 0.7216, 1.0E-4);
    }

    @Test
    public void cnnBatchNormTest() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        String modelPath = "modelimport/keras/fullconfigs/cnn/cnn_batch_norm.h5";
        ClassPathResource modelResource = new ClassPathResource(modelPath, classLoader);
        KerasSequentialModel kerasModel = new KerasModel().modelBuilder().modelHdf5Filename(modelResource.getFile().getAbsolutePath()).enforceTrainingConfig(false).buildSequential();
        MultiLayerNetwork model = kerasModel.getMultiLayerNetwork();
        model.init();
        System.out.println(model.summary());
        ClassPathResource inputResource = new ClassPathResource("modelimport/keras/fullconfigs/cnn/input.npy", classLoader);
        INDArray input = Nd4j.createFromNpyFile(inputResource.getFile());
        input = input.permute(0, 3, 1, 2);
        TestCase.assertTrue(Arrays.equals(input.shape(), new long[]{ 5, 3, 10, 10 }));
        INDArray output = model.output(input);
        ClassPathResource outputResource = new ClassPathResource("modelimport/keras/fullconfigs/cnn/predictions.npy", classLoader);
        INDArray kerasOutput = Nd4j.createFromNpyFile(outputResource.getFile());
        for (int i = 0; i < 5; i++) {
            TestCase.assertEquals(output.getDouble(i), kerasOutput.getDouble(i), 1.0E-4);
        }
    }

    @Test
    public void cnnBatchNormLargerTest() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        String modelPath = "modelimport/keras/fullconfigs/cnn_batch_norm/cnn_batch_norm_medium.h5";
        ClassPathResource modelResource = new ClassPathResource(modelPath, classLoader);
        KerasSequentialModel kerasModel = new KerasModel().modelBuilder().modelHdf5Filename(modelResource.getFile().getAbsolutePath()).enforceTrainingConfig(false).buildSequential();
        MultiLayerNetwork model = kerasModel.getMultiLayerNetwork();
        model.init();
        System.out.println(model.summary());
        ClassPathResource inputResource = new ClassPathResource("modelimport/keras/fullconfigs/cnn_batch_norm/input.npy", classLoader);
        INDArray input = Nd4j.createFromNpyFile(inputResource.getFile());
        input = input.permute(0, 3, 1, 2);
        TestCase.assertTrue(Arrays.equals(input.shape(), new long[]{ 5, 1, 48, 48 }));
        INDArray output = model.output(input);
        ClassPathResource outputResource = new ClassPathResource("modelimport/keras/fullconfigs/cnn_batch_norm/predictions.npy", classLoader);
        INDArray kerasOutput = Nd4j.createFromNpyFile(outputResource.getFile());
        for (int i = 0; i < 5; i++) {
            // TODO this should be a little closer
            TestCase.assertEquals(output.getDouble(i), kerasOutput.getDouble(i), 0.01);
        }
    }
}

