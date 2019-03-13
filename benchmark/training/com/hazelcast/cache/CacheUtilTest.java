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
package com.hazelcast.cache;


import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheUtilTest extends HazelcastTestSupport {
    private static final String CACHE_NAME = "MY-CACHE";

    private static final String URI_SCOPE = "MY-SCOPE";

    private static final String CLASSLOADER_SCOPE = "MY-CLASSLOADER";

    @Parameterized.Parameter
    public URI uri;

    @Parameterized.Parameter(1)
    public ClassLoader classLoader;

    @Parameterized.Parameter(2)
    public String expectedPrefix;

    @Parameterized.Parameter(3)
    public String expectedPrefixedCacheName;

    @Parameterized.Parameter(4)
    public String expectedDistributedObjectName;

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(CacheUtil.class);
    }

    @Test
    public void testGetPrefix() {
        String prefix = CacheUtil.getPrefix(uri, classLoader);
        Assert.assertEquals(expectedPrefix, prefix);
    }

    @Test
    public void testGetPrefixedCacheName() {
        String prefix = CacheUtil.getPrefixedCacheName(CacheUtilTest.CACHE_NAME, uri, classLoader);
        Assert.assertEquals(expectedPrefixedCacheName, prefix);
    }

    @Test
    public void testGetDistributedObjectName() {
        String distributedObjectName = CacheUtil.getDistributedObjectName(CacheUtilTest.CACHE_NAME, uri, classLoader);
        Assert.assertEquals(expectedDistributedObjectName, distributedObjectName);
    }
}

