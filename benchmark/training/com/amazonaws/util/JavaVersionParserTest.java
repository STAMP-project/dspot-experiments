/**
 * Copyright 2015-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import KnownJavaVersions.JAVA_6;
import KnownJavaVersions.JAVA_7;
import KnownJavaVersions.JAVA_8;
import KnownJavaVersions.JAVA_9;
import KnownJavaVersions.UNKNOWN;
import com.amazonaws.util.JavaVersionParser.JavaVersion;
import com.amazonaws.util.JavaVersionParser.KnownJavaVersions;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JavaVersionParserTest {
    @Test
    public void verifyHashCodeAndEquals_JavaVersion() {
        EqualsVerifier.forClass(JavaVersion.class).verify();
    }

    @Test
    public void getCurrentVersion_VersionIsCachedInMemory() {
        System.setProperty(JavaVersionParser.JAVA_VERSION_PROPERTY, "1.6.0_40");
        JavaVersion currentVersion = JavaVersionParser.getCurrentJavaVersion();
        System.setProperty(JavaVersionParser.JAVA_VERSION_PROPERTY, "1.7.1_80");
        JavaVersion versionAfterChangingProperty = JavaVersionParser.getCurrentJavaVersion();
        Assert.assertEquals(currentVersion, versionAfterChangingProperty);
    }

    @Test
    public void nullVersion_ReturnsUnknownVersion() {
        JavaVersion version = JavaVersionParser.parseJavaVersion(null);
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(null, null, null, null), version);
        Assert.assertEquals(UNKNOWN, version.getKnownVersion());
    }

    @Test
    public void emptyVersion_ReturnsUnknownVersion() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(null, null, null, null), version);
        Assert.assertEquals(UNKNOWN, version.getKnownVersion());
    }

    @Test
    public void garbageVersion_ReturnsUnknownVersion() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("invalid-version");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(null, null, null, null), version);
        Assert.assertEquals(UNKNOWN, version.getKnownVersion());
    }

    @Test
    public void validJava6Version_WithUpdateNumber() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.6.0_65");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 6, 0, 65), version);
        Assert.assertEquals(JAVA_6, version.getKnownVersion());
    }

    @Test
    public void validJava6Version_WithMultiDigitMaintenanceNumber() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.6.101_65");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 6, 101, 65), version);
        Assert.assertEquals(JAVA_6, version.getKnownVersion());
    }

    @Test
    public void validJava6Version_WithoutUpdateNumber_WithIdentifier() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.6.3-65");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 6, 3, null), version);
        Assert.assertEquals(JAVA_6, version.getKnownVersion());
    }

    @Test
    public void validJava7Version_WithUpdateNumber() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.7.0_1234");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 7, 0, 1234), version);
        Assert.assertEquals(JAVA_7, version.getKnownVersion());
    }

    @Test
    public void validJava7Version_WithoutUpdateNumber() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.7.0");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 7, 0, null), version);
        Assert.assertEquals(JAVA_7, version.getKnownVersion());
    }

    @Test
    public void validJava8Version_WithUpdateNumberAndIdentifier() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.8.0_12-b24");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 8, 0, 12), version);
        Assert.assertEquals(JAVA_8, version.getKnownVersion());
    }

    @Test
    public void validJava9Version_NonZeroMaintenanceNumber_WithUpdateNumberAndIdentifier() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.9.1_00-someIdentifier");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 9, 1, 0), version);
        Assert.assertEquals(JAVA_9, version.getKnownVersion());
    }

    @Test
    public void doubleDigitMajorVersion_ParsesMajorVersionCorrectly() {
        JavaVersion version = JavaVersionParser.parseJavaVersion("1.10.1_00");
        JavaVersionParserTest.assertJavaVersionsEqual(JavaVersionParserTest.jv(1, 10, 1, 0), version);
        Assert.assertEquals(UNKNOWN, version.getKnownVersion());
    }

    @Test
    public void compare_DifferentMajorVersions() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 0, 0);
        JavaVersion second = JavaVersionParserTest.jv(1, 6, 0, 0);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void compare_DifferentMaintenanceVersions() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 5, 0);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, 1, 0);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void compare_DifferentUpdateNumbers() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 0, 60);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, 0, 40);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void compare_NullUpdateNumbers() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 0, null);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, 0, null);
        Assert.assertThat(first, Matchers.comparesEqualTo(second));
    }

    @Test
    public void compare_NullMaintenanceNumbers() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, null, 0);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, null, 0);
        Assert.assertThat(first, Matchers.comparesEqualTo(second));
    }

    @Test
    public void compare_NullMajorVersions() {
        JavaVersion first = JavaVersionParserTest.jv(1, null, 0, 0);
        JavaVersion second = JavaVersionParserTest.jv(1, null, 0, 0);
        Assert.assertThat(first, Matchers.comparesEqualTo(second));
    }

    @Test
    public void compare_NullMajorVersionFamilies() {
        JavaVersion first = JavaVersionParserTest.jv(null, 7, 0, 0);
        JavaVersion second = JavaVersionParserTest.jv(null, 7, 0, 0);
        Assert.assertThat(first, Matchers.comparesEqualTo(second));
    }

    @Test
    public void compare_EqualVersions() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 0, 60);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, 0, 60);
        Assert.assertThat(first, Matchers.comparesEqualTo(second));
    }

    @Test
    public void compare_FirstVersionUnknown() {
        JavaVersion first = JavaVersionParserTest.jv(1, 10, 0, 60);
        JavaVersion second = JavaVersionParserTest.jv(1, 7, 0, 0);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void compare_SecondVersionUnknown() {
        JavaVersion first = JavaVersionParserTest.jv(1, 7, 0, 60);
        JavaVersion second = JavaVersionParserTest.jv(1, 5, 0, 60);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void compare_BothVersionsUnknown() {
        JavaVersion first = JavaVersionParserTest.jv(1, 99, 0, 0);
        JavaVersion second = JavaVersionParserTest.jv(1, 5, 0, 60);
        Assert.assertThat(first, Matchers.greaterThan(second));
        Assert.assertThat(second, Matchers.lessThan(first));
    }

    @Test
    public void knownJavaVersions_Java6() {
        Assert.assertEquals(JAVA_6, KnownJavaVersions.fromMajorVersion(1, 6));
    }

    @Test
    public void knownJavaVersions_Java7() {
        Assert.assertEquals(JAVA_7, KnownJavaVersions.fromMajorVersion(1, 7));
    }

    @Test
    public void knownJavaVersions_Java8() {
        Assert.assertEquals(JAVA_8, KnownJavaVersions.fromMajorVersion(1, 8));
    }

    @Test
    public void knownJavaVersions_Java9() {
        Assert.assertEquals(JAVA_9, KnownJavaVersions.fromMajorVersion(1, 9));
    }

    @Test
    public void unknownJavaVersions() {
        Assert.assertEquals(UNKNOWN, KnownJavaVersions.fromMajorVersion(1, 10));
        Assert.assertEquals(UNKNOWN, KnownJavaVersions.fromMajorVersion((-8), 0));
        Assert.assertEquals(UNKNOWN, KnownJavaVersions.fromMajorVersion(1, 91));
        Assert.assertEquals(UNKNOWN, KnownJavaVersions.fromMajorVersion(2, 0));
    }
}

