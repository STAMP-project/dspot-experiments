/**
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tencent.angel.ml.math2.vector;


import org.junit.Test;


public class ReduceOPTest {
    private static int matrixId;

    private static int rowId;

    private static int clock;

    private static int capacity;

    private static int dim;

    private static int[] intrandIndices;

    private static long[] longrandIndices;

    private static int[] intsortedIndices;

    private static long[] longsortedIndices;

    private static int[] intValues;

    private static long[] longValues;

    private static float[] floatValues;

    private static double[] doubleValues;

    @Test
    public void testall() {
        reduceIntDoubleVector();
        reduceLongDoubleVector();
        reduceIntFloatVector();
        reduceLongFloatVector();
        reduceIntLongVector();
        reduceLongLongVector();
        reduceIntIntVector();
        reduceLongIntVector();
        System.out.println("\n\n\n\n");
        stats(ReduceOPTest.intValues);
        stats(ReduceOPTest.longValues);
        stats(ReduceOPTest.floatValues);
        stats(ReduceOPTest.doubleValues);
    }
}

