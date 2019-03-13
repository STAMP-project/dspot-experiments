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
package org.deeplearning4j.spark.impl.graph;


import Activation.SOFTMAX;
import Activation.TANH;
import DataType.DOUBLE;
import ElementWiseVertex.Op;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import RDDTrainingApproach.Direct;
import Repartition.Always;
import Updater.RMSPROP;
import WeightInit.XAVIER;
import WeightInit.XAVIER_UNIFORM;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.val;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.evaluation.IEvaluation;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.classification.ROC;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import scala.Tuple2;


public class TestSparkComputationGraph extends BaseSparkTest {
    @Test
    public void testBasic() throws Exception {
        JavaSparkContext sc = this.sc;
        RecordReader rr = new CSVRecordReader(0, ',');
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        MultiDataSetIterator iter = new RecordReaderMultiDataSetIterator.Builder(1).addReader("iris", rr).addInput("iris", 0, 3).addOutputOneHot("iris", 4, 3).build();
        List<MultiDataSet> list = new ArrayList<>(150);
        while (iter.hasNext())
            list.add(iter.next());

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).graphBuilder().addInputs("in").addLayer("dense", new DenseLayer.Builder().nIn(4).nOut(2).build(), "in").addLayer("out", nIn(2).nOut(3).build(), "dense").setOutputs("out").build();
        ComputationGraph cg = new ComputationGraph(config);
        cg.init();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        SparkComputationGraph scg = new SparkComputationGraph(sc, cg, tm);
        scg.setListeners(Collections.singleton(((TrainingListener) (new ScoreIterationListener(1)))));
        JavaRDD<MultiDataSet> rdd = sc.parallelize(list);
        scg.fitMultiDataSet(rdd);
        // Try: fitting using DataSet
        DataSetIterator iris = new IrisDataSetIterator(1, 150);
        List<DataSet> list2 = new ArrayList<>();
        while (iris.hasNext())
            list2.add(iris.next());

