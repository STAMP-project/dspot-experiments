/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.math.hadoop;


import DistributedRowMatrix.KEEP_TEMP_FILES;
import Functions.DIV;
import Functions.PLUS;
import TimesSquaredJob.TimesSquaredMapper;
import TimesSquaredJob.VectorSummingReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.decomposer.SolverTest;
import org.junit.Test;

import static TimesSquaredJob.INPUT_VECTOR;
import static TimesSquaredJob.OUTPUT_VECTOR_FILENAME;


public final class TestDistributedRowMatrix extends MahoutTestCase {
    public static final String TEST_PROPERTY_KEY = "test.property.key";

    public static final String TEST_PROPERTY_VALUE = "test.property.value";

    @Test
    public void testTranspose() throws Exception {
        DistributedRowMatrix m = randomDistributedMatrix(10, 9, 5, 4, 1.0, false);
        m.setConf(getConfiguration());
        DistributedRowMatrix mt = m.transpose();
        mt.setConf(getConfiguration());
        Path tmpPath = getTestTempDirPath();
        m.setOutputTempPathString(tmpPath.toString());
        Path tmpOutPath = new Path(tmpPath, "/tmpOutTranspose");
        mt.setOutputTempPathString(tmpOutPath.toString());
        HadoopUtil.delete(getConfiguration(), tmpOutPath);
        DistributedRowMatrix mtt = mt.transpose();
        TestDistributedRowMatrix.assertEquals(m, mtt, MahoutTestCase.EPSILON);
    }

    @Test
    public void testMatrixColumnMeansJob() throws Exception {
        Matrix m = SolverTest.randomSequentialAccessSparseMatrix(100, 90, 50, 20, 1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 50, 20, 1.0, false);
        dm.setConf(getConfiguration());
        Vector expected = new DenseVector(50);
        for (int i = 0; i < (m.numRows()); i++) {
            expected.assign(m.viewRow(i), PLUS);
        }
        expected.assign(DIV, m.numRows());
        Vector actual = dm.columnMeans("DenseVector");
        TestDistributedRowMatrix.assertEquals(0.0, expected.getDistanceSquared(actual), MahoutTestCase.EPSILON);
    }

