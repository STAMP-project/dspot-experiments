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
package org.deeplearning4j.spark.datavec;


import Activation.SOFTMAX;
import Activation.TANH;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import Repartition.Always;
import Updater.RMSPROP;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.input.PortableDataStream;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer.Builder;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.api.stats.SparkTrainingStats;
import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 03/07/2016.
 */
public class TestPreProcessedData extends BaseSparkTest {
    @Test
    public void testPreprocessedData() {
        // Test _loading_ of preprocessed data
        int dataSetObjSize = 5;
        int batchSizePerExecutor = 10;
        String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_testpreprocdata");
        File f = new File(path);
        if (f.exists())
            f.delete();

        f.mkdir();
        DataSetIterator iter = new IrisDataSetIterator(5, 150);
        int i = 0;
        while (iter.hasNext()) {
            File f2 = new File(FilenameUtils.concat(path, (("data" + (i++)) + ".bin")));
            iter.next().save(f2);
        } 
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(RMSPROP).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).list().layer(0, new Builder().nIn(4).nOut(3).activation(TANH).build()).layer(1, nIn(3).nOut(3).activation(SOFTMAX).build()).build();
        SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, conf, new ParameterAveragingTrainingMaster.Builder(numExecutors(), dataSetObjSize).batchSizePerWorker(batchSizePerExecutor).averagingFrequency(1).repartionData(Always).build());
        sparkNet.setCollectTrainingStats(true);
        sparkNet.fit(("file:///" + (path.replaceAll("\\\\", "/"))));
        SparkTrainingStats sts = sparkNet.getSparkTrainingStats();
        int expNumFits = 12;// 4 'fits' per averaging (4 executors, 1 averaging freq); 10 examples each -> 40 examples per fit. 150/40 = 3 averagings (round down); 3*4 = 12

        // Unfortunately: perfect partitioning isn't guaranteed by SparkUtils.balancedRandomSplit (esp. if original partitions are all size 1
        // which appears to be occurring at least some of the time), but we should get close to what we expect...
        Assert.assertTrue(((Math.abs((expNumFits - (sts.getValue("ParameterAveragingWorkerFitTimesMs").size())))) < 3));
        Assert.assertEquals(3, sts.getValue("ParameterAveragingMasterMapPartitionsTimesMs").size());
    }

    @Test
    public void testPreprocessedDataCompGraphDataSet() {
        // Test _loading_ of preprocessed DataSet data
        int dataSetObjSize = 5;
        int batchSizePerExecutor = 10;
        String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_testpreprocdata2");
        File f = new File(path);
        if (f.exists())
            f.delete();

        f.mkdir();
        DataSetIterator iter = new IrisDataSetIterator(5, 150);
        int i = 0;
        while (iter.hasNext()) {
            File f2 = new File(FilenameUtils.concat(path, (("data" + (i++)) + ".bin")));
            iter.next().save(f2);
        } 
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(RMSPROP).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(4).nOut(3).activation(TANH).build(), "in").addLayer("1", nIn(3).nOut(3).activation(SOFTMAX).build(), "0").setOutputs("1").build();
        SparkComputationGraph sparkNet = new SparkComputationGraph(sc, conf, new ParameterAveragingTrainingMaster.Builder(numExecutors(), dataSetObjSize).batchSizePerWorker(batchSizePerExecutor).averagingFrequency(1).repartionData(Always).build());
        sparkNet.setCollectTrainingStats(true);
        sparkNet.fit(("file:///" + (path.replaceAll("\\\\", "/"))));
        SparkTrainingStats sts = sparkNet.getSparkTrainingStats();
        int expNumFits = 12;// 4 'fits' per averaging (4 executors, 1 averaging freq); 10 examples each -> 40 examples per fit. 150/40 = 3 averagings (round down); 3*4 = 12

        // Unfortunately: perfect partitioning isn't guaranteed by SparkUtils.balancedRandomSplit (esp. if original partitions are all size 1
        // which appears to be occurring at least some of the time), but we should get close to what we expect...
        Assert.assertTrue(((Math.abs((expNumFits - (sts.getValue("ParameterAveragingWorkerFitTimesMs").size())))) < 3));
        Assert.assertEquals(3, sts.getValue("ParameterAveragingMasterMapPartitionsTimesMs").size());
    }

    @Test
    public void testPreprocessedDataCompGraphMultiDataSet() throws IOException {
        // Test _loading_ of preprocessed MultiDataSet data
        int dataSetObjSize = 5;
        int batchSizePerExecutor = 10;
        String path = FilenameUtils.concat(System.getProperty("java.io.tmpdir"), "dl4j_testpreprocdata3");
        File f = new File(path);
        if (f.exists())
            f.delete();

        f.mkdir();
        DataSetIterator iter = new IrisDataSetIterator(5, 150);
        int i = 0;
        while (iter.hasNext()) {
            File f2 = new File(FilenameUtils.concat(path, (("data" + (i++)) + ".bin")));
            DataSet ds = iter.next();
            MultiDataSet mds = new MultiDataSet(ds.getFeatures(), ds.getLabels());
            mds.save(f2);
        } 
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(RMSPROP).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).graphBuilder().addInputs("in").addLayer("0", new Builder().nIn(4).nOut(3).activation(TANH).build(), "in").addLayer("1", nIn(3).nOut(3).activation(SOFTMAX).build(), "0").setOutputs("1").build();
        SparkComputationGraph sparkNet = new SparkComputationGraph(sc, conf, new ParameterAveragingTrainingMaster.Builder(numExecutors(), dataSetObjSize).batchSizePerWorker(batchSizePerExecutor).averagingFrequency(1).repartionData(Always).build());
        sparkNet.setCollectTrainingStats(true);
        sparkNet.fitMultiDataSet(("file:///" + (path.replaceAll("\\\\", "/"))));
        SparkTrainingStats sts = sparkNet.getSparkTrainingStats();
        int expNumFits = 12;// 4 'fits' per averaging (4 executors, 1 averaging freq); 10 examples each -> 40 examples per fit. 150/40 = 3 averagings (round down); 3*4 = 12

        // Unfortunately: perfect partitioning isn't guaranteed by SparkUtils.balancedRandomSplit (esp. if original partitions are all size 1
        // which appears to be occurring at least some of the time), but we should get close to what we expect...
        Assert.assertTrue(((Math.abs((expNumFits - (sts.getValue("ParameterAveragingWorkerFitTimesMs").size())))) < 3));
        Assert.assertEquals(3, sts.getValue("ParameterAveragingMasterMapPartitionsTimesMs").size());
    }

    @Test
    public void testCsvPreprocessedDataGeneration() throws Exception {
        List<String> list = new ArrayList<>();
        DataSetIterator iter = new IrisDataSetIterator(1, 150);
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            list.add(TestPreProcessedData.toString(ds.getFeatures(), Nd4j.argMax(ds.getLabels(), 1).getInt(0)));
        } 
        JavaRDD<String> rdd = sc.parallelize(list);
        int partitions = rdd.partitions().size();
        URI tempDir = new File(System.getProperty("java.io.tmpdir")).toURI();
        URI outputDir = new URI(((tempDir.getPath()) + "/dl4j_testPreprocessedData2"));
        File temp = new File(outputDir.getPath());
        if (temp.exists())
            FileUtils.deleteDirectory(temp);

        int numBinFiles = 0;
        try {
            int batchSize = 5;
            int labelIdx = 4;
            int numPossibleLabels = 3;
            rdd.foreachPartition(new org.deeplearning4j.spark.datavec.export.StringToDataSetExportFunction(outputDir, new CSVRecordReader(0), batchSize, false, labelIdx, numPossibleLabels));
            File[] fileList = new File(outputDir.getPath()).listFiles();
            int totalExamples = 0;
            for (File f2 : fileList) {
                if (!(f2.getPath().endsWith(".bin")))
                    continue;

                // System.out.println(f2.getPath());
                numBinFiles++;
                DataSet ds = new DataSet();
                ds.load(f2);
                Assert.assertEquals(4, ds.numInputs());
                Assert.assertEquals(3, ds.numOutcomes());
                totalExamples += ds.numExamples();
            }
            Assert.assertEquals(150, totalExamples);
            Assert.assertTrue(((Math.abs(((150 / batchSize) - numBinFiles))) <= partitions));// Expect 30, give or take due to partitioning randomness

            // Test the PortableDataStreamDataSetIterator:
            JavaPairRDD<String, PortableDataStream> pds = sc.binaryFiles(outputDir.getPath());
            List<PortableDataStream> pdsList = pds.values().collect();
            DataSetIterator pdsIter = new org.deeplearning4j.spark.iterator.PortableDataStreamDataSetIterator(pdsList);
            int pdsCount = 0;
            int totalExamples2 = 0;
            while (pdsIter.hasNext()) {
                DataSet ds = pdsIter.next();
                pdsCount++;
                totalExamples2 += ds.numExamples();
                Assert.assertEquals(4, ds.numInputs());
                Assert.assertEquals(3, ds.numOutcomes());
            } 
            Assert.assertEquals(150, totalExamples2);
            Assert.assertEquals(numBinFiles, pdsCount);
        } finally {
            FileUtils.deleteDirectory(temp);
        }
    }

    @Test
    public void testCsvPreprocessedDataGenerationNoLabel() throws Exception {
        // Same as above test, but without any labels (in which case: input and output arrays are the same)
        List<String> list = new ArrayList<>();
        DataSetIterator iter = new IrisDataSetIterator(1, 150);
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            list.add(TestPreProcessedData.toString(ds.getFeatures(), Nd4j.argMax(ds.getLabels(), 1).getInt(0)));
        } 
        JavaRDD<String> rdd = sc.parallelize(list);
        int partitions = rdd.partitions().size();
        URI tempDir = new File(System.getProperty("java.io.tmpdir")).toURI();
        URI outputDir = new URI(((tempDir.getPath()) + "/dl4j_testPreprocessedData3"));
        File temp = new File(outputDir.getPath());
        if (temp.exists())
            FileUtils.deleteDirectory(temp);

        int numBinFiles = 0;
        try {
            int batchSize = 5;
            int labelIdx = -1;
            int numPossibleLabels = -1;
            rdd.foreachPartition(new org.deeplearning4j.spark.datavec.export.StringToDataSetExportFunction(outputDir, new CSVRecordReader(0), batchSize, false, labelIdx, numPossibleLabels));
            File[] fileList = new File(outputDir.getPath()).listFiles();
            int totalExamples = 0;
            for (File f2 : fileList) {
                if (!(f2.getPath().endsWith(".bin")))
                    continue;

                // System.out.println(f2.getPath());
                numBinFiles++;
                DataSet ds = new DataSet();
                ds.load(f2);
                Assert.assertEquals(5, ds.numInputs());
                Assert.assertEquals(5, ds.numOutcomes());
                totalExamples += ds.numExamples();
            }
            Assert.assertEquals(150, totalExamples);
            Assert.assertTrue(((Math.abs(((150 / batchSize) - numBinFiles))) <= partitions));// Expect 30, give or take due to partitioning randomness

            // Test the PortableDataStreamDataSetIterator:
            JavaPairRDD<String, PortableDataStream> pds = sc.binaryFiles(outputDir.getPath());
            List<PortableDataStream> pdsList = pds.values().collect();
            DataSetIterator pdsIter = new org.deeplearning4j.spark.iterator.PortableDataStreamDataSetIterator(pdsList);
            int pdsCount = 0;
            int totalExamples2 = 0;
            while (pdsIter.hasNext()) {
                DataSet ds = pdsIter.next();
                pdsCount++;
                totalExamples2 += ds.numExamples();
                Assert.assertEquals(5, ds.numInputs());
                Assert.assertEquals(5, ds.numOutcomes());
            } 
            Assert.assertEquals(150, totalExamples2);
            Assert.assertEquals(numBinFiles, pdsCount);
        } finally {
            FileUtils.deleteDirectory(temp);
        }
    }
}

