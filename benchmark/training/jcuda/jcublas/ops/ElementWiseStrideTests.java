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
package jcuda.jcublas.ops;


import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.Pair;


/**
 * Created by raver on 08.05.2016.
 */
@Ignore
public class ElementWiseStrideTests {
    @Test
    public void testEWS1() throws Exception {
        List<Pair<INDArray, String>> list = NDArrayCreationUtil.getAllTestMatricesWithShape(4, 5, 12345);
        list.addAll(NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, 4, 5, 6));
        list.addAll(NDArrayCreationUtil.getAll4dTestArraysWithShape(12345, 4, 5, 6, 7));
        list.addAll(NDArrayCreationUtil.getAll5dTestArraysWithShape(12345, 4, 5, 6, 7, 8));
        list.addAll(NDArrayCreationUtil.getAll6dTestArraysWithShape(12345, 4, 5, 6, 7, 8, 9));
        for (Pair<INDArray, String> p : list) {
            int ewsBefore = Shape.elementWiseStride(p.getFirst().shapeInfo());
            INDArray reshapeAttempt = Shape.newShapeNoCopy(p.getFirst(), new int[]{ 1, p.getFirst().length() }, ((Nd4j.order()) == 'f'));
            if (((reshapeAttempt != null) && (ewsBefore == (-1))) && ((reshapeAttempt.elementWiseStride()) != (-1))) {
                System.out.println(("NDArrayCreationUtil." + (p.getSecond())));
                System.out.println(("ews before: " + ewsBefore));
                System.out.println(p.getFirst().shapeInfoToString());
                System.out.println(("ews returned by elementWiseStride(): " + (p.getFirst().elementWiseStride())));
                System.out.println(("ews returned by reshape(): " + (reshapeAttempt.elementWiseStride())));
                System.out.println();
                // assertTrue(false);
            } else {
                // System.out.println("FAILED: " + p.getFirst().shapeInfoToString());
            }
        }
    }

    @Test
    public void testDualVStack() throws Exception {
        INDArray[] arrs = new INDArray[50];
        INDArray[] arrs2 = new INDArray[50];
        for (int i = 0; i < (arrs.length); i++) {
            arrs[i] = Nd4j.create(new float[]{ 1.0F, 2.0F }).dup('c');
            arrs2[i] = Nd4j.create(new int[]{ 1, 2 }, 'c');
        }
        INDArray result = Nd4j.vstack(arrs);
        System.out.println(("Result: " + result));
        // Nd4j.vstack(arrs2);
    }

    @Test
    public void testBVStack() throws Exception {
        INDArray[] arr = new INDArray[5];
        for (int i = 0; i < (arr.length); i++) {
            arr[i] = Nd4j.create(new int[]{ 1, 5749 }, 'c');
        }
        Nd4j.vstack(arr);
        Nd4j.create(1);
    }

    @Test
    public void test2() {
        INDArray[] first = new INDArray[10];
        INDArray[] second = new INDArray[10];
        for (int i = 0; i < 10; i++) {
            first[i] = Nd4j.create(new int[]{ 1, 784 }, 'c');
            second[i] = Nd4j.create(new int[]{ 1, 5749 }, 'c');
        }
        Nd4j.vstack(first);
        Nd4j.vstack(second);
        Nd4j.create(1);
    }

    @Test
    public void testVstackWithMatrices() {
        INDArray[] arr = new INDArray[3];
        arr[0] = Nd4j.linspace(0, 49, 50).reshape('c', 5, 10);
        arr[1] = Nd4j.linspace(50, 59, 10);
        arr[2] = Nd4j.linspace(60, 99, 40).reshape('c', 4, 10);
        INDArray expected = Nd4j.linspace(0, 99, 100).reshape('c', 10, 10);
        INDArray actual = Nd4j.vstack(arr);
        System.out.println(expected);
        System.out.println();
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHstackConcatCols() {
        int rows = 10;
        INDArray[] arr = new INDArray[5];
        for (int i = 0; i < (arr.length); i++) {
            arr[i] = Nd4j.linspace((i * rows), (((i + 1) * rows) - 1), rows).transpose();
        }
        INDArray expected = Nd4j.linspace(0, (((arr.length) * rows) - 1), ((arr.length) * rows)).reshape('f', rows, arr.length);
        INDArray actual = Nd4j.hstack(arr);
        System.out.println(expected);
        System.out.println();
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHstackConcatSimple() {
        int rows = 10;
        INDArray[] arr = new INDArray[5];
        for (int i = 0; i < (arr.length); i++) {
            arr[i] = Nd4j.linspace((i * rows), (((i + 1) * rows) - 1), rows);
        }
        INDArray expected = Nd4j.linspace(0, (((arr.length) * rows) - 1), ((arr.length) * rows));
        INDArray actual = Nd4j.hstack(arr);
        System.out.println(expected);
        System.out.println();
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testVstackConcatRows() {
        int cols = 10;
        INDArray[] arr = new INDArray[5];
        for (int i = 0; i < (arr.length); i++) {
            arr[i] = Nd4j.linspace((i * cols), (((i + 1) * cols) - 1), cols);
        }
        INDArray expected = Nd4j.linspace(0, (((arr.length) * cols) - 1), ((arr.length) * cols)).reshape('c', arr.length, cols);
        INDArray actual = Nd4j.vstack(arr);
        System.out.println(expected);
        System.out.println();
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }
}

