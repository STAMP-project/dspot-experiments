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
package org.deeplearning4j.spark.datavec.iterator;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator;
import org.deeplearning4j.spark.BaseSparkTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.io.ClassPathResource;


public class TestIteratorUtils extends BaseSparkTest {
    @Test
    public void testIrisRRMDSI() throws Exception {
        ClassPathResource cpr = new ClassPathResource("iris.txt");
        File f = cpr.getFile();
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(f));
        RecordReaderMultiDataSetIterator rrmdsi1 = new RecordReaderMultiDataSetIterator.Builder(1).addReader("reader", rr).addInput("reader", 0, 3).addOutputOneHot("reader", 4, 3).build();
        RecordReaderMultiDataSetIterator rrmdsi2 = new RecordReaderMultiDataSetIterator.Builder(1).addReader("reader", new SparkSourceDummyReader(0)).addInput("reader", 0, 3).addOutputOneHot("reader", 4, 3).build();
        List<MultiDataSet> expected = new ArrayList<>(150);
        while (rrmdsi1.hasNext()) {
            expected.add(rrmdsi1.next());
        } 
        JavaRDD<List<Writable>> rdd = sc.textFile(f.getPath()).coalesce(1).map(new org.datavec.spark.transform.misc.StringToWritablesFunction(new CSVRecordReader()));
        JavaRDD<MultiDataSet> mdsRdd = IteratorUtils.mapRRMDSI(rdd, rrmdsi2);
        List<MultiDataSet> act = mdsRdd.collect();
        Assert.assertEquals(expected, act);
    }

    @Test
    public void testRRMDSIJoin() throws Exception {
        ClassPathResource cpr1 = new ClassPathResource("spark/rrmdsi/file1.txt");
        ClassPathResource cpr2 = new ClassPathResource("spark/rrmdsi/file2.txt");
        RecordReader rr1 = new CSVRecordReader();
        rr1.initialize(new FileSplit(cpr1.getFile()));
        RecordReader rr2 = new CSVRecordReader();
        rr2.initialize(new FileSplit(cpr2.getFile()));
        RecordReaderMultiDataSetIterator rrmdsi1 = new RecordReaderMultiDataSetIterator.Builder(1).addReader("r1", rr1).addReader("r2", rr2).addInput("r1", 1, 2).addOutput("r2", 1, 2).build();
        RecordReaderMultiDataSetIterator rrmdsi2 = new RecordReaderMultiDataSetIterator.Builder(1).addReader("r1", new SparkSourceDummyReader(0)).addReader("r2", new SparkSourceDummyReader(1)).addInput("r1", 1, 2).addOutput("r2", 1, 2).build();
        List<MultiDataSet> expected = new ArrayList<>(3);
        while (rrmdsi1.hasNext()) {
            expected.add(rrmdsi1.next());
        } 
        JavaRDD<List<Writable>> rdd1 = sc.textFile(cpr1.getFile().getPath()).coalesce(1).map(new org.datavec.spark.transform.misc.StringToWritablesFunction(new CSVRecordReader()));
        JavaRDD<List<Writable>> rdd2 = sc.textFile(cpr2.getFile().getPath()).coalesce(1).map(new org.datavec.spark.transform.misc.StringToWritablesFunction(new CSVRecordReader()));
        List<JavaRDD<List<Writable>>> list = Arrays.asList(rdd1, rdd2);
        JavaRDD<MultiDataSet> mdsRdd = IteratorUtils.mapRRMDSI(list, null, new int[]{ 0, 0 }, null, false, rrmdsi2);
        List<MultiDataSet> act = mdsRdd.collect();
        expected = new ArrayList(expected);
        act = new ArrayList(act);
        Comparator<MultiDataSet> comp = new Comparator<MultiDataSet>() {
            @Override
            public int compare(MultiDataSet d1, MultiDataSet d2) {
                return Double.compare(d1.getFeatures(0).getDouble(0), d2.getFeatures(0).getDouble(0));
            }
        };
        Collections.sort(expected, comp);
        Collections.sort(act, comp);
        Assert.assertEquals(expected, act);
    }
}

