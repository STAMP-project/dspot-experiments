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
package org.apache.kafka.message;


import Versions.ALL;
import Versions.NONE;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class VersionsTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testVersionsParse() {
        Assert.assertEquals(NONE, Versions.parse(null, NONE));
        Assert.assertEquals(ALL, Versions.parse(" ", ALL));
        Assert.assertEquals(ALL, Versions.parse("", ALL));
        Assert.assertEquals(VersionsTest.newVersions(4, 5), Versions.parse(" 4-5 ", null));
    }

    @Test
    public void testRoundTrips() {
        testRoundTrip(ALL, "0+");
        testRoundTrip(VersionsTest.newVersions(1, 3), "1-3");
        testRoundTrip(VersionsTest.newVersions(2, 2), "2");
        testRoundTrip(VersionsTest.newVersions(3, Short.MAX_VALUE), "3+");
        testRoundTrip(NONE, "none");
    }

    @Test
    public void testIntersections() {
        Assert.assertEquals(VersionsTest.newVersions(2, 3), VersionsTest.newVersions(1, 3).intersect(VersionsTest.newVersions(2, 4)));
        Assert.assertEquals(VersionsTest.newVersions(3, 3), VersionsTest.newVersions(0, Short.MAX_VALUE).intersect(VersionsTest.newVersions(3, 3)));
        Assert.assertEquals(NONE, VersionsTest.newVersions(9, Short.MAX_VALUE).intersect(VersionsTest.newVersions(2, 8)));
    }

    @Test
    public void testContains() {
        Assert.assertTrue(VersionsTest.newVersions(2, 3).contains(((short) (3))));
        Assert.assertTrue(VersionsTest.newVersions(2, 3).contains(((short) (2))));
        Assert.assertFalse(VersionsTest.newVersions(0, 1).contains(((short) (2))));
        Assert.assertTrue(VersionsTest.newVersions(0, Short.MAX_VALUE).contains(((short) (100))));
        Assert.assertFalse(VersionsTest.newVersions(2, Short.MAX_VALUE).contains(((short) (0))));
        Assert.assertTrue(VersionsTest.newVersions(2, 3).contains(VersionsTest.newVersions(2, 3)));
        Assert.assertTrue(VersionsTest.newVersions(2, 3).contains(VersionsTest.newVersions(2, 2)));
        Assert.assertFalse(VersionsTest.newVersions(2, 3).contains(VersionsTest.newVersions(2, 4)));
        Assert.assertTrue(VersionsTest.newVersions(2, 3).contains(NONE));
        Assert.assertTrue(ALL.contains(VersionsTest.newVersions(1, 2)));
    }
}

