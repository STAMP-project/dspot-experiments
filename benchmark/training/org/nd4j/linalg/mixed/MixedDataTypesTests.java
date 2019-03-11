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
package org.nd4j.linalg.mixed;


import AllocationPolicy.OVERALLOCATE;
import DataType.BOOL;
import DataType.DOUBLE;
import DataType.FLOAT;
import DataType.HALF;
import DataType.INT;
import DataType.LONG;
import DataType.SHORT;
import LearningPolicy.FIRST_LOOP;
import MirroringPolicy.FULL;
import SpillPolicy.EXTERNAL;
import com.google.flatbuffers.FlatBufferBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.graph.FlatArray;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.options.ArrayOptionsHelper;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.memory.abstracts.Nd4jWorkspace;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.nativeblas.NativeOpsHolder;


/**
 * Basic tests for mixed data types
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class MixedDataTypesTests {
    @Test
    public void testBasicCreation_1() {
        val array = Nd4j.create(LONG, 3, 3);
        Assert.assertNotNull(array);
        Assert.assertEquals(9, array.length());
        Assert.assertEquals(LONG, array.dataType());
        Assert.assertEquals(LONG, ArrayOptionsHelper.dataType(array.shapeInfoJava()));
    }

    @Test
    public void testBasicCreation_2() {
        val array = Nd4j.create(SHORT, 3, 3);
        Assert.assertNotNull(array);
        Assert.assertEquals(9, array.length());
        Assert.assertEquals(SHORT, array.dataType());
        Assert.assertEquals(SHORT, ArrayOptionsHelper.dataType(array.shapeInfoJava()));
    }

    @Test
    public void testBasicCreation_3() {
        val array = Nd4j.create(HALF, 3, 3);
        Assert.assertNotNull(array);
        Assert.assertEquals(9, array.length());
        Assert.assertEquals(HALF, array.dataType());
        Assert.assertEquals(HALF, ArrayOptionsHelper.dataType(array.shapeInfoJava()));
    }

    @Test
    public void testBasicCreation_4() {
        val scalar = Nd4j.scalar(DOUBLE, 1.0);
        Assert.assertNotNull(scalar);
        Assert.assertEquals(0, scalar.rank());
        Assert.assertEquals(1, scalar.length());
        Assert.assertEquals(DOUBLE, scalar.dataType());
        Assert.assertEquals(1.0, scalar.getDouble(0), 1.0E-5);
    }

    @Test
    public void testBasicCreation_5() {
        val scalar = Nd4j.trueScalar(new Integer(1));
        Assert.assertNotNull(scalar);
        Assert.assertEquals(0, scalar.rank());
        Assert.assertEquals(1, scalar.length());
        Assert.assertEquals(INT, scalar.dataType());
        Assert.assertEquals(1.0, scalar.getInt(0), 1.0E-5);
    }

    @Test
    public void testBasicCreation_6() {
        val scalar = Nd4j.trueScalar(1);
        Assert.assertNotNull(scalar);
        Assert.assertEquals(0, scalar.rank());
        Assert.assertEquals(1, scalar.length());
        Assert.assertEquals(INT, scalar.dataType());
        Assert.assertEquals(1.0, scalar.getInt(0), 1.0E-5);
    }

    @Test
    public void testBasicCreation_7() {
        val scalar = Nd4j.trueScalar(1L);
        Assert.assertNotNull(scalar);
        Assert.assertEquals(0, scalar.rank());
        Assert.assertEquals(1, scalar.length());
        Assert.assertEquals(LONG, scalar.dataType());
        Assert.assertEquals(1, scalar.getInt(0));
    }

    @Test
    public void testBasicOps_1() {
        val exp = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        val array = Nd4j.create(INT, 3, 3);
        Assert.assertEquals(INT, array.dataType());
        array.assign(1);
        val vector = array.data().asInt();
        Assert.assertArrayEquals(exp, vector);
    }

    @Test
    public void testBasicOps_2() {
        val exp = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        val arrayX = Nd4j.create(INT, 3, 3);
        val arrayY = Nd4j.create(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 3, 3 }, INT);
        arrayX.addi(arrayY);
        val vector = arrayX.data().asInt();
        Assert.assertArrayEquals(exp, vector);
    }

    @Test
    public void testBasicOps_3() {
        if (!(NativeOpsHolder.getInstance().getDeviceNativeOps().isExperimentalEnabled()))
            return;

        val exp = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        val arrayX = Nd4j.create(INT, 3, 3);
        val arrayY = Nd4j.create(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 }, new long[]{ 3, 3 }, LONG);
        val vectorY = arrayY.data().asInt();
        Assert.assertArrayEquals(exp, vectorY);
        arrayX.addi(arrayY);
        val vectorX = arrayX.data().asInt();
        Assert.assertArrayEquals(exp, vectorX);
    }

    @Test
    public void testBasicOps_4() {
        val arrayX = Nd4j.create(new int[]{ 7, 8, 7, 9, 1, 1, 1, 1, 1 }, new long[]{ 3, 3 }, LONG);
        val result = arrayX.maxNumber();
        val l = result.longValue();
        Assert.assertEquals(9L, l);
    }

    @Test
    public void testBasicOps_5() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val result = arrayX.meanNumber().floatValue();
        Assert.assertEquals(2.5F, result, 1.0E-5);
    }

    @Test
    public void testBasicOps_6() {
        val arrayX = Nd4j.create(new int[]{ 1, 0, 0, 4 }, new long[]{ 4 }, INT);
        val z = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.CountNonZero(arrayX));
        Assert.assertEquals(LONG, z.dataType());
        val result = z.getInt(0);
        Assert.assertEquals(2, result);
    }

    @Test
    public void testBasicOps_7() {
        val arrayX = Nd4j.create(new float[]{ 1, 0, Float.NaN, 4 }, new long[]{ 4 }, FLOAT);
        val z = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.bool.IsInf(arrayX));
        Assert.assertEquals(BOOL, z.dataType());
        val result = z.getInt(0);
        val z2 = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.bool.IsNaN(arrayX));
        Assert.assertEquals(BOOL, z2.dataType());
        val result2 = z2.getInt(0);
        Assert.assertEquals(1, result2);
    }

    @Test
    public void testBasicOps_8() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new int[]{ 1, 0, 0, 4 }, new long[]{ 4 }, INT);
        val exp = new long[]{ 1, 0, 0, 1 };
        val result = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.comparison.OldEqualTo(arrayX, arrayY));
        Assert.assertEquals(BOOL, result.dataType());
        val arr = result.data().asLong();
        Assert.assertArrayEquals(exp, arr);
    }

    @Test
    public void testBasicOps_9() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val exp = new long[]{ 1, 0, 0, 1 };
        val op = new org.nd4j.linalg.api.ops.impl.reduce3.CosineSimilarity(arrayX, arrayY);
        val result = Nd4j.getExecutioner().exec(op);
        val arr = result.getDouble(0);
        Assert.assertEquals(1.0, arr, 1.0E-5);
    }

    @Test
    public void testNewAssign_1() {
        val arrayX = Nd4j.create(FLOAT, 5);
        val arrayY = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        val exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        arrayX.assign(arrayY);
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testNewAssign_2() {
        val arrayX = Nd4j.create(INT, 5);
        val arrayY = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        val exp = Nd4j.create(new int[]{ 1, 2, 3, 4, 5 }, new long[]{ 5 }, INT);
        arrayX.assign(arrayY);
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testMethods_1() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val exp = Nd4j.create(new int[]{ 2, 4, 6, 8 }, new long[]{ 4 }, INT);
        val arrayZ = arrayX.add(arrayY);
        Assert.assertEquals(INT, arrayZ.dataType());
        Assert.assertEquals(exp, arrayZ);
    }

    @Test
    public void testMethods_2() {
        if (!(NativeOpsHolder.getInstance().getDeviceNativeOps().isExperimentalEnabled()))
            return;

        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new double[]{ 1, 2, 3, 4 }, new long[]{ 4 }, DOUBLE);
        val exp = Nd4j.create(new double[]{ 2, 4, 6, 8 }, new long[]{ 4 }, DOUBLE);
        val arrayZ = arrayX.add(arrayY);
        Assert.assertEquals(DOUBLE, arrayZ.dataType());
        Assert.assertEquals(exp, arrayZ);
    }

    @Test
    public void testMethods_3() {
        if (!(NativeOpsHolder.getInstance().getDeviceNativeOps().isExperimentalEnabled()))
            return;

        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new double[]{ 0.5, 0.5, 0.5, 0.5 }, new long[]{ 4 }, DOUBLE);
        val exp = Nd4j.create(new double[]{ 1.5, 2.5, 3.5, 4.5 }, new long[]{ 4 }, DOUBLE);
        val arrayZ = arrayX.add(arrayY);
        Assert.assertEquals(DOUBLE, arrayZ.dataType());
        Assert.assertEquals(exp, arrayZ);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypesValidation_1() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, LONG);
        val arrayY = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val exp = new long[]{ 1, 0, 0, 1 };
        val op = new org.nd4j.linalg.api.ops.impl.reduce3.CosineSimilarity(arrayX, arrayY);
        val result = Nd4j.getExecutioner().exec(op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypesValidation_2() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val arrayY = Nd4j.create(new int[]{ 1, 0, 0, 4 }, new long[]{ 4 }, LONG);
        val exp = new long[]{ 1, 0, 0, 1 };
        val result = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.comparison.OldEqualTo(arrayX, arrayY));
        val arr = result.data().asLong();
        Assert.assertArrayEquals(exp, arr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypesValidation_3() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val result = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.strict.OldSoftMax(arrayX));
    }

    @Test
    public void testFlatSerde_1() {
        val arrayX = Nd4j.create(new int[]{ 1, 2, 3, 4 }, new long[]{ 4 }, INT);
        val builder = new FlatBufferBuilder(512);
        val flat = arrayX.toFlatArray(builder);
        builder.finish(flat);
        val db = builder.dataBuffer();
        val flatb = FlatArray.getRootAsFlatArray(db);
        val restored = Nd4j.createFromFlatArray(flatb);
        Assert.assertEquals(arrayX, restored);
    }

    @Test
    public void testFlatSerde_2() {
        val arrayX = Nd4j.create(new long[]{ 1, 2, 3, 4 }, new long[]{ 4 }, LONG);
        val builder = new FlatBufferBuilder(512);
        val flat = arrayX.toFlatArray(builder);
        builder.finish(flat);
        val db = builder.dataBuffer();
        val flatb = FlatArray.getRootAsFlatArray(db);
        val restored = Nd4j.createFromFlatArray(flatb);
        Assert.assertEquals(arrayX, restored);
    }

    @Test
    public void testFlatSerde_3() {
        val arrayX = Nd4j.create(new boolean[]{ true, false, true, true }, new long[]{ 4 }, BOOL);
        val builder = new FlatBufferBuilder(512);
        val flat = arrayX.toFlatArray(builder);
        builder.finish(flat);
        val db = builder.dataBuffer();
        val flatb = FlatArray.getRootAsFlatArray(db);
        val restored = Nd4j.createFromFlatArray(flatb);
        Assert.assertEquals(arrayX, restored);
    }

    @Test
    public void testBoolFloatCast2() {
        val first = Nd4j.zeros(FLOAT, 3, 5000);
        INDArray asBool = first.castTo(BOOL);
        INDArray not = Transforms.not(asBool);// 

        INDArray asFloat = not.castTo(FLOAT);
        System.out.println(not);
        System.out.println(asFloat);
        INDArray exp = Nd4j.ones(FLOAT, 3, 5000);
        Assert.assertEquals(FLOAT, exp.dataType());
        Assert.assertEquals(exp.dataType(), asFloat.dataType());
        val arrX = asFloat.data().asFloat();
        val arrE = exp.data().asFloat();
        Assert.assertArrayEquals(arrE, arrX, 1.0E-5F);
        Assert.assertEquals(exp, asFloat);
    }

    @Test
    public void testReduce3Large() {
        val arrayX = Nd4j.create(FLOAT, 10, 5000);
        val arrayY = Nd4j.create(FLOAT, 10, 5000);
        Assert.assertTrue(arrayX.equalsWithEps(arrayY, (-1.0E-5F)));
    }

    @Test
    public void testAssignScalarSimple() {
        for (DataType dt : new DataType[]{ DataType.DOUBLE, DataType.FLOAT, DataType.HALF }) {
            INDArray arr = Nd4j.scalar(dt, 10.0);
            arr.assign(2.0);
            System.out.println(((((dt + " - value: ") + arr) + " - ") + (arr.getDouble(0))));
        }
    }

    @Test
    public void testSimple() {
        Nd4j.create(1);
        for (DataType dt : new DataType[]{ DataType.DOUBLE, DataType.FLOAT, DataType.HALF, DataType.INT, DataType.LONG }) {
            System.out.println((("----- " + dt) + " -----"));
            INDArray arr = Nd4j.ones(dt, 1, 5);
            System.out.println(("Ones: " + arr));
            arr.assign(1.0);
            System.out.println(("assign(1.0): " + arr));
            System.out.println(("DIV: " + (arr.div(8))));
            System.out.println(("MUL: " + (arr.mul(8))));
            System.out.println(("SUB: " + (arr.sub(8))));
            System.out.println(("ADD: " + (arr.add(8))));
            System.out.println(("RDIV: " + (arr.rdiv(8))));
            System.out.println(("RSUB: " + (arr.rsub(8))));
        }
    }

    @Test
    public void testWorkspaceBool() {
        val conf = WorkspaceConfiguration.builder().minSize(((10 * 1024) * 1024)).overallocationLimit(1.0).policyAllocation(OVERALLOCATE).policyLearning(FIRST_LOOP).policyMirroring(FULL).policySpill(EXTERNAL).build();
        val ws = Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(conf, "WS");
        for (int i = 0; i < 10; i++) {
            try (val workspace = ((Nd4jWorkspace) (ws.notifyScopeEntered()))) {
                val bool = Nd4j.create(BOOL, 1, 10);
                val dbl = Nd4j.create(DOUBLE, 1, 10);
                val boolAttached = bool.isAttached();
                val doubleAttached = dbl.isAttached();
                System.out.println(((((i + "\tboolAttached=") + boolAttached) + ", doubleAttached=") + doubleAttached));
                // System.out.println("bool: " + bool);        //java.lang.IllegalStateException: Indexer must never be null
                // System.out.println("double: " + dbl);
            }
        }
    }

    @Test
    public void testArrayCreationFromPointer() {
        val source = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        val pAddress = source.data().addressPointer();
        val shape = source.shape();
        val stride = source.stride();
        val order = source.ordering();
        val buffer = Nd4j.createBuffer(pAddress, source.length(), source.dataType());
        val restored = Nd4j.create(buffer, shape, stride, 0, order, source.dataType());
        Assert.assertEquals(source, restored);
        Assert.assertArrayEquals(source.toDoubleVector(), restored.toDoubleVector(), 1.0E-5);
        Assert.assertEquals(source.getDouble(0), restored.getDouble(0), 1.0E-5);
    }
}

