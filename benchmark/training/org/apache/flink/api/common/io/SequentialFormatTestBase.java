/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.common.io;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileInputSplit;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test base for {@link BinaryInputFormat} and {@link BinaryOutputFormat}.
 */
public abstract class SequentialFormatTestBase<T> extends TestLogger {
    private class InputSplitSorter implements Comparator<FileInputSplit> {
        @Override
        public int compare(FileInputSplit o1, FileInputSplit o2) {
            int pathOrder = o1.getPath().getName().compareTo(o2.getPath().getName());
            return pathOrder == 0 ? Long.signum(((o1.getStart()) - (o2.getStart()))) : pathOrder;
        }
    }

    private int numberOfTuples;

    protected long blockSize;

    private int parallelism;

    private int[] rawDataSizes;

    protected File tempFile;

    /**
     * Initializes SequentialFormatTest.
     */
    public SequentialFormatTestBase(int numberOfTuples, long blockSize, int parallelism) {
        this.numberOfTuples = numberOfTuples;
        this.blockSize = blockSize;
        this.parallelism = parallelism;
        this.rawDataSizes = new int[parallelism];
    }

    /**
     * Checks if the expected input splits were created.
     */
    @Test
    public void checkInputSplits() throws IOException {
        FileInputSplit[] inputSplits = this.createInputFormat().createInputSplits(0);
        Arrays.sort(inputSplits, new InputSplitSorter());
        int splitIndex = 0;
        for (int fileIndex = 0; fileIndex < (this.parallelism); fileIndex++) {
            List<FileInputSplit> sameFileSplits = new ArrayList<FileInputSplit>();
            Path lastPath = inputSplits[splitIndex].getPath();
            for (; splitIndex < (inputSplits.length); splitIndex++) {
                if (!(inputSplits[splitIndex].getPath().equals(lastPath))) {
                    break;
                }
                sameFileSplits.add(inputSplits[splitIndex]);
            }
            Assert.assertEquals(this.getExpectedBlockCount(fileIndex), sameFileSplits.size());
            long lastBlockLength = ((this.rawDataSizes[fileIndex]) % ((this.blockSize) - (getInfoSize()))) + (getInfoSize());
            for (int index = 0; index < (sameFileSplits.size()); index++) {
                Assert.assertEquals(((this.blockSize) * index), sameFileSplits.get(index).getStart());
                if (index < ((sameFileSplits.size()) - 1)) {
                    Assert.assertEquals(this.blockSize, sameFileSplits.get(index).getLength());
                }
            }
            Assert.assertEquals(lastBlockLength, sameFileSplits.get(((sameFileSplits.size()) - 1)).getLength());
        }
    }

    /**
     * Tests if the expected sequence and amount of data can be read.
     */
    @Test
    public void checkRead() throws Exception {
        BinaryInputFormat<T> input = this.createInputFormat();
        FileInputSplit[] inputSplits = input.createInputSplits(0);
        Arrays.sort(inputSplits, new InputSplitSorter());
        int readCount = 0;
        for (FileInputSplit inputSplit : inputSplits) {
            input.open(inputSplit);
            input.reopen(inputSplit, input.getCurrentState());
            T record = createInstance();
            while (!(input.reachedEnd())) {
                if ((input.nextRecord(record)) != null) {
                    this.checkEquals(this.getRecord(readCount), record);
                    if (!(input.reachedEnd())) {
                        Tuple2<Long, Long> state = input.getCurrentState();
                        input = this.createInputFormat();
                        input.reopen(inputSplit, state);
                    }
                    readCount++;
                }
            } 
        }
        Assert.assertEquals(this.numberOfTuples, readCount);
    }

    /**
     * Tests the statistics of the given format.
     */
    @Test
    public void checkStatistics() {
        BinaryInputFormat<T> input = this.createInputFormat();
        BaseStatistics statistics = input.getStatistics(null);
        Assert.assertEquals(this.numberOfTuples, statistics.getNumberOfRecords());
    }

    /**
     * Tests if the length of the file matches the expected value.
     */
    @Test
    public void checkLength() {
        File[] files = (this.tempFile.isDirectory()) ? this.tempFile.listFiles() : new File[]{ this.tempFile };
        Arrays.sort(files);
        for (int fileIndex = 0; fileIndex < (this.parallelism); fileIndex++) {
            long lastBlockLength = (this.rawDataSizes[fileIndex]) % ((this.blockSize) - (getInfoSize()));
            long expectedLength = ((((this.getExpectedBlockCount(fileIndex)) - 1) * (this.blockSize)) + (getInfoSize())) + lastBlockLength;
            Assert.assertEquals(expectedLength, files[fileIndex].length());
        }
    }

    /**
     * Counts the bytes that would be written.
     */
    private static final class ByteCounter extends OutputStream {
        int length = 0;

        /**
         * Returns the length.
         *
         * @return the length
         */
        public int getLength() {
            return this.length;
        }

        @Override
        public void write(int b) throws IOException {
            (this.length)++;
        }
    }
}

