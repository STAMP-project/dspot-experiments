/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api.utils;


import org.junit.Assert;
import org.junit.Test;


public class VersionTest {
    private static final Version V34_0 = Version.create("34.0");

    private static final Version V34_3 = Version.create("34.3");

    private static final Version V35_0 = Version.create("35.0");

    @Test
    public void shouldCreate() {
        final Version version = VersionTest.V34_3;
        Assert.assertEquals(34, version.getMajor());
        Assert.assertEquals(3, version.getMinor());
    }

    @Test
    public void shouldObserveApiLimits() {
        VersionTest.V34_0.requireAtLeast(34, 0);
        VersionTest.V34_0.requireAtLeast(33, 9);
        VersionTest.V35_0.requireAtLeast(34, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldObserveApiLimitsOnMajorVersions() {
        VersionTest.V35_0.requireAtLeast(36, 0);
        Assert.fail("No UnsupportedOperationException thrown, but expected");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldObserveApiLimitsOnMinorVersions() {
        VersionTest.V35_0.requireAtLeast(35, 1);
        Assert.fail("No UnsupportedOperationException thrown, but expected");
    }

    @Test
    public void testComparator() {
        Assert.assertTrue(((VersionTest.V34_0.compareTo(VersionTest.V34_3)) < 0));
        Assert.assertTrue(((VersionTest.V34_0.compareTo(VersionTest.V35_0)) < 0));
        Assert.assertTrue(((VersionTest.V34_3.compareTo(VersionTest.V35_0)) < 0));
        Assert.assertTrue(((VersionTest.V34_3.compareTo(VersionTest.V34_0)) > 0));
        Assert.assertTrue(((VersionTest.V35_0.compareTo(VersionTest.V34_0)) > 0));
        Assert.assertTrue(((VersionTest.V35_0.compareTo(VersionTest.V34_3)) > 0));
        Assert.assertTrue(((VersionTest.V34_0.compareTo(VersionTest.V34_0)) == 0));
        Assert.assertTrue(((VersionTest.V34_3.compareTo(VersionTest.V34_3)) == 0));
        Assert.assertTrue(((VersionTest.V35_0.compareTo(VersionTest.V35_0)) == 0));
    }
}

