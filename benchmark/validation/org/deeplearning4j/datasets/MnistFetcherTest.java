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
package org.deeplearning4j.datasets;


import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.base.MnistFetcher;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Conditions;


/**
 *
 *
 * @author Justin Long (crockpotveggies)
 */
public class MnistFetcherTest extends BaseDL4JTest {
    @ClassRule
    public static TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testMnist() throws Exception {
        DataSetIterator iter = new MnistDataSetIterator(32, 60000, false, true, false, (-1));
        int count = 0;
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            INDArray arr = ds.getFeatures().sum(1);
            int countMatch = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(arr, Conditions.equals(0))).z().getInt(0);
            Assert.assertEquals(0, countMatch);
            count++;
        } 
        Assert.assertEquals((60000 / 32), count);
        count = 0;
        iter = new MnistDataSetIterator(32, false, 12345);
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            INDArray arr = ds.getFeatures().sum(1);
            int countMatch = Nd4j.getExecutioner().execAndReturn(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(arr, Conditions.equals(0))).z().getInt(0);
            Assert.assertEquals(0, countMatch);
            count++;
        } 
        Assert.assertEquals(((int) (Math.ceil((10000 / 32.0)))), count);
    }

    @Test
    public void testMnistDataFetcher() throws Exception {
        MnistFetcher mnistFetcher = new MnistFetcher();
        File mnistDir = mnistFetcher.downloadAndUntar();
        Assert.assertTrue(mnistDir.isDirectory());
    }

    @Test
    public void testSubsetRepeatability() throws Exception {
        DataSetIterator it = new MnistDataSetIterator(1, 1, false, false, true, 0);
        DataSet d1 = it.next();
        for (int i = 0; i < 10; i++) {
            it.reset();
            DataSet d2 = it.next();
            Assert.assertEquals(d1.get(0).getFeatures(), d2.get(0).getFeatures());
        }
        // Check larger number:
        it = new MnistDataSetIterator(8, 32, false, false, true, 12345);
        Set<String> featureLabelSet = new HashSet<>();
        while (it.hasNext()) {
            DataSet ds = it.next();
            INDArray f = ds.getFeatures();
            INDArray l = ds.getLabels();
            for (int i = 0; i < (f.size(0)); i++) {
                featureLabelSet.add((((f.getRow(i).toString()) + "\t") + (l.getRow(i).toString())));
            }
        } 
        Assert.assertEquals(32, featureLabelSet.size());
        for (int i = 0; i < 3; i++) {
            it.reset();
            Set<String> flSet2 = new HashSet<>();
            while (it.hasNext()) {
                DataSet ds = it.next();
                INDArray f = ds.getFeatures();
                INDArray l = ds.getLabels();
                for (int j = 0; j < (f.size(0)); j++) {
                    flSet2.add((((f.getRow(j).toString()) + "\t") + (l.getRow(j).toString())));
                }
            } 
            Assert.assertEquals(featureLabelSet, flSet2);
        }
    }
}

