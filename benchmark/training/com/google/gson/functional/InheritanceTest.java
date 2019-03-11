/**
 * Copyright (C) 2009 Google Inc.
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.common.TestTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.TestCase;

import static com.google.gson.common.TestTypes.Base.BASE_FIELD_KEY;
import static com.google.gson.common.TestTypes.Base.BASE_NAME;
import static com.google.gson.common.TestTypes.ClassWithBaseField.FIELD_KEY;
import static com.google.gson.common.TestTypes.Sub.SUB_FIELD_KEY;
import static com.google.gson.common.TestTypes.Sub.SUB_NAME;


/**
 * Functional tests for Json serialization and deserialization of classes with
 * inheritance hierarchies.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class InheritanceTest extends TestCase {
    private Gson gson;

    public void testSubClassSerialization() throws Exception {
        InheritanceTest.SubTypeOfNested target = new InheritanceTest.SubTypeOfNested(new TestTypes.BagOfPrimitives(10, 20, false, "stringValue"), new TestTypes.BagOfPrimitives(30, 40, true, "stringValue"));
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testSubClassDeserialization() throws Exception {
        String json = "{\"value\":5,\"primitive1\":{\"longValue\":10,\"intValue\":20," + (("\"booleanValue\":false,\"stringValue\":\"stringValue\"},\"primitive2\":" + "{\"longValue\":30,\"intValue\":40,\"booleanValue\":true,") + "\"stringValue\":\"stringValue\"}}");
        InheritanceTest.SubTypeOfNested target = gson.fromJson(json, InheritanceTest.SubTypeOfNested.class);
        TestCase.assertEquals(json, target.getExpectedJson());
    }

    public void testClassWithBaseFieldSerialization() {
        TestTypes.ClassWithBaseField sub = new TestTypes.ClassWithBaseField(new TestTypes.Sub());
        JsonObject json = ((JsonObject) (gson.toJsonTree(sub)));
        JsonElement base = json.getAsJsonObject().get(FIELD_KEY);
        TestCase.assertEquals(SUB_NAME, base.getAsJsonObject().get(SUB_FIELD_KEY).getAsString());
    }

    public void testClassWithBaseArrayFieldSerialization() {
        TestTypes.Base[] baseClasses = new TestTypes.Base[]{ new TestTypes.Sub(), new TestTypes.Sub() };
        TestTypes.ClassWithBaseArrayField sub = new TestTypes.ClassWithBaseArrayField(baseClasses);
        JsonObject json = gson.toJsonTree(sub).getAsJsonObject();
        JsonArray bases = json.get(TestTypes.ClassWithBaseArrayField.FIELD_KEY).getAsJsonArray();
        for (JsonElement element : bases) {
            TestCase.assertEquals(SUB_NAME, element.getAsJsonObject().get(SUB_FIELD_KEY).getAsString());
        }
    }

    public void testClassWithBaseCollectionFieldSerialization() {
        Collection<TestTypes.Base> baseClasses = new ArrayList<TestTypes.Base>();
        baseClasses.add(new TestTypes.Sub());
        baseClasses.add(new TestTypes.Sub());
        TestTypes.ClassWithBaseCollectionField sub = new TestTypes.ClassWithBaseCollectionField(baseClasses);
        JsonObject json = gson.toJsonTree(sub).getAsJsonObject();
        JsonArray bases = json.get(TestTypes.ClassWithBaseArrayField.FIELD_KEY).getAsJsonArray();
        for (JsonElement element : bases) {
            TestCase.assertEquals(SUB_NAME, element.getAsJsonObject().get(SUB_FIELD_KEY).getAsString());
        }
    }

    public void testBaseSerializedAsSub() {
        TestTypes.Base base = new TestTypes.Sub();
        JsonObject json = gson.toJsonTree(base).getAsJsonObject();
        TestCase.assertEquals(SUB_NAME, json.get(SUB_FIELD_KEY).getAsString());
    }

    public void testBaseSerializedAsSubForToJsonMethod() {
        TestTypes.Base base = new TestTypes.Sub();
        String json = gson.toJson(base);
        TestCase.assertTrue(json.contains(SUB_NAME));
    }

    public void testBaseSerializedAsBaseWhenSpecifiedWithExplicitType() {
        TestTypes.Base base = new TestTypes.Sub();
        JsonObject json = gson.toJsonTree(base, TestTypes.Base.class).getAsJsonObject();
        TestCase.assertEquals(BASE_NAME, json.get(BASE_FIELD_KEY).getAsString());
        TestCase.assertNull(json.get(SUB_FIELD_KEY));
    }

    public void testBaseSerializedAsBaseWhenSpecifiedWithExplicitTypeForToJsonMethod() {
        TestTypes.Base base = new TestTypes.Sub();
        String json = gson.toJson(base, TestTypes.Base.class);
        TestCase.assertTrue(json.contains(BASE_NAME));
        TestCase.assertFalse(json.contains(SUB_FIELD_KEY));
    }

    public void testBaseSerializedAsSubWhenSpecifiedWithExplicitType() {
        TestTypes.Base base = new TestTypes.Sub();
        JsonObject json = gson.toJsonTree(base, TestTypes.Sub.class).getAsJsonObject();
        TestCase.assertEquals(SUB_NAME, json.get(SUB_FIELD_KEY).getAsString());
    }

    public void testBaseSerializedAsSubWhenSpecifiedWithExplicitTypeForToJsonMethod() {
        TestTypes.Base base = new TestTypes.Sub();
        String json = gson.toJson(base, TestTypes.Sub.class);
        TestCase.assertTrue(json.contains(SUB_NAME));
    }

    private static class SubTypeOfNested extends TestTypes.Nested {
        private final long value = 5;

        public SubTypeOfNested(TestTypes.BagOfPrimitives primitive1, TestTypes.BagOfPrimitives primitive2) {
            super(primitive1, primitive2);
        }

        @Override
        public void appendFields(StringBuilder sb) {
            sb.append("\"value\":").append(value).append(",");
            super.appendFields(sb);
        }
    }

    public void testSubInterfacesOfCollectionSerialization() throws Exception {
        List<Integer> list = new LinkedList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        Queue<Long> queue = new LinkedList<Long>();
        queue.add(0L);
        queue.add(1L);
        queue.add(2L);
        queue.add(3L);
        Set<Float> set = new TreeSet<Float>();
        set.add(0.1F);
        set.add(0.2F);
        set.add(0.3F);
        set.add(0.4F);
        SortedSet<Character> sortedSet = new TreeSet<Character>();
        sortedSet.add('a');
        sortedSet.add('b');
        sortedSet.add('c');
        sortedSet.add('d');
        InheritanceTest.ClassWithSubInterfacesOfCollection target = new InheritanceTest.ClassWithSubInterfacesOfCollection(list, queue, set, sortedSet);
        TestCase.assertEquals(target.getExpectedJson(), gson.toJson(target));
    }

    public void testSubInterfacesOfCollectionDeserialization() throws Exception {
        String json = "{\"list\":[0,1,2,3],\"queue\":[0,1,2,3],\"set\":[0.1,0.2,0.3,0.4]," + ("\"sortedSet\":[\"a\",\"b\",\"c\",\"d\"]" + "}");
        InheritanceTest.ClassWithSubInterfacesOfCollection target = gson.fromJson(json, InheritanceTest.ClassWithSubInterfacesOfCollection.class);
        TestCase.assertTrue(target.listContains(0, 1, 2, 3));
        TestCase.assertTrue(target.queueContains(0, 1, 2, 3));
        TestCase.assertTrue(target.setContains(0.1F, 0.2F, 0.3F, 0.4F));
        TestCase.assertTrue(target.sortedSetContains('a', 'b', 'c', 'd'));
    }

    private static class ClassWithSubInterfacesOfCollection {
        private List<Integer> list;

        private Queue<Long> queue;

        private Set<Float> set;

        private SortedSet<Character> sortedSet;

        public ClassWithSubInterfacesOfCollection(List<Integer> list, Queue<Long> queue, Set<Float> set, SortedSet<Character> sortedSet) {
            this.list = list;
            this.queue = queue;
            this.set = set;
            this.sortedSet = sortedSet;
        }

        boolean listContains(int... values) {
            for (int value : values) {
                if (!(list.contains(value))) {
                    return false;
                }
            }
            return true;
        }

        boolean queueContains(long... values) {
            for (long value : values) {
                if (!(queue.contains(value))) {
                    return false;
                }
            }
            return true;
        }

        boolean setContains(float... values) {
            for (float value : values) {
                if (!(set.contains(value))) {
                    return false;
                }
            }
            return true;
        }

        boolean sortedSetContains(char... values) {
            for (char value : values) {
                if (!(sortedSet.contains(value))) {
                    return false;
                }
            }
            return true;
        }

        public String getExpectedJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"list\":");
            append(sb, list).append(",");
            sb.append("\"queue\":");
            append(sb, queue).append(",");
            sb.append("\"set\":");
            append(sb, set).append(",");
            sb.append("\"sortedSet\":");
            append(sb, sortedSet);
            sb.append("}");
            return sb.toString();
        }

        private StringBuilder append(StringBuilder sb, Collection<?> c) {
            sb.append("[");
            boolean first = true;
            for (Object o : c) {
                if (!first) {
                    sb.append(",");
                } else {
                    first = false;
                }
                if ((o instanceof String) || (o instanceof Character)) {
                    sb.append('\"');
                }
                sb.append(o.toString());
                if ((o instanceof String) || (o instanceof Character)) {
                    sb.append('\"');
                }
            }
            sb.append("]");
            return sb;
        }
    }
}

