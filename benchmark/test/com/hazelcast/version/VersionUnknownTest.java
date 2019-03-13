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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class VersionUnknownTest {
    private Version ANY_VERSION = Version.of(3, 7);

    @Test
    public void unknown_equals_to_itself() throws Exception {
        Assert.assertEquals(Version.UNKNOWN, Version.UNKNOWN);
    }

    @Test
    public void unknown_notEquals_to_any() throws Exception {
        Assert.assertNotEquals(Version.UNKNOWN, ANY_VERSION);
    }

    @Test
    public void unknown_isNot_greaterThan_any() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isGreaterThan(ANY_VERSION));
    }

    @Test
    public void unknown_isNot_greaterThan_unknown() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isGreaterThan(Version.UNKNOWN));
    }

    @Test
    public void unknown_isNot_greaterOrEqual_any() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isGreaterOrEqual(ANY_VERSION));
    }

    @Test
    public void unknown_is_greaterOrEqual_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isGreaterOrEqual(Version.UNKNOWN));
    }

    @Test
    public void unknown_isNot_lessThan_any() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isLessThan(ANY_VERSION));
    }

    @Test
    public void unknown_isNot_lessThan_unknown() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isLessThan(Version.UNKNOWN));
    }

    @Test
    public void unknown_isNot_lessOrEqual_any() throws Exception {
        Assert.assertFalse(Version.UNKNOWN.isLessOrEqual(ANY_VERSION));
    }

    @Test
    public void unknown_is_lessOrEqual_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isLessOrEqual(Version.UNKNOWN));
    }

    @Test
    public void unknown_is_unknownOrGreaterThan_any() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterThan(ANY_VERSION));
    }

    @Test
    public void unknown_is_unknownOrGreaterThan_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterThan(Version.UNKNOWN));
    }

    @Test
    public void unknown_is_unknownOrLessThan_any() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessThan(ANY_VERSION));
    }

    @Test
    public void unknown_is_unknownOrLessThan_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessThan(Version.UNKNOWN));
    }

    @Test
    public void unknown_is_unknownGreaterOrEqual_any() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterOrEqual(ANY_VERSION));
    }

    @Test
    public void unknown_is_unknownGreaterOrEqual_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrGreaterOrEqual(Version.UNKNOWN));
    }

    @Test
    public void unknown_is_unknownLessOrEqual_any() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessOrEqual(ANY_VERSION));
    }

    @Test
    public void unknown_is_unknownLessOrEqual_unknown() throws Exception {
        Assert.assertTrue(Version.UNKNOWN.isUnknownOrLessOrEqual(Version.UNKNOWN));
    }

    @Test
    public void any_notEquals_to_unknown() throws Exception {
        Assert.assertNotEquals(ANY_VERSION, Version.UNKNOWN);
    }

    @Test
    public void any_isNot_greaterThan_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isGreaterThan(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_greaterOrEqual_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isGreaterOrEqual(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_lessThan_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isLessThan(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_lessOrEqual_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isLessOrEqual(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_unknownOrGreaterThan_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isUnknownOrGreaterThan(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_unknownOrLessThan_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isUnknownOrLessThan(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_unknownGreaterOrEqual_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isUnknownOrGreaterOrEqual(Version.UNKNOWN));
    }

    @Test
    public void any_isNot_unknownLessOrEqual_unknown() throws Exception {
        Assert.assertFalse(ANY_VERSION.isUnknownOrLessOrEqual(Version.UNKNOWN));
    }
}

