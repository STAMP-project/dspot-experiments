/**
 * Copyright 2014 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.lib.profiler.heap;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.gridkit.jvmtool.heapdump.HeapPathHelper;
import org.gridkit.jvmtool.heapdump.HeapWalker;
import org.gridkit.jvmtool.heapdump.InboundAnalyzer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.JVM)
public abstract class BaseHeapTest {
    @Test
    public void verify_DummyA_via_classes() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyA.class.getName());
        int n = 0;
        for (Instance i : jclass.getInstances()) {
            ++n;
            assertThat(i.getFieldValues().size()).isEqualTo(1);
            FieldValue fv = i.getFieldValues().get(0);
            assertThat(fv).isInstanceOf(ObjectFieldValue.class);
            Instance ii = getInstance();
            assertThat(ii).isInstanceOf(PrimitiveArrayInstance.class);
        }
        assertThat(n).isEqualTo(50);
    }

    @Test
    public void verify_DummyA_via_scan() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyA.class.getName());
        int n = 0;
        for (Instance i : heap.getAllInstances()) {
            if ((i.getJavaClass()) == jclass) {
                ++n;
                assertThat(i.getFieldValues().size()).isEqualTo(1);
                FieldValue fv = i.getFieldValues().get(0);
                assertThat(fv).isInstanceOf(ObjectFieldValue.class);
                Instance ii = getInstance();
                assertThat(ii).isInstanceOf(PrimitiveArrayInstance.class);
            }
        }
        assertThat(n).isEqualTo(50);
    }

    @Test
    public void verify_heap_walker_for_dummyB() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyB.class.getName());
        int n = 0;
        for (Instance i : jclass.getInstances()) {
            ++n;
            int no = Integer.valueOf(HeapWalker.stringValue(HeapWalker.walkFirst(i, "seqNo")));
            SortedSet<String> testSet = new TreeSet<String>();
            for (Instance e : HeapWalker.walk(i, "list.elementData[*]")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            assertThat(testSet).isEqualTo(testSet("", no));
            testSet.clear();
            for (Instance e : HeapWalker.walk(i, "map.table[*].key")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            // some entries may be missing due to hash collisions
            assertThat(testSet("k", no).containsAll(testSet)).isTrue();
            testSet.clear();
            for (Instance e : HeapWalker.walk(i, "map.table[*].value")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            // some entries may be missing due to hash collisions
            assertThat(testSet("v", no).containsAll(testSet)).isTrue();
        }
        assertThat(n).isEqualTo(50);
    }

    @Test
    public void verify_inbound_ref_reporter() {
        Heap heap = getHeap();
        InboundAnalyzer ia = new InboundAnalyzer(heap);
        ia.initRoots();
        Instance i = heap.getJavaClassByName(DummyB.class.getName()).getInstances().get(0);
        ia.mark(i.getInstanceId());
        int d = 1;
        while (!(ia.isExhausted())) {
            System.out.println(("\nDepth " + d));
            ia.report();
            ++d;
        } 
    }

    @Test
    public void verify_heap_walker_for_dummyB_over_map() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyB.class.getName());
        int n = 0;
        for (Instance i : jclass.getInstances()) {
            ++n;
            int no = Integer.valueOf(HeapWalker.stringValue(HeapWalker.walkFirst(i, "seqNo")));
            SortedSet<String> testSet = new TreeSet<String>();
            for (Instance e : HeapWalker.walk(i, "list.elementData[*]")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            assertThat(testSet).isEqualTo(testSet("", no));
            testSet.clear();
            for (Instance e : HeapWalker.walk(i, "map?entrySet.key")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            assertThat(testSet).isEqualTo(testSet("k", no));
            testSet.clear();
            for (Instance e : HeapWalker.walk(i, "map?entrySet.value")) {
                if (e != null) {
                    testSet.add(HeapWalker.stringValue(e));
                }
            }
            assertThat(testSet).isEqualTo(testSet("v", no));
            if ((testSet.size()) > 5) {
                assertThat(HeapWalker.valueOf(i, "map?entrySet[key=k3].value")).isEqualTo("v3");
            }
        }
        assertThat(n).isEqualTo(50);
    }

    @SuppressWarnings("unused")
    @Test
    public void verify_heap_walker_for_array_list() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(ArrayList.class.getName());
        int n = 0;
        for (Instance i : jclass.getInstances()) {
            int m = 0;
            for (Instance e : HeapWalker.walk(i, "elementData[*](**.DummyA)")) {
                ++m;
            }
            if (m != 0) {
                ++n;
                assertThat(m).isEqualTo(50);
            }
        }
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void verify_heap_path_for_arrays() {
        boolean[] bool_values = new boolean[]{ true, false };
        byte[] byte_values = new byte[]{ Byte.MIN_VALUE, Byte.MAX_VALUE };
        short[] short_values = new short[]{ Short.MIN_VALUE, Short.MAX_VALUE };
        char[] char_values = new char[]{ Character.MIN_VALUE, Character.MAX_VALUE };
        int[] int_values = new int[]{ Integer.MIN_VALUE, Integer.MAX_VALUE };
        long[] long_values = new long[]{ Long.MIN_VALUE, Long.MAX_VALUE };
        float[] float_values = new float[]{ Float.MIN_VALUE, Float.NaN, Float.MAX_VALUE };
        double[] double_values = new double[]{ Double.MIN_VALUE, Double.NaN, Double.MAX_VALUE };
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyC.class.getName());
        int n = 0;
        for (Instance i : jclass.getInstances()) {
            BaseHeapTest.assertArrayEquals(bool_values, HeapWalker.valueOf(i, "bool_values"));
            BaseHeapTest.assertArrayEquals(byte_values, HeapWalker.valueOf(i, "byte_values"));
            BaseHeapTest.assertArrayEquals(short_values, HeapWalker.valueOf(i, "short_values"));
            BaseHeapTest.assertArrayEquals(char_values, HeapWalker.valueOf(i, "char_values"));
            BaseHeapTest.assertArrayEquals(int_values, HeapWalker.valueOf(i, "int_values"));
            BaseHeapTest.assertArrayEquals(long_values, HeapWalker.valueOf(i, "long_values"));
            BaseHeapTest.assertArrayEquals(float_values, HeapWalker.valueOf(i, "float_values"));
            BaseHeapTest.assertArrayEquals(double_values, HeapWalker.valueOf(i, "double_values"));
            assertThat(HeapWalker.valueOf(i, "bool_values[0]")).isEqualTo(Boolean.TRUE);
            assertThat(HeapWalker.valueOf(i, "byte_values[0]")).isEqualTo(Byte.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "short_values[0]")).isEqualTo(Short.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "char_values[0]")).isEqualTo(Character.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "int_values[0]")).isEqualTo(Integer.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "long_values[0]")).isEqualTo(Long.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "float_values[0]")).isEqualTo(Float.MIN_VALUE);
            assertThat(HeapWalker.valueOf(i, "double_values[0]")).isEqualTo(Double.MIN_VALUE);
            ++n;
        }
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void verify_heap_path_tracker_over_null() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyD.class.getName());
        Instance i = jclass.getInstances().get(0);
        Assert.assertNull(HeapPathHelper.trackFirst(i, "nested.value"));
        Assert.assertNull(HeapPathHelper.trackFirst(i, "nestedArray[0].value"));
        Assert.assertEquals(".nestedArray[1].value", HeapPathHelper.trackFirst(i, "nestedArray[*].value"));
    }

    @Test
    public void verify_heap_path_walker_over_null() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyD.class.getName());
        Instance i = jclass.getInstances().get(0);
        Assert.assertNull(HeapWalker.walkFirst(i, "nested.value"));
        Assert.assertNull(HeapWalker.walkFirst(i, "nestedArray[0].value"));
        Assert.assertEquals("somevalue", HeapWalker.valueOf(HeapWalker.walkFirst(i, "nestedArray[*].value")));
    }

    @Test
    public void verify_heap_path_walker_inverted_predicate() {
        Heap heap = getHeap();
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("A", "B", "C", "D", "E"), scan(heap.getAllInstances(), "(**DummyP)key"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("A", "C", "E"), scan(heap.getAllInstances(), "(**DummyP)[value!=null]key"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("B", "D"), scan(heap.getAllInstances(), "(**DummyP)[value=null]key"));
    }

    @Test
    public void verify_DummyC_field_access() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyC.class.getName());
        assertThat(jclass.getInstances()).hasSize(1);
        Instance i = jclass.getInstances().get(0);
        assertThat(HeapWalker.valueOf(i, "structField.trueField")).isEqualTo(true);
        assertThat(HeapWalker.valueOf(i, "structField.falseField")).isEqualTo(false);
        assertThat(HeapWalker.valueOf(i, "structField.byteField")).isEqualTo(((byte) (13)));
        assertThat(HeapWalker.valueOf(i, "structField.shortField")).isEqualTo(((short) (-14)));
        assertThat(HeapWalker.valueOf(i, "structField.charField")).isEqualTo(((char) (15)));
        assertThat(HeapWalker.valueOf(i, "structField.intField")).isEqualTo(1717986918);
        assertThat(HeapWalker.valueOf(i, "structField.longField")).isEqualTo(439804651110L);
        assertThat(HeapWalker.valueOf(i, "structField.floatField")).isEqualTo(0.1F);
        assertThat(HeapWalker.valueOf(i, "structField.doubleField")).isEqualTo((-0.2));
        assertThat(HeapWalker.valueOf(i, "structField.trueBoxedField")).isEqualTo(true);
        assertThat(HeapWalker.valueOf(i, "structField.falseBoxedField")).isEqualTo(false);
        assertThat(HeapWalker.valueOf(i, "structField.byteBoxedField")).isEqualTo(((byte) (13)));
        assertThat(HeapWalker.valueOf(i, "structField.shortBoxedField")).isEqualTo(((short) (-14)));
        assertThat(HeapWalker.valueOf(i, "structField.charBoxedField")).isEqualTo(((char) (15)));
        assertThat(HeapWalker.valueOf(i, "structField.intBoxedField")).isEqualTo(1717986918);
        assertThat(HeapWalker.valueOf(i, "structField.longBoxedField")).isEqualTo(439804651110L);
        assertThat(HeapWalker.valueOf(i, "structField.floatBoxedField")).isEqualTo(0.1F);
        assertThat(HeapWalker.valueOf(i, "structField.doubleBoxedField")).isEqualTo((-0.2));
        assertThat(HeapWalker.valueOf(i, "structField.textField")).isEqualTo("this is struct");
        assertThat(HeapWalker.valueOf(i, "structArray[*].trueField")).isEqualTo(true);
        assertThat(HeapWalker.valueOf(i, "structArray[*].falseField")).isEqualTo(false);
        assertThat(HeapWalker.valueOf(i, "structArray[*].byteField")).isEqualTo(((byte) (13)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].shortField")).isEqualTo(((short) (-14)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].charField")).isEqualTo(((char) (15)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].intField")).isEqualTo(1717986918);
        assertThat(HeapWalker.valueOf(i, "structArray[*].longField")).isEqualTo(439804651110L);
        assertThat(HeapWalker.valueOf(i, "structArray[*].floatField")).isEqualTo(0.1F);
        assertThat(HeapWalker.valueOf(i, "structArray[*].doubleField")).isEqualTo((-0.2));
        assertThat(HeapWalker.valueOf(i, "structArray[*].trueBoxedField")).isEqualTo(true);
        assertThat(HeapWalker.valueOf(i, "structArray[*].falseBoxedField")).isEqualTo(false);
        assertThat(HeapWalker.valueOf(i, "structArray[*].byteBoxedField")).isEqualTo(((byte) (13)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].shortBoxedField")).isEqualTo(((short) (-14)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].charBoxedField")).isEqualTo(((char) (15)));
        assertThat(HeapWalker.valueOf(i, "structArray[*].intBoxedField")).isEqualTo(1717986918);
        assertThat(HeapWalker.valueOf(i, "structArray[*].longBoxedField")).isEqualTo(439804651110L);
        assertThat(HeapWalker.valueOf(i, "structArray[*].floatBoxedField")).isEqualTo(0.1F);
        assertThat(HeapWalker.valueOf(i, "structArray[*].doubleBoxedField")).isEqualTo((-0.2));
        assertThat(HeapWalker.valueOf(i, "structArray[*].textField")).isEqualTo("this is struct #1");
    }

    @Test
    public void verify_asterisk_paths() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyN.MyInnerClass.class.getName());
        List<Instance> set = jclass.getInstances();
        assertThat(set).hasSize(5);
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyA", "dummyB"), scan(set, "*(**DummyN).dummyName"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyA", "dummyB"), scan(set, "*(**DummyN).*"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyA", "dummyB"), scan(set, "*.dummyName"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyA"), scan(set, "[innerName=A.1]*(**DummyN).dummyName"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyB"), scan(set, "[innerName=B.1]*(**DummyN).dummyName"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyB"), scan(set, "[innerName=B.1]*(**DummyN).*"));
        BaseHeapTest.assertSetsAreEqual(BaseHeapTest.a("dummyA"), scan(set, "[innerName=A.4]*.dummyName"));
    }

    @Test
    public void verify_single_asterisk_path() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyA.class.getName());
        List<Instance> set = jclass.getInstances();
        assertThat(scan(set, "*")).hasSize(50);
    }

    @Test
    public void verify_string_decoding_latin() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyS.class.getName());
        DummyS proto = new DummyS();
        Instance dummyS = jclass.getInstances().get(0);
        assertThat(HeapWalker.valueOf(dummyS, "latinString")).isEqualTo(proto.latinString);
    }

    @Test
    public void verify_string_decoding_cyrillic() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyS.class.getName());
        DummyS proto = new DummyS();
        Instance dummyS = jclass.getInstances().get(0);
        assertThat(HeapWalker.valueOf(dummyS, "cyrillicString")).isEqualTo(proto.cyrillicString);
    }

    @Test
    public void verify_string_decoding_unicode() {
        Heap heap = getHeap();
        JavaClass jclass = heap.getJavaClassByName(DummyS.class.getName());
        DummyS proto = new DummyS();
        Instance dummyS = jclass.getInstances().get(0);
        assertThat(HeapWalker.valueOf(dummyS, "unicodeString")).isEqualTo(proto.unicodeString);
    }
}

