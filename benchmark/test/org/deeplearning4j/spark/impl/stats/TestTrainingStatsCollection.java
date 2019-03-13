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
package org.deeplearning4j.spark.impl.stats;


import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import Repartition.Always;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.spark.api.stats.CommonSparkTrainingStats;
import org.deeplearning4j.spark.api.stats.SparkTrainingStats;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.deeplearning4j.spark.impl.paramavg.stats.ParameterAveragingTrainingMasterStats;
import org.deeplearning4j.spark.impl.paramavg.stats.ParameterAveragingTrainingWorkerStats;
import org.deeplearning4j.spark.stats.EventStats;
import org.deeplearning4j.spark.stats.StatsUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 17/06/2016.
 */
public class TestTrainingStatsCollection {
    @Test
    public void testStatsCollection() throws Exception {
        int nWorkers = 4;
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster((("local[" + nWorkers) + "]"));
        sparkConf.setAppName("Test");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        try {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new OutputLayer.Builder().nIn(10).nOut(10).build()).build();
            int miniBatchSizePerWorker = 10;
            int averagingFrequency = 5;
            int numberOfAveragings = 3;
            int totalExamples = ((nWorkers * miniBatchSizePerWorker) * averagingFrequency) * numberOfAveragings;
            Nd4j.getRandom().setSeed(12345);
            List<DataSet> list = new ArrayList<>();
            for (int i = 0; i < totalExamples; i++) {
                INDArray f = Nd4j.rand(1, 10);
                INDArray l = Nd4j.rand(1, 10);
                DataSet ds = new DataSet(f, l);
                list.add(ds);
            }
            JavaRDD<DataSet> rdd = sc.parallelize(list);
            rdd.repartition(4);
            ParameterAveragingTrainingMaster tm = new ParameterAveragingTrainingMaster.Builder(nWorkers, 1).averagingFrequency(averagingFrequency).batchSizePerWorker(miniBatchSizePerWorker).saveUpdater(true).workerPrefetchNumBatches(0).repartionData(Always).build();
            SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, conf, tm);
            sparkNet.setCollectTrainingStats(true);
            sparkNet.fit(rdd);
            // Collect the expected keys:
            List<String> expectedStatNames = new ArrayList<>();
            Class<?>[] classes = new Class[]{ CommonSparkTrainingStats.class, ParameterAveragingTrainingMasterStats.class, ParameterAveragingTrainingWorkerStats.class };
            String[] fieldNames = new String[]{ "columnNames", "columnNames", "columnNames" };
            for (int i = 0; i < (classes.length); i++) {
                Field field = classes[i].getDeclaredField(fieldNames[i]);
                field.setAccessible(true);
                Object f = field.get(null);
                Collection<String> c = ((Collection<String>) (f));
                expectedStatNames.addAll(c);
            }
            System.out.println(expectedStatNames);
            SparkTrainingStats stats = sparkNet.getSparkTrainingStats();
            Set<String> actualKeySet = stats.getKeySet();
            Assert.assertEquals(expectedStatNames.size(), actualKeySet.size());
            for (String s : stats.getKeySet()) {
                TestCase.assertTrue(expectedStatNames.contains(s));
                TestCase.assertNotNull(stats.getValue(s));
            }
            String statsAsString = stats.statsAsString();
            System.out.println(statsAsString);
            Assert.assertEquals(actualKeySet.size(), statsAsString.split("\n").length);// One line per stat

            // Go through nested stats
            // First: master stats
            TestCase.assertTrue((stats instanceof ParameterAveragingTrainingMasterStats));
            ParameterAveragingTrainingMasterStats masterStats = ((ParameterAveragingTrainingMasterStats) (stats));
            List<EventStats> exportTimeStats = masterStats.getParameterAveragingMasterExportTimesMs();
            Assert.assertEquals(1, exportTimeStats.size());
            TestTrainingStatsCollection.assertDurationGreaterZero(exportTimeStats);
            TestTrainingStatsCollection.assertNonNullFields(exportTimeStats);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(exportTimeStats, 1, 1, 1);
            List<EventStats> countRddTime = masterStats.getParameterAveragingMasterCountRddSizeTimesMs();
            Assert.assertEquals(1, countRddTime.size());// occurs once per fit

