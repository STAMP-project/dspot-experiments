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
import java.util.Arrays;
import java.util.List;
import org.datavec.api.records.SequenceRecord;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 21/05/2016.
 */
public class TestCollectionRecordReaders {
    @Test
    public void testCollectionSequenceRecordReader() throws Exception {
        List<List<List<Writable>>> listOfSequences = new ArrayList<>();
        List<List<Writable>> sequence1 = new ArrayList<>();
        sequence1.add(Arrays.asList(((Writable) (new IntWritable(0))), new IntWritable(1)));
        sequence1.add(Arrays.asList(((Writable) (new IntWritable(2))), new IntWritable(3)));
        listOfSequences.add(sequence1);
        List<List<Writable>> sequence2 = new ArrayList<>();
        sequence2.add(Arrays.asList(((Writable) (new IntWritable(4))), new IntWritable(5)));
        sequence2.add(Arrays.asList(((Writable) (new IntWritable(6))), new IntWritable(7)));
        listOfSequences.add(sequence2);
        SequenceRecordReader seqRR = new org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader(listOfSequences);
        Assert.assertTrue(seqRR.hasNext());
        Assert.assertEquals(sequence1, seqRR.sequenceRecord());
        Assert.assertEquals(sequence2, seqRR.sequenceRecord());
        Assert.assertFalse(seqRR.hasNext());
        seqRR.reset();
        Assert.assertEquals(sequence1, seqRR.sequenceRecord());
        Assert.assertEquals(sequence2, seqRR.sequenceRecord());
        Assert.assertFalse(seqRR.hasNext());
        // Test metadata:
        seqRR.reset();
        List<List<List<Writable>>> out2 = new ArrayList<>();
        List<SequenceRecord> seq = new ArrayList<>();
        List<RecordMetaData> meta = new ArrayList<>();
        while (seqRR.hasNext()) {
            SequenceRecord r = seqRR.nextSequence();
            out2.add(r.getSequenceRecord());
            seq.add(r);
            meta.add(r.getMetaData());
        } 
        Assert.assertEquals(listOfSequences, out2);
        List<SequenceRecord> fromMeta = seqRR.loadSequenceFromMetaData(meta);
        Assert.assertEquals(seq, fromMeta);
    }
}

