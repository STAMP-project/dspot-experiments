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
package org.apache.flink.streaming.util.typeutils;


import CompositeType.InvalidFieldReferenceException;
import org.apache.flink.api.common.typeinfo.BasicArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for field accessors.
 */
public class FieldAccessorTest {
    // Note, that AggregationFunctionTest indirectly also tests FieldAccessors.
    // ProductFieldAccessors are tested in CaseClassFieldAccessorTest.
    @Test
    public void testFlatTuple() {
        Tuple2<String, Integer> t = Tuple2.of("aa", 5);
        TupleTypeInfo<Tuple2<String, Integer>> tpeInfo = ((TupleTypeInfo<Tuple2<String, Integer>>) (TypeExtractor.getForObject(t)));
        FieldAccessor<Tuple2<String, Integer>, String> f0 = FieldAccessorFactory.getAccessor(tpeInfo, "f0", null);
        Assert.assertEquals("aa", f0.get(t));
        Assert.assertEquals("aa", t.f0);
        t = f0.set(t, "b");
        Assert.assertEquals("b", f0.get(t));
        Assert.assertEquals("b", t.f0);
        FieldAccessor<Tuple2<String, Integer>, Integer> f1 = FieldAccessorFactory.getAccessor(tpeInfo, "f1", null);
        Assert.assertEquals(5, ((int) (f1.get(t))));
        Assert.assertEquals(5, ((int) (t.f1)));
        t = f1.set(t, 7);
        Assert.assertEquals(7, ((int) (f1.get(t))));
        Assert.assertEquals(7, ((int) (t.f1)));
        Assert.assertEquals("b", f0.get(t));
        Assert.assertEquals("b", t.f0);
        FieldAccessor<Tuple2<String, Integer>, Integer> f1n = FieldAccessorFactory.getAccessor(tpeInfo, 1, null);
        Assert.assertEquals(7, ((int) (f1n.get(t))));
        Assert.assertEquals(7, ((int) (t.f1)));
        t = f1n.set(t, 10);
        Assert.assertEquals(10, ((int) (f1n.get(t))));
        Assert.assertEquals(10, ((int) (f1.get(t))));
        Assert.assertEquals(10, ((int) (t.f1)));
        Assert.assertEquals("b", f0.get(t));
        Assert.assertEquals("b", t.f0);
        FieldAccessor<Tuple2<String, Integer>, Integer> f1ns = FieldAccessorFactory.getAccessor(tpeInfo, "1", null);
        Assert.assertEquals(10, ((int) (f1ns.get(t))));
        Assert.assertEquals(10, ((int) (t.f1)));
        t = f1ns.set(t, 11);
        Assert.assertEquals(11, ((int) (f1ns.get(t))));
        Assert.assertEquals(11, ((int) (f1.get(t))));
        Assert.assertEquals(11, ((int) (t.f1)));
        Assert.assertEquals("b", f0.get(t));
        Assert.assertEquals("b", t.f0);
        // This is technically valid (the ".0" is selecting the 0th field of a basic type).
        FieldAccessor<Tuple2<String, Integer>, String> f0f0 = FieldAccessorFactory.getAccessor(tpeInfo, "f0.0", null);
        Assert.assertEquals("b", f0f0.get(t));
        Assert.assertEquals("b", t.f0);
        t = f0f0.set(t, "cc");
        Assert.assertEquals("cc", f0f0.get(t));
        Assert.assertEquals("cc", t.f0);
    }

    @Test(expected = InvalidFieldReferenceException.class)
    public void testIllegalFlatTuple() {
        Tuple2<String, Integer> t = Tuple2.of("aa", 5);
        TupleTypeInfo<Tuple2<String, Integer>> tpeInfo = ((TupleTypeInfo<Tuple2<String, Integer>>) (TypeExtractor.getForObject(t)));
        FieldAccessorFactory.getAccessor(tpeInfo, "illegal", null);
    }

