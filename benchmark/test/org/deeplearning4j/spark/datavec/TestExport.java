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


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.api.java.JavaRDD;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.data.BatchAndExportDataSetsFunction;
import org.deeplearning4j.spark.data.BatchAndExportMultiDataSetsFunction;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 29/08/2016.
 */
public class TestExport extends BaseSparkTest {
    @Test
    public void testBatchAndExportDataSetsFunction() throws Exception {
        String baseDir = System.getProperty("java.io.tmpdir");
        baseDir = FilenameUtils.concat(baseDir, "dl4j_spark_testBatchAndExport/");
        baseDir = baseDir.replaceAll("\\\\", "/");
        File f = new File(baseDir);
        if (f.exists())
            FileUtils.deleteDirectory(f);

        f.mkdir();
        f.deleteOnExit();
        int minibatchSize = 5;
        int nIn = 4;
        int nOut = 3;
        List<DataSet> dataSets = new ArrayList<>();
        dataSets.add(new DataSet(Nd4j.create(10, nIn), Nd4j.create(10, nOut)));// Larger than minibatch size -> tests splitting

        for (int i = 0; i < 98; i++) {
            if ((i % 2) == 0) {
                dataSets.add(new DataSet(Nd4j.create(5, nIn), Nd4j.create(5, nOut)));
            } else {
                dataSets.add(new DataSet(Nd4j.create(1, nIn), Nd4j.create(1, nOut)));
                dataSets.add(new DataSet(Nd4j.create(1, nIn), Nd4j.create(1, nOut)));
                dataSets.add(new DataSet(Nd4j.create(3, nIn), Nd4j.create(3, nOut)));
            }
        }
        Collections.shuffle(dataSets, new Random(12345));
        JavaRDD<DataSet> rdd = sc.parallelize(dataSets);
        rdd = rdd.repartition(1);// For testing purposes (should get exactly 100 out, but maybe more with more partitions)

        JavaRDD<String> pathsRdd = rdd.mapPartitionsWithIndex(new BatchAndExportDataSetsFunction(minibatchSize, ("file:///" + baseDir)), true);
        List<String> paths = pathsRdd.collect();
        Assert.assertEquals(100, paths.size());
        File[] files = f.listFiles();
        Assert.assertNotNull(files);
        int count = 0;
        for (File file : files) {
            if (!(file.getPath().endsWith(".bin")))
                continue;

            System.out.println(file);
            DataSet ds = new DataSet();
            ds.load(file);
            Assert.assertEquals(minibatchSize, ds.numExamples());
            count++;
        }
        Assert.assertEquals(100, count);
        FileUtils.deleteDirectory(f);
    }

    @Test
    public void testBatchAndExportMultiDataSetsFunction() throws Exception {
        String baseDir = System.getProperty("java.io.tmpdir");
        baseDir = FilenameUtils.concat(baseDir, "dl4j_spark_testBatchAndExportMDS/");
        baseDir = baseDir.replaceAll("\\\\", "/");
        File f = new File(baseDir);
        if (f.exists())
            FileUtils.deleteDirectory(f);

        f.mkdir();
        f.deleteOnExit();
        int minibatchSize = 5;
        int nIn = 4;
        int nOut = 3;
        List<MultiDataSet> dataSets = new ArrayList<>();
        dataSets.add(new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.create(10, nIn), Nd4j.create(10, nOut)));// Larger than minibatch size -> tests splitting

        for (int i = 0; i < 98; i++) {
            if ((i % 2) == 0) {
                dataSets.add(new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.create(5, nIn), Nd4j.create(5, nOut)));
            } else {
                dataSets.add(new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.create(1, nIn), Nd4j.create(1, nOut)));
                dataSets.add(new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.create(1, nIn), Nd4j.create(1, nOut)));
                dataSets.add(new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.create(3, nIn), Nd4j.create(3, nOut)));
            }
        }
        Collections.shuffle(dataSets, new Random(12345));
        JavaRDD<MultiDataSet> rdd = sc.parallelize(dataSets);
        rdd = rdd.repartition(1);// For testing purposes (should get exactly 100 out, but maybe more with more partitions)

        JavaRDD<String> pathsRdd = rdd.mapPartitionsWithIndex(new BatchAndExportMultiDataSetsFunction(minibatchSize, ("file:///" + baseDir)), true);
        List<String> paths = pathsRdd.collect();
        Assert.assertEquals(100, paths.size());
        File[] files = f.listFiles();
        Assert.assertNotNull(files);
        int count = 0;
        for (File file : files) {
            if (!(file.getPath().endsWith(".bin")))
                continue;

            System.out.println(file);
            MultiDataSet ds = new org.nd4j.linalg.dataset.MultiDataSet();
            ds.load(file);
            Assert.assertEquals(minibatchSize, ds.getFeatures(0).size(0));
            Assert.assertEquals(minibatchSize, ds.getLabels(0).size(0));
            count++;
        }
        Assert.assertEquals(100, count);
        FileUtils.deleteDirectory(f);
    }
}

