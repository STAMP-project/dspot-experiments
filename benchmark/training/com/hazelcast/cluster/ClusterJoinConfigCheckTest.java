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
package com.hazelcast.cluster;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test that the Hazelcast infrastructure can deal correctly with {@link com.hazelcast.internal.cluster.impl.ConfigCheck} violations.
 * Most of the actual cases are tested in the {@link com.hazelcast.cluster.ConfigCheckTest}. In this class we run a bunch
 * of integration tests to make sure that it really works like it is supposed to work.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClusterJoinConfigCheckTest {
    private static final int BASE_PORT = 7777;

    @Test
    public void tcp_whenDifferentGroups_thenDifferentClustersAreFormed() {
        whenDifferentGroups_thenDifferentClustersAreFormed(true);
    }

    @Test
    public void multicast_whenDifferentGroups_thenDifferentClustersAreFormed() {
        whenDifferentGroups_thenDifferentClustersAreFormed(false);
    }
}

