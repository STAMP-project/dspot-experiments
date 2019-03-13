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


import java.util.ArrayList;
import java.util.List;
import org.datavec.api.records.SequenceRecord;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVNLinesSequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 19/09/2016.
 */
public class CSVNLinesSequenceRecordReaderTest {
    @Test
    public void testCSVNLinesSequenceRecordReader() throws Exception {
        int nLinesPerSequence = 10;
        SequenceRecordReader seqRR = new CSVNLinesSequenceRecordReader(nLinesPerSequence);
        seqRR.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        CSVRecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        int count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> next = seqRR.sequenceRecord();
            List<List<Writable>> expected = new ArrayList<>();
            for (int i = 0; i < nLinesPerSequence; i++) {
                expected.add(rr.next());
            }
            Assert.assertEquals(10, next.size());
            Assert.assertEquals(expected, next);
            count++;
        } 
        Assert.assertEquals((150 / nLinesPerSequence), count);
    }

    @Test
    public void testCSVNlinesSequenceRecordReaderMetaData() throws Exception {
        int nLinesPerSequence = 10;
        SequenceRecordReader seqRR = new CSVNLinesSequenceRecordReader(nLinesPerSequence);
        seqRR.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        CSVRecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/iris.dat").getFile()));
        List<List<List<Writable>>> out = new ArrayList<>();
        while (seqRR.hasNext()) {
            List<List<Writable>> next = seqRR.sequenceRecord();
            out.add(next);
        } 
        seqRR.reset();
        List<List<List<Writable>>> out2 = new ArrayList<>();
        List<SequenceRecord> out3 = new ArrayList<>();
        List<RecordMetaData> meta = new ArrayList<>();
        while (seqRR.hasNext()) {
            SequenceRecord seq = seqRR.nextSequence();
            out2.add(seq.getSequenceRecord());
            meta.add(seq.getMetaData());
            out3.add(seq);
        } 
        Assert.assertEquals(out, out2);
        List<SequenceRecord> out4 = seqRR.loadSequenceFromMetaData(meta);
        Assert.assertEquals(out3, out4);
    }
}

