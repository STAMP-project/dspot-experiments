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


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests for LazyList utility class.
 */
public class JavaVersionTest {
    @Test
    public void testAndroid() {
        JavaVersion version = JavaVersion.parse("0.9");
        MatcherAssert.assertThat(version.toString(), Matchers.is("0.9"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(0));
    }

    @Test
    public void test9() {
        JavaVersion version = JavaVersion.parse("9.0.1");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9.0.1"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test9nano() {
        JavaVersion version = JavaVersion.parse("9.0.1.3");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9.0.1.3"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test9build() {
        JavaVersion version = JavaVersion.parse("9.0.1+11");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9.0.1+11"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test9all() {
        JavaVersion version = JavaVersion.parse("9.0.1-ea+11-b01");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9.0.1-ea+11-b01"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test9yuck() {
        JavaVersion version = JavaVersion.parse("9.0.1.2.3-ea+11-b01");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9.0.1.2.3-ea+11-b01"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test10ea() {
        JavaVersion version = JavaVersion.parse("10-ea");
        MatcherAssert.assertThat(version.toString(), Matchers.is("10-ea"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(10));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(10));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(0));
    }

    @Test
    public void test8() {
        JavaVersion version = JavaVersion.parse("1.8.0_152");
        MatcherAssert.assertThat(version.toString(), Matchers.is("1.8.0_152"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(1));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(0));
    }

    @Test
    public void test8ea() {
        JavaVersion version = JavaVersion.parse("1.8.1_03-ea");
        MatcherAssert.assertThat(version.toString(), Matchers.is("1.8.1_03-ea"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(1));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void test3eaBuild() {
        JavaVersion version = JavaVersion.parse("1.3.1_05-ea-b01");
        MatcherAssert.assertThat(version.toString(), Matchers.is("1.3.1_05-ea-b01"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(3));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(1));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(3));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(1));
    }

    @Test
    public void testUbuntu() {
        JavaVersion version = JavaVersion.parse("9-Ubuntu+0-9b181-4");
        MatcherAssert.assertThat(version.toString(), Matchers.is("9-Ubuntu+0-9b181-4"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(9));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(0));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(0));
    }

    @Test
    public void testUbuntu8() {
        JavaVersion version = JavaVersion.parse("1.8.0_151-8u151-b12-1~deb9u1-b12");
        MatcherAssert.assertThat(version.toString(), Matchers.is("1.8.0_151-8u151-b12-1~deb9u1-b12"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMajor(), Matchers.is(1));
        MatcherAssert.assertThat(version.getMinor(), Matchers.is(8));
        MatcherAssert.assertThat(version.getMicro(), Matchers.is(0));
    }
}

