/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.clustering.infinispan;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.infinispan.spi.CacheContainer;


/**
 * Unit test for {@link DefaultCacheContainer}.
 *
 * @author Paul Ferraro
 */
public class DefaultCacheContainerTestCase {
    private final EmbeddedCacheManager manager = Mockito.mock(EmbeddedCacheManager.class);

    private final CacheContainer subject = new DefaultCacheContainer(this.manager);

    @Test
    public void getName() {
        String name = "foo";
        GlobalConfiguration global = new GlobalConfigurationBuilder().globalJmxStatistics().cacheManagerName(name).build();
        Mockito.when(this.manager.getCacheManagerConfiguration()).thenReturn(global);
        Assert.assertSame(name, this.subject.getName());
    }

    @Test
    public void getDefaultCacheName() {
        String defaultCache = "default";
        GlobalConfiguration global = new GlobalConfigurationBuilder().defaultCacheName(defaultCache).build();
        Mockito.when(this.manager.getCacheManagerConfiguration()).thenReturn(global);
        Assert.assertSame(defaultCache, this.subject.getDefaultCacheName());
    }

    @Test
    public void getDefaultCache() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        Mockito.when(this.manager.getCache()).thenReturn(cache);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Cache<Object, Object> result = this.subject.getCache();
        Assert.assertNotSame(cache, result);
        Assert.assertEquals(result, cache);
        Assert.assertSame(this.subject, result.getCacheManager());
    }

    @Test
    public void getCache() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        Mockito.when(this.manager.getCache("other")).thenReturn(cache);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Cache<Object, Object> result = this.subject.getCache("other");
        Assert.assertNotSame(cache, result);
        Assert.assertEquals(result, cache);
        Assert.assertSame(this.subject, result.getCacheManager());
    }

    @Test
    public void getCacheCreateIfAbsent() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        Mockito.when(this.manager.getCache("non-existent", true)).thenReturn(cache);
        Mockito.when(this.manager.getCache("non-existent", false)).thenReturn(null);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Cache<Object, Object> result = this.subject.getCache("non-existent", true);
        Assert.assertNotSame(cache, result);
        Assert.assertEquals(result, cache);
        Assert.assertSame(this.subject, result.getCacheManager());
        Assert.assertNull(this.subject.getCache("non-existent", false));
    }

    @Deprecated
    @Test
    public void getCacheFromTemplate() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        String templateName = "template";
        Mockito.when(this.manager.getCache("other", templateName)).thenReturn(cache);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Cache<Object, Object> result = this.subject.getCache("other", templateName);
        Assert.assertNotSame(cache, result);
        Assert.assertEquals(result, cache);
        Assert.assertSame(this.subject, result.getCacheManager());
    }

    @Deprecated
    @Test
    public void getCacheFromTemplateCreateIfAbsent() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        String templateName = "template";
        Mockito.when(this.manager.getCache("non-existent", templateName, true)).thenReturn(cache);
        Mockito.when(this.manager.getCache("non-existent", templateName, false)).thenReturn(null);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Cache<Object, Object> result = this.subject.getCache("non-existent", templateName, true);
        Assert.assertNotSame(cache, result);
        Assert.assertEquals(result, cache);
        Assert.assertSame(this.subject, result.getCacheManager());
        Assert.assertNull(this.subject.getCache("non-existent", templateName, false));
    }

    @Test
    public void start() {
        this.subject.start();
        Mockito.verify(this.manager, Mockito.never()).start();
    }

    @Test
    public void stop() {
        this.subject.stop();
        Mockito.verify(this.manager, Mockito.never()).stop();
    }

    @Test
    public void addListener() {
        Object listener = new Object();
        this.subject.addListener(listener);
        Mockito.verify(this.manager).addListener(listener);
    }

    @Test
    public void removeListener() {
        Object listener = new Object();
        this.subject.removeListener(listener);
        Mockito.verify(this.manager).removeListener(listener);
    }

    @Test
    public void getListeners() {
        Set<Object> expected = Collections.singleton(new Object());
        Mockito.when(this.manager.getListeners()).thenReturn(expected);
        Set<Object> result = this.subject.getListeners();
        Assert.assertSame(expected, result);
    }

    @Test
    public void defineConfiguration() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Configuration config = builder.build();
        Mockito.when(this.manager.defineConfiguration("other", config)).thenReturn(config);
        Configuration result = this.subject.defineConfiguration("other", config);
        Assert.assertSame(config, result);
    }

    @Test
    public void undefineConfiguration() {
        this.subject.undefineConfiguration("test");
        Mockito.verify(this.manager).undefineConfiguration("test");
    }

    @Test
    public void getClusterName() {
        String expected = "cluster";
        Mockito.when(this.manager.getClusterName()).thenReturn(expected);
        String result = this.subject.getClusterName();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getMembers() {
        List<Address> expected = Collections.singletonList(Mockito.mock(Address.class));
        Mockito.when(this.manager.getMembers()).thenReturn(expected);
        List<Address> result = this.subject.getMembers();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getAddress() {
        Address expected = Mockito.mock(Address.class);
        Mockito.when(this.manager.getAddress()).thenReturn(expected);
        Address result = this.subject.getAddress();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getCoordinator() {
        Address expected = Mockito.mock(Address.class);
        Mockito.when(this.manager.getCoordinator()).thenReturn(expected);
        Address result = this.subject.getCoordinator();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getStatus() {
        ComponentStatus expected = ComponentStatus.INITIALIZING;
        Mockito.when(this.manager.getStatus()).thenReturn(expected);
        ComponentStatus result = this.subject.getStatus();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getCacheManagerConfiguration() {
        GlobalConfiguration global = new GlobalConfigurationBuilder().build();
        Mockito.when(this.manager.getCacheManagerConfiguration()).thenReturn(global);
        GlobalConfiguration result = this.subject.getCacheManagerConfiguration();
        Assert.assertSame(global, result);
    }

    @Test
    public void getDefaultCacheConfiguration() {
        Configuration config = new ConfigurationBuilder().build();
        Mockito.when(this.manager.getDefaultCacheConfiguration()).thenReturn(config);
        Configuration result = this.subject.getDefaultCacheConfiguration();
        Assert.assertSame(config, result);
    }

    @Test
    public void getCacheConfiguration() {
        Configuration config = new ConfigurationBuilder().build();
        Mockito.when(this.manager.getCacheConfiguration("cache")).thenReturn(config);
        Configuration result = this.subject.getCacheConfiguration("cache");
        Assert.assertSame(config, result);
    }

    @Test
    public void getCacheNames() {
        Set<String> caches = Collections.singleton("other");
        Mockito.when(this.manager.getCacheNames()).thenReturn(caches);
        Set<String> result = this.subject.getCacheNames();
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains("other"));
    }

    @Test
    public void isRunning() {
        Mockito.when(this.manager.isRunning("other")).thenReturn(true);
        boolean result = this.subject.isRunning("other");
        Assert.assertTrue(result);
    }

    @Test
    public void isDefaultRunning() {
        Mockito.when(this.manager.isDefaultRunning()).thenReturn(true);
        boolean result = this.subject.isDefaultRunning();
        Assert.assertTrue(result);
    }

    @Test
    public void startCaches() {
        Mockito.when(this.manager.startCaches("other")).thenReturn(this.manager);
        EmbeddedCacheManager result = this.subject.startCaches("other");
        Assert.assertSame(this.subject, result);
    }
}

