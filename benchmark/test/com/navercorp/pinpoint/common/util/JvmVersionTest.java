/**
 * Copyright 2014 NAVER Corp.
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


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author hyungil.jeong
 */
public class JvmVersionTest {
    @Test
    public void testOnOrAfter() {
        // JDK 5
        Assert.assertTrue(JAVA_5.onOrAfter(JAVA_5));
        Assert.assertFalse(JAVA_5.onOrAfter(JAVA_6));
        Assert.assertFalse(JAVA_5.onOrAfter(JAVA_7));
        Assert.assertFalse(JAVA_5.onOrAfter(JAVA_8));
        Assert.assertFalse(JAVA_5.onOrAfter(JAVA_9));
        Assert.assertFalse(JAVA_5.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_5.onOrAfter(UNSUPPORTED));
        // JDK 6
        Assert.assertTrue(JAVA_6.onOrAfter(JAVA_5));
        Assert.assertTrue(JAVA_6.onOrAfter(JAVA_6));
        Assert.assertFalse(JAVA_6.onOrAfter(JAVA_7));
        Assert.assertFalse(JAVA_6.onOrAfter(JAVA_8));
        Assert.assertFalse(JAVA_6.onOrAfter(JAVA_9));
        Assert.assertFalse(JAVA_6.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_6.onOrAfter(UNSUPPORTED));
        // JDK 7
        Assert.assertTrue(JAVA_7.onOrAfter(JAVA_5));
        Assert.assertTrue(JAVA_7.onOrAfter(JAVA_6));
        Assert.assertTrue(JAVA_7.onOrAfter(JAVA_7));
        Assert.assertFalse(JAVA_7.onOrAfter(JAVA_8));
        Assert.assertFalse(JAVA_7.onOrAfter(JAVA_9));
        Assert.assertFalse(JAVA_7.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_7.onOrAfter(UNSUPPORTED));
        // JDK 8
        Assert.assertTrue(JAVA_8.onOrAfter(JAVA_5));
        Assert.assertTrue(JAVA_8.onOrAfter(JAVA_6));
        Assert.assertTrue(JAVA_8.onOrAfter(JAVA_7));
        Assert.assertTrue(JAVA_8.onOrAfter(JAVA_8));
        Assert.assertFalse(JAVA_8.onOrAfter(JAVA_9));
        Assert.assertFalse(JAVA_8.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_8.onOrAfter(UNSUPPORTED));
        // JDK 9
        Assert.assertTrue(JAVA_9.onOrAfter(JAVA_5));
        Assert.assertTrue(JAVA_9.onOrAfter(JAVA_6));
        Assert.assertTrue(JAVA_9.onOrAfter(JAVA_7));
        Assert.assertTrue(JAVA_9.onOrAfter(JAVA_8));
        Assert.assertTrue(JAVA_9.onOrAfter(JAVA_9));
        Assert.assertFalse(JAVA_9.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_9.onOrAfter(UNSUPPORTED));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_5));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_6));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_7));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_8));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_9));
        Assert.assertTrue(JAVA_10.onOrAfter(JAVA_10));
        Assert.assertFalse(JAVA_10.onOrAfter(JAVA_RECENT));
        Assert.assertFalse(JAVA_10.onOrAfter(UNSUPPORTED));
        Assert.assertTrue(JAVA_RECENT.onOrAfter(JAVA_11));
        // Unsupported
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_5));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_6));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_7));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_8));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_9));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(JAVA_10));
        Assert.assertFalse(UNSUPPORTED.onOrAfter(UNSUPPORTED));
    }

    @Test
    public void testGetFromDoubleVersion() {
        // JDK 5
        final JvmVersion.JvmVersion java_5 = JvmVersion.JvmVersion.getFromVersion(1.5F);
        Assert.assertSame(JAVA_5, java_5);
        // JDK 6
        final JvmVersion.JvmVersion java_6 = JvmVersion.JvmVersion.getFromVersion(1.6F);
        Assert.assertSame(JAVA_6, java_6);
        // JDK 7
        final JvmVersion.JvmVersion java_7 = JvmVersion.JvmVersion.getFromVersion(1.7F);
        Assert.assertSame(JAVA_7, java_7);
        // JDK 8
        final JvmVersion.JvmVersion java_8 = JvmVersion.JvmVersion.getFromVersion(1.8F);
        Assert.assertSame(JAVA_8, java_8);
        // JDK 9
        final JvmVersion.JvmVersion java_9 = JvmVersion.JvmVersion.getFromVersion(9.0F);
        Assert.assertSame(JAVA_9, java_9);
        // JDK 10
        final JvmVersion.JvmVersion java_10 = JvmVersion.JvmVersion.getFromVersion(10.0F);
        Assert.assertSame(JAVA_10, java_10);
    }

    @Test
    public void testGetFromDoubleVersion_exceptional_case() {
        // Unsupported
        final JvmVersion.JvmVersion java_unsupported = JvmVersion.JvmVersion.getFromVersion(0.9F);
        Assert.assertSame(UNSUPPORTED, java_unsupported);
        // new version
        final JvmVersion.JvmVersion java20 = JvmVersion.JvmVersion.getFromVersion(20.0F);
        Assert.assertSame(JAVA_RECENT, java20);
    }

    @Test
    public void testGetFromStringVersion() {
        // JDK 5
        final JvmVersion.JvmVersion java_5 = JvmVersion.JvmVersion.getFromVersion("1.5");
        Assert.assertSame(JAVA_5, java_5);
        // JDK 6
        final JvmVersion.JvmVersion java_6 = JvmVersion.JvmVersion.getFromVersion("1.6");
        Assert.assertSame(JAVA_6, java_6);
        // JDK 7
        final JvmVersion.JvmVersion java_7 = JvmVersion.JvmVersion.getFromVersion("1.7");
        Assert.assertSame(JAVA_7, java_7);
        // JDK 8
        final JvmVersion.JvmVersion java_8 = JvmVersion.JvmVersion.getFromVersion("1.8");
        Assert.assertSame(JAVA_8, java_8);
        // JDK 9
        final JvmVersion.JvmVersion java_9 = JvmVersion.JvmVersion.getFromVersion("9");
        Assert.assertSame(JAVA_9, java_9);
        // JDK 10
        final JvmVersion.JvmVersion java_10 = JvmVersion.JvmVersion.getFromVersion("10");
        Assert.assertSame(JAVA_10, java_10);
        // Unsupported
        final JvmVersion.JvmVersion java_unsupported = JvmVersion.JvmVersion.getFromVersion("abc");
        Assert.assertSame(UNSUPPORTED, java_unsupported);
    }

    @Test
    public void testGetFromClassVersion() {
        // JDK 5
        final JvmVersion.JvmVersion java_5 = JvmVersion.JvmVersion.getFromClassVersion(49);
        Assert.assertSame(JAVA_5, java_5);
        // JDK 6
        final JvmVersion.JvmVersion java_6 = JvmVersion.JvmVersion.getFromClassVersion(50);
        Assert.assertSame(JAVA_6, java_6);
        // JDK 7
        final JvmVersion.JvmVersion java_7 = JvmVersion.JvmVersion.getFromClassVersion(51);
        Assert.assertSame(JAVA_7, java_7);
        // JDK 8
        final JvmVersion.JvmVersion java_8 = JvmVersion.JvmVersion.getFromClassVersion(52);
        Assert.assertSame(JAVA_8, java_8);
        // JDK 9
        final JvmVersion.JvmVersion java_9 = JvmVersion.JvmVersion.getFromClassVersion(53);
        Assert.assertSame(JAVA_9, java_9);
        // JDK 10
        final JvmVersion.JvmVersion java_10 = JvmVersion.JvmVersion.getFromClassVersion(54);
        Assert.assertSame(JAVA_10, java_10);
        // Unsupported
        final JvmVersion.JvmVersion java_unsupported = JvmVersion.JvmVersion.getFromClassVersion((-1));
        Assert.assertSame(UNSUPPORTED, java_unsupported);
    }
}

