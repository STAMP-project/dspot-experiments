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


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.spark.BaseSparkTest;
import org.datavec.spark.functions.data.FilesAsBytesFunction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


public class TestRecordReaderBytesFunction extends BaseSparkTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testRecordReaderBytesFunction() throws Exception {
        JavaSparkContext sc = getContext();
        // Local file path
        File f = testDir.newFolder();
        new ClassPathResource("datavec-spark/imagetest/").copyDirectory(f);
        List<String> labelsList = Arrays.asList("0", "1");// Need this for Spark: can't infer without init call

        String path = (f.getAbsolutePath()) + "/*";
        // Load binary data from local file system, convert to a sequence file:
        // Load and convert
        JavaPairRDD<String, PortableDataStream> origData = sc.binaryFiles(path);
        JavaPairRDD<Text, BytesWritable> filesAsBytes = origData.mapToPair(new FilesAsBytesFunction());
        // Write the sequence file:
        Path p = Files.createTempDirectory("dl4j_rrbytesTest");
        p.toFile().deleteOnExit();
        String outPath = (p.toString()) + "/out";
        filesAsBytes.saveAsNewAPIHadoopFile(outPath, Text.class, BytesWritable.class, SequenceFileOutputFormat.class);
        // Load data from sequence file, parse via RecordReader:
        JavaPairRDD<Text, BytesWritable> fromSeqFile = sc.sequenceFile(outPath, Text.class, BytesWritable.class);
        ImageRecordReader irr = new ImageRecordReader(28, 28, 1, new ParentPathLabelGenerator());
        irr.setLabels(labelsList);
        JavaRDD<List<Writable>> dataVecData = fromSeqFile.map(new org.datavec.spark.functions.data.RecordReaderBytesFunction(irr));
        // Next: do the same thing locally, and compare the results
        InputSplit is = new FileSplit(f, new String[]{ "bmp" }, true);
        irr = new ImageRecordReader(28, 28, 1, new ParentPathLabelGenerator());
        irr.initialize(is);
        List<List<Writable>> list = new ArrayList<>(4);
        while (irr.hasNext()) {
            list.add(irr.next());
        } 
        List<List<Writable>> fromSequenceFile = dataVecData.collect();
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(4, fromSequenceFile.size());
        // Check that each of the values from Spark equals exactly one of the values doing it locally
        boolean[] found = new boolean[4];
        for (int i = 0; i < 4; i++) {
            int foundIndex = -1;
            List<Writable> collection = fromSequenceFile.get(i);
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

        // System.out.println("COUNT: " + count);
    }
}

