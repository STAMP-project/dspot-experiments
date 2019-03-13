/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.servlet;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TeeFilterTest {
    @Test
    public void extractNameList() {
        Assert.assertEquals(Arrays.asList(new String[]{ "a" }), TeeFilter.extractNameList("a"));
        Assert.assertEquals(Arrays.asList(new String[]{ "a", "b" }), TeeFilter.extractNameList("a, b"));
        Assert.assertEquals(Arrays.asList(new String[]{ "a", "b" }), TeeFilter.extractNameList("a; b"));
        Assert.assertEquals(Arrays.asList(new String[]{ "a", "b", "c" }), TeeFilter.extractNameList("a; b, c"));
    }

    @Test
    public void defaultCase() {
        Assert.assertTrue(TeeFilter.computeActivation("somehost", "", ""));
        Assert.assertTrue(TeeFilter.computeActivation("somehost", null, null));
    }

    @Test
    public void withIncludesOnly() {
        Assert.assertTrue(TeeFilter.computeActivation("a", "a", null));
        Assert.assertTrue(TeeFilter.computeActivation("a", "a, b", null));
        Assert.assertFalse(TeeFilter.computeActivation("a", "b", null));
        Assert.assertFalse(TeeFilter.computeActivation("a", "b, c", null));
    }

    @Test
    public void withExcludesOnly() {
        Assert.assertFalse(TeeFilter.computeActivation("a", null, "a"));
        Assert.assertFalse(TeeFilter.computeActivation("a", null, "a, b"));
        Assert.assertTrue(TeeFilter.computeActivation("a", null, "b"));
        Assert.assertTrue(TeeFilter.computeActivation("a", null, "b, c"));
    }

    @Test
    public void withIncludesAndExcludes() {
        Assert.assertFalse(TeeFilter.computeActivation("a", "a", "a"));
        Assert.assertTrue(TeeFilter.computeActivation("a", "a", "b"));
        Assert.assertFalse(TeeFilter.computeActivation("a", "b", "a"));
        Assert.assertFalse(TeeFilter.computeActivation("a", "b", "b"));
    }
}

