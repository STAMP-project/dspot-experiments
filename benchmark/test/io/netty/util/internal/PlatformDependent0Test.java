/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;


import java.security.Permission;
import org.junit.Assert;
import org.junit.Test;


public class PlatformDependent0Test {
    @Test
    public void testNewDirectBufferNegativeMemoryAddress() {
        PlatformDependent0Test.testNewDirectBufferMemoryAddress((-1));
    }

    @Test
    public void testNewDirectBufferNonNegativeMemoryAddress() {
        PlatformDependent0Test.testNewDirectBufferMemoryAddress(10);
    }

    @Test
    public void testNewDirectBufferZeroMemoryAddress() {
        PlatformDependent0.newDirectBuffer(0, 10);
    }

    @Test
    public void testMajorVersionFromJavaSpecificationVersion() {
        final SecurityManager current = System.getSecurityManager();
        try {
            System.setSecurityManager(new SecurityManager() {
                @Override
                public void checkPropertyAccess(String key) {
                    if (key.equals("java.specification.version")) {
                        // deny
                        throw new SecurityException(key);
                    }
                }

                // so we can restore the security manager
                @Override
                public void checkPermission(Permission perm) {
                }
            });
            Assert.assertEquals(6, PlatformDependent0.majorVersionFromJavaSpecificationVersion());
        } finally {
            System.setSecurityManager(current);
        }
    }

    @Test
    public void testMajorVersion() {
        Assert.assertEquals(6, PlatformDependent0.majorVersion("1.6"));
        Assert.assertEquals(7, PlatformDependent0.majorVersion("1.7"));
        Assert.assertEquals(8, PlatformDependent0.majorVersion("1.8"));
        Assert.assertEquals(8, PlatformDependent0.majorVersion("8"));
        Assert.assertEquals(9, PlatformDependent0.majorVersion("1.9"));// early version of JDK 9 before Project Verona

        Assert.assertEquals(9, PlatformDependent0.majorVersion("9"));
    }
}

