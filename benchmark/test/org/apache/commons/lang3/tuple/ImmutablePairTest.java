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
 * Test the Pair class.
 */
public class ImmutablePairTest {
    @Test
    public void testBasic() {
        final ImmutablePair<Integer, String> pair = new ImmutablePair<>(0, "foo");
        Assertions.assertEquals(0, pair.left.intValue());
        Assertions.assertEquals(0, pair.getLeft().intValue());
        Assertions.assertEquals("foo", pair.right);
        Assertions.assertEquals("foo", pair.getRight());
        final ImmutablePair<Object, String> pair2 = new ImmutablePair<>(null, "bar");
        Assertions.assertNull(pair2.left);
        Assertions.assertNull(pair2.getLeft());
        Assertions.assertEquals("bar", pair2.right);
        Assertions.assertEquals("bar", pair2.getRight());
    }

    @Test
    public void testPairOf() {
        final ImmutablePair<Integer, String> pair = ImmutablePair.of(0, "foo");
        Assertions.assertEquals(0, pair.left.intValue());
        Assertions.assertEquals(0, pair.getLeft().intValue());
        Assertions.assertEquals("foo", pair.right);
        Assertions.assertEquals("foo", pair.getRight());
        final ImmutablePair<Object, String> pair2 = ImmutablePair.of(null, "bar");
        Assertions.assertNull(pair2.left);
        Assertions.assertNull(pair2.getLeft());
        Assertions.assertEquals("bar", pair2.right);
        Assertions.assertEquals("bar", pair2.getRight());
    }

    @Test
    public void testEquals() {
        Assertions.assertEquals(ImmutablePair.of(null, "foo"), ImmutablePair.of(null, "foo"));
        Assertions.assertNotEquals(ImmutablePair.of("foo", 0), ImmutablePair.of("foo", null));
        Assertions.assertNotEquals(ImmutablePair.of("foo", "bar"), ImmutablePair.of("xyz", "bar"));
        final ImmutablePair<String, String> p = ImmutablePair.of("foo", "bar");
        Assertions.assertEquals(p, p);
        Assertions.assertNotEquals(p, new Object());
    }

    @Test
    public void testHashCode() {
        Assertions.assertEquals(ImmutablePair.of(null, "foo").hashCode(), ImmutablePair.of(null, "foo").hashCode());
    }

    @Test
    public void testNullPairEquals() {
        Assertions.assertEquals(ImmutablePair.nullPair(), ImmutablePair.nullPair());
    }

    @Test
    public void testNullPairSame() {
        Assertions.assertSame(ImmutablePair.nullPair(), ImmutablePair.nullPair());
    }

    @Test
    public void testNullPairLeft() {
        Assertions.assertNull(ImmutablePair.nullPair().getLeft());
    }

    @Test
    public void testNullPairKey() {
        Assertions.assertNull(ImmutablePair.nullPair().getKey());
    }

    @Test
    public void testNullPairRight() {
        Assertions.assertNull(ImmutablePair.nullPair().getRight());
    }

    @Test
    public void testNullPairValue() {
        Assertions.assertNull(ImmutablePair.nullPair().getValue());
    }

    @Test
    public void testNullPairTyped() {
        // No compiler warnings
        // How do we assert that?
        final ImmutablePair<String, String> pair = ImmutablePair.nullPair();
        Assertions.assertNotNull(pair);
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("(null,null)", ImmutablePair.of(null, null).toString());
        Assertions.assertEquals("(null,two)", ImmutablePair.of(null, "two").toString());
        Assertions.assertEquals("(one,null)", ImmutablePair.of("one", null).toString());
        Assertions.assertEquals("(one,two)", ImmutablePair.of("one", "two").toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialization() throws Exception {
        final ImmutablePair<Integer, String> origPair = ImmutablePair.of(0, "foo");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(origPair);
        final ImmutablePair<Integer, String> deserializedPair = ((ImmutablePair<Integer, String>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Assertions.assertEquals(origPair, deserializedPair);
        Assertions.assertEquals(origPair.hashCode(), deserializedPair.hashCode());
    }

    @Test
    public void testUseAsKeyOfHashMap() {
        HashMap<ImmutablePair<Object, Object>, String> map = new HashMap<>();
        Object o1 = new Object();
        Object o2 = new Object();
        ImmutablePair<Object, Object> key1 = ImmutablePair.of(o1, o2);
        String value1 = "a1";
        map.put(key1, value1);
        Assertions.assertEquals(value1, map.get(key1));
        Assertions.assertEquals(value1, map.get(ImmutablePair.of(o1, o2)));
    }

    @Test
    public void testUseAsKeyOfTreeMap() {
        TreeMap<ImmutablePair<Integer, Integer>, String> map = new TreeMap<>();
        map.put(ImmutablePair.of(1, 2), "12");
        map.put(ImmutablePair.of(1, 1), "11");
        map.put(ImmutablePair.of(0, 1), "01");
        ArrayList<ImmutablePair<Integer, Integer>> expected = new ArrayList<>();
        expected.add(ImmutablePair.of(0, 1));
        expected.add(ImmutablePair.of(1, 1));
        expected.add(ImmutablePair.of(1, 2));
        Iterator<Map.Entry<ImmutablePair<Integer, Integer>, String>> it = map.entrySet().iterator();
        for (ImmutablePair<Integer, Integer> item : expected) {
            Map.Entry<ImmutablePair<Integer, Integer>, String> entry = it.next();
            Assertions.assertEquals(item, entry.getKey());
            Assertions.assertEquals((((item.getLeft()) + "") + (item.getRight())), entry.getValue());
        }
    }
}

