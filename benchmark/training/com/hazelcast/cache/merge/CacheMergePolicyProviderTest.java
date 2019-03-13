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
package com.hazelcast.cache.merge;


import BuiltInCacheMergePolicies.HIGHER_HITS;
import BuiltInCacheMergePolicies.LATEST_ACCESS;
import BuiltInCacheMergePolicies.PASS_THROUGH;
import BuiltInCacheMergePolicies.PUT_IF_ABSENT;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheMergePolicyProviderTest extends HazelcastTestSupport {
    private CacheMergePolicyProvider mergePolicyProvider;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void getMergePolicy_NotExistingMergePolicy() {
        expected.expect(InvalidConfigurationException.class);
        expected.expectCause(IsInstanceOf.any(ClassNotFoundException.class));
        mergePolicyProvider.getMergePolicy("No such policy!");
    }

    @Test
    public void getMergePolicy_NullPolicy() {
        expected.expect(InvalidConfigurationException.class);
        mergePolicyProvider.getMergePolicy(null);
    }

    @Test
    public void getMergePolicy_withClassName_PutIfAbsentCacheMergePolicy() {
        assertMergePolicyCorrectlyInitialised(PutIfAbsentCacheMergePolicy.class.getName(), PutIfAbsentCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withConstant_PutIfAbsentCacheMergePolicy() {
        assertMergePolicyCorrectlyInitialised(PUT_IF_ABSENT.name(), PutIfAbsentCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withClassName_LatestAccessCacheMergePolicy() {
        assertMergePolicyCorrectlyInitialised(LatestAccessCacheMergePolicy.class.getName(), LatestAccessCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withConstant_LatestAccessCacheMergePolicy() {
        assertMergePolicyCorrectlyInitialised(LATEST_ACCESS.name(), LatestAccessCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withClassName_PassThroughCachePolicy() {
        assertMergePolicyCorrectlyInitialised(PassThroughCacheMergePolicy.class.getName(), PassThroughCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withConstant_PassThroughCachePolicy() {
        assertMergePolicyCorrectlyInitialised(PASS_THROUGH.name(), PassThroughCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withClassName_HigherHitsMapCachePolicy() {
        assertMergePolicyCorrectlyInitialised(HigherHitsCacheMergePolicy.class.getName(), HigherHitsCacheMergePolicy.class);
    }

    @Test
    public void getMergePolicy_withConstant_HigherHitsMapCachePolicy() {
        assertMergePolicyCorrectlyInitialised(HIGHER_HITS.name(), HigherHitsCacheMergePolicy.class);
    }
}

