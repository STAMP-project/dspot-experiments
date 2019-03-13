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


import java.util.List;
import junit.framework.TestCase;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author Alex Black
 */
@Ignore
public class TestAsyncIterator extends BaseDL4JTest {
    @Test
    public void testBasic() {
        // Basic test. Make sure it returns the right number of elements,
        // hasNext() works, etc
        int size = 13;
        DataSetIterator baseIter = new TestAsyncIterator.TestIterator(size, 0);
        // async iterator with queue size of 1
        DataSetIterator async = new AsyncDataSetIterator(baseIter, 1);
        for (int i = 0; i < size; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.reset();
        TestCase.assertTrue(async.hasNext());
        shutdown();
        // async iterator with queue size of 5
        baseIter = new TestAsyncIterator.TestIterator(size, 5);
        async = new AsyncDataSetIterator(baseIter, 5);
        for (int i = 0; i < size; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.reset();
        TestCase.assertTrue(async.hasNext());
        shutdown();
        // async iterator with queue size of 100
        baseIter = new TestAsyncIterator.TestIterator(size, 100);
        async = new AsyncDataSetIterator(baseIter, 100);
        for (int i = 0; i < size; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            while (ds == null)
                ds = async.next();

            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.reset();
        TestCase.assertTrue(async.hasNext());
        shutdown();
        // Test iteration where performance is limited by baseIterator.next() speed
        baseIter = new TestAsyncIterator.TestIterator(size, 1000);
        async = new AsyncDataSetIterator(baseIter, 5);
        for (int i = 0; i < size; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.reset();
        TestCase.assertTrue(async.hasNext());
        shutdown();
    }

    @Test
    public void testInitializeNoNextIter() {
        DataSetIterator iter = new IrisDataSetIterator(10, 150);
        while (iter.hasNext())
            iter.next();

        DataSetIterator async = new AsyncDataSetIterator(iter, 2);
        Assert.assertFalse(iter.hasNext());
        Assert.assertFalse(async.hasNext());
        try {
            iter.next();
            Assert.fail("Should have thrown NoSuchElementException");
        } catch (Exception e) {
            // OK
        }
        async.reset();
        int count = 0;
        while (async.hasNext()) {
            async.next();
            count++;
        } 
        Assert.assertEquals((150 / 10), count);
    }

    @Test
    public void testResetWhileBlocking() {
        int size = 6;
        // Test reset while blocking on baseIterator.next()
        DataSetIterator baseIter = new TestAsyncIterator.TestIterator(size, 1000);
        AsyncDataSetIterator async = new AsyncDataSetIterator(baseIter);
        async.next();
        // Should be waiting on baseIter.next()
        async.reset();
        for (int i = 0; i < 6; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.shutdown();
        // Test reset while blocking on blockingQueue.put()
        baseIter = new TestAsyncIterator.TestIterator(size, 0);
        async = new AsyncDataSetIterator(baseIter);
        async.next();
        async.next();
        // Should be waiting on blocingQueue
        async.reset();
        for (int i = 0; i < 6; i++) {
            TestCase.assertTrue(async.hasNext());
            DataSet ds = async.next();
            Assert.assertEquals(ds.getFeatures().getDouble(0), i, 0.0);
            Assert.assertEquals(ds.getLabels().getDouble(0), i, 0.0);
        }
        Assert.assertFalse(async.hasNext());
        async.shutdown();
    }

    private static class TestIterator implements DataSetIterator {
        private int size;

        private int cursor;

        private long delayMSOnNext;

        private TestIterator(int size, long delayMSOnNext) {
            this.size = size;
            this.cursor = 0;
            this.delayMSOnNext = delayMSOnNext;
        }

        @Override
        public DataSet next(int num) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int inputColumns() {
            return 1;
        }

        @Override
        public int totalOutcomes() {
            return 1;
        }

        @Override
        public boolean resetSupported() {
            return true;
        }

        @Override
        public boolean asyncSupported() {
            return false;
        }

        @Override
        public void reset() {
            cursor = 0;
        }

        @Override
        public int batch() {
            return 1;
        }

        @Override
        public void setPreProcessor(DataSetPreProcessor preProcessor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataSetPreProcessor getPreProcessor() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getLabels() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return (cursor) < (size);
        }

        @Override
        public DataSet next() {
            if ((delayMSOnNext) > 0) {
                try {
                    Thread.sleep(delayMSOnNext);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            INDArray features = Nd4j.scalar(cursor);
            INDArray labels = Nd4j.scalar(cursor);
            (cursor)++;
            return new DataSet(features, labels);
        }

        @Override
        public void remove() {
        }
    }
}

