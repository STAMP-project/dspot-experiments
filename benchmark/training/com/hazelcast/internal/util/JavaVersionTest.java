/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.util;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class JavaVersionTest extends HazelcastTestSupport {
    @Test
    public void parseVersion() {
        Assert.assertEquals(JavaVersion.UNKNOWN, JavaVersion.parseVersion("foo"));
        Assert.assertEquals(JavaVersion.JAVA_1_6, JavaVersion.parseVersion("1.6"));
        Assert.assertEquals(JavaVersion.JAVA_1_7, JavaVersion.parseVersion("1.7"));
        Assert.assertEquals(JavaVersion.JAVA_1_8, JavaVersion.parseVersion("1.8"));
        Assert.assertEquals(JavaVersion.JAVA_9, JavaVersion.parseVersion("9-ea"));
        Assert.assertEquals(JavaVersion.JAVA_9, JavaVersion.parseVersion("9"));
        Assert.assertEquals(JavaVersion.JAVA_10, JavaVersion.parseVersion("10.0.0.2"));
        Assert.assertEquals(JavaVersion.JAVA_11, JavaVersion.parseVersion("11-ea"));
        Assert.assertEquals(JavaVersion.JAVA_11, JavaVersion.parseVersion("11"));
        Assert.assertEquals(JavaVersion.JAVA_12, JavaVersion.parseVersion("12-ea"));
        Assert.assertEquals(JavaVersion.JAVA_12, JavaVersion.parseVersion("12"));
    }

    @Test
    public void testIsAtLeast_unknown() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.UNKNOWN));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_1_6));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_1_7));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_11));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_12));
    }

    @Test
    public void testIsAtLeast_1_6() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.UNKNOWN));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_6));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_7));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_11));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_12));
    }

    @Test
    public void testIsAtLeast_1_7() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.UNKNOWN));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_6));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_7));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_11));
    }

    @Test
    public void testIsAtLeast_1_8() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.UNKNOWN));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_1_6));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_1_7));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_11));
    }

    @Test
    public void testIsAtLeast_1_9() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.UNKNOWN));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_1_6));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_1_7));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_1_8));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_11));
    }

    @Test
    public void testIsAtLeastSequence() {
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.UNKNOWN));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_6));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_1_7));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_1_8));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_10, JavaVersion.JAVA_9));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_11, JavaVersion.JAVA_10));
        Assert.assertTrue(JavaVersion.isAtLeast(JavaVersion.JAVA_12, JavaVersion.JAVA_11));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.UNKNOWN, JavaVersion.JAVA_1_6));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_7));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_1_8, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_9, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_10, JavaVersion.JAVA_11));
        Assert.assertFalse(JavaVersion.isAtLeast(JavaVersion.JAVA_11, JavaVersion.JAVA_12));
    }

    @Test
    public void testIsAtMostSequence() {
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_6));
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_1_6, JavaVersion.JAVA_1_7));
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_1_8, JavaVersion.JAVA_9));
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_9, JavaVersion.JAVA_10));
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_11, JavaVersion.JAVA_12));
        Assert.assertTrue(JavaVersion.isAtMost(JavaVersion.JAVA_12, JavaVersion.JAVA_12));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_1_7, JavaVersion.JAVA_1_6));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_1_8, JavaVersion.JAVA_1_7));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_9, JavaVersion.JAVA_1_8));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_10, JavaVersion.JAVA_9));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_11, JavaVersion.JAVA_10));
        Assert.assertFalse(JavaVersion.isAtMost(JavaVersion.JAVA_12, JavaVersion.JAVA_11));
    }
}

