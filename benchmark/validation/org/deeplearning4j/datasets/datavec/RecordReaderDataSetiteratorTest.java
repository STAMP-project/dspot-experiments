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
package org.deeplearning4j.datasets.datavec;


import DataType.FLOAT;
import SequenceRecordReaderDataSetIterator.AlignmentMode;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.Record;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputStreamInputSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.datavec.exception.ZeroLengthSequenceException;
import org.deeplearning4j.datasets.iterator.AsyncDataSetIterator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by agibsonccc on 3/6/15.
 */
@Slf4j
public class RecordReaderDataSetiteratorTest extends BaseDL4JTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testRecordReader() throws Exception {
        RecordReader recordReader = new CSVRecordReader();
        FileSplit csv = new FileSplit(new ClassPathResource("csv-example.csv").getTempFileFromArchive());
        recordReader.initialize(csv);
        DataSetIterator iter = new RecordReaderDataSetIterator(recordReader, 34);
        DataSet next = iter.next();
        Assert.assertEquals(34, next.numExamples());
    }

    @Test
    public void testRecordReaderMaxBatchLimit() throws Exception {
        RecordReader recordReader = new CSVRecordReader();
        FileSplit csv = new FileSplit(new ClassPathResource("csv-example.csv").getTempFileFromArchive());
        recordReader.initialize(csv);
        DataSetIterator iter = new RecordReaderDataSetIterator(recordReader, 10, (-1), (-1), 2);
        DataSet ds = iter.next();
        Assert.assertFalse((ds == null));
        Assert.assertEquals(10, ds.numExamples());
        iter.hasNext();
        iter.next();
        Assert.assertEquals(false, iter.hasNext());
    }

    @Test
    public void testRecordReaderMultiRegression() throws Exception {
        for (boolean builder : new boolean[]{ false, true }) {
            RecordReader csv = new CSVRecordReader();
            csv.initialize(new FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
            int batchSize = 3;
            int labelIdxFrom = 3;
            int labelIdxTo = 4;
            DataSetIterator iter;
            if (builder) {
                iter = regression(labelIdxFrom, labelIdxTo).build();
            } else {
                iter = new RecordReaderDataSetIterator(csv, batchSize, labelIdxFrom, labelIdxTo, true);
            }
            DataSet ds = iter.next();
            INDArray f = ds.getFeatures();
            INDArray l = ds.getLabels();
            Assert.assertArrayEquals(new long[]{ 3, 3 }, f.shape());
            Assert.assertArrayEquals(new long[]{ 3, 2 }, l.shape());
            // Check values:
            double[][] fExpD = new double[][]{ new double[]{ 5.1, 3.5, 1.4 }, new double[]{ 4.9, 3.0, 1.4 }, new double[]{ 4.7, 3.2, 1.3 } };
            double[][] lExpD = new double[][]{ new double[]{ 0.2, 0 }, new double[]{ 0.2, 0 }, new double[]{ 0.2, 0 } };
            INDArray fExp = Nd4j.create(fExpD).castTo(FLOAT);
            INDArray lExp = Nd4j.create(lExpD).castTo(FLOAT);
            Assert.assertEquals(fExp, f);
            Assert.assertEquals(lExp, l);
        }
    }

    @Test
    public void testSequenceRecordReader() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
            new ClassPathResource(String.format("csvsequencelabels_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        String labelsPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequencelabels_%d.txt");
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(featureReader, labelReader, 1, 4, false);
        Assert.assertEquals(3, iter.inputColumns());
        Assert.assertEquals(4, iter.totalOutcomes());
        List<DataSet> dsList = new ArrayList<>();
        while (iter.hasNext()) {
            dsList.add(iter.next());
        } 
        Assert.assertEquals(3, dsList.size());// 3 files

        for (int i = 0; i < 3; i++) {
            DataSet ds = dsList.get(i);
            INDArray features = ds.getFeatures();
            INDArray labels = ds.getLabels();
            Assert.assertEquals(1, features.size(0));// 1 example in mini-batch

            Assert.assertEquals(1, labels.size(0));
            Assert.assertEquals(3, features.size(1));// 3 values per line/time step

            Assert.assertEquals(4, labels.size(1));// 1 value per line, but 4 possible values -> one-hot vector

            Assert.assertEquals(4, features.size(2));// sequence length = 4

            Assert.assertEquals(4, labels.size(2));
        }
        // Check features vs. expected:
        INDArray expF0 = Nd4j.create(1, 3, 4);
        expF0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 2 }));
        expF0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 10, 11, 12 }));
        expF0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 20, 21, 22 }));
        expF0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 30, 31, 32 }));
        Assert.assertEquals(dsList.get(0).getFeatures(), expF0);
        INDArray expF1 = Nd4j.create(1, 3, 4);
        expF1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 100, 101, 102 }));
        expF1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 110, 111, 112 }));
        expF1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 120, 121, 122 }));
        expF1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 130, 131, 132 }));
        Assert.assertEquals(dsList.get(1).getFeatures(), expF1);
        INDArray expF2 = Nd4j.create(1, 3, 4);
        expF2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 200, 201, 202 }));
        expF2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 210, 211, 212 }));
        expF2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 220, 221, 222 }));
        expF2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 230, 231, 232 }));
        Assert.assertEquals(dsList.get(2).getFeatures(), expF2);
        // Check labels vs. expected:
        INDArray expL0 = Nd4j.create(1, 4, 4);
        expL0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1, 0, 0, 0 }));
        expL0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        expL0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 0, 1, 0 }));
        expL0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 0, 0, 1 }));
        Assert.assertEquals(dsList.get(0).getLabels(), expL0);
        INDArray expL1 = Nd4j.create(1, 4, 4);
        expL1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 0, 0, 1 }));
        expL1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 0, 1, 0 }));
        expL1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        expL1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 1, 0, 0, 0 }));
        Assert.assertEquals(dsList.get(1).getLabels(), expL1);
        INDArray expL2 = Nd4j.create(1, 4, 4);
        expL2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        expL2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 1, 0, 0, 0 }));
        expL2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 0, 0, 1 }));
        expL2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(dsList.get(2).getLabels(), expL2);
    }

    @Test
    public void testSequenceRecordReaderMeta() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
            new ClassPathResource(String.format("csvsequencelabels_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        ClassPathResource resource = new ClassPathResource("csvsequence_0.txt");
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        String labelsPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequencelabels_%d.txt");
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(featureReader, labelReader, 1, 4, false);
        iter.setCollectMetaData(true);
        Assert.assertEquals(3, iter.inputColumns());
        Assert.assertEquals(4, iter.totalOutcomes());
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            List<RecordMetaData> meta = ds.getExampleMetaData(RecordMetaData.class);
            DataSet fromMeta = iter.loadFromMetaData(meta);
            Assert.assertEquals(ds, fromMeta);
        } 
    }

    @Test
    public void testSequenceRecordReaderRegression() throws Exception {
        // need to manually extract
        File rootDir = temporaryFolder.newFolder();
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        String labelsPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(featureReader, labelReader, 1, 0, true);
        Assert.assertEquals(3, iter.inputColumns());
        Assert.assertEquals(3, iter.totalOutcomes());
        List<DataSet> dsList = new ArrayList<>();
        while (iter.hasNext()) {
            dsList.add(iter.next());
        } 
        Assert.assertEquals(3, dsList.size());// 3 files

        for (int i = 0; i < 3; i++) {
            DataSet ds = dsList.get(i);
            INDArray features = ds.getFeatures();
            INDArray labels = ds.getLabels();
            Assert.assertArrayEquals(new long[]{ 1, 3, 4 }, features.shape());// 1 examples, 3 values, 4 time steps

            Assert.assertArrayEquals(new long[]{ 1, 3, 4 }, labels.shape());
            Assert.assertEquals(features, labels);
        }
        // Also test regression + reset from a single reader:
        featureReader.reset();
        iter = new SequenceRecordReaderDataSetIterator(featureReader, 1, 0, 2, true);
        int count = 0;
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            Assert.assertEquals(2, ds.getFeatures().size(1));
            Assert.assertEquals(1, ds.getLabels().size(1));
            count++;
        } 
        Assert.assertEquals(3, count);
        iter.reset();
        count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        } 
        Assert.assertEquals(3, count);
    }

    @Test
    public void testSequenceRecordReaderMultiRegression() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        SequenceRecordReader reader = new CSVSequenceRecordReader(1, ",");
        reader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(reader, 1, 2, 1, true);
        Assert.assertEquals(1, iter.inputColumns());
        Assert.assertEquals(2, iter.totalOutcomes());
        List<DataSet> dsList = new ArrayList<>();
        while (iter.hasNext()) {
            dsList.add(iter.next());
        } 
        Assert.assertEquals(3, dsList.size());// 3 files

        for (int i = 0; i < 3; i++) {
            DataSet ds = dsList.get(i);
            INDArray features = ds.getFeatures();
            INDArray labels = ds.getLabels();
            Assert.assertArrayEquals(new long[]{ 1, 1, 4 }, features.shape());// 1 examples, 1 values, 4 time steps

            Assert.assertArrayEquals(new long[]{ 1, 2, 4 }, labels.shape());
            INDArray f2d = features.get(point(0), all(), all()).transpose();
            INDArray l2d = labels.get(point(0), all(), all()).transpose();
            switch (i) {
                case 0 :
                    Assert.assertEquals(Nd4j.create(new double[]{ 0, 10, 20, 30 }, new int[]{ 4, 1 }).castTo(FLOAT), f2d);
                    Assert.assertEquals(Nd4j.create(new double[][]{ new double[]{ 1, 2 }, new double[]{ 11, 12 }, new double[]{ 21, 22 }, new double[]{ 31, 32 } }).castTo(FLOAT), l2d);
                    break;
                case 1 :
                    Assert.assertEquals(Nd4j.create(new double[]{ 100, 110, 120, 130 }, new int[]{ 4, 1 }).castTo(FLOAT), f2d);
                    Assert.assertEquals(Nd4j.create(new double[][]{ new double[]{ 101, 102 }, new double[]{ 111, 112 }, new double[]{ 121, 122 }, new double[]{ 131, 132 } }).castTo(FLOAT), l2d);
                    break;
                case 2 :
                    Assert.assertEquals(Nd4j.create(new double[]{ 200, 210, 220, 230 }, new int[]{ 4, 1 }).castTo(FLOAT), f2d);
                    Assert.assertEquals(Nd4j.create(new double[][]{ new double[]{ 201, 202 }, new double[]{ 211, 212 }, new double[]{ 221, 222 }, new double[]{ 231, 232 } }).castTo(FLOAT), l2d);
                    break;
                default :
                    throw new RuntimeException();
            }
        }
        iter.reset();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        } 
        Assert.assertEquals(3, count);
    }

    @Test
    public void testSequenceRecordReaderReset() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
            new ClassPathResource(String.format("csvsequencelabels_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        String labelsPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequencelabels_%d.txt");
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(featureReader, labelReader, 1, 4, false);
        Assert.assertEquals(3, iter.inputColumns());
        Assert.assertEquals(4, iter.totalOutcomes());
        int nResets = 5;
        for (int i = 0; i < nResets; i++) {
            iter.reset();
            int count = 0;
            while (iter.hasNext()) {
                DataSet ds = iter.next();
                INDArray features = ds.getFeatures();
                INDArray labels = ds.getLabels();
                Assert.assertArrayEquals(new long[]{ 1, 3, 4 }, features.shape());
                Assert.assertArrayEquals(new long[]{ 1, 4, 4 }, labels.shape());
                count++;
            } 
            Assert.assertEquals(3, count);
        }
    }

    @Test
    public void testCSVLoadingRegression() throws Exception {
        int nLines = 30;
        int nFeatures = 5;
        int miniBatchSize = 10;
        int labelIdx = 0;
        String path = "rr_csv_test_rand.csv";
        Pair<double[][], File> p = makeRandomCSV(path, nLines, nFeatures);
        double[][] data = p.getFirst();
        RecordReader testReader = new CSVRecordReader();
        testReader.initialize(new FileSplit(p.getSecond()));
        DataSetIterator iter = new RecordReaderDataSetIterator(testReader, miniBatchSize, labelIdx, labelIdx, true);
        int miniBatch = 0;
        while (iter.hasNext()) {
            DataSet test = iter.next();
            INDArray features = test.getFeatures();
            INDArray labels = test.getLabels();
            Assert.assertArrayEquals(new long[]{ miniBatchSize, nFeatures }, features.shape());
            Assert.assertArrayEquals(new long[]{ miniBatchSize, 1 }, labels.shape());
            int startRow = miniBatch * miniBatchSize;
            for (int i = 0; i < miniBatchSize; i++) {
                double labelExp = data[(startRow + i)][labelIdx];
                double labelAct = labels.getDouble(i);
                Assert.assertEquals(labelExp, labelAct, 1.0E-5F);
                int featureCount = 0;
                for (int j = 0; j < (nFeatures + 1); j++) {
                    if (j == labelIdx)
                        continue;

                    double featureExp = data[(startRow + i)][j];
                    double featureAct = features.getDouble(i, (featureCount++));
                    Assert.assertEquals(featureExp, featureAct, 1.0E-5F);
                }
            }
            miniBatch++;
        } 
        Assert.assertEquals((nLines / miniBatchSize), miniBatch);
    }

    @Test
    public void testVariableLengthSequence() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequence_%d.txt", i)).getTempFileFromArchive(rootDir);
            new ClassPathResource(String.format("csvsequencelabelsShort_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String featuresPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequence_%d.txt");
        String labelsPath = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequencelabelsShort_%d.txt");
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReader featureReader2 = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader2 = new CSVSequenceRecordReader(1, ",");
        featureReader2.initialize(new NumberedFileInputSplit(featuresPath, 0, 2));
        labelReader2.initialize(new NumberedFileInputSplit(labelsPath, 0, 2));
        SequenceRecordReaderDataSetIterator iterAlignStart = new SequenceRecordReaderDataSetIterator(featureReader, labelReader, 1, 4, false, AlignmentMode.ALIGN_START);
        SequenceRecordReaderDataSetIterator iterAlignEnd = new SequenceRecordReaderDataSetIterator(featureReader2, labelReader2, 1, 4, false, AlignmentMode.ALIGN_END);
        Assert.assertEquals(3, iterAlignStart.inputColumns());
        Assert.assertEquals(4, iterAlignStart.totalOutcomes());
        Assert.assertEquals(3, iterAlignEnd.inputColumns());
        Assert.assertEquals(4, iterAlignEnd.totalOutcomes());
        List<DataSet> dsListAlignStart = new ArrayList<>();
        while (iterAlignStart.hasNext()) {
            dsListAlignStart.add(iterAlignStart.next());
        } 
        List<DataSet> dsListAlignEnd = new ArrayList<>();
        while (iterAlignEnd.hasNext()) {
            dsListAlignEnd.add(iterAlignEnd.next());
        } 
        Assert.assertEquals(3, dsListAlignStart.size());// 3 files

        Assert.assertEquals(3, dsListAlignEnd.size());// 3 files

        for (int i = 0; i < 3; i++) {
            DataSet ds = dsListAlignStart.get(i);
            INDArray features = ds.getFeatures();
            INDArray labels = ds.getLabels();
            Assert.assertEquals(1, features.size(0));// 1 example in mini-batch

            Assert.assertEquals(1, labels.size(0));
            Assert.assertEquals(3, features.size(1));// 3 values per line/time step

            Assert.assertEquals(4, labels.size(1));// 1 value per line, but 4 possible values -> one-hot vector

            Assert.assertEquals(4, features.size(2));// sequence length = 4

            Assert.assertEquals(4, labels.size(2));
            DataSet ds2 = dsListAlignEnd.get(i);
            features = ds2.getFeatures();
            labels = ds2.getLabels();
            Assert.assertEquals(1, features.size(0));// 1 example in mini-batch

            Assert.assertEquals(1, labels.size(0));
            Assert.assertEquals(3, features.size(1));// 3 values per line/time step

            Assert.assertEquals(4, labels.size(1));// 1 value per line, but 4 possible values -> one-hot vector

            Assert.assertEquals(4, features.size(2));// sequence length = 4

            Assert.assertEquals(4, labels.size(2));
        }
        // Check features vs. expected:
        // Here: labels always longer than features -> same features for align start and align end
        INDArray expF0 = Nd4j.create(1, 3, 4);
        expF0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 2 }));
        expF0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 10, 11, 12 }));
        expF0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 20, 21, 22 }));
        expF0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 30, 31, 32 }));
        Assert.assertEquals(expF0, dsListAlignStart.get(0).getFeatures());
        Assert.assertEquals(expF0, dsListAlignEnd.get(0).getFeatures());
        INDArray expF1 = Nd4j.create(1, 3, 4);
        expF1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 100, 101, 102 }));
        expF1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 110, 111, 112 }));
        expF1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 120, 121, 122 }));
        expF1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 130, 131, 132 }));
        Assert.assertEquals(expF1, dsListAlignStart.get(1).getFeatures());
        Assert.assertEquals(expF1, dsListAlignEnd.get(1).getFeatures());
        INDArray expF2 = Nd4j.create(1, 3, 4);
        expF2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 200, 201, 202 }));
        expF2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 210, 211, 212 }));
        expF2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 220, 221, 222 }));
        expF2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 230, 231, 232 }));
        Assert.assertEquals(expF2, dsListAlignStart.get(2).getFeatures());
        Assert.assertEquals(expF2, dsListAlignEnd.get(2).getFeatures());
        // Check features mask array:
        INDArray featuresMaskExpected = null;// null: equivalent to all 1s (i.e., present for all time steps)

        for (int i = 0; i < 3; i++) {
            INDArray featuresMaskStart = dsListAlignStart.get(i).getFeaturesMaskArray();
            INDArray featuresMaskEnd = dsListAlignEnd.get(i).getFeaturesMaskArray();
            Assert.assertEquals(featuresMaskExpected, featuresMaskStart);
            Assert.assertEquals(featuresMaskExpected, featuresMaskEnd);
        }
        // Check labels vs. expected:
        // First: aligning start
        INDArray expL0 = Nd4j.create(1, 4, 4);
        expL0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1, 0, 0, 0 }));
        expL0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL0, dsListAlignStart.get(0).getLabels());
        INDArray expL1 = Nd4j.create(1, 4, 4);
        expL1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL1, dsListAlignStart.get(1).getLabels());
        INDArray expL2 = Nd4j.create(1, 4, 4);
        expL2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 0, 0, 1 }));
        expL2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 0, 1, 0 }));
        expL2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL2, dsListAlignStart.get(2).getLabels());
        // Second: align end
        INDArray expL0end = Nd4j.create(1, 4, 4);
        expL0end.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 1, 0, 0, 0 }));
        expL0end.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL0end, dsListAlignEnd.get(0).getLabels());
        INDArray expL1end = Nd4j.create(1, 4, 4);
        expL1end.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL1end, dsListAlignEnd.get(1).getLabels());
        INDArray expL2end = Nd4j.create(1, 4, 4);
        expL2end.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 0, 0, 1 }));
        expL2end.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 0, 1, 0 }));
        expL2end.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 1, 0, 0 }));
        Assert.assertEquals(expL2end, dsListAlignEnd.get(2).getLabels());
        // Check labels mask array
        INDArray[] labelsMaskExpectedStart = new INDArray[]{ Nd4j.create(new float[]{ 1, 1, 0, 0 }, new int[]{ 1, 4 }), Nd4j.create(new float[]{ 1, 0, 0, 0 }, new int[]{ 1, 4 }), Nd4j.create(new float[]{ 1, 1, 1, 0 }, new int[]{ 1, 4 }) };
        INDArray[] labelsMaskExpectedEnd = new INDArray[]{ Nd4j.create(new float[]{ 0, 0, 1, 1 }, new int[]{ 1, 4 }), Nd4j.create(new float[]{ 0, 0, 0, 1 }, new int[]{ 1, 4 }), Nd4j.create(new float[]{ 0, 1, 1, 1 }, new int[]{ 1, 4 }) };
        for (int i = 0; i < 3; i++) {
            INDArray labelsMaskStart = dsListAlignStart.get(i).getLabelsMaskArray();
            INDArray labelsMaskEnd = dsListAlignEnd.get(i).getLabelsMaskArray();
            Assert.assertEquals(labelsMaskExpectedStart[i], labelsMaskStart);
            Assert.assertEquals(labelsMaskExpectedEnd[i], labelsMaskEnd);
        }
    }

    @Test
    public void testSequenceRecordReaderSingleReader() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequenceSingle_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String path = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequenceSingle_%d.txt");
        SequenceRecordReader reader = new CSVSequenceRecordReader(1, ",");
        reader.initialize(new NumberedFileInputSplit(path, 0, 2));
        SequenceRecordReaderDataSetIterator iteratorClassification = new SequenceRecordReaderDataSetIterator(reader, 1, 3, 0, false);
        Assert.assertTrue(iteratorClassification.hasNext());
        SequenceRecordReader reader2 = new CSVSequenceRecordReader(1, ",");
        reader2.initialize(new NumberedFileInputSplit(path, 0, 2));
        SequenceRecordReaderDataSetIterator iteratorRegression = new SequenceRecordReaderDataSetIterator(reader2, 1, 1, 0, true);
        INDArray expF0 = Nd4j.create(1, 2, 4);
        expF0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1, 2 }));
        expF0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 11, 12 }));
        expF0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 21, 22 }));
        expF0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 31, 32 }));
        INDArray expF1 = Nd4j.create(1, 2, 4);
        expF1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 101, 102 }));
        expF1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 111, 112 }));
        expF1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 121, 122 }));
        expF1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 131, 132 }));
        INDArray expF2 = Nd4j.create(1, 2, 4);
        expF2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 201, 202 }));
        expF2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 211, 212 }));
        expF2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 221, 222 }));
        expF2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 231, 232 }));
        INDArray[] expF = new INDArray[]{ expF0, expF1, expF2 };
        // Expected out for classification:
        INDArray expOut0 = Nd4j.create(1, 3, 4);
        expOut0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1, 0, 0 }));
        expOut0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 1, 0 }));
        expOut0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 0, 1 }));
        expOut0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 1, 0, 0 }));
        INDArray expOut1 = Nd4j.create(1, 3, 4);
        expOut1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 0 }));
        expOut1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0, 0, 1 }));
        expOut1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 1, 0, 0 }));
        expOut1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 0, 1 }));
        INDArray expOut2 = Nd4j.create(1, 3, 4);
        expOut2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0, 1, 0 }));
        expOut2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 1, 0, 0 }));
        expOut2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0, 1, 0 }));
        expOut2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0, 0, 1 }));
        INDArray[] expOutClassification = new INDArray[]{ expOut0, expOut1, expOut2 };
        // Expected out for regression:
        INDArray expOutR0 = Nd4j.create(1, 1, 4);
        expOutR0.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 0 }));
        expOutR0.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 1 }));
        expOutR0.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 2 }));
        expOutR0.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 0 }));
        INDArray expOutR1 = Nd4j.create(1, 1, 4);
        expOutR1.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1 }));
        expOutR1.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 2 }));
        expOutR1.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 0 }));
        expOutR1.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 2 }));
        INDArray expOutR2 = Nd4j.create(1, 1, 4);
        expOutR2.tensorAlongDimension(0, 1).assign(Nd4j.create(new double[]{ 1 }));
        expOutR2.tensorAlongDimension(1, 1).assign(Nd4j.create(new double[]{ 0 }));
        expOutR2.tensorAlongDimension(2, 1).assign(Nd4j.create(new double[]{ 1 }));
        expOutR2.tensorAlongDimension(3, 1).assign(Nd4j.create(new double[]{ 2 }));
        INDArray[] expOutRegression = new INDArray[]{ expOutR0, expOutR1, expOutR2 };
        int countC = 0;
        while (iteratorClassification.hasNext()) {
            DataSet ds = iteratorClassification.next();
            INDArray f = ds.getFeatures();
            INDArray l = ds.getLabels();
            Assert.assertNull(ds.getFeaturesMaskArray());
            Assert.assertNull(ds.getLabelsMaskArray());
            Assert.assertArrayEquals(new long[]{ 1, 2, 4 }, f.shape());
            Assert.assertArrayEquals(new long[]{ 1, 3, 4 }, l.shape());// One-hot representation

            Assert.assertEquals(expF[countC], f);
            Assert.assertEquals(expOutClassification[(countC++)], l);
        } 
        Assert.assertEquals(3, countC);
        Assert.assertEquals(3, iteratorClassification.totalOutcomes());
        int countF = 0;
        while (iteratorRegression.hasNext()) {
            DataSet ds = iteratorRegression.next();
            INDArray f = ds.getFeatures();
            INDArray l = ds.getLabels();
            Assert.assertNull(ds.getFeaturesMaskArray());
            Assert.assertNull(ds.getLabelsMaskArray());
            Assert.assertArrayEquals(new long[]{ 1, 2, 4 }, f.shape());
            Assert.assertArrayEquals(new long[]{ 1, 1, 4 }, l.shape());// Regression (single output)

            Assert.assertEquals(expF[countF], f);
            Assert.assertEquals(expOutRegression[(countF++)], l);
        } 
        Assert.assertEquals(3, countF);
        Assert.assertEquals(1, iteratorRegression.totalOutcomes());
    }

    @Test(expected = ZeroLengthSequenceException.class)
    public void testSequenceRecordReaderSingleReaderWithEmptySequenceThrows() throws Exception {
        SequenceRecordReader reader = new CSVSequenceRecordReader(1, ",");
        reader.initialize(new FileSplit(new ClassPathResource("empty.txt").getTempFileFromArchive()));
        next();
    }

    @Test(expected = ZeroLengthSequenceException.class)
    public void testSequenceRecordReaderTwoReadersWithEmptyFeatureSequenceThrows() throws Exception {
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new FileSplit(new ClassPathResource("empty.txt").getTempFileFromArchive()));
        labelReader.initialize(new FileSplit(new ClassPathResource("csvsequencelabels_0.txt").getTempFileFromArchive()));
        next();
    }

    @Test(expected = ZeroLengthSequenceException.class)
    public void testSequenceRecordReaderTwoReadersWithEmptyLabelSequenceThrows() throws Exception {
        SequenceRecordReader featureReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelReader = new CSVSequenceRecordReader(1, ",");
        featureReader.initialize(new FileSplit(new ClassPathResource("csvsequence_0.txt").getTempFileFromArchive()));
        labelReader.initialize(new FileSplit(new ClassPathResource("empty.txt").getTempFileFromArchive()));
        next();
    }

    @Test
    public void testSequenceRecordReaderSingleReaderMetaData() throws Exception {
        File rootDir = temporaryFolder.newFolder();
        // need to manually extract
        for (int i = 0; i < 3; i++) {
            new ClassPathResource(String.format("csvsequenceSingle_%d.txt", i)).getTempFileFromArchive(rootDir);
        }
        String path = FilenameUtils.concat(rootDir.getAbsolutePath(), "csvsequenceSingle_%d.txt");
        SequenceRecordReader reader = new CSVSequenceRecordReader(1, ",");
        reader.initialize(new NumberedFileInputSplit(path, 0, 2));
        SequenceRecordReaderDataSetIterator iteratorClassification = new SequenceRecordReaderDataSetIterator(reader, 1, 3, 0, false);
        SequenceRecordReader reader2 = new CSVSequenceRecordReader(1, ",");
        reader2.initialize(new NumberedFileInputSplit(path, 0, 2));
        SequenceRecordReaderDataSetIterator iteratorRegression = new SequenceRecordReaderDataSetIterator(reader2, 1, 1, 0, true);
        iteratorClassification.setCollectMetaData(true);
        iteratorRegression.setCollectMetaData(true);
        while (iteratorClassification.hasNext()) {
            DataSet ds = iteratorClassification.next();
            DataSet fromMeta = iteratorClassification.loadFromMetaData(ds.getExampleMetaData(RecordMetaData.class));
            Assert.assertEquals(ds, fromMeta);
        } 
        while (iteratorRegression.hasNext()) {
            DataSet ds = iteratorRegression.next();
            DataSet fromMeta = iteratorRegression.loadFromMetaData(ds.getExampleMetaData(RecordMetaData.class));
            Assert.assertEquals(ds, fromMeta);
        } 
    }

    @Test
    public void testSeqRRDSIArrayWritableOneReader() {
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1, 2, 3 }, new long[]{ 1, 3 })))), new IntWritable(0)));
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4, 5, 6 }, new long[]{ 1, 3 })))), new IntWritable(1)));
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7, 8, 9 }, new long[]{ 1, 3 })))), new IntWritable(2)));
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 10, 11, 12 }, new long[]{ 1, 3 })))), new IntWritable(3)));
        SequenceRecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1, sequence2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(rr, 2, 4, 1, false);
        DataSet ds = iter.next();
        INDArray expFeatures = Nd4j.create(2, 3, 2);// 2 examples, 3 values per time step, 2 time steps

        expFeatures.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 4 }, new double[]{ 2, 5 }, new double[]{ 3, 6 } }));
        expFeatures.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 7, 10 }, new double[]{ 8, 11 }, new double[]{ 9, 12 } }));
        INDArray expLabels = Nd4j.create(2, 4, 2);
        expLabels.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 0 }, new double[]{ 0, 1 }, new double[]{ 0, 0 }, new double[]{ 0, 0 } }));
        expLabels.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 0, 0 }, new double[]{ 1, 0 }, new double[]{ 0, 1 } }));
        Assert.assertEquals(expFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
    }

    @Test
    public void testSeqRRDSIArrayWritableOneReaderRegression() {
        // Regression, where the output is an array writable
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1, 2, 3 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 100, 200, 300 }, new long[]{ 1, 3 }))));
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4, 5, 6 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 400, 500, 600 }, new long[]{ 1, 3 }))));
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7, 8, 9 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 700, 800, 900 }, new long[]{ 1, 3 }))));
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 10, 11, 12 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1000, 1100, 1200 }, new long[]{ 1, 3 }))));
        SequenceRecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1, sequence2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(rr, 2, (-1), 1, true);
        DataSet ds = iter.next();
        INDArray expFeatures = Nd4j.create(2, 3, 2);// 2 examples, 3 values per time step, 2 time steps

        expFeatures.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 4 }, new double[]{ 2, 5 }, new double[]{ 3, 6 } }));
        expFeatures.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 7, 10 }, new double[]{ 8, 11 }, new double[]{ 9, 12 } }));
        INDArray expLabels = Nd4j.create(2, 3, 2);
        expLabels.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 100, 400 }, new double[]{ 200, 500 }, new double[]{ 300, 600 } }));
        expLabels.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 700, 1000 }, new double[]{ 800, 1100 }, new double[]{ 900, 1200 } }));
        Assert.assertEquals(expFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
    }

    @Test
    public void testSeqRRDSIMultipleArrayWritablesOneReader() {
        // Input with multiple array writables:
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1, 2, 3 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 100, 200, 300 }, new long[]{ 1, 3 })), new IntWritable(0)));
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4, 5, 6 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 400, 500, 600 }, new long[]{ 1, 3 })), new IntWritable(1)));
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7, 8, 9 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 700, 800, 900 }, new long[]{ 1, 3 })), new IntWritable(2)));
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 10, 11, 12 }, new long[]{ 1, 3 })))), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1000, 1100, 1200 }, new long[]{ 1, 3 })), new IntWritable(3)));
        SequenceRecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1, sequence2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(rr, 2, 4, 2, false);
        DataSet ds = iter.next();
        INDArray expFeatures = Nd4j.create(2, 6, 2);// 2 examples, 6 values per time step, 2 time steps

        expFeatures.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 4 }, new double[]{ 2, 5 }, new double[]{ 3, 6 }, new double[]{ 100, 400 }, new double[]{ 200, 500 }, new double[]{ 300, 600 } }));
        expFeatures.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 7, 10 }, new double[]{ 8, 11 }, new double[]{ 9, 12 }, new double[]{ 700, 1000 }, new double[]{ 800, 1100 }, new double[]{ 900, 1200 } }));
        INDArray expLabels = Nd4j.create(2, 4, 2);
        expLabels.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 0 }, new double[]{ 0, 1 }, new double[]{ 0, 0 }, new double[]{ 0, 0 } }));
        expLabels.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 0, 0 }, new double[]{ 0, 0 }, new double[]{ 1, 0 }, new double[]{ 0, 1 } }));
        Assert.assertEquals(expFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
    }

    @Test
    public void testSeqRRDSIArrayWritableTwoReaders() {
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1, 2, 3 }, new long[]{ 1, 3 })))), new IntWritable(100)));
        sequence1.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4, 5, 6 }, new long[]{ 1, 3 })))), new IntWritable(200)));
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7, 8, 9 }, new long[]{ 1, 3 })))), new IntWritable(300)));
        sequence2.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 10, 11, 12 }, new long[]{ 1, 3 })))), new IntWritable(400)));
        SequenceRecordReader rrFeatures = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1, sequence2));
        List<List<Writable>> sequence1L = new ArrayList<>();
        sequence1L.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 100, 200, 300 }, new long[]{ 1, 3 })))), new IntWritable(101)));
        sequence1L.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 400, 500, 600 }, new long[]{ 1, 3 })))), new IntWritable(201)));
        List<List<Writable>> sequence2L = new ArrayList<>();
        sequence2L.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 700, 800, 900 }, new long[]{ 1, 3 })))), new IntWritable(301)));
        sequence2L.add(Arrays.asList(((Writable) (new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1000, 1100, 1200 }, new long[]{ 1, 3 })))), new IntWritable(401)));
        SequenceRecordReader rrLabels = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1L, sequence2L));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(rrFeatures, rrLabels, 2, (-1), true);
        INDArray expFeatures = Nd4j.create(2, 4, 2);// 2 examples, 4 values per time step, 2 time steps

        expFeatures.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 1, 4 }, new double[]{ 2, 5 }, new double[]{ 3, 6 }, new double[]{ 100, 200 } }));
        expFeatures.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 7, 10 }, new double[]{ 8, 11 }, new double[]{ 9, 12 }, new double[]{ 300, 400 } }));
        INDArray expLabels = Nd4j.create(2, 4, 2);
        expLabels.tensorAlongDimension(0, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 100, 400 }, new double[]{ 200, 500 }, new double[]{ 300, 600 }, new double[]{ 101, 201 } }));
        expLabels.tensorAlongDimension(1, 1, 2).assign(Nd4j.create(new double[][]{ new double[]{ 700, 1000 }, new double[]{ 800, 1100 }, new double[]{ 900, 1200 }, new double[]{ 301, 401 } }));
        DataSet ds = iter.next();
        Assert.assertEquals(expFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
    }

    @Test
    public void testRecordReaderMetaData() throws Exception {
        RecordReader csv = new CSVRecordReader();
        csv.initialize(new FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        int batchSize = 10;
        int labelIdx = 4;
        int numClasses = 3;
        RecordReaderDataSetIterator rrdsi = new RecordReaderDataSetIterator(csv, batchSize, labelIdx, numClasses);
        rrdsi.setCollectMetaData(true);
        while (rrdsi.hasNext()) {
            DataSet ds = rrdsi.next();
            List<RecordMetaData> meta = ds.getExampleMetaData(RecordMetaData.class);
            int i = 0;
            for (RecordMetaData m : meta) {
                Record r = csv.loadFromMetaData(m);
                INDArray row = ds.getFeatures().getRow(i);
                System.out.println((((((m.getLocation()) + "\t") + (r.getRecord())) + "\t") + row));
                for (int j = 0; j < 4; j++) {
                    double exp = r.getRecord().get(j).toDouble();
                    double act = row.getDouble(j);
                    Assert.assertEquals(("Failed on idx: " + j), exp, act, 1.0E-6);
                }
                i++;
            }
            System.out.println();
            DataSet fromMeta = rrdsi.loadFromMetaData(meta);
            Assert.assertEquals(ds, fromMeta);
        } 
    }

    @Test
    public void testRRDSIwithAsync() throws Exception {
        RecordReader csv = new CSVRecordReader();
        csv.initialize(new FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        int batchSize = 10;
        int labelIdx = 4;
        int numClasses = 3;
        RecordReaderDataSetIterator rrdsi = new RecordReaderDataSetIterator(csv, batchSize, labelIdx, numClasses);
        AsyncDataSetIterator adsi = new AsyncDataSetIterator(rrdsi, 8, true);
        while (adsi.hasNext()) {
            DataSet ds = adsi.next();
        } 
    }

    @Test
    public void testRecordReaderDataSetIteratorNDArrayWritableLabels() {
        Collection<Collection<Writable>> data = new ArrayList<>();
        data.add(Arrays.<Writable>asList(new DoubleWritable(0), new DoubleWritable(1), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1.1, 2.1, 3.1 }, new long[]{ 1, 3 }))));
        data.add(Arrays.<Writable>asList(new DoubleWritable(2), new DoubleWritable(3), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4.1, 5.1, 6.1 }, new long[]{ 1, 3 }))));
        data.add(Arrays.<Writable>asList(new DoubleWritable(4), new DoubleWritable(5), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7.1, 8.1, 9.1 }, new long[]{ 1, 3 }))));
        RecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionRecordReader(data);
        int batchSize = 3;
        int labelIndexFrom = 2;
        int labelIndexTo = 2;
        boolean regression = true;
        DataSetIterator rrdsi = new RecordReaderDataSetIterator(rr, batchSize, labelIndexFrom, labelIndexTo, regression);
        DataSet ds = rrdsi.next();
        INDArray expFeatures = Nd4j.create(new float[][]{ new float[]{ 0, 1 }, new float[]{ 2, 3 }, new float[]{ 4, 5 } });
        INDArray expLabels = Nd4j.create(new float[][]{ new float[]{ 1.1F, 2.1F, 3.1F }, new float[]{ 4.1F, 5.1F, 6.1F }, new float[]{ 7.1F, 8.1F, 9.1F } });
        Assert.assertEquals(expFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
        // ALSO: test if we have NDArrayWritables for BOTH the features and the labels
        data = new ArrayList();
        data.add(Arrays.<Writable>asList(new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 0, 1 }, new long[]{ 1, 2 })), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 1.1, 2.1, 3.1 }, new long[]{ 1, 3 }))));
        data.add(Arrays.<Writable>asList(new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 2, 3 }, new long[]{ 1, 2 })), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4.1, 5.1, 6.1 }, new long[]{ 1, 3 }))));
        data.add(Arrays.<Writable>asList(new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 4, 5 }, new long[]{ 1, 2 })), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 7.1, 8.1, 9.1 }, new long[]{ 1, 3 }))));
        labelIndexFrom = 1;
        labelIndexTo = 1;
        rr = new org.datavec.api.records.reader.impl.collection.CollectionRecordReader(data);
        rrdsi = new RecordReaderDataSetIterator(rr, batchSize, labelIndexFrom, labelIndexTo, regression);
        DataSet ds2 = rrdsi.next();
        Assert.assertEquals(expFeatures, ds2.getFeatures());
        Assert.assertEquals(expLabels, ds2.getLabels());
    }

    /* @Test
    public void specialRRTest1() throws Exception {
    RecordReader rr = new SpecialImageRecordReader(250, 10,3, 224, 224);
    DataSetIterator rrdsi = new ParallelRecordReaderDataSetIterator.Builder(rr)
    .setBatchSize(10)
    .numberOfWorkers(1)
    .build();

    int cnt = 0;
    int examples = 0;
    while (rrdsi.hasNext()) {
    DataSet ds = rrdsi.next();
    for (int i = 0; i < ds.numExamples(); i++) {
    INDArray example = ds.getFeatures().tensorAlongDimension(i, 1, 2, 3).dup();
    assertEquals("Failed on DataSet ["+ cnt + "], example ["+ i +"]",(double) examples, example.meanNumber().doubleValue(), 0.01);
    examples++;
    }
    cnt++;
    log.info("DataSet {} passed...", cnt);
    }

    assertEquals(25, cnt);
    }


    @Test
    public void specialRRTest2() throws Exception {
    RecordReader rr = new SpecialImageRecordReader(250, 10,3, 224, 224);
    DataSetIterator rrdsi = new ParallelRecordReaderDataSetIterator.Builder(rr)
    .setBatchSize(10)
    .numberOfWorkers(1)
    .prefetchBufferSize(4)
    .build();

    rrdsi = new AsyncDataSetIterator(rrdsi);

    int cnt = 0;
    int examples = 0;
    while (rrdsi.hasNext()) {
    DataSet ds = rrdsi.next();
    for (int i = 0; i < ds.numExamples(); i++) {
    INDArray example = ds.getFeatures().tensorAlongDimension(i, 1, 2, 3).dup();
    assertEquals("Failed on DataSet ["+ cnt + "], example ["+ i +"]",(double) examples, example.meanNumber().doubleValue(), 0.01);
    examples++;
    }
    cnt++;
    }

    assertEquals(25, cnt);
    }


    @Test
    public void specialRRTest3() throws Exception {
    RecordReader rr = new SpecialImageRecordReader(400, 10,3, 224, 224);
    DataSetIterator rrdsi = new ParallelRecordReaderDataSetIterator.Builder(rr)
    .setBatchSize(128)
    .numberOfWorkers(2)
    .prefetchBufferSize(2)
    .build();

    log.info("DataType: {}", Nd4j.dataType() );

    // rrdsi = new AsyncDataSetIterator(rrdsi);

    int cnt = 0;
    int examples = 0;
    while (rrdsi.hasNext()) {
    DataSet ds = rrdsi.next();
    for (int i = 0; i < ds.numExamples(); i++) {
    INDArray example = ds.getFeatures().tensorAlongDimension(i, 1, 2, 3).dup();
    assertEquals("Failed on DataSet ["+ cnt + "], example ["+ i +"]",(double) examples, example.meanNumber().doubleValue(), 0.01);
    examples++;
    }
    cnt++;
    }

    }
     */
    @Test
    public void testRecordReaderDataSetIteratorConcat() {
        // [DoubleWritable, DoubleWritable, NDArrayWritable([1,10]), IntWritable] -> concatenate to a [1,13] feature vector automatically.
        List<Writable> l = Arrays.<Writable>asList(new DoubleWritable(1), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 2, 3, 4 })), new DoubleWritable(5), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new double[]{ 6, 7, 8 })), new IntWritable(9), new IntWritable(1));
        RecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionRecordReader(Collections.singletonList(l));
        DataSetIterator iter = new RecordReaderDataSetIterator(rr, 1, 5, 3);
        DataSet ds = iter.next();
        INDArray expF = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        INDArray expL = Nd4j.create(new float[]{ 0, 1, 0 });
        Assert.assertEquals(expF, ds.getFeatures());
        Assert.assertEquals(expL, ds.getLabels());
    }

    @Test
    public void testRecordReaderDataSetIteratorConcat2() {
        List<Writable> l = new ArrayList<>();
        l.add(new IntWritable(0));
        l.add(new org.datavec.api.writable.NDArrayWritable(Nd4j.arange(1, 9)));
        l.add(new IntWritable(9));
        RecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionRecordReader(Collections.singletonList(l));
        DataSetIterator iter = new RecordReaderDataSetIterator(rr, 1);
        DataSet ds = iter.next();
        INDArray expF = Nd4j.create(new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        Assert.assertEquals(expF, ds.getFeatures());
    }

    @Test
    public void testRecordReaderDataSetIteratorDisjointFeatures() {
        // Idea: input vector is like [f,f,f,f,l,l,f,f] or similar - i.e., label writables aren't start/end
        List<Writable> l = Arrays.<Writable>asList(new DoubleWritable(1), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new float[]{ 2, 3, 4 }, new long[]{ 1, 3 })), new DoubleWritable(5), new org.datavec.api.writable.NDArrayWritable(Nd4j.create(new float[]{ 6, 7, 8 }, new long[]{ 1, 3 })));
        INDArray expF = Nd4j.create(new float[]{ 1, 6, 7, 8 }, new long[]{ 1, 4 });
        INDArray expL = Nd4j.create(new float[]{ 2, 3, 4, 5 }, new long[]{ 1, 4 });
        RecordReader rr = new org.datavec.api.records.reader.impl.collection.CollectionRecordReader(Collections.singletonList(l));
        DataSetIterator iter = new RecordReaderDataSetIterator(rr, 1, 1, 2, true);
        DataSet ds = iter.next();
        Assert.assertEquals(expF, ds.getFeatures());
        Assert.assertEquals(expL, ds.getLabels());
    }

    @Test
    public void testNormalizerPrefetchReset() throws Exception {
        // Check NPE fix for: https://github.com/deeplearning4j/deeplearning4j/issues/4214
        RecordReader csv = new CSVRecordReader();
        csv.initialize(new FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        int batchSize = 3;
        DataSetIterator iter = new RecordReaderDataSetIterator(csv, batchSize, 4, 4, true);
        DataNormalization normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fit(iter);
        iter.setPreProcessor(normalizer);
        iter.inputColumns();// Prefetch

        iter.totalOutcomes();
        iter.hasNext();
        iter.reset();
        iter.next();
    }

    @Test
    public void testReadingFromStream() throws Exception {
        for (boolean b : new boolean[]{ false, true }) {
            int batchSize = 1;
            int labelIndex = 4;
            int numClasses = 3;
            InputStream dataFile = new ClassPathResource("iris.txt").getInputStream();
            RecordReader recordReader = new CSVRecordReader(0, ',');
            recordReader.initialize(new InputStreamInputSplit(dataFile));
            Assert.assertTrue(recordReader.hasNext());
            Assert.assertFalse(recordReader.resetSupported());
            DataSetIterator iterator;
            if (b) {
                iterator = classification(labelIndex, numClasses).build();
            } else {
                iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
            }
            Assert.assertFalse(iterator.resetSupported());
            int count = 0;
            while (iterator.hasNext()) {
                Assert.assertNotNull(iterator.next());
                count++;
            } 
            Assert.assertEquals(150, count);
            try {
                iterator.reset();
                Assert.fail("Expected exception");
            } catch (Exception e) {
                // expected
            }
        }
    }

    @Test
    public void testImagesRRDSI() throws Exception {
        File parentDir = temporaryFolder.newFolder();
        parentDir.deleteOnExit();
        String str1 = FilenameUtils.concat(parentDir.getAbsolutePath(), "Zico/");
        String str2 = FilenameUtils.concat(parentDir.getAbsolutePath(), "Ziwang_Xu/");
        File f2 = new File(str2);
        File f1 = new File(str1);
        f1.mkdirs();
        f2.mkdirs();
        TestUtils.writeStreamToFile(new File(FilenameUtils.concat(f1.getPath(), "Zico_0001.jpg")), new ClassPathResource("lfwtest/Zico/Zico_0001.jpg").getInputStream());
        TestUtils.writeStreamToFile(new File(FilenameUtils.concat(f2.getPath(), "Ziwang_Xu_0001.jpg")), new ClassPathResource("lfwtest/Ziwang_Xu/Ziwang_Xu_0001.jpg").getInputStream());
        Random r = new Random(12345);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader rr1 = new ImageRecordReader(28, 28, 3, labelMaker);
        rr1.initialize(new FileSplit(parentDir));
        RecordReaderDataSetIterator rrdsi = new RecordReaderDataSetIterator(rr1, 2);
        DataSet ds = rrdsi.next();
        Assert.assertArrayEquals(new long[]{ 2, 3, 28, 28 }, ds.getFeatures().shape());
        Assert.assertArrayEquals(new long[]{ 2, 2 }, ds.getLabels().shape());
        // Check the same thing via the builder:
        rr1.reset();
        rrdsi = classification(1, 2).build();
        ds = rrdsi.next();
        Assert.assertArrayEquals(new long[]{ 2, 3, 28, 28 }, ds.getFeatures().shape());
        Assert.assertArrayEquals(new long[]{ 2, 2 }, ds.getLabels().shape());
    }

    @Test
    public void testSeqRRDSINoLabels() {
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new DoubleWritable(1))), new DoubleWritable(2)));
        sequence1.add(Arrays.asList(((Writable) (new DoubleWritable(3))), new DoubleWritable(4)));
        sequence1.add(Arrays.asList(((Writable) (new DoubleWritable(5))), new DoubleWritable(6)));
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new DoubleWritable(10))), new DoubleWritable(20)));
        sequence2.add(Arrays.asList(((Writable) (new DoubleWritable(30))), new DoubleWritable(40)));
        SequenceRecordReader rrFeatures = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(Arrays.asList(sequence1, sequence2));
        SequenceRecordReaderDataSetIterator iter = new SequenceRecordReaderDataSetIterator(rrFeatures, 2, (-1), (-1));
        DataSet ds = iter.next();
        Assert.assertNotNull(ds.getFeatures());
        Assert.assertNull(ds.getLabels());
    }
}

