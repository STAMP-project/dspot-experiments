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
package com.google.gson;


import java.util.NoSuchElementException;
import junit.framework.TestCase;


/**
 * Unit tests for {@link JsonStreamParser}
 *
 * @author Inderjeet Singh
 */
public class JsonStreamParserTest extends TestCase {
    private JsonStreamParser parser;

    public void testParseTwoStrings() {
        String actualOne = parser.next().getAsString();
        TestCase.assertEquals("one", actualOne);
        String actualTwo = parser.next().getAsString();
        TestCase.assertEquals("two", actualTwo);
    }

    public void testIterator() {
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertEquals("one", parser.next().getAsString());
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertEquals("two", parser.next().getAsString());
        TestCase.assertFalse(parser.hasNext());
    }

    public void testNoSideEffectForHasNext() throws Exception {
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertEquals("one", parser.next().getAsString());
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertTrue(parser.hasNext());
        TestCase.assertEquals("two", parser.next().getAsString());
        TestCase.assertFalse(parser.hasNext());
        TestCase.assertFalse(parser.hasNext());
    }

    public void testCallingNextBeyondAvailableInput() {
        parser.next();
        parser.next();
        try {
            parser.next();
            TestCase.fail("Parser should not go beyond available input");
        } catch (NoSuchElementException expected) {
        }
    }
}

