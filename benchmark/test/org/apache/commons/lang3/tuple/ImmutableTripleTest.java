/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.tuple;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Test the Triple class.
 */
public class ImmutableTripleTest {
    @Test
    public void testBasic() {
        final ImmutableTriple<Integer, String, Boolean> triple = new ImmutableTriple<>(0, "foo", Boolean.TRUE);
        Assertions.assertEquals(0, triple.left.intValue());
        Assertions.assertEquals(0, triple.getLeft().intValue());
        Assertions.assertEquals("foo", triple.middle);
        Assertions.assertEquals("foo", triple.getMiddle());
        Assertions.assertEquals(Boolean.TRUE, triple.right);
        Assertions.assertEquals(Boolean.TRUE, triple.getRight());
        final ImmutableTriple<Object, String, Integer> triple2 = new ImmutableTriple<>(null, "bar", 42);
        Assertions.assertNull(triple2.left);
        Assertions.assertNull(triple2.getLeft());
        Assertions.assertEquals("bar", triple2.middle);
        Assertions.assertEquals("bar", triple2.getMiddle());
        Assertions.assertEquals(new Integer(42), triple2.right);
        Assertions.assertEquals(new Integer(42), triple2.getRight());
    }

    @Test
    public void testTripleOf() {
        final ImmutableTriple<Integer, String, Boolean> triple = ImmutableTriple.of(0, "foo", Boolean.FALSE);
        Assertions.assertEquals(0, triple.left.intValue());
        Assertions.assertEquals(0, triple.getLeft().intValue());
        Assertions.assertEquals("foo", triple.middle);
        Assertions.assertEquals("foo", triple.getMiddle());
        Assertions.assertEquals(Boolean.FALSE, triple.right);
        Assertions.assertEquals(Boolean.FALSE, triple.getRight());
        final ImmutableTriple<Object, String, Boolean> triple2 = ImmutableTriple.of(null, "bar", Boolean.TRUE);
        Assertions.assertNull(triple2.left);
        Assertions.assertNull(triple2.getLeft());
        Assertions.assertEquals("bar", triple2.middle);
        Assertions.assertEquals("bar", triple2.getMiddle());
        Assertions.assertEquals(Boolean.TRUE, triple2.right);
        Assertions.assertEquals(Boolean.TRUE, triple2.getRight());
    }

    @Test
    public void testEquals() {
        Assertions.assertEquals(ImmutableTriple.of(null, "foo", 42), ImmutableTriple.of(null, "foo", 42));
        Assertions.assertNotEquals(ImmutableTriple.of("foo", 0, Boolean.TRUE), ImmutableTriple.of("foo", null, null));
        Assertions.assertNotEquals(ImmutableTriple.of("foo", "bar", "baz"), ImmutableTriple.of("xyz", "bar", "blo"));
        final ImmutableTriple<String, String, String> p = ImmutableTriple.of("foo", "bar", "baz");
        Assertions.assertEquals(p, p);
        Assertions.assertNotEquals(p, new Object());
    }

    @Test
    public void testHashCode() {
        Assertions.assertEquals(ImmutableTriple.of(null, "foo", Boolean.TRUE).hashCode(), ImmutableTriple.of(null, "foo", Boolean.TRUE).hashCode());
    }

    @Test
    public void testNullTripleEquals() {
        Assertions.assertEquals(ImmutableTriple.nullTriple(), ImmutableTriple.nullTriple());
    }

    @Test
    public void testNullTripleSame() {
        Assertions.assertSame(ImmutableTriple.nullTriple(), ImmutableTriple.nullTriple());
    }

    @Test
    public void testNullTripleLeft() {
        Assertions.assertNull(ImmutableTriple.nullTriple().getLeft());
    }

    @Test
    public void testNullTripleMiddle() {
        Assertions.assertNull(ImmutableTriple.nullTriple().getMiddle());
    }

    @Test
    public void testNullTripleRight() {
        Assertions.assertNull(ImmutableTriple.nullTriple().getRight());
    }

    @Test
    public void testNullTripleTyped() {
        // No compiler warnings
        // How do we assert that?
        final ImmutableTriple<String, String, String> triple = ImmutableTriple.nullTriple();
        Assertions.assertNotNull(triple);
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("(null,null,null)", ImmutableTriple.of(null, null, null).toString());
        Assertions.assertEquals("(null,two,null)", ImmutableTriple.of(null, "two", null).toString());
        Assertions.assertEquals("(one,null,null)", ImmutableTriple.of("one", null, null).toString());
        Assertions.assertEquals("(one,two,null)", ImmutableTriple.of("one", "two", null).toString());
        Assertions.assertEquals("(null,two,three)", ImmutableTriple.of(null, "two", "three").toString());
        Assertions.assertEquals("(one,null,three)", ImmutableTriple.of("one", null, "three").toString());
        Assertions.assertEquals("(one,two,three)", MutableTriple.of("one", "two", "three").toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialization() throws Exception {
        final ImmutableTriple<Integer, String, Boolean> origTriple = ImmutableTriple.of(0, "foo", Boolean.TRUE);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(origTriple);
        final ImmutableTriple<Integer, String, Boolean> deserializedTriple = ((ImmutableTriple<Integer, String, Boolean>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Assertions.assertEquals(origTriple, deserializedTriple);
        Assertions.assertEquals(origTriple.hashCode(), deserializedTriple.hashCode());
    }

    @Test
    public void testUseAsKeyOfHashMap() {
        HashMap<ImmutableTriple<Object, Object, Object>, String> map = new HashMap<>();
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        ImmutableTriple<Object, Object, Object> key1 = ImmutableTriple.of(o1, o2, o3);
        String value1 = "a1";
        map.put(key1, value1);
        Assertions.assertEquals(value1, map.get(key1));
        Assertions.assertEquals(value1, map.get(ImmutableTriple.of(o1, o2, o3)));
    }

    @Test
    public void testUseAsKeyOfTreeMap() {
        TreeMap<ImmutableTriple<Integer, Integer, Integer>, String> map = new TreeMap<>();
        map.put(ImmutableTriple.of(0, 1, 2), "012");
        map.put(ImmutableTriple.of(0, 1, 1), "011");
        map.put(ImmutableTriple.of(0, 0, 1), "001");
        ArrayList<ImmutableTriple<Integer, Integer, Integer>> expected = new ArrayList<>();
        expected.add(ImmutableTriple.of(0, 0, 1));
        expected.add(ImmutableTriple.of(0, 1, 1));
        expected.add(ImmutableTriple.of(0, 1, 2));
        Iterator<Map.Entry<ImmutableTriple<Integer, Integer, Integer>, String>> it = map.entrySet().iterator();
        for (ImmutableTriple<Integer, Integer, Integer> item : expected) {
            Map.Entry<ImmutableTriple<Integer, Integer, Integer>, String> entry = it.next();
            Assertions.assertEquals(item, entry.getKey());
            Assertions.assertEquals((((((item.getLeft()) + "") + (item.getMiddle())) + "") + (item.getRight())), entry.getValue());
        }
    }
}

