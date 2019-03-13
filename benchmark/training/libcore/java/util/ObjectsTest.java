/**
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util;


import java.util.Arrays;
import java.util.Objects;
import junit.framework.TestCase;


public class ObjectsTest extends TestCase {
    public static final class Hello {
        public String toString() {
            return "hello";
        }
    }

    public void test_compare() throws Exception {
        TestCase.assertEquals(0, Objects.compare(null, null, String.CASE_INSENSITIVE_ORDER));
        TestCase.assertEquals(0, Objects.compare("a", "A", String.CASE_INSENSITIVE_ORDER));
        TestCase.assertEquals((-1), Objects.compare("a", "b", String.CASE_INSENSITIVE_ORDER));
        TestCase.assertEquals(1, Objects.compare("b", "a", String.CASE_INSENSITIVE_ORDER));
    }

    public void test_deepEquals() throws Exception {
        int[] xs = new int[3];
        int[] ys = new int[4];
        int[] zs = new int[3];
        String[] o1 = new String[]{ "hello" };
        String[] o2 = new String[]{ "world" };
        String[] o3 = new String[]{ "hello" };
        TestCase.assertTrue(Objects.deepEquals(null, null));
        TestCase.assertFalse(Objects.deepEquals(xs, null));
        TestCase.assertFalse(Objects.deepEquals(null, xs));
        TestCase.assertTrue(Objects.deepEquals(xs, xs));
        TestCase.assertTrue(Objects.deepEquals(xs, zs));
        TestCase.assertFalse(Objects.deepEquals(xs, ys));
        TestCase.assertTrue(Objects.deepEquals(o1, o1));
        TestCase.assertTrue(Objects.deepEquals(o1, o3));
        TestCase.assertFalse(Objects.deepEquals(o1, o2));
        TestCase.assertTrue(Objects.deepEquals("hello", "hello"));
        TestCase.assertFalse(Objects.deepEquals("hello", "world"));
    }

    public void test_equals() throws Exception {
        ObjectsTest.Hello h1 = new ObjectsTest.Hello();
        ObjectsTest.Hello h2 = new ObjectsTest.Hello();
        TestCase.assertTrue(Objects.equals(null, null));
        TestCase.assertFalse(Objects.equals(h1, null));
        TestCase.assertFalse(Objects.equals(null, h1));
        TestCase.assertFalse(Objects.equals(h1, h2));
        TestCase.assertTrue(Objects.equals(h1, h1));
    }

    public void test_hash() throws Exception {
        TestCase.assertEquals(Arrays.hashCode(new Object[0]), Objects.hash());
        TestCase.assertEquals(31, Objects.hash(((Object) (null))));
        TestCase.assertEquals(0, Objects.hash(((Object[]) (null))));
        TestCase.assertEquals((-1107615551), Objects.hash("hello", "world"));
        TestCase.assertEquals(23656287, Objects.hash("hello", "world", null));
    }

    public void test_hashCode() throws Exception {
        ObjectsTest.Hello h = new ObjectsTest.Hello();
        TestCase.assertEquals(h.hashCode(), Objects.hashCode(h));
        TestCase.assertEquals(0, Objects.hashCode(null));
    }

    public void test_requireNonNull_T() throws Exception {
        ObjectsTest.Hello h = new ObjectsTest.Hello();
        TestCase.assertEquals(h, Objects.requireNonNull(h));
        try {
            Objects.requireNonNull(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void test_requireNonNull_T_String() throws Exception {
        ObjectsTest.Hello h = new ObjectsTest.Hello();
        TestCase.assertEquals(h, Objects.requireNonNull(h, "test"));
        try {
            Objects.requireNonNull(null, "message");
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals("message", expected.getMessage());
        }
        try {
            Objects.requireNonNull(null, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void test_toString_Object() throws Exception {
        TestCase.assertEquals("hello", Objects.toString(new ObjectsTest.Hello()));
        TestCase.assertEquals("null", Objects.toString(null));
    }

    public void test_toString_Object_String() throws Exception {
        TestCase.assertEquals("hello", Objects.toString(new ObjectsTest.Hello(), "world"));
        TestCase.assertEquals("world", Objects.toString(null, "world"));
        TestCase.assertEquals(null, Objects.toString(null, null));
    }
}

