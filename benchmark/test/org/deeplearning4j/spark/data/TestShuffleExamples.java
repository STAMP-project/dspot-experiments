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
package org.deeplearning4j.spark.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.util.SparkUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 06/01/2017.
 */
public class TestShuffleExamples extends BaseSparkTest {
    @Test
    public void testShuffle() {
        List<DataSet> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            INDArray f = Nd4j.valueArrayOf(new int[]{ 10, 1 }, i);
            INDArray l = f.dup();
            DataSet ds = new DataSet(f, l);
            list.add(ds);
        }
        JavaRDD<DataSet> rdd = sc.parallelize(list);
        JavaRDD<DataSet> shuffled = SparkUtils.shuffleExamples(rdd, 10, 10);
        List<DataSet> shuffledList = shuffled.collect();
        int totalExampleCount = 0;
        for (DataSet ds : shuffledList) {
            totalExampleCount += ds.getFeatures().length();
            System.out.println(Arrays.toString(ds.getFeatures().data().asFloat()));
            Assert.assertEquals(ds.getFeatures(), ds.getLabels());
        }
        Assert.assertEquals(100, totalExampleCount);
    }
}

