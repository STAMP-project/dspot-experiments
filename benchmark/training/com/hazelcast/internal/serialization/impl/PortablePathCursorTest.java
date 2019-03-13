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
package com.hazelcast.internal.serialization.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class PortablePathCursorTest {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void nonInitialised_throwsException() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        // THEN
        expected.expect(NullPointerException.class);
        cursor.advanceToNextToken();
    }

    @Test
    public void initialised_reset_throwsException() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine.oil");
        // WHEN
        cursor.reset();
        // THEN
        expected.expect(NullPointerException.class);
        cursor.advanceToNextToken();
    }

    @Test
    public void oneElementToken() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine");
        // THEN
        Assert.assertEquals("engine", cursor.token());
        Assert.assertEquals("engine", cursor.path());
        Assert.assertTrue(cursor.isLastToken());
        Assert.assertFalse(cursor.isAnyPath());
        Assert.assertFalse(cursor.advanceToNextToken());
        Assert.assertFalse(cursor.advanceToNextToken());
        Assert.assertEquals("engine", cursor.token());
    }

    @Test
    public void multiElementToken_iterationOverAll() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine.turbocharger.nozzle");
        // THEN - generic options
        Assert.assertFalse(cursor.isAnyPath());
        Assert.assertEquals("engine.turbocharger.nozzle", cursor.path());
        // THEN - first token
        Assert.assertEquals("engine", cursor.token());
        Assert.assertFalse(cursor.isLastToken());
        // THEN - second token
        Assert.assertTrue(cursor.advanceToNextToken());
        Assert.assertEquals("turbocharger", cursor.token());
        Assert.assertFalse(cursor.isLastToken());
        // THEN - third token
        Assert.assertTrue(cursor.advanceToNextToken());
        Assert.assertEquals("nozzle", cursor.token());
        Assert.assertTrue(cursor.isLastToken());
        // THEN - no other token
        Assert.assertFalse(cursor.advanceToNextToken());
        Assert.assertEquals("nozzle", cursor.token());
    }

    @Test
    public void multiElementToken_anyPath() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine.turbocharger[any].nozzle");
        // THEN
        Assert.assertTrue(cursor.isAnyPath());
    }

    @Test
    public void multiElementToken_jumpingToIndex() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine.turbocharger.nozzle");
        // THEN - 2nd token
        cursor.index(2);
        Assert.assertEquals("nozzle", cursor.token());
        Assert.assertTrue(cursor.isLastToken());
        Assert.assertEquals(2, cursor.index());
        Assert.assertFalse(cursor.advanceToNextToken());
        // THEN - 1st token
        cursor.index(0);
        Assert.assertEquals("engine", cursor.token());
        Assert.assertFalse(cursor.isLastToken());
        Assert.assertEquals(0, cursor.index());
        Assert.assertTrue(cursor.advanceToNextToken());
        // THEN - 3rd token
        cursor.index(1);
        Assert.assertEquals("turbocharger", cursor.token());
        Assert.assertFalse(cursor.isLastToken());
        Assert.assertEquals(1, cursor.index());
        Assert.assertTrue(cursor.advanceToNextToken());
    }

    @Test
    public void multiElementToken_jumpingToIndexOutOfBound() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("engine.turbocharger.nozzle");
        // WHEN
        expected.expect(IndexOutOfBoundsException.class);
        cursor.index(3);
        // THEN
        cursor.token();
    }

    @Test
    public void reuseOfCursor() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        // THEN
        cursor.init("engine.turbocharger.nozzle");
        Assert.assertFalse(cursor.isAnyPath());
        Assert.assertEquals("engine", cursor.token());
        cursor.init("person.brain[any]");
        Assert.assertTrue(cursor.isAnyPath());
        Assert.assertEquals("person", cursor.token());
    }

    @Test
    public void emptyPath() {
        PortablePathCursor cursor = new PortablePathCursor();
        expected.expect(IllegalArgumentException.class);
        cursor.init("");
    }

    @Test
    public void nullPath() {
        PortablePathCursor cursor = new PortablePathCursor();
        expected.expect(IllegalArgumentException.class);
        cursor.init(null);
    }

    @Test
    public void wrongPath_dotOnly() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        expected.expect(IllegalArgumentException.class);
        cursor.init(".");
        // THEN
        Assert.assertEquals("", cursor.token());
        Assert.assertEquals("", cursor.path());
        Assert.assertTrue(cursor.isLastToken());
        Assert.assertFalse(cursor.isAnyPath());
        Assert.assertFalse(cursor.advanceToNextToken());
    }

    @Test
    public void wrongPath_moreDots() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        expected.expect(IllegalArgumentException.class);
        cursor.init("...");
    }

    @Test
    public void wrongPath_emptyTokens() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("a..");
        // THEN - 1st token
        Assert.assertEquals("a", cursor.token());
        Assert.assertTrue(cursor.advanceToNextToken());
        // THEN - 2nd token
        PortablePathCursorTest.assertTokenThrowsException(cursor);
        Assert.assertTrue(cursor.advanceToNextToken());
        // THEN - 3rd token
        PortablePathCursorTest.assertTokenThrowsException(cursor);
        Assert.assertFalse(cursor.advanceToNextToken());
    }

    @Test
    public void wrongPath_pathEndingWithDot() {
        // GIVEN
        PortablePathCursor cursor = new PortablePathCursor();
        cursor.init("a.b.");
        // THEN - 1st token
        Assert.assertEquals("a", cursor.token());
        Assert.assertTrue(cursor.advanceToNextToken());
        // THEN - 2nd token
        Assert.assertEquals("b", cursor.token());
        Assert.assertTrue(cursor.advanceToNextToken());
        // THEN - 3rd token
        PortablePathCursorTest.assertTokenThrowsException(cursor);
        Assert.assertFalse(cursor.advanceToNextToken());
    }
}

