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
package org.eclipse.jetty.webapp;


import java.net.URI;
import java.nio.file.Paths;
import org.eclipse.jetty.util.TypeUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;


public class ClasspathPatternTest {
    private final ClasspathPattern _pattern = new ClasspathPattern();

    @Test
    public void testClassMatch() {
        Assertions.assertTrue(_pattern.match("org.example.FooBar"));
        Assertions.assertTrue(_pattern.match("org.example.Nested"));
        Assertions.assertFalse(_pattern.match("org.example.Unknown"));
        Assertions.assertFalse(_pattern.match("org.example.FooBar.Unknown"));
    }

    @Test
    public void testPackageMatch() {
        Assertions.assertTrue(_pattern.match("org.package.Something"));
        Assertions.assertTrue(_pattern.match("org.package.other.Something"));
        Assertions.assertFalse(_pattern.match("org.example.Unknown"));
        Assertions.assertFalse(_pattern.match("org.example.FooBar.Unknown"));
        Assertions.assertFalse(_pattern.match("org.example.FooBarElse"));
    }

    @Test
    public void testExplicitNestedMatch() {
        Assertions.assertTrue(_pattern.match("org.example.Nested$Something"));
        Assertions.assertFalse(_pattern.match("org.example.Nested$Minus"));
        Assertions.assertTrue(_pattern.match("org.example.Nested$Other"));
    }

    @Test
    public void testImplicitNestedMatch() {
        Assertions.assertTrue(_pattern.match("org.example.FooBar$Other"));
        Assertions.assertTrue(_pattern.match("org.example.Nested$Other"));
    }

    @Test
    public void testDoubledNested() {
        Assertions.assertTrue(_pattern.match("org.example.Nested$Something$Else"));
        Assertions.assertFalse(_pattern.match("org.example.Nested$Minus$Else"));
    }

    @Test
    public void testMatchAll() {
        _pattern.clear();
        _pattern.add(".");
        Assertions.assertTrue(_pattern.match("org.example.Anything"));
        Assertions.assertTrue(_pattern.match("org.example.Anything$Else"));
    }

    @SuppressWarnings("restriction")
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testIncludedLocations() throws Exception {
        // jar from JVM classloader
        URI loc_string = TypeUtil.getLocationOfClass(String.class);
        // System.err.println(loc_string);
        // a jar from maven repo jar
        URI loc_junit = TypeUtil.getLocationOfClass(Test.class);
        // System.err.println(loc_junit);
        // class file
        URI loc_test = TypeUtil.getLocationOfClass(ClasspathPatternTest.class);
        // System.err.println(loc_test);
        ClasspathPattern pattern = new ClasspathPattern();
        pattern.include("something");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(false));
        // Add directory for both JVM classes
        pattern.include(Paths.get(loc_string).getParent().toUri().toString());
        // Add jar for individual class and classes directory
        pattern.include(loc_junit.toString(), loc_test.toString());
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
        pattern.add("-java.lang.String");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
    }

    @SuppressWarnings("restriction")
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testIncludedLocationsOrModule() throws Exception {
        // jar from JVM classloader
        URI mod_string = TypeUtil.getLocationOfClass(String.class);
        // System.err.println(mod_string);
        // a jar from maven repo jar
        URI loc_junit = TypeUtil.getLocationOfClass(Test.class);
        // System.err.println(loc_junit);
        // class file
        URI loc_test = TypeUtil.getLocationOfClass(ClasspathPatternTest.class);
        // System.err.println(loc_test);
        ClasspathPattern pattern = new ClasspathPattern();
        pattern.include("something");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(false));
        // Add module for all JVM base classes
        pattern.include("jrt:/java.base");
        // Add jar for individual class and classes directory
        pattern.include(loc_junit.toString(), loc_test.toString());
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
        pattern.add("-java.lang.String");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
    }

    @SuppressWarnings("restriction")
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    public void testExcludeLocations() throws Exception {
        // jar from JVM classloader
        URI loc_string = TypeUtil.getLocationOfClass(String.class);
        // System.err.println(loc_string);
        // a jar from maven repo jar
        URI loc_junit = TypeUtil.getLocationOfClass(Test.class);
        // System.err.println(loc_junit);
        // class file
        URI loc_test = TypeUtil.getLocationOfClass(ClasspathPatternTest.class);
        // System.err.println(loc_test);
        ClasspathPattern pattern = new ClasspathPattern();
        // include everything
        pattern.include(".");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
        // Add directory for both JVM classes
        pattern.exclude(Paths.get(loc_string).getParent().toUri().toString());
        // Add jar for individual class and classes directory
        pattern.exclude(loc_junit.toString(), loc_test.toString());
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(false));
    }

    @SuppressWarnings("restriction")
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    public void testExcludeLocationsOrModule() throws Exception {
        // jar from JVM classloader
        URI mod_string = TypeUtil.getLocationOfClass(String.class);
        // System.err.println(mod_string);
        // a jar from maven repo jar
        URI loc_junit = TypeUtil.getLocationOfClass(Test.class);
        // System.err.println(loc_junit);
        // class file
        URI loc_test = TypeUtil.getLocationOfClass(ClasspathPatternTest.class);
        // System.err.println(loc_test);
        ClasspathPattern pattern = new ClasspathPattern();
        // include everything
        pattern.include(".");
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(true));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(true));
        // Add directory for both JVM classes
        pattern.exclude("jrt:/java.base/");
        // Add jar for individual class and classes directory
        pattern.exclude(loc_junit.toString(), loc_test.toString());
        MatcherAssert.assertThat(pattern.match(String.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(Test.class), Matchers.is(false));
        MatcherAssert.assertThat(pattern.match(ClasspathPatternTest.class), Matchers.is(false));
    }

    @Test
    public void testLarge() {
        ClasspathPattern pattern = new ClasspathPattern();
        for (int i = 0; i < 500; i++) {
            Assertions.assertTrue(pattern.add((((("n" + i) + ".") + (Integer.toHexString((100 + i)))) + ".Name")));
        }
        for (int i = 0; i < 500; i++) {
            Assertions.assertTrue(pattern.match((((("n" + i) + ".") + (Integer.toHexString((100 + i)))) + ".Name")));
        }
    }

    @Test
    public void testJvmModule() {
        URI uri = TypeUtil.getLocationOfClass(String.class);
        System.err.println(uri);
        System.err.println(uri.toString().split("/")[0]);
        System.err.println(uri.toString().split("/")[1]);
    }
}

