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
package org.apache.flink.api.java.typeutils;


import BasicTypeInfo.DOUBLE_TYPE_INFO;
import BasicTypeInfo.INT_TYPE_INFO;
import BasicTypeInfo.LONG_TYPE_INFO;
import BasicTypeInfo.STRING_TYPE_INFO;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Assert;
import org.junit.Test;


public class CompositeTypeTest {
    private final TupleTypeInfo<?> tupleTypeInfo = new TupleTypeInfo<org.apache.flink.api.java.tuple.Tuple4<Integer, Integer, Integer, Integer>>(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);

    private final TupleTypeInfo<Tuple3<Integer, String, Long>> inNestedTuple1 = new TupleTypeInfo<Tuple3<Integer, String, Long>>(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO);

    private final TupleTypeInfo<Tuple2<Double, Double>> inNestedTuple2 = new TupleTypeInfo<Tuple2<Double, Double>>(BasicTypeInfo.DOUBLE_TYPE_INFO, BasicTypeInfo.DOUBLE_TYPE_INFO);

    private final TupleTypeInfo<?> nestedTypeInfo = new TupleTypeInfo<org.apache.flink.api.java.tuple.Tuple4<Integer, Tuple3<Integer, String, Long>, Integer, Tuple2<Double, Double>>>(BasicTypeInfo.INT_TYPE_INFO, inNestedTuple1, BasicTypeInfo.INT_TYPE_INFO, inNestedTuple2);

    private final TupleTypeInfo<Tuple2<Integer, Tuple2<Integer, Integer>>> inNestedTuple3 = new TupleTypeInfo<Tuple2<Integer, Tuple2<Integer, Integer>>>(BasicTypeInfo.INT_TYPE_INFO, new TupleTypeInfo<Tuple2<Integer, Integer>>(BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO));

    private final TupleTypeInfo<?> deepNestedTupleTypeInfo = new TupleTypeInfo<Tuple3<Integer, Tuple2<Integer, Tuple2<Integer, Integer>>, Integer>>(BasicTypeInfo.INT_TYPE_INFO, inNestedTuple3, BasicTypeInfo.INT_TYPE_INFO);

    private final PojoTypeInfo<?> pojoTypeInfo = ((PojoTypeInfo<?>) (TypeExtractor.getForClass(CompositeTypeTest.MyPojo.class)));

    private final TupleTypeInfo<?> pojoInTupleTypeInfo = new TupleTypeInfo<Tuple2<Integer, CompositeTypeTest.MyPojo>>(BasicTypeInfo.INT_TYPE_INFO, pojoTypeInfo);

    @Test
    public void testGetFlatFields() {
        Assert.assertEquals(0, tupleTypeInfo.getFlatFields("0").get(0).getPosition());
        Assert.assertEquals(1, tupleTypeInfo.getFlatFields("1").get(0).getPosition());
        Assert.assertEquals(2, tupleTypeInfo.getFlatFields("2").get(0).getPosition());
        Assert.assertEquals(3, tupleTypeInfo.getFlatFields("3").get(0).getPosition());
        Assert.assertEquals(0, tupleTypeInfo.getFlatFields("f0").get(0).getPosition());
        Assert.assertEquals(1, tupleTypeInfo.getFlatFields("f1").get(0).getPosition());
        Assert.assertEquals(2, tupleTypeInfo.getFlatFields("f2").get(0).getPosition());
        Assert.assertEquals(3, tupleTypeInfo.getFlatFields("f3").get(0).getPosition());
        Assert.assertEquals(0, nestedTypeInfo.getFlatFields("0").get(0).getPosition());
        Assert.assertEquals(1, nestedTypeInfo.getFlatFields("1.0").get(0).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("1.1").get(0).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("1.2").get(0).getPosition());
        Assert.assertEquals(4, nestedTypeInfo.getFlatFields("2").get(0).getPosition());
        Assert.assertEquals(5, nestedTypeInfo.getFlatFields("3.0").get(0).getPosition());
        Assert.assertEquals(6, nestedTypeInfo.getFlatFields("3.1").get(0).getPosition());
        Assert.assertEquals(4, nestedTypeInfo.getFlatFields("f2").get(0).getPosition());
        Assert.assertEquals(5, nestedTypeInfo.getFlatFields("f3.f0").get(0).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("1").size());
        Assert.assertEquals(1, nestedTypeInfo.getFlatFields("1").get(0).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("1").get(1).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("1").get(2).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("1.*").size());
        Assert.assertEquals(1, nestedTypeInfo.getFlatFields("1.*").get(0).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("1.*").get(1).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("1.*").get(2).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("3").size());
        Assert.assertEquals(5, nestedTypeInfo.getFlatFields("3").get(0).getPosition());
        Assert.assertEquals(6, nestedTypeInfo.getFlatFields("3").get(1).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("f1").size());
        Assert.assertEquals(1, nestedTypeInfo.getFlatFields("f1").get(0).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("f1").get(1).getPosition());
        Assert.assertEquals(3, nestedTypeInfo.getFlatFields("f1").get(2).getPosition());
        Assert.assertEquals(2, nestedTypeInfo.getFlatFields("f3").size());
        Assert.assertEquals(5, nestedTypeInfo.getFlatFields("f3").get(0).getPosition());
        Assert.assertEquals(6, nestedTypeInfo.getFlatFields("f3").get(1).getPosition());
        Assert.assertEquals(INT_TYPE_INFO, nestedTypeInfo.getFlatFields("0").get(0).getType());
        Assert.assertEquals(STRING_TYPE_INFO, nestedTypeInfo.getFlatFields("1.1").get(0).getType());
        Assert.assertEquals(LONG_TYPE_INFO, nestedTypeInfo.getFlatFields("1").get(2).getType());
        Assert.assertEquals(DOUBLE_TYPE_INFO, nestedTypeInfo.getFlatFields("3").get(1).getType());
        Assert.assertEquals(3, deepNestedTupleTypeInfo.getFlatFields("1").size());
        Assert.assertEquals(1, deepNestedTupleTypeInfo.getFlatFields("1").get(0).getPosition());
        Assert.assertEquals(2, deepNestedTupleTypeInfo.getFlatFields("1").get(1).getPosition());
        Assert.assertEquals(3, deepNestedTupleTypeInfo.getFlatFields("1").get(2).getPosition());
        Assert.assertEquals(5, deepNestedTupleTypeInfo.getFlatFields("*").size());
        Assert.assertEquals(0, deepNestedTupleTypeInfo.getFlatFields("*").get(0).getPosition());
        Assert.assertEquals(1, deepNestedTupleTypeInfo.getFlatFields("*").get(1).getPosition());
        Assert.assertEquals(2, deepNestedTupleTypeInfo.getFlatFields("*").get(2).getPosition());
        Assert.assertEquals(3, deepNestedTupleTypeInfo.getFlatFields("*").get(3).getPosition());
        Assert.assertEquals(4, deepNestedTupleTypeInfo.getFlatFields("*").get(4).getPosition());
        Assert.assertEquals(0, pojoTypeInfo.getFlatFields("a").get(0).getPosition());
        Assert.assertEquals(1, pojoTypeInfo.getFlatFields("b").get(0).getPosition());
        Assert.assertEquals(2, pojoTypeInfo.getFlatFields("*").size());
        Assert.assertEquals(0, pojoTypeInfo.getFlatFields("*").get(0).getPosition());
        Assert.assertEquals(1, pojoTypeInfo.getFlatFields("*").get(1).getPosition());
        Assert.assertEquals(1, pojoInTupleTypeInfo.getFlatFields("f1.a").get(0).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("1.b").get(0).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("1").size());
        Assert.assertEquals(1, pojoInTupleTypeInfo.getFlatFields("1.*").get(0).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("1").get(1).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("f1.*").size());
        Assert.assertEquals(1, pojoInTupleTypeInfo.getFlatFields("f1.*").get(0).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("f1").get(1).getPosition());
        Assert.assertEquals(3, pojoInTupleTypeInfo.getFlatFields("*").size());
        Assert.assertEquals(0, pojoInTupleTypeInfo.getFlatFields("*").get(0).getPosition());
        Assert.assertEquals(1, pojoInTupleTypeInfo.getFlatFields("*").get(1).getPosition());
        Assert.assertEquals(2, pojoInTupleTypeInfo.getFlatFields("*").get(2).getPosition());
    }