    @Test
    public void testTupleInTuple() {
        Tuple2<String, Tuple3<Integer, Long, Double>> t = Tuple2.of("aa", Tuple3.of(5, 9L, 2.0));
        TupleTypeInfo<Tuple2<String, Tuple3<Integer, Long, Double>>> tpeInfo = ((TupleTypeInfo<Tuple2<String, Tuple3<Integer, Long, Double>>>) (TypeExtractor.getForObject(t)));
        FieldAccessor<Tuple2<String, Tuple3<Integer, Long, Double>>, String> f0 = FieldAccessorFactory.getAccessor(tpeInfo, "f0", null);
        Assert.assertEquals("aa", f0.get(t));
        Assert.assertEquals("aa", t.f0);
        FieldAccessor<Tuple2<String, Tuple3<Integer, Long, Double>>, Double> f1f2 = FieldAccessorFactory.getAccessor(tpeInfo, "f1.f2", null);
        Assert.assertEquals(2.0, f1f2.get(t), 0);
        Assert.assertEquals(2.0, t.f1.f2, 0);
        t = f1f2.set(t, 3.0);
        Assert.assertEquals(3.0, f1f2.get(t), 0);
        Assert.assertEquals(3.0, t.f1.f2, 0);
        Assert.assertEquals("aa", f0.get(t));
        Assert.assertEquals("aa", t.f0);
        FieldAccessor<Tuple2<String, Tuple3<Integer, Long, Double>>, Tuple3<Integer, Long, Double>> f1 = FieldAccessorFactory.getAccessor(tpeInfo, "f1", null);
        Assert.assertEquals(Tuple3.of(5, 9L, 3.0), f1.get(t));
        Assert.assertEquals(Tuple3.of(5, 9L, 3.0), t.f1);
        t = f1.set(t, Tuple3.of(8, 12L, 4.0));
        Assert.assertEquals(Tuple3.of(8, 12L, 4.0), f1.get(t));
        Assert.assertEquals(Tuple3.of(8, 12L, 4.0), t.f1);
        Assert.assertEquals("aa", f0.get(t));
        Assert.assertEquals("aa", t.f0);
        FieldAccessor<Tuple2<String, Tuple3<Integer, Long, Double>>, Tuple3<Integer, Long, Double>> f1n = FieldAccessorFactory.getAccessor(tpeInfo, 1, null);
        Assert.assertEquals(Tuple3.of(8, 12L, 4.0), f1n.get(t));
        Assert.assertEquals(Tuple3.of(8, 12L, 4.0), t.f1);
        t = f1n.set(t, Tuple3.of(10, 13L, 5.0));
        Assert.assertEquals(Tuple3.of(10, 13L, 5.0), f1n.get(t));
        Assert.assertEquals(Tuple3.of(10, 13L, 5.0), f1.get(t));
        Assert.assertEquals(Tuple3.of(10, 13L, 5.0), t.f1);
        Assert.assertEquals("aa", f0.get(t));
        Assert.assertEquals("aa", t.f0);
    }

    @Test(expected = InvalidFieldReferenceException.class)
    @SuppressWarnings("unchecked")
    public void testIllegalTupleField() {
        FieldAccessorFactory.getAccessor(TupleTypeInfo.getBasicTupleTypeInfo(Integer.class, Integer.class), 2, null);
    }

    /**
     * POJO.
     */
    public static class Foo {
        public int x;

        public Tuple2<String, Long> t;

        public Short y;

        public Foo() {
        }

        public Foo(int x, Tuple2<String, Long> t, Short y) {
            this.x = x;
            this.t = t;
            this.y = y;
        }
    }

