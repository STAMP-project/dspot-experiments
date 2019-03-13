/**
 * Copyright (C) 2017 The Gson authors
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
package com.google.gson.internal;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit and functional tests for {@link JavaVersion}
 *
 * @author Inderjeet Singh
 */
public class JavaVersionTest {
    // Borrowed some of test strings from https://github.com/prestodb/presto/blob/master/presto-main/src/test/java/com/facebook/presto/server/TestJavaVersion.java
    @Test
    public void testGetMajorJavaVersion() {
        JavaVersion.getMajorJavaVersion();
    }

    @Test
    public void testJava6() {
        Assert.assertEquals(6, JavaVersion.getMajorJavaVersion("1.6.0"));// http://www.oracle.com/technetwork/java/javase/version-6-141920.html

    }

    @Test
    public void testJava7() {
        Assert.assertEquals(7, JavaVersion.getMajorJavaVersion("1.7.0"));// http://www.oracle.com/technetwork/java/javase/jdk7-naming-418744.html

    }

    @Test
    public void testJava8() {
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8"));
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0"));
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0_131"));
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0_60-ea"));
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0_111-internal"));
        // openjdk8 per https://github.com/AdoptOpenJDK/openjdk-build/issues/93
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0-internal"));
        Assert.assertEquals(8, JavaVersion.getMajorJavaVersion("1.8.0_131-adoptopenjdk"));
    }

    @Test
    public void testJava9() {
        // Legacy style
        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9.0.4"));// Oracle JDK 9

        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9-Debian"));// Debian as reported in https://github.com/google/gson/issues/1310

        // New style
        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9-ea+19"));
        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9+100"));
        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9.0.1+20"));
        Assert.assertEquals(9, JavaVersion.getMajorJavaVersion("9.1.1+20"));
    }

    @Test
    public void testJava10() {
        Assert.assertEquals(10, JavaVersion.getMajorJavaVersion("10.0.1"));// Oracle JDK 10.0.1

    }

    @Test
    public void testUnknownVersionFormat() {
        Assert.assertEquals(6, JavaVersion.getMajorJavaVersion("Java9"));// unknown format

    }
}

