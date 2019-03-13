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
package com.hazelcast.nio.ascii;


import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests if HTTP REST URLs are protected by the correct REST endpoint groups.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category(QuickTest.class)
public class HttpRestEndpointGroupsTest extends RestApiConfigTestBase {
    @Parameterized.Parameter
    public RestEndpointGroup restEndpointGroup;

    @Test
    public void testGroupEnabled() throws Exception {
        HazelcastInstance hz = factory.newHazelcastInstance(createConfigWithEnabledGroups(restEndpointGroup));
        for (RestApiConfigTestBase.TestUrl testUrl : RestApiConfigTestBase.TEST_URLS) {
            if ((restEndpointGroup) == (testUrl.restEndpointGroup)) {
                assertTextProtocolResponse(hz, testUrl);
            }
        }
    }

    @Test
    public void testGroupDisabled() throws Exception {
        HazelcastInstance hz = factory.newHazelcastInstance(createConfigWithDisabledGroups(restEndpointGroup));
        for (RestApiConfigTestBase.TestUrl testUrl : RestApiConfigTestBase.TEST_URLS) {
            if ((restEndpointGroup) == (testUrl.restEndpointGroup)) {
                assertNoTextProtocolResponse(hz, testUrl);
            }
        }
    }

    @Test
    public void testOthersWhenGroupEnabled() throws Exception {
        HazelcastInstance hz = factory.newHazelcastInstance(createConfigWithEnabledGroups(restEndpointGroup));
        for (RestApiConfigTestBase.TestUrl testUrl : RestApiConfigTestBase.TEST_URLS) {
            if ((restEndpointGroup) != (testUrl.restEndpointGroup)) {
                assertNoTextProtocolResponse(hz, testUrl);
            }
        }
    }

    @Test
    public void testOthersWhenGroupDisabled() throws Exception {
        HazelcastInstance hz = factory.newHazelcastInstance(createConfigWithDisabledGroups(restEndpointGroup));
        for (RestApiConfigTestBase.TestUrl testUrl : RestApiConfigTestBase.TEST_URLS) {
            if ((restEndpointGroup) != (testUrl.restEndpointGroup)) {
                assertTextProtocolResponse(hz, testUrl);
            }
        }
    }
}