    @Test
    public void testTupleInPojoInTuple() {
        Tuple2<String, FieldAccessorTest.Foo> t = Tuple2.of("aa", new FieldAccessorTest.Foo(8, Tuple2.of("ddd", 9L), ((short) (2))));
        TupleTypeInfo<Tuple2<String, FieldAccessorTest.Foo>> tpeInfo = ((TupleTypeInfo<Tuple2<String, FieldAccessorTest.Foo>>) (TypeExtractor.getForObject(t)));
        FieldAccessor<Tuple2<String, FieldAccessorTest.Foo>, Long> f1tf1 = FieldAccessorFactory.getAccessor(tpeInfo, "f1.t.f1", null);
        Assert.assertEquals(9L, ((long) (f1tf1.get(t))));
        Assert.assertEquals(9L, ((long) (t.f1.t.f1)));
        t = f1tf1.set(t, 12L);
        Assert.assertEquals(12L, ((long) (f1tf1.get(t))));
        Assert.assertEquals(12L, ((long) (t.f1.t.f1)));
        FieldAccessor<Tuple2<String, FieldAccessorTest.Foo>, String> f1tf0 = FieldAccessorFactory.getAccessor(tpeInfo, "f1.t.f0", null);
        Assert.assertEquals("ddd", f1tf0.get(t));
        Assert.assertEquals("ddd", t.f1.t.f0);
        t = f1tf0.set(t, "alma");
        Assert.assertEquals("alma", f1tf0.get(t));
        Assert.assertEquals("alma", t.f1.t.f0);
        FieldAccessor<Tuple2<String, FieldAccessorTest.Foo>, FieldAccessorTest.Foo> f1 = FieldAccessorFactory.getAccessor(tpeInfo, "f1", null);
        FieldAccessor<Tuple2<String, FieldAccessorTest.Foo>, FieldAccessorTest.Foo> f1n = FieldAccessorFactory.getAccessor(tpeInfo, 1, null);
        Assert.assertEquals(Tuple2.of("alma", 12L), f1.get(t).t);
        Assert.assertEquals(Tuple2.of("alma", 12L), f1n.get(t).t);
        Assert.assertEquals(Tuple2.of("alma", 12L), t.f1.t);
        FieldAccessorTest.Foo newFoo = new FieldAccessorTest.Foo(8, Tuple2.of("ddd", 9L), ((short) (2)));
        f1.set(t, newFoo);
        Assert.assertEquals(newFoo, f1.get(t));
        Assert.assertEquals(newFoo, f1n.get(t));
        Assert.assertEquals(newFoo, t.f1);
    }

    @Test(expected = InvalidFieldReferenceException.class)
    public void testIllegalTupleInPojoInTuple() {
        Tuple2<String, FieldAccessorTest.Foo> t = Tuple2.of("aa", new FieldAccessorTest.Foo(8, Tuple2.of("ddd", 9L), ((short) (2))));
        TupleTypeInfo<Tuple2<String, FieldAccessorTest.Foo>> tpeInfo = ((TupleTypeInfo<Tuple2<String, FieldAccessorTest.Foo>>) (TypeExtractor.getForObject(t)));
        FieldAccessorFactory.getAccessor(tpeInfo, "illegal.illegal.illegal", null);
    }

    /**
     * POJO for testing field access.
     */
    public static class Inner {
        public long x;

        public boolean b;

        public Inner() {
        }

        public Inner(long x) {
            this.x = x;
        }

        public Inner(long x, boolean b) {
            this.x = x;
            this.b = b;
        }

        @Override
        public String toString() {
            return ((((Long) (x)).toString()) + ", ") + (b);
        }
    }

    /**
     * POJO containing POJO.
     */
    public static class Outer {
        public int a;

        public FieldAccessorTest.Inner i;

        public short b;

        public Outer() {
        }

        public Outer(int a, FieldAccessorTest.Inner i, short b) {
            this.a = a;
            this.i = i;
            this.b = b;
        }

        @Override
        public String toString() {
            return ((((a) + ", ") + (i.toString())) + ", ") + (b);
        }
    }

