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
package org.nd4j.linalg.util;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4jBackend;


public class TestArrayUtils extends BaseNd4jTest {
    public TestArrayUtils(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testFlattenDoubleArray() {
        BaseNd4jTest.assertArrayEquals(new double[0], ArrayUtil.flattenDoubleArray(new double[0]), 0.0);
        Random r = new Random(12345L);
        double[] d1 = new double[10];
        for (int i = 0; i < (d1.length); i++)
            d1[i] = r.nextDouble();

        BaseNd4jTest.assertArrayEquals(d1, ArrayUtil.flattenDoubleArray(d1), 0.0);
        double[][] d2 = new double[5][10];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 10; j++)
                d2[i][j] = r.nextDouble();


        BaseNd4jTest.assertArrayEquals(ArrayUtil.flatten(d2), ArrayUtil.flattenDoubleArray(d2), 0.0);
        double[][][] d3 = new double[5][10][15];
        double[] exp3 = new double[(5 * 10) * 15];
        int c = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 15; k++) {
                    double d = r.nextDouble();
                    exp3[(c++)] = d;
                    d3[i][j][k] = d;
                }
            }
        }
        BaseNd4jTest.assertArrayEquals(exp3, ArrayUtil.flattenDoubleArray(d3), 0.0);
        double[][][][] d4 = new double[3][5][7][9];
        double[] exp4 = new double[((3 * 5) * 7) * 9];
        c = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 7; k++) {
                    for (int l = 0; l < 9; l++) {
                        double d = r.nextDouble();
                        exp4[(c++)] = d;
                        d4[i][j][k][l] = d;
                    }
                }
            }
        }
        BaseNd4jTest.assertArrayEquals(exp4, ArrayUtil.flattenDoubleArray(d4), 0.0);
    }

    @Test
    public void testFlattenFloatArray() {
        BaseNd4jTest.assertArrayEquals(new float[0], ArrayUtil.flattenFloatArray(new float[0]), 0.0F);
        Random r = new Random(12345L);
        float[] f1 = new float[10];
        for (int i = 0; i < (f1.length); i++)
            f1[i] = r.nextFloat();

        BaseNd4jTest.assertArrayEquals(f1, ArrayUtil.flattenFloatArray(f1), 0.0F);
        float[][] f2 = new float[5][10];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 10; j++)
                f2[i][j] = r.nextFloat();


        BaseNd4jTest.assertArrayEquals(ArrayUtil.flatten(f2), ArrayUtil.flattenFloatArray(f2), 0.0F);
        float[][][] f3 = new float[5][10][15];
        float[] exp3 = new float[(5 * 10) * 15];
        int c = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 15; k++) {
                    float d = r.nextFloat();
                    exp3[(c++)] = d;
                    f3[i][j][k] = d;
                }
            }
        }
        BaseNd4jTest.assertArrayEquals(exp3, ArrayUtil.flattenFloatArray(f3), 0.0F);
        float[][][][] f4 = new float[3][5][7][9];
        float[] exp4 = new float[((3 * 5) * 7) * 9];
        c = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 7; k++) {
                    for (int l = 0; l < 9; l++) {
                        float d = r.nextFloat();
                        exp4[(c++)] = d;
                        f4[i][j][k][l] = d;
                    }
                }
            }
        }
        BaseNd4jTest.assertArrayEquals(exp4, ArrayUtil.flattenFloatArray(f4), 0.0F);
    }

    @Test
    public void testArrayShape() {
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new int[0]), new int[]{ 0 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new int[5][7][9]), new int[]{ 5, 7, 9 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new Object[2][3][4][5][6]), new int[]{ 2, 3, 4, 5, 6 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new double[9][7][5][3]), new int[]{ 9, 7, 5, 3 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new double[1][1][1][0]), new int[]{ 1, 1, 1, 0 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new char[3][2][1]), new int[]{ 3, 2, 1 });
        BaseNd4jTest.assertArrayEquals(ArrayUtil.arrayShape(new String[3][2][1]), new int[]{ 3, 2, 1 });
    }

    @Test
    public void testArgMinOfMaxMethods() {
        int[] first = new int[]{ 1, 5, 2, 4 };
        int[] second = new int[]{ 4, 6, 3, 2 };
        Assert.assertEquals(2, ArrayUtil.argMinOfMax(first, second));
        int[] third = new int[]{ 7, 3, 8, 10 };
        Assert.assertEquals(1, ArrayUtil.argMinOfMax(first, second, third));
    }

    @Test
    public void testAssertNotRagged() {
        // Rank 1 - should be fine
        ArrayUtil.assertNotRagged(new Object[0]);
        ArrayUtil.assertNotRagged(new Object[10]);
        // Rank 2
        ArrayUtil.assertNotRagged(new Object[3][4]);
        ArrayUtil.assertNotRagged(new Object[2][1]);
        ArrayUtil.assertNotRagged(new double[3][4]);
        Object[] ragged = new Object[3][4];
        ragged[2] = new Object[10];
        TestArrayUtils.shouldBeRagged(ragged);
        double[][] ragged2 = new double[2][3];
        ragged2[0] = new double[2];
        TestArrayUtils.shouldBeRagged(ragged2);
        // Rank 3
        ArrayUtil.assertNotRagged(new Object[1][0][2]);
        ArrayUtil.assertNotRagged(new Object[2][3][4]);
        ArrayUtil.assertNotRagged(new double[2][3][4]);
        Object[][][] ragged3 = new Object[2][3][4];
        ragged3[1][2] = new Object[7];
        TestArrayUtils.shouldBeRagged(ragged3);
        double[][][] ragged4 = new double[2][3][4];
        ragged4[0][1] = new double[1];
        TestArrayUtils.shouldBeRagged(ragged4);
        // Rank 4:
        ArrayUtil.assertNotRagged(new Object[2][3][4][5]);
        ArrayUtil.assertNotRagged(new double[2][3][4][5]);
        Object[][][][] ragged5 = new Object[2][3][4][5];
        ragged5[1][2][1] = new Object[3][5];
        TestArrayUtils.shouldBeRagged(ragged5);
    }
}

