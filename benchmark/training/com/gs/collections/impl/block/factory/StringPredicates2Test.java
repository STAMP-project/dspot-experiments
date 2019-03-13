/**
 * Copyright 2013 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block.factory;


import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


public class StringPredicates2Test {
    @Test
    public void startsWith() {
        Assert.assertFalse(StringPredicates2.startsWith().accept(null, "Hello"));
        Assert.assertTrue(StringPredicates2.startsWith().accept("HelloWorld", "Hello"));
        Assert.assertFalse(StringPredicates2.startsWith().accept("HelloWorld", "World"));
        Assert.assertEquals("StringPredicates2.startsWith()", StringPredicates2.startsWith().toString());
    }

    @Test
    public void notStartsWith() {
        Assert.assertTrue(StringPredicates2.notStartsWith().accept(null, "Hello"));
        Assert.assertFalse(StringPredicates2.notStartsWith().accept("HelloWorld", "Hello"));
        Assert.assertTrue(StringPredicates2.notStartsWith().accept("HelloWorld", "World"));
        Assert.assertEquals("StringPredicates2.notStartsWith()", StringPredicates2.notStartsWith().toString());
    }

    @Test
    public void endsWith() {
        Assert.assertFalse(StringPredicates2.endsWith().accept(null, "Hello"));
        Assert.assertFalse(StringPredicates2.endsWith().accept("HelloWorld", "Hello"));
        Assert.assertTrue(StringPredicates2.endsWith().accept("HelloWorld", "World"));
        Assert.assertEquals("StringPredicates2.endsWith()", StringPredicates2.endsWith().toString());
    }

    @Test
    public void notEndsWith() {
        Assert.assertTrue(StringPredicates2.notEndsWith().accept(null, "Hello"));
        Assert.assertTrue(StringPredicates2.notEndsWith().accept("HelloWorld", "Hello"));
        Assert.assertFalse(StringPredicates2.notEndsWith().accept("HelloWorld", "World"));
        Assert.assertEquals("StringPredicates2.notEndsWith()", StringPredicates2.notEndsWith().toString());
    }

    @Test
    public void equalsIgnoreCase() {
        Assert.assertFalse(StringPredicates2.equalsIgnoreCase().accept(null, "HELLO"));
        Assert.assertTrue(StringPredicates2.equalsIgnoreCase().accept("hello", "HELLO"));
        Assert.assertTrue(StringPredicates2.equalsIgnoreCase().accept("WORLD", "world"));
        Assert.assertFalse(StringPredicates2.equalsIgnoreCase().accept("World", "Hello"));
        Assert.assertEquals("StringPredicates2.equalsIgnoreCase()", StringPredicates2.equalsIgnoreCase().toString());
    }

    @Test
    public void notEqualsIgnoreCase() {
        Assert.assertTrue(StringPredicates2.notEqualsIgnoreCase().accept(null, "HELLO"));
        Assert.assertFalse(StringPredicates2.notEqualsIgnoreCase().accept("hello", "HELLO"));
        Assert.assertFalse(StringPredicates2.notEqualsIgnoreCase().accept("WORLD", "world"));
        Assert.assertTrue(StringPredicates2.notEqualsIgnoreCase().accept("World", "Hello"));
        Assert.assertEquals("StringPredicates2.notEqualsIgnoreCase()", StringPredicates2.notEqualsIgnoreCase().toString());
    }

    @Test
    public void containsString() {
        Assert.assertTrue(StringPredicates2.contains().accept("WorldHelloWorld", "Hello"));
        Assert.assertFalse(StringPredicates2.contains().accept("WorldHelloWorld", "Goodbye"));
        Assert.assertEquals("StringPredicates2.contains()", StringPredicates2.contains().toString());
    }

    @Test
    public void matches() {
        Assert.assertTrue(StringPredicates2.matches().accept("aaaaabbbbb", "a*b*"));
        Assert.assertFalse(StringPredicates2.matches().accept("ba", "a*b"));
        Assert.assertEquals("StringPredicates2.matches()", StringPredicates2.matches().toString());
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(StringPredicates2.class);
    }
}

