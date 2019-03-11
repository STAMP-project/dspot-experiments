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
package ch.qos.logback.core.util;


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link StringCollectionUtil}.
 *
 * @author Carl Harris
 */
public class StringCollectionUtilTest {
    @Test
    public void testRetainMatchingWithNoPatterns() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.retainMatching(values);
        Assert.assertTrue(values.contains("A"));
    }

    @Test
    public void testRetainMatchingWithMatchingPattern() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.retainMatching(values, "A");
        Assert.assertTrue(values.contains("A"));
    }

    @Test
    public void testRetainMatchingWithNoMatchingPattern() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.retainMatching(values, "B");
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testRemoveMatchingWithNoPatterns() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.removeMatching(values);
        Assert.assertTrue(values.contains("A"));
    }

    @Test
    public void testRemoveMatchingWithMatchingPattern() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.removeMatching(values, "A");
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testRemoveMatchingWithNoMatchingPattern() throws Exception {
        Collection<String> values = stringToList("A");
        StringCollectionUtil.removeMatching(values, "B");
        Assert.assertTrue(values.contains("A"));
    }
}

