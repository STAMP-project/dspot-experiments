/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.lang3;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.SystemUtils}.
 *
 * Only limited testing can be performed.
 */
public class SystemUtilsTest {
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new SystemUtils());
        final Constructor<?>[] cons = SystemUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(SystemUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(SystemUtils.class.getModifiers()));
    }

    @Test
    public void testGetEnvironmentVariableAbsent() {
        final String name = "THIS_ENV_VAR_SHOULD_NOT_EXIST_FOR_THIS_TEST_TO_PASS";
        final String expected = System.getenv(name);
        Assertions.assertNull(expected);
        final String value = SystemUtils.getEnvironmentVariable(name, "DEFAULT");
        Assertions.assertEquals("DEFAULT", value);
    }

    @Test
    public void testGetEnvironmentVariablePresent() {
        final String name = "PATH";
        final String expected = System.getenv(name);
        final String value = SystemUtils.getEnvironmentVariable(name, null);
        Assertions.assertEquals(expected, value);
    }

    @Test
    public void testGetHostName() {
        final String hostName = SystemUtils.getHostName();
        final String expected = (SystemUtils.IS_OS_WINDOWS) ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
        Assertions.assertEquals(expected, hostName);
    }

    /**
     * Assumes no security manager exists.
     */
    @Test
    public void testGetJavaHome() {
        final File dir = SystemUtils.getJavaHome();
        Assertions.assertNotNull(dir);
        Assertions.assertTrue(dir.exists());
    }

    /**
     * Assumes no security manager exists.
     */
    @Test
    public void testGetJavaIoTmpDir() {
        final File dir = SystemUtils.getJavaIoTmpDir();
        Assertions.assertNotNull(dir);
        Assertions.assertTrue(dir.exists());
    }

    /**
     * Assumes no security manager exists.
     */
    @Test
    public void testGetUserDir() {
        final File dir = SystemUtils.getUserDir();
        Assertions.assertNotNull(dir);
        Assertions.assertTrue(dir.exists());
    }

    /**
     * Assumes no security manager exists.
     */
    @Test
    public void testGetUserHome() {
        final File dir = SystemUtils.getUserHome();
        Assertions.assertNotNull(dir);
        Assertions.assertTrue(dir.exists());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testIS_JAVA() {
        final String javaVersion = SystemUtils.JAVA_VERSION;
        if (javaVersion == null) {
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_1);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_2);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_3);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_4);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_5);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_6);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_7);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_8);
            Assertions.assertFalse(SystemUtils.IS_JAVA_1_9);
            Assertions.assertFalse(SystemUtils.IS_JAVA_9);
            Assertions.assertFalse(SystemUtils.IS_JAVA_10);
            Assertions.assertFalse(SystemUtils.IS_JAVA_11);
        } else
            if (javaVersion.startsWith("1.8")) {
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_1);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_2);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_3);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_4);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_5);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_6);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_7);
                Assertions.assertTrue(SystemUtils.IS_JAVA_1_8);
                Assertions.assertFalse(SystemUtils.IS_JAVA_1_9);
                Assertions.assertFalse(SystemUtils.IS_JAVA_9);
                Assertions.assertFalse(SystemUtils.IS_JAVA_10);
                Assertions.assertFalse(SystemUtils.IS_JAVA_11);
            } else
                if (javaVersion.startsWith("9")) {
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_1);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_2);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_3);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_4);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_5);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_6);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_7);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_1_8);
                    Assertions.assertTrue(SystemUtils.IS_JAVA_1_9);
                    Assertions.assertTrue(SystemUtils.IS_JAVA_9);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_10);
                    Assertions.assertFalse(SystemUtils.IS_JAVA_11);
                } else
                    if (javaVersion.startsWith("10")) {
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_1);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_2);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_3);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_4);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_5);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_6);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_7);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_8);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_1_9);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_9);
                        Assertions.assertTrue(SystemUtils.IS_JAVA_10);
                        Assertions.assertFalse(SystemUtils.IS_JAVA_11);
                    } else
                        if (javaVersion.startsWith("11")) {
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_1);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_2);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_3);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_4);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_5);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_6);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_7);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_8);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_1_9);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_9);
                            Assertions.assertFalse(SystemUtils.IS_JAVA_10);
                            Assertions.assertTrue(SystemUtils.IS_JAVA_11);
                        } else {
                            System.out.println(("Can't test IS_JAVA value: " + javaVersion));
                        }




    }

    @Test
    public void testIS_OS() {
        final String osName = System.getProperty("os.name");
        if (osName == null) {
            Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
            Assertions.assertFalse(SystemUtils.IS_OS_UNIX);
            Assertions.assertFalse(SystemUtils.IS_OS_SOLARIS);
            Assertions.assertFalse(SystemUtils.IS_OS_LINUX);
            Assertions.assertFalse(SystemUtils.IS_OS_MAC_OSX);
        } else
            if (osName.startsWith("Windows")) {
                Assertions.assertFalse(SystemUtils.IS_OS_UNIX);
                Assertions.assertTrue(SystemUtils.IS_OS_WINDOWS);
            } else
                if (osName.startsWith("Solaris")) {
                    Assertions.assertTrue(SystemUtils.IS_OS_SOLARIS);
                    Assertions.assertTrue(SystemUtils.IS_OS_UNIX);
                    Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                } else
                    if (osName.toLowerCase(Locale.ENGLISH).startsWith("linux")) {
                        Assertions.assertTrue(SystemUtils.IS_OS_LINUX);
                        Assertions.assertTrue(SystemUtils.IS_OS_UNIX);
                        Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                    } else
                        if (osName.startsWith("Mac OS X")) {
                            Assertions.assertTrue(SystemUtils.IS_OS_MAC_OSX);
                            Assertions.assertTrue(SystemUtils.IS_OS_UNIX);
                            Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                        } else
                            if (osName.startsWith("OS/2")) {
                                Assertions.assertTrue(SystemUtils.IS_OS_OS2);
                                Assertions.assertFalse(SystemUtils.IS_OS_UNIX);
                                Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                            } else
                                if (osName.startsWith("SunOS")) {
                                    Assertions.assertTrue(SystemUtils.IS_OS_SUN_OS);
                                    Assertions.assertTrue(SystemUtils.IS_OS_UNIX);
                                    Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                                } else
                                    if (osName.startsWith("FreeBSD")) {
                                        Assertions.assertTrue(SystemUtils.IS_OS_FREE_BSD);
                                        Assertions.assertTrue(SystemUtils.IS_OS_UNIX);
                                        Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                                    } else {
                                        System.out.println(("Can't test IS_OS value: " + osName));
                                    }







    }

    @Test
    public void testIS_zOS() {
        final String osName = System.getProperty("os.name");
        if (osName == null) {
            Assertions.assertFalse(SystemUtils.IS_OS_ZOS);
        } else
            if (osName.contains("z/OS")) {
                Assertions.assertFalse(SystemUtils.IS_OS_WINDOWS);
                Assertions.assertTrue(SystemUtils.IS_OS_ZOS);
            }

    }

    @Test
    public void testJavaVersionMatches() {
        String javaVersion = null;
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.0";
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.1";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.2";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.3.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.3.1";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.4.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.4.1";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.4.2";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.5.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.6.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.7.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "1.8.0";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
        javaVersion = "9";
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.0"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.1"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.2"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.3"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.4"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.5"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.6"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.7"));
        Assertions.assertFalse(SystemUtils.isJavaVersionMatch(javaVersion, "1.8"));
        Assertions.assertTrue(SystemUtils.isJavaVersionMatch(javaVersion, "9"));
    }

    @Test
    public void testIsJavaVersionAtLeast() {
        if (SystemUtils.IS_JAVA_1_8) {
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_1));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_2));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_3));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_4));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_7));
            Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8));
            Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
            Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_10));
            Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11));
        } else
            if (SystemUtils.IS_JAVA_9) {
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_1));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_2));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_3));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_4));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_7));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8));
                Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
                Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_10));
                Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11));
            } else
                if (SystemUtils.IS_JAVA_10) {
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_1));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_2));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_3));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_4));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_7));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
                    Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_10));
                    Assertions.assertFalse(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11));
                } else
                    if (SystemUtils.IS_JAVA_11) {
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_1));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_2));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_3));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_4));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_7));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_10));
                        Assertions.assertTrue(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11));
                    }



    }

    @Test
    public void testIsJavaVersionAtMost() {
        if (SystemUtils.IS_JAVA_1_8) {
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_1));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_2));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_3));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_4));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_5));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_6));
            Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_7));
            Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_1_8));
            Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_9));
            Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_10));
            Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_11));
        } else
            if (SystemUtils.IS_JAVA_9) {
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_1));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_2));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_3));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_4));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_5));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_6));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_7));
                Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_8));
                Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_9));
                Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_10));
                Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_11));
            } else
                if (SystemUtils.IS_JAVA_10) {
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_1));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_2));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_3));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_4));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_5));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_6));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_7));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_8));
                    Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_9));
                    Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_10));
                    Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_11));
                } else
                    if (SystemUtils.IS_JAVA_11) {
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_1));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_2));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_3));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_4));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_5));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_6));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_7));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_1_8));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_9));
                        Assertions.assertFalse(isJavaVersionAtMost(JavaVersion.JAVA_10));
                        Assertions.assertTrue(isJavaVersionAtMost(JavaVersion.JAVA_11));
                    }



    }

    @Test
    public void testOSMatchesName() {
        String osName = null;
        Assertions.assertFalse(SystemUtils.isOSNameMatch(osName, "Windows"));
        osName = "";
        Assertions.assertFalse(SystemUtils.isOSNameMatch(osName, "Windows"));
        osName = "Windows 95";
        Assertions.assertTrue(SystemUtils.isOSNameMatch(osName, "Windows"));
        osName = "Windows NT";
        Assertions.assertTrue(SystemUtils.isOSNameMatch(osName, "Windows"));
        osName = "OS/2";
        Assertions.assertFalse(SystemUtils.isOSNameMatch(osName, "Windows"));
    }

    @Test
    public void testOSMatchesNameAndVersion() {
        String osName = null;
        String osVersion = null;
        Assertions.assertFalse(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "";
        osVersion = "";
        Assertions.assertFalse(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "Windows 95";
        osVersion = "4.0";
        Assertions.assertFalse(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "Windows 95";
        osVersion = "4.1";
        Assertions.assertTrue(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "Windows 98";
        osVersion = "4.1";
        Assertions.assertTrue(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "Windows NT";
        osVersion = "4.0";
        Assertions.assertFalse(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
        osName = "OS/2";
        osVersion = "4.0";
        Assertions.assertFalse(SystemUtils.isOSMatch(osName, osVersion, "Windows 9", "4.1"));
    }

    @Test
    public void testOsVersionMatches() {
        String osVersion = null;
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        osVersion = "";
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        osVersion = "10";
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10.1"));
        osVersion = "10.1";
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1.1"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.10"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.10.1"));
        osVersion = "10.1.1";
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.1.1"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.10"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.10.1"));
        osVersion = "10.10";
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10.1"));
        osVersion = "10.10.1";
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1"));
        Assertions.assertFalse(SystemUtils.isOSVersionMatch(osVersion, "10.1.1"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10"));
        Assertions.assertTrue(SystemUtils.isOSVersionMatch(osVersion, "10.10.1"));
    }

    @Test
    public void testJavaAwtHeadless() {
        final String expectedStringValue = System.getProperty("java.awt.headless");
        final String expectedStringValueWithDefault = System.getProperty("java.awt.headless", "false");
        Assertions.assertNotNull(expectedStringValueWithDefault);
        final boolean expectedValue = Boolean.valueOf(expectedStringValue).booleanValue();
        if (expectedStringValue != null) {
            Assertions.assertEquals(expectedStringValue, SystemUtils.JAVA_AWT_HEADLESS);
        }
        Assertions.assertEquals(expectedValue, SystemUtils.isJavaAwtHeadless());
        Assertions.assertEquals(expectedStringValueWithDefault, ("" + (SystemUtils.isJavaAwtHeadless())));
    }
}

