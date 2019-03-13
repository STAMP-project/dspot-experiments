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
package com.hazelcast.test.starter.constructor.test;


import CacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.starter.ReflectionUtils;
import com.hazelcast.test.starter.constructor.MergePolicyProviderConstructor;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class MergePolicyProviderConstructorTest extends HazelcastTestSupport {
    private HazelcastInstance hz;

    @Test
    public void testConstructor() throws Exception {
        // this is testing the Hazelcast Started can create an instances of merge policies
        NodeEngine nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz);
        CacheService service = nodeEngine.getService(SERVICE_NAME);
        CacheMergePolicyProvider mergePolicyProvider = service.getMergePolicyProvider();
        Object delegate = ReflectionUtils.getDelegateFromMock(service);
        Object mergePolicyProviderProxy = ReflectionUtils.getFieldValueReflectively(delegate, "mergePolicyProvider");
        MergePolicyProviderConstructor constructor = new MergePolicyProviderConstructor(CacheMergePolicyProvider.class);
        CacheMergePolicyProvider clonedMergePolicyProvider = ((CacheMergePolicyProvider) (constructor.createNew(mergePolicyProviderProxy)));
        // invalid merge policy
        MergePolicyProviderConstructorTest.assertInvalidCacheMergePolicy(mergePolicyProvider);
        MergePolicyProviderConstructorTest.assertInvalidCacheMergePolicy(clonedMergePolicyProvider);
        // legacy merge policy
        MergePolicyProviderConstructorTest.assertCacheMergePolicy(mergePolicyProvider, "com.hazelcast.cache.merge.PutIfAbsentCacheMergePolicy");
        MergePolicyProviderConstructorTest.assertCacheMergePolicy(clonedMergePolicyProvider, "com.hazelcast.cache.merge.PutIfAbsentCacheMergePolicy");
        // unified merge policy
        MergePolicyProviderConstructorTest.assertCacheMergePolicy(mergePolicyProvider, "com.hazelcast.spi.merge.PutIfAbsentMergePolicy");
        MergePolicyProviderConstructorTest.assertCacheMergePolicy(clonedMergePolicyProvider, "com.hazelcast.spi.merge.PutIfAbsentMergePolicy");
    }
}

