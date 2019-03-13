/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.common.MoreAsserts;
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import junit.framework.TestCase;


/**
 * Functional tests for Json serialization and deserialization of arrays.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ArrayTest extends TestCase {
    private Gson gson;

    public void testTopLevelArrayOfIntsSerialization() {
        int[] target = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        TestCase.assertEquals("[1,2,3,4,5,6,7,8,9]", gson.toJson(target));
    }

    public void testTopLevelArrayOfIntsDeserialization() {
        int[] expected = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] actual = gson.fromJson("[1,2,3,4,5,6,7,8,9]", int[].class);
        MoreAsserts.assertEquals(expected, actual);
    }

    public void testInvalidArrayDeserialization() {
        String json = "[1, 2 3, 4, 5]";
        try {
            gson.fromJson(json, int[].class);
            TestCase.fail("Gson should not deserialize array elements with missing ,");
        } catch (JsonParseException expected) {
        }
    }

    public void testEmptyArraySerialization() {
        int[] target = new int[]{  };
        TestCase.assertEquals("[]", gson.toJson(target));
    }

    public void testEmptyArrayDeserialization() {
        int[] actualObject = gson.fromJson("[]", int[].class);
        TestCase.assertTrue(((actualObject.length) == 0));
        Integer[] actualObject2 = gson.fromJson("[]", Integer[].class);
        TestCase.assertTrue(((actualObject2.length) == 0));
        actualObject = gson.fromJson("[ ]", int[].class);
        TestCase.assertTrue(((actualObject.length) == 0));
    }

    public void testNullsInArraySerialization() {
        String[] array = new String[]{ "foo", null, "bar" };
        String expected = "[\"foo\",null,\"bar\"]";
        String json = gson.toJson(array);
        TestCase.assertEquals(expected, json);
    }

    public void testNullsInArrayDeserialization() {
        String json = "[\"foo\",null,\"bar\"]";
        String[] expected = new String[]{ "foo", null, "bar" };
        String[] target = gson.fromJson(json, expected.getClass());
        for (int i = 0; i < (expected.length); ++i) {
            TestCase.assertEquals(expected[i], target[i]);
        }
    }

    public void testSingleNullInArraySerialization() {
        TestTypes.BagOfPrimitives[] array = new TestTypes.BagOfPrimitives[1];
        array[0] = null;
        String json = gson.toJson(array);
        TestCase.assertEquals("[null]", json);
    }

    public void testSingleNullInArrayDeserialization() {
        TestTypes.BagOfPrimitives[] array = gson.fromJson("[null]", TestTypes.BagOfPrimitives[].class);
        TestCase.assertNull(array[0]);
    }

    public void testNullsInArrayWithSerializeNullPropertySetSerialization() {
        gson = new GsonBuilder().serializeNulls().create();
        String[] array = new String[]{ "foo", null, "bar" };
        String expected = "[\"foo\",null,\"bar\"]";
        String json = gson.toJson(array);
        TestCase.assertEquals(expected, json);
    }

    public void testArrayOfStringsSerialization() {
        String[] target = new String[]{ "Hello", "World" };
        TestCase.assertEquals("[\"Hello\",\"World\"]", gson.toJson(target));
    }

    public void testArrayOfStringsDeserialization() {
        String json = "[\"Hello\",\"World\"]";
        String[] target = gson.fromJson(json, String[].class);
        TestCase.assertEquals("Hello", target[0]);
        TestCase.assertEquals("World", target[1]);
    }

    public void testSingleStringArraySerialization() throws Exception {
        String[] s = new String[]{ "hello" };
        String output = gson.toJson(s);
        TestCase.assertEquals("[\"hello\"]", output);
    }

    public void testSingleStringArrayDeserialization() throws Exception {
        String json = "[\"hello\"]";
        String[] arrayType = gson.fromJson(json, String[].class);
        TestCase.assertEquals(1, arrayType.length);
        TestCase.assertEquals("hello", arrayType[0]);
    }

    public void testArrayOfCollectionDeserialization() throws Exception {
        String json = "[[1,2],[3,4]]";
        Type type = new TypeToken<Collection<Integer>[]>() {}.getType();
        Collection<Integer>[] target = gson.fromJson(json, type);
        TestCase.assertEquals(2, target.length);
        MoreAsserts.assertEquals(new Integer[]{ 1, 2 }, target[0].toArray(new Integer[0]));
        MoreAsserts.assertEquals(new Integer[]{ 3, 4 }, target[1].toArray(new Integer[0]));
    }

    public void testArrayOfPrimitivesAsObjectsSerialization() throws Exception {
        Object[] objs = new Object[]{ 1, "abc", 0.3F, 5L };
        String json = gson.toJson(objs);
        TestCase.assertTrue(json.contains("abc"));
        TestCase.assertTrue(json.contains("0.3"));
        TestCase.assertTrue(json.contains("5"));
    }

    public void testArrayOfPrimitivesAsObjectsDeserialization() throws Exception {
        String json = "[1,'abc',0.3,1.1,5]";
        Object[] objs = gson.fromJson(json, Object[].class);
        TestCase.assertEquals(1, ((Number) (objs[0])).intValue());
        TestCase.assertEquals("abc", objs[1]);
        TestCase.assertEquals(0.3, ((Number) (objs[2])).doubleValue());
        TestCase.assertEquals(new BigDecimal("1.1"), new BigDecimal(objs[3].toString()));
        TestCase.assertEquals(5, ((Number) (objs[4])).shortValue());
    }

    public void testObjectArrayWithNonPrimitivesSerialization() throws Exception {
        TestTypes.ClassWithObjects classWithObjects = new TestTypes.ClassWithObjects();
        TestTypes.BagOfPrimitives bagOfPrimitives = new TestTypes.BagOfPrimitives();
        String classWithObjectsJson = gson.toJson(classWithObjects);
        String bagOfPrimitivesJson = gson.toJson(bagOfPrimitives);
        Object[] objects = new Object[]{ classWithObjects, bagOfPrimitives };
        String json = gson.toJson(objects);
        TestCase.assertTrue(json.contains(classWithObjectsJson));
        TestCase.assertTrue(json.contains(bagOfPrimitivesJson));
    }

    public void testArrayOfNullSerialization() {
        Object[] array = new Object[]{ null };
        String json = gson.toJson(array);
        TestCase.assertEquals("[null]", json);
    }

    public void testArrayOfNullDeserialization() {
        String[] values = gson.fromJson("[null]", String[].class);
        TestCase.assertNull(values[0]);
    }

    /**
     * Regression tests for Issue 272
     */
    public void testMultidimenstionalArraysSerialization() {
        String[][] items = new String[][]{ new String[]{ "3m Co", "71.72", "0.02", "0.03", "4/2 12:00am", "Manufacturing" }, new String[]{ "Alcoa Inc", "29.01", "0.42", "1.47", "4/1 12:00am", "Manufacturing" } };
        String json = gson.toJson(items);
        TestCase.assertTrue(json.contains("[[\"3m Co"));
        TestCase.assertTrue(json.contains("Manufacturing\"]]"));
    }

    public void testMultiDimenstionalObjectArraysSerialization() {
        Object[][] array = new Object[][]{ new Object[]{ 1, 2 } };
        TestCase.assertEquals("[[1,2]]", gson.toJson(array));
    }

    /**
     * Regression test for Issue 205
     */
    public void testMixingTypesInObjectArraySerialization() {
        Object[] array = new Object[]{ 1, 2, new Object[]{ "one", "two", 3 } };
        TestCase.assertEquals("[1,2,[\"one\",\"two\",3]]", gson.toJson(array));
    }

    /**
     * Regression tests for Issue 272
     */
    public void testMultidimenstionalArraysDeserialization() {
        String json = "[['3m Co','71.72','0.02','0.03','4/2 12:00am','Manufacturing']," + "['Alcoa Inc','29.01','0.42','1.47','4/1 12:00am','Manufacturing']]";
        String[][] items = gson.fromJson(json, String[][].class);
        TestCase.assertEquals("3m Co", items[0][0]);
        TestCase.assertEquals("Manufacturing", items[1][5]);
    }

    /**
     * http://code.google.com/p/google-gson/issues/detail?id=342
     */
    public void testArrayElementsAreArrays() {
        Object[] stringArrays = new Object[]{ new String[]{ "test1", "test2" }, new String[]{ "test3", "test4" } };
        TestCase.assertEquals("[[\"test1\",\"test2\"],[\"test3\",\"test4\"]]", new Gson().toJson(stringArrays));
    }
}

