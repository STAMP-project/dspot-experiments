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
package com.hazelcast.cache.impl.operation;


import CacheService.SERVICE_NAME;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.JCacheDetector;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Test whether OnJoinCacheOperation logs warning, fails or succeeds under different JCache API availability
 * in classpath.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JCacheDetector.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OnJoinCacheOperationTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private NodeEngine nodeEngine = Mockito.mock(NodeEngine.class);

    private ClassLoader classLoader = Mockito.mock(ClassLoader.class);

    private ILogger logger = Mockito.mock(ILogger.class);

    @Test
    public void test_cachePostJoinOperationSucceeds_whenJCacheAvailable_noWarningIsLogged() throws Exception {
        // JCacheDetector finds JCache in classpath
        Mockito.when(JCacheDetector.isJCacheAvailable(classLoader)).thenReturn(true);
        // node engine returns mock CacheService
        Mockito.when(nodeEngine.getService(SERVICE_NAME)).thenReturn(Mockito.mock(ICacheService.class));
        OnJoinCacheOperation onJoinCacheOperation = new OnJoinCacheOperation();
        onJoinCacheOperation.setNodeEngine(nodeEngine);
        onJoinCacheOperation.run();
        Mockito.verify(nodeEngine).getConfigClassLoader();
        Mockito.verify(nodeEngine).getService(SERVICE_NAME);
        // verify logger was not invoked
        Mockito.verify(logger, Mockito.never()).warning(ArgumentMatchers.anyString());
    }

    @Test
    public void test_cachePostJoinOperationSucceeds_whenJCacheNotAvailable_noCacheConfigs() throws Exception {
        Mockito.when(JCacheDetector.isJCacheAvailable(classLoader)).thenReturn(false);
        OnJoinCacheOperation onJoinCacheOperation = new OnJoinCacheOperation();
        onJoinCacheOperation.setNodeEngine(nodeEngine);
        onJoinCacheOperation.run();
        Mockito.verify(nodeEngine).getConfigClassLoader();
        // verify a warning was logged
        Mockito.verify(logger).warning(ArgumentMatchers.anyString());
        // verify CacheService instance was not requested in OnJoinCacheOperation.run
        Mockito.verify(nodeEngine, Mockito.never()).getService(SERVICE_NAME);
    }

    @Test
    public void test_cachePostJoinOperationFails_whenJCacheNotAvailable_withCacheConfigs() throws Exception {
        // JCache is not available in classpath
        Mockito.when(JCacheDetector.isJCacheAvailable(classLoader)).thenReturn(false);
        // node engine throws HazelcastException due to missing CacheService
        Mockito.when(nodeEngine.getService(SERVICE_NAME)).thenThrow(new HazelcastException("CacheService not found"));
        // some CacheConfigs are added in the OnJoinCacheOperation (so JCache is actually in use in the rest of the cluster)
        OnJoinCacheOperation onJoinCacheOperation = new OnJoinCacheOperation();
        onJoinCacheOperation.addCacheConfig(new CacheConfig("test"));
        onJoinCacheOperation.setNodeEngine(nodeEngine);
        expectedException.expect(HazelcastException.class);
        onJoinCacheOperation.run();
        Mockito.verify(nodeEngine).getConfigClassLoader();
        Mockito.verify(nodeEngine).getService(SERVICE_NAME);
        Mockito.verify(logger, Mockito.never()).warning(ArgumentMatchers.anyString());
    }
}

