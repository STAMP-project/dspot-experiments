/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.operator;


import CompositeType.InvalidFieldReferenceException;
import Order.ANY;
import Order.ASCENDING;
import Order.DESCENDING;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.api.common.InvalidProgramException;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DataSet#writeAsText(String)}.
 */
public class DataSinkTest {
    // TUPLE DATA
    private final List<Tuple5<Integer, Long, String, Long, Integer>> emptyTupleData = new ArrayList<>();

    private final TupleTypeInfo<Tuple5<Integer, Long, String, Long, Integer>> tupleTypeInfo = new TupleTypeInfo(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);

    // POJO DATA
    private final List<DataSinkTest.CustomType> pojoData = new ArrayList<>();

    @Test
    public void testTupleSingleOrderIdx() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // should work
        try {
            tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(0, ANY);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTupleTwoOrderIdx() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // should work
        try {
            tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(0, ASCENDING).sortLocalOutput(3, DESCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTupleSingleOrderExp() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // should work
        try {
            tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("f0", ANY);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTupleTwoOrderExp() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // should work
        try {
            tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("f1", ASCENDING).sortLocalOutput("f4", DESCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testTupleTwoOrderMixed() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // should work
        try {
            tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(4, ASCENDING).sortLocalOutput("f2", DESCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testFailTupleIndexOutOfBounds() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // must not work
        tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(3, ASCENDING).sortLocalOutput(5, DESCENDING);
    }

    @Test(expected = InvalidFieldReferenceException.class)
    public void testFailTupleInv() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple5<Integer, Long, String, Long, Integer>> tupleDs = env.fromCollection(emptyTupleData, tupleTypeInfo);
        // must not work
        tupleDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("notThere", ASCENDING).sortLocalOutput("f4", DESCENDING);
    }

    @Test
    public void testPrimitiveOrder() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> longDs = env.generateSequence(0, 2);
        // should work
        try {
            longDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("*", ASCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test(expected = InvalidProgramException.class)
    public void testFailPrimitiveOrder1() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> longDs = env.generateSequence(0, 2);
        // must not work
        longDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(0, ASCENDING);
    }

    @Test(expected = InvalidProgramException.class)
    public void testFailPrimitiveOrder2() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> longDs = env.generateSequence(0, 2);
        // must not work
        longDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("0", ASCENDING);
    }

    @Test(expected = InvalidProgramException.class)
    public void testFailPrimitiveOrder3() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> longDs = env.generateSequence(0, 2);
        // must not work
        longDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("nope", ASCENDING);
    }

    @Test
    public void testPojoSingleOrder() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<DataSinkTest.CustomType> pojoDs = env.fromCollection(pojoData);
        // should work
        try {
            pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("myString", ASCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testPojoTwoOrder() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<DataSinkTest.CustomType> pojoDs = env.fromCollection(pojoData);
        // should work
        try {
            pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("myLong", ASCENDING).sortLocalOutput("myString", DESCENDING);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test(expected = InvalidProgramException.class)
    public void testFailPojoIdx() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<DataSinkTest.CustomType> pojoDs = env.fromCollection(pojoData);
        // must not work
        pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput(1, DESCENDING);
    }

    @Test(expected = InvalidFieldReferenceException.class)
    public void testFailPojoInvalidField() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<DataSinkTest.CustomType> pojoDs = env.fromCollection(pojoData);
        // must not work
        pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("myInt", ASCENDING).sortLocalOutput("notThere", DESCENDING);
    }

    @Test(expected = InvalidProgramException.class)
    public void testPojoSingleOrderFull() {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<DataSinkTest.CustomType> pojoDs = env.fromCollection(pojoData);
        // must not work
        pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("*", ASCENDING);
    }

    @Test(expected = InvalidProgramException.class)
    public void testArrayOrderFull() {
        List<Object[]> arrayData = new ArrayList<>();
        arrayData.add(new Object[0]);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Object[]> pojoDs = env.fromCollection(arrayData);
        // must not work
        pojoDs.writeAsText("/tmp/willNotHappen").sortLocalOutput("*", ASCENDING);
    }

    /**
     * Custom data type, for testing purposes.
     */
    public static class CustomType implements Serializable {
        private static final long serialVersionUID = 1L;

        public int myInt;

        public long myLong;

        public String myString;

        public CustomType() {
        }

        public CustomType(int i, long l, String s) {
            myInt = i;
            myLong = l;
            myString = s;
        }

        @Override
        public String toString() {
            return ((((myInt) + ",") + (myLong)) + ",") + (myString);
        }
    }
}

