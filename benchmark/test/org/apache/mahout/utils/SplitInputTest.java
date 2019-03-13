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
package org.apache.mahout.utils;


import java.nio.charset.Charset;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.map.OpenObjectIntHashMap;
import org.junit.Test;


public final class SplitInputTest extends MahoutTestCase {
    private OpenObjectIntHashMap<String> countMap;

    private Charset charset;

    private FileSystem fs;

    private Path tempInputFile;

    private Path tempTrainingDirectory;

    private Path tempTestDirectory;

    private Path tempMapRedOutputDirectory;

    private Path tempInputDirectory;

    private Path tempSequenceDirectory;

    private SplitInput si;

    @Test
    public void testSplitDirectory() throws Exception {
        writeMultipleInputFiles();
        final int testSplitSize = 1;
        si.setTestSplitSize(testSplitSize);
        si.setCallback(new SplitInput.SplitCallback() {
            @Override
            public void splitComplete(Path inputFile, int lineCount, int trainCount, int testCount, int testSplitStart) {
                int trainingLines = (countMap.get(inputFile.getName())) - testSplitSize;
                SplitInputTest.assertSplit(fs, inputFile, charset, testSplitSize, trainingLines, tempTrainingDirectory, tempTestDirectory);
            }
        });
        si.splitDirectory(tempInputDirectory);
    }

    @Test
    public void testSplitFile() throws Exception {
        writeSingleInputFile();
        si.setTestSplitSize(2);
        si.setCallback(new SplitInputTest.TestCallback(2, 10));
        si.splitFile(tempInputFile);
    }

    @Test
    public void testSplitFileLocation() throws Exception {
        writeSingleInputFile();
        si.setTestSplitSize(2);
        si.setSplitLocation(50);
        si.setCallback(new SplitInputTest.TestCallback(2, 10));
        si.splitFile(tempInputFile);
    }

    @Test
    public void testSplitFilePct() throws Exception {
        writeSingleInputFile();
        si.setTestSplitPct(25);
        si.setCallback(new SplitInputTest.TestCallback(3, 9));
        si.splitFile(tempInputFile);
    }

    @Test
    public void testSplitFilePctLocation() throws Exception {
        writeSingleInputFile();
        si.setTestSplitPct(25);
        si.setSplitLocation(50);
        si.setCallback(new SplitInputTest.TestCallback(3, 9));
        si.splitFile(tempInputFile);
    }

    @Test
    public void testSplitFileRandomSelectionSize() throws Exception {
        writeSingleInputFile();
        si.setTestRandomSelectionSize(5);
        si.setCallback(new SplitInputTest.TestCallback(5, 7));
        si.splitFile(tempInputFile);
    }

    @Test
    public void testSplitFileRandomSelectionPct() throws Exception {
        writeSingleInputFile();
        si.setTestRandomSelectionPct(25);
        si.setCallback(new SplitInputTest.TestCallback(3, 9));
        si.splitFile(tempInputFile);
    }

    /**
     * Test map reduce version of split input with Text, Text key value
     * pairs in input
     */
    @Test
    public void testSplitInputMapReduceText() throws Exception {
        writeTextSequenceFile(tempSequenceDirectory, 1000);
        testSplitInputMapReduce(1000);
    }

    /**
     * Test map reduce version of split input with Text, Text key value pairs in input called from command line
     */
    @Test
    public void testSplitInputMapReduceTextCli() throws Exception {
        writeTextSequenceFile(tempSequenceDirectory, 1000);
        testSplitInputMapReduceCli(1000);
    }

    /**
     * Test map reduce version of split input with IntWritable, Vector key value
     * pairs in input
     */
    @Test
    public void testSplitInputMapReduceVector() throws Exception {
        writeVectorSequenceFile(tempSequenceDirectory, 1000);
        testSplitInputMapReduce(1000);
    }

    /**
     * Test map reduce version of split input with IntWritable, Vector key value
     * pairs in input called from command line
     */
    @Test
    public void testSplitInputMapReduceVectorCli() throws Exception {
        writeVectorSequenceFile(tempSequenceDirectory, 1000);
        testSplitInputMapReduceCli(1000);
    }

    @Test
    public void testValidate() throws Exception {
        SplitInput st = new SplitInput();
        SplitInputTest.assertValidateException(st);
        st.setTestSplitSize(100);
        SplitInputTest.assertValidateException(st);
        st.setTestOutputDirectory(tempTestDirectory);
        SplitInputTest.assertValidateException(st);
        st.setTrainingOutputDirectory(tempTrainingDirectory);
        st.validate();
        st.setTestSplitPct(50);
        SplitInputTest.assertValidateException(st);
        st = new SplitInput();
        st.setTestRandomSelectionPct(50);
        st.setTestOutputDirectory(tempTestDirectory);
        st.setTrainingOutputDirectory(tempTrainingDirectory);
        st.validate();
        st.setTestSplitPct(50);
        SplitInputTest.assertValidateException(st);
        st = new SplitInput();
        st.setTestRandomSelectionPct(50);
        st.setTestOutputDirectory(tempTestDirectory);
        st.setTrainingOutputDirectory(tempTrainingDirectory);
        st.validate();
        st.setTestSplitSize(100);
        SplitInputTest.assertValidateException(st);
    }

    private class TestCallback implements SplitInput.SplitCallback {
        private final int testSplitSize;

        private final int trainingLines;

        private TestCallback(int testSplitSize, int trainingLines) {
            this.testSplitSize = testSplitSize;
            this.trainingLines = trainingLines;
        }

        @Override
        public void splitComplete(Path inputFile, int lineCount, int trainCount, int testCount, int testSplitStart) {
            SplitInputTest.assertSplit(fs, tempInputFile, charset, testSplitSize, trainingLines, tempTrainingDirectory, tempTestDirectory);
        }
    }
}

