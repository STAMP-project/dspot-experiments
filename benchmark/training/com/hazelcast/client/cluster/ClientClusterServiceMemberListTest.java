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
package com.hazelcast.client.cluster;


import com.hazelcast.client.spi.ClientClusterService;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientClusterServiceMemberListTest extends HazelcastTestSupport {
    private Config liteConfig = new Config().setLiteMember(true);

    private TestHazelcastFactory factory;

    private HazelcastInstance liteInstance;

    private HazelcastInstance dataInstance;

    private HazelcastInstance dataInstance2;

    private HazelcastInstance client;

    @Test
    public void testLiteMembers() {
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final ClientClusterService clusterService = getClientClusterService(client);
                final Collection<Member> members = clusterService.getMembers(LITE_MEMBER_SELECTOR);
                verifyMembers(members, Collections.singletonList(liteInstance));
                Assert.assertEquals(1, clusterService.getSize(LITE_MEMBER_SELECTOR));
            }
        });
    }

    @Test
    public void testDataMembers() {
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final ClientClusterService clusterService = getClientClusterService(client);
                final Collection<Member> members = clusterService.getMembers(DATA_MEMBER_SELECTOR);
                verifyMembers(members, Arrays.asList(dataInstance, dataInstance2));
                Assert.assertEquals(2, clusterService.getSize(DATA_MEMBER_SELECTOR));
            }
        });
    }

    @Test
    public void testMemberListOrderConsistentWithServer() {
        Set<Member> membersFromClient = client.getCluster().getMembers();
        Set<Member> membersFromServer = dataInstance.getCluster().getMembers();
        assertArrayEquals(membersFromClient.toArray(), membersFromServer.toArray());
    }
}

