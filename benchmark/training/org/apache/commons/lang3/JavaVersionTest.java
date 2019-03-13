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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.JavaVersion}.
 */
public class JavaVersionTest {
    @Test
    public void testGetJavaVersion() {
        Assertions.assertEquals(JavaVersion.JAVA_0_9, JavaVersion.get("0.9"), "0.9 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_1, JavaVersion.get("1.1"), "1.1 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_2, JavaVersion.get("1.2"), "1.2 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_3, JavaVersion.get("1.3"), "1.3 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_4, JavaVersion.get("1.4"), "1.4 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_5, JavaVersion.get("1.5"), "1.5 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_6, JavaVersion.get("1.6"), "1.6 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_7, JavaVersion.get("1.7"), "1.7 failed");
        Assertions.assertEquals(JavaVersion.JAVA_1_8, JavaVersion.get("1.8"), "1.8 failed");
        Assertions.assertEquals(JavaVersion.JAVA_9, JavaVersion.get("9"), "9 failed");
        Assertions.assertEquals(JavaVersion.JAVA_10, JavaVersion.get("10"), "10 failed");
        Assertions.assertEquals(JavaVersion.JAVA_11, JavaVersion.get("11"), "11 failed");
        Assertions.assertEquals(JavaVersion.JAVA_RECENT, JavaVersion.get("1.10"), "1.10 failed");
        // assertNull("2.10 unexpectedly worked", get("2.10"));
        Assertions.assertEquals(JavaVersion.get("1.5"), JavaVersion.getJavaVersion("1.5"), "Wrapper method failed");
        Assertions.assertEquals(JavaVersion.JAVA_RECENT, JavaVersion.get("12"), "Unhandled");// LANG-1384

    }

    @Test
    public void testAtLeast() {
        Assertions.assertFalse(JavaVersion.JAVA_1_2.atLeast(JavaVersion.JAVA_1_5), "1.2 at least 1.5 passed");
        Assertions.assertTrue(JavaVersion.JAVA_1_5.atLeast(JavaVersion.JAVA_1_2), "1.5 at least 1.2 failed");
        Assertions.assertFalse(JavaVersion.JAVA_1_6.atLeast(JavaVersion.JAVA_1_7), "1.6 at least 1.7 passed");
        Assertions.assertTrue(JavaVersion.JAVA_0_9.atLeast(JavaVersion.JAVA_1_5), "0.9 at least 1.5 failed");
        Assertions.assertFalse(JavaVersion.JAVA_0_9.atLeast(JavaVersion.JAVA_1_6), "0.9 at least 1.6 passed");
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("1.2", JavaVersion.JAVA_1_2.toString());
    }
}