    @Test
    public void testNullMatrixColumnMeansJob() throws Exception {
        Matrix m = SolverTest.randomSequentialAccessSparseMatrix(100, 90, 0, 0, 1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 0, 0, 1.0, false);
        dm.setConf(getConfiguration());
        Vector expected = new DenseVector(0);
        for (int i = 0; i < (m.numRows()); i++) {
            expected.assign(m.viewRow(i), PLUS);
        }
        expected.assign(DIV, m.numRows());
        Vector actual = dm.columnMeans();
        TestDistributedRowMatrix.assertEquals(0.0, expected.getDistanceSquared(actual), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMatrixTimesVector() throws Exception {
        Vector v = new RandomAccessSparseVector(50);
        v.assign(1.0);
        Matrix m = SolverTest.randomSequentialAccessSparseMatrix(100, 90, 50, 20, 1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 50, 20, 1.0, false);
        dm.setConf(getConfiguration());
        Vector expected = m.times(v);
        Vector actual = dm.times(v);
        TestDistributedRowMatrix.assertEquals(0.0, expected.getDistanceSquared(actual), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMatrixTimesSquaredVector() throws Exception {
        Vector v = new RandomAccessSparseVector(50);
        v.assign(1.0);
        Matrix m = SolverTest.randomSequentialAccessSparseMatrix(100, 90, 50, 20, 1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 50, 20, 1.0, false);
        dm.setConf(getConfiguration());
        Vector expected = m.timesSquared(v);
        Vector actual = dm.timesSquared(v);
        TestDistributedRowMatrix.assertEquals(0.0, expected.getDistanceSquared(actual), 1.0E-9);
    }

    @Test
    public void testMatrixTimesMatrix() throws Exception {
        Matrix inputA = SolverTest.randomSequentialAccessSparseMatrix(20, 19, 15, 5, 10.0);
        Matrix inputB = SolverTest.randomSequentialAccessSparseMatrix(20, 13, 25, 10, 5.0);
        Matrix expected = inputA.transpose().times(inputB);
        DistributedRowMatrix distA = randomDistributedMatrix(20, 19, 15, 5, 10.0, false, "distA");
        distA.setConf(getConfiguration());
        DistributedRowMatrix distB = randomDistributedMatrix(20, 13, 25, 10, 5.0, false, "distB");
        distB.setConf(getConfiguration());
        DistributedRowMatrix product = distA.times(distB);
        TestDistributedRowMatrix.assertEquals(expected, product, MahoutTestCase.EPSILON);
    }

    @Test
    public void testMatrixMultiplactionJobConfBuilder() throws Exception {
        Configuration initialConf = createInitialConf();
        Path baseTmpDirPath = getTestTempDirPath("testpaths");
        Path aPath = new Path(baseTmpDirPath, "a");
        Path bPath = new Path(baseTmpDirPath, "b");
        Path outPath = new Path(baseTmpDirPath, "out");
        Configuration mmJobConf = MatrixMultiplicationJob.createMatrixMultiplyJobConf(aPath, bPath, outPath, 10);
        Configuration mmCustomJobConf = MatrixMultiplicationJob.createMatrixMultiplyJobConf(initialConf, aPath, bPath, outPath, 10);
        assertNull(mmJobConf.get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, mmCustomJobConf.get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
    }

    @Test
    public void testTransposeJobConfBuilder() throws Exception {
        Configuration initialConf = createInitialConf();
        Path baseTmpDirPath = getTestTempDirPath("testpaths");
        Path inputPath = new Path(baseTmpDirPath, "input");
        Path outputPath = new Path(baseTmpDirPath, "output");
        Configuration transposeJobConf = TransposeJob.buildTransposeJob(inputPath, outputPath, 10).getConfiguration();
        Configuration transposeCustomJobConf = TransposeJob.buildTransposeJob(initialConf, inputPath, outputPath, 10).getConfiguration();
        assertNull(transposeJobConf.get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, transposeCustomJobConf.get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
    }

    @Test
    public void testTimesSquaredJobConfBuilders() throws Exception {
        Configuration initialConf = createInitialConf();
        Path baseTmpDirPath = getTestTempDirPath("testpaths");
        Path inputPath = new Path(baseTmpDirPath, "input");
        Path outputPath = new Path(baseTmpDirPath, "output");
        Vector v = new RandomAccessSparseVector(50);
        v.assign(1.0);
        Job timesSquaredJob1 = TimesSquaredJob.createTimesSquaredJob(v, inputPath, outputPath);
        Job customTimesSquaredJob1 = TimesSquaredJob.createTimesSquaredJob(initialConf, v, inputPath, outputPath);
        assertNull(timesSquaredJob1.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, customTimesSquaredJob1.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        Job timesJob = TimesSquaredJob.createTimesJob(v, 50, inputPath, outputPath);
        Job customTimesJob = TimesSquaredJob.createTimesJob(initialConf, v, 50, inputPath, outputPath);
        assertNull(timesJob.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, customTimesJob.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        Job timesSquaredJob2 = TimesSquaredJob.createTimesSquaredJob(v, inputPath, outputPath, TimesSquaredMapper.class, VectorSummingReducer.class);
        Job customTimesSquaredJob2 = TimesSquaredJob.createTimesSquaredJob(initialConf, v, inputPath, outputPath, TimesSquaredMapper.class, VectorSummingReducer.class);
        assertNull(timesSquaredJob2.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, customTimesSquaredJob2.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        Job timesSquaredJob3 = TimesSquaredJob.createTimesSquaredJob(v, 50, inputPath, outputPath, TimesSquaredMapper.class, VectorSummingReducer.class);
        Job customTimesSquaredJob3 = TimesSquaredJob.createTimesSquaredJob(initialConf, v, 50, inputPath, outputPath, TimesSquaredMapper.class, VectorSummingReducer.class);
        assertNull(timesSquaredJob3.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
        TestDistributedRowMatrix.assertEquals(TestDistributedRowMatrix.TEST_PROPERTY_VALUE, customTimesSquaredJob3.getConfiguration().get(TestDistributedRowMatrix.TEST_PROPERTY_KEY));
    }

    @Test
    public void testTimesVectorTempDirDeletion() throws Exception {
        Configuration conf = getConfiguration();
        Vector v = new RandomAccessSparseVector(50);
        v.assign(1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 50, 20, 1.0, false);
        dm.setConf(conf);
        Path outputPath = dm.getOutputTempPath();
        FileSystem fs = outputPath.getFileSystem(conf);
        TestDistributedRowMatrix.deleteContentsOfPath(conf, outputPath);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        Vector result1 = dm.times(v);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        TestDistributedRowMatrix.deleteContentsOfPath(conf, outputPath);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        conf.setBoolean(KEEP_TEMP_FILES, true);
        dm.setConf(conf);
        Vector result2 = dm.times(v);
        FileStatus[] outputStatuses = fs.listStatus(outputPath);
        TestDistributedRowMatrix.assertEquals(1, outputStatuses.length);
        Path outputTempPath = outputStatuses[0].getPath();
        Path inputVectorPath = new Path(outputTempPath, INPUT_VECTOR);
        Path outputVectorPath = new Path(outputTempPath, OUTPUT_VECTOR_FILENAME);
        TestDistributedRowMatrix.assertEquals(1, fs.listStatus(inputVectorPath, PathFilters.logsCRCFilter()).length);
        TestDistributedRowMatrix.assertEquals(1, fs.listStatus(outputVectorPath, PathFilters.logsCRCFilter()).length);
        TestDistributedRowMatrix.assertEquals(0.0, result1.getDistanceSquared(result2), MahoutTestCase.EPSILON);
    }

    @Test
    public void testTimesSquaredVectorTempDirDeletion() throws Exception {
        Configuration conf = getConfiguration();
        Vector v = new RandomAccessSparseVector(50);
        v.assign(1.0);
        DistributedRowMatrix dm = randomDistributedMatrix(100, 90, 50, 20, 1.0, false);
        dm.setConf(getConfiguration());
        Path outputPath = dm.getOutputTempPath();
        FileSystem fs = outputPath.getFileSystem(conf);
        TestDistributedRowMatrix.deleteContentsOfPath(conf, outputPath);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        Vector result1 = dm.timesSquared(v);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        TestDistributedRowMatrix.deleteContentsOfPath(conf, outputPath);
        TestDistributedRowMatrix.assertEquals(0, HadoopUtil.listStatus(fs, outputPath).length);
        conf.setBoolean(KEEP_TEMP_FILES, true);
        dm.setConf(conf);
        Vector result2 = dm.timesSquared(v);
        FileStatus[] outputStatuses = fs.listStatus(outputPath);
        TestDistributedRowMatrix.assertEquals(1, outputStatuses.length);
        Path outputTempPath = outputStatuses[0].getPath();
        Path inputVectorPath = new Path(outputTempPath, INPUT_VECTOR);
        Path outputVectorPath = new Path(outputTempPath, OUTPUT_VECTOR_FILENAME);
        TestDistributedRowMatrix.assertEquals(1, fs.listStatus(inputVectorPath, PathFilters.logsCRCFilter()).length);
        TestDistributedRowMatrix.assertEquals(1, fs.listStatus(outputVectorPath, PathFilters.logsCRCFilter()).length);
        TestDistributedRowMatrix.assertEquals(0.0, result1.getDistanceSquared(result2), MahoutTestCase.EPSILON);
    }
}

