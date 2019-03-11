/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the Matchers that operate over strings
 */
public class StringMatchersTest {
    @Test
    public void startsWithString() {
        Assert.assertTrue(new StartsWith("mockito").matches("mockito is here"));
    }

    @Test
    public void doesNotStartWithString() {
        Assert.assertFalse(new StartsWith("junit").matches("mockito is here"));
    }

    @Test
    public void nullStartsWith() {
        Assert.assertFalse(new StartsWith("java").matches(null));
    }

    @Test
    public void endsWithString() {
        Assert.assertTrue(new EndsWith("mockito").matches("here is mockito"));
    }

    @Test
    public void doesNotEndWithString() {
        Assert.assertFalse(new EndsWith("junit").matches("here is mockito"));
    }

    @Test
    public void nullEndsWith() {
        Assert.assertFalse(new EndsWith("java").matches(null));
    }

    @Test
    public void containsString() {
        Assert.assertTrue(new Contains("mockito").matches("****mockito****"));
    }

    @Test
    public void stringDoesNotContain() {
        Assert.assertFalse(new Contains("junit").matches("****mockito****"));
    }

    @Test
    public void nullContainsNothing() {
        Assert.assertFalse(new Contains("mockito").matches(null));
    }

    @Test
    public void matchesRegex() {
        Assert.assertTrue(new Find("eleph.nt").matches("the elephant in the room"));
        Assert.assertTrue(new Find("eleph.nt").matches("the elephInt in the room"));
    }

    @Test
    public void doesNotMatchRegex() {
        Assert.assertFalse(new Find("eleph.nt").matches("the otter in the room"));
    }

    @Test
    public void nullDoesNotMatchRegex() {
        Assert.assertFalse(new Find("eleph.nt").matches(null));
    }
}

