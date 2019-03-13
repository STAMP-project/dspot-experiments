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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterServiceMemberListTest extends HazelcastTestSupport {
    private Config liteConfig = new Config().setLiteMember(true);

    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance liteInstance;

    private HazelcastInstance dataInstance;

    private HazelcastInstance dataInstance2;

    @Test
    public void testGetMembersWithMemberSelector() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                verifyMembersFromLiteMember(liteInstance);
                verifyMembersFromDataMember(dataInstance);
                verifyMembersFromDataMember(dataInstance2);
            }
        });
    }

    @Test
    public void testSizeWithMemberSelector() {
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                verifySizeFromLiteMember(liteInstance);
                verifySizeFromDataMember(dataInstance);
                verifySizeFromDataMember(dataInstance2);
            }
        });
    }
}

