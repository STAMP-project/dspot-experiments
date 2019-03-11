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
package org.apache.hadoop.util;


import org.junit.Assert;
import org.junit.Test;


public class TestVersionUtil {
    @Test
    public void testCompareVersions() {
        // Equal versions are equal.
        Assert.assertEquals(0, VersionUtil.compareVersions("2.0.0", "2.0.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("2.0.0a", "2.0.0a"));
        Assert.assertEquals(0, VersionUtil.compareVersions("2.0.0-SNAPSHOT", "2.0.0-SNAPSHOT"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1", "1"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1", "1.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1", "1.0.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0", "1"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0", "1.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0", "1.0.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0", "1"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0", "1.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0", "1.0.0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0-alpha-1", "1.0.0-a1"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0-alpha-2", "1.0.0-a2"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.0.0-alpha1", "1.0.0-alpha-1"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1a0", "1.0.0-alpha-0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1a0", "1-a0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.a0", "1-a0"));
        Assert.assertEquals(0, VersionUtil.compareVersions("1.a0", "1.0.0-alpha-0"));
        // Assert that lower versions are lower, and higher versions are higher.
        TestVersionUtil.assertExpectedValues("1", "2.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "2");
        TestVersionUtil.assertExpectedValues("1.0.0", "2.0.0");
        TestVersionUtil.assertExpectedValues("1.0", "2.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "2.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.0a");
        TestVersionUtil.assertExpectedValues("1.0.0.0", "2.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.0-dev");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.1");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.2");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.1.0");
        TestVersionUtil.assertExpectedValues("2.0.0", "10.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.0a");
        TestVersionUtil.assertExpectedValues("1.0.2a", "1.0.10");
        TestVersionUtil.assertExpectedValues("1.0.2a", "1.0.2b");
        TestVersionUtil.assertExpectedValues("1.0.2a", "1.0.2ab");
        TestVersionUtil.assertExpectedValues("1.0.0a1", "1.0.0a2");
        TestVersionUtil.assertExpectedValues("1.0.0a2", "1.0.0a10");
        // The 'a' in "1.a" is not followed by digit, thus not treated as "alpha",
        // and treated larger than "1.0", per maven's ComparableVersion class
        // implementation.
        TestVersionUtil.assertExpectedValues("1.0", "1.a");
        // The 'a' in "1.a0" is followed by digit, thus treated as "alpha-<digit>"
        TestVersionUtil.assertExpectedValues("1.a0", "1.0");
        TestVersionUtil.assertExpectedValues("1a0", "1.0");
        TestVersionUtil.assertExpectedValues("1.0.1-alpha-1", "1.0.1-alpha-2");
        TestVersionUtil.assertExpectedValues("1.0.1-beta-1", "1.0.1-beta-2");
        // Snapshot builds precede their eventual releases.
        TestVersionUtil.assertExpectedValues("1.0-SNAPSHOT", "1.0");
        TestVersionUtil.assertExpectedValues("1.0.0-SNAPSHOT", "1.0");
        TestVersionUtil.assertExpectedValues("1.0.0-SNAPSHOT", "1.0.0");
        TestVersionUtil.assertExpectedValues("1.0.0", "1.0.1-SNAPSHOT");
        TestVersionUtil.assertExpectedValues("1.0.1-SNAPSHOT", "1.0.1");
        TestVersionUtil.assertExpectedValues("1.0.1-SNAPSHOT", "1.0.2");
        TestVersionUtil.assertExpectedValues("1.0.1-alpha-1", "1.0.1-SNAPSHOT");
        TestVersionUtil.assertExpectedValues("1.0.1-beta-1", "1.0.1-SNAPSHOT");
        TestVersionUtil.assertExpectedValues("1.0.1-beta-2", "1.0.1-SNAPSHOT");
    }
}

