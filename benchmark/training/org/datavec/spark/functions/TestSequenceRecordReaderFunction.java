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
package org.datavec.spark.functions;


import CodecRecordReader.COLUMNS;
import CodecRecordReader.RAVEL;
import CodecRecordReader.ROWS;
import CodecRecordReader.START_FRAME;
import CodecRecordReader.TOTAL_FRAMES;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;
import org.datavec.codec.reader.CodecRecordReader;
import org.datavec.spark.BaseSparkTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


public class TestSequenceRecordReaderFunction extends BaseSparkTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testSequenceRecordReaderFunctionCSV() throws Exception {
        JavaSparkContext sc = getContext();
        File f = testDir.newFolder();
        new ClassPathResource("datavec-spark/csvsequence/").copyDirectory(f);
        String path = (f.getAbsolutePath()) + "/*";
        JavaPairRDD<String, PortableDataStream> origData = sc.binaryFiles(path);
        Assert.assertEquals(3, origData.count());// 3 CSV files

        SequenceRecordReaderFunction srrf = new SequenceRecordReaderFunction(new CSVSequenceRecordReader(1, ","));// CSV, skip 1 line

        JavaRDD<List<List<Writable>>> rdd = origData.map(srrf);
        List<List<List<Writable>>> listSpark = rdd.collect();
        Assert.assertEquals(3, listSpark.size());
        for (int i = 0; i < 3; i++) {
            List<List<Writable>> thisSequence = listSpark.get(i);
            Assert.assertEquals(4, thisSequence.size());// Expect exactly 4 time steps in sequence

            for (List<Writable> c : thisSequence) {
                Assert.assertEquals(3, c.size());// 3 values per time step

            }
        }
        // Load normally, and check that we get the same results (order not withstanding)
        InputSplit is = new FileSplit(f, new String[]{ "txt" }, true);
        // System.out.println("Locations:");
        // System.out.println(Arrays.toString(is.locations()));
        SequenceRecordReader srr = new CSVSequenceRecordReader(1, ",");
        srr.initialize(is);
        List<List<List<Writable>>> list = new ArrayList<>(3);
        while (srr.hasNext()) {
            list.add(srr.sequenceRecord());
        } 
        Assert.assertEquals(3, list.size());
        // System.out.println("Spark list:");
        // for(List<List<Writable>> c : listSpark ) System.out.println(c);
        // System.out.println("Local list:");
        // for(List<List<Writable>> c : list ) System.out.println(c);
        // Check that each of the values from Spark equals exactly one of the values doing it normally
        boolean[] found = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int foundIndex = -1;
            List<List<Writable>> collection = listSpark.get(i);
            for (int j = 0; j < 3; j++) {
                if (collection.equals(list.get(j))) {
                    if (foundIndex != (-1))
                        Assert.fail();
                    // Already found this value -> suggests this spark value equals two or more of local version? (Shouldn't happen)

                    foundIndex = j;
                    if (found[foundIndex])
                        Assert.fail();
                    // One of the other spark values was equal to this one -> suggests duplicates in Spark list

                    found[foundIndex] = true;// mark this one as seen before

                }
            }
        }
        int count = 0;
        for (boolean b : found)
            if (b)
                count++;


        Assert.assertEquals(3, count);// Expect all 3 and exactly 3 pairwise matches between spark and local versions

    }

    @Test
    public void testSequenceRecordReaderFunctionVideo() throws Exception {
        JavaSparkContext sc = getContext();
        File f = testDir.newFolder();
        new ClassPathResource("datavec-spark/video/").copyDirectory(f);
        String path = (f.getAbsolutePath()) + "/*";
        JavaPairRDD<String, PortableDataStream> origData = sc.binaryFiles(path);
        // System.out.println(origData.collectAsMap().keySet());
        Assert.assertEquals(4, origData.count());// 4 video files

        // Load 64x64, 25 frames - originally, 130x130, 150 frames
        SequenceRecordReader sparkSeqReader = new CodecRecordReader();
        Configuration conf = new Configuration();
        conf.set(RAVEL, "true");
        conf.set(START_FRAME, "0");
        conf.set(TOTAL_FRAMES, "25");
        conf.set(ROWS, "64");
        conf.set(COLUMNS, "64");
        Configuration confCopy = new Configuration(conf);
        sparkSeqReader.setConf(conf);
        SequenceRecordReaderFunction srrf = new SequenceRecordReaderFunction(sparkSeqReader);
        JavaRDD<List<List<Writable>>> rdd = origData.map(srrf);
        List<List<List<Writable>>> listSpark = rdd.collect();
        Assert.assertEquals(4, listSpark.size());
        for (int i = 0; i < 4; i++) {
            List<List<Writable>> thisSequence = listSpark.get(i);
            Assert.assertEquals(25, thisSequence.size());// Expect exactly 25 time steps (frames) in sequence

            for (List<Writable> c : thisSequence) {
                Assert.assertEquals(1, c.size());// 64*64 videos, RGB

                Assert.assertEquals(((64 * 64) * 3), length());
            }
        }
        // Load normally, and check that we get the same results (order not withstanding)
        InputSplit is = new FileSplit(f, new String[]{ "mp4" }, true);
        // System.out.println("Locations:");
        // System.out.println(Arrays.toString(is.locations()));
        SequenceRecordReader srr = new CodecRecordReader();
        srr.initialize(is);
        srr.setConf(confCopy);
        List<List<List<Writable>>> list = new ArrayList<>(4);
        while (srr.hasNext()) {
            list.add(srr.sequenceRecord());
        } 
        Assert.assertEquals(4, list.size());
        // System.out.println("Spark list:");
        // for(List<List<Writable>> c : listSpark ) System.out.println(c);
        // System.out.println("Local list:");
        // for(List<List<Writable>> c : list ) System.out.println(c);
        // Check that each of the values from Spark equals exactly one of the values doing it locally
        boolean[] found = new boolean[4];
        for (int i = 0; i < 4; i++) {
            int foundIndex = -1;
            List<List<Writable>> collection = listSpark.get(i);
            for (int j = 0; j < 4; j++) {
                if (collection.equals(list.get(j))) {
                    if (foundIndex != (-1))
                        Assert.fail();
                    // Already found this value -> suggests this spark value equals two or more of local version? (Shouldn't happen)

                    foundIndex = j;
                    if (found[foundIndex])
                        Assert.fail();
                    // One of the other spark values was equal to this one -> suggests duplicates in Spark list

                    found[foundIndex] = true;// mark this one as seen before

                }
            }
        }
        int count = 0;
        for (boolean b : found)
            if (b)
                count++;


        Assert.assertEquals(4, count);// Expect all 4 and exactly 4 pairwise matches between spark and local versions

    }
}

