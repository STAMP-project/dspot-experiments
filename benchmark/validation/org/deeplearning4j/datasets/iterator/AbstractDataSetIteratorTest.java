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


import java.util.concurrent.atomic.AtomicInteger;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;


/**
 * Created by raver on 16.06.2016.
 */
public class AbstractDataSetIteratorTest extends BaseDL4JTest {
    @Test
    public void next() throws Exception {
        int numFeatures = 128;
        int batchSize = 10;
        int numRows = 1000;
        AtomicInteger cnt = new AtomicInteger(0);
        FloatsDataSetIterator iterator = new FloatsDataSetIterator(AbstractDataSetIteratorTest.floatIterable(numRows, numFeatures), batchSize);
        Assert.assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            DataSet dataSet = iterator.next();
            INDArray features = dataSet.getFeatures();
            Assert.assertEquals(batchSize, features.rows());
            Assert.assertEquals(numFeatures, features.columns());
            cnt.incrementAndGet();
        } 
        Assert.assertEquals((numRows / batchSize), cnt.get());
    }
}

