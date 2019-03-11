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
package org.deeplearning4j.spark.util;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.spark.BaseSparkTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by agibsonccc on 1/23/15.
 */
public class MLLIbUtilTest extends BaseSparkTest {
    private static final Logger log = LoggerFactory.getLogger(MLLIbUtilTest.class);

    @Test
    public void testMlLibTest() {
        DataSet dataSet = new IrisDataSetIterator(150, 150).next();
        List<DataSet> list = dataSet.asList();
        JavaRDD<DataSet> data = sc.parallelize(list);
        JavaRDD<LabeledPoint> mllLibData = MLLibUtil.fromDataSet(sc, data);
    }

    @Test
    public void testINDtoMLMatrix() {
        INDArray matIND = Nd4j.rand(23, 100);
        Matrix matMl = MLLibUtil.toMatrix(matIND);
        Assert.assertTrue(matrixEquals(matMl, matIND, 0.01));
    }

    @Test
    public void testMltoINDMatrix() {
        Matrix matMl = Matrices.randn(23, 100, new Random(3949955));
        INDArray matIND = MLLibUtil.toMatrix(matMl);
        MLLIbUtilTest.log.info("matrix shape: {}", Arrays.toString(matIND.shapeInfoDataBuffer().asInt()));
        Assert.assertTrue(matrixEquals(matMl, matIND, 0.01));
    }
}

