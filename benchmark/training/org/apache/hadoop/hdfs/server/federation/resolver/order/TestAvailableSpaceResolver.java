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
package org.apache.hadoop.hdfs.server.federation.resolver.order;


import java.io.IOException;
import org.apache.hadoop.hdfs.server.federation.resolver.MultipleDestinationMountTableResolver;
import org.apache.hadoop.hdfs.server.federation.resolver.PathLocation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link AvailableSpaceResolver}.
 */
public class TestAvailableSpaceResolver {
    private static final int SUBCLUSTER_NUM = 10;

    @Test
    public void testResolverWithNoPreference() throws IOException {
        MultipleDestinationMountTableResolver mountTableResolver = mockAvailableSpaceResolver(1.0F);
        // Since we don't have any preference, it will
        // always chose the maximum-available-space subcluster.
        PathLocation loc = mountTableResolver.getDestinationForPath("/space");
        Assert.assertEquals("subcluster9", loc.getDestinations().get(0).getNameserviceId());
        loc = mountTableResolver.getDestinationForPath("/space/subdir");
        Assert.assertEquals("subcluster9", loc.getDestinations().get(0).getNameserviceId());
    }

    @Test
    public void testResolverWithDefaultPreference() throws IOException {
        MultipleDestinationMountTableResolver mountTableResolver = mockAvailableSpaceResolver(AvailableSpaceResolver.BALANCER_PREFERENCE_DEFAULT);
        int retries = 10;
        int retryTimes = 0;
        // There is chance we won't always chose the
        // maximum-available-space subcluster.
        for (retryTimes = 0; retryTimes < retries; retryTimes++) {
            PathLocation loc = mountTableResolver.getDestinationForPath("/space");
            if (!("subcluster9".equals(loc.getDestinations().get(0).getNameserviceId()))) {
                break;
            }
        }
        Assert.assertNotEquals(retries, retryTimes);
    }

    @Test
    public void testSubclusterSpaceComparator() {
        verifyRank(0.0F, true, false);
        verifyRank(1.0F, true, true);
        verifyRank(0.5F, false, false);
        verifyRank(AvailableSpaceResolver.BALANCER_PREFERENCE_DEFAULT, false, false);
        // test for illegal cases
        try {
            verifyRank(2.0F, false, false);
            Assert.fail("Subcluster comparison should be failed.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("The balancer preference value should be in the range 0.0 - 1.0", e);
        }
        try {
            verifyRank((-1.0F), false, false);
            Assert.fail("Subcluster comparison should be failed.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("The balancer preference value should be in the range 0.0 - 1.0", e);
        }
    }
}

