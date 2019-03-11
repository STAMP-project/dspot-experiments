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
import junit.framework.TestCase;


/**
 * Functional tests for Java Character values.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PrimitiveCharacterTest extends TestCase {
    private Gson gson;

    public void testPrimitiveCharacterAutoboxedSerialization() {
        TestCase.assertEquals("\"A\"", gson.toJson('A'));
        TestCase.assertEquals("\"A\"", gson.toJson('A', char.class));
        TestCase.assertEquals("\"A\"", gson.toJson('A', Character.class));
    }

    public void testPrimitiveCharacterAutoboxedDeserialization() {
        char expected = 'a';
        char actual = gson.fromJson("a", char.class);
        TestCase.assertEquals(expected, actual);
        actual = gson.fromJson("\"a\"", char.class);
        TestCase.assertEquals(expected, actual);
        actual = gson.fromJson("a", Character.class);
        TestCase.assertEquals(expected, actual);
    }
}

