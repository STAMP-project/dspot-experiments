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
package org.datavec.image.loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;

import static CifarLoader.NUM_TEST_IMAGES;
import static LFWLoader.ALLOWED_FORMATS;
import static LFWLoader.LABEL_PATTERN;


/**
 *
 */
public class LoaderTests {
    @Test
    public void testLfwReader() throws Exception {
        String subDir = "lfw-a/lfw";
        File path = new File(FilenameUtils.concat(System.getProperty("user.home"), subDir));
        FileSplit fileSplit = new FileSplit(path, ALLOWED_FORMATS, new Random(42));
        BalancedPathFilter pathFilter = new BalancedPathFilter(new Random(42), LABEL_PATTERN, 1, 1, 1);
        InputSplit[] inputSplit = fileSplit.sample(pathFilter, 1);
        RecordReader rr = new org.datavec.image.recordreader.ImageRecordReader(250, 250, 3, LABEL_PATTERN);
        rr.initialize(inputSplit[0]);
        List<String> exptedLabel = rr.getLabels();
        RecordReader rr2 = new LFWLoader(new long[]{ 250, 250, 3 }, true).getRecordReader(1, 1, 1, new Random(42));
        Assert.assertEquals(exptedLabel.get(0), rr2.getLabels().get(0));
    }

    @Test
    public void testCifarLoader() {
        File dir = new File(FilenameUtils.concat(System.getProperty("user.home"), "cifar/cifar-10-batches-bin"));
        CifarLoader cifar = new CifarLoader(false, dir);
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(((cifar.getLabels()) != null));
    }

    @Test
    public void testCifarInputStream() throws Exception {
        // check train
        String subDir = "cifar/cifar-10-batches-bin/data_batch_1.bin";
        String path = FilenameUtils.concat(System.getProperty("user.home"), subDir);
        byte[] fullDataExpected = new byte[3073];
        FileInputStream inExpected = new FileInputStream(new File(path));
        inExpected.read(fullDataExpected);
        byte[] fullDataActual = new byte[3073];
        CifarLoader cifarLoad = new CifarLoader(true);
        InputStream inActual = cifarLoad.getInputStream();
        inActual.read(fullDataActual);
        Assert.assertEquals(fullDataExpected[0], fullDataActual[0]);
        // check test
        subDir = "cifar/cifar-10-batches-bin/test_batch.bin";
        path = FilenameUtils.concat(System.getProperty("user.home"), subDir);
        fullDataExpected = new byte[3073];
        inExpected = new FileInputStream(new File(path));
        inExpected.read(fullDataExpected);
        fullDataActual = new byte[3073];
        cifarLoad = new CifarLoader(false);
        inActual = cifarLoad.getInputStream();
        inActual.read(fullDataActual);
        Assert.assertEquals(fullDataExpected[0], fullDataActual[0]);
    }

    @Test
    public void testCifarLoaderNext() throws Exception {
        boolean train = true;
        boolean preProcessCifar = false;
        int numExamples = 10;
        int row = 28;
        int col = 28;
        int channels = 1;
        CifarLoader loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        DataSet data = loader.next(numExamples);
        Assert.assertEquals(numExamples, data.getLabels().size(0));
        Assert.assertEquals(channels, data.getFeatures().size(1));
        train = true;
        preProcessCifar = true;
        row = 32;
        col = 32;
        channels = 3;
        loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        data = loader.next(1);
        Assert.assertEquals(1, data.getFeatures().size(0));
        Assert.assertEquals(((channels * row) * col), data.getFeatures().ravel().length());
        train = false;
        preProcessCifar = false;
        loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        data = loader.next(numExamples);
        Assert.assertEquals(row, data.getFeatures().size(2));
        train = false;
        preProcessCifar = true;
        loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        data = loader.next(numExamples);
        Assert.assertEquals(numExamples, data.getLabels().size(0));
        Assert.assertEquals(col, data.getFeatures().size(3));
    }

    @Test
    public void testCifarLoaderReset() throws Exception {
        int numExamples = 50;
        int row = 28;
        int col = 28;
        int channels = 3;
        CifarLoader loader = new CifarLoader(row, col, channels, null, false, false, false);
        DataSet data;
        for (int i = 0; i < ((NUM_TEST_IMAGES) / numExamples); i++) {
            loader.next(numExamples);
        }
        data = loader.next(numExamples);
        Assert.assertEquals(new DataSet(), data);
        loader.reset();
        data = loader.next(numExamples);
        Assert.assertEquals(numExamples, data.getLabels().size(0));
    }

    @Test
    public void testCifarLoaderWithBiggerImage() {
        boolean train = true;
        boolean preProcessCifar = false;
        int row = 128;
        int col = 128;
        int channels = 3;
        int numExamples = 10;
        CifarLoader loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        DataSet data = loader.next(numExamples);
        long[] shape = data.getFeatures().shape();
        Assert.assertEquals(shape.length, 4);
        Assert.assertEquals(shape[0], numExamples);
        Assert.assertEquals(shape[1], channels);
        Assert.assertEquals(shape[2], row);
        Assert.assertEquals(shape[2], col);
    }

    @Test
    public void testCifarLoaderExpNumExamples() throws Exception {
        boolean train = true;
        boolean preProcessCifar = false;
        int numExamples = 10;
        int row = 28;
        int col = 28;
        int channels = 1;
        CifarLoader loader = new CifarLoader(row, col, channels, train, preProcessCifar);
        int minibatch = 100;
        int nMinibatches = 50000 / minibatch;
        for (int i = 0; i < nMinibatches; i++) {
            DataSet ds = loader.next(minibatch);
            String s = String.valueOf(i);
            Assert.assertNotNull(s, ds.getFeatures());
            Assert.assertNotNull(s, ds.getLabels());
            Assert.assertEquals(s, minibatch, ds.getFeatures().size(0));
            Assert.assertEquals(s, minibatch, ds.getLabels().size(0));
            Assert.assertEquals(s, 10, ds.getLabels().size(1));
        }
    }
}

