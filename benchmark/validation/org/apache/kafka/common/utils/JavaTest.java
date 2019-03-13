/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import Java.Version;
import org.junit.Assert;
import org.junit.Test;


public class JavaTest {
    private String javaVendor;

    @Test
    public void testIsIBMJdk() {
        System.setProperty("java.vendor", "Oracle Corporation");
        Assert.assertFalse(Java.isIbmJdk());
        System.setProperty("java.vendor", "IBM Corporation");
        Assert.assertTrue(Java.isIbmJdk());
    }

    @Test
    public void testLoadKerberosLoginModule() throws ClassNotFoundException {
        String clazz = (Java.isIbmJdk()) ? "com.ibm.security.auth.module.Krb5LoginModule" : "com.sun.security.auth.module.Krb5LoginModule";
        Class.forName(clazz);
    }

    @Test
    public void testJavaVersion() {
        Java.Version v = Java.parseVersion("9");
        Assert.assertEquals(9, v.majorVersion);
        Assert.assertEquals(0, v.minorVersion);
        Assert.assertTrue(v.isJava9Compatible());
        v = Java.parseVersion("9.0.1");
        Assert.assertEquals(9, v.majorVersion);
        Assert.assertEquals(0, v.minorVersion);
        Assert.assertTrue(v.isJava9Compatible());
        v = Java.parseVersion("9.0.0.15");// Azul Zulu

        Assert.assertEquals(9, v.majorVersion);
        Assert.assertEquals(0, v.minorVersion);
        Assert.assertTrue(v.isJava9Compatible());
        v = Java.parseVersion("9.1");
        Assert.assertEquals(9, v.majorVersion);
        Assert.assertEquals(1, v.minorVersion);
        Assert.assertTrue(v.isJava9Compatible());
        v = Java.parseVersion("1.8.0_152");
        Assert.assertEquals(1, v.majorVersion);
        Assert.assertEquals(8, v.minorVersion);
        Assert.assertFalse(v.isJava9Compatible());
        v = Java.parseVersion("1.7.0_80");
        Assert.assertEquals(1, v.majorVersion);
        Assert.assertEquals(7, v.minorVersion);
        Assert.assertFalse(v.isJava9Compatible());
    }
}

