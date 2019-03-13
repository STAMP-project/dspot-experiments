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
package org.deeplearning4j.datasets.iterator;


import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.util.TestDataSetConsumer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


public class MultipleEpochsIteratorTest extends BaseDL4JTest {
    @Test
    public void testNextAndReset() throws Exception {
        int epochs = 3;
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        DataSetIterator iter = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(rr, 150);
        MultipleEpochsIterator multiIter = new MultipleEpochsIterator(epochs, iter);
        Assert.assertTrue(multiIter.hasNext());
        while (multiIter.hasNext()) {
            DataSet path = multiIter.next();
            Assert.assertFalse((path == null));
        } 
        Assert.assertEquals(epochs, multiIter.epochs);
    }

    @Test
    public void testLoadFullDataSet() throws Exception {
        int epochs = 3;
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getTempFileFromArchive()));
        DataSetIterator iter = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(rr, 150);
        DataSet ds = iter.next(50);
        Assert.assertEquals(50, ds.getFeatures().size(0));
        MultipleEpochsIterator multiIter = new MultipleEpochsIterator(epochs, ds);
        Assert.assertTrue(multiIter.hasNext());
        int count = 0;
        while (multiIter.hasNext()) {
            DataSet path = multiIter.next();
            Assert.assertNotNull(path);
            Assert.assertEquals(50, path.numExamples(), 0);
            count++;
        } 
        Assert.assertEquals(epochs, count);
        Assert.assertEquals(epochs, multiIter.epochs);
    }

    @Test
    public void testLoadBatchDataSet() throws Exception {
        int epochs = 2;
        RecordReader rr = new CSVRecordReader();
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.txt").getFile()));
        DataSetIterator iter = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(rr, 150, 4, 3);
        DataSet ds = iter.next(20);
        Assert.assertEquals(20, ds.getFeatures().size(0));
        MultipleEpochsIterator multiIter = new MultipleEpochsIterator(epochs, ds);
        while (multiIter.hasNext()) {
            DataSet path = multiIter.next(10);
            Assert.assertNotNull(path);
            Assert.assertEquals(path.numExamples(), 10, 0.0);
        } 
        Assert.assertEquals(epochs, multiIter.epochs);
    }

    @Test
    public void testMEDIWithLoad1() throws Exception {
        ExistingDataSetIterator iter = new ExistingDataSetIterator(new MultipleEpochsIteratorTest.IterableWithoutException(100));
        MultipleEpochsIterator iterator = new MultipleEpochsIterator(10, iter, 24);
        TestDataSetConsumer consumer = new TestDataSetConsumer(iterator, 1);
        long num = consumer.consumeWhileHasNext(true);
        Assert.assertEquals((10 * 100), num);
    }

    @Test
    public void testMEDIWithLoad2() throws Exception {
        ExistingDataSetIterator iter = new ExistingDataSetIterator(new MultipleEpochsIteratorTest.IterableWithoutException(100));
        MultipleEpochsIterator iterator = new MultipleEpochsIterator(10, iter, 24);
        TestDataSetConsumer consumer = new TestDataSetConsumer(iterator, 2);
        long num1 = 0;
        for (; num1 < 150; num1++) {
            consumer.consumeOnce(iterator.next(), true);
        }
        iterator.reset();
        long num2 = consumer.consumeWhileHasNext(true);
        Assert.assertEquals(((10 * 100) + 150), (num1 + num2));
    }

    @Test
    public void testMEDIWithLoad3() throws Exception {
        ExistingDataSetIterator iter = new ExistingDataSetIterator(new MultipleEpochsIteratorTest.IterableWithoutException(10000));
        MultipleEpochsIterator iterator = new MultipleEpochsIterator(iter, 24, 136);
        TestDataSetConsumer consumer = new TestDataSetConsumer(iterator, 2);
        long num1 = 0;
        while (iterator.hasNext()) {
            consumer.consumeOnce(iterator.next(), true);
            num1++;
        } 
        Assert.assertEquals(136, num1);
    }

    private class IterableWithoutException implements Iterable<DataSet> {
        private final AtomicLong counter = new AtomicLong(0);

        private final int datasets;

        public IterableWithoutException(int datasets) {
            this.datasets = datasets;
        }

        @Override
        public Iterator<DataSet> iterator() {
            counter.set(0);
            return new Iterator<DataSet>() {
                @Override
                public boolean hasNext() {
                    return (counter.get()) < (datasets);
                }

                @Override
                public DataSet next() {
                    counter.incrementAndGet();
                    return new DataSet(Nd4j.create(100), Nd4j.create(10));
                }

                @Override
                public void remove() {
                }
            };
        }
    }
}

