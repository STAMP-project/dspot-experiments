/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;


import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class NonParcelTest {
    @Test
    public void testCharacter() {
        Character input = 'A';
        Character output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input, output);
    }

    @Test
    public void testBoolean() {
        Boolean input = true;
        Boolean output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input, output);
    }

    @Test
    public void testByteArray() {
        byte[] input = "test".getBytes();
        byte[] output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertArrayEquals(input, output);
    }

    @Test
    public void testCharArray() {
        char[] input = "test".toCharArray();
        char[] output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertArrayEquals(input, output);
    }

    @Test
    public void testBooleanArray() {
        boolean[] input = new boolean[]{ true, false, false, true };
        boolean[] output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(input.length, output.length);
        for (int i = 0; i < (input.length); i++) {
            Assert.assertEquals(input[i], output[i]);
        }
    }

    @Test
    public void testUnmodifiableList() {
        List<SubParcel> input = new ArrayList<SubParcel>();
        input.add(new SubParcel("name"));
        input.add(null);
        List<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(List.class, Collections.unmodifiableList(input)));
        Assert.assertEquals(2, output.size());
        Assert.assertEquals("name", output.get(0).getName());
        Assert.assertNull(output.get(1));
    }

    @Test
    public void testSublist() {
        List<SubParcel> input = new ArrayList<SubParcel>();
        input.add(new SubParcel("one"));
        input.add(new SubParcel("two"));
        input.add(new SubParcel("three"));
        input.add(null);
        List<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(List.class, input.subList(0, 2)));
        Assert.assertEquals(2, output.size());
        Assert.assertEquals("one", output.get(0).getName());
        Assert.assertEquals("two", output.get(1).getName());
    }

    @Test
    public void testList() {
        List<SubParcel> input = new ArrayList<SubParcel>();
        input.add(new SubParcel("name"));
        input.add(null);
        ArrayList<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(2, output.size());
        Assert.assertEquals("name", output.get(0).getName());
        Assert.assertNull(output.get(1));
    }

    @Test
    public void testLinkedList() {
        List<SubParcel> input = new LinkedList<SubParcel>();
        input.add(new SubParcel("name"));
        input.add(null);
        LinkedList<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(2, output.size());
        Assert.assertEquals("name", output.get(0).getName());
        Assert.assertNull(output.get(1));
    }

    @Test
    public void testSet() {
        Set<SubParcel> input = new HashSet<SubParcel>();
        SubParcel subParcel = new SubParcel("name");
        input.add(subParcel);
        input.add(null);
        HashSet<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(2, output.size());
        Assert.assertTrue(output.contains(subParcel));
        Assert.assertTrue(output.contains(null));
    }

    @Test
    public void testTreeSet() {
        Set<SubParcel> input = new TreeSet<SubParcel>();
        input.add(new SubParcel("one"));
        input.add(new SubParcel("two"));
        input.add(new SubParcel("three"));
        TreeSet<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(3, output.size());
        Assert.assertTrue(output.contains(new SubParcel("one")));
        Assert.assertTrue(output.contains(new SubParcel("two")));
        Assert.assertTrue(output.contains(new SubParcel("three")));
    }

    @Test
    public void testSortedSet() {
        SortedSet<SubParcel> input = new TreeSet<SubParcel>();
        input.add(new SubParcel("one"));
        input.add(new SubParcel("two"));
        input.add(new SubParcel("three"));
        SortedSet<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(SortedSet.class, input));
        Assert.assertEquals(3, output.size());
        Assert.assertTrue(output.contains(new SubParcel("one")));
        Assert.assertTrue(output.contains(new SubParcel("two")));
        Assert.assertTrue(output.contains(new SubParcel("three")));
    }

    @Test
    public void testLinkedHashSet() {
        Set<SubParcel> input = new LinkedHashSet<SubParcel>();
        SubParcel subParcel = new SubParcel("name");
        input.add(subParcel);
        input.add(null);
        LinkedHashSet<SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(2, output.size());
        Assert.assertTrue(output.contains(subParcel));
        Assert.assertTrue(output.contains(null));
    }

    @Test
    public void testMap() {
        Map<SubParcel, SubParcel> input = new HashMap<SubParcel, SubParcel>();
        SubParcel key1 = new SubParcel("key");
        SubParcel key2 = new SubParcel("key2");
        input.put(key1, new SubParcel("name"));
        input.put(null, new SubParcel("null"));
        input.put(key2, null);
        Map<SubParcel, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(3, output.size());
        Assert.assertEquals("name", output.get(key1).getName());
        Assert.assertEquals("null", output.get(null).getName());
        Assert.assertNull(output.get(key2));
    }

    @Test
    public void testLinkedHashMap() {
        Map<SubParcel, SubParcel> input = new LinkedHashMap<SubParcel, SubParcel>();
        SubParcel key1 = new SubParcel("key");
        SubParcel key2 = new SubParcel("key2");
        input.put(key1, new SubParcel("name"));
        input.put(null, new SubParcel("null"));
        input.put(key2, null);
        LinkedHashMap<SubParcel, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(3, output.size());
        Assert.assertEquals("name", output.get(key1).getName());
        Assert.assertEquals("null", output.get(null).getName());
        Assert.assertNull(output.get(key2));
    }

    @Test
    public void testSortedMap() {
        SortedMap<String, SubParcel> input = new TreeMap<String, SubParcel>();
        input.put("1", new SubParcel("name"));
        input.put("2", new SubParcel("null"));
        input.put("3", null);
        SortedMap<String, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(SortedMap.class, input));
        Assert.assertEquals(3, output.size());
        Assert.assertEquals("name", output.get("1").getName());
        Assert.assertEquals("null", output.get("2").getName());
        Assert.assertNull(output.get("3"));
    }

    @Test
    public void testPrimitiveKeyMap() {
        Map<Integer, SubParcel> input = new HashMap<Integer, SubParcel>();
        Integer key1 = 1;
        Integer key2 = 2;
        input.put(key1, new SubParcel("name"));
        input.put(null, new SubParcel("null"));
        input.put(key2, null);
        HashMap<Integer, SubParcel> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(3, output.size());
        Assert.assertEquals("name", output.get(key1).getName());
        Assert.assertEquals("null", output.get(null).getName());
        Assert.assertNull(output.get(key2));
    }

    @Test
    public void testPrimitiveValueMap() {
        Map<SubParcel, Integer> input = new HashMap<SubParcel, Integer>();
        Integer value = 42;
        Integer value2 = 43;
        SubParcel subParcel = new SubParcel("name");
        SubParcel nullParcel = new SubParcel("null");
        input.put(subParcel, value);
        input.put(nullParcel, null);
        input.put(null, value2);
        Map<SubParcel, Integer> output = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(3, output.size());
        Assert.assertEquals(value, output.get(subParcel));
        Assert.assertEquals(value2, output.get(null));
        Assert.assertNull(output.get(nullParcel));
    }

    @Test
    public void testSparseArray() {
        SparseArray<SubParcel> input = new SparseArray<SubParcel>();
        input.append(1, new SubParcel("name"));
        input.append(2, null);
        SparseArray<SubParcel> exampleSparseArray = Parcels.unwrap(ParcelsTestUtil.wrap(input));
        Assert.assertEquals(2, exampleSparseArray.size());
        SubParcel output = exampleSparseArray.get(1);
        Assert.assertEquals("name", output.getName());
        Assert.assertNull(exampleSparseArray.get(2));
    }

    @Test
    public void testInteger() {
        Integer integerOuput = Parcels.unwrap(ParcelsTestUtil.wrap(42));
        Integer integerNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals(Integer.valueOf(42), integerOuput);
        Assert.assertNull(integerNullOuput);
    }

    @Test
    public void testLong() {
        Long longOuput = Parcels.unwrap(ParcelsTestUtil.wrap(42L));
        Long longNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals(Long.valueOf(42L), longOuput);
        Assert.assertNull(longNullOuput);
    }

    @Test
    public void testDouble() {
        Double doubleOutput = Parcels.unwrap(ParcelsTestUtil.wrap(42.42));
        Double doubleNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals(Double.valueOf(42.42), doubleOutput);
        Assert.assertNull(doubleNullOuput);
    }

    @Test
    public void testByte() {
        Byte byteOutput = Parcels.unwrap(ParcelsTestUtil.wrap(((byte) (66))));
        Byte byteNullOuput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals(Byte.valueOf(((byte) (66))), byteOutput);
        Assert.assertNull(byteNullOuput);
    }

    @Test
    public void testFloat() {
        Float floatOutput = Parcels.unwrap(ParcelsTestUtil.wrap(42.42F));
        Float floatNullOutput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals(Float.valueOf(42.42F), floatOutput);
        Assert.assertNull(floatNullOutput);
    }

    @Test
    public void testString() {
        String stringOutput = Parcels.unwrap(ParcelsTestUtil.wrap("42"));
        String stringNullOutput = Parcels.unwrap(ParcelsTestUtil.wrap(null));
        Assert.assertEquals("42", stringOutput);
        Assert.assertNull(stringNullOutput);
    }
}

