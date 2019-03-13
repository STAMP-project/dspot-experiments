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
package com.hazelcast.spi;


import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractDistributedObjectTest extends HazelcastTestSupport {
    private static final Version NEXT_MINOR_VERSION = Version.of(Versions.CURRENT_CLUSTER_VERSION.getMajor(), ((Versions.CURRENT_CLUSTER_VERSION.getMinor()) + 1));

    private AbstractDistributedObject object;

    @Test
    public void testClusterVersionIsNotUnknown() {
        Assert.assertFalse(object.isClusterVersionUnknown());
    }

    @Test
    public void testClusterVersion_isEqualTo_currentVersion() {
        Assert.assertTrue(object.isClusterVersionEqualTo(Versions.CURRENT_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isGreaterOrEqual_currentVersion() {
        Assert.assertTrue(object.isClusterVersionGreaterOrEqual(Versions.CURRENT_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isUnknownGreaterOrEqual_currentVersion() {
        Assert.assertTrue(object.isClusterVersionUnknownOrGreaterOrEqual(Versions.CURRENT_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isGreaterThan_previousVersion() {
        Assert.assertTrue(object.isClusterVersionGreaterThan(Versions.PREVIOUS_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isUnknownGreaterThan_previousVersion() {
        Assert.assertTrue(object.isClusterVersionUnknownOrGreaterThan(Versions.PREVIOUS_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isLessOrEqual_currentVersion() {
        Assert.assertTrue(object.isClusterVersionLessOrEqual(Versions.CURRENT_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isUnknownLessOrEqual_currentVersion() {
        Assert.assertTrue(object.isClusterVersionUnknownOrLessOrEqual(Versions.CURRENT_CLUSTER_VERSION));
    }

    @Test
    public void testClusterVersion_isLessThan_nextMinorVersion() {
        Assert.assertTrue(object.isClusterVersionLessThan(AbstractDistributedObjectTest.NEXT_MINOR_VERSION));
    }

    @Test
    public void testClusterVersion_isUnknownOrLessThan_nextMinorVersion() {
        Assert.assertTrue(object.isClusterVersionUnknownOrLessThan(AbstractDistributedObjectTest.NEXT_MINOR_VERSION));
    }
}

