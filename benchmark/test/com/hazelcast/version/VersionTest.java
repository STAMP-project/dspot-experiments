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


import Version.UNKNOWN;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class VersionTest {
    private Version V3_0 = Version.of(3, 0);

    @Test
    public void getValue() throws Exception {
        Assert.assertEquals(3, V3_0.getMajor());
        Assert.assertEquals(0, V3_0.getMinor());
    }

    @Test
    public void isEqualTo() throws Exception {
        Assert.assertTrue(V3_0.isEqualTo(Version.of(3, 0)));
        Assert.assertFalse(V3_0.isEqualTo(Version.of(4, 0)));
    }

    @Test
    public void isGreaterThan() throws Exception {
        Assert.assertTrue(V3_0.isGreaterThan(Version.of(2, 0)));
        Assert.assertFalse(V3_0.isGreaterThan(Version.of(3, 0)));
        Assert.assertFalse(V3_0.isGreaterThan(Version.of(4, 0)));
    }

    @Test
    public void isUnknownOrGreaterThan() throws Exception {
        Assert.assertTrue(V3_0.isUnknownOrGreaterThan(Version.of(2, 0)));
        Assert.assertFalse(V3_0.isUnknownOrGreaterThan(Version.of(3, 0)));
        Assert.assertFalse(V3_0.isUnknownOrGreaterThan(Version.of(4, 0)));
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterThan(Version.of(4, 0)));
    }

    @Test
    public void isGreaterOrEqual() throws Exception {
        Assert.assertTrue(V3_0.isGreaterOrEqual(Version.of(2, 0)));
        Assert.assertTrue(V3_0.isGreaterOrEqual(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isGreaterOrEqual(Version.of(3, 0)));
        Assert.assertFalse(V3_0.isGreaterOrEqual(Version.of(4, 0)));
    }

    @Test
    public void isUnknownGreaterOrEqual() throws Exception {
        Assert.assertTrue(V3_0.isUnknownOrGreaterOrEqual(Version.of(2, 0)));
        Assert.assertTrue(V3_0.isUnknownOrGreaterOrEqual(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isUnknownOrGreaterOrEqual(Version.of(3, 0)));
        Assert.assertFalse(V3_0.isUnknownOrGreaterOrEqual(Version.of(4, 0)));
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterOrEqual(Version.of(4, 0)));
    }

    @Test
    public void isLessThan() throws Exception {
        Assert.assertFalse(V3_0.isLessThan(Version.of(2, 0)));
        Assert.assertFalse(V3_0.isLessThan(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isLessThan(Version.of(3, 1)));
        Assert.assertTrue(V3_0.isLessThan(Version.of(4, 0)));
        Assert.assertTrue(V3_0.isLessThan(Version.of(100, 0)));
    }

    @Test
    public void isUnknownOrLessThan() throws Exception {
        Assert.assertFalse(V3_0.isUnknownOrLessThan(Version.of(2, 0)));
        Assert.assertFalse(V3_0.isUnknownOrLessThan(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isUnknownOrLessThan(Version.of(3, 1)));
        Assert.assertTrue(V3_0.isUnknownOrLessThan(Version.of(4, 0)));
        Assert.assertTrue(V3_0.isUnknownOrLessThan(Version.of(100, 0)));
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessThan(Version.of(100, 0)));
    }

    @Test
    public void isLessOrEqual() throws Exception {
        Assert.assertFalse(V3_0.isLessOrEqual(Version.of(2, 0)));
        Assert.assertTrue(V3_0.isLessOrEqual(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isLessOrEqual(Version.of(4, 0)));
    }

    @Test
    public void isUnknownLessOrEqual() throws Exception {
        Assert.assertFalse(V3_0.isUnknownOrLessOrEqual(Version.of(2, 0)));
        Assert.assertTrue(V3_0.isUnknownOrLessOrEqual(Version.of(3, 0)));
        Assert.assertTrue(V3_0.isUnknownOrLessOrEqual(Version.of(4, 0)));
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessOrEqual(Version.of(4, 0)));
    }

    @Test
    public void isBetween() throws Exception {
        Assert.assertFalse(V3_0.isBetween(Version.of(0, 0), Version.of(1, 0)));
        Assert.assertFalse(V3_0.isBetween(Version.of(4, 0), Version.of(5, 0)));
        Assert.assertTrue(V3_0.isBetween(Version.of(3, 0), Version.of(5, 0)));
        Assert.assertTrue(V3_0.isBetween(Version.of(2, 0), Version.of(3, 0)));
        Assert.assertTrue(V3_0.isBetween(Version.of(1, 0), Version.of(5, 0)));
    }

    @Test
    public void isUnknown() throws Exception {
        Assert.assertTrue(UNKNOWN.isUnknown());
        Assert.assertTrue(Version.of(Version.UNKNOWN_VERSION, Version.UNKNOWN_VERSION).isUnknown());
        Assert.assertTrue(Version.of(0, 0).isUnknown());
    }

    @Test
    public void equals() throws Exception {
        Assert.assertEquals(UNKNOWN, UNKNOWN);
        Assert.assertEquals(Version.of(3, 0), Version.of(3, 0));
        Assert.assertFalse(Version.of(3, 0).equals(Version.of(4, 0)));
        Assert.assertFalse(UNKNOWN.equals(Version.of(4, 0)));
        Assert.assertFalse(UNKNOWN.equals(new Object()));
    }

    @Test
    public void compareTo() throws Exception {
        Assert.assertEquals(0, Version.of(3, 9).compareTo(Version.of(3, 9)));
        Assert.assertThat(Version.of(3, 10).compareTo(Version.of(3, 9)), Matchers.greaterThan(0));
        Assert.assertThat(Version.of(4, 0).compareTo(Version.of(3, 9)), Matchers.greaterThan(0));
        Assert.assertThat(Version.of(3, 9).compareTo(Version.of(3, 10)), Matchers.lessThan(0));
        Assert.assertThat(Version.of(3, 9).compareTo(Version.of(4, 10)), Matchers.lessThan(0));
    }

    @Test
    public void hashCodeTest() throws Exception {
        Assert.assertEquals(UNKNOWN.hashCode(), UNKNOWN.hashCode());
        Assert.assertTrue(((UNKNOWN.hashCode()) != (Version.of(4, 0).hashCode())));
    }

    @Test
    public void test_ofString() {
        Version v = Version.of("3.0");
        Assert.assertEquals(v, V3_0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofMalformed() throws Exception {
        Version.of("3,9");
    }

    @Test
    public void testSerialization() {
        Version given = Version.of(3, 9);
        SerializationService ss = new DefaultSerializationServiceBuilder().build();
        Version deserialized = ss.toObject(ss.toData(given));
        Assert.assertEquals(deserialized, given);
    }

    @Test
    public void toStringTest() throws Exception {
        Assert.assertEquals("3.8", Version.of(3, 8).toString());
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void construct_withNegativeMajor() {
        Version.of((-1), 1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void construct_withOverflowingMajor() {
        Version.of(((Byte.MAX_VALUE) + 1), 1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void construct_withNegativeMinor() {
        Version.of(1, (-1));
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void construct_withOverflowingMinor() {
        Version.of(1, ((Byte.MAX_VALUE) + 1));
    }
}

