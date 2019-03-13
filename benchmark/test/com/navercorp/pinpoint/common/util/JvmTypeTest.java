/**
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.util;


import JvmType.IBM;
import JvmType.OPENJDK;
import JvmType.ORACLE;
import JvmType.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class JvmTypeTest {
    @Test
    public void fromVendorNullParameter() {
        JvmType actualType = JvmType.fromVendor(null);
        Assert.assertSame(UNKNOWN, actualType);
    }

    @Test
    public void fromVendorEmptyParameter() {
        JvmType actualType = JvmType.fromVendor("");
        Assert.assertSame(UNKNOWN, actualType);
    }

    @Test
    public void fromVendorValidParameter() {
        Assert.assertSame(IBM, JvmType.fromVendor("IBM"));
        Assert.assertSame(IBM, JvmType.fromVendor("ibm"));
        Assert.assertSame(ORACLE, JvmType.fromVendor("Oracle"));
        Assert.assertSame(ORACLE, JvmType.fromVendor("oracle"));
        Assert.assertSame(OPENJDK, JvmType.fromVendor("OpenJDK"));
        Assert.assertSame(OPENJDK, JvmType.fromVendor("openjdk"));
    }

    @Test
    public void fromVendorInvalidParameter() {
        Assert.assertSame(UNKNOWN, JvmType.fromVendor("Some Invalid Parameter"));
    }

    @Test
    public void fromVmNameNullParameter() {
        JvmType actualType = JvmType.fromVendor(null);
        Assert.assertSame(UNKNOWN, actualType);
    }

    @Test
    public void fromVmNameEmptyParameter() {
        JvmType actualType = JvmType.fromVendor("");
        Assert.assertSame(UNKNOWN, actualType);
    }

    @Test
    public void fromVmNameValidParameter() {
        final String openJdkJavaVmName = "OpenJDK 64-Bit Server VM";
        final String oracleJavaVmName = "Java HotSpot(TM) 64-Bit Server VM";
        final String ibmJavaVmName = "IBM J9 VM";
        Assert.assertSame(OPENJDK, JvmType.fromVmName(openJdkJavaVmName));
        Assert.assertSame(ORACLE, JvmType.fromVmName(oracleJavaVmName));
        Assert.assertSame(IBM, JvmType.fromVmName(ibmJavaVmName));
    }

    @Test
    public void fromVmNameInvalidParameter() {
        Assert.assertSame(UNKNOWN, JvmType.fromVmName("Some Invalid Parameter"));
    }
}

