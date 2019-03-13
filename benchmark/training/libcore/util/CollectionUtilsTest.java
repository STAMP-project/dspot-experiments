/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.util;


import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;


public final class CollectionUtilsTest extends TestCase {
    public void testDereferenceIterable() {
        List<Reference<String>> refs = new ArrayList<Reference<String>>();
        refs.add(newLiteralReference("a"));
        refs.add(newLiteralReference("b"));
        refs.add(newLiteralReference("c"));
        refs.add(newLiteralReference("d"));
        refs.add(newLiteralReference("e"));
        Iterable<String> strings = CollectionUtils.dereferenceIterable(refs, true);
        TestCase.assertEquals(Arrays.<String>asList("a", "b", "c", "d", "e"), toList(strings));
        refs.get(1).clear();// b

        TestCase.assertEquals(Arrays.<String>asList("a", "c", "d", "e"), toList(strings));
        TestCase.assertEquals(4, refs.size());
        Iterator<String> i = strings.iterator();
        TestCase.assertEquals("a", i.next());
        i.remove();
        TestCase.assertEquals(3, refs.size());
        TestCase.assertEquals("c", i.next());
        TestCase.assertEquals("d", i.next());
        TestCase.assertTrue(i.hasNext());
        try {
            i.remove();
            TestCase.fail("Expected hasNext() to make remove() impossible.");
        } catch (IllegalStateException expected) {
        }
        TestCase.assertEquals("e", i.next());
        i.remove();
        TestCase.assertEquals(2, refs.size());
        TestCase.assertFalse(i.hasNext());
        refs.get(0).clear();// c

        refs.get(1).clear();// d

        TestCase.assertEquals(Arrays.<String>asList(), toList(strings));
    }

    public void testRemoveDuplicatesOnEmptyCollection() {
        List<String> list = new ArrayList<String>();
        CollectionUtils.removeDuplicates(list, String.CASE_INSENSITIVE_ORDER);
        TestCase.assertTrue(list.isEmpty());
    }

    public void testRemoveDuplicatesOnSingletonCollection() {
        List<String> list = Arrays.asList("A");
        CollectionUtils.removeDuplicates(list, String.CASE_INSENSITIVE_ORDER);
        TestCase.assertEquals(Collections.singletonList("A"), list);
    }

    public void testRemoveDuplicates() {
        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("A");
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("C");
        list.add("C");
        CollectionUtils.removeDuplicates(list, String.CASE_INSENSITIVE_ORDER);
        TestCase.assertEquals(Arrays.asList("A", "B", "C"), list);
    }
}