        JavaRDD<DataSet> rddDS = sc.parallelize(list2);
        scg.fit(rddDS);
    }

    @Test
    public void testDistributedScoring() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().l1(0.1).l2(0.1).seed(123).updater(new Nesterovs(0.1, 0.9)).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(nIn).nOut(3).activation(TANH).build(), "in").addLayer("1", nIn(3).nOut(nOut).activation(SOFTMAX).build(), "0").setOutputs("1").build();
        TrainingMaster tm = new ParameterAveragingTrainingMaster(true, numExecutors(), 1, 10, 1, 0);
        SparkComputationGraph sparkNet = new SparkComputationGraph(sc, conf, tm);
        ComputationGraph netCopy = sparkNet.getNetwork().clone();
        int nRows = 100;
        INDArray features = Nd4j.rand(nRows, nIn);
        INDArray labels = Nd4j.zeros(nRows, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < nRows; i++) {
            labels.putScalar(new int[]{ i, r.nextInt(nOut) }, 1.0);
        }
        INDArray localScoresWithReg = netCopy.scoreExamples(new DataSet(features, labels), true);
        INDArray localScoresNoReg = netCopy.scoreExamples(new DataSet(features, labels), false);
        List<Tuple2<String, DataSet>> dataWithKeys = new ArrayList<>();
        for (int i = 0; i < nRows; i++) {
            DataSet ds = new DataSet(features.getRow(i).dup(), labels.getRow(i).dup());
            dataWithKeys.add(new Tuple2(String.valueOf(i), ds));
        }
        JavaPairRDD<String, DataSet> dataWithKeysRdd = sc.parallelizePairs(dataWithKeys);
        JavaPairRDD<String, Double> sparkScoresWithReg = sparkNet.scoreExamples(dataWithKeysRdd, true, 4);
        JavaPairRDD<String, Double> sparkScoresNoReg = sparkNet.scoreExamples(dataWithKeysRdd, false, 4);
        Map<String, Double> sparkScoresWithRegMap = sparkScoresWithReg.collectAsMap();
        Map<String, Double> sparkScoresNoRegMap = sparkScoresNoReg.collectAsMap();
        for (int i = 0; i < nRows; i++) {
            double scoreRegExp = localScoresWithReg.getDouble(i);
            double scoreRegAct = sparkScoresWithRegMap.get(String.valueOf(i));
            Assert.assertEquals(scoreRegExp, scoreRegAct, 1.0E-5);
            double scoreNoRegExp = localScoresNoReg.getDouble(i);
            double scoreNoRegAct = sparkScoresNoRegMap.get(String.valueOf(i));
            Assert.assertEquals(scoreNoRegExp, scoreNoRegAct, 1.0E-5);
            // System.out.println(scoreRegExp + "\t" + scoreRegAct + "\t" + scoreNoRegExp + "\t" + scoreNoRegAct);
        }
        List<DataSet> dataNoKeys = new ArrayList<>();
        for (int i = 0; i < nRows; i++) {
            dataNoKeys.add(new DataSet(features.getRow(i).dup(), labels.getRow(i).dup()));
        }
        JavaRDD<DataSet> dataNoKeysRdd = sc.parallelize(dataNoKeys);
        List<Double> scoresWithReg = new ArrayList(sparkNet.scoreExamples(dataNoKeysRdd, true, 4).collect());
        List<Double> scoresNoReg = new ArrayList(sparkNet.scoreExamples(dataNoKeysRdd, false, 4).collect());
        Collections.sort(scoresWithReg);
        Collections.sort(scoresNoReg);
        double[] localScoresWithRegDouble = localScoresWithReg.data().asDouble();
        double[] localScoresNoRegDouble = localScoresNoReg.data().asDouble();
        Arrays.sort(localScoresWithRegDouble);
        Arrays.sort(localScoresNoRegDouble);
        for (int i = 0; i < (localScoresWithRegDouble.length); i++) {
            Assert.assertEquals(localScoresWithRegDouble[i], scoresWithReg.get(i), 1.0E-5);
            Assert.assertEquals(localScoresNoRegDouble[i], scoresNoReg.get(i), 1.0E-5);
            // System.out.println(localScoresWithRegDouble[i] + "\t" + scoresWithReg.get(i) + "\t" + localScoresNoRegDouble[i] + "\t" + scoresNoReg.get(i));
        }
    }

    @Test
    public void testSeedRepeatability() throws Exception {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(RMSPROP).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).weightInit(XAVIER).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build(), "in").addLayer("1", nIn(4).nOut(3).activation(SOFTMAX).build(), "0").setOutputs("1").build();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph n1 = new ComputationGraph(conf.clone());
        n1.init();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph n2 = new ComputationGraph(conf.clone());
        n2.init();
        Nd4j.getRandom().setSeed(12345);
        ComputationGraph n3 = new ComputationGraph(conf.clone());
        n3.init();
        SparkComputationGraph sparkNet1 = new SparkComputationGraph(sc, n1, new ParameterAveragingTrainingMaster.Builder(1).workerPrefetchNumBatches(5).batchSizePerWorker(5).averagingFrequency(1).repartionData(Always).rngSeed(12345).build());
        Thread.sleep(100);// Training master IDs are only unique if they are created at least 1 ms apart...

        SparkComputationGraph sparkNet2 = new SparkComputationGraph(sc, n2, new ParameterAveragingTrainingMaster.Builder(1).workerPrefetchNumBatches(5).batchSizePerWorker(5).averagingFrequency(1).repartionData(Always).rngSeed(12345).build());
        Thread.sleep(100);
        SparkComputationGraph sparkNet3 = new SparkComputationGraph(sc, n3, new ParameterAveragingTrainingMaster.Builder(1).workerPrefetchNumBatches(5).batchSizePerWorker(5).averagingFrequency(1).repartionData(Always).rngSeed(98765).build());
        List<DataSet> data = new ArrayList<>();
        DataSetIterator iter = new IrisDataSetIterator(1, 150);
        while (iter.hasNext())
            data.add(iter.next());

        JavaRDD<DataSet> rdd = sc.parallelize(data);
        sparkNet1.fit(rdd);
        sparkNet2.fit(rdd);
        sparkNet3.fit(rdd);
        INDArray p1 = sparkNet1.getNetwork().params();
        INDArray p2 = sparkNet2.getNetwork().params();
        INDArray p3 = sparkNet3.getNetwork().params();
        sparkNet1.getTrainingMaster().deleteTempFiles(sc);
        sparkNet2.getTrainingMaster().deleteTempFiles(sc);
        sparkNet3.getTrainingMaster().deleteTempFiles(sc);
        Assert.assertEquals(p1, p2);
        Assert.assertNotEquals(p1, p3);
    }

    @Test(timeout = 60000L)
    public void testEvaluationAndRoc() {
        for (int evalWorkers : new int[]{ 1, 4, 8 }) {
            DataSetIterator iter = new IrisDataSetIterator(5, 150);
            // Make a 2-class version of iris:
            List<DataSet> l = new ArrayList<>();
            iter.reset();
            while (iter.hasNext()) {
                DataSet ds = iter.next();
                INDArray newL = Nd4j.create(ds.getLabels().size(0), 2);
                newL.putColumn(0, ds.getLabels().getColumn(0));
                newL.putColumn(1, ds.getLabels().getColumn(1));
                newL.getColumn(1).addi(ds.getLabels().getColumn(2));
                ds.setLabels(newL);
                l.add(ds);
            } 
            iter = new org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator(l);
            ComputationGraph cg = TestSparkComputationGraph.getBasicNetIris2Class();
            Evaluation e = cg.evaluate(iter);
            ROC roc = cg.evaluateROC(iter, 32);
            SparkComputationGraph scg = new SparkComputationGraph(sc, cg, null);
            scg.setDefaultEvaluationWorkers(evalWorkers);
            JavaRDD<DataSet> rdd = sc.parallelize(l);
            rdd = rdd.repartition(20);
            Evaluation e2 = scg.evaluate(rdd);
            ROC roc2 = scg.evaluateROC(rdd);
            Assert.assertEquals(e2.accuracy(), e.accuracy(), 0.001);
            Assert.assertEquals(e2.f1(), e.f1(), 0.001);
            Assert.assertEquals(e2.getNumRowCounter(), e.getNumRowCounter(), 0.001);
            Assert.assertEquals(e2.falseNegatives(), e.falseNegatives());
            Assert.assertEquals(e2.falsePositives(), e.falsePositives());
            Assert.assertEquals(e2.trueNegatives(), e.trueNegatives());
            Assert.assertEquals(e2.truePositives(), e.truePositives());
            Assert.assertEquals(e2.precision(), e.precision(), 0.001);
            Assert.assertEquals(e2.recall(), e.recall(), 0.001);
            Assert.assertEquals(e2.getConfusionMatrix(), e.getConfusionMatrix());
            Assert.assertEquals(roc.calculateAUC(), roc2.calculateAUC(), 1.0E-5);
            Assert.assertEquals(roc.calculateAUCPR(), roc2.calculateAUCPR(), 1.0E-5);
        }
    }

    @Test
    public void testEvaluationAndRocMDS() {
        for (int evalWorkers : new int[]{ 1, 4, 8 }) {
            DataSetIterator iter = new IrisDataSetIterator(5, 150);
            // Make a 2-class version of iris:
            List<MultiDataSet> l = new ArrayList<>();
            iter.reset();
            while (iter.hasNext()) {
                DataSet ds = iter.next();
                INDArray newL = Nd4j.create(ds.getLabels().size(0), 2);
                newL.putColumn(0, ds.getLabels().getColumn(0));
                newL.putColumn(1, ds.getLabels().getColumn(1));
                newL.getColumn(1).addi(ds.getLabels().getColumn(2));
                MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(ds.getFeatures(), newL);
                l.add(mds);
            } 
            MultiDataSetIterator mdsIter = new org.deeplearning4j.datasets.iterator.IteratorMultiDataSetIterator(l.iterator(), 5);
            ComputationGraph cg = TestSparkComputationGraph.getBasicNetIris2Class();
            IEvaluation[] es = cg.doEvaluation(mdsIter, new Evaluation(), new ROC(32));
            Evaluation e = ((Evaluation) (es[0]));
            ROC roc = ((ROC) (es[1]));
            SparkComputationGraph scg = new SparkComputationGraph(sc, cg, null);
            scg.setDefaultEvaluationWorkers(evalWorkers);
            JavaRDD<MultiDataSet> rdd = sc.parallelize(l);
            rdd = rdd.repartition(20);
            IEvaluation[] es2 = scg.doEvaluationMDS(rdd, 5, new Evaluation(), new ROC(32));
            Evaluation e2 = ((Evaluation) (es2[0]));
            ROC roc2 = ((ROC) (es2[1]));
            Assert.assertEquals(e2.accuracy(), e.accuracy(), 0.001);
            Assert.assertEquals(e2.f1(), e.f1(), 0.001);
            Assert.assertEquals(e2.getNumRowCounter(), e.getNumRowCounter(), 0.001);
            Assert.assertEquals(e2.falseNegatives(), e.falseNegatives());
            Assert.assertEquals(e2.falsePositives(), e.falsePositives());
            Assert.assertEquals(e2.trueNegatives(), e.trueNegatives());
            Assert.assertEquals(e2.truePositives(), e.truePositives());
            Assert.assertEquals(e2.precision(), e.precision(), 0.001);
            Assert.assertEquals(e2.recall(), e.recall(), 0.001);
            Assert.assertEquals(e2.getConfusionMatrix(), e.getConfusionMatrix());
            Assert.assertEquals(roc.calculateAUC(), roc2.calculateAUC(), 1.0E-5);
            Assert.assertEquals(roc.calculateAUCPR(), roc2.calculateAUCPR(), 1.0E-5);
        }
    }

    @Test
    public void testIssue7068() throws Exception {
        val batchSize = 5;
        val featSize = 10;
        val labelSize = 2;
        val random = new Random(0);
        List<MultiDataSet> l = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            org.nd4j.linalg.dataset.MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(new INDArray[]{ Nd4j.rand(batchSize, featSize).castTo(DOUBLE), Nd4j.rand(batchSize, featSize).castTo(DOUBLE) }, new INDArray[]{ Nd4j.rand(batchSize, labelSize).castTo(DOUBLE) });
            l.add(mds);
        }
        JavaRDD<MultiDataSet> rdd = sc.parallelize(l);
        // simple model
        val modelConf = new NeuralNetConfiguration.Builder().updater(new Adam(0.01)).weightInit(XAVIER_UNIFORM).biasInit(0).graphBuilder().addInputs("input1", "input2").addVertex("avg", new org.deeplearning4j.nn.conf.graph.ElementWiseVertex(Op.Average), "input1", "input2").addLayer("dense", new DenseLayer.Builder().dropOut(0.9).nIn(featSize).nOut((featSize / 2)).build(), "avg").addLayer("output", new OutputLayer.Builder().nIn((featSize / 2)).nOut(2).lossFunction(MCXENT).activation(SOFTMAX).hasBias(false).build(), "dense").setOutputs("output").build();
        val model = new ComputationGraph(modelConf);
        model.init();
        val trainingMaster = new ParameterAveragingTrainingMaster.Builder(batchSize).rddTrainingApproach(Direct).build();
        val sparkModel = new SparkComputationGraph(sc, model, trainingMaster);
        for (int i = 0; i < 3; i++) {
            sparkModel.fitMultiDataSet(rdd);
        }
    }
}

