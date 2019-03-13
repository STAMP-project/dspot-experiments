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
package org.datavec.api.records.reader.impl;


import java.util.LinkedList;
import java.util.List;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVVariableSlidingWindowRecordReader;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Tests for variable sliding window csv reader.
 *
 * @author Justin Long (crockpotveggies)
 */
public class CSVVariableSlidingWindowRecordReaderTest {
    @Test
    public void testCSVVariableSlidingWindowRecordReader() throws Exception {
        int maxLinesPerSequence = 3;
        SequenceRecordReader seqRR = new CSVVariableSlidingWindowRecordReader(maxLinesPerSequence);
        seqRR.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        CSVRecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        int count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> next = seqRR.sequenceRecord();
            if (count == (maxLinesPerSequence - 1)) {
                LinkedList<List<Writable>> expected = new LinkedList<>();
                for (int i = 0; i < maxLinesPerSequence; i++) {
                    expected.addFirst(rr.next());
                }
                Assert.assertEquals(expected, next);
            }
            if (count == maxLinesPerSequence) {
                Assert.assertEquals(maxLinesPerSequence, next.size());
            }
            if (count == 0) {
                // first seq should be length 1
                Assert.assertEquals(1, next.size());
            }
            if (count > 151) {
                // last seq should be length 1
                Assert.assertEquals(1, next.size());
            }
            count++;
        } 
        Assert.assertEquals(152, count);
    }

    @Test
    public void testCSVVariableSlidingWindowRecordReaderStride() throws Exception {
        int maxLinesPerSequence = 3;
        int stride = 2;
        SequenceRecordReader seqRR = new CSVVariableSlidingWindowRecordReader(maxLinesPerSequence, stride);
        seqRR.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        CSVRecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        int count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> next = seqRR.sequenceRecord();
            if (count == (maxLinesPerSequence - 1)) {
                LinkedList<List<Writable>> expected = new LinkedList<>();
                for (int s = 0; s < stride; s++) {
                    expected = new LinkedList();
                    for (int i = 0; i < maxLinesPerSequence; i++) {
                        expected.addFirst(rr.next());
                    }
                }
                Assert.assertEquals(expected, next);
            }
            if (count == maxLinesPerSequence) {
                Assert.assertEquals(maxLinesPerSequence, next.size());
            }
            if (count == 0) {
                // first seq should be length 2
                Assert.assertEquals(2, next.size());
            }
            if (count > 151) {
                // last seq should be length 1
                Assert.assertEquals(1, next.size());
            }
            count++;
        } 
        Assert.assertEquals(76, count);
    }
}

