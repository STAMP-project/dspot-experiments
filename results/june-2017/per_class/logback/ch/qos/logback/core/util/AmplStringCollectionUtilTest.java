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


/**
 * Unit tests for {@link StringCollectionUtil}.
 *
 * @author Carl Harris
 */
public class AmplStringCollectionUtilTest {
    @org.junit.Test
    public void testRetainMatchingWithNoPatterns() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.retainMatching(values);
        org.junit.Assert.assertTrue(values.contains("A"));
    }

    @org.junit.Test
    public void testRetainMatchingWithMatchingPattern() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.retainMatching(values, "A");
        org.junit.Assert.assertTrue(values.contains("A"));
    }

    @org.junit.Test
    public void testRetainMatchingWithNoMatchingPattern() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.retainMatching(values, "B");
        org.junit.Assert.assertTrue(values.isEmpty());
    }

    @org.junit.Test
    public void testRemoveMatchingWithNoPatterns() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.removeMatching(values);
        org.junit.Assert.assertTrue(values.contains("A"));
    }

    @org.junit.Test
    public void testRemoveMatchingWithMatchingPattern() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.removeMatching(values, "A");
        org.junit.Assert.assertTrue(values.isEmpty());
    }

    @org.junit.Test
    public void testRemoveMatchingWithNoMatchingPattern() throws java.lang.Exception {
        java.util.Collection<java.lang.String> values = stringToList("A");
        ch.qos.logback.core.util.StringCollectionUtil.removeMatching(values, "B");
        org.junit.Assert.assertTrue(values.contains("A"));
    }

    @java.lang.SuppressWarnings(value = "unchecked")
    private java.util.List<java.lang.String> stringToList(java.lang.String... values) {
        java.util.List<java.lang.String> result = new java.util.ArrayList<java.lang.String>(values.length);
        result.addAll(java.util.Arrays.asList(values));
        return result;
    }
}

