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


import RandomDataSetIterator.Values;
import RandomMultiDataSetIterator.Values.BINARY;
import RandomMultiDataSetIterator.Values.INTEGER_0_100;
import RandomMultiDataSetIterator.Values.ZEROS;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


public class RandomDataSetIteratorTest extends BaseDL4JTest {
    @Test
    public void testDSI() {
        DataSetIterator iter = new RandomDataSetIterator(5, new long[]{ 3, 4 }, new long[]{ 3, 5 }, Values.RANDOM_UNIFORM, Values.ONE_HOT);
        int count = 0;
        while (iter.hasNext()) {
            count++;
            DataSet ds = iter.next();
            Assert.assertArrayEquals(new long[]{ 3, 4 }, ds.getFeatures().shape());
            Assert.assertArrayEquals(new long[]{ 3, 5 }, ds.getLabels().shape());
            Assert.assertTrue((((ds.getFeatures().minNumber().doubleValue()) >= 0.0) && ((ds.getFeatures().maxNumber().doubleValue()) <= 1.0)));
            Assert.assertEquals(Nd4j.ones(3, 1), ds.getLabels().sum(1));
        } 
        Assert.assertEquals(5, count);
    }

    @Test
    public void testMDSI() {
        Nd4j.getRandom().setSeed(12345);
        MultiDataSetIterator iter = new RandomMultiDataSetIterator.Builder(5).addFeatures(new long[]{ 3, 4 }, INTEGER_0_100).addFeatures(new long[]{ 3, 5 }, BINARY).addLabels(new long[]{ 3, 6 }, ZEROS).build();
        int count = 0;
        while (iter.hasNext()) {
            count++;
            MultiDataSet mds = iter.next();
            Assert.assertEquals(2, mds.numFeatureArrays());
            Assert.assertEquals(1, mds.numLabelsArrays());
            Assert.assertArrayEquals(new long[]{ 3, 4 }, mds.getFeatures(0).shape());
            Assert.assertArrayEquals(new long[]{ 3, 5 }, mds.getFeatures(1).shape());
            Assert.assertArrayEquals(new long[]{ 3, 6 }, mds.getLabels(0).shape());
            Assert.assertTrue(((((mds.getFeatures(0).minNumber().doubleValue()) >= 0) && ((mds.getFeatures(0).maxNumber().doubleValue()) <= 100.0)) && ((mds.getFeatures(0).maxNumber().doubleValue()) > 2.0)));
            Assert.assertTrue((((mds.getFeatures(1).minNumber().doubleValue()) == 0.0) && ((mds.getFeatures(1).maxNumber().doubleValue()) == 1.0)));
            Assert.assertEquals(0.0, mds.getLabels(0).sumNumber().doubleValue(), 0.0);
        } 
        Assert.assertEquals(5, count);
    }
}

