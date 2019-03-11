/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.util.Properties;
import java.util.function.Supplier;
import org.apache.geode.cache.CacheExistsException;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.InternalCacheBuilder.InternalCacheConstructor;
import org.apache.geode.internal.cache.InternalCacheBuilder.InternalDistributedSystemConstructor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link InternalCacheBuilder} when
 * {@code InternalDistributedSystem.ALLOW_MULTIPLE_SYSTEMS} is set to true.
 */
public class InternalCacheBuilderAllowsMultipleSystemsTest {
    private static final int ANY_SYSTEM_ID = 12;

    private static final String ANY_MEMBER_NAME = "a-member-name";

    private static final Supplier<InternalDistributedSystem> THROWING_SYSTEM_SUPPLIER = () -> {
        throw new AssertionError("throwing system supplier");
    };

    private static final Supplier<InternalCache> THROWING_CACHE_SUPPLIER = () -> {
        throw new AssertionError("throwing cache supplier");
    };

    private static final InternalDistributedSystemConstructor THROWING_SYSTEM_CONSTRUCTOR = ( a, b) -> {
        throw new AssertionError("throwing system constructor");
    };

    private static final InternalCacheConstructor THROWING_CACHE_CONSTRUCTOR = ( a, b, c, d, e, f) -> {
        throw new AssertionError("throwing cache constructor");
    };

    @Test
    public void create_throwsNullPointerException_ifConfigPropertiesIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(null, new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem()), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache()));
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create());
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void create_throwsNullPointerException_andCacheConfigIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), null, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem()), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache()));
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create());
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void create_constructsSystem_withGivenProperties_ifNoSystemExists() {
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalDistributedSystemConstructor systemConstructor = InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem());
        Properties configProperties = new Properties();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(configProperties, new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, systemConstructor, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        internalCacheBuilder.create();
        Mockito.verify(systemConstructor).construct(ArgumentMatchers.same(configProperties), ArgumentMatchers.any());
    }

    @Test
    public void create_returnsConstructedCache_ifNoSystemExists() {
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem()), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create();
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void create_setsConstructedCache_onConstructedSystem_ifNoSystemExists() {
        InternalDistributedSystem constructedSystem = constructedSystem();
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        internalCacheBuilder.create();
        Mockito.verify(constructedSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void create_setsConstructedSystem_onConstructedCache_ifNoSystemExists() {
        InternalDistributedSystem constructedSystem = constructedSystem();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedSystem), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, cacheConstructor);
        internalCacheBuilder.create();
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(constructedSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_throwsNullPointerException_ifSystemIsNull() {
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.create(null));
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void createWithSystem_returnsConstructedCache_ifSystemCacheDoesNotExist() {
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create(givenSystem());
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void createWithSystem_setsConstructedCache_onGivenSystem_ifSystemCacheDoesNotExist() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void createWithSystem_setsGivenSystem_onConstructedCache_ifSystemCacheDoesNotExist() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, cacheConstructor);
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(givenSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_returnsConstructedCache_ifSystemCacheIsClosed() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.CLOSED);
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        InternalCache result = internalCacheBuilder.create(givenSystem);
        assertThat(result).isSameAs(constructedCache);
    }

    @Test
    public void createWithSystem_setsConstructedCache_onGivenSystem_ifSystemCacheIsClosed() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.CLOSED);
        InternalCache constructedCache = InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache();
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(constructedCache));
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(constructedCache));
    }

    @Test
    public void createWithSystem_setsGivenSystem_onConstructedCache_ifSystemCacheIsClosed() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.CLOSED);
        InternalCacheConstructor cacheConstructor = InternalCacheBuilderAllowsMultipleSystemsTest.constructorOf(InternalCacheBuilderAllowsMultipleSystemsTest.constructedCache());
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, cacheConstructor);
        internalCacheBuilder.create(givenSystem);
        Mockito.verify(cacheConstructor).construct(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.same(givenSystem), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_throwsCacheExistsException_ifSystemCacheIsOpen_butExistingNotOk() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(false).create(givenSystem));
        assertThat(thrown).isInstanceOf(CacheExistsException.class);
    }

    @Test
    public void createWithSystem_doesNotSetSystemCache_onGivenSystem__ifSystemCacheIsOpen_butExistingNotOk() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCacheBuilderAllowsMultipleSystemsTest.ignoreThrowable(() -> internalCacheBuilder.setIsExistingOk(false).create(givenSystem));
        Mockito.verify(givenSystem, Mockito.never()).setCache(ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_propagatesCacheConfigException_ifSystemCacheIsOpen_andExistingOk_butCacheIsIncompatible() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        Throwable thrownByCacheConfig = new IllegalStateException("incompatible");
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), InternalCacheBuilderAllowsMultipleSystemsTest.throwingCacheConfig(thrownByCacheConfig), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        Throwable thrown = catchThrowable(() -> internalCacheBuilder.setIsExistingOk(true).create(givenSystem));
        assertThat(thrown).isSameAs(thrownByCacheConfig);
    }

    @Test
    public void createWithSystem_doesNotSetSystemCache_onGivenSystem_ifSystemCacheIsOpen_andExistingOk_butCacheIsNotCompatible() {
        InternalDistributedSystem givenSystem = givenSystemWithCache(InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), InternalCacheBuilderAllowsMultipleSystemsTest.throwingCacheConfig(new IllegalStateException("incompatible")), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCacheBuilderAllowsMultipleSystemsTest.ignoreThrowable(() -> internalCacheBuilder.setIsExistingOk(true).create(givenSystem));
        Mockito.verify(givenSystem, Mockito.never()).setCache(ArgumentMatchers.any());
    }

    @Test
    public void createWithSystem_returnsSystemCache_ifSystemCacheIsOpen_andExistingOk_andCacheIsCompatible() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache systemCache = InternalCacheBuilderAllowsMultipleSystemsTest.systemCache(givenSystem, InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        InternalCache result = internalCacheBuilder.setIsExistingOk(true).create(givenSystem);
        assertThat(result).isSameAs(systemCache);
    }

    @Test
    public void createWithSystem_setsSystemCache_onGivenSystem_ifSystemCacheIsOpen_andExistingOk_andCacheIsCompatible() {
        InternalDistributedSystem givenSystem = givenSystem();
        InternalCache systemCache = InternalCacheBuilderAllowsMultipleSystemsTest.systemCache(givenSystem, InternalCacheBuilderAllowsMultipleSystemsTest.CacheState.OPEN);
        InternalCacheBuilder internalCacheBuilder = new InternalCacheBuilder(new Properties(), new CacheConfig(), InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_SYSTEM_CONSTRUCTOR, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_SUPPLIER, InternalCacheBuilderAllowsMultipleSystemsTest.THROWING_CACHE_CONSTRUCTOR);
        internalCacheBuilder.setIsExistingOk(true).create(givenSystem);
        Mockito.verify(givenSystem).setCache(ArgumentMatchers.same(systemCache));
    }

    enum CacheState {

        OPEN(false),
        CLOSED(true);
        private final boolean isClosed;

        CacheState(boolean isClosed) {
            this.isClosed = isClosed;
        }

        boolean isClosed() {
            return isClosed;
        }
    }
}

