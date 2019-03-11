/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;


public class TypeUtilTest {
    @Test
    public void convertHexDigitTest() {
        Assertions.assertEquals(((byte) (0)), TypeUtil.convertHexDigit(((byte) ('0'))));
        Assertions.assertEquals(((byte) (9)), TypeUtil.convertHexDigit(((byte) ('9'))));
        Assertions.assertEquals(((byte) (10)), TypeUtil.convertHexDigit(((byte) ('a'))));
        Assertions.assertEquals(((byte) (10)), TypeUtil.convertHexDigit(((byte) ('A'))));
        Assertions.assertEquals(((byte) (15)), TypeUtil.convertHexDigit(((byte) ('f'))));
        Assertions.assertEquals(((byte) (15)), TypeUtil.convertHexDigit(((byte) ('F'))));
        Assertions.assertEquals(((int) (0)), TypeUtil.convertHexDigit(((int) ('0'))));
        Assertions.assertEquals(((int) (9)), TypeUtil.convertHexDigit(((int) ('9'))));
        Assertions.assertEquals(((int) (10)), TypeUtil.convertHexDigit(((int) ('a'))));
        Assertions.assertEquals(((int) (10)), TypeUtil.convertHexDigit(((int) ('A'))));
        Assertions.assertEquals(((int) (15)), TypeUtil.convertHexDigit(((int) ('f'))));
        Assertions.assertEquals(((int) (15)), TypeUtil.convertHexDigit(((int) ('F'))));
    }

    @Test
    public void testToHexInt() throws Exception {
        StringBuilder b = new StringBuilder();
        b.setLength(0);
        TypeUtil.toHex(((int) (0)), b);
        Assertions.assertEquals("00000000", b.toString());
        b.setLength(0);
        TypeUtil.toHex(Integer.MAX_VALUE, b);
        Assertions.assertEquals("7FFFFFFF", b.toString());
        b.setLength(0);
        TypeUtil.toHex(Integer.MIN_VALUE, b);
        Assertions.assertEquals("80000000", b.toString());
        b.setLength(0);
        TypeUtil.toHex(305419896, b);
        Assertions.assertEquals("12345678", b.toString());
        b.setLength(0);
        TypeUtil.toHex(-1698898192, b);
        Assertions.assertEquals("9ABCDEF0", b.toString());
    }

    @Test
    public void testToHexLong() throws Exception {
        StringBuilder b = new StringBuilder();
        b.setLength(0);
        TypeUtil.toHex(((long) (0)), b);
        Assertions.assertEquals("0000000000000000", b.toString());
        b.setLength(0);
        TypeUtil.toHex(Long.MAX_VALUE, b);
        Assertions.assertEquals("7FFFFFFFFFFFFFFF", b.toString());
        b.setLength(0);
        TypeUtil.toHex(Long.MIN_VALUE, b);
        Assertions.assertEquals("8000000000000000", b.toString());
        b.setLength(0);
        TypeUtil.toHex(1311768467463790320L, b);
        Assertions.assertEquals("123456789ABCDEF0", b.toString());
    }

    @Test
    public void testIsTrue() throws Exception {
        Assertions.assertTrue(TypeUtil.isTrue(Boolean.TRUE));
        Assertions.assertTrue(TypeUtil.isTrue(true));
        Assertions.assertTrue(TypeUtil.isTrue("true"));
        Assertions.assertTrue(TypeUtil.isTrue(new Object() {
            @Override
            public String toString() {
                return "true";
            }
        }));
        Assertions.assertFalse(TypeUtil.isTrue(Boolean.FALSE));
        Assertions.assertFalse(TypeUtil.isTrue(false));
        Assertions.assertFalse(TypeUtil.isTrue("false"));
        Assertions.assertFalse(TypeUtil.isTrue("blargle"));
        Assertions.assertFalse(TypeUtil.isTrue(new Object() {
            @Override
            public String toString() {
                return "false";
            }
        }));
    }

    @Test
    public void testIsFalse() throws Exception {
        Assertions.assertTrue(TypeUtil.isFalse(Boolean.FALSE));
        Assertions.assertTrue(TypeUtil.isFalse(false));
        Assertions.assertTrue(TypeUtil.isFalse("false"));
        Assertions.assertTrue(TypeUtil.isFalse(new Object() {
            @Override
            public String toString() {
                return "false";
            }
        }));
        Assertions.assertFalse(TypeUtil.isFalse(Boolean.TRUE));
        Assertions.assertFalse(TypeUtil.isFalse(true));
        Assertions.assertFalse(TypeUtil.isFalse("true"));
        Assertions.assertFalse(TypeUtil.isFalse("blargle"));
        Assertions.assertFalse(TypeUtil.isFalse(new Object() {
            @Override
            public String toString() {
                return "true";
            }
        }));
    }

    @Test
    public void testGetLocationOfClass() throws Exception {
        String mavenRepoPathProperty = System.getProperty("mavenRepoPath");
        Assumptions.assumeTrue((mavenRepoPathProperty != null));
        Path mavenRepoPath = Paths.get(mavenRepoPathProperty);
        String mavenRepo = mavenRepoPath.toFile().getPath().replaceAll("\\\\", "/");
        // Classes from maven dependencies
        MatcherAssert.assertThat(TypeUtil.getLocationOfClass(Assertions.class).toASCIIString(), CoreMatchers.containsString(mavenRepo));
        // Class from project dependencies
        MatcherAssert.assertThat(TypeUtil.getLocationOfClass(TypeUtil.class).toASCIIString(), CoreMatchers.containsString("/classes/"));
    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testGetLocation_JvmCore_JPMS() {
        // Class from JVM core
        String expectedJavaBase = "/java.base/";
        MatcherAssert.assertThat(TypeUtil.getLocationOfClass(String.class).toASCIIString(), CoreMatchers.containsString(expectedJavaBase));
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    public void testGetLocation_JvmCore_Java8RT() {
        // Class from JVM core
        String expectedJavaBase = "/rt.jar";
        MatcherAssert.assertThat(TypeUtil.getLocationOfClass(String.class).toASCIIString(), CoreMatchers.containsString(expectedJavaBase));
    }
}