            TestTrainingStatsCollection.assertDurationGreaterEqZero(countRddTime);
            TestTrainingStatsCollection.assertNonNullFields(countRddTime);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(countRddTime, 1, 1, 1);// should occur only in master once

            List<EventStats> broadcastCreateTime = masterStats.getParameterAveragingMasterBroadcastCreateTimesMs();
            Assert.assertEquals(numberOfAveragings, broadcastCreateTime.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(broadcastCreateTime);
            TestTrainingStatsCollection.assertNonNullFields(broadcastCreateTime);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(broadcastCreateTime, 1, 1, 1);// only 1 thread for master

            List<EventStats> fitTimes = masterStats.getParameterAveragingMasterFitTimesMs();
            Assert.assertEquals(1, fitTimes.size());// i.e., number of times fit(JavaRDD<DataSet>) was called

            TestTrainingStatsCollection.assertDurationGreaterZero(fitTimes);
            TestTrainingStatsCollection.assertNonNullFields(fitTimes);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(fitTimes, 1, 1, 1);// only 1 thread for master

            List<EventStats> splitTimes = masterStats.getParameterAveragingMasterSplitTimesMs();
            Assert.assertEquals(1, splitTimes.size());// Splitting of the data set is executed once only (i.e., one fit(JavaRDD<DataSet>) call)

            TestTrainingStatsCollection.assertDurationGreaterEqZero(splitTimes);
            TestTrainingStatsCollection.assertNonNullFields(splitTimes);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(splitTimes, 1, 1, 1);// only 1 thread for master

            List<EventStats> aggregateTimesMs = masterStats.getParamaterAveragingMasterAggregateTimesMs();
            Assert.assertEquals(numberOfAveragings, aggregateTimesMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(aggregateTimesMs);
            TestTrainingStatsCollection.assertNonNullFields(aggregateTimesMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(aggregateTimesMs, 1, 1, 1);// only 1 thread for master

            List<EventStats> processParamsTimesMs = masterStats.getParameterAveragingMasterProcessParamsUpdaterTimesMs();
            Assert.assertEquals(numberOfAveragings, processParamsTimesMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(processParamsTimesMs);
            TestTrainingStatsCollection.assertNonNullFields(processParamsTimesMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(processParamsTimesMs, 1, 1, 1);// only 1 thread for master

            List<EventStats> repartitionTimesMs = masterStats.getParameterAveragingMasterRepartitionTimesMs();
            Assert.assertEquals(numberOfAveragings, repartitionTimesMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(repartitionTimesMs);
            TestTrainingStatsCollection.assertNonNullFields(repartitionTimesMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(repartitionTimesMs, 1, 1, 1);// only 1 thread for master

            // Second: Common spark training stats
            SparkTrainingStats commonStats = masterStats.getNestedTrainingStats();
            TestCase.assertNotNull(commonStats);
            TestCase.assertTrue((commonStats instanceof CommonSparkTrainingStats));
            CommonSparkTrainingStats cStats = ((CommonSparkTrainingStats) (commonStats));
            List<EventStats> workerFlatMapTotalTimeMs = cStats.getWorkerFlatMapTotalTimeMs();
            Assert.assertEquals((numberOfAveragings * nWorkers), workerFlatMapTotalTimeMs.size());
            TestTrainingStatsCollection.assertDurationGreaterZero(workerFlatMapTotalTimeMs);
            TestTrainingStatsCollection.assertNonNullFields(workerFlatMapTotalTimeMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(workerFlatMapTotalTimeMs, 1, 1, nWorkers);
            List<EventStats> workerFlatMapGetInitialModelTimeMs = cStats.getWorkerFlatMapGetInitialModelTimeMs();
            Assert.assertEquals((numberOfAveragings * nWorkers), workerFlatMapGetInitialModelTimeMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(workerFlatMapGetInitialModelTimeMs);
            TestTrainingStatsCollection.assertNonNullFields(workerFlatMapGetInitialModelTimeMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(workerFlatMapGetInitialModelTimeMs, 1, 1, nWorkers);
            List<EventStats> workerFlatMapDataSetGetTimesMs = cStats.getWorkerFlatMapDataSetGetTimesMs();
            int numMinibatchesProcessed = workerFlatMapDataSetGetTimesMs.size();
            int expectedNumMinibatchesProcessed = (numberOfAveragings * nWorkers) * averagingFrequency;// 1 for every time we get a data set

            // Sometimes random split is just bad - some executors might miss out on getting the expected amount of data
            TestCase.assertTrue((numMinibatchesProcessed >= (expectedNumMinibatchesProcessed - 5)));
            List<EventStats> workerFlatMapProcessMiniBatchTimesMs = cStats.getWorkerFlatMapProcessMiniBatchTimesMs();
            TestCase.assertTrue(((workerFlatMapProcessMiniBatchTimesMs.size()) >= (((numberOfAveragings * nWorkers) * averagingFrequency) - 5)));
            TestTrainingStatsCollection.assertDurationGreaterEqZero(workerFlatMapProcessMiniBatchTimesMs);
            TestTrainingStatsCollection.assertNonNullFields(workerFlatMapDataSetGetTimesMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(workerFlatMapDataSetGetTimesMs, 1, 1, nWorkers);
            // Third: ParameterAveragingTrainingWorker stats
            SparkTrainingStats paramAvgStats = cStats.getNestedTrainingStats();
            TestCase.assertNotNull(paramAvgStats);
            TestCase.assertTrue((paramAvgStats instanceof ParameterAveragingTrainingWorkerStats));
            ParameterAveragingTrainingWorkerStats pStats = ((ParameterAveragingTrainingWorkerStats) (paramAvgStats));
            List<EventStats> parameterAveragingWorkerBroadcastGetValueTimeMs = pStats.getParameterAveragingWorkerBroadcastGetValueTimeMs();
            Assert.assertEquals((numberOfAveragings * nWorkers), parameterAveragingWorkerBroadcastGetValueTimeMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(parameterAveragingWorkerBroadcastGetValueTimeMs);
            TestTrainingStatsCollection.assertNonNullFields(parameterAveragingWorkerBroadcastGetValueTimeMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(parameterAveragingWorkerBroadcastGetValueTimeMs, 1, 1, nWorkers);
            List<EventStats> parameterAveragingWorkerInitTimeMs = pStats.getParameterAveragingWorkerInitTimeMs();
            Assert.assertEquals((numberOfAveragings * nWorkers), parameterAveragingWorkerInitTimeMs.size());
            TestTrainingStatsCollection.assertDurationGreaterEqZero(parameterAveragingWorkerInitTimeMs);
            TestTrainingStatsCollection.assertNonNullFields(parameterAveragingWorkerInitTimeMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(parameterAveragingWorkerInitTimeMs, 1, 1, nWorkers);
            List<EventStats> parameterAveragingWorkerFitTimesMs = pStats.getParameterAveragingWorkerFitTimesMs();
            TestCase.assertTrue(((parameterAveragingWorkerFitTimesMs.size()) >= (((numberOfAveragings * nWorkers) * averagingFrequency) - 5)));
            TestTrainingStatsCollection.assertDurationGreaterEqZero(parameterAveragingWorkerFitTimesMs);
            TestTrainingStatsCollection.assertNonNullFields(parameterAveragingWorkerFitTimesMs);
            TestTrainingStatsCollection.assertExpectedNumberMachineIdsJvmIdsThreadIds(parameterAveragingWorkerFitTimesMs, 1, 1, nWorkers);
            Assert.assertNull(pStats.getNestedTrainingStats());
            // Finally: try exporting stats
            String tempDir = System.getProperty("java.io.tmpdir");
            String outDir = FilenameUtils.concat(tempDir, "dl4j_testTrainingStatsCollection");
            stats.exportStatFiles(outDir, sc.sc());
            String htmlPlotsPath = FilenameUtils.concat(outDir, "AnalysisPlots.html");
            StatsUtils.exportStatsAsHtml(stats, htmlPlotsPath, sc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StatsUtils.exportStatsAsHTML(stats, baos);
            baos.close();
            byte[] bytes = baos.toByteArray();
            String str = new String(bytes, "UTF-8");
            // System.out.println(str);
        } finally {
            sc.stop();
        }
    }
}

