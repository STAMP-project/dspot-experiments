/**
 * -
 * -\-\-
 * Helios Client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.common.descriptors;


import HostSelector.Operator;
import com.spotify.helios.common.Json;
import org.junit.Assert;
import org.junit.Test;


public class HostSelectorTest {
    @Test
    public void testParseEquals() {
        Assert.assertEquals(new HostSelector("A", Operator.EQUALS, "B"), HostSelector.parse("A=B"));
        Assert.assertEquals(new HostSelector("A", Operator.EQUALS, "B"), HostSelector.parse("A = B"));
        Assert.assertEquals(new HostSelector("A", Operator.EQUALS, "B"), HostSelector.parse("A =B"));
        Assert.assertEquals(new HostSelector("A", Operator.EQUALS, "B"), HostSelector.parse("A= B"));
        Assert.assertEquals(new HostSelector("A", Operator.EQUALS, "B"), HostSelector.parse("A\t\t=  B"));
    }

    @Test
    public void testParseNotEquals() {
        Assert.assertEquals(new HostSelector("A", Operator.NOT_EQUALS, "B"), HostSelector.parse("A!=B"));
        Assert.assertEquals(new HostSelector("A", Operator.NOT_EQUALS, "B"), HostSelector.parse("A != B"));
        Assert.assertEquals(new HostSelector("A", Operator.NOT_EQUALS, "B"), HostSelector.parse("A !=B"));
        Assert.assertEquals(new HostSelector("A", Operator.NOT_EQUALS, "B"), HostSelector.parse("A!= B"));
        Assert.assertEquals(new HostSelector("A", Operator.NOT_EQUALS, "B"), HostSelector.parse("A\t\t!=  B"));
    }

    @Test
    public void testParseAllowedCharacters() {
        Assert.assertEquals(new HostSelector("foo", Operator.EQUALS, "123"), HostSelector.parse("foo=123"));
        Assert.assertEquals(new HostSelector("_abc", Operator.NOT_EQUALS, "d-e"), HostSelector.parse("_abc!=d-e"));
    }

    @Test
    public void testParseDisallowedCharacters() {
        Assert.assertNull(HostSelector.parse("foo = @123"));
        Assert.assertNull(HostSelector.parse("f/oo = 123"));
        // Verify equal not allowed in label and operand
        Assert.assertNull(HostSelector.parse("f=oo = 123"));
        Assert.assertNull(HostSelector.parse("foo = 12=3"));
        // Verify spaces not allowed in label and operand
        Assert.assertNull(HostSelector.parse("fo o = 123"));
        Assert.assertNull(HostSelector.parse("foo = 1 23"));
        // Verify ! not allowed in label and operand
        Assert.assertNull(HostSelector.parse("foo=!123"));
        Assert.assertNull(HostSelector.parse("!foo=bar"));
        // Verify fails on unknown operators
        Assert.assertNull(HostSelector.parse("foo or 123"));
        Assert.assertNull(HostSelector.parse("foo==123"));
        Assert.assertNull(HostSelector.parse("foo&&123"));
        // Verify fails on empty label or operand
        Assert.assertNull(HostSelector.parse("=123"));
        Assert.assertNull(HostSelector.parse(" =123"));
        Assert.assertNull(HostSelector.parse(" = 123"));
        Assert.assertNull(HostSelector.parse("foo="));
        Assert.assertNull(HostSelector.parse("foo= "));
        Assert.assertNull(HostSelector.parse("foo = "));
    }

    @Test
    public void testEqualsMatch() {
        final HostSelector hostSelector = HostSelector.parse("A=B");
        Assert.assertTrue(hostSelector.matches("B"));
        Assert.assertFalse(hostSelector.matches("Bb"));
        Assert.assertFalse(hostSelector.matches("b"));
        Assert.assertFalse(hostSelector.matches("A"));
    }

    @Test
    public void testNotEqualsMatch() {
        final HostSelector hostSelector = HostSelector.parse("A!=B");
        Assert.assertFalse(hostSelector.matches("B"));
        Assert.assertTrue(hostSelector.matches("Bb"));
        Assert.assertTrue(hostSelector.matches("b"));
        Assert.assertTrue(hostSelector.matches("A"));
    }

    @Test
    public void testInOperator() {
        final HostSelector hostSelector = HostSelector.parse("a in (foo, bar)");
        Assert.assertTrue(hostSelector.matches("foo"));
        Assert.assertTrue(hostSelector.matches("bar"));
        Assert.assertFalse(hostSelector.matches("baz"));
        final HostSelector hostSelector2 = HostSelector.parse("a in(foo,bar)");
        Assert.assertTrue(hostSelector2.matches("foo"));
        Assert.assertTrue(hostSelector2.matches("bar"));
        Assert.assertFalse(hostSelector2.matches("baz"));
        final HostSelector hostSelector3 = HostSelector.parse("a in(foo)");
        Assert.assertTrue(hostSelector3.matches("foo"));
        Assert.assertFalse(hostSelector3.matches("baz"));
    }

    @Test
    public void testInOperatorEquality() {
        Assert.assertEquals(HostSelector.parse("a in (foo,bar)"), HostSelector.parse("a in (foo, bar)"));
        Assert.assertEquals(HostSelector.parse("a in (foo,bar)"), HostSelector.parse("a in (bar,foo)"));
        Assert.assertEquals(HostSelector.parse("a in (foo)"), HostSelector.parse("a in (foo,foo)"));
    }

    @Test
    public void testInOperatorSerialization() throws Exception {
        final HostSelector orig = HostSelector.parse("a in (foo,bar)");
        final HostSelector parsed = Json.read(Json.asString(orig), HostSelector.class);
        Assert.assertEquals(orig, parsed);
    }

    @Test
    public void testInOperatorEmptySet() {
        final HostSelector hostSelector = HostSelector.parse("a in ()");
        Assert.assertFalse(hostSelector.matches("foo"));
        Assert.assertFalse(hostSelector.matches("bar"));
        Assert.assertFalse(hostSelector.matches("baz"));
        final HostSelector hostSelector2 = HostSelector.parse("a in()");
        Assert.assertFalse(hostSelector2.matches("foo"));
        Assert.assertFalse(hostSelector2.matches("bar"));
        Assert.assertFalse(hostSelector2.matches("baz"));
    }

    @Test
    public void testNotInOperator() {
        final HostSelector hostSelector = HostSelector.parse("a notin (foo, bar)");
        Assert.assertFalse(hostSelector.matches("foo"));
        Assert.assertFalse(hostSelector.matches("bar"));
        Assert.assertTrue(hostSelector.matches("baz"));
        final HostSelector hostSelector2 = HostSelector.parse("a notin(foo,bar)");
        Assert.assertFalse(hostSelector2.matches("foo"));
        Assert.assertFalse(hostSelector2.matches("bar"));
        Assert.assertTrue(hostSelector2.matches("baz"));
        final HostSelector hostSelector3 = HostSelector.parse("a notin(foo)");
        Assert.assertFalse(hostSelector3.matches("foo"));
        Assert.assertTrue(hostSelector3.matches("baz"));
    }

    @Test
    public void testNotInOperatorEquality() {
        Assert.assertEquals(HostSelector.parse("a notin (foo,bar)"), HostSelector.parse("a notin (foo, bar)"));
        Assert.assertEquals(HostSelector.parse("a notin (foo,bar)"), HostSelector.parse("a notin (bar,foo)"));
        Assert.assertEquals(HostSelector.parse("a notin (foo)"), HostSelector.parse("a notin (foo,foo)"));
    }

    @Test
    public void testNotInOperatorSerialization() throws Exception {
        final HostSelector orig = HostSelector.parse("a notin (foo,bar)");
        final HostSelector parsed = Json.read(Json.asString(orig), HostSelector.class);
        Assert.assertEquals(orig, parsed);
    }

    @Test
    public void testNotInOperatorEmptySet() {
        final HostSelector hostSelector = HostSelector.parse("a notin ()");
        Assert.assertTrue(hostSelector.matches("foo"));
        Assert.assertTrue(hostSelector.matches("bar"));
        Assert.assertTrue(hostSelector.matches("baz"));
        final HostSelector hostSelector2 = HostSelector.parse("a notin()");
        Assert.assertTrue(hostSelector2.matches("foo"));
        Assert.assertTrue(hostSelector2.matches("bar"));
        Assert.assertTrue(hostSelector2.matches("baz"));
    }

    @Test
    public void testToPrettyString() {
        Assert.assertEquals("A != B", HostSelector.parse("A!=B").toPrettyString());
        Assert.assertEquals("A = B", HostSelector.parse("A=B").toPrettyString());
    }
}

