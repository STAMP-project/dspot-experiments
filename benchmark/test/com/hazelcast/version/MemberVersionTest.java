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
package com.hazelcast.version;


import MemberVersion.UNKNOWN;
import SerializationServiceV1.VERSION_1;
import Versions.V3_8;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.internal.serialization.impl.SerializationServiceV1;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberVersionTest {
    private static final String VERSION_3_8_SNAPSHOT_STRING = "3.8-SNAPSHOT";

    private static final String VERSION_3_8_1_RC1_STRING = "3.8.1-RC1";

    private static final String VERSION_3_8_1_BETA_1_STRING = "3.8.1-beta-1";

    private static final String VERSION_3_8_BETA_2_STRING = "3.8-beta-2";

    private static final String VERSION_3_8_2_STRING = "3.8.2";

    private static final String VERSION_3_9_0_STRING = "3.9.0";

    private static final String VERSION_UNKNOWN_STRING = "0.0.0";

    private static final MemberVersion VERSION_3_8 = MemberVersion.of(MemberVersionTest.VERSION_3_8_SNAPSHOT_STRING);

    private static final MemberVersion VERSION_3_8_1 = MemberVersion.of(MemberVersionTest.VERSION_3_8_1_RC1_STRING);

    private static final MemberVersion VERSION_3_8_2 = MemberVersion.of(MemberVersionTest.VERSION_3_8_2_STRING);

    private static final MemberVersion VERSION_3_9 = MemberVersion.of(MemberVersionTest.VERSION_3_9_0_STRING);

    private MemberVersion version = MemberVersion.of(3, 8, 0);

    private MemberVersion versionSameAttributes = MemberVersion.of(3, 8, 0);

    private MemberVersion versionOtherMajor = MemberVersion.of(4, 8, 0);

    private MemberVersion versionOtherMinor = MemberVersion.of(3, 7, 0);

    private MemberVersion versionOtherPath = MemberVersion.of(3, 8, 1);

    @Test
    public void testIsUnknown() {
        Assert.assertTrue(UNKNOWN.isUnknown());
        Assert.assertFalse(MemberVersion.of(MemberVersionTest.VERSION_3_8_SNAPSHOT_STRING).isUnknown());
        Assert.assertFalse(MemberVersion.of(MemberVersionTest.VERSION_3_8_1_RC1_STRING).isUnknown());
        Assert.assertFalse(MemberVersion.of(MemberVersionTest.VERSION_3_8_1_BETA_1_STRING).isUnknown());
        Assert.assertFalse(MemberVersion.of(MemberVersionTest.VERSION_3_8_BETA_2_STRING).isUnknown());
        Assert.assertFalse(MemberVersion.of(MemberVersionTest.VERSION_3_8_2_STRING).isUnknown());
    }

    @Test
    public void testVersionOf_whenVersionIsUnknown() {
        Assert.assertEquals(UNKNOWN, MemberVersion.of(0, 0, 0));
    }

    @Test
    public void testVersionOf_whenVersionStringIsSnapshot() {
        MemberVersion expected = MemberVersion.of(3, 8, 0);
        Assert.assertEquals(expected, MemberVersion.of(MemberVersionTest.VERSION_3_8_SNAPSHOT_STRING));
    }

    @Test
    public void testVersionOf_whenVersionStringIsBeta() {
        Assert.assertEquals(MemberVersion.of(3, 8, 0), MemberVersion.of(MemberVersionTest.VERSION_3_8_BETA_2_STRING));
        Assert.assertEquals(MemberVersion.of(3, 8, 1), MemberVersion.of(MemberVersionTest.VERSION_3_8_1_BETA_1_STRING));
    }

    @Test
    public void test_constituents_whenVersionStringIsBeta() {
        final MemberVersion expected = MemberVersion.of(MemberVersionTest.VERSION_3_8_BETA_2_STRING);
        Assert.assertEquals(3, expected.getMajor());
        Assert.assertEquals(8, expected.getMinor());
        Assert.assertEquals(0, expected.getPatch());
    }

    @Test
    public void testVersionOf_whenVersionStringIsRC() {
        MemberVersion expected = MemberVersion.of(3, 8, 1);
        Assert.assertEquals(expected, MemberVersion.of(MemberVersionTest.VERSION_3_8_1_RC1_STRING));
    }

    @Test
    public void testVersionOf_whenVersionStringIsRelease() {
        MemberVersion expected = MemberVersion.of(3, 8, 2);
        Assert.assertEquals(expected, MemberVersion.of(MemberVersionTest.VERSION_3_8_2_STRING));
    }

    @Test
    public void test_constituents() {
        MemberVersion expected = MemberVersion.of(3, 8, 2);
        Assert.assertEquals(3, expected.getMajor());
        Assert.assertEquals(8, expected.getMinor());
        Assert.assertEquals(2, expected.getPatch());
    }

    @Test
    public void testVersionOf_whenVersionStringIsUnknown() {
        Assert.assertEquals(UNKNOWN, MemberVersion.of(MemberVersionTest.VERSION_UNKNOWN_STRING));
    }

    @Test
    public void testVersionOf_whenVersionStringIsNull() {
        Assert.assertEquals(UNKNOWN, MemberVersion.of(null));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(version, version);
        Assert.assertEquals(version, versionSameAttributes);
        Assert.assertNotEquals(version, null);
        Assert.assertNotEquals(version, new Object());
        Assert.assertNotEquals(version, versionOtherMajor);
        Assert.assertNotEquals(version, versionOtherMinor);
        Assert.assertNotEquals(version, versionOtherPath);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(version.hashCode(), version.hashCode());
        Assert.assertEquals(version.hashCode(), versionSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(version.hashCode(), versionOtherMajor.hashCode());
        Assert.assertNotEquals(version.hashCode(), versionOtherMinor.hashCode());
        Assert.assertNotEquals(version.hashCode(), versionOtherPath.hashCode());
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(((MemberVersionTest.VERSION_3_8.compareTo(MemberVersionTest.VERSION_3_8)) == 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_8.compareTo(MemberVersionTest.VERSION_3_8_1)) < 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_8.compareTo(MemberVersionTest.VERSION_3_8_2)) < 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_8.compareTo(MemberVersionTest.VERSION_3_9)) < 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_9.compareTo(MemberVersionTest.VERSION_3_8)) > 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_9.compareTo(MemberVersionTest.VERSION_3_8_1)) > 0));
        Assert.assertTrue(((MemberVersionTest.VERSION_3_9.compareTo(MemberVersionTest.VERSION_3_8_2)) > 0));
    }

    @Test
    public void testMajorMinorVersionComparator() {
        Assert.assertEquals(0, MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_8, MemberVersionTest.VERSION_3_8_1));
        Assert.assertEquals(0, MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_8, MemberVersionTest.VERSION_3_8_2));
        Assert.assertTrue(((MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_9, MemberVersionTest.VERSION_3_8)) > 0));
        Assert.assertTrue(((MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_8, MemberVersionTest.VERSION_3_9)) < 0));
        Assert.assertTrue(((MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_9, MemberVersionTest.VERSION_3_8_1)) > 0));
        Assert.assertTrue(((MemberVersion.MAJOR_MINOR_VERSION_COMPARATOR.compare(MemberVersionTest.VERSION_3_8_1, MemberVersionTest.VERSION_3_9)) < 0));
    }

    @Test
    public void testAsClusterVersion() {
        Version clusterVersion = MemberVersion.of(3, 8, 2).asVersion();
        Assert.assertEquals(3, clusterVersion.getMajor());
        Assert.assertEquals(8, clusterVersion.getMinor());
    }

    @Test
    public void testAsSerializationVersion() {
        Version version = MemberVersion.of(3, 8, 2).asVersion();
        Assert.assertEquals(V3_8, version);
    }

    @Test
    public void testEmpty() {
        MemberVersion version = new MemberVersion();
        Assert.assertEquals(0, version.getMajor());
        Assert.assertEquals(0, version.getMinor());
        Assert.assertEquals(0, version.getPatch());
    }

    @Test
    public void testSerialization() {
        MemberVersion given = MemberVersion.of(3, 9, 1);
        SerializationServiceV1 ss = new DefaultSerializationServiceBuilder().setVersion(VERSION_1).build();
        MemberVersion deserialized = ss.toObject(ss.toData(given));
        Assert.assertEquals(deserialized, given);
    }

    @Test
    public void toStringTest() throws Exception {
        Assert.assertEquals("3.8.2", MemberVersion.of(3, 8, 2).toString());
    }
}

