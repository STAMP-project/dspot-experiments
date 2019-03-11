/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.packages;


import TestSize.ENORMOUS;
import TestSize.LARGE;
import TestSize.MEDIUM;
import TestSize.SMALL;
import TestTimeout.ETERNAL;
import TestTimeout.LONG;
import TestTimeout.MODERATE;
import TestTimeout.SHORT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the various methods of {@link TestSize}
 */
@RunWith(JUnit4.class)
public class TestSizeTest {
    @Test
    public void testBasicConversion() {
        assertThat(TestSize.valueOf("SMALL")).isEqualTo(SMALL);
        assertThat(TestSize.valueOf("MEDIUM")).isEqualTo(MEDIUM);
        assertThat(TestSize.valueOf("LARGE")).isEqualTo(LARGE);
        assertThat(TestSize.valueOf("ENORMOUS")).isEqualTo(ENORMOUS);
    }

    @Test
    public void testGetDefaultTimeout() {
        assertThat(SMALL.getDefaultTimeout()).isEqualTo(SHORT);
        assertThat(MEDIUM.getDefaultTimeout()).isEqualTo(MODERATE);
        assertThat(LARGE.getDefaultTimeout()).isEqualTo(LONG);
        assertThat(ENORMOUS.getDefaultTimeout()).isEqualTo(ETERNAL);
    }

    @Test
    public void testGetDefaultShards() {
        assertThat(SMALL.getDefaultShards()).isEqualTo(2);
        assertThat(MEDIUM.getDefaultShards()).isEqualTo(10);
        assertThat(LARGE.getDefaultShards()).isEqualTo(20);
        assertThat(ENORMOUS.getDefaultShards()).isEqualTo(30);
    }

    @Test
    public void testGetTestSizeFromString() {
        assertThat(TestSize.getTestSize("Small")).isNull();
        assertThat(TestSize.getTestSize("Koala")).isNull();
        assertThat(TestSize.getTestSize("small")).isEqualTo(SMALL);
        assertThat(TestSize.getTestSize("medium")).isEqualTo(MEDIUM);
        assertThat(TestSize.getTestSize("large")).isEqualTo(LARGE);
        assertThat(TestSize.getTestSize("enormous")).isEqualTo(ENORMOUS);
    }

    @Test
    public void testGetTestSizeFromDefaultTimeout() {
        assertThat(TestSize.getTestSize(SHORT)).isEqualTo(SMALL);
        assertThat(TestSize.getTestSize(MODERATE)).isEqualTo(MEDIUM);
        assertThat(TestSize.getTestSize(LONG)).isEqualTo(LARGE);
        assertThat(TestSize.getTestSize(ETERNAL)).isEqualTo(ENORMOUS);
    }
}

