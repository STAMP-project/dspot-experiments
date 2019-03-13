/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.repository.utils;


import IRepositoryFactory.CachingRepositoryFactory;
import IRepositoryFactory.CachingRepositoryFactory.REGION;
import IRepositoryFactory.DefaultRepositoryFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.platform.api.engine.ICacheManager;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;

import static IRepositoryFactory.DEFAULT;


/**
 * Created by nbaker on 11/5/15.
 */
public class RepositoryFactoryTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testConnectNoSession() throws Exception {
        IRepositoryFactory.CachingRepositoryFactory cachingRepositoryFactory = new IRepositoryFactory.CachingRepositoryFactory();
        // Call with no Session, should throw Exception
        try {
            cachingRepositoryFactory.connect("foo");
            Assert.fail("Should have thrown exception");
        } catch (KettleException expected) {
        }
    }

    @Test
    public void testCachingFactoryConnect() throws Exception {
        ICacheManager cacheManager = Mockito.mock(ICacheManager.class);
        PentahoSystem.registerObject(cacheManager);
        IPentahoSession session = new StandaloneSession("joe");
        PentahoSessionHolder.setSession(session);
        // Delegate is just a mock. connect will be a cache miss
        IRepositoryFactory mockFactory = Mockito.mock(IRepositoryFactory.class);
        IRepositoryFactory.CachingRepositoryFactory cachingRepositoryFactory = new IRepositoryFactory.CachingRepositoryFactory(mockFactory);
        cachingRepositoryFactory.connect("foo");
        Mockito.verify(mockFactory, Mockito.times(1)).connect("foo");
        // Test with Cache Hit
        Repository mockRepository = Mockito.mock(Repository.class);
        Mockito.when(cacheManager.cacheEnabled(REGION)).thenReturn(true);
        Mockito.when(cacheManager.getFromRegionCache(REGION, "joe")).thenReturn(mockRepository);
        Repository repo = cachingRepositoryFactory.connect("foo");
        MatcherAssert.assertThat(repo, CoreMatchers.sameInstance(mockRepository));
    }

    @Test
    public void testDefaultFactoryConnect() throws Exception {
        IRepositoryFactory.DefaultRepositoryFactory repositoryFactory = new IRepositoryFactory.DefaultRepositoryFactory();
        repositoryFactory.setRepositoryId("KettleFileRepository");
        IPentahoSession session = new StandaloneSession("joe");
        PentahoSessionHolder.setSession(session);
        repositoryFactory.connect("foo");
    }

    @Test
    public void testFactoryRegisteredWithPentahoSystem() throws Exception {
        IRepositoryFactory defaultRepositoryFactory = DEFAULT;
        IRepositoryFactory repositoryFactoryFromPentahoSystem = PentahoSystem.get(IRepositoryFactory.class);
        MatcherAssert.assertThat(repositoryFactoryFromPentahoSystem, CoreMatchers.sameInstance(defaultRepositoryFactory));
    }
}

