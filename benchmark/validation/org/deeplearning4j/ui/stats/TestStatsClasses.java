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
package org.deeplearning4j.ui.stats;


import StatsType.Activations;
import StatsType.Gradients;
import StatsType.Parameters;
import StatsType.Updates;
import SummaryType.Mean;
import SummaryType.MeanMagnitudes;
import SummaryType.Stdev;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deeplearning4j.ui.stats.impl.SbeStatsInitializationReport;
import org.deeplearning4j.ui.stats.impl.SbeStatsReport;
import org.deeplearning4j.ui.stats.impl.java.JavaStatsInitializationReport;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by Alex on 01/10/2016.
 */
public class TestStatsClasses {
    @Test
    public void testStatsInitializationReport() throws Exception {
        boolean[] tf = new boolean[]{ true, false };
        for (boolean useJ7 : new boolean[]{ false, true }) {
            // IDs
            String sessionID = "sid";
            String typeID = "tid";
            String workerID = "wid";
            long timestamp = -1;
            // Hardware info
            int jvmAvailableProcessors = 1;
            int numDevices = 2;
            long jvmMaxMemory = 3;
            long offHeapMaxMemory = 4;
            long[] deviceTotalMemory = new long[]{ 5, 6 };
            String[] deviceDescription = new String[]{ "7", "8" };
            String hwUID = "8a";
            // Software info
            String arch = "9";
            String osName = "10";
            String jvmName = "11";
            String jvmVersion = "12";
            String jvmSpecVersion = "13";
            String nd4jBackendClass = "14";
            String nd4jDataTypeName = "15";
            String hostname = "15a";
            String jvmUID = "15b";
            Map<String, String> swEnvInfo = new HashMap<>();
            swEnvInfo.put("env15c-1", "SomeData");
            swEnvInfo.put("env15c-2", "OtherData");
            swEnvInfo.put("env15c-3", "EvenMoreData");
            // Model info
            String modelClassName = "16";
            String modelConfigJson = "17";
            String[] modelparamNames = new String[]{ "18", "19", "20", "21" };
            int numLayers = 22;
            long numParams = 23;
            for (boolean hasHardwareInfo : tf) {
                for (boolean hasSoftwareInfo : tf) {
                    for (boolean hasModelInfo : tf) {
                        StatsInitializationReport report;
                        if (useJ7) {
                            report = new JavaStatsInitializationReport();
                        } else {
                            report = new SbeStatsInitializationReport();
                        }
                        report.reportIDs(sessionID, typeID, workerID, timestamp);
                        if (hasHardwareInfo) {
                            report.reportHardwareInfo(jvmAvailableProcessors, numDevices, jvmMaxMemory, offHeapMaxMemory, deviceTotalMemory, deviceDescription, hwUID);
                        }
                        if (hasSoftwareInfo) {
                            report.reportSoftwareInfo(arch, osName, jvmName, jvmVersion, jvmSpecVersion, nd4jBackendClass, nd4jDataTypeName, hostname, jvmUID, swEnvInfo);
                        }
                        if (hasModelInfo) {
                            report.reportModelInfo(modelClassName, modelConfigJson, modelparamNames, numLayers, numParams);
                        }
                        byte[] asBytes = report.encode();
                        StatsInitializationReport report2;// = new SbeStatsInitializationReport();

                        if (useJ7) {
                            report2 = new JavaStatsInitializationReport();
                        } else {
                            report2 = new SbeStatsInitializationReport();
                        }
                        report2.decode(asBytes);
                        Assert.assertEquals(report, report2);
                        Assert.assertEquals(sessionID, report2.getSessionID());
                        Assert.assertEquals(typeID, report2.getTypeID());
                        Assert.assertEquals(workerID, report2.getWorkerID());
                        Assert.assertEquals(timestamp, report2.getTimeStamp());
                        if (hasHardwareInfo) {
                            Assert.assertEquals(jvmAvailableProcessors, report2.getHwJvmAvailableProcessors());
                            Assert.assertEquals(numDevices, report2.getHwNumDevices());
                            Assert.assertEquals(jvmMaxMemory, report2.getHwJvmMaxMemory());
                            Assert.assertEquals(offHeapMaxMemory, report2.getHwOffHeapMaxMemory());
                            Assert.assertArrayEquals(deviceTotalMemory, report2.getHwDeviceTotalMemory());
                            Assert.assertArrayEquals(deviceDescription, report2.getHwDeviceDescription());
                            Assert.assertEquals(hwUID, report2.getHwHardwareUID());
                            Assert.assertTrue(report2.hasHardwareInfo());
                        } else {
                            Assert.assertFalse(report2.hasHardwareInfo());
                        }
                        if (hasSoftwareInfo) {
                            Assert.assertEquals(arch, report2.getSwArch());
                            Assert.assertEquals(osName, report2.getSwOsName());
                            Assert.assertEquals(jvmName, report2.getSwJvmName());
                            Assert.assertEquals(jvmVersion, report2.getSwJvmVersion());
                            Assert.assertEquals(jvmSpecVersion, report2.getSwJvmSpecVersion());
                            Assert.assertEquals(nd4jBackendClass, report2.getSwNd4jBackendClass());
                            Assert.assertEquals(nd4jDataTypeName, report2.getSwNd4jDataTypeName());
                            Assert.assertEquals(jvmUID, report2.getSwJvmUID());
                            Assert.assertEquals(hostname, report2.getSwHostName());
                            Assert.assertEquals(swEnvInfo, report2.getSwEnvironmentInfo());
                            Assert.assertTrue(report2.hasSoftwareInfo());
                        } else {
                            Assert.assertFalse(report2.hasSoftwareInfo());
                        }
                        if (hasModelInfo) {
                            Assert.assertEquals(modelClassName, report2.getModelClassName());
                            Assert.assertEquals(modelConfigJson, report2.getModelConfigJson());
                            Assert.assertArrayEquals(modelparamNames, report2.getModelParamNames());
                            Assert.assertEquals(numLayers, report2.getModelNumLayers());
                            Assert.assertEquals(numParams, report2.getModelNumParams());
                            Assert.assertTrue(report2.hasModelInfo());
                        } else {
                            Assert.assertFalse(report2.hasModelInfo());
                        }
                        // Check standard Java serialization
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(report);
                        oos.close();
                        byte[] javaBytes = baos.toByteArray();
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(javaBytes));
                        StatsInitializationReport report3 = ((StatsInitializationReport) (ois.readObject()));
                        Assert.assertEquals(report, report3);
                    }
                }
            }
        }
    }

    @Test
    public void testStatsInitializationReportNullValues() throws Exception {
        // Sanity check: shouldn't have any issues with encoding/decoding null values...
        boolean[] tf = new boolean[]{ true, false };
        for (boolean useJ7 : new boolean[]{ false, true }) {
            // Hardware info
            int jvmAvailableProcessors = 1;
            int numDevices = 2;
            long jvmMaxMemory = 3;
            long offHeapMaxMemory = 4;
            long[] deviceTotalMemory = null;
            String[] deviceDescription = null;
            String hwUID = null;
            // Software info
            String arch = null;
            String osName = null;
            String jvmName = null;
            String jvmVersion = null;
            String jvmSpecVersion = null;
            String nd4jBackendClass = null;
            String nd4jDataTypeName = null;
            String hostname = null;
            String jvmUID = null;
            Map<String, String> swEnvInfo = null;
            // Model info
            String modelClassName = null;
            String modelConfigJson = null;
            String[] modelparamNames = null;
            int numLayers = 22;
            long numParams = 23;
            for (boolean hasHardwareInfo : tf) {
                for (boolean hasSoftwareInfo : tf) {
                    for (boolean hasModelInfo : tf) {
                        System.out.println(((((hasHardwareInfo + "\t") + hasSoftwareInfo) + "\t") + hasModelInfo));
                        StatsInitializationReport report;
                        if (useJ7) {
                            report = new JavaStatsInitializationReport();
                        } else {
                            report = new SbeStatsInitializationReport();
                        }
                        report.reportIDs(null, null, null, (-1));
                        if (hasHardwareInfo) {
                            report.reportHardwareInfo(jvmAvailableProcessors, numDevices, jvmMaxMemory, offHeapMaxMemory, deviceTotalMemory, deviceDescription, hwUID);
                        }
                        if (hasSoftwareInfo) {
                            report.reportSoftwareInfo(arch, osName, jvmName, jvmVersion, jvmSpecVersion, nd4jBackendClass, nd4jDataTypeName, hostname, jvmUID, swEnvInfo);
                        }
                        if (hasModelInfo) {
                            report.reportModelInfo(modelClassName, modelConfigJson, modelparamNames, numLayers, numParams);
                        }
                        byte[] asBytes = report.encode();
                        StatsInitializationReport report2;
                        if (useJ7) {
                            report2 = new JavaStatsInitializationReport();
                        } else {
                            report2 = new SbeStatsInitializationReport();
                        }
                        report2.decode(asBytes);
                        if (hasHardwareInfo) {
                            Assert.assertEquals(jvmAvailableProcessors, report2.getHwJvmAvailableProcessors());
                            Assert.assertEquals(numDevices, report2.getHwNumDevices());
                            Assert.assertEquals(jvmMaxMemory, report2.getHwJvmMaxMemory());
                            Assert.assertEquals(offHeapMaxMemory, report2.getHwOffHeapMaxMemory());
                            if (useJ7) {
                                Assert.assertArrayEquals(null, report2.getHwDeviceTotalMemory());
                                Assert.assertArrayEquals(null, report2.getHwDeviceDescription());
                            } else {
                                Assert.assertArrayEquals(new long[]{ 0, 0 }, report2.getHwDeviceTotalMemory());// Edge case: nDevices = 2, but missing mem data -> expect long[] of 0s out, due to fixed encoding

                                Assert.assertArrayEquals(new String[]{ "", "" }, report2.getHwDeviceDescription());// As above

                            }
                            TestStatsClasses.assertNullOrZeroLength(report2.getHwHardwareUID());
                            Assert.assertTrue(report2.hasHardwareInfo());
                        } else {
                            Assert.assertFalse(report2.hasHardwareInfo());
                        }
                        if (hasSoftwareInfo) {
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwArch());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwOsName());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwJvmName());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwJvmVersion());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwJvmSpecVersion());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwNd4jBackendClass());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwNd4jDataTypeName());
                            TestStatsClasses.assertNullOrZeroLength(report2.getSwJvmUID());
                            Assert.assertNull(report2.getSwEnvironmentInfo());
                            Assert.assertTrue(report2.hasSoftwareInfo());
                        } else {
                            Assert.assertFalse(report2.hasSoftwareInfo());
                        }
                        if (hasModelInfo) {
                            TestStatsClasses.assertNullOrZeroLength(report2.getModelClassName());
                            TestStatsClasses.assertNullOrZeroLength(report2.getModelConfigJson());
                            TestStatsClasses.assertNullOrZeroLengthArray(report2.getModelParamNames());
                            Assert.assertEquals(numLayers, report2.getModelNumLayers());
                            Assert.assertEquals(numParams, report2.getModelNumParams());
                            Assert.assertTrue(report2.hasModelInfo());
                        } else {
                            Assert.assertFalse(report2.hasModelInfo());
                        }
                        // Check standard Java serialization
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(report);
                        oos.close();
                        byte[] javaBytes = baos.toByteArray();
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(javaBytes));
                        StatsInitializationReport report3 = ((StatsInitializationReport) (ois.readObject()));
                        Assert.assertEquals(report, report3);
                    }
                }
            }
        }
    }

    @Test
    public void testSbeStatsUpdate() throws Exception {
        String[] paramNames = new String[]{ "param0", "param1" };
        String[] layerNames = new String[]{ "layer0", "layer1" };
        // IDs
        String sessionID = "sid";
        String typeID = "tid";
        String workerID = "wid";
        long timestamp = -1;
        long time = System.currentTimeMillis();
        int duration = 123456;
        int iterCount = 123;
        long perfRuntime = 1;
        long perfTotalEx = 2;
        long perfTotalMB = 3;
        double perfEPS = 4.0;
        double perfMBPS = 5.0;
        long memJC = 6;
        long memJM = 7;
        long memOC = 8;
        long memOM = 9;
        long[] memDC = new long[]{ 10, 11 };
        long[] memDM = new long[]{ 12, 13 };
        String gc1Name = "14";
        int gcdc1 = 16;
        int gcdt1 = 17;
        String gc2Name = "18";
        int gcdc2 = 20;
        int gcdt2 = 21;
        double score = 22.0;
        Map<String, Double> lrByParam = new HashMap<>();
        lrByParam.put(paramNames[0], 22.5);
        lrByParam.put(paramNames[1], 22.75);
        Map<String, Histogram> pHist = new HashMap<>();
        pHist.put(paramNames[0], new Histogram(23, 24, 2, new int[]{ 25, 26 }));
        pHist.put(paramNames[1], new Histogram(27, 28, 3, new int[]{ 29, 30, 31 }));
        Map<String, Histogram> gHist = new HashMap<>();
        gHist.put(paramNames[0], new Histogram(230, 240, 2, new int[]{ 250, 260 }));
        gHist.put(paramNames[1], new Histogram(270, 280, 3, new int[]{ 290, 300, 310 }));
        Map<String, Histogram> uHist = new HashMap<>();
        uHist.put(paramNames[0], new Histogram(32, 33, 2, new int[]{ 34, 35 }));
        uHist.put(paramNames[1], new Histogram(36, 37, 3, new int[]{ 38, 39, 40 }));
        Map<String, Histogram> aHist = new HashMap<>();
        aHist.put(layerNames[0], new Histogram(41, 42, 2, new int[]{ 43, 44 }));
        aHist.put(layerNames[1], new Histogram(45, 46, 3, new int[]{ 47, 48, 47 }));
        Map<String, Double> pMean = new HashMap<>();
        pMean.put(paramNames[0], 49.0);
        pMean.put(paramNames[1], 50.0);
        Map<String, Double> gMean = new HashMap<>();
        gMean.put(paramNames[0], 49.1);
        gMean.put(paramNames[1], 50.1);
        Map<String, Double> uMean = new HashMap<>();
        uMean.put(paramNames[0], 51.0);
        uMean.put(paramNames[1], 52.0);
        Map<String, Double> aMean = new HashMap<>();
        aMean.put(layerNames[0], 53.0);
        aMean.put(layerNames[1], 54.0);
        Map<String, Double> pStd = new HashMap<>();
        pStd.put(paramNames[0], 55.0);
        pStd.put(paramNames[1], 56.0);
        Map<String, Double> gStd = new HashMap<>();
        gStd.put(paramNames[0], 55.1);
        gStd.put(paramNames[1], 56.1);
        Map<String, Double> uStd = new HashMap<>();
        uStd.put(paramNames[0], 57.0);
        uStd.put(paramNames[1], 58.0);
        Map<String, Double> aStd = new HashMap<>();
        aStd.put(layerNames[0], 59.0);
        aStd.put(layerNames[1], 60.0);
        Map<String, Double> pMM = new HashMap<>();
        pMM.put(paramNames[0], 61.0);
        pMM.put(paramNames[1], 62.0);
        Map<String, Double> gMM = new HashMap<>();
        gMM.put(paramNames[0], 61.1);
        gMM.put(paramNames[1], 62.1);
        Map<String, Double> uMM = new HashMap<>();
        uMM.put(paramNames[0], 63.0);
        uMM.put(paramNames[1], 64.0);
        Map<String, Double> aMM = new HashMap<>();
        aMM.put(layerNames[0], 65.0);
        aMM.put(layerNames[1], 66.0);
        List<Serializable> metaDataList = new ArrayList<>();
        metaDataList.add("meta1");
        metaDataList.add("meta2");
        metaDataList.add("meta3");
        Class<?> metaDataClass = String.class;
        boolean[] tf = new boolean[]{ true, false };
        boolean[][] tf4 = new boolean[][]{ new boolean[]{ false, false, false, false }, new boolean[]{ true, false, false, false }, new boolean[]{ false, true, false, false }, new boolean[]{ false, false, true, false }, new boolean[]{ false, false, false, true }, new boolean[]{ true, true, true, true } };
        // Total tests: 2^6 x 6^3 = 13,824 separate tests
        int testCount = 0;
        for (boolean collectPerformanceStats : tf) {
            for (boolean collectMemoryStats : tf) {
                for (boolean collectGCStats : tf) {
                    for (boolean collectScore : tf) {
                        for (boolean collectLearningRates : tf) {
                            for (boolean collectMetaData : tf) {
                                for (boolean[] collectHistograms : tf4) {
                                    for (boolean[] collectMeanStdev : tf4) {
                                        for (boolean[] collectMM : tf4) {
                                            SbeStatsReport report = new SbeStatsReport();
                                            report.reportIDs(sessionID, typeID, workerID, time);
                                            report.reportStatsCollectionDurationMS(duration);
                                            report.reportIterationCount(iterCount);
                                            if (collectPerformanceStats) {
                                                report.reportPerformance(perfRuntime, perfTotalEx, perfTotalMB, perfEPS, perfMBPS);
                                            }
                                            if (collectMemoryStats) {
                                                report.reportMemoryUse(memJC, memJM, memOC, memOM, memDC, memDM);
                                            }
                                            if (collectGCStats) {
                                                report.reportGarbageCollection(gc1Name, gcdc1, gcdt1);
                                                report.reportGarbageCollection(gc2Name, gcdc2, gcdt2);
                                            }
                                            if (collectScore) {
                                                report.reportScore(score);
                                            }
                                            if (collectLearningRates) {
                                                report.reportLearningRates(lrByParam);
                                            }
                                            if (collectMetaData) {
                                                report.reportDataSetMetaData(metaDataList, metaDataClass);
                                            }
                                            if (collectHistograms[0]) {
                                                // Param hist
                                                report.reportHistograms(Parameters, pHist);
                                            }
                                            if (collectHistograms[1]) {
                                                // Grad hist
                                                report.reportHistograms(Gradients, gHist);
                                            }
                                            if (collectHistograms[2]) {
                                                // Update hist
                                                report.reportHistograms(Updates, uHist);
                                            }
                                            if (collectHistograms[3]) {
                                                // Act hist
                                                report.reportHistograms(Activations, aHist);
                                            }
                                            if (collectMeanStdev[0]) {
                                                // Param mean/stdev
                                                report.reportMean(Parameters, pMean);
                                                report.reportStdev(Parameters, pStd);
                                            }
                                            if (collectMeanStdev[1]) {
                                                // Gradient mean/stdev
                                                report.reportMean(Gradients, gMean);
                                                report.reportStdev(Gradients, gStd);
                                            }
                                            if (collectMeanStdev[2]) {
                                                // Update mean/stdev
                                                report.reportMean(Updates, uMean);
                                                report.reportStdev(Updates, uStd);
                                            }
                                            if (collectMeanStdev[3]) {
                                                // Act mean/stdev
                                                report.reportMean(Activations, aMean);
                                                report.reportStdev(Activations, aStd);
                                            }
                                            if (collectMM[0]) {
                                                // Param mean mag
                                                report.reportMeanMagnitudes(Parameters, pMM);
                                            }
                                            if (collectMM[1]) {
                                                // Gradient mean mag
                                                report.reportMeanMagnitudes(Gradients, gMM);
                                            }
                                            if (collectMM[2]) {
                                                // Update mm
                                                report.reportMeanMagnitudes(Updates, uMM);
                                            }
                                            if (collectMM[3]) {
                                                // Act mm
                                                report.reportMeanMagnitudes(Activations, aMM);
                                            }
                                            byte[] bytes = report.encode();
                                            StatsReport report2 = new SbeStatsReport();
                                            report2.decode(bytes);
                                            Assert.assertEquals(report, report2);
                                            Assert.assertEquals(sessionID, report2.getSessionID());
                                            Assert.assertEquals(typeID, report2.getTypeID());
                                            Assert.assertEquals(workerID, report2.getWorkerID());
                                            Assert.assertEquals(time, report2.getTimeStamp());
                                            Assert.assertEquals(time, report2.getTimeStamp());
                                            Assert.assertEquals(duration, report2.getStatsCollectionDurationMs());
                                            Assert.assertEquals(iterCount, report2.getIterationCount());
                                            if (collectPerformanceStats) {
                                                Assert.assertEquals(perfRuntime, report2.getTotalRuntimeMs());
                                                Assert.assertEquals(perfTotalEx, report2.getTotalExamples());
                                                Assert.assertEquals(perfTotalMB, report2.getTotalMinibatches());
                                                Assert.assertEquals(perfEPS, report2.getExamplesPerSecond(), 0.0);
                                                Assert.assertEquals(perfMBPS, report2.getMinibatchesPerSecond(), 0.0);
                                                Assert.assertTrue(report2.hasPerformance());
                                            } else {
                                                Assert.assertFalse(report2.hasPerformance());
                                            }
                                            if (collectMemoryStats) {
                                                Assert.assertEquals(memJC, report2.getJvmCurrentBytes());
                                                Assert.assertEquals(memJM, report2.getJvmMaxBytes());
                                                Assert.assertEquals(memOC, report2.getOffHeapCurrentBytes());
                                                Assert.assertEquals(memOM, report2.getOffHeapMaxBytes());
                                                Assert.assertArrayEquals(memDC, report2.getDeviceCurrentBytes());
                                                Assert.assertArrayEquals(memDM, report2.getDeviceMaxBytes());
                                                Assert.assertTrue(report2.hasMemoryUse());
                                            } else {
                                                Assert.assertFalse(report2.hasMemoryUse());
                                            }
                                            if (collectGCStats) {
                                                List<Pair<String, int[]>> gcs = report2.getGarbageCollectionStats();
                                                Assert.assertEquals(2, gcs.size());
                                                Assert.assertEquals(gc1Name, gcs.get(0).getFirst());
                                                Assert.assertArrayEquals(new int[]{ gcdc1, gcdt1 }, gcs.get(0).getSecond());
                                                Assert.assertEquals(gc2Name, gcs.get(1).getFirst());
                                                Assert.assertArrayEquals(new int[]{ gcdc2, gcdt2 }, gcs.get(1).getSecond());
                                                Assert.assertTrue(report2.hasGarbageCollection());
                                            } else {
                                                Assert.assertFalse(report2.hasGarbageCollection());
                                            }
                                            if (collectScore) {
                                                Assert.assertEquals(score, report2.getScore(), 0.0);
                                                Assert.assertTrue(report2.hasScore());
                                            } else {
                                                Assert.assertFalse(report2.hasScore());
                                            }
                                            if (collectLearningRates) {
                                                Assert.assertEquals(lrByParam.keySet(), report2.getLearningRates().keySet());
                                                for (String s : lrByParam.keySet()) {
                                                    Assert.assertEquals(lrByParam.get(s), report2.getLearningRates().get(s), 1.0E-6);
                                                }
                                                Assert.assertTrue(report2.hasLearningRates());
                                            } else {
                                                Assert.assertFalse(report2.hasLearningRates());
                                            }
                                            if (collectMetaData) {
                                                Assert.assertNotNull(report2.getDataSetMetaData());
                                                Assert.assertEquals(metaDataList, report2.getDataSetMetaData());
                                                Assert.assertEquals(metaDataClass.getName(), report2.getDataSetMetaDataClassName());
                                                Assert.assertTrue(report2.hasDataSetMetaData());
                                            } else {
                                                Assert.assertFalse(report2.hasDataSetMetaData());
                                            }
                                            if (collectHistograms[0]) {
                                                Assert.assertEquals(pHist, report2.getHistograms(Parameters));
                                                Assert.assertTrue(report2.hasHistograms(Parameters));
                                            } else {
                                                Assert.assertFalse(report2.hasHistograms(Parameters));
                                            }
                                            if (collectHistograms[1]) {
                                                Assert.assertEquals(gHist, report2.getHistograms(Gradients));
                                                Assert.assertTrue(report2.hasHistograms(Gradients));
                                            } else {
                                                Assert.assertFalse(report2.hasHistograms(Gradients));
                                            }
                                            if (collectHistograms[2]) {
                                                Assert.assertEquals(uHist, report2.getHistograms(Updates));
                                                Assert.assertTrue(report2.hasHistograms(Updates));
                                            } else {
                                                Assert.assertFalse(report2.hasHistograms(Updates));
                                            }
                                            if (collectHistograms[3]) {
                                                Assert.assertEquals(aHist, report2.getHistograms(Activations));
                                                Assert.assertTrue(report2.hasHistograms(Activations));
                                            } else {
                                                Assert.assertFalse(report2.hasHistograms(Activations));
                                            }
                                            if (collectMeanStdev[0]) {
                                                Assert.assertEquals(pMean, report2.getMean(Parameters));
                                                Assert.assertEquals(pStd, report2.getStdev(Parameters));
                                                Assert.assertTrue(report2.hasSummaryStats(Parameters, Mean));
                                                Assert.assertTrue(report2.hasSummaryStats(Parameters, Stdev));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Parameters, Mean));
                                                Assert.assertFalse(report2.hasSummaryStats(Parameters, Stdev));
                                            }
                                            if (collectMeanStdev[1]) {
                                                Assert.assertEquals(gMean, report2.getMean(Gradients));
                                                Assert.assertEquals(gStd, report2.getStdev(Gradients));
                                                Assert.assertTrue(report2.hasSummaryStats(Gradients, Mean));
                                                Assert.assertTrue(report2.hasSummaryStats(Gradients, Stdev));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Gradients, Mean));
                                                Assert.assertFalse(report2.hasSummaryStats(Gradients, Stdev));
                                            }
                                            if (collectMeanStdev[2]) {
                                                Assert.assertEquals(uMean, report2.getMean(Updates));
                                                Assert.assertEquals(uStd, report2.getStdev(Updates));
                                                Assert.assertTrue(report2.hasSummaryStats(Updates, Mean));
                                                Assert.assertTrue(report2.hasSummaryStats(Updates, Stdev));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Updates, Mean));
                                                Assert.assertFalse(report2.hasSummaryStats(Updates, Stdev));
                                            }
                                            if (collectMeanStdev[3]) {
                                                Assert.assertEquals(aMean, report2.getMean(Activations));
                                                Assert.assertEquals(aStd, report2.getStdev(Activations));
                                                Assert.assertTrue(report2.hasSummaryStats(Activations, Mean));
                                                Assert.assertTrue(report2.hasSummaryStats(Activations, Stdev));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Activations, Mean));
                                                Assert.assertFalse(report2.hasSummaryStats(Activations, Stdev));
                                            }
                                            if (collectMM[0]) {
                                                Assert.assertEquals(pMM, report2.getMeanMagnitudes(Parameters));
                                                Assert.assertTrue(report2.hasSummaryStats(Parameters, MeanMagnitudes));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Parameters, MeanMagnitudes));
                                            }
                                            if (collectMM[1]) {
                                                Assert.assertEquals(gMM, report2.getMeanMagnitudes(Gradients));
                                                Assert.assertTrue(report2.hasSummaryStats(Gradients, MeanMagnitudes));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Gradients, MeanMagnitudes));
                                            }
                                            if (collectMM[2]) {
                                                Assert.assertEquals(uMM, report2.getMeanMagnitudes(Updates));
                                                Assert.assertTrue(report2.hasSummaryStats(Updates, MeanMagnitudes));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Updates, MeanMagnitudes));
                                            }
                                            if (collectMM[3]) {
                                                Assert.assertEquals(aMM, report2.getMeanMagnitudes(Activations));
                                                Assert.assertTrue(report2.hasSummaryStats(Activations, MeanMagnitudes));
                                            } else {
                                                Assert.assertFalse(report2.hasSummaryStats(Activations, MeanMagnitudes));
                                            }
                                            // Check standard Java serialization
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                                            oos.writeObject(report);
                                            oos.close();
                                            byte[] javaBytes = baos.toByteArray();
                                            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(javaBytes));
                                            SbeStatsReport report3 = ((SbeStatsReport) (ois.readObject()));
                                            Assert.assertEquals(report, report3);
                                            testCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Assert.assertEquals(13824, testCount);
    }

    @Test
    public void testSbeStatsUpdateNullValues() throws Exception {
        String[] paramNames = null;// new String[]{"param0", "param1"};

        long time = System.currentTimeMillis();
        int duration = 123456;
        int iterCount = 123;
        long perfRuntime = 1;
        long perfTotalEx = 2;
        long perfTotalMB = 3;
        double perfEPS = 4.0;
        double perfMBPS = 5.0;
        long memJC = 6;
        long memJM = 7;
        long memOC = 8;
        long memOM = 9;
        long[] memDC = null;
        long[] memDM = null;
        String gc1Name = null;
        int gcdc1 = 16;
        int gcdt1 = 17;
        String gc2Name = null;
        int gcdc2 = 20;
        int gcdt2 = 21;
        double score = 22.0;
        Map<String, Double> lrByParam = null;
        Map<String, Histogram> pHist = null;
        Map<String, Histogram> gHist = null;
        Map<String, Histogram> uHist = null;
        Map<String, Histogram> aHist = null;
        Map<String, Double> pMean = null;
        Map<String, Double> gMean = null;
        Map<String, Double> uMean = null;
        Map<String, Double> aMean = null;
        Map<String, Double> pStd = null;
        Map<String, Double> gStd = null;
        Map<String, Double> uStd = null;
        Map<String, Double> aStd = null;
        Map<String, Double> pMM = null;
        Map<String, Double> gMM = null;
        Map<String, Double> uMM = null;
        Map<String, Double> aMM = null;
        boolean[] tf = new boolean[]{ true, false };
        boolean[][] tf4 = new boolean[][]{ new boolean[]{ false, false, false, false }, new boolean[]{ true, false, false, false }, new boolean[]{ false, true, false, false }, new boolean[]{ false, false, true, false }, new boolean[]{ false, false, false, true }, new boolean[]{ true, true, true, true } };
        // Total tests: 2^6 x 6^3 = 13,824 separate tests
        int testCount = 0;
        for (boolean collectPerformanceStats : tf) {
            for (boolean collectMemoryStats : tf) {
                for (boolean collectGCStats : tf) {
                    for (boolean collectDataSetMetaData : tf) {
                        for (boolean collectScore : tf) {
                            for (boolean collectLearningRates : tf) {
                                for (boolean[] collectHistograms : tf4) {
                                    for (boolean[] collectMeanStdev : tf4) {
                                        for (boolean[] collectMM : tf4) {
                                            SbeStatsReport report = new SbeStatsReport();
                                            report.reportIDs(null, null, null, time);
                                            report.reportStatsCollectionDurationMS(duration);
                                            report.reportIterationCount(iterCount);
                                            if (collectPerformanceStats) {
                                                report.reportPerformance(perfRuntime, perfTotalEx, perfTotalMB, perfEPS, perfMBPS);
                                            }
                                            if (collectMemoryStats) {
                                                report.reportMemoryUse(memJC, memJM, memOC, memOM, memDC, memDM);
                                            }
                                            if (collectGCStats) {
                                                report.reportGarbageCollection(gc1Name, gcdc1, gcdt1);
                                                report.reportGarbageCollection(gc2Name, gcdc2, gcdt2);
                                            }
                                            if (collectDataSetMetaData) {
                                                // TODO
                                            }
                                            if (collectScore) {
                                                report.reportScore(score);
                                            }
                                            if (collectLearningRates) {
                                                report.reportLearningRates(lrByParam);
                                            }
                                            if (collectHistograms[0]) {
                                                // Param hist
                                                report.reportHistograms(Parameters, pHist);
                                            }
                                            if (collectHistograms[1]) {
                                                report.reportHistograms(Gradients, gHist);
                                            }
                                            if (collectHistograms[2]) {
                                                // Update hist
                                                report.reportHistograms(Updates, uHist);
                                            }
                                            if (collectHistograms[3]) {
                                                // Act hist
                                                report.reportHistograms(Activations, aHist);
                                            }
                                            if (collectMeanStdev[0]) {
                                                // Param mean/stdev
                                                report.reportMean(Parameters, pMean);
                                                report.reportStdev(Parameters, pStd);
                                            }
                                            if (collectMeanStdev[1]) {
                                                // Param mean/stdev
                                                report.reportMean(Gradients, gMean);
                                                report.reportStdev(Gradients, gStd);
                                            }
                                            if (collectMeanStdev[2]) {
                                                // Update mean/stdev
                                                report.reportMean(Updates, uMean);
                                                report.reportStdev(Updates, uStd);
                                            }
                                            if (collectMeanStdev[3]) {
                                                // Act mean/stdev
                                                report.reportMean(Activations, aMean);
                                                report.reportStdev(Activations, aStd);
                                            }
                                            if (collectMM[0]) {
                                                // Param mean mag
                                                report.reportMeanMagnitudes(Parameters, pMM);
                                            }
                                            if (collectMM[1]) {
                                                // Param mean mag
                                                report.reportMeanMagnitudes(Gradients, gMM);
                                            }
                                            if (collectMM[2]) {
                                                // Update mm
                                                report.reportMeanMagnitudes(Updates, uMM);
                                            }
                                            if (collectMM[3]) {
                                                // Act mm
                                                report.reportMeanMagnitudes(Activations, aMM);
                                            }
                                            byte[] bytes = report.encode();
                                            StatsReport report2 = new SbeStatsReport();
                                            report2.decode(bytes);
                                            Assert.assertEquals(time, report2.getTimeStamp());
                                            Assert.assertEquals(duration, report2.getStatsCollectionDurationMs());
                                            Assert.assertEquals(iterCount, report2.getIterationCount());
                                            if (collectPerformanceStats) {
                                                Assert.assertEquals(perfRuntime, report2.getTotalRuntimeMs());
                                                Assert.assertEquals(perfTotalEx, report2.getTotalExamples());
                                                Assert.assertEquals(perfTotalMB, report2.getTotalMinibatches());
                                                Assert.assertEquals(perfEPS, report2.getExamplesPerSecond(), 0.0);
                                                Assert.assertEquals(perfMBPS, report2.getMinibatchesPerSecond(), 0.0);
                                                Assert.assertTrue(report2.hasPerformance());
                                            } else {
                                                Assert.assertFalse(report2.hasPerformance());
                                            }
                                            if (collectMemoryStats) {
                                                Assert.assertEquals(memJC, report2.getJvmCurrentBytes());
                                                Assert.assertEquals(memJM, report2.getJvmMaxBytes());
                                                Assert.assertEquals(memOC, report2.getOffHeapCurrentBytes());
                                                Assert.assertEquals(memOM, report2.getOffHeapMaxBytes());
                                                Assert.assertArrayEquals(memDC, report2.getDeviceCurrentBytes());
                                                Assert.assertArrayEquals(memDM, report2.getDeviceMaxBytes());
                                                Assert.assertTrue(report2.hasMemoryUse());
                                            } else {
                                                Assert.assertFalse(report2.hasMemoryUse());
                                            }
                                            if (collectGCStats) {
                                                List<Pair<String, int[]>> gcs = report2.getGarbageCollectionStats();
                                                Assert.assertEquals(2, gcs.size());
                                                TestStatsClasses.assertNullOrZeroLength(gcs.get(0).getFirst());
                                                Assert.assertArrayEquals(new int[]{ gcdc1, gcdt1 }, gcs.get(0).getSecond());
                                                TestStatsClasses.assertNullOrZeroLength(gcs.get(1).getFirst());
                                                Assert.assertArrayEquals(new int[]{ gcdc2, gcdt2 }, gcs.get(1).getSecond());
                                                Assert.assertTrue(report2.hasGarbageCollection());
                                            } else {
                                                Assert.assertFalse(report2.hasGarbageCollection());
                                            }
                                            if (collectDataSetMetaData) {
                                                // TODO
                                            }
                                            if (collectScore) {
                                                Assert.assertEquals(score, report2.getScore(), 0.0);
                                                Assert.assertTrue(report2.hasScore());
                                            } else {
                                                Assert.assertFalse(report2.hasScore());
                                            }
                                            if (collectLearningRates) {
                                                Assert.assertNull(report2.getLearningRates());
                                            } else {
                                                Assert.assertFalse(report2.hasLearningRates());
                                            }
                                            Assert.assertNull(report2.getHistograms(Parameters));
                                            Assert.assertFalse(report2.hasHistograms(Parameters));
                                            Assert.assertNull(report2.getHistograms(Gradients));
                                            Assert.assertFalse(report2.hasHistograms(Gradients));
                                            Assert.assertNull(report2.getHistograms(Updates));
                                            Assert.assertFalse(report2.hasHistograms(Updates));
                                            Assert.assertNull(report2.getHistograms(Activations));
                                            Assert.assertFalse(report2.hasHistograms(Activations));
                                            Assert.assertNull(report2.getMean(Parameters));
                                            Assert.assertNull(report2.getStdev(Parameters));
                                            Assert.assertFalse(report2.hasSummaryStats(Parameters, Mean));
                                            Assert.assertFalse(report2.hasSummaryStats(Parameters, Stdev));
                                            Assert.assertNull(report2.getMean(Gradients));
                                            Assert.assertNull(report2.getStdev(Gradients));
                                            Assert.assertFalse(report2.hasSummaryStats(Gradients, Mean));
                                            Assert.assertFalse(report2.hasSummaryStats(Gradients, Stdev));
                                            Assert.assertNull(report2.getMean(Updates));
                                            Assert.assertNull(report2.getStdev(Updates));
                                            Assert.assertFalse(report2.hasSummaryStats(Updates, Mean));
                                            Assert.assertFalse(report2.hasSummaryStats(Updates, Stdev));
                                            Assert.assertNull(report2.getMean(Activations));
                                            Assert.assertNull(report2.getStdev(Activations));
                                            Assert.assertFalse(report2.hasSummaryStats(Activations, Mean));
                                            Assert.assertFalse(report2.hasSummaryStats(Activations, Stdev));
                                            Assert.assertNull(report2.getMeanMagnitudes(Parameters));
                                            Assert.assertFalse(report2.hasSummaryStats(Parameters, MeanMagnitudes));
                                            Assert.assertNull(report2.getMeanMagnitudes(Gradients));
                                            Assert.assertFalse(report2.hasSummaryStats(Gradients, MeanMagnitudes));
                                            Assert.assertNull(report2.getMeanMagnitudes(Updates));
                                            Assert.assertFalse(report2.hasSummaryStats(Updates, MeanMagnitudes));
                                            Assert.assertNull(report2.getMeanMagnitudes(Activations));
                                            Assert.assertFalse(report2.hasSummaryStats(Activations, MeanMagnitudes));
                                            // Check standard Java serialization
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                                            oos.writeObject(report);
                                            oos.close();
                                            byte[] javaBytes = baos.toByteArray();
                                            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(javaBytes));
                                            SbeStatsReport report3 = ((SbeStatsReport) (ois.readObject()));
                                            Assert.assertEquals(report, report3);
                                            testCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Assert.assertEquals(13824, testCount);
    }
}

