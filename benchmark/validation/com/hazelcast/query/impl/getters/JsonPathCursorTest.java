/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.query.impl.getters;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class JsonPathCursorTest {
    @Test
    public void testOneItemPath() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathWithUnMatchedCloseBraceShouldThrowIllegalArgumentException() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("a]");
        cursor.getNext();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPathWithNonNumberArrayIndexShouldThrowIllegalArgumentException() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[a]");
        cursor.getNext();
    }

    @Test
    public void testOneItemArrayPath() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[4]");
        Assert.assertEquals("4", cursor.getNext());
        Assert.assertEquals(4, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemPath() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc.def");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("def", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoArrayItemPath() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[4][1]");
        Assert.assertEquals("4", cursor.getNext());
        Assert.assertEquals(4, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("1", cursor.getNext());
        Assert.assertEquals(1, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemPath_whenTheSecondIsArray() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc[1]");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("1", cursor.getNext());
        Assert.assertEquals(1, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemPath_whenTheFirstIsArray() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[1].abc");
        Assert.assertEquals("1", cursor.getNext());
        Assert.assertEquals(1, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testOneItemArrayPath_whenAny() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[any]");
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemArrayPath_whenAny() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[any][any]");
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemPath_whenFirstIsAny() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[any].abc");
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testTwoItemPath_whenSecondIsAny() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc[any]");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenAllNonArray() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc.def.ghi");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("def", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("ghi", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenNonArray_array_any() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc[12][any]");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenNonArray_any_array() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("abc[any][12]");
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenArray_nonArray_any() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[12].abc[any]");
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenArray_any_nonArray() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[12][any].abc");
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenAny_nonArray_array() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[any].abc[12]");
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        assertEnd(cursor);
    }

    @Test
    public void testThreeItemPath_whenAny_array_nonArray() {
        JsonPathCursor cursor = JsonPathCursor.createCursor("[any][12].abc");
        Assert.assertEquals("any", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertTrue(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("12", cursor.getNext());
        Assert.assertEquals(12, cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertTrue(cursor.isArray());
        Assert.assertEquals("abc", cursor.getNext());
        Assert.assertEquals((-1), cursor.getArrayIndex());
        Assert.assertFalse(cursor.isAny());
        Assert.assertFalse(cursor.isArray());
        assertEnd(cursor);
    }
}