    @Test
    public void testFieldAtStringRef() {
        Assert.assertEquals(INT_TYPE_INFO, tupleTypeInfo.getTypeAt("0"));
        Assert.assertEquals(INT_TYPE_INFO, tupleTypeInfo.getTypeAt("2"));
        Assert.assertEquals(INT_TYPE_INFO, tupleTypeInfo.getTypeAt("f1"));
        Assert.assertEquals(INT_TYPE_INFO, tupleTypeInfo.getTypeAt("f3"));
        Assert.assertEquals(INT_TYPE_INFO, nestedTypeInfo.getTypeAt("0"));
        Assert.assertEquals(INT_TYPE_INFO, nestedTypeInfo.getTypeAt("1.0"));
        Assert.assertEquals(STRING_TYPE_INFO, nestedTypeInfo.getTypeAt("1.1"));
        Assert.assertEquals(LONG_TYPE_INFO, nestedTypeInfo.getTypeAt("1.2"));
        Assert.assertEquals(INT_TYPE_INFO, nestedTypeInfo.getTypeAt("2"));
        Assert.assertEquals(DOUBLE_TYPE_INFO, nestedTypeInfo.getTypeAt("3.0"));
        Assert.assertEquals(DOUBLE_TYPE_INFO, nestedTypeInfo.getTypeAt("3.1"));
        Assert.assertEquals(INT_TYPE_INFO, nestedTypeInfo.getTypeAt("f2"));
        Assert.assertEquals(DOUBLE_TYPE_INFO, nestedTypeInfo.getTypeAt("f3.f0"));
        Assert.assertEquals(inNestedTuple1, nestedTypeInfo.getTypeAt("1"));
        Assert.assertEquals(inNestedTuple2, nestedTypeInfo.getTypeAt("3"));
        Assert.assertEquals(inNestedTuple1, nestedTypeInfo.getTypeAt("f1"));
        Assert.assertEquals(inNestedTuple2, nestedTypeInfo.getTypeAt("f3"));
        Assert.assertEquals(inNestedTuple3, deepNestedTupleTypeInfo.getTypeAt("1"));
        Assert.assertEquals(STRING_TYPE_INFO, pojoTypeInfo.getTypeAt("a"));
        Assert.assertEquals(INT_TYPE_INFO, pojoTypeInfo.getTypeAt("b"));
        Assert.assertEquals(STRING_TYPE_INFO, pojoInTupleTypeInfo.getTypeAt("f1.a"));
        Assert.assertEquals(INT_TYPE_INFO, pojoInTupleTypeInfo.getTypeAt("1.b"));
        Assert.assertEquals(pojoTypeInfo, pojoInTupleTypeInfo.getTypeAt("1"));
        Assert.assertEquals(pojoTypeInfo, pojoInTupleTypeInfo.getTypeAt("f1"));
    }

    public static class MyPojo {
        public String a;

        public int b;
    }
}

