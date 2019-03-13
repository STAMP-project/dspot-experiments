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
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.MoreAsserts;
import com.google.gson.common.TestTypes;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import junit.framework.TestCase;


/**
 * Functional tests for Json serialization and deserialization of collections.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class CollectionTest extends TestCase {
    private Gson gson;

    public void testTopLevelCollectionOfIntegersSerialization() {
        Collection<Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Type targetType = new TypeToken<Collection<Integer>>() {}.getType();
        String json = gson.toJson(target, targetType);
        TestCase.assertEquals("[1,2,3,4,5,6,7,8,9]", json);
    }

    public void testTopLevelCollectionOfIntegersDeserialization() {
        String json = "[0,1,2,3,4,5,6,7,8,9]";
        Type collectionType = new TypeToken<Collection<Integer>>() {}.getType();
        Collection<Integer> target = gson.fromJson(json, collectionType);
        int[] expected = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        MoreAsserts.assertEquals(expected, CollectionTest.toIntArray(target));
    }

    public void testTopLevelListOfIntegerCollectionsDeserialization() throws Exception {
        String json = "[[1,2,3],[4,5,6],[7,8,9]]";
        Type collectionType = new TypeToken<Collection<Collection<Integer>>>() {}.getType();
        List<Collection<Integer>> target = gson.fromJson(json, collectionType);
        int[][] expected = new int[3][3];
        for (int i = 0; i < 3; ++i) {
            int start = (3 * i) + 1;
            for (int j = 0; j < 3; ++j) {
                expected[i][j] = start + j;
            }
        }
        for (int i = 0; i < 3; i++) {
            MoreAsserts.assertEquals(expected[i], CollectionTest.toIntArray(target.get(i)));
        }
    }

    public void testLinkedListSerialization() {
        List<String> list = new LinkedList<String>();
        list.add("a1");
        list.add("a2");
        Type linkedListType = new TypeToken<LinkedList<String>>() {}.getType();
        String json = gson.toJson(list, linkedListType);
        TestCase.assertTrue(json.contains("a1"));
        TestCase.assertTrue(json.contains("a2"));
    }

    public void testLinkedListDeserialization() {
        String json = "['a1','a2']";
        Type linkedListType = new TypeToken<LinkedList<String>>() {}.getType();
        List<String> list = gson.fromJson(json, linkedListType);
        TestCase.assertEquals("a1", list.get(0));
        TestCase.assertEquals("a2", list.get(1));
    }

    public void testQueueSerialization() {
        Queue<String> queue = new LinkedList<String>();
        queue.add("a1");
        queue.add("a2");
        Type queueType = new TypeToken<Queue<String>>() {}.getType();
        String json = gson.toJson(queue, queueType);
        TestCase.assertTrue(json.contains("a1"));
        TestCase.assertTrue(json.contains("a2"));
    }

    public void testQueueDeserialization() {
        String json = "['a1','a2']";
        Type queueType = new TypeToken<Queue<String>>() {}.getType();
        Queue<String> queue = gson.fromJson(json, queueType);
        TestCase.assertEquals("a1", queue.element());
        queue.remove();
        TestCase.assertEquals("a2", queue.element());
    }

    public void testPriorityQueue() throws Exception {
        Type type = new TypeToken<PriorityQueue<Integer>>() {}.getType();
        PriorityQueue<Integer> queue = gson.fromJson("[10, 20, 22]", type);
        TestCase.assertEquals(3, queue.size());
        String json = gson.toJson(queue);
        TestCase.assertEquals(10, queue.remove().intValue());
        TestCase.assertEquals(20, queue.remove().intValue());
        TestCase.assertEquals(22, queue.remove().intValue());
        TestCase.assertEquals("[10,20,22]", json);
    }

    public void testVector() {
        Type type = new TypeToken<Vector<Integer>>() {}.getType();
        Vector<Integer> target = gson.fromJson("[10, 20, 31]", type);
        TestCase.assertEquals(3, target.size());
        TestCase.assertEquals(10, target.get(0).intValue());
        TestCase.assertEquals(20, target.get(1).intValue());
        TestCase.assertEquals(31, target.get(2).intValue());
        String json = gson.toJson(target);
        TestCase.assertEquals("[10,20,31]", json);
    }

    public void testStack() {
        Type type = new TypeToken<Stack<Integer>>() {}.getType();
        Stack<Integer> target = gson.fromJson("[11, 13, 17]", type);
        TestCase.assertEquals(3, target.size());
        String json = gson.toJson(target);
        TestCase.assertEquals(17, target.pop().intValue());
        TestCase.assertEquals(13, target.pop().intValue());
        TestCase.assertEquals(11, target.pop().intValue());
        TestCase.assertEquals("[11,13,17]", json);
    }

    public void testNullsInListSerialization() {
        List<String> list = new ArrayList<String>();
        list.add("foo");
        list.add(null);
        list.add("bar");
        String expected = "[\"foo\",null,\"bar\"]";
        Type typeOfList = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(list, typeOfList);
        TestCase.assertEquals(expected, json);
    }

    public void testNullsInListDeserialization() {
        List<String> expected = new ArrayList<String>();
        expected.add("foo");
        expected.add(null);
        expected.add("bar");
        String json = "[\"foo\",null,\"bar\"]";
        Type expectedType = new TypeToken<List<String>>() {}.getType();
        List<String> target = gson.fromJson(json, expectedType);
        for (int i = 0; i < (expected.size()); ++i) {
            TestCase.assertEquals(expected.get(i), target.get(i));
        }
    }

    public void testCollectionOfObjectSerialization() {
        List<Object> target = new ArrayList<Object>();
        target.add("Hello");
        target.add("World");
        TestCase.assertEquals("[\"Hello\",\"World\"]", gson.toJson(target));
        Type type = new TypeToken<List<Object>>() {}.getType();
        TestCase.assertEquals("[\"Hello\",\"World\"]", gson.toJson(target, type));
    }

    public void testCollectionOfObjectWithNullSerialization() {
        List<Object> target = new ArrayList<Object>();
        target.add("Hello");
        target.add(null);
        target.add("World");
        TestCase.assertEquals("[\"Hello\",null,\"World\"]", gson.toJson(target));
        Type type = new TypeToken<List<Object>>() {}.getType();
        TestCase.assertEquals("[\"Hello\",null,\"World\"]", gson.toJson(target, type));
    }

    public void testCollectionOfStringsSerialization() {
        List<String> target = new ArrayList<String>();
        target.add("Hello");
        target.add("World");
        TestCase.assertEquals("[\"Hello\",\"World\"]", gson.toJson(target));
    }

    public void testCollectionOfBagOfPrimitivesSerialization() {
        List<TestTypes.BagOfPrimitives> target = new ArrayList<TestTypes.BagOfPrimitives>();
        TestTypes.BagOfPrimitives objA = new TestTypes.BagOfPrimitives(3L, 1, true, "blah");
        TestTypes.BagOfPrimitives objB = new TestTypes.BagOfPrimitives(2L, 6, false, "blahB");
        target.add(objA);
        target.add(objB);
        String result = gson.toJson(target);
        TestCase.assertTrue(result.startsWith("["));
        TestCase.assertTrue(result.endsWith("]"));
        for (TestTypes.BagOfPrimitives obj : target) {
            TestCase.assertTrue(result.contains(obj.getExpectedJson()));
        }
    }

    public void testCollectionOfStringsDeserialization() {
        String json = "[\"Hello\",\"World\"]";
        Type collectionType = new TypeToken<Collection<String>>() {}.getType();
        Collection<String> target = gson.fromJson(json, collectionType);
        TestCase.assertTrue(target.contains("Hello"));
        TestCase.assertTrue(target.contains("World"));
    }

    public void testRawCollectionOfIntegersSerialization() {
        Collection<Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        TestCase.assertEquals("[1,2,3,4,5,6,7,8,9]", gson.toJson(target));
    }

    public void testWildcardPrimitiveCollectionSerilaization() throws Exception {
        Collection<? extends Integer> target = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Type collectionType = new TypeToken<Collection<? extends Integer>>() {}.getType();
        String json = gson.toJson(target, collectionType);
        TestCase.assertEquals("[1,2,3,4,5,6,7,8,9]", json);
        json = gson.toJson(target);
        TestCase.assertEquals("[1,2,3,4,5,6,7,8,9]", json);
    }

    public void testWildcardPrimitiveCollectionDeserilaization() throws Exception {
        String json = "[1,2,3,4,5,6,7,8,9]";
        Type collectionType = new TypeToken<Collection<? extends Integer>>() {}.getType();
        Collection<? extends Integer> target = gson.fromJson(json, collectionType);
        TestCase.assertEquals(9, target.size());
        TestCase.assertTrue(target.contains(1));
        TestCase.assertTrue(target.contains(9));
    }

    public void testWildcardCollectionField() throws Exception {
        Collection<TestTypes.BagOfPrimitives> collection = new ArrayList<TestTypes.BagOfPrimitives>();
        TestTypes.BagOfPrimitives objA = new TestTypes.BagOfPrimitives(3L, 1, true, "blah");
        TestTypes.BagOfPrimitives objB = new TestTypes.BagOfPrimitives(2L, 6, false, "blahB");
        collection.add(objA);
        collection.add(objB);
        CollectionTest.ObjectWithWildcardCollection target = new CollectionTest.ObjectWithWildcardCollection(collection);
        String json = gson.toJson(target);
        TestCase.assertTrue(json.contains(objA.getExpectedJson()));
        TestCase.assertTrue(json.contains(objB.getExpectedJson()));
        target = gson.fromJson(json, CollectionTest.ObjectWithWildcardCollection.class);
        Collection<? extends TestTypes.BagOfPrimitives> deserializedCollection = target.getCollection();
        TestCase.assertEquals(2, deserializedCollection.size());
        TestCase.assertTrue(deserializedCollection.contains(objA));
        TestCase.assertTrue(deserializedCollection.contains(objB));
    }

    public void testFieldIsArrayList() {
        CollectionTest.HasArrayListField object = new CollectionTest.HasArrayListField();
        object.longs.add(1L);
        object.longs.add(3L);
        String json = gson.toJson(object, CollectionTest.HasArrayListField.class);
        TestCase.assertEquals("{\"longs\":[1,3]}", json);
        CollectionTest.HasArrayListField copy = gson.fromJson("{\"longs\":[1,3]}", CollectionTest.HasArrayListField.class);
        TestCase.assertEquals(Arrays.asList(1L, 3L), copy.longs);
    }

    public void testUserCollectionTypeAdapter() {
        Type listOfString = new TypeToken<List<String>>() {}.getType();
        Object stringListSerializer = new JsonSerializer<List<String>>() {
            public JsonElement serialize(List<String> src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive((((src.get(0)) + ";") + (src.get(1))));
            }
        };
        Gson gson = new GsonBuilder().registerTypeAdapter(listOfString, stringListSerializer).create();
        TestCase.assertEquals("\"ab;cd\"", gson.toJson(Arrays.asList("ab", "cd"), listOfString));
    }

    static class HasArrayListField {
        ArrayList<Long> longs = new ArrayList<Long>();
    }

    private static class ObjectWithWildcardCollection {
        private final Collection<? extends TestTypes.BagOfPrimitives> collection;

        public ObjectWithWildcardCollection(Collection<? extends TestTypes.BagOfPrimitives> collection) {
            this.collection = collection;
        }

        public Collection<? extends TestTypes.BagOfPrimitives> getCollection() {
            return collection;
        }
    }

    private static class Entry {
        int value;

        Entry(int value) {
            this.value = value;
        }
    }

    public void testSetSerialization() {
        Set<CollectionTest.Entry> set = new HashSet<CollectionTest.Entry>();
        set.add(new CollectionTest.Entry(1));
        set.add(new CollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertTrue(json.contains("1"));
        TestCase.assertTrue(json.contains("2"));
    }

    public void testSetDeserialization() {
        String json = "[{value:1},{value:2}]";
        Type type = new TypeToken<Set<CollectionTest.Entry>>() {}.getType();
        Set<CollectionTest.Entry> set = gson.fromJson(json, type);
        TestCase.assertEquals(2, set.size());
        for (CollectionTest.Entry entry : set) {
            TestCase.assertTrue((((entry.value) == 1) || ((entry.value) == 2)));
        }
    }

    private class BigClass {
        private Map<String, ? extends List<CollectionTest.SmallClass>> inBig;
    }

    private class SmallClass {
        private String inSmall;
    }

    public void testIssue1107() {
        String json = "{\n" + ((((("  \"inBig\": {\n" + "    \"key\": [\n") + "      { \"inSmall\": \"hello\" }\n") + "    ]\n") + "  }\n") + "}");
        CollectionTest.BigClass bigClass = new Gson().fromJson(json, CollectionTest.BigClass.class);
        CollectionTest.SmallClass small = bigClass.inBig.get("key").get(0);
        TestCase.assertNotNull(small);
        TestCase.assertEquals("hello", small.inSmall);
    }
}

