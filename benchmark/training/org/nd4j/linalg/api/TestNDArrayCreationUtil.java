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
package org.nd4j.linalg.api;


import DataType.DOUBLE;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.linalg.util.ArrayUtil;


/**
 * Created by Alex on 30/04/2016.
 */
public class TestNDArrayCreationUtil extends BaseNd4jTest {
    public TestNDArrayCreationUtil(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testShapes() {
        // FIXME: int cast
        long[] shape2d = new long[]{ 2, 3 };
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAllTestMatricesWithShape(2, 3, 12345, DOUBLE)) {
            BaseNd4jTest.assertArrayEquals(p.getSecond(), shape2d, p.getFirst().shape());
        }
        // FIXME: int cast
        long[] shape3d = new long[]{ 2, 3, 4 };
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, shape3d, DOUBLE)) {
            BaseNd4jTest.assertArrayEquals(p.getSecond(), shape3d, p.getFirst().shape());
        }
        // FIXME: int cast
        long[] shape4d = new long[]{ 2, 3, 4, 5 };
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAll4dTestArraysWithShape(12345, ArrayUtil.toInts(shape4d), DOUBLE)) {
            BaseNd4jTest.assertArrayEquals(p.getSecond(), shape4d, p.getFirst().shape());
        }
        // FIXME: int cast
        long[] shape5d = new long[]{ 2, 3, 4, 5, 6 };
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAll5dTestArraysWithShape(12345, ArrayUtil.toInts(shape5d), DOUBLE)) {
            BaseNd4jTest.assertArrayEquals(p.getSecond(), shape5d, p.getFirst().shape());
        }
        // FIXME: int cast
        long[] shape6d = new long[]{ 2, 3, 4, 5, 6, 7 };
        for (Pair<INDArray, String> p : NDArrayCreationUtil.getAll6dTestArraysWithShape(12345, ArrayUtil.toInts(shape6d), DOUBLE)) {
            BaseNd4jTest.assertArrayEquals(p.getSecond(), shape6d, p.getFirst().shape());
        }
    }
}

