/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import java.lang.management.ManagementFactory;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * The SystemUtilsJUnitTest class is a test suite of test cases for testing the contract and
 * functionality of the SystemUtils class.
 * <p/>
 *
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since GemFire 6.8
 */
public class SystemUtilsJUnitTest {
    @Test
    public void testIsAppleJVM() {
        final boolean expected = ManagementFactory.getRuntimeMXBean().getVmVendor().contains(SystemUtils.APPLE_JVM_VENDOR_NAME);
        Assert.assertEquals(expected, SystemUtils.isAppleJVM());
    }

    @Test
    public void testIsOracleJVM() {
        final boolean expected = ManagementFactory.getRuntimeMXBean().getVmVendor().contains(SystemUtils.ORACLE_JVM_VENDOR_NAME);
        Assert.assertEquals(expected, SystemUtils.isOracleJVM());
    }

    @Test
    public void testIsHotSpotVM() {
        final boolean expected = ManagementFactory.getRuntimeMXBean().getVmName().contains(SystemUtils.JAVA_HOTSPOT_JVM_NAME);
        Assert.assertEquals(expected, SystemUtils.isHotSpotVM());
    }

    @Test
    public void testIsJ9VM() {
        final boolean expected = ManagementFactory.getRuntimeMXBean().getVmName().contains(SystemUtils.IBM_J9_JVM_NAME);
        Assert.assertEquals(expected, SystemUtils.isJ9VM());
    }

    @Test
    public void testIsJRockitVM() {
        final boolean expected = ManagementFactory.getRuntimeMXBean().getVmName().contains(SystemUtils.ORACLE_JROCKIT_JVM_NAME);
        Assert.assertEquals(expected, SystemUtils.isJRockitVM());
    }

    @Test
    public void testIsLinux() {
        final boolean expected = ManagementFactory.getOperatingSystemMXBean().getName().contains(SystemUtils.LINUX_OS_NAME);
        Assert.assertEquals(expected, SystemUtils.isLinux());
    }

    @Test
    public void testIsMacOSX() {
        final boolean expected = ManagementFactory.getOperatingSystemMXBean().getName().contains(SystemUtils.MAC_OSX_NAME);
        Assert.assertEquals(expected, SystemUtils.isMacOSX());
    }

    @Test
    public void testIsWindows() throws Exception {
        final boolean expected = ManagementFactory.getOperatingSystemMXBean().getName().contains(SystemUtils.WINDOWS_OS_NAME);
        Assert.assertEquals(expected, SystemUtils.isWindows());
    }

    @Test
    public void getOsNameShouldReturnOsNameValue() {
        assertThat(SystemUtils.getOsName()).isEqualTo(System.getProperty("os.name"));
    }

    @Test
    public void getOsVersionShouldReturnOsVersionValue() {
        assertThat(SystemUtils.getOsVersion()).isEqualTo(System.getProperty("os.version"));
    }

    @Test
    public void getOsArchitectureShouldReturnOsArchValue() {
        assertThat(SystemUtils.getOsArchitecture()).isEqualTo(System.getProperty("os.arch"));
    }

    @Test
    public void getClassPathShouldReturnJavaClassPathValue() {
        assertThat(SystemUtils.getClassPath()).isEqualTo(System.getProperty("java.class.path"));
    }

    @Test
    public void getBootClassPathShouldReturnSunBootClassPathValue() {
        String value = System.getProperty("sun.boot.class.path");
        Assume.assumeNotNull(value);
        assertThat(SystemUtils.getBootClassPath()).isEqualTo(value);
    }
}