    @Test
    public void testPojoInPojo() {
        FieldAccessorTest.Outer o = new FieldAccessorTest.Outer(10, new FieldAccessorTest.Inner(4L), ((short) (12)));
        PojoTypeInfo<FieldAccessorTest.Outer> tpeInfo = ((PojoTypeInfo<FieldAccessorTest.Outer>) (TypeInformation.of(FieldAccessorTest.Outer.class)));
        FieldAccessor<FieldAccessorTest.Outer, Long> fix = FieldAccessorFactory.getAccessor(tpeInfo, "i.x", null);
        Assert.assertEquals(4L, ((long) (fix.get(o))));
        Assert.assertEquals(4L, o.i.x);
        o = fix.set(o, 22L);
        Assert.assertEquals(22L, ((long) (fix.get(o))));
        Assert.assertEquals(22L, o.i.x);
        FieldAccessor<FieldAccessorTest.Outer, FieldAccessorTest.Inner> fi = FieldAccessorFactory.getAccessor(tpeInfo, "i", null);
        Assert.assertEquals(22L, fi.get(o).x);
        Assert.assertEquals(22L, ((long) (fix.get(o))));
        Assert.assertEquals(22L, o.i.x);
        o = fi.set(o, new FieldAccessorTest.Inner(30L));
        Assert.assertEquals(30L, fi.get(o).x);
        Assert.assertEquals(30L, ((long) (fix.get(o))));
        Assert.assertEquals(30L, o.i.x);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testArray() {
        int[] a = new int[]{ 3, 5 };
        FieldAccessor<int[], Integer> fieldAccessor = ((FieldAccessor<int[], Integer>) ((Object) (FieldAccessorFactory.getAccessor(PrimitiveArrayTypeInfo.getInfoFor(a.getClass()), 1, null))));
        Assert.assertEquals(Integer.class, fieldAccessor.getFieldType().getTypeClass());
        Assert.assertEquals(((Integer) (a[1])), fieldAccessor.get(a));
        a = fieldAccessor.set(a, 6);
        Assert.assertEquals(((Integer) (a[1])), fieldAccessor.get(a));
        Integer[] b = new Integer[]{ 3, 5 };
        FieldAccessor<Integer[], Integer> fieldAccessor2 = ((FieldAccessor<Integer[], Integer>) ((Object) (FieldAccessorFactory.getAccessor(BasicArrayTypeInfo.getInfoFor(b.getClass()), 1, null))));
        Assert.assertEquals(Integer.class, fieldAccessor2.getFieldType().getTypeClass());
        Assert.assertEquals(b[1], fieldAccessor2.get(b));
        b = fieldAccessor2.set(b, 6);
        Assert.assertEquals(b[1], fieldAccessor2.get(b));
    }

    /**
     * POJO with array.
     */
    public static class ArrayInPojo {
        public long x;

        public int[] arr;

        public int y;

        public ArrayInPojo() {
        }

        public ArrayInPojo(long x, int[] arr, int y) {
            this.x = x;
            this.arr = arr;
            this.y = y;
        }
    }

    @Test
    public void testArrayInPojo() {
        FieldAccessorTest.ArrayInPojo o = new FieldAccessorTest.ArrayInPojo(10L, new int[]{ 3, 4, 5 }, 12);
        PojoTypeInfo<FieldAccessorTest.ArrayInPojo> tpeInfo = ((PojoTypeInfo<FieldAccessorTest.ArrayInPojo>) (TypeInformation.of(FieldAccessorTest.ArrayInPojo.class)));
        FieldAccessor<FieldAccessorTest.ArrayInPojo, Integer> fix = FieldAccessorFactory.getAccessor(tpeInfo, "arr.1", null);
        Assert.assertEquals(4, ((int) (fix.get(o))));
        Assert.assertEquals(4L, o.arr[1]);
        o = fix.set(o, 8);
        Assert.assertEquals(8, ((int) (fix.get(o))));
        Assert.assertEquals(8, o.arr[1]);
    }

    @Test
    public void testBasicType() {
        Long x = 7L;
        TypeInformation<Long> tpeInfo = BasicTypeInfo.LONG_TYPE_INFO;
        FieldAccessor<Long, Long> f = FieldAccessorFactory.getAccessor(tpeInfo, 0, null);
        Assert.assertEquals(7L, ((long) (f.get(x))));
        x = f.set(x, 12L);
        Assert.assertEquals(12L, ((long) (f.get(x))));
        Assert.assertEquals(12L, ((long) (x)));
        FieldAccessor<Long, Long> f2 = FieldAccessorFactory.getAccessor(tpeInfo, "*", null);
        Assert.assertEquals(12L, ((long) (f2.get(x))));
        x = f2.set(x, 14L);
        Assert.assertEquals(14L, ((long) (f2.get(x))));
        Assert.assertEquals(14L, ((long) (x)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBasicType1() {
        Long x = 7L;
        TypeInformation<Long> tpeInfo = BasicTypeInfo.LONG_TYPE_INFO;
        FieldAccessor<Long, Long> f = FieldAccessorFactory.getAccessor(tpeInfo, 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBasicType2() {
        Long x = 7L;
        TypeInformation<Long> tpeInfo = BasicTypeInfo.LONG_TYPE_INFO;
        FieldAccessor<Long, Long> f = FieldAccessorFactory.getAccessor(tpeInfo, "foo", null);
    }
}

