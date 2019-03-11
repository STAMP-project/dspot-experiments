/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.configuration;


import CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that checks that all packages that need to be loaded 'parent-first' are also
 * in the parent-first patterns.
 */
public class ParentFirstPatternsTest extends TestLogger {
    private static final HashSet<String> PARENT_FIRST_PACKAGES = new HashSet(Arrays.asList(ALWAYS_PARENT_FIRST_LOADER_PATTERNS.defaultValue().split(";")));

    /**
     * All java and Flink classes must be loaded parent first.
     */
    @Test
    public void testAllCorePatterns() {
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("java."));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.apache.flink."));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("javax.annotation."));
    }

    /**
     * To avoid multiple binding problems and warnings for logger frameworks, we load them
     * parent-first.
     */
    @Test
    public void testLoggersParentFirst() {
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.slf4j"));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.apache.log4j"));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.apache.logging"));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.apache.commons.logging"));
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("ch.qos.logback"));
    }

    /**
     * As long as Scala is not a pure user library, but is also used in the Flink runtime, we need
     * to load all Scala classes parent-first.
     */
    @Test
    public void testScalaParentFirst() {
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("scala."));
    }

    /**
     * As long as we have Hadoop classes leaking through some of Flink's APIs (example bucketing sink),
     * we need to make them parent first.
     */
    @Test
    public void testHadoopParentFirst() {
        Assert.assertTrue(ParentFirstPatternsTest.PARENT_FIRST_PACKAGES.contains("org.apache.hadoop."));
    }
}

