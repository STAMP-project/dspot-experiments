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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;


/**
 * Created by susaneraly on 6/8/17.
 */
public class EarlyTerminationMultiDataSetIteratorTest extends BaseDL4JTest {
    int minibatchSize = 5;

    int numExamples = 105;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testNextAndReset() throws Exception {
        int terminateAfter = 2;
        MultiDataSetIterator iter = new org.deeplearning4j.datasets.iterator.impl.MultiDataSetIteratorAdapter(new MnistDataSetIterator(minibatchSize, numExamples));
        int count = 0;
        List<MultiDataSet> seenMDS = new ArrayList<>();
        while (count < terminateAfter) {
            seenMDS.add(iter.next());
            count++;
        } 
        iter.reset();
        EarlyTerminationMultiDataSetIterator earlyEndIter = new EarlyTerminationMultiDataSetIterator(iter, terminateAfter);
        Assert.assertTrue(earlyEndIter.hasNext());
        count = 0;
        while (earlyEndIter.hasNext()) {
            MultiDataSet path = earlyEndIter.next();
            Assert.assertEquals(path.getFeatures()[0], seenMDS.get(count).getFeatures()[0]);
            Assert.assertEquals(path.getLabels()[0], seenMDS.get(count).getLabels()[0]);
            count++;
        } 
        Assert.assertEquals(count, terminateAfter);
        // check data is repeated
        earlyEndIter.reset();
        count = 0;
        while (earlyEndIter.hasNext()) {
            MultiDataSet path = earlyEndIter.next();
            Assert.assertEquals(path.getFeatures()[0], seenMDS.get(count).getFeatures()[0]);
            Assert.assertEquals(path.getLabels()[0], seenMDS.get(count).getLabels()[0]);
            count++;
        } 
    }

    @Test
    public void testNextNum() throws IOException {
        int terminateAfter = 1;
        MultiDataSetIterator iter = new org.deeplearning4j.datasets.iterator.impl.MultiDataSetIteratorAdapter(new MnistDataSetIterator(minibatchSize, numExamples));
        EarlyTerminationMultiDataSetIterator earlyEndIter = new EarlyTerminationMultiDataSetIterator(iter, terminateAfter);
        earlyEndIter.next(10);
        Assert.assertEquals(false, earlyEndIter.hasNext());
        earlyEndIter.reset();
        Assert.assertEquals(true, earlyEndIter.hasNext());
    }

    @Test
    public void testCallstoNextNotAllowed() throws IOException {
        int terminateAfter = 1;
        MultiDataSetIterator iter = new org.deeplearning4j.datasets.iterator.impl.MultiDataSetIteratorAdapter(new MnistDataSetIterator(minibatchSize, numExamples));
        EarlyTerminationMultiDataSetIterator earlyEndIter = new EarlyTerminationMultiDataSetIterator(iter, terminateAfter);
        earlyEndIter.next(10);
        iter.reset();
        exception.expect(RuntimeException.class);
        earlyEndIter.next(10);
    }
}

