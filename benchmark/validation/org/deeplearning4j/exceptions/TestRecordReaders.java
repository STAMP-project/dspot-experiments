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
package org.deeplearning4j.exceptions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;
import org.datavec.api.records.reader.impl.collection.CollectionRecordReader;
import org.datavec.api.records.reader.impl.collection.CollectionSequenceRecordReader;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


/**
 * Created by Alex on 14/11/2016.
 */
public class TestRecordReaders extends BaseDL4JTest {
    @Test
    public void testClassIndexOutsideOfRangeRRDSI() {
        Collection<Collection<Writable>> c = new ArrayList<>();
        c.add(Arrays.<Writable>asList(new DoubleWritable(0.5), new IntWritable(0)));
        c.add(Arrays.<Writable>asList(new DoubleWritable(1.0), new IntWritable(2)));
        CollectionRecordReader crr = new CollectionRecordReader(c);
        RecordReaderDataSetIterator iter = new RecordReaderDataSetIterator(crr, 2, 1, 2);
        try {
            DataSet ds = iter.next();
            TestCase.fail("Expected exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("to one-hot"));
        }
    }

    @Test
    public void testClassIndexOutsideOfRangeRRMDSI() {
        Collection<Collection<Collection<Writable>>> c = new ArrayList<>();
        Collection<Collection<Writable>> seq1 = new ArrayList<>();
        seq1.add(Arrays.<Writable>asList(new DoubleWritable(0.0), new IntWritable(0)));
        seq1.add(Arrays.<Writable>asList(new DoubleWritable(0.0), new IntWritable(1)));
        c.add(seq1);
        Collection<Collection<Writable>> seq2 = new ArrayList<>();
        seq2.add(Arrays.<Writable>asList(new DoubleWritable(0.0), new IntWritable(0)));
        seq2.add(Arrays.<Writable>asList(new DoubleWritable(0.0), new IntWritable(2)));
        c.add(seq2);
        CollectionSequenceRecordReader csrr = new CollectionSequenceRecordReader(c);
        DataSetIterator dsi = new org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator(csrr, 2, 2, 1);
        try {
            DataSet ds = dsi.next();
            TestCase.fail("Expected exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("to one-hot"));
        }
    }

    @Test
    public void testClassIndexOutsideOfRangeRRMDSI_MultipleReaders() {
        Collection<Collection<Collection<Writable>>> c1 = new ArrayList<>();
        Collection<Collection<Writable>> seq1 = new ArrayList<>();
        seq1.add(Arrays.<Writable>asList(new DoubleWritable(0.0)));
        seq1.add(Arrays.<Writable>asList(new DoubleWritable(0.0)));
        c1.add(seq1);
        Collection<Collection<Writable>> seq2 = new ArrayList<>();
        seq2.add(Arrays.<Writable>asList(new DoubleWritable(0.0)));
        seq2.add(Arrays.<Writable>asList(new DoubleWritable(0.0)));
        c1.add(seq2);
        Collection<Collection<Collection<Writable>>> c2 = new ArrayList<>();
        Collection<Collection<Writable>> seq1a = new ArrayList<>();
        seq1a.add(Arrays.<Writable>asList(new IntWritable(0)));
        seq1a.add(Arrays.<Writable>asList(new IntWritable(1)));
        c2.add(seq1a);
        Collection<Collection<Writable>> seq2a = new ArrayList<>();
        seq2a.add(Arrays.<Writable>asList(new IntWritable(0)));
        seq2a.add(Arrays.<Writable>asList(new IntWritable(2)));
        c2.add(seq2a);
        CollectionSequenceRecordReader csrr = new CollectionSequenceRecordReader(c1);
        CollectionSequenceRecordReader csrrLabels = new CollectionSequenceRecordReader(c2);
        DataSetIterator dsi = new org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator(csrr, csrrLabels, 2, 2);
        try {
            DataSet ds = dsi.next();
            TestCase.fail("Expected exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("to one-hot"));
        }
    }
}

